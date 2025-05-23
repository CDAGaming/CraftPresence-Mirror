/*
 * MIT License
 *
 * Copyright (c) 2018 - 2025 CDAGaming (cstack2011@yahoo.com)
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
import com.gitlab.cdagaming.craftpresence.core.config.Config;
import com.gitlab.cdagaming.craftpresence.core.config.element.ModuleData;
import com.gitlab.cdagaming.craftpresence.core.impl.ExtendedModule;
import io.github.cdagaming.unicore.utils.FileUtils;
import io.github.cdagaming.unicore.utils.MappingUtils;
import io.github.cdagaming.unicore.utils.StringUtils;
import net.minecraft.client.gui.GuiScreen;
import unilib.external.io.github.classgraph.ClassInfo;

import java.util.List;
import java.util.function.Supplier;

/**
 * Extension of {@link com.gitlab.cdagaming.craftpresence.utils.gui.GuiUtils} designed for ReplayMod
 * <p>
 * Source: <a href="https://replaymod.com/">Click Here</a>
 *
 * @author CDAGaming
 */
@SuppressWarnings("DuplicatedCode")
public class ReplayModUtils implements ExtendedModule {
    /**
     * Whether this module is allowed to start and enabled
     */
    private boolean enabled = false;
    /**
     * Whether this module is active and currently in use
     */
    private boolean isInUse = false;
    /**
     * Whether this module has performed an initial retrieval of internal items
     */
    private boolean hasScannedInternals = false;
    /**
     * Whether this module has performed an initial event sync
     */
    private boolean hasInitialized = false;
    /**
     * Whether placeholders for the main screen have been initialized
     */
    private boolean hasInitializedMain = false;
    /**
     * Whether placeholders for the sub screen have been initialized
     */
    private boolean hasInitializedSub = false;
    /**
     * The current "inUse" state of the inherited module
     */
    private boolean otherModuleState = false;
    /**
     * The name of the Current Gui the player is in
     */
    private String CURRENT_GUI_NAME;
    /**
     * The Current Instance of the Gui the player is in
     */
    private Object CURRENT_SCREEN;

    @Override
    public void clearAttributes() {
        CURRENT_GUI_NAME = null;
        CURRENT_SCREEN = null;

        clearMainPlaceholders();
        clearSubPlaceholders();
        hasInitialized = false;
        hasInitializedMain = false;
        hasInitializedSub = false;
        otherModuleState = false;
    }

    @Override
    public void updateData() {
        if (CraftPresence.GUIS.CURRENT_SCREEN == null) {
            final Class<?> replayClass = FileUtils.loadClass("com.replaymod.replay.ReplayModReplay");
            final Class<?> replayHandlerClass = FileUtils.loadClass("com.replaymod.replay.ReplayHandler");

            final Object replayInstance = StringUtils.getField(replayClass, null, "instance");
            final Object replayHandler = replayInstance != null ? StringUtils.executeMethod(replayClass, replayInstance, null, null, "getReplayHandler") : null;
            final Object replayOverlay = replayHandler != null ? StringUtils.executeMethod(replayHandlerClass, replayHandler, null, null, "getOverlay") : null;
            if (replayOverlay == null) {
                clearClientData();
            } else {
                processScreen(replayOverlay);
            }
        } else {
            final Class<?> screenClass = FileUtils.loadClass("com.replaymod.lib.de.johni0702.minecraft.gui.container.GuiScreen");
            final Class<?> overlayClass = FileUtils.loadClass("com.replaymod.lib.de.johni0702.minecraft.gui.container.GuiOverlay");

            final Object possibleScreen = StringUtils.executeMethod(screenClass, null, new Class[]{GuiScreen.class}, new Object[]{CraftPresence.GUIS.CURRENT_SCREEN}, "from");
            final Object possibleOverlay = StringUtils.executeMethod(overlayClass, null, new Class[]{GuiScreen.class}, new Object[]{CraftPresence.GUIS.CURRENT_SCREEN}, "from");
            if (possibleScreen == null && possibleOverlay == null) {
                clearClientData();
            } else {
                processScreen(possibleOverlay != null ? possibleOverlay : possibleScreen);
            }
        }
    }

    private void processScreen(final Object newScreen) {
        final String newScreenName = MappingUtils.getClassName(newScreen);

        final boolean newModuleState = CraftPresence.GUIS.isInUse();
        final boolean moduleStateChanged = newModuleState != otherModuleState;

        if (!newScreen.equals(CURRENT_SCREEN) || !newScreenName.equals(CURRENT_GUI_NAME) || moduleStateChanged) {
            CURRENT_SCREEN = newScreen;
            CURRENT_GUI_NAME = newScreenName;
            otherModuleState = newModuleState;

            if (!CraftPresence.GUIS.GUI_NAMES.contains(newScreenName)) {
                CraftPresence.GUIS.GUI_NAMES.add(newScreenName);
            }

            if (!hasInitialized || moduleStateChanged) {
                initPresence();
                hasInitialized = true;
            }
            updatePresence();
        }
        syncPlaceholders();
    }

