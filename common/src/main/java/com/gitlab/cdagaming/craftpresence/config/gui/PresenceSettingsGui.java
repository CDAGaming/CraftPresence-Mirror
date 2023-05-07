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
import com.gitlab.cdagaming.craftpresence.config.category.Display;
import com.gitlab.cdagaming.craftpresence.config.element.Button;
import com.gitlab.cdagaming.craftpresence.config.element.PresenceData;
import com.gitlab.cdagaming.craftpresence.utils.StringUtils;
import com.gitlab.cdagaming.craftpresence.utils.discord.assets.DiscordAsset;
import com.gitlab.cdagaming.craftpresence.utils.discord.assets.DiscordAssetUtils;
import com.gitlab.cdagaming.craftpresence.utils.gui.controls.CheckBoxControl;
import com.gitlab.cdagaming.craftpresence.utils.gui.controls.ExtendedButtonControl;
import com.gitlab.cdagaming.craftpresence.utils.gui.controls.ScrollableListControl;
import com.gitlab.cdagaming.craftpresence.utils.gui.impl.ConfigurationGui;
import com.gitlab.cdagaming.craftpresence.utils.gui.impl.DynamicEditorGui;
import com.gitlab.cdagaming.craftpresence.utils.gui.impl.SelectorGui;
import com.gitlab.cdagaming.craftpresence.utils.gui.widgets.TextWidget;
import net.minecraft.client.gui.GuiScreen;

import java.util.function.Consumer;

@SuppressWarnings("DuplicatedCode")
public class PresenceSettingsGui extends ConfigurationGui<Display> {
    private final Display INSTANCE;
    private final PresenceData PRESENCE;
    private final boolean isDefaultModule;
    private final Consumer<PresenceData> onChangedCallback;
    private TextWidget detailsFormat, gameStateFormat, largeImageFormat, smallImageFormat,
            smallImageKeyFormat, largeImageKeyFormat, startTimeFormat, endTimeFormat;
    private CheckBoxControl useAsMainCheckbox, enabledCheckbox;

    PresenceSettingsGui(GuiScreen parentScreen, PresenceData moduleData, Consumer<PresenceData> changedCallback) {
        super(parentScreen, "gui.config.title", "gui.config.title.presence_settings");
        INSTANCE = getCurrentData().copy();
        PRESENCE = moduleData != null ? moduleData : getCurrentData().presenceData;
        if (PRESENCE.buttons.isEmpty()) {
            PRESENCE.buttons.put("default", new Button(getCurrentData().presenceData.buttons.get("default")));
        }
        isDefaultModule = moduleData != null && moduleData.equals(getCurrentData().presenceData);
        onChangedCallback = changedCallback;
    }

    PresenceSettingsGui(GuiScreen parentScreen) {
        this(parentScreen, CraftPresence.CONFIG.displaySettings.presenceData, (output) ->
                CraftPresence.CONFIG.displaySettings.presenceData = output
        );
    }

