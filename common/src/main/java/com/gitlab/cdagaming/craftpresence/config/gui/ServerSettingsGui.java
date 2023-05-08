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

package com.gitlab.cdagaming.craftpresence.config.gui;

import com.gitlab.cdagaming.craftpresence.CraftPresence;
import com.gitlab.cdagaming.craftpresence.ModUtils;
import com.gitlab.cdagaming.craftpresence.config.Config;
import com.gitlab.cdagaming.craftpresence.config.category.Server;
import com.gitlab.cdagaming.craftpresence.config.element.ModuleData;
import com.gitlab.cdagaming.craftpresence.config.element.PresenceData;
import com.gitlab.cdagaming.craftpresence.utils.StringUtils;
import com.gitlab.cdagaming.craftpresence.utils.discord.assets.DiscordAssetUtils;
import com.gitlab.cdagaming.craftpresence.utils.gui.controls.ExtendedButtonControl;
import com.gitlab.cdagaming.craftpresence.utils.gui.controls.ExtendedTextControl;
import com.gitlab.cdagaming.craftpresence.utils.gui.controls.ScrollableListControl.RenderType;
import com.gitlab.cdagaming.craftpresence.utils.gui.impl.ConfigurationGui;
import com.gitlab.cdagaming.craftpresence.utils.gui.impl.DynamicEditorGui;
import com.gitlab.cdagaming.craftpresence.utils.gui.impl.SelectorGui;
import com.gitlab.cdagaming.craftpresence.utils.gui.widgets.TextWidget;
import net.minecraft.client.gui.GuiScreen;

@SuppressWarnings("DuplicatedCode")
public class ServerSettingsGui extends ConfigurationGui<Server> {
    private final Server INSTANCE;
    private final ModuleData defaultData;
    private ExtendedButtonControl serverMessagesButton;
    private ExtendedTextControl defaultMOTD, defaultName, defaultMessage;

    ServerSettingsGui(GuiScreen parentScreen) {
        super(parentScreen, "gui.config.title", "gui.config.title.server_messages");
        INSTANCE = getCurrentData().copy();
        defaultData = getCurrentData().serverData.get("default");
    }

