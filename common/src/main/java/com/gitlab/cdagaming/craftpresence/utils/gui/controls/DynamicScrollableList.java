/*
 * MIT License
 *
 * Copyright (c) 2018 - 2025 CDAGaming (cstack2011@yahoo.com)
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
import com.gitlab.cdagaming.craftpresence.core.integrations.discord.assets.DiscordAssetUtils;
import com.gitlab.cdagaming.craftpresence.utils.CommandUtils;
import com.gitlab.cdagaming.unilib.utils.ImageUtils;
import com.gitlab.cdagaming.unilib.utils.ItemUtils;
import com.gitlab.cdagaming.unilib.utils.ResourceUtils;
import com.gitlab.cdagaming.unilib.utils.gui.RenderUtils;
import com.gitlab.cdagaming.unilib.utils.gui.controls.ScrollableListControl;
import com.gitlab.cdagaming.unilib.utils.gui.integrations.ExtendedScreen;
import io.github.cdagaming.unicore.impl.Pair;
import io.github.cdagaming.unicore.utils.MappingUtils;
import io.github.cdagaming.unicore.utils.StringUtils;
import net.minecraft.client.Minecraft;
import net.minecraft.common.item.ItemStack;
import unilib.external.io.github.classgraph.ClassInfo;

import javax.annotation.Nonnull;
import java.awt.*;
import java.util.List;
import java.util.Map;

/**
 * Gui Widget for a Dynamic Scrollable List
 *
 * @author CDAGaming
 */
