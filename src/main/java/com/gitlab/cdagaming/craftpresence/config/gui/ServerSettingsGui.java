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
import com.gitlab.cdagaming.craftpresence.config.category.Server;
import com.gitlab.cdagaming.craftpresence.config.element.ModuleData;
import com.gitlab.cdagaming.craftpresence.utils.StringUtils;
import com.gitlab.cdagaming.craftpresence.utils.discord.assets.DiscordAssetUtils;
import com.gitlab.cdagaming.craftpresence.utils.gui.controls.ExtendedButtonControl;
import com.gitlab.cdagaming.craftpresence.utils.gui.controls.ExtendedTextControl;
import com.gitlab.cdagaming.craftpresence.utils.gui.controls.ScrollableListControl.RenderType;
import com.gitlab.cdagaming.craftpresence.utils.gui.impl.DynamicEditorGui;
import com.gitlab.cdagaming.craftpresence.utils.gui.impl.SelectorGui;
import com.gitlab.cdagaming.craftpresence.utils.gui.integrations.ExtendedScreen;
import net.minecraft.client.gui.GuiScreen;

@SuppressWarnings("DuplicatedCode")
public class ServerSettingsGui extends ExtendedScreen {
    private final Server CONFIG;
    private ExtendedButtonControl proceedButton, serverMessagesButton;
    private ExtendedTextControl defaultMOTD, defaultName, defaultMessage;

    ServerSettingsGui(GuiScreen parentScreen) {
        super(parentScreen);
        CONFIG = CraftPresence.CONFIG.serverSettings;
    }

