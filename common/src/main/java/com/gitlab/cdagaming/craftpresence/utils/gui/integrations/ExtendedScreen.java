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

package com.gitlab.cdagaming.craftpresence.utils.gui.integrations;

import com.gitlab.cdagaming.craftpresence.CraftPresence;
import com.gitlab.cdagaming.craftpresence.utils.CommandUtils;
import com.gitlab.cdagaming.craftpresence.utils.StringUtils;
import com.gitlab.cdagaming.craftpresence.utils.gui.GuiUtils;
import com.gitlab.cdagaming.craftpresence.utils.gui.controls.ExtendedButtonControl;
import com.gitlab.cdagaming.craftpresence.utils.gui.controls.ExtendedTextControl;
import com.gitlab.cdagaming.craftpresence.utils.gui.controls.ScrollableListControl;
import com.gitlab.cdagaming.craftpresence.utils.gui.widgets.DynamicWidget;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.FontRenderer;
import net.minecraft.client.gui.Gui;
import net.minecraft.client.gui.GuiButton;
import net.minecraft.client.gui.GuiScreen;
import org.lwjgl.input.Keyboard;
import org.lwjgl.input.Mouse;

import javax.annotation.Nonnull;
import java.util.List;

/**
 * An Extended and Globalized Gui Screen
 *
 * @author CDAGaming
 */
public class ExtendedScreen extends GuiScreen {
    /**
     * The Parent or Past Screen
     */
    public final GuiScreen parentScreen;
    /**
     * The Current Screen Instance
     */
    public final GuiScreen currentScreen;
    /**
     * Similar to buttonList, a list of compatible controls in this Screen
     */
    protected final List<Gui> extendedControls = StringUtils.newArrayList();
    /**
     * Similar to buttonList, a list of compatible widgets in this Screen
     */
    protected final List<DynamicWidget> extendedWidgets = StringUtils.newArrayList();
    /**
     * Similar to buttonList, a list of compatible ScrollLists in this Screen
     */
    protected final List<ScrollableListControl> extendedLists = StringUtils.newArrayList();
    /**
     * The Current Screen Phase, used to define where in the initialization it is at
     */
    private Phase currentPhase = Phase.PREINIT;
    /**
     * Whether to enable debug mode screen data, specified from screen developers
     */
    private boolean debugMode = false;
    /**
     * Whether to enable verbose mode screen data, specified from screen developers
     */
    private boolean verboseMode = false;

    /**
     * The Screen's Current X coordinate position
     */
    private int screenX = 0;
    /**
     * The Screen's Current Y coordinate position
     */
    private int screenY = 0;

    /**
     * The Last Ticked Mouse X Coordinate
     */
    private int lastMouseX = 0;
    /**
     * The Last Ticked Mouse Y Coordinate
     */
    private int lastMouseY = 0;
    /**
     * The Last Ticked Scroll Delta
     */
    private int lastMouseScroll = 0;
    /**
     * The Content Height of all applicable widgets
     */
    private int contentHeight;
    /**
     * Whether this Screen can be closed by normal means, true by default
     */
    private boolean canClose;
    /**
     * Whether the mouse is currently within screen bounds
     */
    private boolean isOverScreen;

    /**
     * Initialization Event for this Control, assigning defined arguments
     *
     * @param parentScreen The Parent Screen for this Instance
     */
    public ExtendedScreen(final GuiScreen parentScreen) {
        mc = CraftPresence.instance;
        currentScreen = this;
        this.parentScreen = parentScreen;
        this.contentHeight = 0;
        this.canClose = true;
        setDebugMode(CommandUtils.isDebugMode());
        setVerboseMode(CommandUtils.isVerboseMode());
    }

    /**
     * Initialization Event for this Control, assigning defined arguments
     */
    public ExtendedScreen() {
        this(null);
        this.canClose = false;
    }

    /**
     * Initialization Event for this Control, assigning defined arguments
     *
     * @param parentScreen The Parent Screen for this Instance
     * @param debugMode    Whether debug mode should be enabled for this screen
     */
    public ExtendedScreen(final GuiScreen parentScreen, final boolean debugMode) {
        this(parentScreen);
        setDebugMode(debugMode);
    }

