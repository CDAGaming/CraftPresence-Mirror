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

package com.gitlab.cdagaming.craftpresence.utils.gui.controls;

import com.gitlab.cdagaming.craftpresence.utils.gui.RenderUtils;
import com.gitlab.cdagaming.craftpresence.utils.gui.integrations.ExtendedScreen;
import net.minecraft.client.Minecraft;

import javax.annotation.Nonnull;

/**
 * Gui Widget for a Clickable Checkbox-Style Button
 *
 * @author CDAGaming
 */
public class CheckBoxControl extends ExtendedButtonControl {
    /**
     * The default border width for this control
     */
    private static final int DEFAULT_BORDER = 1;
    /**
     * The default inner box width for this control
     */
    private static final int DEFAULT_BOX_WIDTH = 11;

    /**
     * The width of the inner box of this control
     */
    private int boxWidth;
    /**
     * The current border width for this control
     */
    private int borderWidth;
    /**
     * The Check state of this control
     */
    private boolean is_Checked;

    /**
     * Initialization Event for this Control, assigning defined arguments
     *
     * @param id            The ID for the control to Identify as
     * @param xPos          The Starting X Position for this Control
     * @param yPos          The Starting Y Position for this Control
     * @param displayString The display text, to display within this control
     * @param isChecked     The beginning check state for this Control
     */
    public CheckBoxControl(final int id, final int xPos, final int yPos, final String displayString, final boolean isChecked) {
        super(id, xPos, yPos, displayString);
        resetRenderStates();
        setIsChecked(isChecked);
    }

    /**
     * Initialization Event for this Control, assigning defined arguments
     *
     * @param id            The ID for the control to Identify as
     * @param xPos          The Starting X Position for this Control
     * @param yPos          The Starting Y Position for this Control
     * @param displayString The display text, to display within this control
     * @param isChecked     The beginning check state for this Control
     * @param onPushEvent   The Click Event to Occur when this control is clicked
     */
    public CheckBoxControl(final int id, final int xPos, final int yPos, final String displayString, final boolean isChecked, final Runnable onPushEvent) {
        this(id, xPos, yPos, displayString, isChecked);
        setOnClick(onPushEvent);
    }

    /**
     * Initialization Event for this Control, assigning defined arguments
     *
     * @param id            The ID for the control to Identify as
     * @param xPos          The Starting X Position for this Control
     * @param yPos          The Starting Y Position for this Control
     * @param displayString The display text, to display within this control
     * @param isChecked     The beginning check state for this Control
     * @param onPushEvent   The Click Event to Occur when this control is clicked
     * @param onHoverEvent  The Hover Event to Occur when this control is hovered over
     */
    public CheckBoxControl(final int id, final int xPos, final int yPos, final String displayString, final boolean isChecked, final Runnable onPushEvent, final Runnable onHoverEvent) {
        this(id, xPos, yPos, displayString, isChecked, onPushEvent);
        setOnHover(onHoverEvent);
    }

    /**
     * Initialization Event for this Control, assigning defined arguments
     *
     * @param xPos          The Starting X Position for this Control
     * @param yPos          The Starting Y Position for this Control
     * @param displayString The display text, to display within this control
     * @param isChecked     The beginning check state for this Control
     */
    public CheckBoxControl(final int xPos, final int yPos, final String displayString, final boolean isChecked) {
        this(ExtendedScreen.getNextIndex(), xPos, yPos, displayString, isChecked);
    }

    /**
     * Initialization Event for this Control, assigning defined arguments
     *
     * @param xPos          The Starting X Position for this Control
     * @param yPos          The Starting Y Position for this Control
     * @param displayString The display text, to display within this control
     * @param isChecked     The beginning check state for this Control
     * @param onPushEvent   The Click Event to Occur when this control is clicked
     */
    public CheckBoxControl(final int xPos, final int yPos, final String displayString, final boolean isChecked, final Runnable onPushEvent) {
        this(xPos, yPos, displayString, isChecked);
        setOnClick(onPushEvent);
    }

