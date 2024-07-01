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

package com.gitlab.cdagaming.craftpresence.config.gui;

import com.gitlab.cdagaming.craftpresence.CraftPresence;
import com.gitlab.cdagaming.craftpresence.core.Constants;
import com.gitlab.cdagaming.craftpresence.core.config.Config;
import com.gitlab.cdagaming.craftpresence.core.config.category.Server;
import com.gitlab.cdagaming.craftpresence.core.config.element.ModuleData;
import com.gitlab.cdagaming.craftpresence.core.config.element.PresenceData;
import com.gitlab.cdagaming.craftpresence.utils.gui.controls.DynamicScrollableList;
import com.gitlab.cdagaming.craftpresence.utils.gui.impl.ConfigurationGui;
import com.gitlab.cdagaming.craftpresence.utils.gui.impl.DynamicEditorGui;
import com.gitlab.cdagaming.craftpresence.utils.gui.impl.DynamicSelectorGui;
import com.gitlab.cdagaming.unilib.utils.gui.controls.ExtendedButtonControl;
import com.gitlab.cdagaming.unilib.utils.gui.widgets.TextWidget;
import io.github.cdagaming.unicore.utils.StringUtils;

@SuppressWarnings("DuplicatedCode")
public class ServerSettingsGui extends ConfigurationGui<Server> {
    private final Server INSTANCE, DEFAULTS;
    private ExtendedButtonControl serverMessagesButton;
    private TextWidget defaultMOTD, defaultName, defaultMessage, defaultIcon;

    ServerSettingsGui() {
        super(
                Constants.TRANSLATOR.translate("gui.config.title"),
                Constants.TRANSLATOR.translate("gui.config.title.server_messages")
        );
        DEFAULTS = getCurrentData().getDefaults();
        INSTANCE = getCurrentData().copy();
    }