    /**
     * Initialization Event for this Control, assigning defined arguments
     *
     * @param debugMode Whether debug mode should be enabled for this screen
     */
    public ExtendedScreen(final boolean debugMode) {
        this();
        setDebugMode(debugMode);
    }

    /**
     * Initialization Event for this Control, assigning defined arguments
     *
     * @param parentScreen The Parent Screen for this Instance
     * @param debugMode    Whether debug mode should be enabled for this screen
     * @param verboseMode  Whether verbose mode should be enabled for this screen
     */
    public ExtendedScreen(final GuiScreen parentScreen, final boolean debugMode, final boolean verboseMode) {
        this(parentScreen, debugMode);
        setVerboseMode(verboseMode);
    }

    /**
     * Initialization Event for this Control, assigning defined arguments
     *
     * @param debugMode   Whether debug mode should be enabled for this screen
     * @param verboseMode Whether verbose mode should be enabled for this screen
     */
    public ExtendedScreen(final boolean debugMode, final boolean verboseMode) {
        this(debugMode);
        setVerboseMode(verboseMode);
    }

    /**
     * Pre-Initializes this Screen
     * <p>
     * Responsible for Setting preliminary data
     */
    @Override
    public void initGui() {
        // Clear Data before Initialization
        super.initGui();
        clearData();
        Keyboard.enableRepeatEvents(true);

        currentPhase = Phase.INIT;
        initializeUi();
        currentPhase = Phase.READY;
    }

    /**
     * Clear the Screen Data
     */
    public void clearData() {
        if (currentPhase != Phase.PREINIT) {
            currentPhase = Phase.PREINIT;
            contentHeight = 0;

            buttonList.clear();
            extendedControls.clear();
            extendedWidgets.clear();
            extendedLists.clear();
        }
    }

    /**
     * Initializes this Screen
     * <p>
     * Responsible for setting initial Data and creating controls
     */
    public void initializeUi() {
        if (currentPhase == Phase.PREINIT) {
            initGui();
            return;
        }
        resetMouseScroll();

        for (Gui extendedControl : extendedControls) {
            if (extendedControl instanceof ExtendedScreen) {
                ((ExtendedScreen) extendedControl).initializeUi();
            }
        }
    }

    /**
     * Event to trigger upon Window Resize
     *
     * @param mcIn The Minecraft Instance
     * @param w    The New Screen Width
     * @param h    The New Screen Height
     */
    @Override
    public void onResize(@Nonnull Minecraft mcIn, int w, int h) {
        for (Gui extendedControl : extendedControls) {
            if (extendedControl instanceof ExtendedScreen) {
                ((ExtendedScreen) extendedControl).onResize(mcIn, w, h);
            }
        }
        super.onResize(mcIn, w, h);
    }

    /**
     * Adds a Compatible Button to this Screen with specified type
     *
     * @param buttonIn The Button to add to this Screen
     * @param <T>      The Button's Class Type
     * @return The added button with attached class type
     */
    @Nonnull
    @Override
    protected <T extends GuiButton> T addButton(@Nonnull T buttonIn) {
        return addControl(buttonIn);
    }

    /**
     * Adds a Compatible Control to this Screen with specified type
     *
     * @param buttonIn The Control to add to this Screen
     * @param <T>      The Control's Class Type
     * @return The added control with attached class type
     */
    @Nonnull
    public <T extends Gui> T addControl(@Nonnull T buttonIn) {
        if (buttonIn instanceof GuiButton && !buttonList.contains(buttonIn)) {
            buttonList.add((GuiButton) buttonIn);
        }
        if (!extendedControls.contains(buttonIn)) {
            extendedControls.add(buttonIn);
        }
        if (buttonIn instanceof DynamicWidget && !extendedWidgets.contains(buttonIn)) {
            addWidget((DynamicWidget) buttonIn);
        }
        return buttonIn;
    }

    /**
     * Adds a Compatible Scroll List to this Screen with specified type
     *
     * @param buttonIn The Scroll List to add to this Screen
     * @param <T>      The Scroll List's Class Type
     * @return The added scroll list with attached class type
     */
    @Nonnull
    public <T extends ScrollableListControl> T addList(@Nonnull T buttonIn) {
        if (!extendedLists.contains(buttonIn)) {
            extendedLists.add(buttonIn);
        }
        return buttonIn;
    }

