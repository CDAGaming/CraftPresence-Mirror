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
import com.gitlab.cdagaming.craftpresence.config.Config;
import com.gitlab.cdagaming.craftpresence.config.category.Status;
import com.gitlab.cdagaming.craftpresence.config.element.ModuleData;
import com.gitlab.cdagaming.craftpresence.config.element.PresenceData;
import com.gitlab.cdagaming.craftpresence.impl.Pair;
import com.gitlab.cdagaming.craftpresence.impl.discord.ArgumentType;
import com.gitlab.cdagaming.craftpresence.utils.StringUtils;
import com.gitlab.cdagaming.craftpresence.utils.discord.assets.DiscordAssetUtils;
import com.gitlab.cdagaming.craftpresence.utils.gui.controls.ExtendedButtonControl;
import com.gitlab.cdagaming.craftpresence.utils.gui.controls.ExtendedTextControl;
import com.gitlab.cdagaming.craftpresence.utils.gui.controls.ScrollableListControl;
import com.gitlab.cdagaming.craftpresence.utils.gui.impl.DynamicEditorGui;
import com.gitlab.cdagaming.craftpresence.utils.gui.impl.SelectorGui;
import com.gitlab.cdagaming.craftpresence.utils.gui.integrations.PaginatedScreen;
import com.google.common.collect.ImmutableMap;
import net.minecraft.client.gui.GuiScreen;

import java.util.Map;

@SuppressWarnings("DuplicatedCode")
public class StatusMessagesGui extends PaginatedScreen {
    private final Status CONFIG;
    private ExtendedTextControl outerPlayerMessage, innerPlayerMessage, playerCoordsMessage, playerHealthMessage,
            playerAmountMessage, playerItemsMessage, worldMessage, modsMessage, viveCraftMessage, fallbackPackPlaceholderMessage;
    // nameTranslation, [configPath,commentTranslation]
    private final Map<String, Pair<String, Runnable>> eventMappings = ImmutableMap.<String, Pair<String, Runnable>>builder()
            .put("gui.config.name.status_messages.main_menu_message", new Pair<>(
                    "mainMenuData", () -> CraftPresence.GUIS.drawMultiLineString(
                    StringUtils.splitTextByNewLine(
                            ModUtils.TRANSLATOR.translate("gui.config.comment.status_messages.main_menu_message",
                                    CraftPresence.CLIENT.generateArgumentMessage(null, null,
                                            CraftPresence.CLIENT.generalArgs
                                    ))
                    ), this, true
            )
            ))
            .put("gui.config.name.status_messages.loading_message", new Pair<>(
                    "loadingData", () -> CraftPresence.GUIS.drawMultiLineString(
                    StringUtils.splitTextByNewLine(
                            ModUtils.TRANSLATOR.translate("gui.config.comment.status_messages.loading_message",
                                    CraftPresence.CLIENT.generateArgumentMessage(null, null,
                                            CraftPresence.CLIENT.generalArgs
                                    ))
                    ), this, true
            )
            ))
            .put("gui.config.name.status_messages.lan_message", new Pair<>(
                    "lanData", () -> CraftPresence.GUIS.drawMultiLineString(
                    StringUtils.splitTextByNewLine(
                            ModUtils.TRANSLATOR.translate("gui.config.comment.status_messages.lan_message",
                                    CraftPresence.SERVER.generateArgumentMessage())
                    ), this, true
            )
            ))
            .put("gui.config.name.status_messages.single_player_message", new Pair<>(
                    "singleplayerData", () -> CraftPresence.GUIS.drawMultiLineString(
                    StringUtils.splitTextByNewLine(
                            ModUtils.TRANSLATOR.translate("gui.config.comment.status_messages.single_player_message",
                                    CraftPresence.SERVER.generateArgumentMessage())
                    ), this, true
            )
            ))
            .put("gui.config.name.status_messages.placeholder.pack_message", new Pair<>(
                    "packData", () -> CraftPresence.GUIS.drawMultiLineString(
                    StringUtils.splitTextByNewLine(
                            ModUtils.TRANSLATOR.translate("gui.config.comment.status_messages.placeholder.pack_message",
                                    CraftPresence.CLIENT.generateArgumentMessage(
                                            "&PACK&", "&PACK:",
                                            ArgumentType.Text, "&PACK:"
                                    ))
                    ), this, true
            )
            ))
            .build();

