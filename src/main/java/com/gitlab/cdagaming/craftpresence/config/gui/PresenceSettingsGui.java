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

package com.gitlab.cdagaming.craftpresence.config.gui;

import com.gitlab.cdagaming.craftpresence.CraftPresence;
import com.gitlab.cdagaming.craftpresence.ModUtils;
import com.gitlab.cdagaming.craftpresence.impl.discord.ArgumentType;
import com.gitlab.cdagaming.craftpresence.utils.StringUtils;
import com.gitlab.cdagaming.craftpresence.utils.discord.DiscordUtils;
import com.gitlab.cdagaming.craftpresence.utils.discord.assets.DiscordAsset;
import com.gitlab.cdagaming.craftpresence.utils.discord.assets.DiscordAssetUtils;
import com.gitlab.cdagaming.craftpresence.utils.gui.controls.ExtendedButtonControl;
import com.gitlab.cdagaming.craftpresence.utils.gui.controls.ExtendedTextControl;
import com.gitlab.cdagaming.craftpresence.utils.gui.controls.ScrollableListControl;
import com.gitlab.cdagaming.craftpresence.utils.gui.impl.DynamicEditorGui;
import com.gitlab.cdagaming.craftpresence.utils.gui.impl.SelectorGui;
import com.gitlab.cdagaming.craftpresence.utils.gui.integrations.PaginatedScreen;
import net.minecraft.client.gui.GuiScreen;

@SuppressWarnings("DuplicatedCode")
public class PresenceSettingsGui extends PaginatedScreen {
    private ExtendedTextControl detailsFormat, gameStateFormat, largeImageFormat, smallImageFormat,
            smallImageKeyFormat, largeImageKeyFormat;
    private ExtendedButtonControl buttonMessagesButton, dynamicIconsButton;

    PresenceSettingsGui(GuiScreen parentScreen) {
        super(parentScreen);
    }

