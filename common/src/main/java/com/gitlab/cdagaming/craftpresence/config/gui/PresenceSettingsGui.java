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
import com.gitlab.cdagaming.craftpresence.utils.gui.controls.ExtendedTextControl;
import com.gitlab.cdagaming.craftpresence.utils.gui.controls.ScrollableListControl;
import com.gitlab.cdagaming.craftpresence.utils.gui.impl.DynamicEditorGui;
import com.gitlab.cdagaming.craftpresence.utils.gui.impl.SelectorGui;
import com.gitlab.cdagaming.craftpresence.utils.gui.integrations.PaginatedScreen;
import net.minecraft.client.gui.GuiScreen;

import java.util.function.Consumer;

@SuppressWarnings("DuplicatedCode")
public class PresenceSettingsGui extends PaginatedScreen {
    private final Display CONFIG;
    private final PresenceData PRESENCE;
    private final Button DEFAULT_BUTTON;
    private final boolean isDefaultModule;
    private final Consumer<PresenceData> onChangedCallback;
    private ExtendedTextControl detailsFormat, gameStateFormat, largeImageFormat, smallImageFormat,
            smallImageKeyFormat, largeImageKeyFormat, startTimeFormat, endTimeFormat;
    private CheckBoxControl useAsMainCheckbox, enabledCheckbox;

    PresenceSettingsGui(GuiScreen parentScreen, PresenceData moduleData, Consumer<PresenceData> changedCallback) {
        super(parentScreen);
        CONFIG = CraftPresence.CONFIG.displaySettings;
        PRESENCE = moduleData != null ? moduleData : CONFIG.presenceData;
        if (PRESENCE.buttons.isEmpty()) {
            PRESENCE.buttons.put("default", new Button(CONFIG.presenceData.buttons.get("default")));
        }
        DEFAULT_BUTTON = PRESENCE.buttons.get("default");
        isDefaultModule = moduleData != null && moduleData.equals(CONFIG.presenceData);
        onChangedCallback = changedCallback;
    }

    PresenceSettingsGui(GuiScreen parentScreen) {
        this(parentScreen, CraftPresence.CONFIG.displaySettings.presenceData, (output) ->
                CraftPresence.CONFIG.displaySettings.presenceData = output
        );
    }

