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

package com.gitlab.cdagaming.craftpresence.utils.gui;

import com.gitlab.cdagaming.craftpresence.CraftPresence;
import com.gitlab.cdagaming.craftpresence.config.Config;
import com.gitlab.cdagaming.craftpresence.core.config.element.ModuleData;
import com.gitlab.cdagaming.craftpresence.core.impl.Module;
import com.gitlab.cdagaming.craftpresence.core.utils.FileUtils;
import com.gitlab.cdagaming.craftpresence.core.utils.MappingUtils;
import com.gitlab.cdagaming.craftpresence.core.utils.StringUtils;
import io.github.classgraph.ClassInfo;
import net.minecraft.client.gui.FontRenderer;
import net.minecraft.client.gui.GuiScreen;
import net.minecraft.client.gui.inventory.GuiContainer;

import java.util.List;
import java.util.Map;

/**
 * Gui Utilities used to Parse Gui Data and handle related RPC Events
 *
 * @author CDAGaming
 */
@SuppressWarnings("DuplicatedCode")
public class GuiUtils implements Module {
    /**
     * A List of the detected Gui Screen Classes
     */
    public final Map<String, ClassInfo> GUI_CLASSES = StringUtils.newHashMap();
    /**
     * If an Element is being focused on in a GUI or if a GUI is currently open
     * <p>Conditions depend on Game Version
     */
    public boolean isFocused = false;
    /**
     * Whether this module is allowed to start and enabled
     */
    public boolean enabled = false;
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
     * Gets the Default/Global Font Renderer
     *
     * @return The Default/Global Font Renderer
     */
    public static FontRenderer getDefaultFontRenderer() {
        return CraftPresence.instance.fontRenderer;
    }

    @Override
    public void emptyData() {
        hasScanned = false;
        DEFAULT_NAMES.clear();
        GUI_NAMES.clear();
        GUI_CLASSES.clear();
        clearClientData();
    }

    @Override
    public void clearClientData() {
        CURRENT_GUI_NAME = null;
        CURRENT_SCREEN = null;

        setInUse(false);
        CraftPresence.CLIENT.removeArguments("screen", "data.screen");
        CraftPresence.CLIENT.clearOverride("screen.message", "screen.icon");
    }

    @Override
    public void onTick() {
        enabled = !CraftPresence.CONFIG.hasChanged ? CraftPresence.CONFIG.advancedSettings.enablePerGui : enabled;
        isFocused = CraftPresence.instance.currentScreen != null && (CraftPresence.instance.currentScreen.isFocused() || CraftPresence.player != null);
        final boolean needsUpdate = enabled && !hasScanned && canFetchData();

        if (needsUpdate) {
            scanForData();
            hasScanned = true;
        }

        if (enabled) {
            if (CraftPresence.instance.currentScreen != null) {
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
        if (CraftPresence.instance.currentScreen == null) {
            clearClientData();
        } else {
            final GuiScreen newScreen = CraftPresence.instance.currentScreen;
            final String newScreenName = MappingUtils.getClassName(newScreen);

            if (!newScreen.equals(CURRENT_SCREEN) || !newScreenName.equals(CURRENT_GUI_NAME)) {
                CURRENT_SCREEN = newScreen;
                CURRENT_GUI_NAME = newScreenName;

                if (!DEFAULT_NAMES.contains(newScreenName)) {
                    DEFAULT_NAMES.add(newScreenName);
                }
                if (!GUI_NAMES.contains(newScreenName)) {
                    GUI_NAMES.add(newScreenName);
                }

                updatePresence();
            }
        }
    }

    @Override
    public void getAllData() {
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

        for (String guiEntry : CraftPresence.CONFIG.advancedSettings.guiSettings.guiData.keySet()) {
            if (!StringUtils.isNullOrEmpty(guiEntry) && !GUI_NAMES.contains(guiEntry)) {
                GUI_NAMES.add(guiEntry);
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
        CraftPresence.CLIENT.syncTimestamp("data.screen.time");
    }
}
