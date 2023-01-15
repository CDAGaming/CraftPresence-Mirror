/*
 * MIT License
 *
 * Copyright (c) 2018 - 2023 CDAGaming (cstack2011@yahoo.com)
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

package com.gitlab.cdagaming.craftpresence.forge.integrations.replaymod;

import com.gitlab.cdagaming.craftpresence.CraftPresence;
import com.gitlab.cdagaming.craftpresence.config.Config;
import com.gitlab.cdagaming.craftpresence.config.element.ModuleData;
import com.gitlab.cdagaming.craftpresence.impl.Module;
import com.gitlab.cdagaming.craftpresence.utils.FileUtils;
import com.gitlab.cdagaming.craftpresence.utils.MappingUtils;
import com.gitlab.cdagaming.craftpresence.utils.StringUtils;
import com.google.common.collect.Lists;
import com.replaymod.lib.de.johni0702.minecraft.gui.container.*;
import com.replaymod.render.gui.GuiVideoRenderer;
import com.replaymod.render.rendering.VideoRenderer;

import java.util.List;

@SuppressWarnings("ConstantConditions")
public class ReplayModUtils implements Module {
    /**
     * Whether this module is active and currently in use
     */
    public boolean isInUse = false;
    /**
     * Whether this module is allowed to start and enabled
     */
    public boolean enabled = false;
    /**
     * Whether this module has performed an initial retrieval of items
     */
    public boolean hasScanned = false;
    /**
     * The name of the Current Gui the player is in
     */
    public String CURRENT_GUI_NAME;
    /**
     * The Class Type of the Current Gui the player is in
     */
    public Class<?> CURRENT_GUI_CLASS;
    /**
     * The Current Instance of the Gui the player is in
     */
    public AbstractGuiContainer<?> CURRENT_SCREEN;

    @Override
    public void emptyData() {
        clearClientData();
    }

    @Override
    public void clearClientData() {
        CURRENT_GUI_NAME = null;
        CURRENT_SCREEN = null;
        CURRENT_GUI_CLASS = null;

        setInUse(false);
        CraftPresence.CLIENT.removeArguments("replaymod");
    }

    @Override
    public void onTick() {
        enabled = !CraftPresence.CONFIG.hasChanged ? CraftPresence.CONFIG.advancedSettings.enablePerGui : enabled;
        final boolean needsUpdate = enabled && !hasScanned;

        if (needsUpdate) {
            new Thread(this::getAllData, "CraftPresence-ReplayMod-Screen-Lookup").start();
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
            final AbstractGuiScreen<?> possibleScreen = GuiScreen.from(CraftPresence.GUIS.CURRENT_SCREEN);
            final AbstractGuiOverlay<?> possibleOverlay = GuiOverlay.from(CraftPresence.GUIS.CURRENT_SCREEN);
            if (possibleScreen == null && possibleOverlay == null) {
                clearClientData();
            } else {
                final AbstractGuiContainer<?> newScreen = possibleOverlay != null ? possibleOverlay : possibleScreen;
                final Class<?> newScreenClass = newScreen.getClass();
                final String newScreenName = MappingUtils.getClassName(newScreen);

                if (!newScreen.equals(CURRENT_SCREEN) || !newScreenClass.equals(CURRENT_GUI_CLASS) || !newScreenName.equals(CURRENT_GUI_NAME)) {
                    CURRENT_SCREEN = newScreen;
                    CURRENT_GUI_CLASS = newScreenClass;
                    CURRENT_GUI_NAME = newScreenName;

                    if (!CraftPresence.GUIS.GUI_NAMES.contains(newScreenName)) {
                        CraftPresence.GUIS.GUI_NAMES.add(newScreenName);
                    }
                    if (!CraftPresence.GUIS.GUI_CLASSES.containsKey(newScreenName)) {
                        CraftPresence.GUIS.GUI_CLASSES.put(newScreenName, newScreenClass);
                    }

                    updatePresence();
                }
                syncPlaceholders();
            }
        }
    }

    @Override
    public void getAllData() {
        final List<Class<?>> searchClasses = Lists.newArrayList(AbstractGuiContainer.class, AbstractGuiScreen.class, AbstractGuiOverlay.class);

        for (Class<?> classObj : FileUtils.getClassNamesMatchingSuperType(searchClasses, CraftPresence.CONFIG.advancedSettings.includeExtraGuiClasses)) {
            String screenName = MappingUtils.getClassName(classObj);
            if (!CraftPresence.GUIS.GUI_NAMES.contains(screenName)) {
                CraftPresence.GUIS.GUI_NAMES.add(screenName);
            }
            if (!CraftPresence.GUIS.GUI_CLASSES.containsKey(screenName)) {
                CraftPresence.GUIS.GUI_CLASSES.put(screenName, classObj);
            }
        }
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
    public boolean canBeLoaded() {
        return true;
    }

    @Override
    public void updatePresence() {
        final ModuleData defaultData = CraftPresence.CONFIG.advancedSettings.guiSettings.guiData.get("default");
        final ModuleData currentData = CraftPresence.CONFIG.advancedSettings.guiSettings.guiData.get(CURRENT_GUI_NAME);

        final String defaultMessage = Config.isValidProperty(defaultData, "textOverride") ? defaultData.getTextOverride() : "";
        final String currentMessage = Config.isValidProperty(currentData, "textOverride") ? currentData.getTextOverride() : defaultMessage;
        final String defaultIcon = Config.isValidProperty(defaultData, "iconOverride") ? defaultData.getIconOverride() : CURRENT_GUI_NAME;
        final String currentIcon = Config.isValidProperty(currentData, "iconOverride") ? currentData.getIconOverride() : defaultIcon;
        final String formattedIcon = StringUtils.formatAsIcon(currentIcon.replace(" ", "_"));

        CraftPresence.CLIENT.syncArgument("screen.default.icon", CraftPresence.CONFIG.advancedSettings.guiSettings.fallbackGuiIcon);

        CraftPresence.CLIENT.syncArgument("data.screen.instance", CURRENT_SCREEN);
        CraftPresence.CLIENT.syncArgument("screen.name", CURRENT_GUI_NAME);
        CraftPresence.CLIENT.syncArgument("data.screen.class", CURRENT_GUI_CLASS);

        CraftPresence.CLIENT.syncOverride(currentData != null ? currentData : defaultData, "screen.message", "screen.icon");
        CraftPresence.CLIENT.syncArgument("screen.message", currentMessage);
        CraftPresence.CLIENT.syncArgument("screen.icon", CraftPresence.CLIENT.imageOf("screen.icon", true, formattedIcon, CraftPresence.CONFIG.advancedSettings.guiSettings.fallbackGuiIcon));
    }

    private void syncPlaceholders() {
        // Additional Data for Replay Mod
        if (CURRENT_SCREEN instanceof GuiVideoRenderer) {
            CraftPresence.CLIENT.syncArgument("replaymod.time.current", secToString(
                    (Integer) StringUtils.getField(
                            GuiVideoRenderer.class, CURRENT_SCREEN, "renderTimeTaken"
                    ) / 1000
            ));
            CraftPresence.CLIENT.syncArgument("replaymod.time.remaining", secToString(
                    (Integer) StringUtils.getField(
                            GuiVideoRenderer.class, CURRENT_SCREEN, "renderTimeLeft"
                    ) / 1000
            ));

            final VideoRenderer renderer = (VideoRenderer) StringUtils.getField(
                    GuiVideoRenderer.class, CURRENT_SCREEN, "renderer"
            );
            CraftPresence.CLIENT.syncArgument("replaymod.frames.current", renderer.getFramesDone());
            CraftPresence.CLIENT.syncArgument("replaymod.frames.total", renderer.getTotalFrames());
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