    @Override
    public void initializeUi() {
        final ModuleData defaultData = CONFIG.serverData.get("default");
        final String defaultServerMessage = defaultData != null ? defaultData.getTextOverride() : "";

        defaultName = addControl(
                new ExtendedTextControl(
                        getFontRenderer(),
                        (getScreenWidth() / 2) + 3, CraftPresence.GUIS.getButtonY(1),
                        180, 20
                )
        );
        defaultName.setControlMessage(CONFIG.fallbackServerName);
        defaultMOTD = addControl(
                new ExtendedTextControl(
                        getFontRenderer(),
                        (getScreenWidth() / 2) + 3, CraftPresence.GUIS.getButtonY(2),
                        180, 20
                )
        );
        defaultMOTD.setControlMessage(CONFIG.fallbackServerMotd);
        defaultMessage = addControl(
                new ExtendedTextControl(
                        getFontRenderer(),
                        (getScreenWidth() / 2) + 3, CraftPresence.GUIS.getButtonY(3),
                        180, 20
                )
        );
        defaultMessage.setControlMessage(defaultServerMessage);

        serverMessagesButton = addControl(
                new ExtendedButtonControl(
                        (getScreenWidth() / 2) - 90, CraftPresence.GUIS.getButtonY(4),
                        180, 20,
                        "gui.config.name.server_messages.server_messages",
                        () -> CraftPresence.GUIS.openScreen(
                                new SelectorGui(
                                        currentScreen,
                                        ModUtils.TRANSLATOR.translate("gui.config.title.selector.server"), CraftPresence.SERVER.knownAddresses,
                                        null, null,
                                        true, true, RenderType.ServerData,
                                        (attributeName, currentValue) -> {
                                            final ModuleData defaultServerData = CONFIG.serverData.get("default");
                                            final ModuleData currentServerData = CONFIG.serverData.get(attributeName);
                                            final String defaultMessage = defaultServerData != null ? defaultServerData.getTextOverride() : "";
                                            final String currentMessage = currentServerData != null ? currentServerData.getTextOverride() : "";

                                            CraftPresence.CONFIG.hasChanged = true;
                                            final ModuleData newData = new ModuleData();
                                            if (StringUtils.isNullOrEmpty(currentMessage) || currentMessage.equals(defaultMessage)) {
                                                newData.setTextOverride(defaultMessage);
                                            }
                                            newData.setIconOverride(currentValue);
                                            CONFIG.serverData.put(attributeName, newData);
                                        },
                                        (currentValue, parentScreen) -> {
                                            // Event to occur when Setting Dynamic/Specific Data
                                            CraftPresence.GUIS.openScreen(
                                                    new DynamicEditorGui(
                                                            parentScreen, currentValue,
                                                            (attributeName, screenInstance) -> {
                                                                // Event to occur when initializing new data
                                                                final ModuleData defaultServerData = CONFIG.serverData.get("default");
                                                                screenInstance.primaryMessage = screenInstance.originalPrimaryMessage = defaultServerData != null ? defaultServerData.getTextOverride() : "";
                                                            },
                                                            (attributeName, screenInstance) -> {
                                                                // Event to occur when initializing existing data
                                                                final ModuleData defaultServerData = CONFIG.serverData.get("default");
                                                                final ModuleData currentServerData = CONFIG.serverData.get(attributeName);
                                                                screenInstance.isPreliminaryData = currentServerData == null;
                                                                screenInstance.mainTitle = ModUtils.TRANSLATOR.translate("gui.config.title.server.edit_specific_server", attributeName);
                                                                screenInstance.originalPrimaryMessage = defaultServerData != null ? defaultServerData.getTextOverride() : "";
                                                                screenInstance.primaryMessage = currentServerData != null ? currentServerData.getTextOverride() : screenInstance.originalPrimaryMessage;
                                                            },
                                                            (screenInstance, attributeName, inputText) -> {
                                                                // Event to occur when adjusting set data
                                                                final ModuleData defaultServerData = CONFIG.serverData.get("default");
                                                                final ModuleData currentServerData = CONFIG.serverData.getOrDefault(attributeName, defaultServerData);
                                                                currentServerData.setTextOverride(inputText);
                                                                CraftPresence.CONFIG.hasChanged = true;
                                                                CONFIG.serverData.put(attributeName, currentServerData);
                                                                if (!CraftPresence.SERVER.knownAddresses.contains(attributeName)) {
                                                                    CraftPresence.SERVER.knownAddresses.add(attributeName);
                                                                }
                                                            },
                                                            (screenInstance, attributeName, inputText) -> {
                                                                // Event to occur when removing set data
                                                                CraftPresence.CONFIG.hasChanged = true;
                                                                CONFIG.serverData.remove(attributeName);
                                                                CraftPresence.SERVER.knownAddresses.remove(attributeName);
                                                            },
                                                            (attributeName, screenInstance) -> {
                                                                // Event to occur when adding an attachment icon to set data
                                                                final ModuleData defaultServerData = CONFIG.serverData.get("default");
                                                                final ModuleData currentServerData = CONFIG.serverData.get(attributeName);
                                                                final String defaultIcon = defaultServerData != null ? defaultServerData.getIconOverride() : CONFIG.fallbackServerIcon;
                                                                final String specificIcon = currentServerData != null ? currentServerData.getIconOverride() : defaultIcon;
                                                                CraftPresence.GUIS.openScreen(
                                                                        new SelectorGui(
                                                                                screenInstance,
                                                                                ModUtils.TRANSLATOR.translate("gui.config.title.selector.icon"), DiscordAssetUtils.ASSET_LIST.keySet(),
                                                                                specificIcon, attributeName,
                                                                                true, false, RenderType.DiscordAsset,
                                                                                (innerAttributeName, innerCurrentValue) -> {
                                                                                    // Inner-Event to occur when proceeding with adjusted data
                                                                                    final ModuleData defaultInnerServerData = CONFIG.serverData.get("default");
                                                                                    final ModuleData currentInnerServerData = CONFIG.serverData.get(innerAttributeName);
                                                                                    final String defaultMessage = defaultInnerServerData != null ? defaultInnerServerData.getTextOverride() : "";
                                                                                    final String currentMessage = currentInnerServerData != null ? currentInnerServerData.getTextOverride() : "";

                                                                                    CraftPresence.CONFIG.hasChanged = true;
                                                                                    final ModuleData newData = new ModuleData();
                                                                                    if (StringUtils.isNullOrEmpty(currentMessage) || currentMessage.equals(defaultMessage)) {
                                                                                        newData.setTextOverride(defaultMessage);
                                                                                    }
                                                                                    newData.setIconOverride(innerCurrentValue);
                                                                                    CONFIG.serverData.put(innerAttributeName, newData);
                                                                                }, null
                                                                        )
                                                                );
                                                            },
                                                            (attributeName, screenInstance) -> {
                                                                // Event to occur when Hovering over Message Label
                                                                CraftPresence.GUIS.drawMultiLineString(
                                                                        StringUtils.splitTextByNewLine(
                                                                                ModUtils.TRANSLATOR.translate("gui.config.comment.server_messages.server_messages",
                                                                                        CraftPresence.SERVER.generateArgumentMessage())
                                                                        ), screenInstance, true
                                                                );
                                                            }
                                                    )
                                            );
                                        }
                                )
                        ),
                        () -> {
                            if (!serverMessagesButton.isControlEnabled()) {
                                CraftPresence.GUIS.drawMultiLineString(
                                        StringUtils.splitTextByNewLine(
                                                ModUtils.TRANSLATOR.translate("gui.config.message.hover.access",
                                                        ModUtils.TRANSLATOR.translate("gui.config.name.server_messages.server_messages"))
                                        ), this, true
                                );
                            } else {
                                CraftPresence.GUIS.drawMultiLineString(
                                        StringUtils.splitTextByNewLine(
                                                ModUtils.TRANSLATOR.translate("gui.config.comment.server_messages.server_messages",
                                                        CraftPresence.SERVER.generateArgumentMessage())
                                        ), this, true
                                );
                            }
                        }
                )
        );
        // Adding Default Icon Button
        addControl(
                new ExtendedButtonControl(
                        (getScreenWidth() / 2) - 90, CraftPresence.GUIS.getButtonY(5),
                        180, 20,
                        "gui.config.name.server_messages.server_icon",
                        () -> CraftPresence.GUIS.openScreen(
                                new SelectorGui(
                                        currentScreen,
                                        ModUtils.TRANSLATOR.translate("gui.config.title.selector.icon"), DiscordAssetUtils.ASSET_LIST.keySet(),
                                        CONFIG.fallbackServerIcon, null,
                                        true, false, RenderType.DiscordAsset,
                                        (attributeName, currentValue) -> {
                                            CraftPresence.CONFIG.hasChanged = true;
                                            CraftPresence.CONFIG.hasClientPropertiesChanged = true;
                                            CONFIG.fallbackServerIcon = currentValue;
                                        }, null
                                )
                        ),
                        () -> CraftPresence.GUIS.drawMultiLineString(
                                StringUtils.splitTextByNewLine(
                                        ModUtils.TRANSLATOR.translate("gui.config.comment.server_messages.server_icon")
                                ), this, true
                        )
                )
        );
        proceedButton = addControl(
                new ExtendedButtonControl(
                        (getScreenWidth() / 2) - 90, (getScreenHeight() - 30),
                        180, 20,
                        "gui.config.message.button.back",
                        () -> {
                            if (!defaultName.getControlMessage().equals(CONFIG.fallbackServerName)) {
                                CraftPresence.CONFIG.hasChanged = true;
                                CraftPresence.CONFIG.hasClientPropertiesChanged = true;
                                CONFIG.fallbackServerName = defaultName.getControlMessage();
                            }
                            if (!defaultMOTD.getControlMessage().equals(CONFIG.fallbackServerMotd)) {
                                CraftPresence.CONFIG.hasChanged = true;
                                CraftPresence.CONFIG.hasClientPropertiesChanged = true;
                                CONFIG.fallbackServerMotd = defaultMOTD.getControlMessage();
                            }
                            if (!defaultMessage.getControlMessage().equals(defaultServerMessage)) {
                                CraftPresence.CONFIG.hasChanged = true;
                                CraftPresence.CONFIG.hasClientPropertiesChanged = true;
                                final ModuleData defaultServerData = CONFIG.serverData.getOrDefault("default", new ModuleData());
                                defaultServerData.setTextOverride(defaultMessage.getControlMessage());
                                CONFIG.serverData.put("default", defaultServerData);
                            }
                            CraftPresence.GUIS.openScreen(parentScreen);
                        },
                        () -> {
                            if (!proceedButton.isControlEnabled()) {
                                CraftPresence.GUIS.drawMultiLineString(
                                        StringUtils.splitTextByNewLine(
                                                ModUtils.TRANSLATOR.translate("gui.config.message.hover.empty.default")
                                        ), this, true
                                );
                            }
                        }
                )
        );

        super.initializeUi();
    }

