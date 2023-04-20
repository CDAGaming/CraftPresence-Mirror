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
import com.gitlab.cdagaming.craftpresence.config.element.ModuleData;
import com.gitlab.cdagaming.craftpresence.impl.Module;
import com.gitlab.cdagaming.craftpresence.utils.FileUtils;
import com.gitlab.cdagaming.craftpresence.utils.MappingUtils;
import com.gitlab.cdagaming.craftpresence.utils.MathUtils;
import com.gitlab.cdagaming.craftpresence.utils.StringUtils;
import com.gitlab.cdagaming.craftpresence.utils.gui.controls.ExtendedButtonControl;
import com.gitlab.cdagaming.craftpresence.utils.gui.controls.ExtendedTextControl;
import com.gitlab.cdagaming.craftpresence.utils.gui.integrations.ExtendedScreen;
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
    public final Map<String, Class<?>> GUI_CLASSES = StringUtils.newHashMap();
    /**
     * If the Config GUI is currently open
     */
    public boolean configGUIOpened = false;
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
     * The Last Used Control ID
     */
    private int lastIndex = 0;
    /**
     * The name of the Current Gui the player is in
     */
    private String CURRENT_GUI_NAME;
    /**
     * The Class Type of the Current Gui the player is in
     */
    private Class<?> CURRENT_GUI_CLASS;

    /**
     * Gets the Default/Global Font Renderer
     *
     * @return The Default/Global Font Renderer
     */
    public static FontRenderer getDefaultFontRenderer() {
        return CraftPresence.instance.fontRenderer;
    }

    /**
     * Retrieves the Next Available Button ID for use in the currently open Screen
     *
     * @return The next available Button ID
     */
    public int getNextIndex() {
        return lastIndex++;
    }

    /**
     * Resets the Button Index to 0
     * Normally used when closing a screen and no longer using the allocated ID's
     */
    public void resetIndex() {
        lastIndex = 0;
    }

    /**
     * Determines if the Mouse is over an element, following the defined Arguments
     *
     * @param mouseX   The Mouse's Current X Position
     * @param mouseY   The Mouse's Current Y Position
     * @param topIn    The top-most boundary of the zone
     * @param bottomIn The bottom-most boundary of the zone
     * @param leftIn   The left-most boundary of the zone
     * @param rightIn  The right-most boundary of the zone
     * @return {@link Boolean#TRUE} if the Mouse Position is within the bounds of the object, and thus is over it
     */
    public boolean isMouseWithin(final double mouseX, final double mouseY, final double topIn, final double bottomIn, final double leftIn, final double rightIn) {
        return MathUtils.isWithinValue(mouseY, topIn, bottomIn, true, true) &&
                MathUtils.isWithinValue(mouseX, leftIn, rightIn, true, true);
    }

    /**
     * Determines if the Mouse is over an element, following the defined Arguments
     *
     * @param mouseX        The Mouse's Current X Position
     * @param mouseY        The Mouse's Current Y Position
     * @param elementX      The Object's starting X Position
     * @param elementY      The Object's starting Y Position
     * @param elementWidth  The total width of the object
     * @param elementHeight The total height of the object
     * @return {@link Boolean#TRUE} if the Mouse Position is within the bounds of the object, and thus is over it
     */
    public boolean isMouseOver(final double mouseX, final double mouseY, final double elementX, final double elementY, final double elementWidth, final double elementHeight) {
        return MathUtils.isWithinValue(mouseX, elementX, elementX + elementWidth, true, false) &&
                MathUtils.isWithinValue(mouseY, elementY, elementY + elementHeight, true, false);
    }

    /**
     * Determines if the Mouse is over an element, following the defined Arguments
     *
     * @param mouseX The Mouse's Current X Position
     * @param mouseY The Mouse's Current Y Position
     * @param button The Object to check bounds and position
     * @return {@link Boolean#TRUE} if the Mouse Position is within the bounds of the object, and thus is over it
     */
    public boolean isMouseOver(final double mouseX, final double mouseY, final ExtendedButtonControl button) {
        return button.isControlVisible() && isMouseOver(mouseX, mouseY, button.getControlPosX(), button.getControlPosY(), button.getControlWidth() - 1, button.getControlHeight() - 1);
    }

    /**
     * Determines if the Mouse is over an element, following the defined Arguments
     *
     * @param mouseY The Mouse's Current Y Position
     * @param button The Object to check bounds and position
     * @return {@link Boolean#TRUE} if the Mouse Position is within the bounds of the object, and thus is over it
     */
    public boolean isMouseOver(final double mouseY, final ExtendedButtonControl button) {
        return isMouseOver(0, mouseY, 0, button.getControlPosY(), 0, button.getControlHeight() - 1);
    }

    /**
     * Determines if the Mouse is over an element, following the defined Arguments
     *
     * @param mouseX      The Mouse's Current X Position
     * @param mouseY      The Mouse's Current Y Position
     * @param textControl The Object to check bounds and position
     * @return {@link Boolean#TRUE} if the Mouse Position is within the bounds of the object, and thus is over it
     */
    public boolean isMouseOver(final double mouseX, final double mouseY, final ExtendedTextControl textControl) {
        return isMouseOver(mouseX, mouseY, textControl.getControlPosX(), textControl.getControlPosY(), textControl.getControlWidth() - 1, textControl.getControlHeight() - 1);
    }

    /**
     * Determines if the Mouse is over an element, following the defined Arguments
     *
     * @param mouseY      The Mouse's Current Y Position
     * @param textControl The Object to check bounds and position
     * @return {@link Boolean#TRUE} if the Mouse Position is within the bounds of the object, and thus is over it
     */
    public boolean isMouseOver(final double mouseY, final ExtendedTextControl textControl) {
        return isMouseOver(0, mouseY, 0, textControl.getControlPosY(), 0, textControl.getControlHeight() - 1);
    }

    /**
     * Determines if the Mouse is over an element, following the defined Arguments
     *
     * @param mouseX The Mouse's Current X Position
     * @param mouseY The Mouse's Current Y Position
     * @param screen The Object to check bounds and position
     * @return {@link Boolean#TRUE} if the Mouse Position is within the bounds of the object, and thus is over it
     */
    public boolean isMouseOver(final double mouseX, final double mouseY, final ExtendedScreen screen) {
        return screen.isLoaded() && isMouseOver(mouseX, mouseY, screen.getScreenX(), screen.getScreenY(), screen.getScreenWidth(), screen.getScreenHeight());
    }

    /**
     * Determines if the Mouse is over an element, following the defined Arguments
     *
     * @param mouseY The Mouse's Current Y Position
     * @param screen The Object to check bounds and position
     * @return {@link Boolean#TRUE} if the Mouse Position is within the bounds of the object, and thus is over it
     */
    public boolean isMouseOver(final double mouseY, final ExtendedScreen screen) {
        return screen.isLoaded() && isMouseOver(0, mouseY, 0, screen.getScreenY(), 0, screen.getScreenHeight());
    }

    /**
     * Determines if the Mouse is over an element, following the defined Arguments
     *
     * @param screen The Object to check bounds and position
     * @return {@link Boolean#TRUE} if the Mouse Position is within the bounds of the object, and thus is over it
     */
    public boolean isMouseOver(final ExtendedScreen screen) {
        return isMouseOver(screen.getMouseX(), screen.getMouseY(), screen);
    }

    @Override
    public void emptyData() {
        hasScanned = false;
        GUI_NAMES.clear();
        GUI_CLASSES.clear();
        clearClientData();
    }

    @Override
    public void clearClientData() {
        CURRENT_GUI_NAME = null;
        CURRENT_SCREEN = null;
        CURRENT_GUI_CLASS = null;

        setInUse(false);
        CraftPresence.CLIENT.removeArguments("screen", "data.screen");
        CraftPresence.CLIENT.clearOverride("screen.message", "screen.icon");
    }

    @Override
    public void onTick() {
        enabled = !CraftPresence.CONFIG.hasChanged ? CraftPresence.CONFIG.advancedSettings.enablePerGui : enabled;
        isFocused = CraftPresence.instance.currentScreen != null && CraftPresence.instance.currentScreen.isFocused();
        final boolean needsUpdate = enabled && !hasScanned && canFetchData();

        if (needsUpdate) {
            new Thread(this::getAllData, "CraftPresence-Screen-Lookup").start();
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

        // Fallback Switch for Config Gui, used for situations where the Gui is forced closed
        // Example: This can occur during server transitions where you transition to a different world
        if (configGUIOpened && !(CraftPresence.instance.currentScreen instanceof ExtendedScreen)) {
            configGUIOpened = false;
        }
    }

    @Override
    public void updateData() {
        if (CraftPresence.instance.currentScreen == null) {
            clearClientData();
        } else {
            final GuiScreen newScreen = CraftPresence.instance.currentScreen;
            final Class<?> newScreenClass = newScreen.getClass();
            final String newScreenName = MappingUtils.getClassName(newScreen);

            if (!newScreen.equals(CURRENT_SCREEN) || !newScreenClass.equals(CURRENT_GUI_CLASS) || !newScreenName.equals(CURRENT_GUI_NAME)) {
                CURRENT_SCREEN = newScreen;
                CURRENT_GUI_CLASS = newScreenClass;
                CURRENT_GUI_NAME = newScreenName;

                if (!GUI_NAMES.contains(newScreenName)) {
                    GUI_NAMES.add(newScreenName);
                }
                if (!GUI_CLASSES.containsKey(newScreenName)) {
                    GUI_CLASSES.put(newScreenName, newScreenClass);
                }

                updatePresence();
            }
        }
    }

    @Override
    public void getAllData() {
        final List<Class<?>> searchClasses = StringUtils.newArrayList(GuiScreen.class, GuiContainer.class);

        for (Class<?> classObj : FileUtils.getClassNamesMatchingSuperType(searchClasses, CraftPresence.CONFIG.advancedSettings.includeExtraGuiClasses)) {
            final String screenName = MappingUtils.getClassName(classObj);
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
        final String formattedIcon = CraftPresence.CLIENT.imageOf("screen.icon", true, currentIcon, CraftPresence.CONFIG.advancedSettings.guiSettings.fallbackGuiIcon);

        CraftPresence.CLIENT.syncArgument("screen.default.icon", CraftPresence.CONFIG.advancedSettings.guiSettings.fallbackGuiIcon);

        CraftPresence.CLIENT.syncArgument("data.screen.instance", CURRENT_SCREEN);
        CraftPresence.CLIENT.syncArgument("screen.name", CURRENT_GUI_NAME);
        CraftPresence.CLIENT.syncArgument("data.screen.class", CURRENT_GUI_CLASS);

        CraftPresence.CLIENT.syncOverride(currentData != null ? currentData : defaultData, "screen.message", "screen.icon");
        CraftPresence.CLIENT.syncArgument("screen.message", currentMessage);
        CraftPresence.CLIENT.syncArgument("screen.icon", formattedIcon);
        CraftPresence.CLIENT.syncTimestamp("data.screen.time");
    }
}
