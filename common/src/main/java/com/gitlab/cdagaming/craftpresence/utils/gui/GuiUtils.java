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

package com.gitlab.cdagaming.craftpresence.utils.gui;

import com.gitlab.cdagaming.craftpresence.CraftPresence;
import com.gitlab.cdagaming.craftpresence.core.config.Config;
import com.gitlab.cdagaming.craftpresence.core.config.element.ModuleData;
import com.gitlab.cdagaming.craftpresence.core.impl.ExtendedModule;
import com.gitlab.cdagaming.unilib.utils.GameUtils;
import io.github.cdagaming.unicore.utils.FileUtils;
import io.github.cdagaming.unicore.utils.MappingUtils;
import io.github.cdagaming.unicore.utils.StringUtils;
import net.minecraft.client.gui.GuiScreen;
import net.minecraft.client.gui.inventory.GuiContainer;
import unilib.external.io.github.classgraph.ClassInfo;

import java.util.List;
import java.util.Map;
import java.util.function.Supplier;

/**
 * Gui Utilities used to Parse Gui Data and handle related RPC Events
 *
 * @author CDAGaming
 */
@SuppressWarnings("DuplicatedCode")
public class GuiUtils implements ExtendedModule {
    /**
     * A List of the detected Gui Screen Classes
     */
    public final Map<String, ClassInfo> GUI_CLASSES = StringUtils.newHashMap();
    /**
     * A List of the detected Gui Screen Names
     */
    public List<String> GUI_NAMES = StringUtils.newArrayList();
    /**
     * A List of the default detected Gui Screen Names
     */
    public List<String> DEFAULT_NAMES = StringUtils.newArrayList();
    /**
     * The Current Instance of the Gui the player is in
     */
    public GuiScreen CURRENT_SCREEN;
    /**
     * Whether this module is allowed to start and enabled
     */
    private boolean enabled = false;
    /**
     * Whether this module is active and currently in use
     */
    private boolean isInUse = false;
    /**
     * Whether this module has performed an initial retrieval of config items
     */
    private boolean hasScannedConfig = false;
    /**
     * Whether this module has performed an initial retrieval of internal items
     */
    private boolean hasScannedInternals = false;
    /**
     * Whether this module has performed an initial event sync
     */
    private boolean hasInitialized = false;
    /**
     * The name of the Current Gui the player is in
     */
    private String CURRENT_GUI_NAME;

    @Override
    public void clearFieldData() {
        DEFAULT_NAMES.clear();
        GUI_NAMES.clear();
        GUI_CLASSES.clear();
    }

    @Override
    public void clearAttributes() {
        CURRENT_GUI_NAME = null;
        CURRENT_SCREEN = null;

        CraftPresence.CLIENT.removeArguments("screen", "data.screen");
        CraftPresence.CLIENT.clearForcedData("screen");
        hasInitialized = false;
    }

    @Override
    public void updateData() {
        final GuiScreen newScreen = GameUtils.getCurrentScreen(CraftPresence.instance);

        if (newScreen == null) {
            clearClientData();
        } else {
            final String newScreenName = StringUtils.getOrDefault(
                    MappingUtils.getClassName(newScreen),
                    MappingUtils.getClassName(GuiScreen.class)
            );

            if (!newScreen.equals(CURRENT_SCREEN) || !newScreenName.equals(CURRENT_GUI_NAME)) {
                CURRENT_SCREEN = newScreen;
                CURRENT_GUI_NAME = newScreenName;

                if (!DEFAULT_NAMES.contains(newScreenName)) {
                    DEFAULT_NAMES.add(newScreenName);
                }
                if (!GUI_NAMES.contains(newScreenName)) {
                    GUI_NAMES.add(newScreenName);
                }

                if (!hasInitialized) {
                    initPresence();
                    hasInitialized = true;
                }
                updatePresence();
            }
        }
    }

    @Override
    public void getInternalData() {
        final List<Class<?>> searchClasses = StringUtils.newArrayList(GuiScreen.class, GuiContainer.class);

        for (ClassInfo classObj : FileUtils.getClassNamesMatchingSuperType(searchClasses).values()) {
            final String screenName = MappingUtils.getClassName(classObj);
            if (!DEFAULT_NAMES.contains(screenName)) {
                DEFAULT_NAMES.add(screenName);
            }
            if (!GUI_NAMES.contains(screenName)) {
                GUI_NAMES.add(screenName);
            }
            if (!GUI_CLASSES.containsKey(screenName)) {
                GUI_CLASSES.put(screenName, classObj);
            }
        }
    }

    @Override
    public void getConfigData() {
        for (String guiEntry : CraftPresence.CONFIG.advancedSettings.guiSettings.guiData.keySet()) {
            if (!StringUtils.isNullOrEmpty(guiEntry) && !GUI_NAMES.contains(guiEntry)) {
                GUI_NAMES.add(guiEntry);
            }
        }
    }

    @Override
    public void syncArgument(String argumentName, Supplier<Boolean> condition, Supplier<Object> event, boolean plain) {
        CraftPresence.CLIENT.syncArgument(argumentName, getModuleFunction(condition, event), plain);
    }

    @Override
    public ModuleData getData(String key) {
        return CraftPresence.CONFIG.advancedSettings.guiSettings.guiData.get(key);
    }

    @Override
    public String getOverrideText(ModuleData data) {
        return CraftPresence.CLIENT.getOverrideText(getPresenceData(data));
    }

    @Override
    public boolean canFetchInternals() {
        return MappingUtils.areMappingsLoaded() && FileUtils.isClassGraphEnabled() && FileUtils.canScanClasses();
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
    public boolean canFetchConfig() {
        return CraftPresence.CONFIG != null;
    }

    @Override
    public boolean hasScannedConfig() {
        return hasScannedConfig;
    }

    @Override
    public void setScannedConfig(final boolean state) {
        hasScannedConfig = state;
    }

    @Override
    public boolean canBeEnabled() {
        return !CraftPresence.CONFIG.hasChanged() ? CraftPresence.CONFIG.advancedSettings.enablePerGui : isEnabled();
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
        return GameUtils.getCurrentScreen(CraftPresence.instance) != null;
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
            return Config.isValidProperty(currentData, "textOverride") ? currentData.getTextOverride() : defaultMessage;
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
}