    StatusMessagesGui(GuiScreen parentScreen) {
        super(parentScreen);
        CONFIG = CraftPresence.CONFIG.statusMessages;
    }

    @Override
    public void initializeUi() {
        // Page 1 Items
        final int calc1 = (getScreenWidth() / 2) - 183;
        final int calc2 = (getScreenWidth() / 2) + 3;
        final int midCalc = (getScreenWidth() / 2) - 90;

        int buttonRow = 1, index = 1;
        for (Map.Entry<String, Pair<String, Runnable>> entry : eventMappings.entrySet()) {
            final boolean isEven = (index % 2 == 0);
            int startX;
            if (index >= eventMappings.size()) {
                startX = midCalc;
            } else {
                startX = isEven ? calc2 : calc1;
            }

            addControl(
                    new ExtendedButtonControl(
                            startX, CraftPresence.GUIS.getButtonY(buttonRow),
                            180, 20,
                            entry.getKey(),
                            () -> CraftPresence.GUIS.openScreen(
                                    new DynamicEditorGui(
                                            currentScreen, entry.getValue().getFirst(),
                                            (attributeName, screenInstance) -> {
                                                // Event to occur when initializing new data
                                                screenInstance.defaultData = (ModuleData) CONFIG.getDefaults().getProperty(attributeName);
                                                screenInstance.primaryMessage = screenInstance.originalPrimaryMessage = Config.getProperty(screenInstance.defaultData, "textOverride") != null ? screenInstance.defaultData.getTextOverride() : "";
                                                screenInstance.resetText = "gui.config.message.button.reset";
                                            },
                                            (attributeName, screenInstance) -> {
                                                // Event to occur when initializing existing data
                                                screenInstance.defaultData = (ModuleData) CONFIG.getDefaults().getProperty(attributeName);
                                                screenInstance.currentData = (ModuleData) CONFIG.getProperty(attributeName);
                                                screenInstance.isPreliminaryData = screenInstance.currentData == null;
                                                screenInstance.mainTitle = ModUtils.TRANSLATOR.translate("gui.config.title.gui.edit_specific_gui", attributeName);
                                                screenInstance.originalPrimaryMessage = Config.getProperty(screenInstance.defaultData, "textOverride") != null ? screenInstance.defaultData.getTextOverride() : "";
                                                screenInstance.primaryMessage = Config.getProperty(screenInstance.currentData, "textOverride") != null ? screenInstance.currentData.getTextOverride() : screenInstance.originalPrimaryMessage;
                                                screenInstance.resetText = "gui.config.message.button.reset";
                                            },
                                            (screenInstance, attributeName, inputText) -> {
                                                // Event to occur when adjusting set data
                                                screenInstance.currentData.setTextOverride(inputText);
                                                CraftPresence.CONFIG.hasChanged = true;
                                                CONFIG.setProperty(attributeName, screenInstance.currentData);
                                                if (!CraftPresence.GUIS.GUI_NAMES.contains(attributeName)) {
                                                    CraftPresence.GUIS.GUI_NAMES.add(attributeName);
                                                }
                                            },
                                            (screenInstance, attributeName, inputText) -> {
                                                // Event to occur when removing set data
                                                CraftPresence.CONFIG.hasChanged = true;
                                                CONFIG.resetProperty(attributeName);
                                            },
                                            (attributeName, screenInstance, isPresenceButton) -> {
                                                // Event to occur when adding specific info to set data
                                                if (isPresenceButton) {
                                                    final PresenceData defaultPresenceData = Config.getProperty(screenInstance.defaultData, "data") != null ? screenInstance.defaultData.getData() : new PresenceData();
                                                    final PresenceData currentPresenceData = Config.getProperty(screenInstance.currentData, "data") != null ? screenInstance.currentData.getData() : defaultPresenceData;
                                                    CraftPresence.GUIS.openScreen(
                                                            new PresenceSettingsGui(
                                                                    screenInstance, currentPresenceData,
                                                                    (output) -> screenInstance.currentData.setData(output)
                                                            )
                                                    );
                                                } else {
                                                    final String defaultIcon = Config.getProperty(screenInstance.defaultData, "iconOverride") != null ? screenInstance.defaultData.getIconOverride() : CraftPresence.CONFIG.generalSettings.defaultIcon;
                                                    final String specificIcon = Config.getProperty(screenInstance.currentData, "iconOverride") != null ? screenInstance.currentData.getIconOverride() : defaultIcon;
                                                    CraftPresence.GUIS.openScreen(
                                                            new SelectorGui(
                                                                    screenInstance,
                                                                    ModUtils.TRANSLATOR.translate("gui.config.title.selector.icon"), DiscordAssetUtils.ASSET_LIST.keySet(),
                                                                    specificIcon, attributeName,
                                                                    true, false, ScrollableListControl.RenderType.DiscordAsset,
                                                                    (innerAttributeName, innerCurrentValue) -> {
                                                                        // Inner-Event to occur when proceeding with adjusted data
                                                                        final String defaultMessage = Config.getProperty(screenInstance.defaultData, "textOverride") != null ? screenInstance.defaultData.getTextOverride() : "";
                                                                        final String currentMessage = Config.getProperty(screenInstance.currentData, "textOverride") != null ? screenInstance.currentData.getTextOverride() : "";

                                                                        screenInstance.currentData.setIconOverride(innerCurrentValue);
                                                                    }, null
                                                            )
                                                    );
                                                }
                                            }
                                    )
                            ),
                            entry.getValue().getSecond()
                    ), startPage
            );

            if (isEven) {
                buttonRow++;
            }
            index++;
        }
        modsMessage = addControl(
                new ExtendedTextControl(
                        getFontRenderer(),
                        calc2, CraftPresence.GUIS.getButtonY(5),
                        180, 20
                ), startPage
        );
        viveCraftMessage = addControl(
                new ExtendedTextControl(
                        getFontRenderer(),
                        calc2, CraftPresence.GUIS.getButtonY(6),
                        180, 20
                ), startPage
        );

        // Page 2 Items
        outerPlayerMessage = addControl(
                new ExtendedTextControl(
                        getFontRenderer(),
                        calc2, CraftPresence.GUIS.getButtonY(1),
                        180, 20
                ), startPage + 1
        );
        innerPlayerMessage = addControl(
                new ExtendedTextControl(
                        getFontRenderer(),
                        calc2, CraftPresence.GUIS.getButtonY(2),
                        180, 20
                ), startPage + 1
        );
        playerCoordsMessage = addControl(
                new ExtendedTextControl(
                        getFontRenderer(),
                        calc2, CraftPresence.GUIS.getButtonY(3),
                        180, 20
                ), startPage + 1
        );
        playerHealthMessage = addControl(
                new ExtendedTextControl(
                        getFontRenderer(),
                        calc2, CraftPresence.GUIS.getButtonY(4),
                        180, 20
                ), startPage + 1
        );
        playerAmountMessage = addControl(
                new ExtendedTextControl(
                        getFontRenderer(),
                        calc2, CraftPresence.GUIS.getButtonY(5),
                        180, 20
                ), startPage + 1
        );
        worldMessage = addControl(
                new ExtendedTextControl(
                        getFontRenderer(),
                        calc2, CraftPresence.GUIS.getButtonY(6),
                        180, 20
                ), startPage + 1
        );

        // Page 3 Items
        playerItemsMessage = addControl(
                new ExtendedTextControl(
                        getFontRenderer(),
                        calc2, CraftPresence.GUIS.getButtonY(2),
                        180, 20
                ), startPage + 2
        );
        fallbackPackPlaceholderMessage = addControl(
                new ExtendedTextControl(
                        getFontRenderer(),
                        calc2, CraftPresence.GUIS.getButtonY(3),
                        180, 20
                ), startPage + 2
        );

        // Page 1 setText
        modsMessage.setControlMessage(CONFIG.modsPlaceholderMessage);
        viveCraftMessage.setControlMessage(CONFIG.vivecraftMessage);

        // Page 2 setText
        outerPlayerMessage.setControlMessage(CONFIG.outerPlayerPlaceholderMessage);
        innerPlayerMessage.setControlMessage(CONFIG.innerPlayerPlaceholderMessage);
        playerCoordsMessage.setControlMessage(CONFIG.playerCoordinatePlaceholderMessage);
        playerHealthMessage.setControlMessage(CONFIG.playerHealthPlaceholderMessage);
        playerAmountMessage.setControlMessage(CONFIG.playerAmountPlaceholderMessage);
        worldMessage.setControlMessage(CONFIG.worldPlaceholderMessage);

        // Page 3 setText
        //loadingMessage.setControlMessage(CONFIG.loadingMessage);
        playerItemsMessage.setControlMessage(CONFIG.playerItemsPlaceholderMessage);
        fallbackPackPlaceholderMessage.setControlMessage(CONFIG.fallbackPackPlaceholderMessage);

        super.initializeUi();

        backButton.setOnClick(
                () -> {
                    // Page 1 Saving
                    if (!modsMessage.getControlMessage().equals(CONFIG.modsPlaceholderMessage)) {
                        CraftPresence.CONFIG.hasChanged = true;
                        CraftPresence.CONFIG.hasClientPropertiesChanged = true;
                        CONFIG.modsPlaceholderMessage = modsMessage.getControlMessage();
                    }
                    if (!viveCraftMessage.getControlMessage().equals(CONFIG.vivecraftMessage)) {
                        CraftPresence.CONFIG.hasChanged = true;
                        CraftPresence.CONFIG.hasClientPropertiesChanged = true;
                        CONFIG.vivecraftMessage = viveCraftMessage.getControlMessage();
                    }

                    // Page 2 Saving
                    if (!outerPlayerMessage.getControlMessage().equals(CONFIG.outerPlayerPlaceholderMessage)) {
                        CraftPresence.CONFIG.hasChanged = true;
                        CraftPresence.CONFIG.hasClientPropertiesChanged = true;
                        CONFIG.outerPlayerPlaceholderMessage = outerPlayerMessage.getControlMessage();
                    }
                    if (!innerPlayerMessage.getControlMessage().equals(CONFIG.innerPlayerPlaceholderMessage)) {
                        CraftPresence.CONFIG.hasChanged = true;
                        CraftPresence.CONFIG.hasClientPropertiesChanged = true;
                        CONFIG.innerPlayerPlaceholderMessage = innerPlayerMessage.getControlMessage();
                    }
                    if (!playerCoordsMessage.getControlMessage().equals(CONFIG.playerCoordinatePlaceholderMessage)) {
                        CraftPresence.CONFIG.hasChanged = true;
                        CraftPresence.CONFIG.hasClientPropertiesChanged = true;
                        CONFIG.playerCoordinatePlaceholderMessage = playerCoordsMessage.getControlMessage();
                    }
                    if (!playerHealthMessage.getControlMessage().equals(CONFIG.playerHealthPlaceholderMessage)) {
                        CraftPresence.CONFIG.hasChanged = true;
                        CraftPresence.CONFIG.hasClientPropertiesChanged = true;
                        CONFIG.playerHealthPlaceholderMessage = playerHealthMessage.getControlMessage();
                    }
                    if (!playerAmountMessage.getControlMessage().equals(CONFIG.playerAmountPlaceholderMessage)) {
                        CraftPresence.CONFIG.hasChanged = true;
                        CraftPresence.CONFIG.hasClientPropertiesChanged = true;
                        CONFIG.playerAmountPlaceholderMessage = playerAmountMessage.getControlMessage();
                    }
                    if (!worldMessage.getControlMessage().equals(CONFIG.worldPlaceholderMessage)) {
                        CraftPresence.CONFIG.hasChanged = true;
                        CraftPresence.CONFIG.hasClientPropertiesChanged = true;
                        CONFIG.worldPlaceholderMessage = worldMessage.getControlMessage();
                    }

                    // Page 3 Saving
                    if (!playerItemsMessage.getControlMessage().equals(CONFIG.playerItemsPlaceholderMessage)) {
                        CraftPresence.CONFIG.hasChanged = true;
                        CraftPresence.CONFIG.hasClientPropertiesChanged = true;
                        CONFIG.playerItemsPlaceholderMessage = playerItemsMessage.getControlMessage();
                    }
                    if (!fallbackPackPlaceholderMessage.getControlMessage().equals(CONFIG.fallbackPackPlaceholderMessage)) {
                        CraftPresence.CONFIG.hasChanged = true;
                        CraftPresence.CONFIG.hasClientPropertiesChanged = true;
                        CONFIG.fallbackPackPlaceholderMessage = fallbackPackPlaceholderMessage.getControlMessage();
                    }
                    CraftPresence.GUIS.openScreen(parentScreen);
                }
        );
        backButton.setOnHover(
                () -> {
                    if (!backButton.isControlEnabled()) {
                        CraftPresence.GUIS.drawMultiLineString(
                                StringUtils.splitTextByNewLine(
                                        ModUtils.TRANSLATOR.translate("gui.config.message.hover.empty.default")
                                ), this, true
                        );
                    }
                }
        );
    }

