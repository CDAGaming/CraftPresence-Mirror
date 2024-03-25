/*
 * MIT License
 *
 * Copyright (c) 2018 - 2024 CDAGaming (cstack2011@yahoo.com)
 *
 * Permission is hereby granted, free of charge, to any person obtaining a copy
 * of this software and associated documentation files (the "Software"), to deal
 * in the Software without restriction, including without limitation the rights
 * to use, copy, modify, merge, publish, distribute, sublicense, and/or sell
 * copies of the Software, and to permit persons to whom the Software is
 * furnished to do so, subject to the following conditions:
 *
 * The above copyright notice and this permission notice shall be included in all
 * copies or substantial portions of the Software.
 *
 * THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
 * IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
 * FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE
 * AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER
 * LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,
 * OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN THE
 * SOFTWARE.
 */

package com.gitlab.cdagaming.craftpresence.integrations.replaymod;

import com.gitlab.cdagaming.craftpresence.CraftPresence;
import com.gitlab.cdagaming.craftpresence.config.Config;
import com.gitlab.cdagaming.craftpresence.core.config.element.ModuleData;
import com.gitlab.cdagaming.craftpresence.core.impl.Module;
import io.github.cdagaming.unicore.utils.FileUtils;
import io.github.cdagaming.unicore.utils.MappingUtils;
import io.github.cdagaming.unicore.utils.StringUtils;
import io.github.classgraph.ClassInfo;
import net.minecraft.client.gui.GuiScreen;

import java.util.List;

/**
 * Extension of {@link com.gitlab.cdagaming.craftpresence.utils.gui.GuiUtils} designed for ReplayMod
 * <p>
 * Source: <a href="https://replaymod.com/">Click Here</a>
 *
 * @author CDAGaming
 */
@SuppressWarnings("DuplicatedCode")
public class ReplayModUtils implements Module {
    // CLASS REFLECTION STORAGE -- DO NOT TOUCH !!!
    private final Class<?> screenClass = FileUtils.findValidClass("com.replaymod.lib.de.johni0702.minecraft.gui.container.GuiScreen");
    private final Class<?> overlayClass = FileUtils.findValidClass("com.replaymod.lib.de.johni0702.minecraft.gui.container.GuiOverlay");
    private final Class<?> abstractContainerClass = FileUtils.findValidClass("com.replaymod.lib.de.johni0702.minecraft.gui.container.AbstractGuiContainer");
    private final Class<?> abstractScreenClass = FileUtils.findValidClass("com.replaymod.lib.de.johni0702.minecraft.gui.container.AbstractGuiScreen");
    private final Class<?> abstractOverlayClass = FileUtils.findValidClass("com.replaymod.lib.de.johni0702.minecraft.gui.container.AbstractGuiOverlay");
    private final Class<?> videoRendererScreen = FileUtils.findValidClass("com.replaymod.render.gui.GuiVideoRenderer");
    private final Class<?> videoRendererInfo = FileUtils.findValidClass("com.replaymod.render.rendering.VideoRenderer");
    /**
     * Whether this module is allowed to start and enabled
     */
    public boolean enabled = false;
    /**
     * Whether this module is active and currently in use
     */
    private boolean isInUse = false;
    /**
     * Whether this module has performed an initial retrieval of items
     */
    private boolean hasScanned = false;
    /**
     * The name of the Current Gui the player is in
     */
    private String CURRENT_GUI_NAME;
    /**
     * The Current Instance of the Gui the player is in
     */
    private Object CURRENT_SCREEN;

    @Override
    public void emptyData() {
        clearClientData();
    }

    @Override
    public void clearClientData() {
        CURRENT_GUI_NAME = null;
        CURRENT_SCREEN = null;

        setInUse(false);
        CraftPresence.CLIENT.removeArguments("replaymod");
    }

    @Override
    public void onTick() {
        enabled = !CraftPresence.CONFIG.hasChanged ? CraftPresence.CONFIG.advancedSettings.enablePerGui : enabled;
        final boolean needsUpdate = enabled && !hasScanned && canFetchData();

        if (needsUpdate) {
            scanForData();
            hasScanned = true;
        }

        if (isEnabled()) {
            if (CraftPresence.GUIS.CURRENT_SCREEN != null) {
                setInUse(true);
                updateData();
            } else if (isInUse()) {
                clearClientData();
            }
        } else if (isInUse()) {
            emptyData();
        }
    }

    @Override
    public void updateData() {
        if (CraftPresence.GUIS.CURRENT_SCREEN == null) {
            clearClientData();
        } else {
            final Object possibleScreen = StringUtils.executeMethod(screenClass, null, new Class[]{GuiScreen.class}, new Object[]{CraftPresence.GUIS.CURRENT_SCREEN}, "from");
            final Object possibleOverlay = StringUtils.executeMethod(overlayClass, null, new Class[]{GuiScreen.class}, new Object[]{CraftPresence.GUIS.CURRENT_SCREEN}, "from");
            if (possibleScreen == null && possibleOverlay == null) {
                clearClientData();
            } else {
                final Object newScreen = possibleOverlay != null ? possibleOverlay : possibleScreen;
                final String newScreenName = MappingUtils.getClassName(newScreen);

                if (!newScreen.equals(CURRENT_SCREEN) || !newScreenName.equals(CURRENT_GUI_NAME)) {
                    CURRENT_SCREEN = newScreen;
                    CURRENT_GUI_NAME = newScreenName;

                    if (!CraftPresence.GUIS.GUI_NAMES.contains(newScreenName)) {
                        CraftPresence.GUIS.GUI_NAMES.add(newScreenName);
                    }

                    updatePresence();
                }
                syncPlaceholders();
            }
        }
    }