    @Override
    public void initializeUi() {
        final int calc1 = (getScreenWidth() / 2) - 183;
        final int calc2 = (getScreenWidth() / 2) + 3;

        final int checkboxCalc1 = (getScreenWidth() / 2) - 160;

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

        detailsFormat.setControlMessage(PRESENCE.details);
        gameStateFormat.setControlMessage(PRESENCE.gameState);
        largeImageFormat.setControlMessage(PRESENCE.largeImageText);
        smallImageFormat.setControlMessage(PRESENCE.smallImageText);

        if (!isDefaultModule) {
            enabledCheckbox = addControl(
                    new CheckBoxControl(
                            checkboxCalc1, CraftPresence.GUIS.getButtonY(5),
                            "gui.config.name.display.enabled",
                            PRESENCE.enabled,
                            null,
                            () -> CraftPresence.GUIS.drawMultiLineString(
                                    StringUtils.splitTextByNewLine(
                                            ModUtils.TRANSLATOR.translate("gui.config.comment.display.enabled")
                                    ), this, true
                            )
                    ), startPage
            );
            useAsMainCheckbox = addControl(
                    new CheckBoxControl(
                            calc2, CraftPresence.GUIS.getButtonY(5),
                            "gui.config.name.display.use_as_main",
                            PRESENCE.useAsMain,
                            null,
                            () -> CraftPresence.GUIS.drawMultiLineString(
                                    StringUtils.splitTextByNewLine(
                                            ModUtils.TRANSLATOR.translate("gui.config.comment.display.use_as_main")
                                    ), this, true
                            )
                    ), startPage
            );
        }

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

        smallImageKeyFormat.setControlMessage(PRESENCE.smallImageKey);
        largeImageKeyFormat.setControlMessage(PRESENCE.largeImageKey);

        startTimeFormat = addControl(
                new ExtendedTextControl(
                        getFontRenderer(),
                        calc2, CraftPresence.GUIS.getButtonY(3),
                        180, 20
                ), startPage + 1
        );
        endTimeFormat = addControl(
                new ExtendedTextControl(
                        getFontRenderer(),
                        calc2, CraftPresence.GUIS.getButtonY(4),
                        180, 20
                ), startPage + 1
        );

        startTimeFormat.setControlMessage(PRESENCE.startTimestamp);
        endTimeFormat.setControlMessage(PRESENCE.endTimestamp);

        // Button Messages Button
        addControl(
                new ExtendedButtonControl(
                        calc1, CraftPresence.GUIS.getButtonY(5),
                        180, 20,
                        "gui.config.name.display.button_messages",
                        () -> CraftPresence.GUIS.openScreen(
                                new SelectorGui(
                                        currentScreen,
                                        ModUtils.TRANSLATOR.translate("gui.config.title.selector.button"), CraftPresence.CLIENT.createButtonsList(PRESENCE.buttons),
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
                                                                screenInstance.attributeName = "button_" + CraftPresence.CLIENT.createButtonsList(PRESENCE.buttons).size();
                                                                screenInstance.mainTitle = ModUtils.TRANSLATOR.translate("gui.config.title.editor.add.new.prefilled", screenInstance.attributeName);
                                                                screenInstance.primaryText = ModUtils.TRANSLATOR.translate("gui.config.message.editor.label");
                                                                screenInstance.secondaryText = ModUtils.TRANSLATOR.translate("gui.config.message.editor.url");
                                                                final Button defaultData = DEFAULT_BUTTON;
                                                                screenInstance.primaryMessage = screenInstance.originalPrimaryMessage = Config.getProperty(defaultData, "label") != null ? defaultData.label : "";
                                                                screenInstance.secondaryMessage = screenInstance.originalSecondaryMessage = Config.getProperty(defaultData, "url") != null ? defaultData.url : "";
                                                            },
                                                            (attributeName, screenInstance) -> {
                                                                // Event to occur when initializing existing data
                                                                screenInstance.primaryText = ModUtils.TRANSLATOR.translate("gui.config.message.editor.label");
                                                                screenInstance.secondaryText = ModUtils.TRANSLATOR.translate("gui.config.message.editor.url");
                                                                screenInstance.overrideSecondaryRender = true;
                                                                screenInstance.mainTitle = ModUtils.TRANSLATOR.translate("gui.config.title.display.edit_specific_button", attributeName);
                                                                final Button defaultData = DEFAULT_BUTTON;
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
                                                                CraftPresence.GUIS.drawMultiLineString(
                                                                        StringUtils.splitTextByNewLine(
                                                                                ModUtils.TRANSLATOR.translate("gui.config.comment.display.button_messages")
                                                                        ), screenInstance, true
                                                                );
                                                            },
                                                            (attributeName, screenInstance) -> {
                                                                // Event to occur when Hovering over Secondary Label
                                                                CraftPresence.GUIS.drawMultiLineString(
                                                                        StringUtils.splitTextByNewLine(
                                                                                ModUtils.TRANSLATOR.translate("gui.config.comment.display.button_messages")
                                                                        ), screenInstance, true
                                                                );
                                                            }
                                                    )
                                            );
                                        }
                                )
                        ),
                        () -> CraftPresence.GUIS.drawMultiLineString(
                                StringUtils.splitTextByNewLine(
                                        ModUtils.TRANSLATOR.translate("gui.config.comment.display.button_messages")
                                ), this, true
                        )
                ), startPage + 1
        );

        // Dynamic Icons Button
        addControl(
                new ExtendedButtonControl(
                        calc2, CraftPresence.GUIS.getButtonY(5),
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
                                                                screenInstance.primaryMessage = screenInstance.originalPrimaryMessage = CONFIG.dynamicIcons.getOrDefault("default", "");
                                                            },
                                                            (attributeName, screenInstance) -> {
                                                                // Event to occur when initializing existing data
                                                                screenInstance.primaryText = ModUtils.TRANSLATOR.translate("gui.config.message.editor.url");
                                                                screenInstance.maxPrimaryLength = 32767;
                                                                screenInstance.secondaryText = ModUtils.TRANSLATOR.translate("gui.config.message.editor.label");
                                                                screenInstance.maxSecondaryLength = 32;
                                                                screenInstance.mainTitle = ModUtils.TRANSLATOR.translate("gui.config.title.display.edit_specific_icon", attributeName);
                                                                screenInstance.originalPrimaryMessage = CONFIG.dynamicIcons.getOrDefault("default", "");
                                                                screenInstance.primaryMessage = CONFIG.dynamicIcons.getOrDefault(attributeName, screenInstance.originalPrimaryMessage);
                                                            },
                                                            (screenInstance, attributeName, inputText) -> {
                                                                // Event to occur when adjusting set data
                                                                CraftPresence.CONFIG.hasChanged = true;
                                                                CONFIG.dynamicIcons.put(attributeName, inputText);
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
                                                                CONFIG.dynamicIcons.remove(attributeName);
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
                                                                        ), screenInstance, true
                                                                );
                                                            },
                                                            (attributeName, screenInstance) -> {
                                                                // Event to occur when Hovering over Secondary Label
                                                                CraftPresence.GUIS.drawMultiLineString(
                                                                        StringUtils.splitTextByNewLine(
                                                                                ModUtils.TRANSLATOR.translate("gui.config.comment.display.dynamic_icons")
                                                                        ), screenInstance, true
                                                                );
                                                            }
                                                    )
                                            );
                                        }
                                )
                        ),
                        () -> CraftPresence.GUIS.drawMultiLineString(
                                StringUtils.splitTextByNewLine(
                                        ModUtils.TRANSLATOR.translate("gui.config.comment.display.dynamic_icons")
                                ), this, true
                        )
                ), startPage + 1
        );

        // Dynamic Variables Button
        addControl(
                new ExtendedButtonControl(
                        (getScreenWidth() / 2) - 90, CraftPresence.GUIS.getButtonY(6),
                        180, 20,
                        "gui.config.name.display.dynamic_variables",
                        () -> CraftPresence.GUIS.openScreen(
                                new SelectorGui(
                                        currentScreen,
                                        ModUtils.TRANSLATOR.translate("gui.config.title.selector.item"), CONFIG.dynamicVariables.keySet(),
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
                                                                screenInstance.maxPrimaryLength = 32767;
                                                                screenInstance.maxSecondaryLength = 32;
                                                                screenInstance.primaryMessage = screenInstance.originalPrimaryMessage = CONFIG.dynamicVariables.getOrDefault("default", "");
                                                            },
                                                            (attributeName, screenInstance) -> {
                                                                // Event to occur when initializing existing data
                                                                screenInstance.maxPrimaryLength = 32767;
                                                                screenInstance.maxSecondaryLength = 32;
                                                                screenInstance.mainTitle = ModUtils.TRANSLATOR.translate("gui.config.title.item.edit_specific_item", attributeName);
                                                                screenInstance.originalPrimaryMessage = CONFIG.dynamicVariables.getOrDefault("default", "");
                                                                screenInstance.primaryMessage = CONFIG.dynamicVariables.getOrDefault(attributeName, screenInstance.originalPrimaryMessage);
                                                            },
                                                            (screenInstance, attributeName, inputText) -> {
                                                                // Event to occur when adjusting set data
                                                                CraftPresence.CONFIG.hasChanged = true;
                                                                CONFIG.dynamicVariables.put(attributeName, inputText);
                                                            },
                                                            (screenInstance, attributeName, inputText) -> {
                                                                // Event to occur when removing set data
                                                                CraftPresence.CONFIG.hasChanged = true;
                                                                CONFIG.dynamicVariables.remove(attributeName);
                                                            }, null,
                                                            (attributeName, screenInstance) -> {
                                                                // Event to occur when Hovering over Primary Label
                                                                CraftPresence.GUIS.drawMultiLineString(
                                                                        StringUtils.splitTextByNewLine(
                                                                                ModUtils.TRANSLATOR.translate("gui.config.comment.display.dynamic_variables")
                                                                        ), screenInstance, true
                                                                );
                                                            },
                                                            (attributeName, screenInstance) -> {
                                                                // Event to occur when Hovering over Secondary Label
                                                                CraftPresence.GUIS.drawMultiLineString(
                                                                        StringUtils.splitTextByNewLine(
                                                                                ModUtils.TRANSLATOR.translate("gui.config.comment.display.dynamic_variables")
                                                                        ), screenInstance, true
                                                                );
                                                            }
                                                    )
                                            );
                                        }
                                )
                        ),
                        () -> CraftPresence.GUIS.drawMultiLineString(
                                StringUtils.splitTextByNewLine(
                                        ModUtils.TRANSLATOR.translate("gui.config.comment.display.dynamic_variables")
                                ), this, true
                        )
                ), startPage + 1
        );

        super.initializeUi();

        backButton.setOnClick(
                () -> {
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
        final String startTimeFormatTitle = ModUtils.TRANSLATOR.translate("gui.config.name.display.start_timestamp");
        final String endTimeFormatTitle = ModUtils.TRANSLATOR.translate("gui.config.name.display.end_timestamp");

        renderString(mainTitle, (getScreenWidth() / 2f) - (getStringWidth(mainTitle) / 2f), 10, 0xFFFFFF);
        renderString(subTitle, (getScreenWidth() / 2f) - (getStringWidth(subTitle) / 2f), 20, 0xFFFFFF);

        renderString(detailsFormatTitle, (getScreenWidth() / 2f) - 160, CraftPresence.GUIS.getButtonY(1, 5), 0xFFFFFF, startPage);
        renderString(gameStateFormatTitle, (getScreenWidth() / 2f) - 160, CraftPresence.GUIS.getButtonY(2, 5), 0xFFFFFF, startPage);
        renderString(largeImageFormatTitle, (getScreenWidth() / 2f) - 160, CraftPresence.GUIS.getButtonY(3, 5), 0xFFFFFF, startPage);
        renderString(smallImageFormatTitle, (getScreenWidth() / 2f) - 160, CraftPresence.GUIS.getButtonY(4, 5), 0xFFFFFF, startPage);

        renderString(smallImageKeyFormatTitle, (getScreenWidth() / 2f) - 160, CraftPresence.GUIS.getButtonY(1, 5), 0xFFFFFF, startPage + 1);
        renderString(largeImageKeyFormatTitle, (getScreenWidth() / 2f) - 160, CraftPresence.GUIS.getButtonY(2, 5), 0xFFFFFF, startPage + 1);
        renderString(startTimeFormatTitle, (getScreenWidth() / 2f) - 160, CraftPresence.GUIS.getButtonY(3, 5), 0xFFFFFF, startPage + 1);
        renderString(endTimeFormatTitle, (getScreenWidth() / 2f) - 160, CraftPresence.GUIS.getButtonY(4, 5), 0xFFFFFF, startPage + 1);

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
        final String startTimeFormatTitle = ModUtils.TRANSLATOR.translate("gui.config.name.display.start_timestamp");
        final String endTimeFormatTitle = ModUtils.TRANSLATOR.translate("gui.config.name.display.end_timestamp");
        if (currentPage == startPage) {
            final boolean hoveringOverDetails = CraftPresence.GUIS.isMouseOver(getMouseX(), getMouseY(), (getScreenWidth() / 2f) - 160, CraftPresence.GUIS.getButtonY(1, 5), getStringWidth(detailsFormatTitle), getFontHeight());
            final boolean hoveringOverGameState = CraftPresence.GUIS.isMouseOver(getMouseX(), getMouseY(), (getScreenWidth() / 2f) - 160, CraftPresence.GUIS.getButtonY(2, 5), getStringWidth(gameStateFormatTitle), getFontHeight());
            final boolean hoveringOverLargeImageFormat = CraftPresence.GUIS.isMouseOver(getMouseX(), getMouseY(), (getScreenWidth() / 2f) - 160, CraftPresence.GUIS.getButtonY(3, 5), getStringWidth(largeImageFormatTitle), getFontHeight());
            final boolean hoveringOverSmallImageFormat = CraftPresence.GUIS.isMouseOver(getMouseX(), getMouseY(), (getScreenWidth() / 2f) - 160, CraftPresence.GUIS.getButtonY(4, 5), getStringWidth(smallImageFormatTitle), getFontHeight());

            if (isDefaultModule && (hoveringOverDetails || hoveringOverGameState || hoveringOverLargeImageFormat || hoveringOverSmallImageFormat)) {
                CraftPresence.GUIS.drawMultiLineString(
                        StringUtils.splitTextByNewLine(
                                ModUtils.TRANSLATOR.translate("gui.config.message.presence.generalArgs",
                                        CraftPresence.CLIENT.generateArgumentMessage("general.", "custom."))
                        ), this, true
                );
            }
        }

        if (currentPage == (startPage + 1)) {
            final boolean hoveringOverSmallImageFormat = CraftPresence.GUIS.isMouseOver(getMouseX(), getMouseY(), (getScreenWidth() / 2f) - 160, CraftPresence.GUIS.getButtonY(1, 5), getStringWidth(smallImageKeyFormatTitle), getFontHeight());
            final boolean hoveringOverLargeImageFormat = CraftPresence.GUIS.isMouseOver(getMouseX(), getMouseY(), (getScreenWidth() / 2f) - 160, CraftPresence.GUIS.getButtonY(2, 5), getStringWidth(largeImageKeyFormatTitle), getFontHeight());
            final boolean hoveringOverStartTimeFormat = CraftPresence.GUIS.isMouseOver(getMouseX(), getMouseY(), (getScreenWidth() / 2f) - 160, CraftPresence.GUIS.getButtonY(3, 5), getStringWidth(startTimeFormatTitle), getFontHeight());
            final boolean hoveringOverEndTimeFormat = CraftPresence.GUIS.isMouseOver(getMouseX(), getMouseY(), (getScreenWidth() / 2f) - 160, CraftPresence.GUIS.getButtonY(4, 5), getStringWidth(endTimeFormatTitle), getFontHeight());

            if (isDefaultModule) {
                if (hoveringOverSmallImageFormat || hoveringOverLargeImageFormat) {
                    CraftPresence.GUIS.drawMultiLineString(
                            StringUtils.splitTextByNewLine(
                                    ModUtils.TRANSLATOR.translate("gui.config.message.presence.iconArgs",
                                            CraftPresence.CLIENT.generateArgumentMessage("general.", "custom."))
                            ), this, true
                    );
                } else if (hoveringOverStartTimeFormat || hoveringOverEndTimeFormat) {
                    CraftPresence.GUIS.drawMultiLineString(
                            StringUtils.splitTextByNewLine(
                                    ModUtils.TRANSLATOR.translate("gui.config.message.presence.generalArgs",
                                            CraftPresence.CLIENT.generateArgumentMessage("general.", "custom."))
                            ), this, true
                    );
                }
            }
        }

        super.postRender();
    }
}
