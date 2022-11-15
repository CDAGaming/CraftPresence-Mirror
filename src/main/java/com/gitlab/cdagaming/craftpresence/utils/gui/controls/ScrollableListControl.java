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
import com.gitlab.cdagaming.craftpresence.ModUtils;
import com.gitlab.cdagaming.craftpresence.impl.Pair;
import com.gitlab.cdagaming.craftpresence.utils.ImageUtils;
import com.gitlab.cdagaming.craftpresence.utils.StringUtils;
import com.gitlab.cdagaming.craftpresence.utils.discord.assets.DiscordAssetUtils;
import com.gitlab.cdagaming.craftpresence.utils.gui.GuiUtils;
import com.gitlab.cdagaming.craftpresence.utils.gui.integrations.ExtendedScreen;
import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.FontRenderer;
import net.minecraft.client.gui.GuiSlot;
import net.minecraft.client.multiplayer.ServerData;
import net.minecraft.util.ResourceLocation;

import java.util.List;
import java.util.Map;

/**
 * Gui Widget for a Scrollable List
 *
 * @author CDAGaming
 */
public class ScrollableListControl extends GuiSlot {
    /**
     * Mapping representing a link between the entries original name, and it's display name
     */
    public final Map<String, String> entryAliases = Maps.newHashMap();
    /**
     * The Currently Selected Value in the List
     */
    public String currentValue;
    /**
     * The Current Hover Text that should be displayed
     */
    public List<String> currentHoverText = Lists.newArrayList();
    /**
     * The Items available to select within the List Gui
     */
    public List<String> itemList;
    /**
     * The Rendering Type to render the slots in
     */
    public RenderType renderType;

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
    public ScrollableListControl(Minecraft mc, ExtendedScreen currentScreen, int width, int height, int topIn, int bottomIn, int slotHeightIn, List<String> itemList, String currentValue) {
        this(mc, currentScreen, width, height, topIn, bottomIn, slotHeightIn, itemList, currentValue, RenderType.None);
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
     * @param slotHeightIn  The height of each slot in the list
     * @param itemList      The List of items to allocate for the slots in the Gui
     * @param currentValue  The current value, if any, to select upon initialization of the Gui
     * @param renderType    The Rendering type for this Scroll List
     */
    public ScrollableListControl(Minecraft mc, ExtendedScreen currentScreen, int width, int height, int topIn, int bottomIn, int slotHeightIn, List<String> itemList, String currentValue, RenderType renderType) {
        super(mc, width, height, topIn, bottomIn, slotHeightIn);
        setList(itemList);
        this.currentScreen = currentScreen;
        this.currentValue = currentValue;
        this.renderType = renderType;

        if (renderType == RenderType.ItemData) {
            CraftPresence.TILE_ENTITIES.getEntities();
        }
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
     * @param mouseX      The Mouse's Current X Position
     * @param mouseZ      The Mouse's Current Y Position
     * @param mouseButton Which Mouse Button was Clicked
     */
    @Override
    public boolean mouseClicked(double mouseX, double mouseZ, int mouseButton) {
        if (isMouseInList(mouseX, mouseZ)) {
            int scrollIndex = getEntryAt(mouseX, mouseZ);
            if (scrollIndex != -1) {
                currentValue = getSelectedItem(scrollIndex);
            }
        }
        return super.mouseClicked(mouseX, mouseZ, mouseButton);
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
        final String originalName = getSelectedItem(slotIndex);
        final List<String> hoverText = Lists.newArrayList();
        String displayName = entryAliases.getOrDefault(originalName, originalName);
        int xOffset = xPos;

        if (!CraftPresence.CONFIG.accessibilitySettings.stripExtraGuiElements &&
                ((renderType == RenderType.DiscordAsset || renderType == RenderType.CustomDiscordAsset) || (renderType == RenderType.ServerData && CraftPresence.SERVER.enabled) ||
                        (renderType == RenderType.EntityData && CraftPresence.ENTITIES.enabled) ||
                        (renderType == RenderType.ItemData && CraftPresence.TILE_ENTITIES.enabled))) {
            ResourceLocation texture = new ResourceLocation("");
            String assetUrl;

            if (renderType == RenderType.ServerData) {
                final ServerData data = CraftPresence.SERVER.getDataFromName(originalName);

                if (data != null) {
                    assetUrl = StringUtils.UNKNOWN_BASE64_ID + "," + data.getBase64EncodedIconData();
                    texture = ImageUtils.getTextureFromUrl(originalName, new Pair<>(ImageUtils.InputType.ByteStream, assetUrl));
                } else if (CraftPresence.CONFIG.advancedSettings.allowEndpointIcons && !StringUtils.isNullOrEmpty(CraftPresence.CONFIG.advancedSettings.serverIconEndpoint)) {
                    final String endpointUrl = String.format(CraftPresence.CONFIG.advancedSettings.serverIconEndpoint, originalName);
                    texture = ImageUtils.getTextureFromUrl(originalName, endpointUrl);
                    if (currentScreen.isDebugMode()) {
                        hoverText.add(ModUtils.TRANSLATOR.translate("gui.config.message.editor.url") + " " + endpointUrl);
                    }
                }
            } else if (renderType == RenderType.DiscordAsset || renderType == RenderType.CustomDiscordAsset) {
                assetUrl = DiscordAssetUtils.getUrl(
                        renderType == RenderType.CustomDiscordAsset ? DiscordAssetUtils.CUSTOM_ASSET_LIST : DiscordAssetUtils.ASSET_LIST,
                        originalName
                );
                if (currentScreen.isDebugMode()) {
                    hoverText.add(ModUtils.TRANSLATOR.translate("gui.config.message.editor.url") + " " + assetUrl);
                }
                texture = ImageUtils.getTextureFromUrl(originalName, assetUrl);
            } else if (renderType == RenderType.EntityData) {
                if (StringUtils.isValidUuid(originalName)) {
                    // If the entity is classified via Uuid, assume it is a player's and get their altFace texture
                    final String fullUuid = StringUtils.getFromUuid(originalName, false);
                    final String trimmedUuid = StringUtils.getFromUuid(originalName, true);
                    if (CraftPresence.CONFIG.advancedSettings.allowEndpointIcons && !StringUtils.isNullOrEmpty(CraftPresence.CONFIG.advancedSettings.playerSkinEndpoint)) {
                        final String endpointUrl = String.format(CraftPresence.CONFIG.advancedSettings.playerSkinEndpoint, fullUuid);
                        texture = ImageUtils.getTextureFromUrl(fullUuid, endpointUrl);
                        if (currentScreen.isDebugMode()) {
                            hoverText.add(ModUtils.TRANSLATOR.translate("gui.config.message.editor.url") + " " + endpointUrl);
                        }
                    }
                }
            } else if (renderType == RenderType.ItemData) {
                texture = CraftPresence.TILE_ENTITIES.TILE_ENTITY_RESOURCES.getOrDefault(originalName, texture);
            }
            if (!ImageUtils.isTextureNull(texture)) {
                CraftPresence.GUIS.drawTextureRect(0.0D, xOffset, yPos + 4.5, 32, 32, 0, texture);
                if (currentScreen.isDebugMode()) {
                    hoverText.add(ModUtils.TRANSLATOR.translate("gui.config.message.editor.texture_path") + " " + texture);
                }
            }
            // Note: 35 Added to xOffset to accommodate for Image Size
            xOffset += 35;
        }
        if (!originalName.equals(displayName)) {
            hoverText.add(ModUtils.TRANSLATOR.translate("gui.config.message.editor.original") + " " + originalName);
        }
        getFontRenderer().drawStringWithShadow(displayName, xOffset, yPos + ((heightIn / 2f) - (getFontHeight() / 2f)), 0xFFFFFF);

        if (CraftPresence.GUIS.isMouseOver(mouseXIn, mouseYIn, xPos, yPos, getListWidth(), heightIn)) {
            currentHoverText = hoverText;
        }
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
     * Sets the item list to be rendered (And resets the scroll if needed)
     *
     * @param itemList The list to interpret
     */
    public void setList(List<String> itemList) {
        if (itemList == null) {
            itemList = Lists.newArrayList();
        }
        if (!itemList.equals(this.itemList)) {
            this.itemList = itemList;
            // Reset the scrollbar to prevent OOB issues
            if (amountScrolled != 0.0f) {
                amountScrolled = 0.0f;
            }
        }

        setupAliasData();
    }

    /**
     * Setup Mappings between the entries original name, and it's display name
     */
    public void setupAliasData() {
        entryAliases.clear();

        for (String originalName : itemList) {
            String displayName = originalName;
            if (renderType == RenderType.EntityData) {
                if (StringUtils.isValidUuid(originalName)) {
                    final String fullUuid = StringUtils.getFromUuid(originalName, false);
                    final String trimmedUuid = StringUtils.getFromUuid(originalName, true);
                    displayName = CraftPresence.ENTITIES.PLAYER_BINDINGS.getOrDefault(fullUuid, trimmedUuid);
                }
            }

            if (!originalName.equals(displayName)) {
                entryAliases.put(originalName, displayName);
            }
        }
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
        /**
         * Constant for the "Discord Asset" Rendering Mode.
         */
        DiscordAsset,
        /**
         * Constant for the "Discord Asset (Custom)" Rendering Mode.
         */
        CustomDiscordAsset,
        /**
         * Constant for the "Server" Rendering Mode.
         */
        ServerData,
        /**
         * Constant for the "Entity" Rendering Mode.
         */
        EntityData,
        /**
         * Constant for the "Item" Rendering Mode.
         */
        ItemData,
        /**
         * Constant for the "Text Only" Rendering Mode.
         */
        None
    }
}
