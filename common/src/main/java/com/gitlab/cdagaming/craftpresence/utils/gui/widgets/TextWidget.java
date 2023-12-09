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

package com.gitlab.cdagaming.craftpresence.utils.gui.widgets;

import com.gitlab.cdagaming.craftpresence.core.Constants;
import com.gitlab.cdagaming.craftpresence.core.utils.StringUtils;
import com.gitlab.cdagaming.craftpresence.utils.gui.RenderUtils;
import com.gitlab.cdagaming.craftpresence.utils.gui.controls.ExtendedTextControl;
import com.gitlab.cdagaming.craftpresence.utils.gui.integrations.ExtendedScreen;
import net.minecraft.client.gui.FontRenderer;

/**
 * Implementation for a Row-Style {@link ExtendedTextControl} Widget
 *
 * @author CDAGaming
 */
public class TextWidget extends ExtendedTextControl {
    /**
     * The message to display alongside the control
     */
    private String title;
    /**
     * The X coordinate for the additional message
     */
    private float titleX;
    /**
     * The X coordinate offset for the additional message
     */
    private float titleXOffset = 0;
    /**
     * The Y coordinate for the additional message
     */
    private float titleY;
    /**
     * The Y coordinate offset for the additional message
     */
    private float titleYOffset = 0;
    /**
     * Event to Deploy when this Control is Hovered Over, if any
     */
    private Runnable onHoverEvent = null;

    /**
     * Initialization Event for this Control, assigning defined arguments
     *
     * @param componentId     The ID for the control to Identify as
     * @param fontRendererObj The Font Renderer Instance
     * @param y               The Starting Y Position for this Control
     * @param widthIn         The Width for this Control
     * @param heightIn        The Height for this Control
     * @param title           The text to be rendered with this widget
     * @param onHoverEvent    The Hover Event to Occur when this control is hovered over
     */
    public TextWidget(int componentId, FontRenderer fontRendererObj, int y, int widthIn, int heightIn, String title, Runnable onHoverEvent) {
        super(componentId, fontRendererObj, 0, y, widthIn, heightIn);
        this.title = title;
        setOnHover(onHoverEvent);
    }

    /**
     * Initialization Event for this Control, assigning defined arguments
     *
     * @param componentId     The ID for the control to Identify as
     * @param fontRendererObj The Font Renderer Instance
     * @param y               The Starting Y Position for this Control
     * @param widthIn         The Width for this Control
     * @param heightIn        The Height for this Control
     * @param title           The text to be rendered with this widget
     */
    public TextWidget(int componentId, FontRenderer fontRendererObj, int y, int widthIn, int heightIn, String title) {
        this(componentId, fontRendererObj, y, widthIn, heightIn, title, null);
    }

    /**
     * Initialization Event for this Control, assigning defined arguments
     *
     * @param fontRendererObj The Font Renderer Instance
     * @param y               The Starting Y Position for this Control
     * @param widthIn         The Width for this Control
     * @param heightIn        The Height for this Control
     * @param title           The text to be rendered with this widget
     * @param onHoverEvent    The Hover Event to Occur when this control is hovered over
     */
    public TextWidget(FontRenderer fontRendererObj, int y, int widthIn, int heightIn, String title, Runnable onHoverEvent) {
        super(fontRendererObj, 0, y, widthIn, heightIn);
        this.title = title;
        setOnHover(onHoverEvent);
    }

    /**
     * Initialization Event for this Control, assigning defined arguments
     *
     * @param fontRendererObj The Font Renderer Instance
     * @param y               The Starting Y Position for this Control
     * @param widthIn         The Width for this Control
     * @param heightIn        The Height for this Control
     * @param title           The text to be rendered with this widget
     */
    public TextWidget(FontRenderer fontRendererObj, int y, int widthIn, int heightIn, String title) {
        this(fontRendererObj, y, widthIn, heightIn, title, null);
    }

    /**
     * Initialization Event for this Control, assigning defined arguments
     *
     * @param fontRendererObj The Font Renderer Instance
     * @param y               The Starting Y Position for this Control
     * @param widthIn         The Width for this Control
     * @param heightIn        The Height for this Control
     * @param keyEvent        The event to run when characters are typed in this control
     * @param title           The text to be rendered with this widget
     * @param onHoverEvent    The Hover Event to Occur when this control is hovered over
     */
    public TextWidget(FontRenderer fontRendererObj, int y, int widthIn, int heightIn, Runnable keyEvent, String title, Runnable onHoverEvent) {
        super(fontRendererObj, 0, y, widthIn, heightIn, keyEvent);
        this.title = title;
        setOnHover(onHoverEvent);
    }

