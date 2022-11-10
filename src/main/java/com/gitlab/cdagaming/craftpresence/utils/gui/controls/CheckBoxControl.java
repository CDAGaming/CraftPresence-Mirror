/*
 * MIT License
 *
 * Copyright (c) 2018 - 2022 CDAGaming (cstack2011@yahoo.com)
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

import com.gitlab.cdagaming.craftpresence.CraftPresence;
import com.gitlab.cdagaming.craftpresence.impl.Pair;
import net.minecraft.client.Minecraft;

import javax.annotation.Nonnull;

/**
 * Gui Widget for a Clickable Checkbox-Style Button
 *
 * @author CDAGaming
 */
public class CheckBoxControl extends ExtendedButtonControl {
    /**
     * The width of the inner box of this control
     */
    public int boxWidth;

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
    public CheckBoxControl(int id, int xPos, int yPos, String displayString, boolean isChecked) {
        super(id, xPos, yPos, displayString);
        is_Checked = isChecked;
        boxWidth = 11;
        height = 11;
        width = boxWidth + 2 + getFontRenderer().getStringWidth(getDisplayMessage());
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
    public CheckBoxControl(int id, int xPos, int yPos, String displayString, boolean isChecked, Runnable onPushEvent) {
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
     * @param onHoverEvent  The Hover Event to Occur when this control is clicked
     */
    public CheckBoxControl(int id, int xPos, int yPos, String displayString, boolean isChecked, Runnable onPushEvent, Runnable onHoverEvent) {
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
    public CheckBoxControl(int xPos, int yPos, String displayString, boolean isChecked) {
        this(CraftPresence.GUIS.getNextIndex(), xPos, yPos, displayString, isChecked);
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
    public CheckBoxControl(int xPos, int yPos, String displayString, boolean isChecked, Runnable onPushEvent) {
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
     * @param onHoverEvent  The Hover Event to Occur when this control is clicked
     */
    public CheckBoxControl(int xPos, int yPos, String displayString, boolean isChecked, Runnable onPushEvent, Runnable onHoverEvent) {
        this(xPos, yPos, displayString, isChecked, onPushEvent);
        setOnHover(onHoverEvent);
    }

    /**
     * Draws this button to the screen.
     */
    @Override
    public void drawButton(@Nonnull Minecraft mc, int mouseX, int mouseY) {
        setCurrentFontRender(mc.fontRendererObj);
        if (visible) {
            hovered = CraftPresence.GUIS.isMouseOver(mouseX, mouseY, this);
            CraftPresence.GUIS.drawContinuousTexturedBox(new Pair<>(getControlPosX(), getControlPosY()), new Pair<>(0, 46), new Pair<>(boxWidth, getControlHeight()), new Pair<>(200, 20), new Pair<>(2, 3), new Pair<>(2, 2), zLevel, buttonTextures);
            mouseDragged(mc, mouseX, mouseY);
            int color = !isControlEnabled() ? 10526880 : 14737632;

            if (is_Checked)
                drawCenteredString(getFontRenderer(), "x", getControlPosX() + boxWidth / 2 + 1, getControlPosY() + 1, 14737632);

            drawString(getFontRenderer(), getDisplayMessage(), getControlPosX() + boxWidth + 2, getControlPosY() + 2, color);
        }
    }

    /**
     * Returns true if the mouse has been pressed on this control.<p>
     * Equivalent of MouseListener.mousePressed(MouseEvent e).
     */
    @Override
    public boolean mousePressed(@Nonnull Minecraft mc, int mouseX, int mouseY) {
        if (isControlEnabled() && visible && hovered) {
            is_Checked = !is_Checked;
            return true;
        }
        return false;
    }

    /**
     * Returns this Control's Check State
     *
     * @return The Current Check State of this control
     */
    public boolean isChecked() {
        return is_Checked;
    }
}
