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

package io.github.cdagaming.unilib.utils.gui.controls;

import io.github.cdagaming.unicore.utils.StringUtils;
import io.github.cdagaming.unilib.utils.gui.integrations.ExtendedScreen;
import io.github.cdagaming.unilib.utils.gui.widgets.DynamicWidget;
import net.minecraft.client.gui.FontRenderer;
import net.minecraft.client.gui.GuiTextField;

/**
 * Extended Gui Widget for a Text Field
 *
 * @author CDAGaming
 */
public class ExtendedTextControl extends GuiTextField implements DynamicWidget {
    /**
     * The default character limit for all controls of this type
     */
    private static final int DEFAULT_TEXT_LIMIT = 2048;
    /**
     * The event to occur when a key event occurs
     */
    private Runnable onKeyEvent;

    /**
     * Initialization Event for this Control, assigning defined arguments
     *
     * @param componentId     The ID for the control to Identify as
     * @param fontRendererObj The Font Renderer Instance
     * @param x               The Starting X Position for this Control
     * @param y               The Starting Y Position for this Control
     * @param widthIn         The Width for this Control
     * @param heightIn        The Height for this Control
     */
    public ExtendedTextControl(final int componentId, final FontRenderer fontRendererObj, final int x, final int y, final int widthIn, final int heightIn) {
        super(componentId, fontRendererObj, x, y, widthIn, heightIn);
        setControlMaxLength(DEFAULT_TEXT_LIMIT);
    }

    /**
     * Initialization Event for this Control, assigning defined arguments
     *
     * @param fontRendererObj The Font Renderer Instance
     * @param x               The Starting X Position for this Control
     * @param y               The Starting Y Position for this Control
     * @param widthIn         The Width for this Control
     * @param heightIn        The Height for this Control
     */
    public ExtendedTextControl(final FontRenderer fontRendererObj, final int x, final int y, final int widthIn, final int heightIn) {
        this(ExtendedScreen.getNextIndex(), fontRendererObj, x, y, widthIn, heightIn);
    }

    /**
     * Initialization Event for this Control, assigning defined arguments
     *
     * @param fontRendererObj The Font Renderer Instance
     * @param x               The Starting X Position for this Control
     * @param y               The Starting Y Position for this Control
     * @param widthIn         The Width for this Control
     * @param heightIn        The Height for this Control
     * @param keyEvent        The event to run when characters are typed in this control
     */
    public ExtendedTextControl(final FontRenderer fontRendererObj, final int x, final int y, final int widthIn, final int heightIn, final Runnable keyEvent) {
        this(fontRendererObj, x, y, widthIn, heightIn);
        setOnKeyTyped(keyEvent);
    }

    @Override
    public int getControlWidth() {
        return width;
    }

    @Override
    public void setControlWidth(final int width) {
        this.width = width;
    }

    @Override
    public int getControlHeight() {
        return height;
    }

    @Override
    public void setControlHeight(final int height) {
        this.height = height;
    }

    @Override
    public void preDraw(ExtendedScreen screen) {
        // N/A
    }

    @Override
    public void draw(ExtendedScreen screen) {
        // N/A
    }

    @Override
    public void postDraw(ExtendedScreen screen) {
        // N/A
    }

    @Override
    public int getControlPosX() {
        return this.x;
    }

    @Override
    public void setControlPosX(final int posX) {
        this.x = posX;
    }

    @Override
    public int getControlPosY() {
        return this.y;
    }

    @Override
    public void setControlPosY(final int posY) {
        this.y = posY;
    }

    /**
     * Gets the control's current text contents
     *
     * @return The control's current text contents
     */
    public String getControlMessage() {
        return this.getText();
    }

    /**
     * Sets the control's display message to the specified value
     *
     * @param newMessage The new display message for this control
     */
    public void setControlMessage(final String newMessage) {
        this.setText(StringUtils.getOrDefault(newMessage));
    }

    /**
     * Gets the control's maximum text length
     *
     * @return The control's maximum text contents
     */
    public int getControlMaxLength() {
        return this.getMaxStringLength();
    }

    /**
     * Sets the control's message maximum length to the specified value
     *
     * @param newLength The new maximum length for this control's message
     */
    public void setControlMaxLength(final int newLength) {
        this.setMaxStringLength(newLength);
    }

    /**
     * Gets whether the control is currently being focused upon
     *
     * @return The control's focus status
     */
    public boolean isControlFocused() {
        return this.isFocused();
    }

    /**
     * Sets whether the control should be focused upon
     *
     * @param focused the new focus state for the control
     */
    public void setControlFocused(final boolean focused) {
        this.setFocused(focused);
    }

    /**
     * Sets the Event to occur upon typing keys
     *
     * @param event The event to occur
     */
    public void setOnKeyTyped(final Runnable event) {
        onKeyEvent = event;
    }

    /**
     * Triggers the onKey event to occur
     */
    public void onKeyTyped() {
        if (onKeyEvent != null) {
            onKeyEvent.run();
        }
    }

    /**
     * The event to occur when a character is typed within this control
     *
     * @param typedChar The typed character, if any
     * @param keyCode   The keycode, if any
     * @return Whether the event completed successfully
     */
    @Override
    public boolean textboxKeyTyped(char typedChar, int keyCode) {
        final boolean returnValue = super.textboxKeyTyped(typedChar, keyCode);
        if (returnValue) {
            onKeyTyped();
        }

        return returnValue;
    }
}