    @Override
    public void getAllData() {
        final List<Class<?>> searchClasses = StringUtils.newArrayList(abstractContainerClass, abstractScreenClass, abstractOverlayClass);

        for (ClassInfo classObj : FileUtils.getClassNamesMatchingSuperType(searchClasses).values()) {
            final String screenName = MappingUtils.getClassName(classObj);
            if (!CraftPresence.GUIS.GUI_NAMES.contains(screenName)) {
                CraftPresence.GUIS.GUI_NAMES.add(screenName);
            }
            if (!CraftPresence.GUIS.GUI_CLASSES.containsKey(screenName)) {
                CraftPresence.GUIS.GUI_CLASSES.put(screenName, classObj);
            }
        }
    }

    @Override
    public boolean canFetchData() {
        return FileUtils.canScanClasses();
    }

    @Override
    public boolean isEnabled() {
        return enabled;
    }

    @Override
    public void setEnabled(boolean state) {
        this.enabled = state;
    }

    @Override
    public boolean isInUse() {
        return isInUse;
    }

    @Override
    public void setInUse(boolean state) {
        this.isInUse = state;
    }

    @Override
    public void updatePresence() {
        final ModuleData defaultData = CraftPresence.CONFIG.advancedSettings.guiSettings.guiData.get("default");
        final ModuleData currentData = CraftPresence.CONFIG.advancedSettings.guiSettings.guiData.get(CURRENT_GUI_NAME);

        final String defaultMessage = Config.isValidProperty(defaultData, "textOverride") ? defaultData.getTextOverride() : "";
        final String currentMessage = Config.isValidProperty(currentData, "textOverride") ? currentData.getTextOverride() : defaultMessage;
        final String defaultIcon = Config.isValidProperty(defaultData, "iconOverride") ? defaultData.getIconOverride() : CURRENT_GUI_NAME;
        final String currentIcon = Config.isValidProperty(currentData, "iconOverride") ? currentData.getIconOverride() : defaultIcon;
        final String formattedIcon = CraftPresence.CLIENT.imageOf("screen.icon", true, currentIcon, CraftPresence.CONFIG.advancedSettings.guiSettings.fallbackGuiIcon);

        CraftPresence.CLIENT.syncArgument("screen.default.icon", CraftPresence.CONFIG.advancedSettings.guiSettings.fallbackGuiIcon);

        CraftPresence.CLIENT.syncArgument("data.screen.instance", CURRENT_SCREEN);
        CraftPresence.CLIENT.syncArgument("screen.name", CURRENT_GUI_NAME);

        CraftPresence.CLIENT.syncOverride(currentData != null ? currentData : defaultData, "screen.message", "screen.icon");
        CraftPresence.CLIENT.syncArgument("screen.message", currentMessage);
        CraftPresence.CLIENT.syncArgument("screen.icon", formattedIcon);
    }

    private void syncPlaceholders() {
        // Additional Data for Replay Mod
        if (CURRENT_SCREEN != null && CURRENT_SCREEN.getClass() == videoRendererScreen) {
            CraftPresence.CLIENT.syncArgument("replaymod.time.current", secToString(
                    StringUtils.getValidInteger(StringUtils.getField(
                            videoRendererScreen, CURRENT_SCREEN, "renderTimeTaken"
                    )).getSecond() / 1000
            ));
            CraftPresence.CLIENT.syncArgument("replaymod.time.remaining", secToString(
                    StringUtils.getValidInteger(StringUtils.getField(
                            videoRendererScreen, CURRENT_SCREEN, "renderTimeLeft"
                    )).getSecond() / 1000
            ));

            final Object rendererObj = StringUtils.getField(
                    videoRendererScreen, CURRENT_SCREEN, "renderer"
            );
            if (rendererObj != null && rendererObj.getClass() == videoRendererInfo) {
                CraftPresence.CLIENT.syncArgument("replaymod.frames.current",
                        StringUtils.executeMethod(videoRendererInfo, rendererObj, null, null, "getFramesDone"));
                CraftPresence.CLIENT.syncArgument("replaymod.frames.total",
                        StringUtils.executeMethod(videoRendererInfo, rendererObj, null, null, "getTotalFrames"));
            }
        } else {
            CraftPresence.CLIENT.removeArguments("replaymod");
        }
    }

    private String secToString(int seconds) {
        int hours = seconds / 3600;
        int min = seconds / 60 - hours * 60;
        int sec = seconds - (min * 60 + hours * 60 * 60);
        StringBuilder builder = new StringBuilder();
        if (hours > 0) {
            builder.append(hours).append("hour(s)");
        }
        if (min > 0 || hours > 0) {
            builder.append(min).append("minute(s)");
        }
        builder.append(sec).append("second(s)");
        return builder.toString();
    }
}
