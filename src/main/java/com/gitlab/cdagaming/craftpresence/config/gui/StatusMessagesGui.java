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
import com.gitlab.cdagaming.craftpresence.config.category.Status;
import com.gitlab.cdagaming.craftpresence.impl.discord.ArgumentType;
import com.gitlab.cdagaming.craftpresence.utils.StringUtils;
import com.gitlab.cdagaming.craftpresence.utils.gui.controls.ExtendedButtonControl;
import com.gitlab.cdagaming.craftpresence.utils.gui.controls.ExtendedTextControl;
import com.gitlab.cdagaming.craftpresence.utils.gui.integrations.PaginatedScreen;
import net.minecraft.client.gui.GuiScreen;

@SuppressWarnings("DuplicatedCode")
public class StatusMessagesGui extends PaginatedScreen {
    private final Status CONFIG;
    private ExtendedButtonControl mainMenuButton, loadingButton, lanButton, singlePlayerButton, packButton;
    private ExtendedTextControl outerPlayerMessage, innerPlayerMessage, playerCoordsMessage, playerHealthMessage,
            playerAmountMessage, playerItemsMessage, worldMessage, modsMessage, viveCraftMessage, fallbackPackPlaceholderMessage;

    StatusMessagesGui(GuiScreen parentScreen) {
        super(parentScreen);
        CONFIG = CraftPresence.CONFIG.statusMessages;
    }