public class DynamicScrollableList extends ScrollableListControl {
    /**
     * The Rendering Type to render the slots in
     */
    private final RenderType renderType;
    /**
     * The Current Hover Text that should be displayed
     */
    public List<String> currentHoverText = StringUtils.newArrayList();
    /**
     * Mapping representing a link between the entries original name, and it's display name
     */
    private Map<String, String> entryAliases;
    /**
     * The Identifier Type, normally related to the Render Type
     */
    private IdentifierType identifierType = IdentifierType.None;

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
    public DynamicScrollableList(@Nonnull final Minecraft mc, final ExtendedScreen currentScreen, final int width, final int height, final int topIn, final int bottomIn, final int slotHeightIn, final List<String> itemList, final String currentValue) {
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
    public DynamicScrollableList(@Nonnull final Minecraft mc, final ExtendedScreen currentScreen, final int width, final int height, final int topIn, final int bottomIn, final int slotHeightIn, final List<String> itemList, final String currentValue, final RenderType renderType) {
        super(mc, currentScreen, width, height, topIn, bottomIn, slotHeightIn, itemList, currentValue);
        this.renderType = renderType;
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
    public DynamicScrollableList(@Nonnull final Minecraft mc, final ExtendedScreen currentScreen, final int width, final int height, final int topIn, final int bottomIn, final List<String> itemList, final String currentValue, final RenderType renderType) {
        this(
                mc,
                currentScreen,
                width, height,
                topIn, bottomIn,
                renderType.canRenderImage() ? 45 : ScrollableListControl.DEFAULT_SLOT_HEIGHT,
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
    public DynamicScrollableList setIdentifierType(final IdentifierType type) {
        this.identifierType = type;
        return this;
    }

    /**
     * Mapping representing a link between the entries original name, and it's display name
     *
     * @return the Mapping representing a link between the entries original name, and it's display name
     */
    public Map<String, String> getEntryAliases() {
        if (entryAliases == null) {
            entryAliases = StringUtils.newHashMap();
        }
        return entryAliases;
    }

    /**
     * Setup Mappings between the entries original name, and it's entry variant
     */
    public void syncEntries() {
        getEntryAliases().clear();
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
                getEntryAliases().put(originalName, displayName);
            }

            final StringEntry dataEntry = new StringEntry(originalName);
            addEntry(dataEntry);
            if (originalName.equals(currentValue)) {
                setSelected(dataEntry);
            }
        }

        if (getSelected() != null) {
            centerScrollOn(getSelected());
        }
    }

    @Override
    public void renderSlotItem(final String originalName, final int xPos, final int yPos, final int widthIn, final int heightIn, final float mouseXIn, final float mouseYIn, final boolean isHovering, final float partialTicks) {
        final List<String> hoverText = StringUtils.newArrayList();
        String displayName = getEntryAliases().getOrDefault(originalName, originalName);
        int xOffset = xPos;

        String texture = ResourceUtils.getEmptyResource();
        String assetUrl;

        if (renderType == RenderType.ServerData) {
            // Note: ServerData Base64 unavailable in MC 1.6.4 and below
            /*final ServerData data = CraftPresence.SERVER.getDataFromName(originalName);

            if (data != null && !StringUtils.isNullOrEmpty(data.getBase64EncodedIconData())) {
                assetUrl = "data:image/png;base64," + data.getBase64EncodedIconData();
                texture = ImageUtils.getTextureFromUrl(getGameInstance(), originalName, new Pair<>(ImageUtils.InputType.ByteStream, assetUrl));
            } else */if (CraftPresence.CONFIG.advancedSettings.allowEndpointIcons &&
                    !StringUtils.isNullOrEmpty(CraftPresence.CONFIG.advancedSettings.serverIconEndpoint)) {
                final String formattedIP = originalName.contains(":") ? StringUtils.formatAddress(originalName, false) : originalName;
                final String endpointUrl = CraftPresence.CLIENT.compileData(String.format(
                                CraftPresence.CONFIG.advancedSettings.serverIconEndpoint,
                                originalName
                        ),
                        new Pair<>("server.address.short", () -> formattedIP),
                        new Pair<>("server.address.full", () -> originalName)
                ).get().toString();
                texture = ImageUtils.getTextureFromUrl(getGameInstance(), originalName, endpointUrl);
                if (CommandUtils.isDebugMode() && isHovering) {
                    hoverText.add(Constants.TRANSLATOR.translate("gui.config.message.editor.url") + " " + endpointUrl);
                }
            }
        } else if (renderType == RenderType.DiscordAsset || renderType == RenderType.CustomDiscordAsset) {
            assetUrl = DiscordAssetUtils.getUrl(
                    renderType == RenderType.CustomDiscordAsset ? DiscordAssetUtils.CUSTOM_ASSET_LIST : DiscordAssetUtils.ASSET_LIST,
                    originalName,
                    CraftPresence.CLIENT::getResult
            );
            if (CommandUtils.isDebugMode() && isHovering) {
                hoverText.add(Constants.TRANSLATOR.translate("gui.config.message.editor.url") + " " + assetUrl);
            }
            texture = ImageUtils.getTextureFromUrl(getGameInstance(), originalName, assetUrl);
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
                texture = ImageUtils.getTextureFromUrl(getGameInstance(), originalName, endpointUrl);
                if (CommandUtils.isDebugMode() && isHovering) {
                    hoverText.add(Constants.TRANSLATOR.translate("gui.config.message.editor.url") + " " + endpointUrl);
                }
            }
        } else if (renderType == RenderType.ItemData && renderType.canRenderImage()) {
            final Map<String, ItemStack> data = CraftPresence.TILE_ENTITIES.TILE_ENTITY_RESOURCES;
            if (data.containsKey(originalName)) {
                final ItemStack stack = data.get(originalName);
                if (!ItemUtils.isItemEmpty(stack)) {
                    RenderUtils.drawItemStack(
                            getGameInstance(), getFontRenderer(), xOffset, yPos + 4, stack,
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

        if (renderType.canRenderImage() && ResourceUtils.isValidResource(texture)) {
            final double yOffset = yPos + 4.5;
            final double size = 32;
            RenderUtils.drawTexture(getGameInstance(),
                    xOffset, xOffset + size, yOffset, yOffset + size,
                    0.0D,
                    0.0D, 1.0D,
                    0.0D, 1.0D,
                    Color.white, Color.white,
                    texture
            );
            if (CommandUtils.isDebugMode() && isHovering) {
                hoverText.add(Constants.TRANSLATOR.translate("gui.config.message.editor.texture_path") + " " + texture);
            }
            // Note: 35 Added to xOffset to accommodate for Image Size
            xOffset += 35;
        }

        final String identifierName = identifierType.getIdentifier(originalName);
        if (!identifierName.equals(displayName) && isHovering) {
            hoverText.add(Constants.TRANSLATOR.translate("gui.config.message.editor.original") + " " + identifierName);
        }

        super.renderSlotItem(displayName, xOffset, yPos, widthIn, heightIn, mouseXIn, mouseYIn, isHovering, partialTicks);

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
