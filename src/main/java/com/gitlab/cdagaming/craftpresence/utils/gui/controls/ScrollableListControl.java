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
import com.gitlab.cdagaming.craftpresence.utils.ImageUtils;
import com.gitlab.cdagaming.craftpresence.utils.StringUtils;
import com.gitlab.cdagaming.craftpresence.utils.discord.assets.DiscordAssetUtils;
import com.gitlab.cdagaming.craftpresence.utils.gui.GuiUtils;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.FontRenderer;
import net.minecraft.client.gui.GuiSlot;
import net.minecraft.client.multiplayer.ServerData;
import net.minecraft.util.ResourceLocation;

import java.util.List;

/**
 * Gui Widget for a Scrollable List
 *
 * @author CDAGaming
 */
public class ScrollableListControl extends GuiSlot {
    /**
     * The Currently Selected Value in the List
     */
    public String currentValue;

    /**
     * The Items available to select within the List Gui
     */
    public List<String> itemList;

    /**
     * The Rendering Type to render the slots in
     */
    public RenderType renderType;

    /**
     * Initialization Event for this Control, assigning defined arguments
     *
     * @param mc           The Minecraft Instance for this Control
     * @param width        The Width of this Control
     * @param height       The Height of this Control
     * @param topIn        How far from the top of the Screen the List should render at
     * @param bottomIn     How far from the bottom of the Screen the List should render at
     * @param slotHeightIn The height of each slot in the list
     * @param itemList     The List of items to allocate for the slots in the Gui
     * @param currentValue The current value, if any, to select upon initialization of the Gui
     */
    public ScrollableListControl(Minecraft mc, int width, int height, int topIn, int bottomIn, int slotHeightIn, List<String> itemList, String currentValue) {
        this(mc, width, height, topIn, bottomIn, slotHeightIn, itemList, currentValue, RenderType.None);
    }

    /**
     * Initialization Event for this Control, assigning defined arguments
     *
     * @param mc           The Minecraft Instance for this Control
     * @param width        The Width of this Control
     * @param height       The Height of this Control
     * @param topIn        How far from the top of the Screen the List should render at
     * @param bottomIn     How far from the bottom of the Screen the List should render at
     * @param slotHeightIn The height of each slot in the list
     * @param itemList     The List of items to allocate for the slots in the Gui
     * @param currentValue The current value, if any, to select upon initialization of the Gui
     * @param renderType   The Rendering type for this Scroll List
     */
    public ScrollableListControl(Minecraft mc, int width, int height, int topIn, int bottomIn, int slotHeightIn, List<String> itemList, String currentValue, RenderType renderType) {
        super(mc, width, height, topIn, bottomIn, slotHeightIn);
        this.itemList = itemList;
        this.currentValue = currentValue;
        this.renderType = renderType;
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
     * @return {@code true} if the Slot Number is the Currently Selected Slot
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
        int xOffset = xPos;
        String displayName = getSelectedItem(slotIndex);
        if (!CraftPresence.CONFIG.stripExtraGuiElements &&
                (renderType == RenderType.DiscordAsset || (renderType == RenderType.ServerData && CraftPresence.SERVER.enabled) ||
                        (renderType == RenderType.EntityData && CraftPresence.ENTITIES.enabled) ||
                        (renderType == RenderType.ItemData && CraftPresence.TILE_ENTITIES.enabled))) {
            ResourceLocation texture = new ResourceLocation("");
            String assetUrl;

            if (renderType == RenderType.ServerData) {
                final ServerData data = CraftPresence.SERVER.getDataFromName(displayName);

                if (data != null) {
                    assetUrl = StringUtils.UNKNOWN_BASE64_ID + "," + data.getBase64EncodedIconData();
                    texture = ImageUtils.getTextureFromUrl(displayName, new Pair<>(ImageUtils.InputType.ByteStream, assetUrl));
                }
            } else if (renderType == RenderType.DiscordAsset) {
                assetUrl = DiscordAssetUtils.getUrl(displayName);
                texture = ImageUtils.getTextureFromUrl(displayName, assetUrl);
            } else if (renderType == RenderType.EntityData) {
                if (StringUtils.isValidUuid(displayName)) {
                    // If the entity is classified via Uuid, assume it is a player's and get their altFace texture
                    displayName = displayName.replaceAll("-", "");
                    texture = ImageUtils.getTextureFromUrl(displayName, String.format(CraftPresence.CONFIG.playerSkinEndpoint, displayName));
                }
            } else if (renderType == RenderType.ItemData) {
                texture = CraftPresence.TILE_ENTITIES.TILE_ENTITY_RESOURCES.getOrDefault(displayName, texture);
            }
            if (!ImageUtils.isTextureNull(texture)) {
                CraftPresence.GUIS.drawTextureRect(0.0D, xOffset, yPos + 4.5, 32, 32, 0, texture);
            }
            // Note: 35 Added to xOffset to accommodate for Image Size
            xOffset += 35;
        }
        getFontRenderer().drawStringWithShadow(displayName, xOffset, yPos + ((heightIn / 2f) - (getFontHeight() / 2f)), 0xFFFFFF);
    }

    /**
     * Attempts to Retrieve the Slot Item Name from the Slot's ID Number
     *
     * @param slotIndex The Slot's ID Number
     * @return The Name of the found slot, if any
     */
    public String getSelectedItem(int slotIndex) {
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
        return mc.fontRenderer != null ? mc.fontRenderer : GuiUtils.getDefaultFontRenderer();
    }

    /**
     * Get the Current Font Height for this Control
     *
     * @return The Current Font Height for this Control
     */
    public int getFontHeight() {
        return getFontRenderer().FONT_HEIGHT;
    }

    /**
     * The Rendering Type for this Scroll List
     */
    public enum RenderType {
        DiscordAsset, ServerData, EntityData, ItemData, None
    }
}