    @Override
    public void preRender() {
        final String mainTitle = ModUtils.TRANSLATOR.translate("gui.config.title");
        final String subTitle = ModUtils.TRANSLATOR.translate("gui.config.title.status_messages");

        final String modsText = ModUtils.TRANSLATOR.translate("gui.config.name.status_messages.placeholder.mods_message");
        final String viveCraftText = ModUtils.TRANSLATOR.translate("gui.config.name.status_messages.special.vivecraft_message");

        final String outerPlayerText = ModUtils.TRANSLATOR.translate("gui.config.name.status_messages.placeholder.player_message.out");
        final String innerPlayerText = ModUtils.TRANSLATOR.translate("gui.config.name.status_messages.placeholder.player_message.in");
        final String playerCoordsText = ModUtils.TRANSLATOR.translate("gui.config.name.status_messages.placeholder.player_coordinate_message");
        final String playerHealthText = ModUtils.TRANSLATOR.translate("gui.config.name.status_messages.placeholder.player_health_message");
        final String playerAmountText = ModUtils.TRANSLATOR.translate("gui.config.name.status_messages.placeholder.player_amount_message");
        final String playerItemsText = ModUtils.TRANSLATOR.translate("gui.config.name.status_messages.placeholder.player_item_message");
        final String worldDataText = ModUtils.TRANSLATOR.translate("gui.config.name.status_messages.placeholder.world_message");

        final String fallbackPackPlaceholderText = ModUtils.TRANSLATOR.translate("gui.config.name.status_messages.fallback.pack_placeholder_message");

        renderString(mainTitle, (getScreenWidth() / 2f) - (getStringWidth(mainTitle) / 2f), 10, 0xFFFFFF);
        renderString(subTitle, (getScreenWidth() / 2f) - (getStringWidth(subTitle) / 2f), 20, 0xFFFFFF);

        renderString(modsText, (getScreenWidth() / 2f) - 160, CraftPresence.GUIS.getButtonY(5, 5), 0xFFFFFF, startPage);
        renderString(viveCraftText, (getScreenWidth() / 2f) - 160, CraftPresence.GUIS.getButtonY(6, 5), 0xFFFFFF, startPage);

        renderString(outerPlayerText, (getScreenWidth() / 2f) - 160, CraftPresence.GUIS.getButtonY(1, 5), 0xFFFFFF, startPage + 1);
        renderString(innerPlayerText, (getScreenWidth() / 2f) - 160, CraftPresence.GUIS.getButtonY(2, 5), 0xFFFFFF, startPage + 1);
        renderString(playerCoordsText, (getScreenWidth() / 2f) - 160, CraftPresence.GUIS.getButtonY(3, 5), 0xFFFFFF, startPage + 1);
        renderString(playerHealthText, (getScreenWidth() / 2f) - 160, CraftPresence.GUIS.getButtonY(4, 5), 0xFFFFFF, startPage + 1);
        renderString(playerAmountText, (getScreenWidth() / 2f) - 160, CraftPresence.GUIS.getButtonY(5, 5), 0xFFFFFF, startPage + 1);
        renderString(worldDataText, (getScreenWidth() / 2f) - 160, CraftPresence.GUIS.getButtonY(6, 5), 0xFFFFFF, startPage + 1);

        renderString(playerItemsText, (getScreenWidth() / 2f) - 160, CraftPresence.GUIS.getButtonY(2, 5), 0xFFFFFF, startPage + 2);
        renderString(fallbackPackPlaceholderText, (getScreenWidth() / 2f) - 160, CraftPresence.GUIS.getButtonY(3, 5), 0xFFFFFF, startPage + 2);

        super.preRender();
    }

