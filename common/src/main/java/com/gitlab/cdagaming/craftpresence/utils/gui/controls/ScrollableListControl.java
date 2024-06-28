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

import com.gitlab.cdagaming.craftpresence.CraftPresence;
import com.gitlab.cdagaming.craftpresence.core.Constants;
import com.gitlab.cdagaming.craftpresence.core.utils.discord.assets.DiscordAssetUtils;
import com.gitlab.cdagaming.craftpresence.utils.entity.TileEntityUtils;
import io.github.cdagaming.unicore.impl.Pair;
import io.github.cdagaming.unicore.utils.MappingUtils;
import io.github.cdagaming.unicore.utils.StringUtils;
import io.github.cdagaming.unilib.utils.ImageUtils;
import io.github.cdagaming.unilib.utils.ResourceUtils;
import io.github.cdagaming.unilib.utils.gui.RenderUtils;
import io.github.cdagaming.unilib.utils.gui.integrations.ExtendedScreen;
import io.github.classgraph.ClassInfo;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.FontRenderer;
import net.minecraft.client.gui.GuiSlot;
import net.minecraft.client.multiplayer.ServerData;
import net.minecraft.item.ItemStack;
import net.minecraft.util.ResourceLocation;

import javax.annotation.Nonnull;
import java.awt.*;
import java.util.List;
import java.util.Map;

/**
 * Gui Widget for a Scrollable List
 *
 * @author CDAGaming
 */