    @Override
    public void preRender() {
        final String mainTitle = ModUtils.TRANSLATOR.translate("gui.config.title");
        final String subTitle = ModUtils.TRANSLATOR.translate("gui.config.title.server_messages");
        final String serverNameText = ModUtils.TRANSLATOR.translate("gui.config.name.server_messages.server_name");
        final String serverMOTDText = ModUtils.TRANSLATOR.translate("gui.config.name.server_messages.server_motd");
        final String defaultMessageText = ModUtils.TRANSLATOR.translate("gui.config.message.default.server");

        renderString(mainTitle, (getScreenWidth() / 2f) - (getStringWidth(mainTitle) / 2f), 10, 0xFFFFFF);
        renderString(subTitle, (getScreenWidth() / 2f) - (getStringWidth(subTitle) / 2f), 20, 0xFFFFFF);
        renderString(serverNameText, (getScreenWidth() / 2f) - 130, CraftPresence.GUIS.getButtonY(1, 5), 0xFFFFFF);
        renderString(serverMOTDText, (getScreenWidth() / 2f) - 130, CraftPresence.GUIS.getButtonY(2, 5), 0xFFFFFF);
        renderString(defaultMessageText, (getScreenWidth() / 2f) - 130, CraftPresence.GUIS.getButtonY(3, 5), 0xFFFFFF);

        proceedButton.setControlEnabled(!StringUtils.isNullOrEmpty(defaultMessage.getControlMessage()) || !StringUtils.isNullOrEmpty(defaultName.getControlMessage()) || !StringUtils.isNullOrEmpty(defaultMOTD.getControlMessage()));
        serverMessagesButton.setControlEnabled(CraftPresence.SERVER.enabled);
    }