    @Override
    public void postRender() {
        final String modsText = ModUtils.TRANSLATOR.translate("gui.config.name.status_messages.placeholder.mods_message");
        final String viveCraftText = ModUtils.TRANSLATOR.translate("gui.config.name.status_messages.special.vivecraft_message");

        final String outerPlayerText = ModUtils.TRANSLATOR.translate("gui.config.name.status_messages.placeholder.player_message.out");
        final String innerPlayerText = ModUtils.TRANSLATOR.translate("gui.config.name.status_messages.placeholder.player_message.in");
        final String playerCoordsText = ModUtils.TRANSLATOR.translate("gui.config.name.status_messages.placeholder.player_coordinate_message");
        final String playerHealthText = ModUtils.TRANSLATOR.translate("gui.config.name.status_messages.placeholder.player_health_message");
        final String playerAmountText = ModUtils.TRANSLATOR.translate("gui.config.name.status_messages.placeholder.player_amount_message");
        final String playerItemsText = ModUtils.TRANSLATOR.translate("gui.config.name.status_messages.placeholder.player_item_message");
        final String worldDataText = ModUtils.TRANSLATOR.translate("gui.config.name.status_messages.placeholder.world_message");

        final String fallbackPackPlaceholderText = ModUtils.TRANSLATOR.translate("gui.config.name.status_messages.fallback.pack_placeholder_message");
        if (currentPage == startPage) {
            // Hovering over Mods Message Label
            if (CraftPresence.GUIS.isMouseOver(getMouseX(), getMouseY(), (getScreenWidth() / 2f) - 160, CraftPresence.GUIS.getButtonY(5, 5), getStringWidth(modsText), getFontHeight())) {
                CraftPresence.GUIS.drawMultiLineString(
                        StringUtils.splitTextByNewLine(
                                ModUtils.TRANSLATOR.translate("gui.config.comment.status_messages.placeholder.mods_message",
                                        CraftPresence.CLIENT.generateArgumentMessage(
                                                "&MODS&", "&MODS:",
                                                ArgumentType.Text, "&MODS:"
                                        ))
                        ), this, true
                );
            }
            // Hovering over Vivecraft Message Label
            if (CraftPresence.GUIS.isMouseOver(getMouseX(), getMouseY(), (getScreenWidth() / 2f) - 160, CraftPresence.GUIS.getButtonY(6, 5), getStringWidth(viveCraftText), getFontHeight())) {
                CraftPresence.GUIS.drawMultiLineString(
                        StringUtils.splitTextByNewLine(
                                ModUtils.TRANSLATOR.translate("gui.config.comment.status_messages.special.vivecraft_message")
                        ), this, true
                );
            }
        }

        if (currentPage == startPage + 1) {
            // Hovering over Outer Player Message Label
            if (CraftPresence.GUIS.isMouseOver(getMouseX(), getMouseY(), (getScreenWidth() / 2f) - 160, CraftPresence.GUIS.getButtonY(1, 5), getStringWidth(outerPlayerText), getFontHeight())) {
                CraftPresence.GUIS.drawMultiLineString(
                        StringUtils.splitTextByNewLine(
                                ModUtils.TRANSLATOR.translate("gui.config.comment.status_messages.placeholder.player_message.out",
                                        CraftPresence.CLIENT.generateArgumentMessage(
                                                "&IGN&", "&IGN:",
                                                ArgumentType.Text, "&IGN:"
                                        ))
                        ), this, true
                );
            }
            // Hovering over Inner Player Message Label
            if (CraftPresence.GUIS.isMouseOver(getMouseX(), getMouseY(), (getScreenWidth() / 2f) - 160, CraftPresence.GUIS.getButtonY(2, 5), getStringWidth(innerPlayerText), getFontHeight())) {
                CraftPresence.GUIS.drawMultiLineString(
                        StringUtils.splitTextByNewLine(
                                ModUtils.TRANSLATOR.translate("gui.config.comment.status_messages.placeholder.player_message.in",
                                        CraftPresence.SERVER.generateArgumentMessage(
                                                "&SERVER:PLAYERINFO&", "&SERVER:PLAYERINFO:",
                                                ArgumentType.Text
                                        ))
                        ), this, true
                );
            }
            // Hovering over Player Coords Message
            if (CraftPresence.GUIS.isMouseOver(getMouseX(), getMouseY(), (getScreenWidth() / 2f) - 160, CraftPresence.GUIS.getButtonY(3, 5), getStringWidth(playerCoordsText), getFontHeight())) {
                CraftPresence.GUIS.drawMultiLineString(
                        StringUtils.splitTextByNewLine(
                                ModUtils.TRANSLATOR.translate("gui.config.comment.status_messages.placeholder.player_coordinate_message",
                                        CraftPresence.SERVER.generateArgumentMessage(
                                                "&SERVER:PLAYERINFO:COORDS&", "&SERVER:PLAYERINFO:COORDS:",
                                                ArgumentType.Text
                                        ))
                        ), this, true
                );
            }
            // Hovering over Player Health Message
            if (CraftPresence.GUIS.isMouseOver(getMouseX(), getMouseY(), (getScreenWidth() / 2f) - 160, CraftPresence.GUIS.getButtonY(4, 5), getStringWidth(playerHealthText), getFontHeight())) {
                CraftPresence.GUIS.drawMultiLineString(
                        StringUtils.splitTextByNewLine(
                                ModUtils.TRANSLATOR.translate("gui.config.comment.status_messages.placeholder.player_health_message",
                                        CraftPresence.SERVER.generateArgumentMessage(
                                                "&SERVER:PLAYERINFO:HEALTH&", "&SERVER:PLAYERINFO:HEALTH:",
                                                ArgumentType.Text
                                        ))
                        ), this, true
                );
            }
            // Hovering over Player Amount Message Label
            if (CraftPresence.GUIS.isMouseOver(getMouseX(), getMouseY(), (getScreenWidth() / 2f) - 160, CraftPresence.GUIS.getButtonY(5, 5), getStringWidth(playerAmountText), getFontHeight())) {
                CraftPresence.GUIS.drawMultiLineString(
                        StringUtils.splitTextByNewLine(
                                ModUtils.TRANSLATOR.translate("gui.config.comment.status_messages.placeholder.player_amount_message",
                                        CraftPresence.SERVER.generateArgumentMessage(
                                                "&SERVER:PLAYERS&", "&SERVER:PLAYERS:",
                                                ArgumentType.Text
                                        ))
                        ), this, true
                );
            }
            // Hovering over World Data Message Label
            if (CraftPresence.GUIS.isMouseOver(getMouseX(), getMouseY(), (getScreenWidth() / 2f) - 160, CraftPresence.GUIS.getButtonY(6, 5), getStringWidth(worldDataText), getFontHeight())) {
                CraftPresence.GUIS.drawMultiLineString(
                        StringUtils.splitTextByNewLine(
                                ModUtils.TRANSLATOR.translate("gui.config.comment.status_messages.placeholder.world_message",
                                        CraftPresence.SERVER.generateArgumentMessage(
                                                "&SERVER:WORLDINFO&", "&SERVER:WORLDINFO:",
                                                ArgumentType.Text
                                        ))
                        ), this, true
                );
            }
        }

        if (currentPage == startPage + 2) {
            // Hovering over Player Items Message Label
            if (CraftPresence.GUIS.isMouseOver(getMouseX(), getMouseY(), (getScreenWidth() / 2f) - 160, CraftPresence.GUIS.getButtonY(2, 5), getStringWidth(playerItemsText), getFontHeight())) {
                CraftPresence.GUIS.drawMultiLineString(
                        StringUtils.splitTextByNewLine(
                                ModUtils.TRANSLATOR.translate("gui.config.comment.status_messages.placeholder.player_item_message",
                                        CraftPresence.TILE_ENTITIES.generateArgumentMessage(
                                                "&TILEENTITY&",
                                                "&TILEENTITY:"
                                        ))
                        ), this, true
                );
            }
            // Hovering over Fallback Pack Placeholder Label
            if (CraftPresence.GUIS.isMouseOver(getMouseX(), getMouseY(), (getScreenWidth() / 2f) - 160, CraftPresence.GUIS.getButtonY(3, 5), getStringWidth(fallbackPackPlaceholderText), getFontHeight())) {
                CraftPresence.GUIS.drawMultiLineString(
                        StringUtils.splitTextByNewLine(
                                ModUtils.TRANSLATOR.translate("gui.config.comment.status_messages.fallback.pack_placeholder_message")
                        ), this, true
                );
            }
        }
    }
}
