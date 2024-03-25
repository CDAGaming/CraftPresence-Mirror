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
import com.gitlab.cdagaming.craftpresence.utils.ImageUtils;
import com.gitlab.cdagaming.craftpresence.utils.discord.assets.DiscordAssetUtils;
import com.gitlab.cdagaming.craftpresence.utils.entity.TileEntityUtils;
import com.gitlab.cdagaming.craftpresence.utils.gui.GuiUtils;
import com.gitlab.cdagaming.craftpresence.utils.gui.RenderUtils;
import com.gitlab.cdagaming.craftpresence.utils.gui.integrations.ExtendedScreen;
import com.mojang.blaze3d.vertex.PoseStack;
import io.github.cdagaming.unicore.impl.Pair;
import io.github.cdagaming.unicore.utils.MappingUtils;
import io.github.cdagaming.unicore.utils.StringUtils;
import io.github.classgraph.ClassInfo;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.Font;
import net.minecraft.client.gui.components.ObjectSelectionList;
import net.minecraft.client.multiplayer.ServerData;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.ItemStack;
import org.meteordev.starscript.value.Value;

import javax.annotation.Nonnull;
import java.awt.*;
import java.util.List;
import java.util.Map;
import java.util.function.Supplier;

/**
 * Gui Widget for a Scrollable List
 *
 * @author CDAGaming
 */
public class ScrollableListControl extends ObjectSelectionList<ScrollableListControl.StringEntry> {
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
     * The visibility for this control
     */
    public boolean visible = true;

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

    @Override
    public void setSelected(StringEntry entry) {
        super.setSelected(entry);
        if (entry != null) {
            currentValue = entry.name;
        }
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
                y0,
                y1,
                x0,
                x1
        );
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
        return RenderUtils.getFontHeight(getFontRenderer());
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
    public void setVisible(final boolean value) {
        this.visible = value;
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
            setScrollAmount(Integer.MIN_VALUE);

            setupAliasData();
        }
    }

    /**
     * Setup Mappings between the entries original name, and it's display name
     */
    public void setupAliasData() {
        entryAliases.clear();
        clearEntries();

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

            final StringEntry dataEntry = new StringEntry(originalName, renderType);
            this.addEntry(dataEntry);
            if (originalName.equals(currentValue)) {
                this.setSelected(dataEntry);
            }
        }

        if (this.getSelected() != null) {
            this.centerScrollOn(this.getSelected());
        }
    }

    /**
     * Renders a Slot Entry for this Control
     *
     * @param matrices     The Matrix Stack, used for Rendering
     * @param originalName The original entry name, before processing
     * @param xPos         The Starting X Position to render the Object at
     * @param yPos         The Starting Y Position to render the Object at
     * @param widthIn      The Width for the Object to render to
     * @param heightIn     The Height for the Object to render to
     * @param mouseXIn     The Mouse's Current X Position
     * @param mouseYIn     The Mouse's Current Y Position
     */
    public void renderSlotItem(@Nonnull final PoseStack matrices, final String originalName, final int xPos, final int yPos, final int widthIn, final int heightIn, final int mouseXIn, final int mouseYIn) {
        final List<String> hoverText = StringUtils.newArrayList();
        String displayName = entryAliases.getOrDefault(originalName, originalName);
        int xOffset = xPos;

        final boolean isOverEntry = RenderUtils.isMouseOver(mouseXIn, mouseYIn, xPos, yPos, widthIn, heightIn);
        final boolean isInBounds = isWithinBounds(mouseXIn, mouseYIn);
        final boolean isHovering = isInBounds && isOverEntry;

        ResourceLocation texture = new ResourceLocation("");
        String assetUrl;

        if (renderType == RenderType.ServerData) {
            final ServerData data = CraftPresence.SERVER.getDataFromName(originalName);

            if (data != null && !StringUtils.isNullOrEmpty(data.getIconB64())) {
                assetUrl = "data:image/png;base64," + data.getIconB64();
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
                if (currentScreen.isDebugMode() && isHovering) {
                    hoverText.add(Constants.TRANSLATOR.translate("gui.config.message.editor.url") + " " + endpointUrl);
                }
            }
        } else if (renderType == RenderType.DiscordAsset || renderType == RenderType.CustomDiscordAsset) {
            assetUrl = DiscordAssetUtils.getUrl(
                    renderType == RenderType.CustomDiscordAsset ? DiscordAssetUtils.CUSTOM_ASSET_LIST : DiscordAssetUtils.ASSET_LIST,
                    originalName
            );
            if (currentScreen.isDebugMode() && isHovering) {
                hoverText.add(Constants.TRANSLATOR.translate("gui.config.message.editor.url") + " " + assetUrl);
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
                            minecraft, getFontRenderer(), xOffset, yPos + 4, stack,
                            2.0f
                    );
                    xOffset += 35;
                }
            }
        } else if (renderType == RenderType.Placeholder && isHovering) {
            final String placeholderTranslation = String.format("%s.placeholders.%s.description",
                    Constants.MOD_ID,
                    originalName
            );
            final String placeholderUsage = String.format("%s.placeholders.%s.usage",
                    Constants.MOD_ID,
                    originalName
            );
            if (Constants.TRANSLATOR.hasTranslation(placeholderTranslation)) {
                hoverText.add(String.format("%s \"%s\"",
                        Constants.TRANSLATOR.translate("gui.config.message.editor.description"),
                        Constants.TRANSLATOR.translate(placeholderTranslation)
                ));
            }
            if (Constants.TRANSLATOR.hasTranslation(placeholderUsage)) {
                hoverText.add(String.format("%s \"%s\"",
                        Constants.TRANSLATOR.translate("gui.config.message.editor.usage"),
                        Constants.TRANSLATOR.translate(placeholderUsage)
                ));
            }

            final boolean addExtraData = CraftPresence.CONFIG.advancedSettings.allowPlaceholderPreviews;
            if (addExtraData && CraftPresence.CLIENT.isDefaultPlaceholder(originalName.toLowerCase())) {
                final Supplier<Value> suppliedInfo = CraftPresence.CLIENT.getArgument(originalName);

                if (suppliedInfo != null) {
                    final Value rawValue = suppliedInfo.get();
                    final String tagValue = rawValue.toString();
                    if (!rawValue.isNull() && !rawValue.isFunction() && !StringUtils.isNullOrEmpty(tagValue)) {
                        hoverText.add(String.format("%s \"%s\"",
                                Constants.TRANSLATOR.translate("gui.config.message.editor.preview"),
                                (tagValue.length() >= 128) ? "<...>" : tagValue
                        ));
                    }
                }
            }
        }

        if (renderType.canRenderImage() && !ImageUtils.isTextureNull(texture)) {
            final double yOffset = yPos + 4.5;
            final double size = 32;
            RenderUtils.drawTexture(minecraft,
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

        RenderUtils.renderScrollingString(matrices,
                minecraft,
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
        private final String name;

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
            this.name = name;
            this.renderType = renderType;
        }

        /**
         * Renders this Entry to the List
         *
         * @param matrices    The Matrix Stack, used for Rendering
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
        public void render(@Nonnull PoseStack matrices, int index, int yPos, int xPos, int entryWidth, int entryHeight, int mouseX, int mouseY, boolean hovered, float tickDelta) {
            ScrollableListControl.this.renderSlotItem(matrices, name, xPos, yPos, entryWidth, entryHeight, mouseX, mouseY);
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
