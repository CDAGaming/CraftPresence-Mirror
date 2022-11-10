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
import com.gitlab.cdagaming.craftpresence.utils.gui.controls.ExtendedTextControl;
import com.gitlab.cdagaming.craftpresence.utils.gui.integrations.PaginatedScreen;
import net.minecraft.client.gui.screens.Screen;

@SuppressWarnings("DuplicatedCode")
public class StatusMessagesGui extends PaginatedScreen {
    private ExtendedTextControl mainMenuMessage, loadingMessage, lanMessage, singlePlayerMessage, packMessage,
            outerPlayerMessage, innerPlayerMessage, playerCoordsMessage, playerHealthMessage,
            playerAmountMessage, playerItemsMessage, worldMessage, modsMessage, viveCraftMessage, fallbackPackPlaceholderMessage;

    StatusMessagesGui(Screen parentScreen) {
        super(parentScreen);
    }

    @Override
    public void initializeUi() {
        // Page 1 Items
        mainMenuMessage = addControl(
                new ExtendedTextControl(
                        getFontRenderer(),
                        (getScreenWidth() / 2) + 3, CraftPresence.GUIS.getButtonY(1),
                        180, 20
                ), startPage
        );
        lanMessage = addControl(
                new ExtendedTextControl(
                        getFontRenderer(),
                        (getScreenWidth() / 2) + 3, CraftPresence.GUIS.getButtonY(2),
                        180, 20
                ), startPage
        );
        singlePlayerMessage = addControl(
                new ExtendedTextControl(
                        getFontRenderer(),
                        (getScreenWidth() / 2) + 3, CraftPresence.GUIS.getButtonY(3),
                        180, 20
                ), startPage
        );
        packMessage = addControl(
                new ExtendedTextControl(
                        getFontRenderer(),
                        (getScreenWidth() / 2) + 3, CraftPresence.GUIS.getButtonY(4),
                        180, 20
                ), startPage
        );
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
        loadingMessage = addControl(
                new ExtendedTextControl(
                        getFontRenderer(),
                        (getScreenWidth() / 2) + 3, CraftPresence.GUIS.getButtonY(1),
                        180, 20
                ), startPage + 2
        );
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
        mainMenuMessage.setControlMessage(CraftPresence.CONFIG.mainMenuMessage);
        lanMessage.setControlMessage(CraftPresence.CONFIG.lanMessage);
        singlePlayerMessage.setControlMessage(CraftPresence.CONFIG.singlePlayerMessage);
        packMessage.setControlMessage(CraftPresence.CONFIG.packPlaceholderMessage);
        modsMessage.setControlMessage(CraftPresence.CONFIG.modsPlaceholderMessage);
        viveCraftMessage.setControlMessage(CraftPresence.CONFIG.vivecraftMessage);

        // Page 2 setText
        outerPlayerMessage.setControlMessage(CraftPresence.CONFIG.outerPlayerPlaceholderMessage);
        innerPlayerMessage.setControlMessage(CraftPresence.CONFIG.innerPlayerPlaceholderMessage);
        playerCoordsMessage.setControlMessage(CraftPresence.CONFIG.playerCoordinatePlaceholderMessage);
        playerHealthMessage.setControlMessage(CraftPresence.CONFIG.playerHealthPlaceholderMessage);
        playerAmountMessage.setControlMessage(CraftPresence.CONFIG.playerAmountPlaceholderMessage);
        worldMessage.setControlMessage(CraftPresence.CONFIG.worldPlaceholderMessage);

        // Page 3 setText
        loadingMessage.setControlMessage(CraftPresence.CONFIG.loadingMessage);
        playerItemsMessage.setControlMessage(CraftPresence.CONFIG.playerItemsPlaceholderMessage);
        fallbackPackPlaceholderMessage.setControlMessage(CraftPresence.CONFIG.fallbackPackPlaceholderMessage);

        super.initializeUi();

        backButton.setOnClick(
                () -> {
                    // Page 1 Saving
                    if (!mainMenuMessage.getControlMessage().equals(CraftPresence.CONFIG.mainMenuMessage)) {
                        CraftPresence.CONFIG.hasChanged = true;
                        CraftPresence.CONFIG.hasClientPropertiesChanged = true;
                        CraftPresence.CONFIG.mainMenuMessage = mainMenuMessage.getControlMessage();
                    }
                    if (!lanMessage.getControlMessage().equals(CraftPresence.CONFIG.lanMessage)) {
                        CraftPresence.CONFIG.hasChanged = true;
                        CraftPresence.CONFIG.hasClientPropertiesChanged = true;
                        CraftPresence.CONFIG.lanMessage = lanMessage.getControlMessage();
                    }
                    if (!singlePlayerMessage.getControlMessage().equals(CraftPresence.CONFIG.singlePlayerMessage)) {
                        CraftPresence.CONFIG.hasChanged = true;
                        CraftPresence.CONFIG.hasClientPropertiesChanged = true;
                        CraftPresence.CONFIG.singlePlayerMessage = singlePlayerMessage.getControlMessage();
                    }
                    if (!packMessage.getControlMessage().equals(CraftPresence.CONFIG.packPlaceholderMessage)) {
                        CraftPresence.CONFIG.hasChanged = true;
                        CraftPresence.CONFIG.hasClientPropertiesChanged = true;
                        CraftPresence.CONFIG.packPlaceholderMessage = packMessage.getControlMessage();
                    }
                    if (!modsMessage.getControlMessage().equals(CraftPresence.CONFIG.modsPlaceholderMessage)) {
                        CraftPresence.CONFIG.hasChanged = true;
                        CraftPresence.CONFIG.hasClientPropertiesChanged = true;
                        CraftPresence.CONFIG.modsPlaceholderMessage = modsMessage.getControlMessage();
                    }
                    if (!viveCraftMessage.getControlMessage().equals(CraftPresence.CONFIG.vivecraftMessage)) {
                        CraftPresence.CONFIG.hasChanged = true;
                        CraftPresence.CONFIG.hasClientPropertiesChanged = true;
                        CraftPresence.CONFIG.vivecraftMessage = viveCraftMessage.getControlMessage();
                    }

                    // Page 2 Saving
                    if (!outerPlayerMessage.getControlMessage().equals(CraftPresence.CONFIG.outerPlayerPlaceholderMessage)) {
                        CraftPresence.CONFIG.hasChanged = true;
                        CraftPresence.CONFIG.hasClientPropertiesChanged = true;
                        CraftPresence.CONFIG.outerPlayerPlaceholderMessage = outerPlayerMessage.getControlMessage();
                    }
                    if (!innerPlayerMessage.getControlMessage().equals(CraftPresence.CONFIG.innerPlayerPlaceholderMessage)) {
                        CraftPresence.CONFIG.hasChanged = true;
                        CraftPresence.CONFIG.hasClientPropertiesChanged = true;
                        CraftPresence.CONFIG.innerPlayerPlaceholderMessage = innerPlayerMessage.getControlMessage();
                    }
                    if (!playerCoordsMessage.getControlMessage().equals(CraftPresence.CONFIG.playerCoordinatePlaceholderMessage)) {
                        CraftPresence.CONFIG.hasChanged = true;
                        CraftPresence.CONFIG.hasClientPropertiesChanged = true;
                        CraftPresence.CONFIG.playerCoordinatePlaceholderMessage = playerCoordsMessage.getControlMessage();
                    }
                    if (!playerHealthMessage.getControlMessage().equals(CraftPresence.CONFIG.playerHealthPlaceholderMessage)) {
                        CraftPresence.CONFIG.hasChanged = true;
                        CraftPresence.CONFIG.hasClientPropertiesChanged = true;
                        CraftPresence.CONFIG.playerHealthPlaceholderMessage = playerHealthMessage.getControlMessage();
                    }
                    if (!playerAmountMessage.getControlMessage().equals(CraftPresence.CONFIG.playerAmountPlaceholderMessage)) {
                        CraftPresence.CONFIG.hasChanged = true;
                        CraftPresence.CONFIG.hasClientPropertiesChanged = true;
                        CraftPresence.CONFIG.playerAmountPlaceholderMessage = playerAmountMessage.getControlMessage();
                    }
                    if (!worldMessage.getControlMessage().equals(CraftPresence.CONFIG.worldPlaceholderMessage)) {
                        CraftPresence.CONFIG.hasChanged = true;
                        CraftPresence.CONFIG.hasClientPropertiesChanged = true;
                        CraftPresence.CONFIG.worldPlaceholderMessage = worldMessage.getControlMessage();
                    }

                    // Page 3 Saving
                    if (!loadingMessage.getControlMessage().equals(CraftPresence.CONFIG.loadingMessage)) {
                        CraftPresence.CONFIG.hasChanged = true;
                        CraftPresence.CONFIG.hasClientPropertiesChanged = true;
                        CraftPresence.CONFIG.loadingMessage = loadingMessage.getControlMessage();
                    }
                    if (!playerItemsMessage.getControlMessage().equals(CraftPresence.CONFIG.playerItemsPlaceholderMessage)) {
                        CraftPresence.CONFIG.hasChanged = true;
                        CraftPresence.CONFIG.hasClientPropertiesChanged = true;
                        CraftPresence.CONFIG.playerItemsPlaceholderMessage = playerItemsMessage.getControlMessage();
                    }
                    if (!fallbackPackPlaceholderMessage.getControlMessage().equals(CraftPresence.CONFIG.fallbackPackPlaceholderMessage)) {
                        CraftPresence.CONFIG.hasChanged = true;
                        CraftPresence.CONFIG.hasClientPropertiesChanged = true;
                        CraftPresence.CONFIG.fallbackPackPlaceholderMessage = fallbackPackPlaceholderMessage.getControlMessage();
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

        renderString(mainTitle, (getScreenWidth() / 2f) - (getStringWidth(mainTitle) / 2f), 10, 0xFFFFFF);
        renderString(subTitle, (getScreenWidth() / 2f) - (getStringWidth(subTitle) / 2f), 20, 0xFFFFFF);

        renderString(mainMenuText, (getScreenWidth() / 2f) - 160, CraftPresence.GUIS.getButtonY(1, 5), 0xFFFFFF, startPage);
        renderString(lanText, (getScreenWidth() / 2f) - 160, CraftPresence.GUIS.getButtonY(2, 5), 0xFFFFFF, startPage);
        renderString(singlePlayerText, (getScreenWidth() / 2f) - 160, CraftPresence.GUIS.getButtonY(3, 5), 0xFFFFFF, startPage);
        renderString(packText, (getScreenWidth() / 2f) - 160, CraftPresence.GUIS.getButtonY(4, 5), 0xFFFFFF, startPage);
        renderString(modsText, (getScreenWidth() / 2f) - 160, CraftPresence.GUIS.getButtonY(5, 5), 0xFFFFFF, startPage);
        renderString(viveCraftText, (getScreenWidth() / 2f) - 160, CraftPresence.GUIS.getButtonY(6, 5), 0xFFFFFF, startPage);

        renderString(outerPlayerText, (getScreenWidth() / 2f) - 160, CraftPresence.GUIS.getButtonY(1, 5), 0xFFFFFF, startPage + 1);
        renderString(innerPlayerText, (getScreenWidth() / 2f) - 160, CraftPresence.GUIS.getButtonY(2, 5), 0xFFFFFF, startPage + 1);
        renderString(playerCoordsText, (getScreenWidth() / 2f) - 160, CraftPresence.GUIS.getButtonY(3, 5), 0xFFFFFF, startPage + 1);
        renderString(playerHealthText, (getScreenWidth() / 2f) - 160, CraftPresence.GUIS.getButtonY(4, 5), 0xFFFFFF, startPage + 1);
        renderString(playerAmountText, (getScreenWidth() / 2f) - 160, CraftPresence.GUIS.getButtonY(5, 5), 0xFFFFFF, startPage + 1);
        renderString(worldDataText, (getScreenWidth() / 2f) - 160, CraftPresence.GUIS.getButtonY(6, 5), 0xFFFFFF, startPage + 1);

        renderString(loadingText, (getScreenWidth() / 2f) - 160, CraftPresence.GUIS.getButtonY(1, 5), 0xFFFFFF, startPage + 2);
        renderString(playerItemsText, (getScreenWidth() / 2f) - 160, CraftPresence.GUIS.getButtonY(2, 5), 0xFFFFFF, startPage + 2);
        renderString(fallbackPackPlaceholderText, (getScreenWidth() / 2f) - 160, CraftPresence.GUIS.getButtonY(3, 5), 0xFFFFFF, startPage + 2);

        super.preRender();

        backButton.setControlEnabled(!StringUtils.isNullOrEmpty(mainMenuMessage.getControlMessage())
                && !StringUtils.isNullOrEmpty(lanMessage.getControlMessage())
                && !StringUtils.isNullOrEmpty(singlePlayerMessage.getControlMessage())
                && !StringUtils.isNullOrEmpty(packMessage.getControlMessage())
                && !StringUtils.isNullOrEmpty(modsMessage.getControlMessage())
                && !StringUtils.isNullOrEmpty(viveCraftMessage.getControlMessage())
                && !StringUtils.isNullOrEmpty(outerPlayerMessage.getControlMessage())
                && !StringUtils.isNullOrEmpty(innerPlayerMessage.getControlMessage())
                && !StringUtils.isNullOrEmpty(playerCoordsMessage.getControlMessage())
                && !StringUtils.isNullOrEmpty(playerHealthMessage.getControlMessage())
                && !StringUtils.isNullOrEmpty(playerAmountMessage.getControlMessage())
                && !StringUtils.isNullOrEmpty(worldMessage.getControlMessage())
                && !StringUtils.isNullOrEmpty(loadingMessage.getControlMessage())
                && !StringUtils.isNullOrEmpty(playerItemsMessage.getControlMessage()));
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