    @Override
    public void initializeUi() {
        // Page 1 Items
//        mainMenuMessage = addControl(
//                new ExtendedTextControl(
//                        getFontRenderer(),
//                        (getScreenWidth() / 2) + 3, CraftPresence.GUIS.getButtonY(1),
//                        180, 20
//                ), startPage
//        );
//        lanMessage = addControl(
//                new ExtendedTextControl(
//                        getFontRenderer(),
//                        (getScreenWidth() / 2) + 3, CraftPresence.GUIS.getButtonY(2),
//                        180, 20
//                ), startPage
//        );
//        singlePlayerMessage = addControl(
//                new ExtendedTextControl(
//                        getFontRenderer(),
//                        (getScreenWidth() / 2) + 3, CraftPresence.GUIS.getButtonY(3),
//                        180, 20
//                ), startPage
//        );
//        packMessage = addControl(
//                new ExtendedTextControl(
//                        getFontRenderer(),
//                        (getScreenWidth() / 2) + 3, CraftPresence.GUIS.getButtonY(4),
//                        180, 20
//                ), startPage
//        );
        modsMessage = addControl(
                new ExtendedTextControl(
                        getFontRenderer(),
                        (getScreenWidth() / 2) + 3, CraftPresence.GUIS.getButtonY(5),
                        180, 20
                ), startPage
        );
        viveCraftMessage = addControl(
                new ExtendedTextControl(
                        getFontRenderer(),
                        (getScreenWidth() / 2) + 3, CraftPresence.GUIS.getButtonY(6),
                        180, 20
                ), startPage
        );

        // Page 2 Items
        outerPlayerMessage = addControl(
                new ExtendedTextControl(
                        getFontRenderer(),
                        (getScreenWidth() / 2) + 3, CraftPresence.GUIS.getButtonY(1),
                        180, 20
                ), startPage + 1
        );
        innerPlayerMessage = addControl(
                new ExtendedTextControl(
                        getFontRenderer(),
                        (getScreenWidth() / 2) + 3, CraftPresence.GUIS.getButtonY(2),
                        180, 20
                ), startPage + 1
        );
        playerCoordsMessage = addControl(
                new ExtendedTextControl(
                        getFontRenderer(),
                        (getScreenWidth() / 2) + 3, CraftPresence.GUIS.getButtonY(3),
                        180, 20
                ), startPage + 1
        );
        playerHealthMessage = addControl(
                new ExtendedTextControl(
                        getFontRenderer(),
                        (getScreenWidth() / 2) + 3, CraftPresence.GUIS.getButtonY(4),
                        180, 20
                ), startPage + 1
        );
        playerAmountMessage = addControl(
                new ExtendedTextControl(
                        getFontRenderer(),
                        (getScreenWidth() / 2) + 3, CraftPresence.GUIS.getButtonY(5),
                        180, 20
                ), startPage + 1
        );
        worldMessage = addControl(
                new ExtendedTextControl(
                        getFontRenderer(),
                        (getScreenWidth() / 2) + 3, CraftPresence.GUIS.getButtonY(6),
                        180, 20
                ), startPage + 1
        );

        // Page 3 Items
//        loadingMessage = addControl(
//                new ExtendedTextControl(
//                        getFontRenderer(),
//                        (getScreenWidth() / 2) + 3, CraftPresence.GUIS.getButtonY(1),
//                        180, 20
//                ), startPage + 2
//        );
        playerItemsMessage = addControl(
                new ExtendedTextControl(
                        getFontRenderer(),
                        (getScreenWidth() / 2) + 3, CraftPresence.GUIS.getButtonY(2),
                        180, 20
                ), startPage + 2
        );
        fallbackPackPlaceholderMessage = addControl(
                new ExtendedTextControl(
                        getFontRenderer(),
                        (getScreenWidth() / 2) + 3, CraftPresence.GUIS.getButtonY(3),
                        180, 20
                ), startPage + 2
        );

        // Page 1 setText
        // TODO: Replace with ModuleData buttons
//        mainMenuMessage.setControlMessage(CONFIG.mainMenuMessage);
//        lanMessage.setControlMessage(CONFIG.lanMessage);
//        singlePlayerMessage.setControlMessage(CONFIG.singlePlayerMessage);
//        packMessage.setControlMessage(CONFIG.packPlaceholderMessage);
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
//                    if (!mainMenuMessage.getControlMessage().equals(CONFIG.mainMenuMessage)) {
//                        CraftPresence.CONFIG.hasChanged = true;
//                        CraftPresence.CONFIG.hasClientPropertiesChanged = true;
//                        CONFIG.mainMenuMessage = mainMenuMessage.getControlMessage();
//                    }
//                    if (!lanMessage.getControlMessage().equals(CONFIG.lanMessage)) {
//                        CraftPresence.CONFIG.hasChanged = true;
//                        CraftPresence.CONFIG.hasClientPropertiesChanged = true;
//                        CONFIG.lanMessage = lanMessage.getControlMessage();
//                    }
//                    if (!singlePlayerMessage.getControlMessage().equals(CONFIG.singlePlayerMessage)) {
//                        CraftPresence.CONFIG.hasChanged = true;
//                        CraftPresence.CONFIG.hasClientPropertiesChanged = true;
//                        CONFIG.singlePlayerMessage = singlePlayerMessage.getControlMessage();
//                    }
//                    if (!packMessage.getControlMessage().equals(CONFIG.packPlaceholderMessage)) {
//                        CraftPresence.CONFIG.hasChanged = true;
//                        CraftPresence.CONFIG.hasClientPropertiesChanged = true;
//                        CONFIG.packPlaceholderMessage = packMessage.getControlMessage();
//                    }
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
//                    if (!loadingMessage.getControlMessage().equals(CONFIG.loadingMessage)) {
//                        CraftPresence.CONFIG.hasChanged = true;
//                        CraftPresence.CONFIG.hasClientPropertiesChanged = true;
//                        CONFIG.loadingMessage = loadingMessage.getControlMessage();
//                    }
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

//        final String mainMenuText = ModUtils.TRANSLATOR.translate("gui.config.name.status_messages.main_menu_message");
//        final String loadingText = ModUtils.TRANSLATOR.translate("gui.config.name.status_messages.loading_message");
//        final String lanText = ModUtils.TRANSLATOR.translate("gui.config.name.status_messages.lan_message");
//        final String singlePlayerText = ModUtils.TRANSLATOR.translate("gui.config.name.status_messages.single_player_message");
//        final String packText = ModUtils.TRANSLATOR.translate("gui.config.name.status_messages.placeholder.pack_message");
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

//        renderString(mainMenuText, (getScreenWidth() / 2f) - 160, CraftPresence.GUIS.getButtonY(1, 5), 0xFFFFFF, startPage);
//        renderString(lanText, (getScreenWidth() / 2f) - 160, CraftPresence.GUIS.getButtonY(2, 5), 0xFFFFFF, startPage);
//        renderString(singlePlayerText, (getScreenWidth() / 2f) - 160, CraftPresence.GUIS.getButtonY(3, 5), 0xFFFFFF, startPage);
//        renderString(packText, (getScreenWidth() / 2f) - 160, CraftPresence.GUIS.getButtonY(4, 5), 0xFFFFFF, startPage);
        renderString(modsText, (getScreenWidth() / 2f) - 160, CraftPresence.GUIS.getButtonY(5, 5), 0xFFFFFF, startPage);
        renderString(viveCraftText, (getScreenWidth() / 2f) - 160, CraftPresence.GUIS.getButtonY(6, 5), 0xFFFFFF, startPage);

        renderString(outerPlayerText, (getScreenWidth() / 2f) - 160, CraftPresence.GUIS.getButtonY(1, 5), 0xFFFFFF, startPage + 1);
        renderString(innerPlayerText, (getScreenWidth() / 2f) - 160, CraftPresence.GUIS.getButtonY(2, 5), 0xFFFFFF, startPage + 1);
        renderString(playerCoordsText, (getScreenWidth() / 2f) - 160, CraftPresence.GUIS.getButtonY(3, 5), 0xFFFFFF, startPage + 1);
        renderString(playerHealthText, (getScreenWidth() / 2f) - 160, CraftPresence.GUIS.getButtonY(4, 5), 0xFFFFFF, startPage + 1);
        renderString(playerAmountText, (getScreenWidth() / 2f) - 160, CraftPresence.GUIS.getButtonY(5, 5), 0xFFFFFF, startPage + 1);
        renderString(worldDataText, (getScreenWidth() / 2f) - 160, CraftPresence.GUIS.getButtonY(6, 5), 0xFFFFFF, startPage + 1);

//        renderString(loadingText, (getScreenWidth() / 2f) - 160, CraftPresence.GUIS.getButtonY(1, 5), 0xFFFFFF, startPage + 2);
        renderString(playerItemsText, (getScreenWidth() / 2f) - 160, CraftPresence.GUIS.getButtonY(2, 5), 0xFFFFFF, startPage + 2);
        renderString(fallbackPackPlaceholderText, (getScreenWidth() / 2f) - 160, CraftPresence.GUIS.getButtonY(3, 5), 0xFFFFFF, startPage + 2);

        super.preRender();
    }