    /**
     * Adds a Compatible Control to this Screen with specified type
     *
     * @param buttonIn The Control to add to this Screen
     * @param <T>      The Control's Class Type
     * @return The added control with attached class type
     */
    @Nonnull
    public <T extends DynamicWidget> T addWidget(@Nonnull T buttonIn) {
        if (!extendedWidgets.contains(buttonIn)) {
            extendedWidgets.add(buttonIn);
            getContentHeight();
        }
        return buttonIn;
    }

    /**
     * Pre-Preliminary Render Event, executes before preRender
     * <p>
     * Primarily used for rendering critical elements before other elements
     */
    public void renderCriticalData() {
        CraftPresence.GUIS.drawBackground(
                getScreenX(), getScreenY(),
                getScreenWidth(), getScreenHeight()
        );
    }

    /**
     * Preliminary Render Event, executes after renderCriticalData and before postRender
     * <p>
     * Primarily used for rendering title data and preliminary elements
     */
    public void preRender() {
        for (DynamicWidget widget : extendedWidgets) {
            widget.draw(this);
        }
    }

    /**
     * Post-Render event, executes after super event and preRender
     * <p>
     * Primarily used for rendering hover data
     */
    public void postRender() {
        // N/A
    }

    /**
     * Renders this Screen, including controls and post-Hover Events
     *
     * @param mouseX       The Event Mouse X Coordinate
     * @param mouseY       The Event Mouse Y Coordinate
     * @param partialTicks The Rendering Tick Rate
     */
    @Override
    public void drawScreen(int mouseX, int mouseY, float partialTicks) {
        // Ensures initialization events have run first, preventing an NPE
        if (isLoaded()) {
            final int scale = CraftPresence.GUIS.computeGuiScale(mc);
            CraftPresence.GUIS.drawWithin(
                    getLeft() * scale,
                    mc.displayHeight - getBottom() * scale,
                    getScreenWidth() * scale,
                    getScreenHeight() * scale
            );

            renderCriticalData();
            preRender();

            for (ScrollableListControl listControl : extendedLists) {
                if (listControl.getEnabled()) {
                    listControl.drawScreen(mouseX, mouseY, partialTicks);
                }
            }

            for (Gui extendedControl : extendedControls) {
                if (extendedControl instanceof ExtendedTextControl) {
                    final ExtendedTextControl textField = (ExtendedTextControl) extendedControl;
                    textField.drawTextBox();
                }
            }

            super.drawScreen(mouseX, mouseY, partialTicks);

            CraftPresence.GUIS.drawAnywhere();

            lastMouseX = mouseX;
            lastMouseY = mouseY;
            isOverScreen = CraftPresence.GUIS.isMouseOver(mouseX, mouseY, this);

            for (Gui extendedControl : extendedControls) {
                if (extendedControl instanceof ExtendedButtonControl) {
                    final ExtendedButtonControl extendedButton = (ExtendedButtonControl) extendedControl;
                    if (isOverScreen() && CraftPresence.GUIS.isMouseOver(mouseX, mouseY, extendedButton)) {
                        extendedButton.onHover();
                    }
                }
                if (extendedControl instanceof ExtendedScreen) {
                    ((ExtendedScreen) extendedControl).drawScreen(mouseX, mouseY, partialTicks);
                }
            }

            postRender();
        }
    }

    /**
     * Event to trigger upon Mouse Input
     */
    @Override
    public void handleMouseInput() {
        if (isLoaded()) {
            setMouseScroll(Mouse.getEventDWheel());
            for (ScrollableListControl listControl : extendedLists) {
                listControl.handleMouseInput();
            }
            for (Gui extendedControl : extendedControls) {
                if (extendedControl instanceof ExtendedScreen) {
                    ((ExtendedScreen) extendedControl).handleMouseInput();
                }
            }
            super.handleMouseInput();
        }
    }

    /**
     * Event to trigger upon Button Action, including onClick Events
     *
     * @param button The Button to trigger upon
     */
    @Override
    protected void actionPerformed(@Nonnull GuiButton button) {
        if (isOverScreen()) {
            if (button instanceof ExtendedButtonControl) {
                ((ExtendedButtonControl) button).onClick();
            }
            super.actionPerformed(button);
        }
    }