    @Override
    public void postRender() {
        final String serverNameText = ModUtils.TRANSLATOR.translate("gui.config.name.server_messages.server_name");
        final String serverMOTDText = ModUtils.TRANSLATOR.translate("gui.config.name.server_messages.server_motd");
        final String defaultMessageText = ModUtils.TRANSLATOR.translate("gui.config.message.default.server");
        // Hovering over Default Server Name Label
        if (CraftPresence.GUIS.isMouseOver(getMouseX(), getMouseY(), (getScreenWidth() / 2f) - 130, CraftPresence.GUIS.getButtonY(1, 5), getStringWidth(serverNameText), getFontHeight())) {
            CraftPresence.GUIS.drawMultiLineString(
                    StringUtils.splitTextByNewLine(
                            ModUtils.TRANSLATOR.translate("gui.config.comment.server_messages.server_name")
                    ), this, true
            );
        }
        // Hovering over Default Server MOTD Label
        if (CraftPresence.GUIS.isMouseOver(getMouseX(), getMouseY(), (getScreenWidth() / 2f) - 130, CraftPresence.GUIS.getButtonY(2, 5), getStringWidth(serverMOTDText), getFontHeight())) {
            CraftPresence.GUIS.drawMultiLineString(
                    StringUtils.splitTextByNewLine(
                            ModUtils.TRANSLATOR.translate("gui.config.comment.server_messages.server_motd")
                    ), this, true
            );
        }
        // Hovering over Default Server Message Label
        if (CraftPresence.GUIS.isMouseOver(getMouseX(), getMouseY(), (getScreenWidth() / 2f) - 130, CraftPresence.GUIS.getButtonY(3, 5), getStringWidth(defaultMessageText), getFontHeight())) {
            CraftPresence.GUIS.drawMultiLineString(
                    StringUtils.splitTextByNewLine(
                            ModUtils.TRANSLATOR.translate("gui.config.comment.server_messages.server_messages",
                                    CraftPresence.SERVER.generateArgumentMessage())
                    ), this, true
            );
        }
    }
}
