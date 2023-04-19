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
import com.gitlab.cdagaming.craftpresence.ModUtils;
import com.gitlab.cdagaming.craftpresence.config.Config;
import com.gitlab.cdagaming.craftpresence.config.element.ModuleData;
import com.gitlab.cdagaming.craftpresence.impl.Module;
import com.gitlab.cdagaming.craftpresence.impl.Pair;
import com.gitlab.cdagaming.craftpresence.impl.Tuple;
import com.gitlab.cdagaming.craftpresence.utils.*;
import com.gitlab.cdagaming.craftpresence.utils.gui.controls.ExtendedButtonControl;
import com.gitlab.cdagaming.craftpresence.utils.gui.controls.ExtendedTextControl;
import com.gitlab.cdagaming.craftpresence.utils.gui.integrations.ExtendedScreen;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.FontRenderer;
import net.minecraft.client.gui.GuiScreen;
import net.minecraft.client.gui.inventory.GuiContainer;
import net.minecraft.client.renderer.BufferBuilder;
import net.minecraft.client.renderer.Tessellator;
import net.minecraft.client.renderer.vertex.DefaultVertexFormats;
import net.minecraft.util.ResourceLocation;
import org.lwjgl.opengl.GL11;

import java.awt.*;
import java.io.File;
import java.util.List;
import java.util.Map;