    /**
     * Initialization Event for this Control, assigning defined arguments
     *
     * @param xPos          The Starting X Position for this Control
     * @param yPos          The Starting Y Position for this Control
     * @param displayString The display text, to display within this control
     * @param isChecked     The beginning check state for this Control
     * @param onPushEvent   The Click Event to Occur when this control is clicked
     * @param onHoverEvent  The Hover Event to Occur when this control is hovered over
     */
    public CheckBoxControl(final int xPos, final int yPos, final String displayString, final boolean isChecked, final Runnable onPushEvent, final Runnable onHoverEvent) {
        this(xPos, yPos, displayString, isChecked, onPushEvent);
        setOnHover(onHoverEvent);
    }

    /**
     * Draws this button to the screen.
     */
    @Override
    public void drawButton(@Nonnull Minecraft mc, int mouseX, int mouseY, float partial) {
        setCurrentFontRender(mc.fontRenderer);
        if (isControlVisible()) {
            setHoveringOver(isOverScreen() && RenderUtils.isMouseOver(mouseX, mouseY, this));

            mouseDragged(mc, mouseX, mouseY);
            int color = !isControlEnabled() ? 10526880 : 14737632;

            if (isChecked())
                RenderUtils.renderCenteredString(getFontRenderer(), "x", getControlPosX() + getBoxWidth() / 2f + 1f, getControlPosY() + 1f, color);

            RenderUtils.renderString(getFontRenderer(), getDisplayMessage(), getControlPosX() + getBoxWidth() + 2f, getControlPosY() + 2f, color);
        }
    }

    /**
     * Fired when the mouse button is dragged.<p>
     * Equivalent of MouseListener.mouseDragged(MouseEvent e).
     */
    @Override
    protected void mouseDragged(@Nonnull Minecraft mc, int mouseX, int mouseY) {
        if (isControlVisible()) {
            final int hoverState = getHoverState(isHoveringOrFocusingOver());

            final String borderColor = hoverState == 2 ? "#FFFFFF" : "#000000";
            final String contentColor = "#2b2b2b";

            RenderUtils.drawGradientBox(
                    getControlPosX(), getControlPosY(),
                    getBoxWidth(), getControlHeight(),
                    getZLevel(),
                    borderColor, borderColor, getBorderWidth(),
                    contentColor, contentColor
            );
        }
    }

    @Override
    public void onClick() {
        setIsChecked(!is_Checked);
        super.onClick();
    }

    @Override
    public void setControlMessage(String newMessage) {
        super.setControlMessage(newMessage);
        syncRenderStates();
    }

    /**
     * Returns this Control's Check State
     *
     * @return The Current Check State of this control
     */
    public boolean isChecked() {
        return is_Checked;
    }

    /**
     * Sets this Control's Check State
     *
     * @param isChecked the new check state of this control
     */
    public void setIsChecked(final boolean isChecked) {
        this.is_Checked = isChecked;
    }

    /**
     * Retrieve the width of the inner box of this control
     *
     * @return the inner box width
     */
    public int getBoxWidth() {
        return boxWidth;
    }

    /**
     * Sets the width of the inner box of this control
     *
     * @param boxWidth the new inner box width
     */
    public void setBoxWidth(final int boxWidth) {
        this.boxWidth = boxWidth;
        syncRenderStates();
    }

    /**
     * Retrieve the current border width for this control
     *
     * @return the current border width
     */
    public int getBorderWidth() {
        return borderWidth;
    }

    /**
     * Sets the current border width for this control
     *
     * @param borderWidth the new border width
     */
    public void setBorderWidth(final int borderWidth) {
        this.borderWidth = borderWidth;
        syncRenderStates();
    }

    private void syncRenderStates() {
        setControlHeight(getBoxWidth());
        setControlWidth(getBoxWidth() + (getBorderWidth() * 2) + RenderUtils.getStringWidth(getFontRenderer(), getDisplayMessage()));
    }

    private void resetRenderStates() {
        setBoxWidth(DEFAULT_BOX_WIDTH);
        setBorderWidth(DEFAULT_BORDER);
    }
}