    @Override
    public void initializeUi() {
        final int calc1 = (getScreenWidth() / 2) - 183;
        final int calc2 = (getScreenWidth() / 2) + 3;

        // Page 1 Items
        detailsFormat = addControl(
                new ExtendedTextControl(
                        getFontRenderer(),
                        calc2, CraftPresence.GUIS.getButtonY(1),
                        180, 20
                ), startPage
        );
        gameStateFormat = addControl(
                new ExtendedTextControl(
                        getFontRenderer(),
                        calc2, CraftPresence.GUIS.getButtonY(2),
                        180, 20
                ), startPage
        );
        largeImageFormat = addControl(
                new ExtendedTextControl(
                        getFontRenderer(),
                        calc2, CraftPresence.GUIS.getButtonY(3),
                        180, 20
                ), startPage
        );
        smallImageFormat = addControl(
                new ExtendedTextControl(
                        getFontRenderer(),
                        calc2, CraftPresence.GUIS.getButtonY(4),
                        180, 20
                ), startPage
        );

        detailsFormat.setControlMessage(CraftPresence.CONFIG.detailsMessage);
        gameStateFormat.setControlMessage(CraftPresence.CONFIG.gameStateMessage);
        largeImageFormat.setControlMessage(CraftPresence.CONFIG.largeImageMessage);
        smallImageFormat.setControlMessage(CraftPresence.CONFIG.smallImageMessage);

        // Page 2 Items
        smallImageKeyFormat = addControl(
                new ExtendedTextControl(
                        getFontRenderer(),
                        calc2, CraftPresence.GUIS.getButtonY(1),
                        180, 20
                ), startPage + 1
        );
        largeImageKeyFormat = addControl(
                new ExtendedTextControl(
                        getFontRenderer(),
                        calc2, CraftPresence.GUIS.getButtonY(2),
                        180, 20
                ), startPage + 1
        );

        smallImageKeyFormat.setControlMessage(CraftPresence.CONFIG.smallImageKey);
        largeImageKeyFormat.setControlMessage(CraftPresence.CONFIG.largeImageKey);

        // Button Messages Button
        buttonMessagesButton = addControl(
                new ExtendedButtonControl(
                        calc1, CraftPresence.GUIS.getButtonY(3),
                        180, 20,
                        "gui.config.name.display.button_messages",
                        () -> CraftPresence.GUIS.openScreen(
                                new SelectorGui(
                                        currentScreen,
                                        ModUtils.TRANSLATOR.translate("gui.config.title.selector.button"), CraftPresence.CLIENT.createButtonsList(),
                                        null, null,
                                        true, true, ScrollableListControl.RenderType.None,
                                        null,
                                        (currentValue, parentScreen) -> {
                                            // Event to occur when Setting Dynamic/Specific Data
                                            CraftPresence.GUIS.openScreen(
                                                    new DynamicEditorGui(
                                                            parentScreen, currentValue,
                                                            (attributeName, screenInstance) -> {
                                                                // Event to occur when initializing new data
                                                                screenInstance.attributeName = "button_" + CraftPresence.CLIENT.createButtonsList().size();
                                                                screenInstance.mainTitle = ModUtils.TRANSLATOR.translate("gui.config.title.editor.add.new.prefilled", screenInstance.attributeName);
                                                                screenInstance.primaryText = ModUtils.TRANSLATOR.translate("gui.config.message.editor.label");
                                                                screenInstance.secondaryText = ModUtils.TRANSLATOR.translate("gui.config.message.editor.url");
                                                                screenInstance.primaryMessage = screenInstance.originalPrimaryMessage = StringUtils.getConfigPart(CraftPresence.CONFIG.buttonMessages, "default", 0, 1, CraftPresence.CONFIG.splitCharacter, null);
                                                                screenInstance.secondaryMessage = screenInstance.originalSecondaryMessage = StringUtils.getConfigPart(CraftPresence.CONFIG.buttonMessages, "default", 0, 2, CraftPresence.CONFIG.splitCharacter, null);
                                                            },
                                                            (attributeName, screenInstance) -> {
                                                                // Event to occur when initializing existing data
                                                                screenInstance.primaryText = ModUtils.TRANSLATOR.translate("gui.config.message.editor.label");
                                                                screenInstance.secondaryText = ModUtils.TRANSLATOR.translate("gui.config.message.editor.url");
                                                                screenInstance.overrideSecondaryRender = true;
                                                                screenInstance.mainTitle = ModUtils.TRANSLATOR.translate("gui.config.title.display.edit_specific_button", attributeName);
                                                                screenInstance.originalPrimaryMessage = StringUtils.getConfigPart(CraftPresence.CONFIG.buttonMessages, "default", 0, 1, CraftPresence.CONFIG.splitCharacter, null);
                                                                screenInstance.originalSecondaryMessage = StringUtils.getConfigPart(CraftPresence.CONFIG.buttonMessages, "default", 0, 2, CraftPresence.CONFIG.splitCharacter, null);
                                                                screenInstance.primaryMessage = StringUtils.getConfigPart(CraftPresence.CONFIG.buttonMessages, attributeName, 0, 1, CraftPresence.CONFIG.splitCharacter, screenInstance.originalPrimaryMessage);
                                                                screenInstance.secondaryMessage = StringUtils.getConfigPart(CraftPresence.CONFIG.buttonMessages, attributeName, 0, 2, CraftPresence.CONFIG.splitCharacter, screenInstance.originalSecondaryMessage);
                                                            },
                                                            (screenInstance, secondaryText, inputText) -> {
                                                                // Event to occur when adjusting set data
                                                                CraftPresence.CONFIG.hasChanged = true;
                                                                CraftPresence.CONFIG.buttonMessages = StringUtils.setConfigPart(CraftPresence.CONFIG.buttonMessages, screenInstance.attributeName, 0, 1, CraftPresence.CONFIG.splitCharacter, inputText);
                                                                CraftPresence.CONFIG.buttonMessages = StringUtils.setConfigPart(CraftPresence.CONFIG.buttonMessages, screenInstance.attributeName, 0, 2, CraftPresence.CONFIG.splitCharacter, secondaryText);
                                                            },
                                                            (screenInstance, secondaryText, inputText) -> {
                                                                // Event to occur when removing set data
                                                                CraftPresence.CONFIG.hasChanged = true;
                                                                CraftPresence.CONFIG.buttonMessages = StringUtils.removeFromArray(CraftPresence.CONFIG.buttonMessages, screenInstance.attributeName, 0, CraftPresence.CONFIG.splitCharacter);
                                                            }, null,
                                                            (attributeName, screenInstance) -> {
                                                                // Event to occur when Hovering over Primary Label
                                                                CraftPresence.GUIS.drawMultiLineString(
                                                                        StringUtils.splitTextByNewLine(
                                                                                ModUtils.TRANSLATOR.translate("gui.config.comment.display.button_messages")
                                                                        ),
                                                                        screenInstance.getMouseX(), screenInstance.getMouseY(),
                                                                        screenInstance.getScreenWidth(), screenInstance.getScreenHeight(),
                                                                        screenInstance.getWrapWidth(),
                                                                        screenInstance.getFontRenderer(),
                                                                        true
                                                                );
                                                            },
                                                            (attributeName, screenInstance) -> {
                                                                // Event to occur when Hovering over Secondary Label
                                                                CraftPresence.GUIS.drawMultiLineString(
                                                                        StringUtils.splitTextByNewLine(
                                                                                ModUtils.TRANSLATOR.translate("gui.config.comment.display.button_messages")
                                                                        ),
                                                                        screenInstance.getMouseX(), screenInstance.getMouseY(),
                                                                        screenInstance.getScreenWidth(), screenInstance.getScreenHeight(),
                                                                        screenInstance.getWrapWidth(),
                                                                        screenInstance.getFontRenderer(),
                                                                        true
                                                                );
                                                            }
                                                    )
                                            );
                                        }
                                )
                        )
                ), startPage + 1
        );

        // Dynamic Icons Button
        dynamicIconsButton = addControl(
                new ExtendedButtonControl(
                        calc2, CraftPresence.GUIS.getButtonY(3),
                        180, 20,
                        "gui.config.name.display.dynamic_icons",
                        () -> CraftPresence.GUIS.openScreen(
                                new SelectorGui(
                                        currentScreen,
                                        ModUtils.TRANSLATOR.translate("gui.config.title.selector.icon"), DiscordAssetUtils.CUSTOM_ASSET_LIST.keySet(),
                                        null, null,
                                        true, true, ScrollableListControl.RenderType.CustomDiscordAsset,
                                        null,
                                        (currentValue, parentScreen) -> {
                                            // Event to occur when Setting Dynamic/Specific Data
                                            CraftPresence.GUIS.openScreen(
                                                    new DynamicEditorGui(
                                                            parentScreen, currentValue,
                                                            (attributeName, screenInstance) -> {
                                                                // Event to occur when initializing new data
                                                                screenInstance.primaryText = ModUtils.TRANSLATOR.translate("gui.config.message.editor.url");
                                                                screenInstance.maxPrimaryLength = 32767;
                                                                screenInstance.secondaryText = ModUtils.TRANSLATOR.translate("gui.config.message.editor.label");
                                                                screenInstance.maxSecondaryLength = 32;
                                                                screenInstance.primaryMessage = screenInstance.originalPrimaryMessage = StringUtils.getConfigPart(CraftPresence.CONFIG.dynamicIcons, "default", 0, 1, CraftPresence.CONFIG.splitCharacter, null);
                                                            },
                                                            (attributeName, screenInstance) -> {
                                                                // Event to occur when initializing existing data
                                                                screenInstance.primaryText = ModUtils.TRANSLATOR.translate("gui.config.message.editor.url");
                                                                screenInstance.maxPrimaryLength = 32767;
                                                                screenInstance.secondaryText = ModUtils.TRANSLATOR.translate("gui.config.message.editor.label");
                                                                screenInstance.maxSecondaryLength = 32;
                                                                screenInstance.mainTitle = ModUtils.TRANSLATOR.translate("gui.config.title.display.edit_specific_icon", attributeName);
                                                                screenInstance.originalPrimaryMessage = StringUtils.getConfigPart(CraftPresence.CONFIG.dynamicIcons, "default", 0, 1, CraftPresence.CONFIG.splitCharacter, null);
                                                                screenInstance.primaryMessage = StringUtils.getConfigPart(CraftPresence.CONFIG.dynamicIcons, attributeName, 0, 1, CraftPresence.CONFIG.splitCharacter, screenInstance.originalPrimaryMessage);
                                                            },
                                                            (screenInstance, attributeName, inputText) -> {
                                                                // Event to occur when adjusting set data
                                                                CraftPresence.CONFIG.hasChanged = true;
                                                                CraftPresence.CONFIG.hasClientPropertiesChanged = true;
                                                                CraftPresence.CONFIG.flushClientProperties = true;
                                                                CraftPresence.CONFIG.dynamicIcons = StringUtils.setConfigPart(CraftPresence.CONFIG.dynamicIcons, attributeName, 0, 1, CraftPresence.CONFIG.splitCharacter, inputText);
                                                                final DiscordAsset asset = new DiscordAsset()
                                                                        .setName(attributeName)
                                                                        .setUrl(inputText)
                                                                        .setType(DiscordAsset.AssetType.CUSTOM);
                                                                if (!DiscordAssetUtils.CUSTOM_ASSET_LIST.containsKey(asset.getName())) {
                                                                    DiscordAssetUtils.CUSTOM_ASSET_LIST.put(asset.getName(), asset);
                                                                }
                                                                // If a Discord Icon exists with the same name, give priority to the custom one
                                                                // Unless the icon is the default template, in which we don't add it at all
                                                                if (!asset.getName().equalsIgnoreCase("default")) {
                                                                    DiscordAssetUtils.ASSET_LIST.put(asset.getName(), asset);
                                                                }
                                                            },
                                                            (screenInstance, attributeName, inputText) -> {
                                                                // Event to occur when removing set data
                                                                CraftPresence.CONFIG.hasChanged = true;
                                                                CraftPresence.CONFIG.hasClientPropertiesChanged = true;
                                                                CraftPresence.CONFIG.flushClientProperties = true;
                                                                CraftPresence.CONFIG.dynamicIcons = StringUtils.removeFromArray(CraftPresence.CONFIG.dynamicIcons, attributeName, 0, CraftPresence.CONFIG.splitCharacter);
                                                                if (DiscordAssetUtils.CUSTOM_ASSET_LIST.containsKey(attributeName)) {
                                                                    DiscordAssetUtils.CUSTOM_ASSET_LIST.remove(attributeName);
                                                                    if (!attributeName.equalsIgnoreCase("default")) {
                                                                        DiscordAssetUtils.ASSET_LIST.remove(attributeName);
                                                                    }
                                                                }
                                                            }, null,
                                                            (attributeName, screenInstance) -> {
                                                                // Event to occur when Hovering over Primary Label
                                                                CraftPresence.GUIS.drawMultiLineString(
                                                                        StringUtils.splitTextByNewLine(
                                                                                ModUtils.TRANSLATOR.translate("gui.config.comment.display.dynamic_icons")
                                                                        ),
                                                                        screenInstance.getMouseX(), screenInstance.getMouseY(),
                                                                        screenInstance.getScreenWidth(), screenInstance.getScreenHeight(),
                                                                        screenInstance.getWrapWidth(),
                                                                        screenInstance.getFontRenderer(),
                                                                        true
                                                                );
                                                            },
                                                            (attributeName, screenInstance) -> {
                                                                // Event to occur when Hovering over Secondary Label
                                                                CraftPresence.GUIS.drawMultiLineString(
                                                                        StringUtils.splitTextByNewLine(
                                                                                ModUtils.TRANSLATOR.translate("gui.config.comment.display.dynamic_icons")
                                                                        ),
                                                                        screenInstance.getMouseX(), screenInstance.getMouseY(),
                                                                        screenInstance.getScreenWidth(), screenInstance.getScreenHeight(),
                                                                        screenInstance.getWrapWidth(),
                                                                        screenInstance.getFontRenderer(),
                                                                        true
                                                                );
                                                            }
                                                    )
                                            );
                                        }
                                )
                        )
                ), startPage + 1
        );

        super.initializeUi();

        backButton.setOnClick(
                () -> {
                    if (!detailsFormat.getControlMessage().equals(CraftPresence.CONFIG.detailsMessage)) {
                        CraftPresence.CONFIG.hasChanged = true;
                        CraftPresence.CONFIG.hasClientPropertiesChanged = true;
                        CraftPresence.CONFIG.detailsMessage = detailsFormat.getControlMessage();
                    }
                    if (!gameStateFormat.getControlMessage().equals(CraftPresence.CONFIG.gameStateMessage)) {
                        CraftPresence.CONFIG.hasChanged = true;
                        CraftPresence.CONFIG.hasClientPropertiesChanged = true;
                        CraftPresence.CONFIG.gameStateMessage = gameStateFormat.getControlMessage();
                    }
                    if (!largeImageFormat.getControlMessage().equals(CraftPresence.CONFIG.largeImageMessage)) {
                        CraftPresence.CONFIG.hasChanged = true;
                        CraftPresence.CONFIG.hasClientPropertiesChanged = true;
                        CraftPresence.CONFIG.largeImageMessage = largeImageFormat.getControlMessage();
                    }
                    if (!smallImageFormat.getControlMessage().equals(CraftPresence.CONFIG.smallImageMessage)) {
                        CraftPresence.CONFIG.hasChanged = true;
                        CraftPresence.CONFIG.hasClientPropertiesChanged = true;
                        CraftPresence.CONFIG.smallImageMessage = smallImageFormat.getControlMessage();
                    }
                    if (!largeImageKeyFormat.getControlMessage().equals(CraftPresence.CONFIG.largeImageKey)) {
                        CraftPresence.CONFIG.hasChanged = true;
                        CraftPresence.CONFIG.hasClientPropertiesChanged = true;
                        CraftPresence.CONFIG.largeImageKey = largeImageKeyFormat.getControlMessage();
                    }
                    if (!smallImageKeyFormat.getControlMessage().equals(CraftPresence.CONFIG.smallImageKey)) {
                        CraftPresence.CONFIG.hasChanged = true;
                        CraftPresence.CONFIG.hasClientPropertiesChanged = true;
                        CraftPresence.CONFIG.smallImageKey = smallImageKeyFormat.getControlMessage();
                    }
                    CraftPresence.GUIS.openScreen(parentScreen);
                }
        );
    }