    @Override
    protected void appendControls() {
        super.appendControls();

        final String defaultServerMessage = Config.getProperty(defaultData, "textOverride") != null ? defaultData.getTextOverride() : "";

        defaultName = childFrame.addControl(
                new TextWidget(
                        getFontRenderer(),
                        getButtonY(0),
                        180, 20,
                        "gui.config.name.server_messages.server_name",
                        () -> drawMultiLineString(
                                StringUtils.splitTextByNewLine(
                                        ModUtils.TRANSLATOR.translate("gui.config.comment.server_messages.server_name")
                                )
                        )
                )
        );
        defaultName.setControlMessage(getCurrentData().fallbackServerName);
        defaultMOTD = childFrame.addControl(
                new TextWidget(
                        getFontRenderer(),
                        getButtonY(1),
                        180, 20,
                        "gui.config.name.server_messages.server_motd",
                        () -> drawMultiLineString(
                                StringUtils.splitTextByNewLine(
                                        ModUtils.TRANSLATOR.translate("gui.config.comment.server_messages.server_motd")
                                )
                        )
                )
        );
        defaultMOTD.setControlMessage(getCurrentData().fallbackServerMotd);
        defaultMessage = childFrame.addControl(
                new TextWidget(
                        getFontRenderer(),
                        getButtonY(2),
                        180, 20,
                        "gui.config.message.default.server",
                        () -> drawMultiLineString(
                                StringUtils.splitTextByNewLine(
                                        ModUtils.TRANSLATOR.translate("gui.config.comment.server_messages.server_messages",
                                                CraftPresence.CLIENT.generateArgumentMessage("server.", "world.", "player."))
                                )
                        )
                )
        );
        defaultMessage.setControlMessage(defaultServerMessage);

        serverMessagesButton = childFrame.addControl(
                new ExtendedButtonControl(
                        (getScreenWidth() / 2) - 90, getButtonY(3),
                        180, 20,
                        "gui.config.name.server_messages.server_messages",
                        () -> openScreen(
                                new SelectorGui(
                                        currentScreen,
                                        ModUtils.TRANSLATOR.translate("gui.config.title.selector.server"), CraftPresence.SERVER.knownAddresses,
                                        null, null,
                                        true, true, RenderType.ServerData,
                                        (attributeName, currentValue) -> {
                                            final ModuleData defaultServerData = getCurrentData().serverData.get("default");
                                            final ModuleData currentServerData = getCurrentData().serverData.get(attributeName);
                                            final String defaultMessage = Config.getProperty(defaultServerData, "textOverride") != null ? defaultServerData.getTextOverride() : "";
                                            final String currentMessage = Config.getProperty(currentServerData, "textOverride") != null ? currentServerData.getTextOverride() : "";

                                            CraftPresence.CONFIG.hasChanged = true;
                                            final ModuleData newData = new ModuleData();
                                            if (StringUtils.isNullOrEmpty(currentMessage) || currentMessage.equals(defaultMessage)) {
                                                newData.setTextOverride(defaultMessage);
                                            }
                                            newData.setIconOverride(currentValue);
                                            getCurrentData().serverData.put(attributeName, newData);
                                        },
                                        (currentValue, parentScreen) -> {
                                            // Event to occur when Setting Dynamic/Specific Data
                                            openScreen(
                                                    new DynamicEditorGui(
                                                            parentScreen, currentValue,
                                                            (attributeName, screenInstance) -> {
                                                                // Event to occur when initializing new data
                                                                screenInstance.defaultData = getCurrentData().serverData.get("default");
                                                                screenInstance.primaryMessage = screenInstance.originalPrimaryMessage = Config.getProperty(screenInstance.defaultData, "textOverride") != null ? screenInstance.defaultData.getTextOverride() : "";
                                                            },
                                                            (attributeName, screenInstance) -> {
                                                                // Event to occur when initializing existing data
                                                                screenInstance.defaultData = getCurrentData().serverData.get("default");
                                                                screenInstance.currentData = getCurrentData().serverData.get(attributeName);
                                                                screenInstance.isPreliminaryData = screenInstance.currentData == null;
                                                                screenInstance.mainTitle = ModUtils.TRANSLATOR.translate("gui.config.title.server.edit_specific_server", attributeName);
                                                                screenInstance.originalPrimaryMessage = Config.getProperty(screenInstance.defaultData, "textOverride") != null ? screenInstance.defaultData.getTextOverride() : "";
                                                                screenInstance.primaryMessage = Config.getProperty(screenInstance.currentData, "textOverride") != null ? screenInstance.currentData.getTextOverride() : screenInstance.originalPrimaryMessage;
                                                            },
                                                            (screenInstance, attributeName, inputText) -> {
                                                                // Event to occur when adjusting set data
                                                                screenInstance.currentData.setTextOverride(inputText);
                                                                CraftPresence.CONFIG.hasChanged = true;
                                                                getCurrentData().serverData.put(attributeName, screenInstance.currentData);
                                                                if (!CraftPresence.SERVER.knownAddresses.contains(attributeName)) {
                                                                    CraftPresence.SERVER.knownAddresses.add(attributeName);
                                                                }
                                                            },
                                                            (screenInstance, attributeName, inputText) -> {
                                                                // Event to occur when removing set data
                                                                CraftPresence.CONFIG.hasChanged = true;
                                                                getCurrentData().serverData.remove(attributeName);
                                                                if (!CraftPresence.SERVER.defaultAddresses.contains(attributeName)) {
                                                                    CraftPresence.SERVER.knownAddresses.remove(attributeName);
                                                                }
                                                            },
                                                            (attributeName, screenInstance, isPresenceButton) -> {
                                                                // Event to occur when adding specific info to set data
                                                                if (isPresenceButton) {
                                                                    final PresenceData defaultPresenceData = Config.getProperty(screenInstance.defaultData, "data") != null ? screenInstance.defaultData.getData() : new PresenceData();
                                                                    final PresenceData currentPresenceData = Config.getProperty(screenInstance.currentData, "data") != null ? screenInstance.currentData.getData() : defaultPresenceData;
                                                                    openScreen(
                                                                            new PresenceSettingsGui(
                                                                                    screenInstance, currentPresenceData,
                                                                                    (output) -> screenInstance.currentData.setData(output)
                                                                            )
                                                                    );
                                                                } else {
                                                                    final String defaultIcon = Config.getProperty(screenInstance.defaultData, "iconOverride") != null ? screenInstance.defaultData.getIconOverride() : getCurrentData().fallbackServerIcon;
                                                                    final String specificIcon = Config.getProperty(screenInstance.currentData, "iconOverride") != null ? screenInstance.currentData.getIconOverride() : defaultIcon;
                                                                    openScreen(
                                                                            new SelectorGui(
                                                                                    screenInstance,
                                                                                    ModUtils.TRANSLATOR.translate("gui.config.title.selector.icon"), DiscordAssetUtils.ASSET_LIST.keySet(),
                                                                                    specificIcon, attributeName,
                                                                                    true, false, RenderType.DiscordAsset,
                                                                                    (innerAttributeName, innerCurrentValue) -> {
                                                                                        // Inner-Event to occur when proceeding with adjusted data
                                                                                        screenInstance.currentData.setIconOverride(innerCurrentValue);
                                                                                    }, null
                                                                            )
                                                                    );
                                                                }
                                                            },
                                                            (attributeName, screenInstance) -> {
                                                                // Event to occur when Hovering over Message Label
                                                                drawMultiLineString(
                                                                        StringUtils.splitTextByNewLine(
                                                                                ModUtils.TRANSLATOR.translate("gui.config.comment.server_messages.server_messages",
                                                                                        CraftPresence.CLIENT.generateArgumentMessage("server.", "world.", "player."))
                                                                        )
                                                                );
                                                            }
                                                    )
                                            );
                                        }
                                )
                        ),
                        () -> {
                            if (!serverMessagesButton.isControlEnabled()) {
                                drawMultiLineString(
                                        StringUtils.splitTextByNewLine(
                                                ModUtils.TRANSLATOR.translate("gui.config.message.hover.access",
                                                        ModUtils.TRANSLATOR.translate("gui.config.name.server_messages.server_messages"))
                                        )
                                );
                            } else {
                                drawMultiLineString(
                                        StringUtils.splitTextByNewLine(
                                                ModUtils.TRANSLATOR.translate("gui.config.comment.server_messages.server_messages",
                                                        CraftPresence.CLIENT.generateArgumentMessage("server.", "world.", "player."))
                                        )
                                );
                            }
                        }
                )
        );
        // Adding Default Icon Button
        childFrame.addControl(
                new ExtendedButtonControl(
                        (getScreenWidth() / 2) - 90, getButtonY(4),
                        180, 20,
                        "gui.config.name.server_messages.server_icon",
                        () -> openScreen(
                                new SelectorGui(
                                        currentScreen,
                                        ModUtils.TRANSLATOR.translate("gui.config.title.selector.icon"), DiscordAssetUtils.ASSET_LIST.keySet(),
                                        getCurrentData().fallbackServerIcon, null,
                                        true, false, RenderType.DiscordAsset,
                                        (attributeName, currentValue) -> {
                                            CraftPresence.CONFIG.hasChanged = true;
                                            getCurrentData().fallbackServerIcon = currentValue;
                                        }, null
                                )
                        ),
                        () -> drawMultiLineString(
                                StringUtils.splitTextByNewLine(
                                        ModUtils.TRANSLATOR.translate("gui.config.comment.server_messages.server_icon")
                                )
                        )
                )
        );
        proceedButton.setOnHover(() -> {
            if (!proceedButton.isControlEnabled()) {
                drawMultiLineString(
                        StringUtils.splitTextByNewLine(
                                ModUtils.TRANSLATOR.translate("gui.config.message.hover.empty.default")
                        )
                );
            }
        });
    }