@SuppressWarnings("DuplicatedCode")
public class ScrollableListControl extends GuiSlot {
    /**
     * Mapping representing a link between the entries original name, and it's display name
     */
    public final Map<String, String> entryAliases = StringUtils.newHashMap();
    /**
     * The Rendering Type to render the slots in
     */
    public final RenderType renderType;
    /**
     * The Currently Selected Value in the List
     */
    public String currentValue;
    /**
     * The Current Hover Text that should be displayed
     */
    public List<String> currentHoverText = StringUtils.newArrayList();
    /**
     * The Items available to select within the List Gui
     */
    public List<String> itemList;
    /**
     * The Identifier Type, normally related to the Render Type
     */
    public IdentifierType identifierType = IdentifierType.None;
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
    public ScrollableListControl(@Nonnull final Minecraft mc, final ExtendedScreen currentScreen, final int width, final int height, final int topIn, final int bottomIn, final int slotHeightIn, final List<String> itemList, final String currentValue, final RenderType renderType) {
        super(mc, width, height, topIn, bottomIn, slotHeightIn);
        this.currentScreen = currentScreen;
        this.currentValue = currentValue;
        this.renderType = renderType;
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
     * @param renderType    The Rendering type for this Scroll List
     */
    public ScrollableListControl(@Nonnull final Minecraft mc, final ExtendedScreen currentScreen, final int width, final int height, final int topIn, final int bottomIn, final List<String> itemList, final String currentValue, final RenderType renderType) {
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
     * Sets the Identifier Type to be linked to this Render Type
     *
     * @param type The {@link IdentifierType} to interpret
     * @return the modified instance
     */
    public ScrollableListControl setIdentifierType(final IdentifierType type) {
        this.identifierType = type;
        return this;
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
     */
    public void setList(List<String> itemList) {
        if (itemList == null) {
            itemList = StringUtils.newArrayList();
        }
        if (!itemList.equals(this.itemList)) {
            this.itemList = itemList;
            // Reset the scrollbar to prevent OOB issues
            scrollBy(Integer.MIN_VALUE);

            setupAliasData();
        }
    }

    /**
     * Setup Mappings between the entries original name, and it's display name
     */
    public void setupAliasData() {
        entryAliases.clear();

        for (String originalName : StringUtils.newArrayList(itemList)) {
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
        final List<String> hoverText = StringUtils.newArrayList();
        String displayName = entryAliases.getOrDefault(originalName, originalName);
        int xOffset = xPos;

        final boolean isOverEntry = RenderUtils.isMouseOver(mouseXIn, mouseYIn, xPos, yPos, widthIn, heightIn);
        final boolean isInBounds = isWithinBounds(mouseXIn, mouseYIn);
        final boolean isHovering = isInBounds && isOverEntry;

        ResourceLocation texture = ResourceUtils.getEmptyResource();
        String assetUrl;

        if (renderType == RenderType.ServerData) {
            final ServerData data = CraftPresence.SERVER.getDataFromName(originalName);

            if (data != null && !StringUtils.isNullOrEmpty(data.getBase64EncodedIconData())) {
                assetUrl = "data:image/png;base64," + data.getBase64EncodedIconData();
                texture = ImageUtils.getTextureFromUrl(mc, originalName, new Pair<>(ImageUtils.InputType.ByteStream, assetUrl));
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
                texture = ImageUtils.getTextureFromUrl(mc, originalName, endpointUrl);
                if (currentScreen.isDebugMode() && isHovering) {
                    hoverText.add(Constants.TRANSLATOR.translate("gui.config.message.editor.url") + " " + endpointUrl);
                }
            }
        } else if (renderType == RenderType.DiscordAsset || renderType == RenderType.CustomDiscordAsset) {
            assetUrl = DiscordAssetUtils.getUrl(
                    renderType == RenderType.CustomDiscordAsset ? DiscordAssetUtils.CUSTOM_ASSET_LIST : DiscordAssetUtils.ASSET_LIST,
                    originalName,
                    CraftPresence.CLIENT::getResult
            );
            if (currentScreen.isDebugMode() && isHovering) {
                hoverText.add(Constants.TRANSLATOR.translate("gui.config.message.editor.url") + " " + assetUrl);
            }
            texture = ImageUtils.getTextureFromUrl(mc, originalName, assetUrl);
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
                texture = ImageUtils.getTextureFromUrl(mc, originalName, endpointUrl);
                if (currentScreen.isDebugMode() && isHovering) {
                    hoverText.add(Constants.TRANSLATOR.translate("gui.config.message.editor.url") + " " + endpointUrl);
                }
            }
        } else if (renderType == RenderType.ItemData && renderType.canRenderImage()) {
            final Map<String, ItemStack> data = CraftPresence.TILE_ENTITIES.TILE_ENTITY_RESOURCES;
            if (data.containsKey(originalName)) {
                final ItemStack stack = data.get(originalName);
                if (!TileEntityUtils.isEmpty(stack)) {
                    RenderUtils.drawItemStack(
                            mc, getFontRenderer(), xOffset, yPos + 4, stack,
                            2.0f
                    );
                    xOffset += 35;
                }
            }
        } else if (renderType == RenderType.Placeholder && isHovering) {
            final String message = CraftPresence.CLIENT.generateArgumentMessage(
                    originalName, false, false,
                    CraftPresence.CONFIG.advancedSettings.allowPlaceholderPreviews,
                    ""
            );
            if (!StringUtils.isNullOrEmpty(message)) {
                hoverText.addAll(StringUtils.splitTextByNewLine(message));
            }
        }

        if (renderType.canRenderImage() && !ImageUtils.isTextureNull(texture)) {
            final double yOffset = yPos + 4.5;
            final double size = 32;
            RenderUtils.drawTexture(mc,
                    xOffset, xOffset + size, yOffset, yOffset + size,
                    0.0D,
                    0.0D, 1.0D,
                    0.0D, 1.0D,
                    Color.white, Color.white,
                    texture
            );
            if (currentScreen.isDebugMode() && isHovering) {
                hoverText.add(Constants.TRANSLATOR.translate("gui.config.message.editor.texture_path") + " " + texture);
            }
            // Note: 35 Added to xOffset to accommodate for Image Size
            xOffset += 35;
        }

        final String identifierName = identifierType.getIdentifier(originalName);
        if (!identifierName.equals(displayName) && isHovering) {
            hoverText.add(Constants.TRANSLATOR.translate("gui.config.message.editor.original") + " " + identifierName);
        }

        RenderUtils.renderScrollingString(mc,
                getFontRenderer(),
                displayName,
                xOffset + (RenderUtils.getStringWidth(getFontRenderer(), displayName) / 2),
                xOffset, yPos,
                xPos + widthIn - 4,
                yPos + heightIn,
                0xFFFFFF
        );

        if (isHovering) {
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
        Placeholder(false),
        /**
         * Constant for the "Text Only" Rendering Mode.
         */
        None(false);

        private final boolean canRenderImage;

        RenderType() {
            canRenderImage = true;
        }

        RenderType(final boolean canRenderImage) {
            this.canRenderImage = canRenderImage;
        }

        /**
         * Retrieve whether this Render Mode can render images
         *
         * @return {@link Boolean#TRUE} if this Render Mode can render images
         */
        public boolean canRenderImage() {
            return this.canRenderImage && !CraftPresence.CONFIG.accessibilitySettings.stripExtraGuiElements;
        }
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
        None;

        /**
         * Retrieve the identifier name to use with the specified argument
         *
         * @param originalName The original entry name
         * @return the processed identifier name
         */
        public String getIdentifier(final String originalName) {
            String identifierName;
            switch (this) {
                case Gui: {
                    final ClassInfo target = CraftPresence.GUIS.GUI_CLASSES.get(originalName);
                    identifierName = target != null ? MappingUtils.getCanonicalName(target) : originalName;
                    break;
                }
                case None:
                default: {
                    identifierName = originalName;
                    break;
                }
            }
            return identifierName;
        }
    }
}