    @Override
    public void postRender() {
        final String mainMenuText = ModUtils.TRANSLATOR.translate("gui.config.name.status_messages.main_menu_message");
        final String loadingText = ModUtils.TRANSLATOR.translate("gui.config.name.status_messages.loading_message");
        final String lanText = ModUtils.TRANSLATOR.translate("gui.config.name.status_messages.lan_message");
        final String singlePlayerText = ModUtils.TRANSLATOR.translate("gui.config.name.status_messages.single_player_message");
        final String packText = ModUtils.TRANSLATOR.translate("gui.config.name.status_messages.placeholder.pack_message");
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
            // Hovering over Main Menu Message Label
            if (CraftPresence.GUIS.isMouseOver(getMouseX(), getMouseY(), (getScreenWidth() / 2f) - 160, CraftPresence.GUIS.getButtonY(1, 5), getStringWidth(mainMenuText), getFontHeight())) {
                CraftPresence.GUIS.drawMultiLineString(
                        StringUtils.splitTextByNewLine(
                                ModUtils.TRANSLATOR.translate("gui.config.comment.status_messages.main_menu_message",
                                        CraftPresence.CLIENT.generateArgumentMessage(null, null,
                                                CraftPresence.CLIENT.generalArgs
                                        ))
                        ), this, true
                );
            }
            // Hovering over LAN Message Label
            if (CraftPresence.GUIS.isMouseOver(getMouseX(), getMouseY(), (getScreenWidth() / 2f) - 160, CraftPresence.GUIS.getButtonY(2, 5), getStringWidth(lanText), getFontHeight())) {
                CraftPresence.GUIS.drawMultiLineString(
                        StringUtils.splitTextByNewLine(
                                ModUtils.TRANSLATOR.translate("gui.config.comment.status_messages.lan_message",
                                        CraftPresence.SERVER.generateArgumentMessage())
                        ), this, true
                );
            }
            // Hovering over Single Player Message Label
            if (CraftPresence.GUIS.isMouseOver(getMouseX(), getMouseY(), (getScreenWidth() / 2f) - 160, CraftPresence.GUIS.getButtonY(3, 5), getStringWidth(singlePlayerText), getFontHeight())) {
                CraftPresence.GUIS.drawMultiLineString(
                        StringUtils.splitTextByNewLine(
                                ModUtils.TRANSLATOR.translate("gui.config.comment.status_messages.single_player_message",
                                        CraftPresence.SERVER.generateArgumentMessage())
                        ), this, true
                );
            }
            // Hovering over Pack Message Label
            if (CraftPresence.GUIS.isMouseOver(getMouseX(), getMouseY(), (getScreenWidth() / 2f) - 160, CraftPresence.GUIS.getButtonY(4, 5), getStringWidth(packText), getFontHeight())) {
                CraftPresence.GUIS.drawMultiLineString(
                        StringUtils.splitTextByNewLine(
                                ModUtils.TRANSLATOR.translate("gui.config.comment.status_messages.placeholder.pack_message",
                                        CraftPresence.CLIENT.generateArgumentMessage(
                                                "&PACK&", "&PACK:",
                                                ArgumentType.Text, "&PACK:"
                                        ))
                        ), this, true
                );
            }
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
            // Hovering over Loading Message Label
            if (CraftPresence.GUIS.isMouseOver(getMouseX(), getMouseY(), (getScreenWidth() / 2f) - 160, CraftPresence.GUIS.getButtonY(1, 5), getStringWidth(loadingText), getFontHeight())) {
                CraftPresence.GUIS.drawMultiLineString(
                        StringUtils.splitTextByNewLine(
                                ModUtils.TRANSLATOR.translate("gui.config.comment.status_messages.loading_message",
                                        CraftPresence.CLIENT.generateArgumentMessage(null, null,
                                                CraftPresence.CLIENT.generalArgs
                                        ))
                        ), this, true
                );
            }
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