    @Override
    protected boolean canReset() {
        return !getCurrentData().isDefaults();
    }

    @Override
    protected boolean allowedToReset() {
        return true;
    }

    @Override
    protected boolean resetData() {
        return setCurrentData(getCurrentData().getDefaults());
    }

    @Override
    protected boolean canSync() {
        return true;
    }

    @Override
    protected boolean allowedToSync() {
        return true;
    }

    @Override
    protected boolean syncData() {
        return setCurrentData(Config.loadOrCreate().serverSettings);
    }

    @Override
    protected void syncRenderStates() {
        super.syncRenderStates();

        proceedButton.setControlEnabled(!StringUtils.isNullOrEmpty(defaultMessage.getControlMessage()) || !StringUtils.isNullOrEmpty(defaultName.getControlMessage()) || !StringUtils.isNullOrEmpty(defaultMOTD.getControlMessage()));
        serverMessagesButton.setControlEnabled(CraftPresence.SERVER.enabled);
    }

    @Override
    protected void applySettings() {
        final String defaultServerMessage = Config.getProperty(defaultData, "textOverride") != null ? defaultData.getTextOverride() : "";
        if (!defaultName.getControlMessage().equals(getCurrentData().fallbackServerName)) {
            CraftPresence.CONFIG.hasChanged = true;
            getCurrentData().fallbackServerName = defaultName.getControlMessage();
        }
        if (!defaultMOTD.getControlMessage().equals(getCurrentData().fallbackServerMotd)) {
            CraftPresence.CONFIG.hasChanged = true;
            getCurrentData().fallbackServerMotd = defaultMOTD.getControlMessage();
        }
        if (!defaultMessage.getControlMessage().equals(defaultServerMessage)) {
            CraftPresence.CONFIG.hasChanged = true;
            final ModuleData defaultServerData = getCurrentData().serverData.getOrDefault("default", new ModuleData());
            defaultServerData.setTextOverride(defaultMessage.getControlMessage());
            getCurrentData().serverData.put("default", defaultServerData);
        }
    }

    @Override
    protected Server getOriginalData() {
        return INSTANCE;
    }

    @Override
    protected Server getCurrentData() {
        return CraftPresence.CONFIG.serverSettings;
    }

    @Override
    protected boolean setCurrentData(Server data) {
        if (!getCurrentData().equals(data)) {
            getCurrentData().transferFrom(data);
            CraftPresence.CONFIG.hasChanged = true;
            return true;
        }
        return false;
    }
}
