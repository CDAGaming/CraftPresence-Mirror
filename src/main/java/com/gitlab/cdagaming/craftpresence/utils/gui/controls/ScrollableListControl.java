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
import net.minecraft.client.gui.Font;
import net.minecraft.client.gui.components.ObjectSelectionList;
import net.minecraft.client.multiplayer.ServerData;
import net.minecraft.resources.ResourceLocation;

import java.util.List;

/**
 * Gui Widget for a Scrollable List
 *
 * @author CDAGaming
 */
public class ScrollableListControl extends ObjectSelectionList<ScrollableListControl.StringEntry> {
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
     * The visibility for this control
     */
    public boolean visible = true;

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

        if (renderType == RenderType.ItemData) {
            CraftPresence.TILE_ENTITIES.getEntities();
        }
        updateEntries();
    }

    @Override
    public void setSelected(StringEntry entry) {
        super.setSelected(entry);
        if (entry != null) {
            currentValue = entry.displayName;
        }
    }

    /**
     * Retrieves the Amount of Items in the List
     *
     * @return The Amount of Items in the List
     */
    @Override
    protected int getItemCount() {
        return itemList.size();
    }

    /**
     * Renders the Background for this Control
     */
    @Override
    protected void renderBackground() {
        if (getItemCount() != children().size()) {
            clearEntries();
            updateEntries();
        }
    }

    /**
     * Updates the Entries in this List
     */
    public void updateEntries() {
        for (String item : itemList) {
            StringEntry dataEntry = new StringEntry(item, renderType);
            this.addEntry(dataEntry);
            if (item.equals(currentValue)) {
                this.setSelected(dataEntry);
            }
        }

        if (this.getSelected() != null) {
            this.centerScrollOn(this.getSelected());
        }
    }

    /**
     * Get the Current Font Renderer for this Control
     *
     * @return The Current Font Renderer for this Control
     */
    public Font getFontRenderer() {
        return minecraft.font != null ? minecraft.font : GuiUtils.getDefaultFontRenderer();
    }

    /**
     * Get the Current Font Height for this Control
     *
     * @return The Current Font Height for this Control
     */
    public int getFontHeight() {
        return getFontRenderer().lineHeight;
    }

    /**
     * Getter for Visibility for this control
     */
    public boolean isVisible() {
        return this.visible;
    }

    /**
     * Setter for Visibility for this control
     *
     * @param value The new visibility value for this control
     */
    public void setVisible(boolean value) {
        this.visible = value;
    }

    /**
     * The Rendering Type for this Scroll List
     */
    public enum RenderType {
        DiscordAsset, CustomDiscordAsset, ServerData, EntityData, ItemData, None
    }

    /**
     * Gui Entry for a Scrollable List
     *
     * @author CDAGaming
     */
    public class StringEntry extends ObjectSelectionList.Entry<StringEntry> {
        /**
         * The rendering type to render this entry in
         */
        private final RenderType renderType;
        /**
         * The name of this Entry
         */
        private String displayName;

        /**
         * Initialization Event for this Control, assigning defined arguments
         *
         * @param name The name to assign to this Entry
         */
        public StringEntry(String name) {
            this(name, RenderType.None);
        }

        /**
         * Initialization Event for this Control, assigning defined arguments
         *
         * @param name       The name to assign to this Entry
         * @param renderType The Render Type to assign to this Entry
         */
        public StringEntry(String name, RenderType renderType) {
            this.displayName = name;
            this.renderType = renderType;
        }

        /**
         * Renders this Entry to the List
         *
         * @param index       The Index of the Entry within the List
         * @param yPos        The Y Coordinate to render at
         * @param xPos        The X Coordinate to render at
         * @param entryWidth  The specified Entry's Width
         * @param entryHeight The specified Entry's Height
         * @param mouseX      The Event Mouse X Coordinate
         * @param mouseY      The Event Mouse Y Coordinate
         * @param hovered     Whether the specified entry is currently hovered over
         * @param tickDelta   The Rendering Tick Rate
         */
        @Override
        public void render(int index, int yPos, int xPos, int entryWidth, int entryHeight, int mouseX, int mouseY, boolean hovered, float tickDelta) {
            int xOffset = xPos;
            if (!CraftPresence.CONFIG.stripExtraGuiElements &&
                    ((renderType == RenderType.DiscordAsset || renderType == RenderType.CustomDiscordAsset) || (renderType == RenderType.ServerData && CraftPresence.SERVER.enabled) ||
                            (renderType == RenderType.EntityData && CraftPresence.ENTITIES.enabled) ||
                            (renderType == RenderType.ItemData && CraftPresence.TILE_ENTITIES.enabled))) {
                ResourceLocation texture = new ResourceLocation("");
                String assetUrl;

                if (renderType == RenderType.ServerData) {
                    final ServerData data = CraftPresence.SERVER.getDataFromName(displayName);

                    if (data != null) {
                        assetUrl = StringUtils.UNKNOWN_BASE64_ID + "," + data.getIconB64();
                        texture = ImageUtils.getTextureFromUrl(displayName, new Pair<>(ImageUtils.InputType.ByteStream, assetUrl));
                    }
                } else if (renderType == RenderType.DiscordAsset || renderType == RenderType.CustomDiscordAsset) {
                    assetUrl = DiscordAssetUtils.getUrl(
                            renderType == RenderType.CustomDiscordAsset ? DiscordAssetUtils.CUSTOM_ASSET_LIST : DiscordAssetUtils.ASSET_LIST,
                            displayName
                    );
                    texture = ImageUtils.getTextureFromUrl(displayName, assetUrl);
                } else if (renderType == RenderType.EntityData) {
                    if (StringUtils.isValidUuid(displayName)) {
                        // If the entity is classified via Uuid, assume it is a player's and get their altFace texture
                        displayName = StringUtils.getFromUuid(displayName);
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
            getFontRenderer().drawShadow(displayName, xOffset, yPos + ((entryHeight / 2f) - (getFontHeight() / 2f)), 0xFFFFFF);
        }

        /**
         * Event to trigger upon the mouse being clicked
         *
         * @param mouseX The Event Mouse X Coordinate
         * @param mouseY The Event Mouse Y Coordinate
         * @param button The Event Mouse Button Clicked
         * @return The Event Result
         */
        @Override
        public boolean mouseClicked(double mouseX, double mouseY, int button) {
            if (button == 0) {
                this.onPressed();
                return true;
            } else {
                return false;
            }
        }

        /**
         * The Event to occur when this Entry is pressed
         */
        private void onPressed() {
            ScrollableListControl.this.setSelected(this);
        }
    }
}