    /**
     * Event to trigger upon Typing a Key
     *
     * @param typedChar The typed Character, if any
     * @param keyCode   The KeyCode entered, if any
     */
    @Override
    protected void keyTyped(char typedChar, int keyCode) {
        if (isLoaded()) {
            if (keyCode == Keyboard.KEY_ESCAPE && canClose) {
                CraftPresence.GUIS.openScreen(parentScreen);
                return;
            }

            for (Gui extendedControl : extendedControls) {
                if (extendedControl instanceof ExtendedTextControl) {
                    final ExtendedTextControl textField = (ExtendedTextControl) extendedControl;
                    textField.textboxKeyTyped(typedChar, keyCode);
                }
                if (extendedControl instanceof ExtendedScreen) {
                    ((ExtendedScreen) extendedControl).keyTyped(typedChar, keyCode);
                }
            }
        }
    }

    /**
     * Event to trigger upon the mouse being clicked
     *
     * @param mouseX      The Event Mouse X Coordinate
     * @param mouseY      The Event Mouse Y Coordinate
     * @param mouseButton The Event Mouse Button Clicked
     */
    @Override
    protected void mouseClicked(int mouseX, int mouseY, int mouseButton) {
        if (isLoaded()) {
            for (Gui extendedControl : extendedControls) {
                if (extendedControl instanceof ExtendedTextControl) {
                    final ExtendedTextControl textField = (ExtendedTextControl) extendedControl;
                    textField.mouseClicked(mouseX, mouseY, mouseButton);
                }
                if (extendedControl instanceof ExtendedScreen) {
                    ((ExtendedScreen) extendedControl).mouseClicked(mouseX, mouseY, mouseButton);
                }
            }
            super.mouseClicked(mouseX, mouseY, mouseButton);
        }
    }

    /**
     * Event to trigger on each tick
     */
    @Override
    public void updateScreen() {
        if (isLoaded()) {
            for (Gui extendedControl : extendedControls) {
                if (extendedControl instanceof ExtendedTextControl) {
                    final ExtendedTextControl textField = (ExtendedTextControl) extendedControl;
                    textField.updateCursorCounter();
                }
                if (extendedControl instanceof ExtendedScreen) {
                    ((ExtendedScreen) extendedControl).updateScreen();
                }
            }
        }
    }

    /**
     * Event to trigger upon exiting the Gui
     */
    @Override
    public void onGuiClosed() {
        clearData();
        CraftPresence.GUIS.resetIndex();
        Keyboard.enableRepeatEvents(false);

        for (Gui extendedControl : extendedControls) {
            if (extendedControl instanceof ExtendedScreen) {
                ((ExtendedScreen) extendedControl).onGuiClosed();
            }
        }
    }

    /**
     * Renders a String in the Screen, in the style of a notice
     *
     * @param notice The List of Strings to render
     */
    public void renderNotice(final List<String> notice) {
        renderNotice(notice, 2, 3, false, false);
    }

    /**
     * Renders a String in the Screen, in the style of a notice
     *
     * @param notice      The List of Strings to render
     * @param widthScale  The Scale/Value away from the center X to render at
     * @param heightScale The Scale/Value away from the center Y to render at
     */
    public void renderNotice(final List<String> notice, final float widthScale, final float heightScale) {
        renderNotice(notice, widthScale, heightScale, false, false);
    }

    /**
     * Renders a String in the Screen, in the style of a notice
     *
     * @param notice       The List of Strings to render
     * @param widthScale   The Scale/Value away from the center X to render at
     * @param heightScale  The Scale/Value away from the center Y to render at
     * @param useXAsActual Whether to use the widthScale as the actual X value
     * @param useYAsActual Whether to use the heightScale as the actual Y value
     */
    public void renderNotice(final List<String> notice, final float widthScale, final float heightScale, final boolean useXAsActual, final boolean useYAsActual) {
        if (notice != null && !notice.isEmpty()) {
            for (int i = 0; i < notice.size(); i++) {
                final String string = notice.get(i);
                renderString(string, (useXAsActual ? widthScale : (getScreenWidth() / widthScale)) - (getStringWidth(string) / widthScale), (useYAsActual ? heightScale : (getScreenHeight() / heightScale)) + (i * 10), 0xFFFFFF);
            }
        }
    }