    @Override
    public void getInternalData() {
        final List<Class<?>> searchClasses = StringUtils.newArrayList(
                FileUtils.findClass("com.replaymod.lib.de.johni0702.minecraft.gui.container.AbstractGuiContainer"),
                FileUtils.findClass("com.replaymod.lib.de.johni0702.minecraft.gui.container.AbstractGuiScreen"),
                FileUtils.findClass("com.replaymod.lib.de.johni0702.minecraft.gui.container.AbstractGuiOverlay")
        );

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
    public void getConfigData() {
        // N/A
    }

    @Override
    public void syncArgument(String argumentName, Supplier<Boolean> condition, Supplier<Object> event, boolean plain) {
        CraftPresence.CLIENT.syncArgument(argumentName, getModuleFunction(condition, event), plain);
    }

    @Override
    public ModuleData getData(final String key) {
        return CraftPresence.GUIS.getData(key);
    }

    @Override
    public String getOverrideText(ModuleData data) {
        return CraftPresence.GUIS.getOverrideText(data);
    }

    @Override
    public boolean canFetchInternals() {
        return CraftPresence.GUIS.canFetchInternals();
    }

    @Override
    public boolean hasScannedInternals() {
        return hasScannedInternals;
    }

    @Override
    public void setScannedInternals(final boolean state) {
        hasScannedInternals = state;
    }

    @Override
    public boolean canBeEnabled() {
        return CraftPresence.GUIS.canBeEnabled();
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
    public boolean canBeUsed() {
        return CraftPresence.GUIS.canBeUsed() || CraftPresence.player != null;
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
    public void initPresence() {
        syncArgument("screen.default.icon", () -> CraftPresence.CONFIG.advancedSettings.guiSettings.fallbackGuiIcon);

        syncArgument("data.screen.instance", () -> CURRENT_SCREEN, true);
        syncArgument("screen.name", () -> CURRENT_GUI_NAME, true);

        syncArgument("screen.message", () -> {
            final ModuleData defaultData = getDefaultData();
            final ModuleData currentData = getData(CURRENT_GUI_NAME);

            final String defaultMessage = Config.isValidProperty(defaultData, "textOverride") ? defaultData.getTextOverride() : "";
            return getResult(Config.isValidProperty(currentData, "textOverride") ? currentData.getTextOverride() : defaultMessage, CURRENT_GUI_NAME);
        });
        syncArgument("screen.icon", () -> {
            final ModuleData defaultData = getDefaultData();
            final ModuleData currentData = getData(CURRENT_GUI_NAME);

            final String defaultIcon = Config.isValidProperty(defaultData, "iconOverride") ? defaultData.getIconOverride() : CURRENT_GUI_NAME;
            final String currentIcon = Config.isValidProperty(currentData, "iconOverride") ? currentData.getIconOverride() : defaultIcon;
            return getResult(CraftPresence.CLIENT.imageOf(true, currentIcon, CraftPresence.CONFIG.advancedSettings.guiSettings.fallbackGuiIcon), CURRENT_GUI_NAME);
        });
        CraftPresence.CLIENT.addForcedData("screen", () -> isInUse() ? getPresenceData(CURRENT_GUI_NAME) : null);
        CraftPresence.CLIENT.syncTimestamp("data.screen.time");
    }

    @Override
    public void updatePresence() {
        // N/A
    }

    private void clearMainPlaceholders() {
        CraftPresence.CLIENT.removeArguments("replaymod.time", "data.replaymod.time");
    }

    private void clearSubPlaceholders() {
        CraftPresence.CLIENT.removeArguments("replaymod.frames");
    }

    private void syncPlaceholders() {
        final Class<?> videoRendererScreen = FileUtils.loadClass("com.replaymod.render.gui.GuiVideoRenderer");

        // Additional Data for Replay Mod
        if (CURRENT_SCREEN != null && CURRENT_SCREEN.getClass() == videoRendererScreen) {
            if (!hasInitializedMain) {
                syncArgument("data.replaymod.time.current", () ->
                        StringUtils.getValidInteger(StringUtils.getField(
                                videoRendererScreen, CURRENT_SCREEN, "renderTimeTaken"
                        )).getSecond() / 1000, true);
                syncArgument("replaymod.time.current", () -> secToString(
                        StringUtils.getValidInteger(StringUtils.getField(
                                videoRendererScreen, CURRENT_SCREEN, "renderTimeTaken"
                        )).getSecond() / 1000
                ), true);

                syncArgument("data.replaymod.time.remaining", () ->
                        StringUtils.getValidInteger(StringUtils.getField(
                                videoRendererScreen, CURRENT_SCREEN, "renderTimeLeft"
                        )).getSecond(), true);
                syncArgument("replaymod.time.remaining", () -> secToString(
                        StringUtils.getValidInteger(StringUtils.getField(
                                videoRendererScreen, CURRENT_SCREEN, "renderTimeLeft"
                        )).getSecond()
                ), true);
                hasInitializedMain = true;
            }

            final Object rendererObj = StringUtils.getField(
                    videoRendererScreen, CURRENT_SCREEN, "renderer"
            );
            final Class<?> videoRendererInfo = FileUtils.loadClass("com.replaymod.render.rendering.VideoRenderer");
            if (rendererObj != null && rendererObj.getClass() == videoRendererInfo) {
                if (!hasInitializedSub) {
                    syncArgument("replaymod.frames.current",
                            () -> StringUtils.executeMethod(videoRendererInfo, rendererObj, null, null, "getFramesDone"), true);
                    syncArgument("replaymod.frames.total",
                            () -> StringUtils.executeMethod(videoRendererInfo, rendererObj, null, null, "getTotalFrames"), true);
                    hasInitializedSub = true;
                }
            } else if (hasInitializedSub) {
                clearSubPlaceholders();
                hasInitializedSub = false;
            }
        } else if (hasInitializedMain) {
            clearMainPlaceholders();
            hasInitializedMain = false;
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
