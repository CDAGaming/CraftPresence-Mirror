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

package com.gitlab.cdagaming.unilib.utils.gui.controls;

import com.gitlab.cdagaming.unilib.utils.gui.RenderUtils;
import com.gitlab.cdagaming.unilib.utils.gui.integrations.ExtendedScreen;
import io.github.cdagaming.unicore.utils.StringUtils;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.FontRenderer;
import net.minecraft.client.gui.GuiSlot;

import javax.annotation.Nonnull;
import java.util.List;

/**
 * Gui Widget for a Scrollable List
 *
 * @author CDAGaming
 */
@SuppressWarnings("DuplicatedCode")
public class ScrollableListControl extends GuiSlot {
    /**
     * The default slot height
     */
    public static final int DEFAULT_SLOT_HEIGHT = 18;
    /**
     * The Currently Selected Value in the List
     */
    public String currentValue;
    /**
     * The Items available to select within the List Gui
     */
    public List<String> itemList;
    /**
     * The current screen instance
     */
    public ExtendedScreen currentScreen;

    /**
     * Initialization Event for this Control, assigning defined arguments
     *
     * @param mc            The Minecraft Instance for this Control
     * @param currentScreen The current screen instance for this control
     * @param width         The Width of this Control
     * @param height        The Height of this Control
     * @param topIn         How far from the top of the Screen the List should render at
     * @param bottomIn      How far from the bottom of the Screen the List should render at
     * @param slotHeightIn  The height of each slot in the list
     * @param itemList      The List of items to allocate for the slots in the Gui
     * @param currentValue  The current value, if any, to select upon initialization of the Gui
     */
    public ScrollableListControl(@Nonnull final Minecraft mc, final ExtendedScreen currentScreen, final int width, final int height, final int topIn, final int bottomIn, final int slotHeightIn, final List<String> itemList, final String currentValue) {
        super(mc, width, height, topIn, bottomIn, slotHeightIn);
        this.currentScreen = currentScreen;
        this.currentValue = currentValue;
        setList(itemList);
    }

    /**
     * Initialization Event for this Control, assigning defined arguments
     *
     * @param mc            The Minecraft Instance for this Control
     * @param currentScreen The current screen instance for this control
     * @param width         The Width of this Control
     * @param height        The Height of this Control
     * @param topIn         How far from the top of the Screen the List should render at
     * @param bottomIn      How far from the bottom of the Screen the List should render at
     * @param itemList      The List of items to allocate for the slots in the Gui
     * @param currentValue  The current value, if any, to select upon initialization of the Gui
     */
    public ScrollableListControl(@Nonnull final Minecraft mc, final ExtendedScreen currentScreen, final int width, final int height, final int topIn, final int bottomIn, final List<String> itemList, final String currentValue) {
        this(
                mc,
                currentScreen,
                width, height,
                topIn, bottomIn,
                DEFAULT_SLOT_HEIGHT,
                itemList,
                currentValue
        );
    }

    /**
     * Retrieves whether the specified position is within list bounds
     *
     * @param mouseX The Mouse's Current X Position
     * @param mouseY The Mouse's Current Y Position
     * @return {@link Boolean#TRUE} if the Mouse Position is within the bounds of the list
     */
    public boolean isWithinBounds(final int mouseX, final int mouseY) {
        return RenderUtils.isMouseWithin(
                mouseX, mouseY,
                top,
                bottom,
                left,
                right
        );
    }

    /**
     * Retrieves the Amount of Items in the List
     *
     * @return The Amount of Items in the List
     */
    @Override
    protected int getSize() {
        return itemList.size();
    }

    /**
     * The Event to Occur if a Slot/Element is Clicked within the List
     *
     * @param slotIndex     The Slot Number that was Clicked
     * @param isDoubleClick Whether the Click was a Double or Single Click
     * @param mouseX        The Mouse's Current X Position
     * @param mouseY        The Mouse's Current Y Position
     */
    @Override
    public void elementClicked(int slotIndex, boolean isDoubleClick, int mouseX, int mouseY) {
        currentValue = getSelectedItem(slotIndex);
    }

    /**
     * Whether the Specified Slot Number is the Currently Selected Slot
     *
     * @param slotIndex The Slot's ID Number to check
     * @return {@link Boolean#TRUE} if the Slot Number is the Currently Selected Slot
     */
    @Override
    public boolean isSelected(int slotIndex) {
        return getSelectedItem(slotIndex).equals(currentValue);
    }

    /**
     * Renders the Background for this Control
     */
    @Override
    protected void drawBackground() {
        // N/A
    }

    /**
     * Renders the Slots for this Control
     *
     * @param slotIndex    The Slot Identification Number
     * @param xPos         The Starting X Position to render the Object at
     * @param yPos         The Starting Y Position to render the Object at
     * @param heightIn     The Height for the Object to render to
     * @param mouseXIn     The Mouse's Current X Position
     * @param mouseYIn     The Mouse's Current Y Position
     * @param partialTicks The Current Partial Tick Ratio
     */
    @Override
    protected void drawSlot(int slotIndex, int xPos, int yPos, int heightIn, int mouseXIn, int mouseYIn, float partialTicks) {
        renderSlotItem(getSelectedItem(slotIndex), xPos, yPos, getListWidth(), heightIn, mouseXIn, mouseYIn);
    }

    /**
     * Attempts to Retrieve the Slot Item Name from the Slot's ID Number
     *
     * @param slotIndex The Slot's ID Number
     * @return The Name of the found slot, if any
     */
    public String getSelectedItem(final int slotIndex) {
        try {
            return itemList.get(slotIndex);
        } catch (Exception ex) {
            return null;
        }
    }

    /**
     * Get the Current Font Renderer for this Control
     *
     * @return The Current Font Renderer for this Control
     */
    public FontRenderer getFontRenderer() {
        return mc.fontRenderer != null ? mc.fontRenderer : RenderUtils.getDefaultFontRenderer();
    }

    /**
     * Get the Current Font Height for this Control
     *
     * @return The Current Font Height for this Control
     */
    public int getFontHeight() {
        return RenderUtils.getFontHeight(getFontRenderer());
    }

    /**
     * Sets the item list to be rendered (And resets the scroll if needed)
     *
     * @param itemList The list to interpret
     * @return {@link Boolean#TRUE} if list was modified
     */
    public boolean setList(List<String> itemList) {
        if (itemList == null) {
            itemList = StringUtils.newArrayList();
        }
        if (!itemList.equals(this.itemList)) {
            this.itemList = itemList;
            // Reset the scrollbar to prevent OOB issues
            scrollBy(Integer.MIN_VALUE);

            return true;
        }
        return false;
    }

    /**
     * Renders a Slot Entry for this Control
     *
     * @param originalName The original entry name, before processing
     * @param xPos         The Starting X Position to render the Object at
     * @param yPos         The Starting Y Position to render the Object at
     * @param widthIn      The Width for the Object to render to
     * @param heightIn     The Height for the Object to render to
     * @param mouseXIn     The Mouse's Current X Position
     * @param mouseYIn     The Mouse's Current Y Position
     */
    public void renderSlotItem(final String originalName, final int xPos, final int yPos, final int widthIn, final int heightIn, final int mouseXIn, final int mouseYIn) {
        RenderUtils.renderScrollingString(mc,
                getFontRenderer(),
                originalName,
                xPos + (RenderUtils.getStringWidth(getFontRenderer(), originalName) / 2),
                xPos, yPos,
                xPos + widthIn - 4,
                yPos + heightIn,
                0xFFFFFF
        );
    }
}