    /**
     * Renders a String in the Screen, in the style of normal text
     *
     * @param text  The text to render to the screen
     * @param xPos  The X position to render the text at
     * @param yPos  The Y position to render the text at
     * @param color The color to render the text in
     */
    public void renderString(final String text, final float xPos, final float yPos, final int color) {
        getFontRenderer().drawStringWithShadow(text, xPos, yPos, color);
    }

    /**
     * Get the Width of a String from the FontRenderer
     *
     * @param string The string to interpret
     * @return the string's width from the font renderer
     */
    public int getStringWidth(final String string) {
        return getFontRenderer().getStringWidth(string);
    }

    /**
     * Draws a Scrollable String, dependent on scroll parameters
     *
     * @param text      The text to render to the screen
     * @param xPos      The X position to render the text at
     * @param minScroll The minimum allowed scroll position, inclusive
     * @param scrollPos The current scroll position
     * @param textColor The color to render the text in
     */
    public void drawScrollString(final List<String> text, final int xPos, final int scrollPos, final int minScroll, final int textColor) {
        int currentY = minScroll - scrollPos;
        for (String line : text) {
            renderString(line, xPos, currentY, textColor);
            currentY += getFontHeight() + 1;
        }
    }

    /**
     * Draws a Scrollable String, dependent on scroll parameters
     *
     * @param text      The text to render to the screen
     * @param xPos      The X position to render the text at
     * @param minScroll The minimum allowed scroll position, inclusive
     * @param scrollPos The current scroll position
     * @param wrapWidth The width to wrap the text to
     * @param textColor The color to render the text in
     */
    public void drawScrollString(final String text, final int xPos, final int minScroll, final int scrollPos, final int wrapWidth, final int textColor) {
        drawScrollString(
                createRenderLines(text, wrapWidth),
                xPos, scrollPos, minScroll, textColor
        );
    }

    /**
     * Format a section of strings to conform to the specified width
     *
     * @param original  The text to interpret
     * @param wrapWidth The width to wrap the text to
     * @return the modified lines, if successfull
     */
    public List<String> createRenderLines(final List<String> original, final int wrapWidth) {
        final List<String> data = StringUtils.newArrayList();
        for (String line : original) {
            data.addAll(
                    GuiUtils.listFormattedStringToWidth(getFontRenderer(), line, wrapWidth)
            );
        }
        return data;
    }

    /**
     * Format a section of strings to conform to the specified width
     *
     * @param original  The text to interpret
     * @param wrapWidth The width to wrap the text to
     * @return the modified lines, if successfull
     */
    public List<String> createRenderLines(final String original, final int wrapWidth) {
        return createRenderLines(
                StringUtils.splitTextByNewLine(original),
                wrapWidth
        );
    }

    /**
     * Get the wrap width for elements to be wrapped by
     * <p>Mostly used as a helper method for wrapping String elements
     *
     * @return the wrap width for elements to be wrapped by
     */
    public int getWrapWidth() {
        return -1;
    }

    /**
     * Get the Current Mouse's X Coordinate Position
     *
     * @return The Mouse's X Coordinate Position
     */
    public int getMouseX() {
        return lastMouseX;
    }

    /**
     * Get the Current Mouse's Y Coordinate Position
     *
     * @return The Mouse's Y Coordinate Position
     */
    public int getMouseY() {
        return lastMouseY;
    }

    /**
     * Get the Current Screen's X Coordinate Position
     *
     * @return The Screen's X Coordinate Position
     */
    public int getScreenX() {
        return screenX;
    }

    /**
     * Sets the Current Screen's X Coordinate Position
     *
     * @param screenX the new X position for the screen
     */
    public void setScreenX(final int screenX) {
        this.screenX = screenX;
    }

    /**
     * Get the Current Screen's Y Coordinate Position
     *
     * @return The Screen's Y Coordinate Position
     */
    public int getScreenY() {
        return screenY;
    }

    /**
     * Sets the Current Screen's Y Coordinate Position
     *
     * @param screenY the new Y position for the screen
     */
    public void setScreenY(final int screenY) {
        this.screenY = screenY;
    }

    /**
     * Get whether the mouse is currently within screen bounds
     *
     * @return {@link Boolean#TRUE} is condition is satisfied
     */
    public boolean isOverScreen() {
        return isOverScreen;
    }

    /**
     * Get the Current Mouse's Scroll Delta
     *
     * @return The Mouse's Current Scroll Delta
     */
    public int getMouseScroll() {
        return lastMouseScroll;
    }