    @Override
    public void preRender() {
        final String mainTitle = ModUtils.TRANSLATOR.translate("gui.config.title");
        final String subTitle = ModUtils.TRANSLATOR.translate("gui.config.title.presence_settings");
        final String detailsFormatTitle = ModUtils.TRANSLATOR.translate("gui.config.name.display.details_message");
        final String gameStateFormatTitle = ModUtils.TRANSLATOR.translate("gui.config.name.display.game_state_message");
        final String largeImageFormatTitle = ModUtils.TRANSLATOR.translate("gui.config.name.display.large_image_message");
        final String smallImageFormatTitle = ModUtils.TRANSLATOR.translate("gui.config.name.display.small_image_message");
        final String smallImageKeyFormatTitle = ModUtils.TRANSLATOR.translate("gui.config.name.display.small_image_key");
        final String largeImageKeyFormatTitle = ModUtils.TRANSLATOR.translate("gui.config.name.display.large_image_key");

        renderString(mainTitle, (getScreenWidth() / 2f) - (getStringWidth(mainTitle) / 2f), 10, 0xFFFFFF);
        renderString(subTitle, (getScreenWidth() / 2f) - (getStringWidth(subTitle) / 2f), 20, 0xFFFFFF);

        renderString(detailsFormatTitle, (getScreenWidth() / 2f) - 160, CraftPresence.GUIS.getButtonY(1, 5), 0xFFFFFF, startPage);
        renderString(gameStateFormatTitle, (getScreenWidth() / 2f) - 160, CraftPresence.GUIS.getButtonY(2, 5), 0xFFFFFF, startPage);
        renderString(largeImageFormatTitle, (getScreenWidth() / 2f) - 160, CraftPresence.GUIS.getButtonY(3, 5), 0xFFFFFF, startPage);
        renderString(smallImageFormatTitle, (getScreenWidth() / 2f) - 160, CraftPresence.GUIS.getButtonY(4, 5), 0xFFFFFF, startPage);

        renderString(smallImageKeyFormatTitle, (getScreenWidth() / 2f) - 160, CraftPresence.GUIS.getButtonY(1, 5), 0xFFFFFF, startPage + 1);
        renderString(largeImageKeyFormatTitle, (getScreenWidth() / 2f) - 160, CraftPresence.GUIS.getButtonY(2, 5), 0xFFFFFF, startPage + 1);

        super.preRender();
    }

