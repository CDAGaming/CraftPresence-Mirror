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
import com.gitlab.cdagaming.craftpresence.config.category.Dimension;
import com.gitlab.cdagaming.craftpresence.config.element.ModuleData;
import com.gitlab.cdagaming.craftpresence.config.element.PresenceData;
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
public class DimensionSettingsGui extends ExtendedScreen {
    private final Dimension CONFIG;
    private ExtendedButtonControl proceedButton, dimensionMessagesButton;
    private ExtendedTextControl defaultMessage;

    DimensionSettingsGui(GuiScreen parentScreen) {
        super(parentScreen);
        CONFIG = CraftPresence.CONFIG.dimensionSettings;
    }

    @Override
    public void initializeUi() {
        final ModuleData defaultData = CONFIG.dimensionData.get("default");
        final String defaultDimensionMessage = Config.getProperty(defaultData, "textOverride") != null ? defaultData.getTextOverride() : "";

        defaultMessage = addControl(
                new ExtendedTextControl(
                        getFontRenderer(),
                        (getScreenWidth() / 2) + 3, CraftPresence.GUIS.getButtonY(1),
                        180, 20
                )
        );
        defaultMessage.setControlMessage(defaultDimensionMessage);

        dimensionMessagesButton = addControl(
                new ExtendedButtonControl(
                        (getScreenWidth() / 2) - 90, CraftPresence.GUIS.getButtonY(2),
                        180, 20,
                        "gui.config.name.dimension_messages.dimension_messages",
                        () -> CraftPresence.GUIS.openScreen(
                                new SelectorGui(
                                        currentScreen,
                                        ModUtils.TRANSLATOR.translate("gui.config.title.selector.dimension"), CraftPresence.DIMENSIONS.DIMENSION_NAMES,
                                        null, null,
                                        true, true, RenderType.None,
                                        (attributeName, currentValue) -> {
                                            // Event to Occur when proceeding with adjusted data
                                            final ModuleData defaultDimensionData = CONFIG.dimensionData.get("default");
                                            final ModuleData currentDimensionData = CONFIG.dimensionData.get(attributeName);
                                            final String defaultMessage = Config.getProperty(defaultDimensionData, "textOverride") != null ? defaultDimensionData.getTextOverride() : "";
                                            final String currentMessage = Config.getProperty(currentDimensionData, "textOverride") != null ? currentDimensionData.getTextOverride() : "";

                                            CraftPresence.CONFIG.hasChanged = true;
                                            final ModuleData newData = new ModuleData();
                                            if (StringUtils.isNullOrEmpty(currentMessage) || currentMessage.equals(defaultMessage)) {
                                                newData.setTextOverride(defaultMessage);
                                            }
                                            newData.setIconOverride(currentValue);
                                            CONFIG.dimensionData.put(attributeName, newData);
                                        },
                                        (currentValue, parentScreen) -> {
                                            // Event to occur when Setting Dynamic/Specific Data
                                            CraftPresence.GUIS.openScreen(
                                                    new DynamicEditorGui(
                                                            parentScreen, currentValue,
                                                            (attributeName, screenInstance) -> {
                                                                // Event to occur when initializing new data
                                                                screenInstance.defaultData = CONFIG.dimensionData.get("default");
                                                                screenInstance.primaryMessage = screenInstance.originalPrimaryMessage = Config.getProperty(screenInstance.defaultData, "textOverride") != null ? screenInstance.defaultData.getTextOverride() : "";
                                                            },
                                                            (attributeName, screenInstance) -> {
                                                                // Event to occur when initializing existing data
                                                                screenInstance.defaultData = CONFIG.dimensionData.get("default");
                                                                screenInstance.currentData = CONFIG.dimensionData.get(attributeName);
                                                                screenInstance.isPreliminaryData = screenInstance.currentData == null;
                                                                screenInstance.mainTitle = ModUtils.TRANSLATOR.translate("gui.config.title.dimension.edit_specific_dimension", attributeName);
                                                                screenInstance.originalPrimaryMessage = Config.getProperty(screenInstance.defaultData, "textOverride") != null ? screenInstance.defaultData.getTextOverride() : "";
                                                                screenInstance.primaryMessage = Config.getProperty(screenInstance.currentData, "textOverride") != null ? screenInstance.currentData.getTextOverride() : screenInstance.originalPrimaryMessage;
                                                            },
                                                            (screenInstance, attributeName, inputText) -> {
                                                                // Event to occur when adjusting set data
                                                                screenInstance.currentData.setTextOverride(inputText);
                                                                CraftPresence.CONFIG.hasChanged = true;
                                                                CONFIG.dimensionData.put(attributeName, screenInstance.currentData);
                                                                if (!CraftPresence.DIMENSIONS.DIMENSION_NAMES.contains(attributeName)) {
                                                                    CraftPresence.DIMENSIONS.DIMENSION_NAMES.add(attributeName);
                                                                }
                                                            },
                                                            (screenInstance, attributeName, inputText) -> {
                                                                // Event to occur when removing set data
                                                                CraftPresence.CONFIG.hasChanged = true;
                                                                CONFIG.dimensionData.remove(attributeName);
                                                                if (!screenInstance.isPreliminaryData) {
                                                                    CraftPresence.DIMENSIONS.DIMENSION_NAMES.remove(attributeName);
                                                                }
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
                                                                    final String defaultIcon = Config.getProperty(screenInstance.defaultData, "iconOverride") != null ? screenInstance.defaultData.getIconOverride() : CONFIG.fallbackDimensionIcon;
                                                                    final String specificIcon = Config.getProperty(screenInstance.currentData, "iconOverride") != null ? screenInstance.currentData.getIconOverride() : defaultIcon;
                                                                    CraftPresence.GUIS.openScreen(
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
                                                                CraftPresence.GUIS.drawMultiLineString(
                                                                        StringUtils.splitTextByNewLine(
                                                                                ModUtils.TRANSLATOR.translate("gui.config.comment.dimension_messages.dimension_messages",
                                                                                        CraftPresence.CLIENT.generateArgumentMessage("dimension."))
                                                                        ), screenInstance, true
                                                                );
                                                            }
                                                    )
                                            );
                                        }
                                )
                        ),
                        () -> {
                            if (!dimensionMessagesButton.isControlEnabled()) {
                                CraftPresence.GUIS.drawMultiLineString(
                                        StringUtils.splitTextByNewLine(
                                                ModUtils.TRANSLATOR.translate("gui.config.message.hover.access",
                                                        ModUtils.TRANSLATOR.translate("gui.config.name.general.detect_dimension_data"))
                                        ), this, true
                                );
                            } else {
                                CraftPresence.GUIS.drawMultiLineString(
                                        StringUtils.splitTextByNewLine(
                                                ModUtils.TRANSLATOR.translate("gui.config.comment.dimension_messages.dimension_messages",
                                                        CraftPresence.CLIENT.generateArgumentMessage("dimension."))
                                        ), this, true
                                );
                            }
                        }
                )
        );
        // Adding Default Icon Button
        addControl(
                new ExtendedButtonControl(
                        (getScreenWidth() / 2) - 90, CraftPresence.GUIS.getButtonY(3),
                        180, 20,
                        "gui.config.name.dimension_messages.dimension_icon",
                        () -> CraftPresence.GUIS.openScreen(
                                new SelectorGui(
                                        currentScreen,
                                        ModUtils.TRANSLATOR.translate("gui.config.title.selector.icon"), DiscordAssetUtils.ASSET_LIST.keySet(),
                                        CONFIG.fallbackDimensionIcon, null,
                                        true, false, RenderType.DiscordAsset,
                                        (attributeName, currentValue) -> {
                                            CraftPresence.CONFIG.hasChanged = true;
                                            CraftPresence.CONFIG.hasClientPropertiesChanged = true;
                                            CONFIG.fallbackDimensionIcon = currentValue;
                                        }, null
                                )
                        ),
                        () -> CraftPresence.GUIS.drawMultiLineString(
                                StringUtils.splitTextByNewLine(
                                        ModUtils.TRANSLATOR.translate("gui.config.comment.dimension_messages.dimension_icon")
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
                            if (!defaultMessage.getControlMessage().equals(defaultDimensionMessage)) {
                                CraftPresence.CONFIG.hasChanged = true;
                                CraftPresence.CONFIG.hasClientPropertiesChanged = true;
                                final ModuleData defaultDimensionData = CONFIG.dimensionData.getOrDefault("default", new ModuleData());
                                defaultDimensionData.setTextOverride(defaultMessage.getControlMessage());
                                CONFIG.dimensionData.put("default", defaultDimensionData);
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
        final String subTitle = ModUtils.TRANSLATOR.translate("gui.config.title.dimension_messages");
        final String defaultMessageText = ModUtils.TRANSLATOR.translate("gui.config.message.default.dimension");

        renderString(mainTitle, (getScreenWidth() / 2f) - (getStringWidth(mainTitle) / 2f), 10, 0xFFFFFF);
        renderString(subTitle, (getScreenWidth() / 2f) - (getStringWidth(subTitle) / 2f), 20, 0xFFFFFF);
        renderString(defaultMessageText, (getScreenWidth() / 2f) - 140, CraftPresence.GUIS.getButtonY(1, 5), 0xFFFFFF);

        proceedButton.setControlEnabled(!StringUtils.isNullOrEmpty(defaultMessage.getControlMessage()));
        dimensionMessagesButton.setControlEnabled(CraftPresence.DIMENSIONS.enabled);
    }

    @Override
    public void postRender() {
        final String defaultMessageText = ModUtils.TRANSLATOR.translate("gui.config.message.default.dimension");
        // Hovering over Default Dimension Message Label
        if (CraftPresence.GUIS.isMouseOver(getMouseX(), getMouseY(), (getScreenWidth() / 2f) - 140, CraftPresence.GUIS.getButtonY(1, 5), getStringWidth(defaultMessageText), getFontHeight())) {
            CraftPresence.GUIS.drawMultiLineString(
                    StringUtils.splitTextByNewLine(
                            ModUtils.TRANSLATOR.translate("gui.config.comment.dimension_messages.dimension_messages",
                                    CraftPresence.CLIENT.generateArgumentMessage("dimension."))
                    ), this, true
            );
        }
    }
}