    @Override
    protected void appendControls() {
        super.appendControls();

        final int calc1 = (getScreenWidth() / 2) - 183;
        final int calc2 = (getScreenWidth() / 2) + 3;

        final int checkboxCalc1 = (getScreenWidth() / 2) - 160;

        // Page 1 Items
        detailsFormat = childFrame.addControl(
                new TextWidget(
                        getFontRenderer(),
                        getButtonY(0),
                        180, 20,
                        "gui.config.name.display.details_message",
                        () -> drawMultiLineString(
                                StringUtils.splitTextByNewLine(
                                        ModUtils.TRANSLATOR.translate("gui.config.message.presence.generalArgs",
                                                CraftPresence.CLIENT.generateArgumentMessage("general.", "custom."))
                                )
                        )
                )
        );
        gameStateFormat = childFrame.addControl(
                new TextWidget(
                        getFontRenderer(),
                        getButtonY(1),
                        180, 20,
                        "gui.config.name.display.game_state_message",
                        () -> drawMultiLineString(
                                StringUtils.splitTextByNewLine(
                                        ModUtils.TRANSLATOR.translate("gui.config.message.presence.generalArgs",
                                                CraftPresence.CLIENT.generateArgumentMessage("general.", "custom."))
                                )
                        )
                )
        );
        largeImageFormat = childFrame.addControl(
                new TextWidget(
                        getFontRenderer(),
                        getButtonY(2),
                        180, 20,
                        "gui.config.name.display.large_image_message",
                        () -> drawMultiLineString(
                                StringUtils.splitTextByNewLine(
                                        ModUtils.TRANSLATOR.translate("gui.config.message.presence.generalArgs",
                                                CraftPresence.CLIENT.generateArgumentMessage("general.", "custom."))
                                )
                        )
                )
        );
        smallImageFormat = childFrame.addControl(
                new TextWidget(
                        getFontRenderer(),
                        getButtonY(3),
                        180, 20,
                        "gui.config.name.display.small_image_message",
                        () -> drawMultiLineString(
                                StringUtils.splitTextByNewLine(
                                        ModUtils.TRANSLATOR.translate("gui.config.message.presence.generalArgs",
                                                CraftPresence.CLIENT.generateArgumentMessage("general.", "custom."))
                                )
                        )
                )
        );

        detailsFormat.setControlMessage(PRESENCE.details);
        gameStateFormat.setControlMessage(PRESENCE.gameState);
        largeImageFormat.setControlMessage(PRESENCE.largeImageText);
        smallImageFormat.setControlMessage(PRESENCE.smallImageText);

        if (!isDefaultModule) {
            enabledCheckbox = childFrame.addControl(
                    new CheckBoxControl(
                            checkboxCalc1, getButtonY(4),
                            "gui.config.name.display.enabled",
                            PRESENCE.enabled,
                            null,
                            () -> drawMultiLineString(
                                    StringUtils.splitTextByNewLine(
                                            ModUtils.TRANSLATOR.translate("gui.config.comment.display.enabled")
                                    )
                            )
                    )
            );
            useAsMainCheckbox = childFrame.addControl(
                    new CheckBoxControl(
                            calc2, getButtonY(4),
                            "gui.config.name.display.use_as_main",
                            PRESENCE.useAsMain,
                            null,
                            () -> drawMultiLineString(
                                    StringUtils.splitTextByNewLine(
                                            ModUtils.TRANSLATOR.translate("gui.config.comment.display.use_as_main")
                                    )
                            )
                    )
            );
        }

        // Page 2 Items
        smallImageKeyFormat = childFrame.addControl(
                new TextWidget(
                        getFontRenderer(),
                        getButtonY(5),
                        180, 20,
                        "gui.config.name.display.small_image_key",
                        () -> drawMultiLineString(
                                StringUtils.splitTextByNewLine(
                                        ModUtils.TRANSLATOR.translate("gui.config.message.presence.iconArgs",
                                                CraftPresence.CLIENT.generateArgumentMessage("general.", "custom."))
                                )
                        )
                )
        );
        largeImageKeyFormat = childFrame.addControl(
                new TextWidget(
                        getFontRenderer(),
                        getButtonY(6),
                        180, 20,
                        "gui.config.name.display.large_image_key",
                        () -> drawMultiLineString(
                                StringUtils.splitTextByNewLine(
                                        ModUtils.TRANSLATOR.translate("gui.config.message.presence.iconArgs",
                                                CraftPresence.CLIENT.generateArgumentMessage("general.", "custom."))
                                )
                        )
                )
        );

        smallImageKeyFormat.setControlMessage(PRESENCE.smallImageKey);
        largeImageKeyFormat.setControlMessage(PRESENCE.largeImageKey);

        startTimeFormat = childFrame.addControl(
                new TextWidget(
                        getFontRenderer(),
                        getButtonY(7),
                        180, 20,
                        "gui.config.name.display.start_timestamp",
                        () -> drawMultiLineString(
                                StringUtils.splitTextByNewLine(
                                        ModUtils.TRANSLATOR.translate("gui.config.message.presence.generalArgs",
                                                CraftPresence.CLIENT.generateArgumentMessage("general.", "custom."))
                                )
                        )
                )
        );
        endTimeFormat = childFrame.addControl(
                new TextWidget(
                        getFontRenderer(),
                        getButtonY(8),
                        180, 20,
                        "gui.config.name.display.end_timestamp",
                        () -> drawMultiLineString(
                                StringUtils.splitTextByNewLine(
                                        ModUtils.TRANSLATOR.translate("gui.config.message.presence.generalArgs",
                                                CraftPresence.CLIENT.generateArgumentMessage("general.", "custom."))
                                )
                        )
                )
        );

        startTimeFormat.setControlMessage(PRESENCE.startTimestamp);
        endTimeFormat.setControlMessage(PRESENCE.endTimestamp);

        // Button Messages Button
        childFrame.addControl(
                new ExtendedButtonControl(
                        calc1, getButtonY(9),
                        180, 20,
                        "gui.config.name.display.button_messages",
                        () -> openScreen(
                                new SelectorGui(
                                        currentScreen,
                                        ModUtils.TRANSLATOR.translate("gui.config.title.selector.button"), CraftPresence.CLIENT.createButtonsList(PRESENCE.buttons),
                                        null, null,
                                        true, true, ScrollableListControl.RenderType.None,
                                        null,
                                        (currentValue, parentScreen) -> {
                                            // Event to occur when Setting Dynamic/Specific Data
                                            openScreen(
                                                    new DynamicEditorGui(
                                                            parentScreen, currentValue,
                                                            (attributeName, screenInstance) -> {
                                                                // Event to occur when initializing new data
                                                                screenInstance.attributeName = "button_" + CraftPresence.CLIENT.createButtonsList(PRESENCE.buttons).size();
                                                                screenInstance.mainTitle = ModUtils.TRANSLATOR.translate("gui.config.title.editor.add.new.prefilled", screenInstance.attributeName);
                                                                screenInstance.primaryText = ModUtils.TRANSLATOR.translate("gui.config.message.editor.label");
                                                                screenInstance.secondaryText = ModUtils.TRANSLATOR.translate("gui.config.message.editor.url");
                                                                final Button defaultData = PRESENCE.buttons.get("default");
                                                                screenInstance.primaryMessage = screenInstance.originalPrimaryMessage = Config.getProperty(defaultData, "label") != null ? defaultData.label : "";
                                                                screenInstance.secondaryMessage = screenInstance.originalSecondaryMessage = Config.getProperty(defaultData, "url") != null ? defaultData.url : "";
                                                            },
                                                            (attributeName, screenInstance) -> {
                                                                // Event to occur when initializing existing data
                                                                screenInstance.primaryText = ModUtils.TRANSLATOR.translate("gui.config.message.editor.label");
                                                                screenInstance.secondaryText = ModUtils.TRANSLATOR.translate("gui.config.message.editor.url");
                                                                screenInstance.overrideSecondaryRender = true;
                                                                screenInstance.mainTitle = ModUtils.TRANSLATOR.translate("gui.config.title.display.edit_specific_button", attributeName);
                                                                final Button defaultData = PRESENCE.buttons.get("default");
                                                                final Button currentData = PRESENCE.buttons.get(attributeName);
                                                                screenInstance.isPreliminaryData = currentData == null;
                                                                screenInstance.originalPrimaryMessage = Config.getProperty(defaultData, "label") != null ? defaultData.label : "";
                                                                screenInstance.originalSecondaryMessage = Config.getProperty(defaultData, "url") != null ? defaultData.url : "";
                                                                screenInstance.primaryMessage = Config.getProperty(currentData, "label") != null ? currentData.label : screenInstance.originalPrimaryMessage;
                                                                screenInstance.secondaryMessage = Config.getProperty(currentData, "url") != null ? currentData.url : screenInstance.originalSecondaryMessage;
                                                            },
                                                            (screenInstance, secondaryText, inputText) -> {
                                                                // Event to occur when adjusting set data
                                                                CraftPresence.CONFIG.hasChanged = true;
                                                                PRESENCE.buttons.put(screenInstance.attributeName, new Button(inputText, secondaryText));
                                                            },
                                                            (screenInstance, secondaryText, inputText) -> {
                                                                // Event to occur when removing set data
                                                                CraftPresence.CONFIG.hasChanged = true;
                                                                PRESENCE.buttons.remove(screenInstance.attributeName);
                                                            }, null,
                                                            (attributeName, screenInstance) -> {
                                                                // Event to occur when Hovering over Primary Label
                                                                screenInstance.drawMultiLineString(
                                                                        StringUtils.splitTextByNewLine(
                                                                                ModUtils.TRANSLATOR.translate("gui.config.comment.display.button_messages")
                                                                        )
                                                                );
                                                            },
                                                            (attributeName, screenInstance) -> {
                                                                // Event to occur when Hovering over Secondary Label
                                                                screenInstance.drawMultiLineString(
                                                                        StringUtils.splitTextByNewLine(
                                                                                ModUtils.TRANSLATOR.translate("gui.config.comment.display.button_messages")
                                                                        )
                                                                );
                                                            }
                                                    )
                                            );
                                        }
                                )
                        ),
                        () -> drawMultiLineString(
                                StringUtils.splitTextByNewLine(
                                        ModUtils.TRANSLATOR.translate("gui.config.comment.display.button_messages")
                                )
                        )
                )
        );

        // Dynamic Icons Button
        childFrame.addControl(
                new ExtendedButtonControl(
                        calc2, getButtonY(9),
                        180, 20,
                        "gui.config.name.display.dynamic_icons",
                        () -> openScreen(
                                new SelectorGui(
                                        currentScreen,
                                        ModUtils.TRANSLATOR.translate("gui.config.title.selector.icon"), DiscordAssetUtils.CUSTOM_ASSET_LIST.keySet(),
                                        null, null,
                                        true, true, ScrollableListControl.RenderType.CustomDiscordAsset,
                                        null,
                                        (currentValue, parentScreen) -> {
                                            // Event to occur when Setting Dynamic/Specific Data
                                            openScreen(
                                                    new DynamicEditorGui(
                                                            parentScreen, currentValue,
                                                            (attributeName, screenInstance) -> {
                                                                // Event to occur when initializing new data
                                                                screenInstance.primaryText = ModUtils.TRANSLATOR.translate("gui.config.message.editor.url");
                                                                screenInstance.maxPrimaryLength = 32767;
                                                                screenInstance.secondaryText = ModUtils.TRANSLATOR.translate("gui.config.message.editor.label");
                                                                screenInstance.maxSecondaryLength = 32;
                                                                screenInstance.primaryMessage = screenInstance.originalPrimaryMessage = getCurrentData().dynamicIcons.getOrDefault("default", "");
                                                            },
                                                            (attributeName, screenInstance) -> {
                                                                // Event to occur when initializing existing data
                                                                screenInstance.primaryText = ModUtils.TRANSLATOR.translate("gui.config.message.editor.url");
                                                                screenInstance.maxPrimaryLength = 32767;
                                                                screenInstance.secondaryText = ModUtils.TRANSLATOR.translate("gui.config.message.editor.label");
                                                                screenInstance.maxSecondaryLength = 32;
                                                                screenInstance.mainTitle = ModUtils.TRANSLATOR.translate("gui.config.title.display.edit_specific_icon", attributeName);
                                                                screenInstance.originalPrimaryMessage = getCurrentData().dynamicIcons.getOrDefault("default", "");
                                                                screenInstance.primaryMessage = getCurrentData().dynamicIcons.getOrDefault(attributeName, screenInstance.originalPrimaryMessage);
                                                            },
                                                            (screenInstance, attributeName, inputText) -> {
                                                                // Event to occur when adjusting set data
                                                                CraftPresence.CONFIG.hasChanged = true;
                                                                getCurrentData().dynamicIcons.put(attributeName, inputText);
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
                                                                getCurrentData().dynamicIcons.remove(attributeName);
                                                                if (DiscordAssetUtils.CUSTOM_ASSET_LIST.containsKey(attributeName)) {
                                                                    DiscordAssetUtils.CUSTOM_ASSET_LIST.remove(attributeName);
                                                                    if (!attributeName.equalsIgnoreCase("default")) {
                                                                        DiscordAssetUtils.ASSET_LIST.remove(attributeName);
                                                                    }
                                                                }
                                                            }, null,
                                                            (attributeName, screenInstance) -> {
                                                                // Event to occur when Hovering over Primary Label
                                                                screenInstance.drawMultiLineString(
                                                                        StringUtils.splitTextByNewLine(
                                                                                ModUtils.TRANSLATOR.translate("gui.config.comment.display.dynamic_icons")
                                                                        )
                                                                );
                                                            },
                                                            (attributeName, screenInstance) -> {
                                                                // Event to occur when Hovering over Secondary Label
                                                                screenInstance.drawMultiLineString(
                                                                        StringUtils.splitTextByNewLine(
                                                                                ModUtils.TRANSLATOR.translate("gui.config.comment.display.dynamic_icons")
                                                                        )
                                                                );
                                                            }
                                                    )
                                            );
                                        }
                                )
                        ),
                        () -> drawMultiLineString(
                                StringUtils.splitTextByNewLine(
                                        ModUtils.TRANSLATOR.translate("gui.config.comment.display.dynamic_icons")
                                )
                        )
                )
        );

        // Dynamic Variables Button
        childFrame.addControl(
                new ExtendedButtonControl(
                        (getScreenWidth() / 2) - 90, getButtonY(10),
                        180, 20,
                        "gui.config.name.display.dynamic_variables",
                        () -> openScreen(
                                new SelectorGui(
                                        currentScreen,
                                        ModUtils.TRANSLATOR.translate("gui.config.title.selector.item"), getCurrentData().dynamicVariables.keySet(),
                                        null, null,
                                        true, true, ScrollableListControl.RenderType.None,
                                        null,
                                        (currentValue, parentScreen) -> {
                                            // Event to occur when Setting Dynamic/Specific Data
                                            openScreen(
                                                    new DynamicEditorGui(
                                                            parentScreen, currentValue,
                                                            (attributeName, screenInstance) -> {
                                                                // Event to occur when initializing new data
                                                                screenInstance.maxPrimaryLength = 32767;
                                                                screenInstance.maxSecondaryLength = 32;
                                                                screenInstance.primaryMessage = screenInstance.originalPrimaryMessage = getCurrentData().dynamicVariables.getOrDefault("default", "");
                                                            },
                                                            (attributeName, screenInstance) -> {
                                                                // Event to occur when initializing existing data
                                                                screenInstance.maxPrimaryLength = 32767;
                                                                screenInstance.maxSecondaryLength = 32;
                                                                screenInstance.mainTitle = ModUtils.TRANSLATOR.translate("gui.config.title.item.edit_specific_item", attributeName);
                                                                screenInstance.originalPrimaryMessage = getCurrentData().dynamicVariables.getOrDefault("default", "");
                                                                screenInstance.primaryMessage = getCurrentData().dynamicVariables.getOrDefault(attributeName, screenInstance.originalPrimaryMessage);
                                                            },
                                                            (screenInstance, attributeName, inputText) -> {
                                                                // Event to occur when adjusting set data
                                                                CraftPresence.CONFIG.hasChanged = true;
                                                                getCurrentData().dynamicVariables.put(attributeName, inputText);
                                                            },
                                                            (screenInstance, attributeName, inputText) -> {
                                                                // Event to occur when removing set data
                                                                CraftPresence.CONFIG.hasChanged = true;
                                                                getCurrentData().dynamicVariables.remove(attributeName);
                                                            }, null,
                                                            (attributeName, screenInstance) -> {
                                                                // Event to occur when Hovering over Primary Label
                                                                screenInstance.drawMultiLineString(
                                                                        StringUtils.splitTextByNewLine(
                                                                                ModUtils.TRANSLATOR.translate("gui.config.comment.display.dynamic_variables")
                                                                        )
                                                                );
                                                            },
                                                            (attributeName, screenInstance) -> {
                                                                // Event to occur when Hovering over Secondary Label
                                                                screenInstance.drawMultiLineString(
                                                                        StringUtils.splitTextByNewLine(
                                                                                ModUtils.TRANSLATOR.translate("gui.config.comment.display.dynamic_variables")
                                                                        )
                                                                );
                                                            }
                                                    )
                                            );
                                        }
                                )
                        ),
                        () -> drawMultiLineString(
                                StringUtils.splitTextByNewLine(
                                        ModUtils.TRANSLATOR.translate("gui.config.comment.display.dynamic_variables")
                                )
                        )
                )
        );
    }