    @Override
    public void postRender() {
        final String detailsFormatTitle = ModUtils.TRANSLATOR.translate("gui.config.name.display.details_message");
        final String gameStateFormatTitle = ModUtils.TRANSLATOR.translate("gui.config.name.display.game_state_message");
        final String largeImageFormatTitle = ModUtils.TRANSLATOR.translate("gui.config.name.display.large_image_message");
        final String smallImageFormatTitle = ModUtils.TRANSLATOR.translate("gui.config.name.display.small_image_message");
        final String smallImageKeyFormatTitle = ModUtils.TRANSLATOR.translate("gui.config.name.display.small_image_key");
        final String largeImageKeyFormatTitle = ModUtils.TRANSLATOR.translate("gui.config.name.display.large_image_key");
        if (currentPage == startPage) {
            final boolean hoveringOverDetails = CraftPresence.GUIS.isMouseOver(getMouseX(), getMouseY(), (getScreenWidth() / 2f) - 160, CraftPresence.GUIS.getButtonY(1, 5), getStringWidth(detailsFormatTitle), getFontHeight());
            final boolean hoveringOverGameState = CraftPresence.GUIS.isMouseOver(getMouseX(), getMouseY(), (getScreenWidth() / 2f) - 160, CraftPresence.GUIS.getButtonY(2, 5), getStringWidth(gameStateFormatTitle), getFontHeight());
            final boolean hoveringOverLargeImageFormat = CraftPresence.GUIS.isMouseOver(getMouseX(), getMouseY(), (getScreenWidth() / 2f) - 160, CraftPresence.GUIS.getButtonY(3, 5), getStringWidth(largeImageFormatTitle), getFontHeight());
            final boolean hoveringOverSmallImageFormat = CraftPresence.GUIS.isMouseOver(getMouseX(), getMouseY(), (getScreenWidth() / 2f) - 160, CraftPresence.GUIS.getButtonY(4, 5), getStringWidth(smallImageFormatTitle), getFontHeight());

            if (hoveringOverDetails || hoveringOverGameState || hoveringOverLargeImageFormat || hoveringOverSmallImageFormat) {
                CraftPresence.GUIS.drawMultiLineString(
                        StringUtils.splitTextByNewLine(
                                ModUtils.TRANSLATOR.translate("gui.config.message.presence.generalArgs",
                                        CraftPresence.CLIENT.generateArgumentMessage(
                                                null,
                                                ArgumentType.Text, DiscordUtils.textModules
                                        ))
                        ),
                        getMouseX(), getMouseY(),
                        getScreenWidth(), getScreenHeight(),
                        getWrapWidth(),
                        getFontRenderer(),
                        true
                );
            }
        }

        if (currentPage == (startPage + 1)) {
            final boolean hoveringOverSmallImageFormat = CraftPresence.GUIS.isMouseOver(getMouseX(), getMouseY(), (getScreenWidth() / 2f) - 160, CraftPresence.GUIS.getButtonY(1, 5), getStringWidth(smallImageKeyFormatTitle), getFontHeight());
            final boolean hoveringOverLargeImageFormat = CraftPresence.GUIS.isMouseOver(getMouseX(), getMouseY(), (getScreenWidth() / 2f) - 160, CraftPresence.GUIS.getButtonY(2, 5), getStringWidth(largeImageKeyFormatTitle), getFontHeight());
            if (hoveringOverSmallImageFormat || hoveringOverLargeImageFormat) {
                CraftPresence.GUIS.drawMultiLineString(
                        StringUtils.splitTextByNewLine(
                                ModUtils.TRANSLATOR.translate("gui.config.message.presence.iconArgs",
                                        CraftPresence.CLIENT.generateArgumentMessage(
                                                null,
                                                ArgumentType.Image, DiscordUtils.iconModules
                                        ))
                        ),
                        getMouseX(), getMouseY(),
                        getScreenWidth(), getScreenHeight(),
                        getWrapWidth(),
                        getFontRenderer(),
                        true
                );
            }
            // Hovering over Button Messages Button
            if (CraftPresence.GUIS.isMouseOver(getMouseX(), getMouseY(), buttonMessagesButton)) {
                CraftPresence.GUIS.drawMultiLineString(
                        StringUtils.splitTextByNewLine(
                                ModUtils.TRANSLATOR.translate("gui.config.comment.display.button_messages")
                        ),
                        getMouseX(), getMouseY(),
                        getScreenWidth(), getScreenHeight(),
                        getWrapWidth(),
                        getFontRenderer(),
                        true
                );
            }
            // Hovering over Dynamic Icons Button
            if (CraftPresence.GUIS.isMouseOver(getMouseX(), getMouseY(), dynamicIconsButton)) {
                CraftPresence.GUIS.drawMultiLineString(
                        StringUtils.splitTextByNewLine(
                                ModUtils.TRANSLATOR.translate("gui.config.comment.display.dynamic_icons")
                        ),
                        getMouseX(), getMouseY(),
                        getScreenWidth(), getScreenHeight(),
                        getWrapWidth(),
                        getFontRenderer(),
                        true
                );
            }
        }
    }
}