    /**
     * Initialization Event for this Control, assigning defined arguments
     *
     * @param fontRendererObj The Font Renderer Instance
     * @param y               The Starting Y Position for this Control
     * @param widthIn         The Width for this Control
     * @param heightIn        The Height for this Control
     * @param keyEvent        The event to run when characters are typed in this control
     * @param title           The text to be rendered with this widget
     */
    public TextWidget(FontRenderer fontRendererObj, int y, int widthIn, int heightIn, Runnable keyEvent, String title) {
        this(fontRendererObj, y, widthIn, heightIn, keyEvent, title, null);
    }

    /**
     * Retrieve the text to be displayed alongside this control
     *
     * @return the current attached message
     */
    public String getTitle() {
        return title;
    }

    /**
     * Set the text to be displayed alongside this control
     *
     * @param title The new attached message
     * @return the modified instance
     */
    public TextWidget setTitle(String title) {
        this.title = title;
        return this;
    }

    /**
     * Retrieve the X coordinate for the additional message
     *
     * @return The attached message's X coordinate
     */
    public float getTitleX() {
        return titleX + getTitleXOffset();
    }

    /**
     * Set the X coordinate for the additional message
     *
     * @param titleX The new X coordinate for the attached message
     * @return the modified instance
     */
    public TextWidget setTitleX(float titleX) {
        this.titleX = titleX;
        return this;
    }

    /**
     * Retrieve the X coordinate offset for the additional message
     *
     * @return The attached message's X coordinate offset
     */
    public float getTitleXOffset() {
        return titleXOffset;
    }

    /**
     * Set the X coordinate offset for the additional message
     *
     * @param offset The new X coordinate offset for the attached message
     * @return the modified instance
     */
    public TextWidget setTitleXOffset(float offset) {
        this.titleXOffset = offset;
        return this;
    }

    /**
     * Retrieve the Y coordinate for the additional message
     *
     * @return The attached message's Y coordinate
     */
    public float getTitleY() {
        return titleY + getTitleYOffset();
    }

    /**
     * Set the Y coordinate for the additional message
     *
     * @param titleY The new Y coordinate for the attached message
     * @return the modified instance
     */
    public TextWidget setTitleY(float titleY) {
        this.titleY = titleY;
        return this;
    }

    /**
     * Retrieve the Y coordinate offset for the additional message
     *
     * @return The attached message's Y coordinate offset
     */
    public float getTitleYOffset() {
        return titleYOffset;
    }

    /**
     * Set the Y coordinate offset for the additional message
     *
     * @param offset The new Y coordinate offset for the attached message
     * @return the modified instance
     */
    public TextWidget setTitleYOffset(float offset) {
        this.titleYOffset = offset;
        return this;
    }

    /**
     * Sets the Event to occur upon Mouse Over
     *
     * @param event The event to occur
     */
    public void setOnHover(final Runnable event) {
        onHoverEvent = event;
    }

    /**
     * Triggers the onHover event to occur
     */
    public void onHover() {
        if (onHoverEvent != null) {
            onHoverEvent.run();
        }
    }

    @Override
    public void preDraw(ExtendedScreen screen) {
        // Ensure correct positioning
        final int calc1 = (screen.getScreenWidth() / 2) - (getControlWidth() - 3); // Left; Title Text
        final int calc2 = (screen.getScreenWidth() / 2) + 3; // Left; Textbox
        setControlPosX(calc2);

        if (!StringUtils.isNullOrEmpty(title)) {
            setTitleX((calc1 + getControlWidth()) - (getControlWidth() / 2f));
            setTitleY(getBottom() - (getControlHeight() / 2f) - (screen.getFontHeight() / 2f));
        }
    }

    @Override
    public void draw(ExtendedScreen screen) {
        if (!StringUtils.isNullOrEmpty(title)) {
            final String mainTitle = Constants.TRANSLATOR.getLocalizedMessage(title);
            screen.renderCenteredString(mainTitle, getTitleX(), getTitleY(), 0xFFFFFF);
        }
    }

    @Override
    public void postDraw(ExtendedScreen screen) {
        if (!StringUtils.isNullOrEmpty(title)) {
            final String mainTitle = Constants.TRANSLATOR.getLocalizedMessage(title);
            final int titleWidth = screen.getStringWidth(mainTitle);
            if (screen.isOverScreen() && RenderUtils.isMouseOver(
                    screen.getMouseX(), screen.getMouseY(),
                    getTitleX() - (titleWidth / 2f), getTitleY(),
                    titleWidth, screen.getFontHeight()
            )) {
                onHover();
            }
        }
    }
}