    @Override
    protected void appendControls() {
        super.appendControls();

        final ModuleData defaultData = getInstanceData().serverData.get("default");
        final String defaultServerMessage = Config.getProperty(defaultData, "textOverride") != null ? defaultData.getTextOverride() : "";

        defaultName = childFrame.addControl(
                new TextWidget(
                        getFontRenderer(),
                        getButtonY(0),
                        180, 20,
                        () -> getInstanceData().fallbackServerName = defaultName.getControlMessage(),
                        Constants.TRANSLATOR.translate("gui.config.name.server_messages.server_name"),
                        () -> drawMultiLineString(
                                StringUtils.splitTextByNewLine(
                                        Constants.TRANSLATOR.translate("gui.config.comment.server_messages.server_name")
                                )
                        )
                )
        );
        defaultName.setControlMessage(getInstanceData().fallbackServerName);
        defaultMOTD = childFrame.addControl(
                new TextWidget(
                        getFontRenderer(),
                        getButtonY(1),
                        180, 20,
                        () -> getInstanceData().fallbackServerMotd = defaultMOTD.getControlMessage(),
                        Constants.TRANSLATOR.translate("gui.config.name.server_messages.server_motd"),
                        () -> drawMultiLineString(
                                StringUtils.splitTextByNewLine(
                                        Constants.TRANSLATOR.translate("gui.config.comment.server_messages.server_motd")
                                )
                        )
                )
        );
        defaultMOTD.setControlMessage(getInstanceData().fallbackServerMotd);
        defaultMessage = childFrame.addControl(
                new TextWidget(
                        getFontRenderer(),
                        getButtonY(2),
                        180, 20,
                        () -> {
                            final ModuleData defaultServerData = getInstanceData().serverData.getOrDefault("default", new ModuleData());
                            defaultServerData.setTextOverride(defaultMessage.getControlMessage());
                            getInstanceData().serverData.put("default", defaultServerData);
                        },
                        Constants.TRANSLATOR.translate("gui.config.message.default.server"),
                        () -> drawMultiLineString(
                                StringUtils.splitTextByNewLine(
                                        Constants.TRANSLATOR.translate("gui.config.comment.server_messages.server_messages",
                                                CraftPresence.CLIENT.generateArgumentMessage("server.", "world.", "player."))
                                )
                        )
                )
        );
        defaultMessage.setControlMessage(defaultServerMessage);

        // Adding Default Icon Data
        defaultIcon = childFrame.addControl(
                new TextWidget(
                        getFontRenderer(),
                        getButtonY(3),
                        147, 20,
                        () -> getInstanceData().fallbackServerIcon = defaultIcon.getControlMessage(),
                        Constants.TRANSLATOR.translate("gui.config.name.server_messages.server_icon"),
                        () -> drawMultiLineString(
                                StringUtils.splitTextByNewLine(
                                        Constants.TRANSLATOR.translate("gui.config.comment.server_messages.server_icon")
                                )
                        )
                )
        );
        addIconSelector(childFrame, () -> defaultIcon,
                (attributeName, currentValue) -> getInstanceData().fallbackServerIcon = currentValue
        );
        defaultIcon.setControlMessage(getInstanceData().fallbackServerIcon);

        serverMessagesButton = childFrame.addControl(
                new ExtendedButtonControl(
                        (getScreenWidth() / 2) - 90, getButtonY(4),
                        180, 20,
                        Constants.TRANSLATOR.translate("gui.config.name.server_messages.server_messages"),
                        () -> openScreen(
                                new DynamicSelectorGui(
                                        Constants.TRANSLATOR.translate("gui.config.title.selector.server"), CraftPresence.SERVER.knownAddresses,
                                        null, null,
                                        true, true, DynamicScrollableList.RenderType.ServerData,
                                        (attributeName, currentValue) -> {
                                            final ModuleData defaultServerData = getInstanceData().serverData.get("default");
                                            final ModuleData currentServerData = getInstanceData().serverData.get(attributeName);
                                            final String defaultMessage = Config.getProperty(defaultServerData, "textOverride") != null ? defaultServerData.getTextOverride() : "";
                                            final String currentMessage = Config.getProperty(currentServerData, "textOverride") != null ? currentServerData.getTextOverride() : "";

                                            final ModuleData newData = new ModuleData();
                                            if (StringUtils.isNullOrEmpty(currentMessage) || currentMessage.equals(defaultMessage)) {
                                                newData.setTextOverride(defaultMessage);
                                            }
                                            newData.setIconOverride(currentValue);
                                            getInstanceData().serverData.put(attributeName, newData);
                                        },
                                        (currentValue, parentScreen) -> {
                                            // Event to occur when Setting Dynamic/Specific Data
                                            openScreen(
                                                    new DynamicEditorGui(
                                                            currentValue,
                                                            (attributeName, screenInstance) -> {
                                                                // Event to occur when initializing new data
                                                                screenInstance.defaultData = getInstanceData().serverData.get("default");
                                                                screenInstance.primaryMessage = screenInstance.originalPrimaryMessage = Config.getProperty(screenInstance.defaultData, "textOverride") != null ? screenInstance.defaultData.getTextOverride() : "";
                                                            },
                                                            (attributeName, screenInstance) -> {
                                                                // Event to occur when initializing existing data
                                                                screenInstance.defaultData = getInstanceData().serverData.get("default");
                                                                screenInstance.currentData = getInstanceData().serverData.get(attributeName);
                                                                screenInstance.isPreliminaryData = screenInstance.currentData == null;
                                                                screenInstance.mainTitle = Constants.TRANSLATOR.translate("gui.config.title.server.edit_specific_server", attributeName);
                                                                screenInstance.originalPrimaryMessage = Config.getProperty(screenInstance.defaultData, "textOverride") != null ? screenInstance.defaultData.getTextOverride() : "";
                                                                screenInstance.primaryMessage = Config.getProperty(screenInstance.currentData, "textOverride") != null ? screenInstance.currentData.getTextOverride() : screenInstance.originalPrimaryMessage;
                                                            },
                                                            (screenInstance, attributeName, inputText) -> {
                                                                // Event to occur when adjusting set data
                                                                screenInstance.currentData.setTextOverride(inputText);
                                                                getInstanceData().serverData.put(attributeName, screenInstance.currentData);
                                                                if (!CraftPresence.SERVER.knownAddresses.contains(attributeName)) {
                                                                    CraftPresence.SERVER.knownAddresses.add(attributeName);
                                                                }
                                                            },
                                                            (screenInstance, attributeName, inputText) -> {
                                                                // Event to occur when removing set data
                                                                getInstanceData().serverData.remove(attributeName);
                                                                if (!CraftPresence.SERVER.defaultAddresses.contains(attributeName)) {
                                                                    CraftPresence.SERVER.knownAddresses.remove(attributeName);
                                                                }
                                                            },
                                                            (attributeName, screenInstance, isPresenceButton) -> {
                                                                // Event to occur when adding specific info to set data
                                                                if (isPresenceButton) {
                                                                    final PresenceData defaultPresenceData = Config.getProperty(screenInstance.defaultData, "data") != null ? screenInstance.defaultData.getData() : new PresenceData();
                                                                    final PresenceData currentPresenceData = Config.getProperty(screenInstance.currentData, "data") != null ? screenInstance.currentData.getData() : defaultPresenceData;
                                                                    screenInstance.openScreen(
                                                                            new PresenceEditorGui(
                                                                                    currentPresenceData,
                                                                                    defaultPresenceData,
                                                                                    (output) -> screenInstance.currentData.setData(output)
                                                                            )
                                                                    );
                                                                } else {
                                                                    screenInstance.currentData.setIconOverride(attributeName);
                                                                }
                                                            },
                                                            (attributeName, screenInstance) -> {
                                                                // Event to occur when Hovering over Message Label
                                                                screenInstance.drawMultiLineString(
                                                                        StringUtils.splitTextByNewLine(
                                                                                Constants.TRANSLATOR.translate("gui.config.comment.server_messages.server_messages",
                                                                                        CraftPresence.CLIENT.generateArgumentMessage("server.", "world.", "player."))
                                                                        )
                                                                );
                                                            }
                                                    ), parentScreen
                                            );
                                        }
                                )
                        ),
                        () -> {
                            if (!serverMessagesButton.isControlEnabled()) {
                                drawMultiLineString(
                                        StringUtils.splitTextByNewLine(
                                                Constants.TRANSLATOR.translate("gui.config.message.hover.access",
                                                        Constants.TRANSLATOR.translate("gui.config.name.server_messages.server_messages"))
                                        )
                                );
                            } else {
                                drawMultiLineString(
                                        StringUtils.splitTextByNewLine(
                                                Constants.TRANSLATOR.translate("gui.config.comment.server_messages.server_messages",
                                                        CraftPresence.CLIENT.generateArgumentMessage("server.", "world.", "player."))
                                        )
                                );
                            }
                        }
                )
        );

        proceedButton.setOnHover(() -> {
            if (!proceedButton.isControlEnabled()) {
                drawMultiLineString(
                        StringUtils.splitTextByNewLine(
                                Constants.TRANSLATOR.translate("gui.config.message.hover.empty.default")
                        )
                );
            }
        });
    }

    @Override
    protected boolean allowedToReset() {
        return true;
    }

    @Override
    protected void syncRenderStates() {
        super.syncRenderStates();

        proceedButton.setControlEnabled(!StringUtils.isNullOrEmpty(defaultMessage.getControlMessage()) || !StringUtils.isNullOrEmpty(defaultName.getControlMessage()) || !StringUtils.isNullOrEmpty(defaultMOTD.getControlMessage()));
        serverMessagesButton.setControlEnabled(CraftPresence.SERVER.isEnabled());
    }

    @Override
    protected Server getInstanceData() {
        return INSTANCE;
    }

    @Override
    protected Server getCurrentData() {
        return CraftPresence.CONFIG.serverSettings;
    }

    @Override
    protected Server getDefaultData() {
        return DEFAULTS;
    }
}
