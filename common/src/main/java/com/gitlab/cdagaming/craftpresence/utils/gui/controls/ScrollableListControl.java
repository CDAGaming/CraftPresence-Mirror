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

package com.gitlab.cdagaming.craftpresence.utils.gui.controls;

import com.gitlab.cdagaming.craftpresence.CraftPresence;
import com.gitlab.cdagaming.craftpresence.ModUtils;
import com.gitlab.cdagaming.craftpresence.impl.Pair;
import com.gitlab.cdagaming.craftpresence.utils.ImageUtils;
import com.gitlab.cdagaming.craftpresence.utils.MappingUtils;
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
@SuppressWarnings("unchecked")
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
            CraftPresence.TILE_ENTITIES.getAllData();
        }
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
     * @param renderType    The Rendering type for this Scroll List
     */
    public ScrollableListControl(Minecraft mc, ExtendedScreen currentScreen, int width, int height, int topIn, int bottomIn, List<String> itemList, String currentValue, RenderType renderType) {
        this(
                mc,
                currentScreen,
                width, height,
                topIn, bottomIn,
                renderType.canRenderImage() ? 45 : 18,
                itemList,
                currentValue,
                renderType
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
        renderSlotItem(getSelectedItem(slotIndex), xPos, yPos, getListWidth(), heightIn, mouseXIn, mouseYIn);
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
            scrollBy(Integer.MIN_VALUE);
        }

        setupAliasData();
    }

    /**
     * Setup Mappings between the entries original name, and it's display name
     */
    public void setupAliasData() {
        entryAliases.clear();

        for (String originalName : Lists.newArrayList(itemList)) {
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
        final List<String> hoverText = Lists.newArrayList();
        String displayName = entryAliases.getOrDefault(originalName, originalName);
        int xOffset = xPos;

        if (!CraftPresence.CONFIG.accessibilitySettings.stripExtraGuiElements) {
            ResourceLocation texture = new ResourceLocation("");
            String assetUrl;

            if (renderType == RenderType.ServerData) {
                final ServerData data = CraftPresence.SERVER.getDataFromName(originalName);

                if (data != null) {
                    assetUrl = StringUtils.UNKNOWN_BASE64_ID + "," + data.getBase64EncodedIconData();
                    texture = ImageUtils.getTextureFromUrl(originalName, new Pair<>(ImageUtils.InputType.ByteStream, assetUrl));
                } else if (CraftPresence.CONFIG.advancedSettings.allowEndpointIcons &&
                        !StringUtils.isNullOrEmpty(CraftPresence.CONFIG.advancedSettings.serverIconEndpoint)) {
                    final String formattedIP = originalName.contains(":") ? StringUtils.formatAddress(originalName, false) : originalName;
                    final String endpointUrl = CraftPresence.CLIENT.compileData(String.format(
                                    CraftPresence.CONFIG.advancedSettings.serverIconEndpoint,
                                    originalName
                            ),
                            new Pair<>("server.address.short", () -> formattedIP),
                            new Pair<>("server.address.full", () -> originalName)
                    ).get().toString();
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
                final boolean isPlayer = CraftPresence.ENTITIES.PLAYER_BINDINGS.containsKey(originalName);
                final boolean isValidUuid = StringUtils.isValidUuid(originalName);
                if (isPlayer && CraftPresence.CONFIG.advancedSettings.allowEndpointIcons &&
                        !StringUtils.isNullOrEmpty(CraftPresence.CONFIG.advancedSettings.playerSkinEndpoint)) {
                    final String endpointUrl = CraftPresence.CLIENT.compileData(String.format(
                                    CraftPresence.CONFIG.advancedSettings.playerSkinEndpoint,
                                    originalName
                            ),
                            new Pair<>("player.name", () -> originalName),
                            new Pair<>("player.uuid.full", () -> isValidUuid ? StringUtils.getFromUuid(originalName, false) : ""),
                            new Pair<>("player.uuid.short", () -> isValidUuid ? StringUtils.getFromUuid(originalName, true) : "")
                    ).get().toString();
                    texture = ImageUtils.getTextureFromUrl(originalName, endpointUrl);
                    if (currentScreen.isDebugMode()) {
                        hoverText.add(ModUtils.TRANSLATOR.translate("gui.config.message.editor.url") + " " + endpointUrl);
                    }
                }
            } else if (renderType == RenderType.ItemData) {
                texture = CraftPresence.TILE_ENTITIES.TILE_ENTITY_RESOURCES.getOrDefault(originalName, texture);
            } else if (renderType == RenderType.Placeholder) {
                final String placeholderTranslation = String.format("%s.placeholders.%s.description",
                        ModUtils.MOD_ID,
                        originalName
                );
                final String placeholderUsage = String.format("%s.placeholders.%s.usage",
                        ModUtils.MOD_ID,
                        originalName
                );
                if (ModUtils.TRANSLATOR.hasTranslation(placeholderTranslation)) {
                    hoverText.add(String.format("%s \"%s\"",
                            ModUtils.TRANSLATOR.translate("gui.config.message.editor.description"),
                            ModUtils.TRANSLATOR.translate(placeholderTranslation)
                    ));
                }
                if (ModUtils.TRANSLATOR.hasTranslation(placeholderUsage)) {
                    hoverText.add(String.format("%s \"%s\"",
                            ModUtils.TRANSLATOR.translate("gui.config.message.editor.usage"),
                            ModUtils.TRANSLATOR.translate(placeholderUsage)
                    ));
                }
                if (CraftPresence.CONFIG.advancedSettings.allowPlaceholderPreviews) {
                    hoverText.add(String.format("%s \"%s\"",
                            ModUtils.TRANSLATOR.translate("gui.config.message.editor.preview"),
                            CraftPresence.CLIENT.getArgument(originalName).get().toString()
                    ));
                }
            }

            if (renderType.canRenderImage() && !ImageUtils.isTextureNull(texture)) {
                CraftPresence.GUIS.drawTextureRect(0.0D, xOffset, yPos + 4.5, 32, 32, 0, texture);
                if (currentScreen.isDebugMode()) {
                    hoverText.add(ModUtils.TRANSLATOR.translate("gui.config.message.editor.texture_path") + " " + texture);
                }
                // Note: 35 Added to xOffset to accommodate for Image Size
                xOffset += 35;
            }
        }

        final String identifierName = renderType.getIdentifier(originalName);
        if (!identifierName.equals(displayName)) {
            hoverText.add(ModUtils.TRANSLATOR.translate("gui.config.message.editor.original") + " " + identifierName);
        }
        getFontRenderer().drawStringWithShadow(displayName, xOffset, yPos + ((heightIn / 2f) - (getFontHeight() / 2f)), 0xFFFFFF);

        if (CraftPresence.GUIS.isMouseOver(mouseXIn, mouseYIn, xPos, yPos, widthIn, heightIn)) {
            currentHoverText = hoverText;
        }
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
         * Constant for the "Placeholder" Rendering Mode.
         */
        Placeholder,
        /**
         * Constant for the "Text Only" Rendering Mode.
         */
        None;

        /**
         * The Identifier Type linked to this Render Type
         */
        public IdentifierType identifierType = IdentifierType.None;

        /**
         * Whether this Render Mode can render images
         */
        private boolean canRenderImage = !CraftPresence.CONFIG.accessibilitySettings.stripExtraGuiElements;

        /**
         * Retrieve whether this Render Mode can render images
         *
         * @return {@link Boolean#TRUE} if this Render Mode can render images
         */
        public boolean canRenderImage() {
            return this != None && canRenderImage;
        }

        /**
         * Sets whether this Rendering Mode involves Image Rendering
         *
         * @param canRenderImage the modified value
         * @return the modified {@link RenderType} instance
         */
        public RenderType setCanRenderImage(boolean canRenderImage) {
            this.canRenderImage = canRenderImage;
            return this;
        }

        /**
         * Sets the Identifier Type to be linked to this Render Type
         *
         * @param type The {@link IdentifierType} to interpret
         * @return the modified {@link RenderType} instance
         */
        public RenderType setIdentifierType(IdentifierType type) {
            this.identifierType = type;
            return this;
        }

        /**
         * Retrieve the identifier name to use with the specified argument
         *
         * @param originalName The original entry name
         * @return the processed identifier name
         */
        public String getIdentifier(final String originalName) {
            String identifierName;
            switch (identifierType) {
                case Gui: {
                    final Class<?> target = CraftPresence.GUIS.GUI_CLASSES.get(originalName);
                    identifierName = target != null ? MappingUtils.getCanonicalName(target) : originalName;
                    break;
                }
                default: {
                    identifierName = originalName;
                    break;
                }
            }
            return identifierName;
        }

        /**
         * The Identifier Type attached to a Render Type
         */
        public enum IdentifierType {
            /**
             * Constant for the "Gui" Identifier Type.
             */
            Gui,
            /**
             * Constant for the "None" Identifier Type.
             */
            None
        }
    }
}