    /**
     * Sets the Current Mouse's Scroll Delta
     *
     * @param mouseScroll the new scroll delta
     */
    public void setMouseScroll(final int mouseScroll) {
        this.lastMouseScroll = mouseScroll;
    }

    /**
     * Resets the Current Mouse's Scroll Delta
     */
    public void resetMouseScroll() {
        setMouseScroll(0);
    }

    /**
     * Get the Maximum Viewable Screen Width
     *
     * @return the maximum view width of the screen
     */
    public int getMaxWidth() {
        return getScreenWidth();
    }

    /**
     * Get the Current Screen Width
     *
     * @return the width of the screen
     */
    public int getScreenWidth() {
        return width;
    }

    /**
     * Sets the Current Screen Width
     *
     * @param screenWidth the new screen width
     */
    public void setScreenWidth(final int screenWidth) {
        this.width = screenWidth;
    }

    /**
     * Retrieve (And refresh) the Content Height of all applicable widgets
     *
     * @return the Content Height
     */
    public int getContentHeight() {
        contentHeight = 0;
        for (DynamicWidget widget : extendedWidgets) {
            contentHeight += widget.getControlHeight();
        }
        return contentHeight;
    }

    /**
     * Get the Current Screen Height
     *
     * @return the height of the screen
     */
    public int getScreenHeight() {
        return height;
    }

    /**
     * Sets the Current Screen Height
     *
     * @param screenHeight the new screen height
     */
    public void setScreenHeight(final int screenHeight) {
        this.height = screenHeight;
    }

    /**
     * Get the Current Font Renderer for this Screen
     *
     * @return The Current Font Renderer for this Screen
     */
    public FontRenderer getFontRenderer() {
        return mc.fontRenderer != null ? mc.fontRenderer : GuiUtils.getDefaultFontRenderer();
    }

    /**
     * Get the Current Font Height for this Screen
     *
     * @return The Current Font Height for this Screen
     */
    public int getFontHeight() {
        return getFontRenderer().FONT_HEIGHT;
    }

    /**
     * Get the left-most coordinate for this screen
     *
     * @return The left-most coordinate for this screen
     */
    public int getLeft() {
        return getScreenX();
    }

    /**
     * Get the right-most coordinate for this screen
     *
     * @return The right-most coordinate for this screen
     */
    public int getRight() {
        return getScreenX() + getScreenWidth();
    }

    /**
     * Get the bottom-most coordinate for this screen
     *
     * @return The bottom-most coordinate for this screen
     */
    public int getBottom() {
        return getScreenY() + getScreenHeight();
    }

    /**
     * Get the top-most coordinate for this screen
     *
     * @return The top-most coordinate for this screen
     */
    public int getTop() {
        return getScreenY();
    }

    /**
     * Gets whether to display any Debug display data for this screen
     *
     * @return Whether to display any Debug display data for this screen
     */
    public boolean isDebugMode() {
        return debugMode;
    }

    /**
     * Sets whether to display any Debug display data for this screen
     *
     * @param isDebugMode Whether to display any Debug display data for this screen
     */
    public void setDebugMode(final boolean isDebugMode) {
        this.debugMode = isDebugMode;
    }

    /**
     * Gets whether to display any Verbose display data for this screen
     *
     * @return Whether to display any Verbose display data for this screen
     */
    public boolean isVerboseMode() {
        return verboseMode;
    }

    /**
     * Sets whether to display any Verbose display data for this screen
     *
     * @param isVerboseMode Whether to display any Verbose display data for this screen
     */
    public void setVerboseMode(final boolean isVerboseMode) {
        this.verboseMode = isVerboseMode;
    }

    /**
     * Gets whether the Screen is fully loaded
     *
     * @return {@link Boolean#TRUE} if condition is satisfied
     */
    public boolean isLoaded() {
        return currentPhase == Phase.READY;
    }

    /**
     * Constants representing various Screen Phase statuses
     */
    public enum Phase {
        /**
         * Defines that the Screen is either unloaded, or has not finished pre-initialization
         */
        PREINIT,
        /**
         * Defines that the Screen has completed initial construction and data preparation
         */
        INIT,
        /**
         * Defines that the Screen is fully ready for use
         */
        READY
    }
}