    @Override
    protected void applySettings() {
        if (!detailsFormat.getControlMessage().equals(PRESENCE.details)) {
            CraftPresence.CONFIG.hasChanged = true;
            PRESENCE.details = detailsFormat.getControlMessage();
        }
        if (!gameStateFormat.getControlMessage().equals(PRESENCE.gameState)) {
            CraftPresence.CONFIG.hasChanged = true;
            PRESENCE.gameState = gameStateFormat.getControlMessage();
        }
        if (!largeImageFormat.getControlMessage().equals(PRESENCE.largeImageText)) {
            CraftPresence.CONFIG.hasChanged = true;
            PRESENCE.largeImageText = largeImageFormat.getControlMessage();
        }
        if (!smallImageFormat.getControlMessage().equals(PRESENCE.smallImageText)) {
            CraftPresence.CONFIG.hasChanged = true;
            PRESENCE.smallImageText = smallImageFormat.getControlMessage();
        }
        if (!isDefaultModule) {
            if (enabledCheckbox.isChecked() != PRESENCE.enabled) {
                CraftPresence.CONFIG.hasChanged = true;
                PRESENCE.enabled = enabledCheckbox.isChecked();
            }
            if (useAsMainCheckbox.isChecked() != PRESENCE.useAsMain) {
                CraftPresence.CONFIG.hasChanged = true;
                PRESENCE.useAsMain = useAsMainCheckbox.isChecked();
            }
        }
        if (!largeImageKeyFormat.getControlMessage().equals(PRESENCE.largeImageKey)) {
            CraftPresence.CONFIG.hasChanged = true;
            PRESENCE.largeImageKey = largeImageKeyFormat.getControlMessage();
        }
        if (!smallImageKeyFormat.getControlMessage().equals(PRESENCE.smallImageKey)) {
            CraftPresence.CONFIG.hasChanged = true;
            PRESENCE.smallImageKey = smallImageKeyFormat.getControlMessage();
        }
        if (!startTimeFormat.getControlMessage().equals(PRESENCE.startTimestamp)) {
            CraftPresence.CONFIG.hasChanged = true;
            PRESENCE.startTimestamp = startTimeFormat.getControlMessage();
        }
        if (!endTimeFormat.getControlMessage().equals(PRESENCE.endTimestamp)) {
            CraftPresence.CONFIG.hasChanged = true;
            PRESENCE.endTimestamp = endTimeFormat.getControlMessage();
        }
        if (onChangedCallback != null) {
            onChangedCallback.accept(PRESENCE);
        }
    }

    @Override
    protected Display getOriginalData() {
        return INSTANCE;
    }

    @Override
    protected Display getCurrentData() {
        return CraftPresence.CONFIG.displaySettings;
    }

    @Override
    protected boolean setCurrentData(Display data) {
        if (!getCurrentData().equals(data)) {
            getCurrentData().transferFrom(data);
            CraftPresence.CONFIG.hasChanged = true;
            return true;
        }
        return false;
    }
}