/**
 * Gui Utilities used to Parse Gui Data and handle related RPC Events, and rendering tasks
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
     * Format the specified string to conform to the specified width
     *
     * @param fontRenderer The Font Renderer Instance
     * @param stringInput  The original String to wrap
     * @param wrapWidth    The target width per line, to wrap the input around
     * @return The converted and wrapped version of the original input
     */
    public static List<String> listFormattedStringToWidth(final FontRenderer fontRenderer, final String stringInput, final int wrapWidth) {
        return StringUtils.splitTextByNewLine(wrapFormattedStringToWidth(fontRenderer, stringInput, wrapWidth));
    }

    /**
     * Wraps a String based on the specified target width per line<p>
     * Separated by newline characters, as needed
     *
     * @param fontRenderer The Font Renderer Instance
     * @param stringInput  The original String to wrap
     * @param wrapWidth    The target width per line, to wrap the input around
     * @return The converted and wrapped version of the original input
     */
    public static String wrapFormattedStringToWidth(final FontRenderer fontRenderer, final String stringInput, final int wrapWidth) {
        final int stringSizeToWidth = sizeStringToWidth(fontRenderer, stringInput, wrapWidth);

        if (stringInput.length() <= stringSizeToWidth) {
            return stringInput;
        } else {
            final String subString = stringInput.substring(0, stringSizeToWidth);
            final char currentCharacter = stringInput.charAt(stringSizeToWidth);
            final boolean flag = Character.isSpaceChar(currentCharacter) || currentCharacter == '\n';
            final String s1 = StringUtils.getFormatFromString(subString) + stringInput.substring(stringSizeToWidth + (flag ? 1 : 0));
            return subString + "\n" + wrapFormattedStringToWidth(fontRenderer, s1, wrapWidth);
        }
    }

    /**
     * Returns the Wrapped Width of a String, defined by the target wrapWidth
     *
     * @param fontRenderer The Font Renderer Instance
     * @param stringEntry  The original String to evaluate
     * @param wrapWidth    The target width to wrap within
     * @return The expected wrapped width the String should be
     */
    public static int sizeStringToWidth(final FontRenderer fontRenderer, final String stringEntry, final int wrapWidth) {
        final int stringLength = stringEntry.length();
        int charWidth = 0;
        int currentLine = 0;
        int currentIndex = -1;

        for (boolean flag = false; currentLine < stringLength; ++currentLine) {
            char currentCharacter = stringEntry.charAt(currentLine);
            String stringOfCharacter = String.valueOf(currentCharacter);

            if (currentCharacter == ' ' || currentCharacter == '\n') {
                currentIndex = currentLine;

                if (currentCharacter == '\n') {
                    break;
                }
            }

            if (currentCharacter == StringUtils.COLOR_CHAR && currentLine < stringLength - 1) {
                ++currentLine;
                currentCharacter = stringEntry.charAt(currentLine);
                stringOfCharacter = String.valueOf(currentCharacter);

                flag = stringOfCharacter.equalsIgnoreCase("l") && !(stringOfCharacter.equalsIgnoreCase("r") ||
                        StringUtils.STRIP_COLOR_PATTERN.matcher(stringOfCharacter).find());
            }

            charWidth += fontRenderer.getStringWidth(stringOfCharacter);
            if (flag) {
                ++charWidth;
            }

            if (charWidth > wrapWidth) {
                break;
            }
        }

        return currentLine != stringLength && currentIndex != -1 && currentIndex < currentLine ? currentIndex : currentLine;
    }

    public void blit(final double xPos, final double yPos,
                     final double zLevel,
                     final double uOffset, final double vOffset,
                     final double uWidth, final double vHeight) {
        blit(xPos, yPos, zLevel, uOffset, vOffset, uWidth, vHeight, 256, 256);
    }

    public void blit(final double xPos, final double yPos,
                     final double zLevel,
                     final double uOffset, final double vOffset,
                     final double uWidth, final double vHeight,
                     final double textureWidth, final double textureHeight) {
        innerBlit(xPos, xPos + uWidth, yPos, yPos + vHeight,
                zLevel,
                uWidth, vHeight,
                uOffset, vOffset,
                textureWidth, textureHeight
        );
    }

    public void innerBlit(final double left, final double right, final double top, final double bottom,
                          final double zLevel,
                          final double uWidth, final double vHeight,
                          final double uOffset, final double vOffset,
                          final double textureWidth, final double textureHeight) {
        innerBlit(left, right, top, bottom,
                zLevel,
                (uOffset + 0.0D) / textureWidth, (uOffset + uWidth) / textureWidth,
                (vOffset + 0.0D) / textureHeight, (vOffset + vHeight) / textureHeight
        );
    }

    public void innerBlit(final double left, final double right, final double top, final double bottom,
                          final double zLevel,
                          final double minU, final double maxU,
                          final double minV, final double maxV) {
        final Tessellator tessellator = Tessellator.getInstance();
        final BufferBuilder buffer = tessellator.getBuffer();
        buffer.begin(GL11.GL_QUADS, DefaultVertexFormats.POSITION_TEX);
        buffer.pos(left, bottom, zLevel).tex(minU, maxV).endVertex();
        buffer.pos(right, bottom, zLevel).tex(maxU, maxV).endVertex();
        buffer.pos(right, top, zLevel).tex(maxU, minV).endVertex();
        buffer.pos(left, top, zLevel).tex(minU, minV).endVertex();
        tessellator.draw();
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

    /**
     * Adds a Scheduled/Queued Task to Display the Specified Gui Screen
     *
     * @param mc           The current game instance
     * @param targetScreen The target Gui Screen to display
     */
    public void openScreen(final Minecraft mc, final GuiScreen targetScreen) {
        mc.addScheduledTask(() -> mc.displayGuiScreen(targetScreen));
    }

    /**
     * Adds a Scheduled/Queued Task to Display the Specified Gui Screen
     *
     * @param targetScreen The target Gui Screen to display
     */
    public void openScreen(final GuiScreen targetScreen) {
        openScreen(CraftPresence.instance, targetScreen);
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

    /**
     * Renders a Specified Multi-Line String, constrained by position and dimension arguments
     *
     * @param textToInput    The Specified Multi-Line String, split by lines into a list
     * @param posX           The starting X position to render the String
     * @param posY           The starting Y position to render the String
     * @param screenWidth    The maximum width to allow rendering to (Text will wrap if output is greater)
     * @param screenHeight   The maximum height to allow rendering to (Text will wrap if output is greater)
     * @param maxTextWidth   The maximum width the output can be before wrapping
     * @param fontRenderer   The font renderer to use to render the String
     * @param withBackground Whether a background should display around and under the String, like a tooltip
     */
    public void drawMultiLineString(final List<String> textToInput, final int posX, final int posY, final int screenWidth, final int screenHeight, final int maxTextWidth, final FontRenderer fontRenderer, final boolean withBackground) {
        if (CraftPresence.CONFIG.advancedSettings.renderTooltips && !ModUtils.forceBlockTooltipRendering && !textToInput.isEmpty() && fontRenderer != null) {
            List<String> textLines = textToInput;
            int tooltipTextWidth = 0;

            for (String textLine : textLines) {
                final int textLineWidth = fontRenderer.getStringWidth(textLine);

                if (textLineWidth > tooltipTextWidth) {
                    tooltipTextWidth = textLineWidth;
                }
            }

            boolean needsWrap = false;

            int titleLinesCount = 1;
            int tooltipX = posX + 12;
            if (tooltipX + tooltipTextWidth + 4 > screenWidth) {
                tooltipX = posX - 16 - tooltipTextWidth;
                if (tooltipX < 4) // if the tooltip doesn't fit on the screen
                {
                    if (posX > screenWidth / 2) {
                        tooltipTextWidth = posX - 12 - 8;
                    } else {
                        tooltipTextWidth = screenWidth - 16 - posX;
                    }
                    needsWrap = true;
                }
            }

            if (maxTextWidth > 0 && tooltipTextWidth > maxTextWidth) {
                tooltipTextWidth = maxTextWidth;
                needsWrap = true;
            }

            if (needsWrap) {
                final List<String> wrappedTextLines = StringUtils.newArrayList();
                int wrappedTooltipWidth = 0;
                for (int i = 0; i < textLines.size(); i++) {
                    final List<String> wrappedLine = StringUtils.splitTextByNewLine(wrapFormattedStringToWidth(fontRenderer, textLines.get(i), tooltipTextWidth));
                    if (i == 0) {
                        titleLinesCount = wrappedLine.size();
                    }

                    for (String line : wrappedLine) {
                        int lineWidth = fontRenderer.getStringWidth(line);
                        if (lineWidth > wrappedTooltipWidth) {
                            wrappedTooltipWidth = lineWidth;
                        }
                        wrappedTextLines.add(line);
                    }
                }
                tooltipTextWidth = wrappedTooltipWidth;
                textLines = wrappedTextLines;

                if (posX > screenWidth / 2) {
                    tooltipX = posX - 16 - tooltipTextWidth;
                } else {
                    tooltipX = posX + 12;
                }
            }

            int tooltipY = posY - 12;
            int tooltipHeight = 8;

            if (textLines.size() > 1) {
                tooltipHeight += (textLines.size() - 1) * 10;
                if (textLines.size() > titleLinesCount) {
                    tooltipHeight += 2; // gap between title lines and next lines
                }
            }

            if (tooltipY < 4) {
                tooltipY = 4;
            } else if (tooltipY + tooltipHeight + 4 > screenHeight) {
                tooltipY = screenHeight - tooltipHeight - 4;
            }

            if (withBackground) {
                GL11.glDisable(GL11.GL_DEPTH_TEST);

                final int zLevel = 300;

                final Pair<Boolean, String> backgroundColorData = getColorData(CraftPresence.CONFIG.accessibilitySettings.tooltipBackgroundColor);
                if (backgroundColorData.getFirst()) {
                    final String backgroundColor = backgroundColorData.getSecond();

                    // Draw with Colors
                    drawGradientRect(zLevel, tooltipX - 3, tooltipY - 4, tooltipX + tooltipTextWidth + 3, tooltipY - 3, backgroundColor, backgroundColor);
                    drawGradientRect(zLevel, tooltipX - 3, tooltipY + tooltipHeight + 3, tooltipX + tooltipTextWidth + 3, tooltipY + tooltipHeight + 4, backgroundColor, backgroundColor);
                    drawGradientRect(zLevel, tooltipX - 3, tooltipY - 3, tooltipX + tooltipTextWidth + 3, tooltipY + tooltipHeight + 3, backgroundColor, backgroundColor);
                    drawGradientRect(zLevel, tooltipX - 4, tooltipY - 3, tooltipX - 3, tooltipY + tooltipHeight + 3, backgroundColor, backgroundColor);
                    drawGradientRect(zLevel, tooltipX + tooltipTextWidth + 3, tooltipY - 3, tooltipX + tooltipTextWidth + 4, tooltipY + tooltipHeight + 3, backgroundColor, backgroundColor);
                } else {
                    final Tuple<Boolean, String, ResourceLocation> textureData = getTextureData(CraftPresence.CONFIG.accessibilitySettings.tooltipBackgroundColor);
                    final ResourceLocation backGroundTexture = textureData.getThird();
                    double widthDivider = 32.0D, heightDivider = 32.0D;

                    if (textureData.getFirst()) {
                        widthDivider = tooltipTextWidth + 8;
                        heightDivider = tooltipHeight + 8;
                    }

                    drawTextureRect(zLevel, tooltipX - 4, tooltipY - 4, tooltipTextWidth + 8, tooltipHeight + 8, 0, widthDivider, heightDivider, false, backGroundTexture);
                }

                final Pair<Boolean, String> borderColorData = getColorData(CraftPresence.CONFIG.accessibilitySettings.tooltipBorderColor);
                if (borderColorData.getFirst()) {
                    final String borderColor = borderColorData.getSecond();

                    // Draw with Colors
                    final int borderColorCode = (borderColor.startsWith("#") ? StringUtils.getColorFrom(borderColor).getRGB() : Integer.parseInt(borderColor));
                    final String borderColorEnd = Integer.toString((borderColorCode & 0xFEFEFE) >> 1 | borderColorCode & 0xFF000000);

                    drawGradientRect(zLevel, tooltipX - 3, tooltipY - 3 + 1, tooltipX - 3 + 1, tooltipY + tooltipHeight + 3 - 1, borderColor, borderColorEnd);
                    drawGradientRect(zLevel, tooltipX + tooltipTextWidth + 2, tooltipY - 3 + 1, tooltipX + tooltipTextWidth + 3, tooltipY + tooltipHeight + 3 - 1, borderColor, borderColorEnd);
                    drawGradientRect(zLevel, tooltipX - 3, tooltipY - 3, tooltipX + tooltipTextWidth + 3, tooltipY - 3 + 1, borderColor, borderColor);
                    drawGradientRect(zLevel, tooltipX - 3, tooltipY + tooltipHeight + 2, tooltipX + tooltipTextWidth + 3, tooltipY + tooltipHeight + 3, borderColorEnd, borderColorEnd);
                } else {
                    final Tuple<Boolean, String, ResourceLocation> textureData = getTextureData(CraftPresence.CONFIG.accessibilitySettings.tooltipBorderColor);
                    final ResourceLocation borderTexture = textureData.getThird();
                    final boolean usingExternalTexture = textureData.getFirst();

                    drawTextureRect(zLevel, tooltipX - 3, tooltipY - 3, tooltipTextWidth + 5, 1, 0, (usingExternalTexture ? tooltipTextWidth + 5 : 32.0D), (usingExternalTexture ? 1 : 32.0D), false, borderTexture); // Top Border
                    drawTextureRect(zLevel, tooltipX - 3, tooltipY + tooltipHeight + 2, tooltipTextWidth + 5, 1, 0, (usingExternalTexture ? tooltipTextWidth + 5 : 32.0D), (usingExternalTexture ? 1 : 32.0D), false, borderTexture); // Bottom Border
                    drawTextureRect(zLevel, tooltipX - 3, tooltipY - 3, 1, tooltipHeight + 5, 0, (usingExternalTexture ? 1 : 32.0D), (usingExternalTexture ? tooltipHeight + 5 : 32.0D), false, borderTexture); // Left Border
                    drawTextureRect(zLevel, tooltipX + tooltipTextWidth + 2, tooltipY - 3, 1, tooltipHeight + 6, 0, (usingExternalTexture ? 1 : 32.0D), (usingExternalTexture ? tooltipHeight + 6 : 32.0D), false, borderTexture); // Right Border
                }
            }

            for (int lineNumber = 0; lineNumber < textLines.size(); ++lineNumber) {
                final String line = textLines.get(lineNumber);
                fontRenderer.drawStringWithShadow(line, tooltipX, tooltipY, -1);

                if (lineNumber + 1 == titleLinesCount) {
                    tooltipY += 2;
                }

                tooltipY += 10;
            }

            if (withBackground) {
                GL11.glEnable(GL11.GL_DEPTH_TEST);
            }
        }
    }

    /**
     * Renders a Specified Multi-Line String, constrained by position and dimension arguments
     *
     * @param textToInput    The Specified Multi-Line String, split by lines into a list
     * @param posX           The starting X position to render the String
     * @param posY           The starting Y position to render the String
     * @param screenInstance The screen instance to use to render the String
     * @param withBackground Whether a background should display around and under the String, like a tooltip
     */
    public void drawMultiLineString(final List<String> textToInput, final int posX, final int posY, final ExtendedScreen screenInstance, final boolean withBackground) {
        drawMultiLineString(textToInput,
                posX, posY,
                screenInstance.getScreenWidth(), screenInstance.getScreenHeight(),
                screenInstance.getWrapWidth(),
                screenInstance.getFontRenderer(),
                withBackground
        );
    }

    /**
     * Renders a Specified Multi-Line String, constrained by position and dimension arguments
     *
     * @param textToInput    The Specified Multi-Line String, split by lines into a list
     * @param screenInstance The screen instance to use to render the String
     * @param withBackground Whether a background should display around and under the String, like a tooltip
     */
    public void drawMultiLineString(final List<String> textToInput, final ExtendedScreen screenInstance, final boolean withBackground) {
        drawMultiLineString(textToInput, screenInstance.getMouseX(), screenInstance.getMouseY(), screenInstance, withBackground);
    }

    /**
     * Retrieve color data for the specified string, if possible
     *
     * @param texture The data to interpret
     * @return a {@link Pair} with the mapping "isColorCode:data"
     */
    public Pair<Boolean, String> getColorData(String texture) {
        final Pair<Boolean, String> result = new Pair<>(false, texture);
        if (!StringUtils.isNullOrEmpty(texture)) {
            texture = texture.trim();
        } else {
            return result;
        }

        final boolean isColorCode = StringUtils.isValidColorCode(texture);
        if (isColorCode) {
            if (texture.length() == 6) {
                texture = "#" + texture;
            } else if (texture.startsWith("0x")) {
                texture = Long.toString(Long.decode(texture).intValue());
            }
        }
        return result.put(isColorCode, texture);
    }

    /**
     * Retrieve texture data for the specified string, if possible
     *
     * @param texture The data to interpret
     * @return a {@link Tuple} with the mapping "usingExternalData:location:resource"
     */
    public Tuple<Boolean, String, ResourceLocation> getTextureData(String texture) {
        ResourceLocation texLocation = new ResourceLocation("");
        final Tuple<Boolean, String, ResourceLocation> result = new Tuple<>(false, "", texLocation);
        if (!StringUtils.isNullOrEmpty(texture)) {
            texture = texture.trim();
        } else {
            return result;
        }

        final boolean isColorCode = StringUtils.isValidColorCode(texture);
        boolean usingExternalTexture = false;

        if (!isColorCode) {
            usingExternalTexture = ImageUtils.isExternalImage(texture);

            // Only Perform Texture Conversion Steps if not an external Url
            // As an external Url should be parsed as-is in most use cases
            //
            // Only when we are not using an external texture, would we then need
            // to convert the path to Minecraft's normal format.
            //
            // If we are using an external texture however, then we'd just make
            // a texture name from the last part of the url and retrieve the external texture
            if (!usingExternalTexture) {
                if (texture.startsWith(":")) {
                    texture = texture.substring(1);
                }

                if (texture.contains(":")) {
                    String[] splitInput = texture.split(":", 2);
                    texLocation = new ResourceLocation(splitInput[0], splitInput[1]);
                } else {
                    texLocation = new ResourceLocation(texture);
                }
            } else {
                final String formattedConvertedName = texture.replaceFirst("file://", "");
                final String[] urlBits = formattedConvertedName.trim().split("/");
                final String textureName = urlBits[urlBits.length - 1].trim();
                texLocation = ImageUtils.getTextureFromUrl(textureName, texture.toLowerCase().startsWith("file://") ? new File(formattedConvertedName) : formattedConvertedName);
            }
        }
        return result.put(usingExternalTexture, texture, texLocation);
    }

    /**
     * Computes the current GUI scale. Calling this method is equivalent to the following:<pre><code>
     * Minecraft mc = Minecraft.getMinecraft();
     * int scale = new ScaledResolution(mc.gameSettings, mc.displayWidth, mc.displayHeight).getScaleFactor();</code></pre>
     *
     * @param mc The Minecraft Instance
     * @return the current GUI scale
     */
    public int computeGuiScale(final Minecraft mc) {
        int scaleFactor = 1;

        int k = mc.gameSettings.guiScale;
        if (k == 0) {
            k = 1000;
        }

        while (scaleFactor < k && mc.displayWidth / (scaleFactor + 1) >= 320 && mc.displayHeight / (scaleFactor + 1) >= 240) {
            ++scaleFactor;
        }
        return scaleFactor;
    }

    /**
     * Computes the current GUI scale. Calling this method is equivalent to the following:<pre><code>
     * Minecraft mc = Minecraft.getMinecraft();
     * int scale = new ScaledResolution(mc.gameSettings, mc.displayWidth, mc.displayHeight).getScaleFactor();</code></pre>
     *
     * @return the current GUI scale
     */
    public int computeGuiScale() {
        return computeGuiScale(CraftPresence.instance);
    }

    /**
     * Define viewable rendering boundaries, utilizing glScissor
     *
     * @param xPos   The Starting X Position of the Object
     * @param yPos   The Starting Y Position of the Object
     * @param width  The width to render the data to
     * @param height The height to render the data to
     */
    public void drawWithin(final int xPos, final int yPos, final int width, final int height) {
        GL11.glEnable(GL11.GL_SCISSOR_TEST);
        GL11.glScissor(xPos, yPos, width, height);
    }

    /**
     * Disables current rendering boundary flags, mainly glScissor
     */
    public void drawAnywhere() {
        GL11.glDisable(GL11.GL_SCISSOR_TEST);
    }

    /**
     * Draws a Background onto a Gui, supporting RGBA Codes, Game Textures and Hexadecimal Colors
     *
     * @param xPos           The Starting X Position of the Object
     * @param yPos           The Starting Y Position of the Object
     * @param width          The width to render the background to
     * @param height         The height to render the background to
     * @param backgroundCode The background render data to interpret
     * @param color          The background RGB data to interpret
     */
    public void drawBackground(final double xPos, final double yPos, final double width, final double height, final String backgroundCode, final Color color) {
        if (StringUtils.isValidColorCode(backgroundCode)) {
            drawGradientRect(300.0F, xPos, yPos, xPos + width, yPos + height, backgroundCode, backgroundCode);
        } else {
            final Tuple<Boolean, String, ResourceLocation> textureData = getTextureData(backgroundCode);
            final ResourceLocation texLocation = textureData.getThird();

            final double widthDivider = textureData.getFirst() ? width : 32.0D;
            final double heightDivider = textureData.getFirst() ? height : 32.0D;

            drawTextureRect(0.0D, xPos, yPos, width, height, 0, widthDivider, heightDivider, color, texLocation);
        }
    }

    /**
     * Draws a Background onto a Gui, supporting RGBA Codes, Game Textures and Hexadecimal Colors
     *
     * @param xPos           The Starting X Position of the Object
     * @param yPos           The Starting Y Position of the Object
     * @param width          The width to render the background to
     * @param height         The height to render the background to
     * @param backgroundCode The background render data to interpret
     * @param shouldBeDark   Whether the background data should display in a darker format
     */
    public void drawBackground(final double xPos, final double yPos, final double width, final double height, final String backgroundCode, final boolean shouldBeDark) {
        final Color color = shouldBeDark ? Color.darkGray : Color.white;
        drawBackground(xPos, yPos, width, height, backgroundCode, color);
    }

    /**
     * Draws a Background onto a Gui, supporting RGBA Codes, Game Textures and Hexadecimal Colors
     *
     * @param xPos           The Starting X Position of the Object
     * @param yPos           The Starting Y Position of the Object
     * @param width          The width to render the background to
     * @param height         The height to render the background to
     * @param backgroundCode The background render data to interpret
     */
    public void drawBackground(final double xPos, final double yPos, final double width, final double height, final String backgroundCode) {
        drawBackground(xPos, yPos, width, height, backgroundCode, CraftPresence.CONFIG.accessibilitySettings.showBackgroundAsDark);
    }

    /**
     * Draws a Background onto a Gui, supporting RGBA Codes, Game Textures and Hexadecimal Colors
     *
     * @param xPos   The Starting X Position of the Object
     * @param yPos   The Starting Y Position of the Object
     * @param width  The width to render the background to
     * @param height The height to render the background to
     * @param color  The background RGB data to interpret
     */
    public void drawBackground(final double xPos, final double yPos, final double width, final double height, final Color color) {
        drawBackground(xPos, yPos, width, height, CraftPresence.CONFIG.accessibilitySettings.guiBackgroundColor, color);
    }

    /**
     * Draws a Background onto a Gui, supporting RGBA Codes, Game Textures and Hexadecimal Colors
     *
     * @param xPos         The Starting X Position of the Object
     * @param yPos         The Starting Y Position of the Object
     * @param width        The width to render the background to
     * @param height       The height to render the background to
     * @param shouldBeDark Whether the background data should display in a darker format
     */
    public void drawBackground(final double xPos, final double yPos, final double width, final double height, final boolean shouldBeDark) {
        final Color color = shouldBeDark ? Color.darkGray : Color.white;
        drawBackground(xPos, yPos, width, height, color);
    }

    /**
     * Draws a Background onto a Gui, supporting RGBA Codes, Game Textures and Hexadecimal Colors
     *
     * @param xPos   The Starting X Position of the Object
     * @param yPos   The Starting Y Position of the Object
     * @param width  The width to render the background to
     * @param height The height to render the background to
     */
    public void drawBackground(final double xPos, final double yPos, final double width, final double height) {
        drawBackground(xPos, yPos, width, height, CraftPresence.CONFIG.accessibilitySettings.showBackgroundAsDark);
    }

    /**
     * Draws a Background onto a Gui, supporting RGBA Codes, Game Textures and Hexadecimal Colors
     *
     * @param width  The width to render the background to
     * @param height The height to render the background to
     * @param color  The background RGB data to interpret
     */
    public void drawBackground(final double width, final double height, final Color color) {
        drawBackground(0.0D, 0.0D, width, height, color);
    }

    /**
     * Draws a Background onto a Gui, supporting RGBA Codes, Game Textures and Hexadecimal Colors
     *
     * @param width        The width to render the background to
     * @param height       The height to render the background to
     * @param shouldBeDark Whether the background data should display in a darker format
     */
    public void drawBackground(final double width, final double height, final boolean shouldBeDark) {
        final Color color = shouldBeDark ? Color.darkGray : Color.white;
        drawBackground(width, height, color);
    }

    /**
     * Draws a Background onto a Gui, supporting RGBA Codes, Game Textures and Hexadecimal Colors
     *
     * @param width  The width to render the background to
     * @param height The height to render the background to
     */
    public void drawBackground(final double width, final double height) {
        drawBackground(width, height, CraftPresence.CONFIG.accessibilitySettings.showBackgroundAsDark);
    }

    /**
     * Renders a Slider Object from the defined arguments
     *
     * @param x           The Starting X Position to render the slider
     * @param y           The Starting Y Position to render the slider
     * @param u           The U Mapping Value
     * @param v           The V Mapping Value
     * @param width       The full width for the slider to render to
     * @param height      The full height for the slider to render to
     * @param zLevel      The Z level position for the slider to render at
     * @param texLocation The game texture to render the slider as
     */
    public void renderSlider(final int x, final int y, final int u, final int v, final int width, final int height, final double zLevel, final ResourceLocation texLocation) {
        try {
            if (texLocation != null) {
                CraftPresence.instance.getTextureManager().bindTexture(texLocation);
            }
        } catch (Exception ignored) {
        }
        GL11.glColor4f(1.0F, 1.0F, 1.0F, 1.0F);

        blit(x, y, zLevel, u, v, width, height);
        blit(x + 4, y, zLevel, u + 196, v, width, height);
    }

    /**
     * Renders a Gradient Box from the defined arguments
     *
     * @param posX            The Starting X Position to render the object
     * @param posY            The Starting Y Position to render the object
     * @param width           The full width for the object to render to
     * @param height          The full height for the object to render to
     * @param zLevel          The Z level position for the object to render at
     * @param borderColor     The starting border color for the object
     * @param borderColorEnd  The ending border color for the object
     * @param border          The full width of the border for the object
     * @param contentColor    The starting content color for the object
     * @param contentColorEnd The ending content color for the object
     */
    public void renderGradientBox(final double posX, final double posY, final double width, final double height, final float zLevel,
                                  final String borderColor, final String borderColorEnd, final int border,
                                  final String contentColor, final String contentColorEnd) {
        final double canvasWidth = width - (border * 2);
        final double canvasHeight = height - (border * 2);

        final double canvasRight = posX + border + canvasWidth;
        final double canvasBottom = posY + border + canvasHeight;

        // Draw Borders
        // Top Left
        drawGradientRect(zLevel, posX, posY, posX + border, canvasBottom + border, borderColor, borderColorEnd);
        // Top Right
        drawGradientRect(zLevel, canvasRight, posY, canvasRight + border, canvasBottom + border, borderColor, borderColorEnd);
        // Bottom Left
        drawGradientRect(zLevel, posX, canvasBottom, canvasRight + border, canvasBottom + border, borderColor, borderColorEnd);
        // Bottom Right
        drawGradientRect(zLevel, posX, posY, canvasRight + border, posY + border, borderColor, borderColorEnd);

        // Draw Content Box
        drawGradientRect(zLevel, posX + border, posY + border, canvasRight, canvasBottom, contentColor, contentColorEnd);
    }

    /**
     * Renders a Button Object from the defined arguments
     *
     * @param x           The Starting X Position to render the button
     * @param y           The Starting Y Position to render the button
     * @param width       The full width for the button to render to
     * @param height      The full height for the button to render to
     * @param hoverState  The hover state for the button
     * @param zLevel      The Z level position for the button to render at
     * @param texLocation The game texture to render the button as
     */
    public void renderButton(final int x, final int y, final int width, final int height, final int hoverState, final double zLevel, final ResourceLocation texLocation) {
        try {
            if (texLocation != null) {
                CraftPresence.instance.getTextureManager().bindTexture(texLocation);
            }
        } catch (Exception ignored) {
        }
        GL11.glColor4f(1.0F, 1.0F, 1.0F, 1.0F);
        GL11.glEnable(GL11.GL_BLEND);
        GL11.glBlendFunc(GL11.GL_SRC_ALPHA, GL11.GL_ONE_MINUS_SRC_ALPHA);
        GL11.glEnable(GL11.GL_DEPTH_TEST);

        final int v = 46 + hoverState * 20;
        final int xOffset = width / 2;

        blit(x, y, zLevel, 0, v, xOffset, height);
        blit(x + xOffset, y, zLevel, 200 - xOffset, v, xOffset, height);
    }

    /**
     * Draws a Textured Rectangle, following the defined arguments
     *
     * @param zLevel      The Z Level Position of the Object
     * @param xPos        The Starting X Position of the Object
     * @param yPos        The Starting Y Position of the Object
     * @param width       The Width of the Object
     * @param height      The Height of the Object
     * @param tint        The Tinting Level of the Object
     * @param texLocation The game texture to render the object as
     */
    public void drawTextureRect(final double zLevel, final double xPos, final double yPos, final double width, final double height, final double tint, final ResourceLocation texLocation) {
        drawTextureRect(zLevel, xPos, yPos, width, height, tint, 32.0D, 32.0D, false, texLocation);
    }

    /**
     * Draws a Textured Rectangle, following the defined arguments
     *
     * @param zLevel      The Z Level Position of the Object
     * @param xPos        The Starting X Position of the Object
     * @param yPos        The Starting Y Position of the Object
     * @param width       The Width of the Object
     * @param height      The Height of the Object
     * @param tint        The Tinting Level of the Object
     * @param rgbData     The texture RGB data to interpret
     * @param texLocation The game texture to render the object as
     */
    public void drawTextureRect(final double zLevel, final double xPos, final double yPos, final double width, final double height, final double tint, final Color rgbData, final ResourceLocation texLocation) {
        drawTextureRect(zLevel, xPos, yPos, width, height, tint, 32.0D, 32.0D, rgbData, texLocation);
    }

    /**
     * Draws a Textured Rectangle, following the defined arguments
     *
     * @param zLevel        The Z Level Position of the Object
     * @param xPos          The Starting X Position of the Object
     * @param yPos          The Starting Y Position of the Object
     * @param width         The Width of the Object
     * @param height        The Height of the Object
     * @param tint          The Tinting Level of the Object
     * @param widthDivider  The number to divide the width by when rendering
     * @param heightDivider The number to divide the height by when rendering
     * @param color         The texture RGB data to interpret
     * @param texLocation   The game texture to render the object as
     */
    public void drawTextureRect(final double zLevel, final double xPos, final double yPos, final double width, final double height, final double tint, final double widthDivider, final double heightDivider, final Color color, final ResourceLocation texLocation) {
        drawTextureRect(zLevel,
                xPos,
                yPos,
                xPos + width,
                yPos + height,
                width / widthDivider,
                height / heightDivider,
                tint,
                color, color,
                texLocation
        );
    }

    /**
     * Draws a Textured Rectangle, following the defined arguments
     *
     * @param zLevel      The Z Level Position of the Object
     * @param left        The Left Position of the Object
     * @param top         The Top Position of the Object
     * @param right       The Right Position of the Object
     * @param bottom      The Bottom Position of the Object
     * @param u           The horizontal axis to render this Object by
     * @param v           The vertical axis to render this Object by
     * @param tint        The Tinting Level of the Object
     * @param startColor  The starting texture RGB data to interpret
     * @param endColor    The starting texture RGB data to interpret
     * @param texLocation The game texture to render the object as
     */
    public void drawTextureRect(final double zLevel, final double left, final double top, final double right, final double bottom, final double u, final double v, final double tint, final Color startColor, final Color endColor, final ResourceLocation texLocation) {
        try {
            if (texLocation != null) {
                CraftPresence.instance.getTextureManager().bindTexture(texLocation);
            }
        } catch (Exception ignored) {
        }

        GL11.glDisable(GL11.GL_LIGHTING);
        GL11.glDisable(GL11.GL_FOG);
        GL11.glColor4f(1.0F, 1.0F, 1.0F, 1.0F);

        final Tessellator tessellator = Tessellator.getInstance();
        final BufferBuilder buffer = tessellator.getBuffer();
        buffer.begin(GL11.GL_QUADS, DefaultVertexFormats.POSITION_TEX_COLOR);
        buffer.pos(left, bottom, zLevel).tex(0.0D, (v + tint)).color(startColor.getRed(), startColor.getGreen(), startColor.getBlue(), startColor.getAlpha()).endVertex();
        buffer.pos(right, bottom, zLevel).tex(u, (v + tint)).color(startColor.getRed(), startColor.getGreen(), startColor.getBlue(), startColor.getAlpha()).endVertex();
        buffer.pos(right, top, zLevel).tex(u, tint).color(endColor.getRed(), endColor.getGreen(), endColor.getBlue(), endColor.getAlpha()).endVertex();
        buffer.pos(left, top, zLevel).tex(0.0D, tint).color(endColor.getRed(), endColor.getGreen(), endColor.getBlue(), endColor.getAlpha()).endVertex();
        tessellator.draw();
    }

    /**
     * Draws a Textured Gradient Rectangle, following the defined arguments
     *
     * @param zLevel      The Z Level Position of the Object
     * @param left        The Left Position of the Object
     * @param top         The Top Position of the Object
     * @param right       The Right Position of the Object
     * @param bottom      The Bottom Position of the Object
     * @param u           The horizontal axis to render this Object by
     * @param v           The vertical axis to render this Object by
     * @param tint        The Tinting Level of the Object
     * @param startColor  The starting texture RGB data to interpret
     * @param endColor    The starting texture RGB data to interpret
     * @param texLocation The game texture to render the object as
     */
    public void drawTextureGradientRect(final double zLevel, final double left, final double top, final double right, final double bottom, final double u, final double v, final double tint, final Color startColor, final Color endColor, final ResourceLocation texLocation) {
        GL11.glDisable(GL11.GL_DEPTH_TEST);
        GL11.glDisable(GL11.GL_TEXTURE_2D);
        GL11.glEnable(GL11.GL_BLEND);
        GL11.glDisable(GL11.GL_ALPHA_TEST);
        GL11.glBlendFunc(GL11.GL_SRC_ALPHA, GL11.GL_ONE_MINUS_SRC_ALPHA);
        GL11.glShadeModel(GL11.GL_SMOOTH);

        drawTextureRect(zLevel,
                left, top,
                right, bottom,
                u, v, tint,
                startColor, endColor,
                texLocation
        );

        GL11.glShadeModel(GL11.GL_FLAT);
        GL11.glDisable(GL11.GL_BLEND);
        GL11.glEnable(GL11.GL_ALPHA_TEST);
        GL11.glEnable(GL11.GL_TEXTURE_2D);
        GL11.glEnable(GL11.GL_DEPTH_TEST);
    }

    /**
     * Draws a Textured Rectangle, following the defined arguments
     *
     * @param zLevel        The Z Level Position of the Object
     * @param xPos          The Starting X Position of the Object
     * @param yPos          The Starting Y Position of the Object
     * @param width         The Width of the Object
     * @param height        The Height of the Object
     * @param tint          The Tinting Level of the Object
     * @param widthDivider  The number to divide the width by when rendering
     * @param heightDivider The number to divide the height by when rendering
     * @param shouldBeDark  Whether the Texture should display in a darker format
     * @param texLocation   The game texture to render the object as
     */
    public void drawTextureRect(final double zLevel, final double xPos, final double yPos, final double width, final double height, final double tint, final double widthDivider, final double heightDivider, final boolean shouldBeDark, final ResourceLocation texLocation) {
        final Color color = shouldBeDark ? Color.darkGray : Color.white;
        drawTextureRect(zLevel, xPos, yPos, width, height, tint, widthDivider, heightDivider, color, texLocation);
    }

    /**
     * Draws a Gradient Rectangle, following the defined arguments
     *
     * @param zLevel         The Z Level Position of the Object
     * @param left           The Left side length of the Object
     * @param top            The top length of the Object
     * @param right          The Right side length of the Object
     * @param bottom         The bottom length of the Object
     * @param startColorCode The Starting Hexadecimal or RGBA Color Code
     * @param endColorCode   The ending Hexadecimal or RGBA Color Code
     */
    public void drawGradientRect(final float zLevel, final double left, final double top, final double right, final double bottom, final String startColorCode, final String endColorCode) {
        Color startColorObj = null, endColorObj = null;
        int startColor = 0xFFFFFF, endColor = 0xFFFFFF;

        if (!StringUtils.isNullOrEmpty(startColorCode)) {
            if (StringUtils.isValidColor(startColorCode).getFirst()) {
                startColorObj = StringUtils.getColorFrom(startColorCode);
                endColorObj = (!StringUtils.isNullOrEmpty(endColorCode) && StringUtils.isValidColor(endColorCode).getFirst()) ? StringUtils.getColorFrom(endColorCode) : startColorObj;
            } else {
                // Determine if Start Color Code is a Valid Number
                final Pair<Boolean, Integer> startColorData = StringUtils.getValidInteger(startColorCode),
                        endColorData = StringUtils.getValidInteger(endColorCode);

                // Check and ensure that at least one of the Color Codes are correct
                if (startColorData.getFirst() || endColorData.getFirst()) {
                    startColor = startColorData.getFirst() ? startColorData.getSecond() : endColor;
                    endColor = endColorData.getFirst() ? endColorData.getSecond() : startColor;
                }
            }
        }

        if (startColorObj == null) {
            startColorObj = StringUtils.getColorFrom(startColor);
        }
        if (endColorObj == null) {
            endColorObj = StringUtils.getColorFrom(endColor);
        }

        drawGradientRect(zLevel,
                left, top,
                right, bottom,
                startColorObj, endColorObj
        );
    }

    /**
     * Draws a Gradient Rectangle, following the defined arguments
     *
     * @param zLevel     The Z Level Position of the Object
     * @param left       The Left side length of the Object
     * @param top        The top length of the Object
     * @param right      The Right side length of the Object
     * @param bottom     The bottom length of the Object
     * @param startColor The Starting Color Data
     * @param endColor   The ending Color Data
     */
    public void drawGradientRect(final float zLevel, final double left, final double top, final double right, final double bottom, final Color startColor, final Color endColor) {
        GL11.glDisable(GL11.GL_DEPTH_TEST);
        GL11.glDisable(GL11.GL_TEXTURE_2D);
        GL11.glEnable(GL11.GL_BLEND);
        GL11.glDisable(GL11.GL_ALPHA_TEST);
        GL11.glBlendFunc(GL11.GL_SRC_ALPHA, GL11.GL_ONE_MINUS_SRC_ALPHA);
        GL11.glShadeModel(GL11.GL_SMOOTH);

        final Tessellator tessellator = Tessellator.getInstance();
        final BufferBuilder buffer = tessellator.getBuffer();
        buffer.begin(GL11.GL_QUADS, DefaultVertexFormats.POSITION_COLOR);
        buffer.pos(right, top, zLevel).color(startColor.getRed(), startColor.getGreen(), startColor.getBlue(), startColor.getAlpha()).endVertex();
        buffer.pos(left, top, zLevel).color(startColor.getRed(), startColor.getGreen(), startColor.getBlue(), startColor.getAlpha()).endVertex();
        buffer.pos(left, bottom, zLevel).color(endColor.getRed(), endColor.getGreen(), endColor.getBlue(), endColor.getAlpha()).endVertex();
        buffer.pos(right, bottom, zLevel).color(endColor.getRed(), endColor.getGreen(), endColor.getBlue(), endColor.getAlpha()).endVertex();
        tessellator.draw();

        GL11.glShadeModel(GL11.GL_FLAT);
        GL11.glDisable(GL11.GL_BLEND);
        GL11.glEnable(GL11.GL_ALPHA_TEST);
        GL11.glEnable(GL11.GL_TEXTURE_2D);
        GL11.glEnable(GL11.GL_DEPTH_TEST);
    }
}
