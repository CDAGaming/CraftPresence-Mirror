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
import com.gitlab.cdagaming.craftpresence.impl.Pair;
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
public class BiomeSettingsGui extends ExtendedScreen {
    private ExtendedButtonControl proceedButton, biomeMessagesButton;
    private ExtendedTextControl defaultMessage;

    BiomeSettingsGui(GuiScreen parentScreen) {
        super(parentScreen);
    }

    @Override
    public void initializeUi() {
        final Pair<String, String> defaultData = CraftPresence.CONFIG.biomeMessages.get("default");
        final String defaultBiomeMessage = defaultData != null ? defaultData.getFirst() : "";

        defaultMessage = addControl(
                new ExtendedTextControl(
                        getFontRenderer(),
                        (getScreenWidth() / 2) + 3, CraftPresence.GUIS.getButtonY(1),
                        180, 20
                )
        );
        defaultMessage.setControlMessage(defaultBiomeMessage);

        biomeMessagesButton = addControl(
                new ExtendedButtonControl(
                        (getScreenWidth() / 2) - 90, CraftPresence.GUIS.getButtonY(2),
                        180, 20,
                        "gui.config.name.biome_messages.biome_messages",
                        () -> CraftPresence.GUIS.openScreen(
                                new SelectorGui(
                                        currentScreen,
                                        ModUtils.TRANSLATOR.translate("gui.config.title.selector.biome"), CraftPresence.BIOMES.BIOME_NAMES,
                                        null, null,
                                        true, true, RenderType.None,
                                        (attributeName, currentValue) -> {
                                            final Pair<String, String> defaultBiomeData = CraftPresence.CONFIG.biomeMessages.get("default");
                                            final Pair<String, String> currentBiomeData = CraftPresence.CONFIG.biomeMessages.get(attributeName);
                                            final String defaultMessage = defaultBiomeData != null ? defaultBiomeData.getFirst() : "";
                                            final String currentMessage = currentBiomeData != null ? currentBiomeData.getFirst() : "";

                                            CraftPresence.CONFIG.hasChanged = true;
                                            final Pair<String, String> newData = new Pair<>();
                                            if (StringUtils.isNullOrEmpty(currentMessage) || currentMessage.equals(defaultMessage)) {
                                                newData.setFirst(defaultMessage);
                                            }
                                            newData.setSecond(currentValue);
                                            CraftPresence.CONFIG.biomeMessages.put(attributeName, newData);
                                        },
                                        (currentValue, parentScreen) -> {
                                            // Event to occur when Setting Dynamic/Specific Data
                                            CraftPresence.GUIS.openScreen(
                                                    new DynamicEditorGui(
                                                            parentScreen, currentValue,
                                                            (attributeName, screenInstance) -> {
                                                                // Event to occur when initializing new data
                                                                final Pair<String, String> defaultBiomeData = CraftPresence.CONFIG.biomeMessages.get("default");
                                                                screenInstance.primaryMessage = screenInstance.originalPrimaryMessage = defaultBiomeData != null ? defaultBiomeData.getFirst() : "";
                                                            },
                                                            (attributeName, screenInstance) -> {
                                                                // Event to occur when initializing existing data
                                                                final Pair<String, String> defaultBiomeData = CraftPresence.CONFIG.biomeMessages.get("default");
                                                                final Pair<String, String> currentBiomeData = CraftPresence.CONFIG.biomeMessages.get(attributeName);
                                                                screenInstance.mainTitle = ModUtils.TRANSLATOR.translate("gui.config.title.biome.edit_specific_biome", attributeName);
                                                                screenInstance.originalPrimaryMessage = defaultBiomeData != null ? defaultBiomeData.getFirst() : "";
                                                                screenInstance.primaryMessage = currentBiomeData != null ? currentBiomeData.getFirst() : screenInstance.originalPrimaryMessage;
                                                            },
                                                            (screenInstance, attributeName, inputText) -> {
                                                                // Event to occur when adjusting set data
                                                                final Pair<String, String> currentBiomeData = CraftPresence.CONFIG.biomeMessages.get(attributeName);
                                                                currentBiomeData.setFirst(inputText);
                                                                CraftPresence.CONFIG.hasChanged = true;
                                                                CraftPresence.CONFIG.biomeMessages.put(attributeName, currentBiomeData);
                                                                if (!CraftPresence.BIOMES.BIOME_NAMES.contains(attributeName)) {
                                                                    CraftPresence.BIOMES.BIOME_NAMES.add(attributeName);
                                                                }
                                                            },
                                                            (screenInstance, attributeName, inputText) -> {
                                                                // Event to occur when removing set data
                                                                CraftPresence.CONFIG.hasChanged = true;
                                                                CraftPresence.CONFIG.biomeMessages.remove(attributeName);
                                                                CraftPresence.BIOMES.BIOME_NAMES.remove(attributeName);
                                                            },
                                                            (attributeName, screenInstance) -> {
                                                                // Event to occur when adding an attachment icon to set data
                                                                final Pair<String, String> defaultBiomeData = CraftPresence.CONFIG.biomeMessages.get("default");
                                                                final Pair<String, String> currentBiomeData = CraftPresence.CONFIG.biomeMessages.get(attributeName);
                                                                final String defaultIcon = defaultBiomeData != null ? defaultBiomeData.getSecond() : CraftPresence.CONFIG.defaultBiomeIcon;
                                                                final String specificIcon = currentBiomeData != null ? currentBiomeData.getSecond() : defaultIcon;
                                                                CraftPresence.GUIS.openScreen(
                                                                        new SelectorGui(
                                                                                screenInstance,
                                                                                ModUtils.TRANSLATOR.translate("gui.config.title.selector.icon"), DiscordAssetUtils.ASSET_LIST.keySet(),
                                                                                specificIcon, attributeName,
                                                                                true, false, RenderType.DiscordAsset,
                                                                                (innerAttributeName, innerCurrentValue) -> {
                                                                                    // Inner-Event to occur when proceeding with adjusted data
                                                                                    final Pair<String, String> defaultInnerBiomeData = CraftPresence.CONFIG.biomeMessages.get("default");
                                                                                    final Pair<String, String> currentInnerBiomeData = CraftPresence.CONFIG.biomeMessages.get(innerAttributeName);
                                                                                    final String defaultMessage = defaultInnerBiomeData != null ? defaultInnerBiomeData.getFirst() : "";
                                                                                    final String currentMessage = currentInnerBiomeData != null ? currentInnerBiomeData.getFirst() : "";

                                                                                    CraftPresence.CONFIG.hasChanged = true;
                                                                                    final Pair<String, String> newData = new Pair<>();
                                                                                    if (StringUtils.isNullOrEmpty(currentMessage) || currentMessage.equals(defaultMessage)) {
                                                                                        newData.setFirst(defaultMessage);
                                                                                    }
                                                                                    newData.setSecond(innerCurrentValue);
                                                                                    CraftPresence.CONFIG.biomeMessages.put(innerAttributeName, newData);
                                                                                }, null
                                                                        )
                                                                );
                                                            },
                                                            (attributeName, screenInstance) -> {
                                                                // Event to occur when Hovering over Message Label
                                                                CraftPresence.GUIS.drawMultiLineString(
                                                                        StringUtils.splitTextByNewLine(
                                                                                ModUtils.TRANSLATOR.translate("gui.config.comment.biome_messages.biome_messages",
                                                                                        CraftPresence.BIOMES.generateArgumentMessage())
                                                                        ), screenInstance, true
                                                                );
                                                            }
                                                    )
                                            );
                                        }
                                )
                        ),
                        () -> {
                            if (!biomeMessagesButton.isControlEnabled()) {
                                CraftPresence.GUIS.drawMultiLineString(
                                        StringUtils.splitTextByNewLine(
                                                ModUtils.TRANSLATOR.translate("gui.config.message.hover.access",
                                                        ModUtils.TRANSLATOR.translate("gui.config.name.general.detect_biome_data"))
                                        ), this, true
                                );
                            } else {
                                CraftPresence.GUIS.drawMultiLineString(
                                        StringUtils.splitTextByNewLine(
                                                ModUtils.TRANSLATOR.translate("gui.config.comment.biome_messages.biome_messages",
                                                        CraftPresence.BIOMES.generateArgumentMessage())
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
                        "gui.config.name.biome_messages.biome_icon",
                        () -> CraftPresence.GUIS.openScreen(
                                new SelectorGui(
                                        currentScreen,
                                        ModUtils.TRANSLATOR.translate("gui.config.title.selector.icon"), DiscordAssetUtils.ASSET_LIST.keySet(),
                                        CraftPresence.CONFIG.defaultBiomeIcon, null,
                                        true, false, RenderType.DiscordAsset,
                                        (attributeName, currentValue) -> {
                                            CraftPresence.CONFIG.hasChanged = true;
                                            CraftPresence.CONFIG.hasClientPropertiesChanged = true;
                                            CraftPresence.CONFIG.defaultBiomeIcon = currentValue;
                                        }, null
                                )
                        ),
                        () -> CraftPresence.GUIS.drawMultiLineString(
                                StringUtils.splitTextByNewLine(
                                        ModUtils.TRANSLATOR.translate("gui.config.comment.biome_messages.biome_icon")
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
                            if (!defaultMessage.getControlMessage().equals(defaultBiomeMessage)) {
                                CraftPresence.CONFIG.hasChanged = true;
                                CraftPresence.CONFIG.hasClientPropertiesChanged = true;
                                final Pair<String, String> defaultBiomeData = CraftPresence.CONFIG.biomeMessages.getOrDefault("default", new Pair<>());
                                defaultBiomeData.setFirst(defaultMessage.getControlMessage());
                                CraftPresence.CONFIG.biomeMessages.put("default", defaultBiomeData);
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
        final String subTitle = ModUtils.TRANSLATOR.translate("gui.config.title.biome_messages");
        final String defaultMessageText = ModUtils.TRANSLATOR.translate("gui.config.message.default.biome");

        renderString(mainTitle, (getScreenWidth() / 2f) - (getStringWidth(mainTitle) / 2f), 10, 0xFFFFFF);
        renderString(subTitle, (getScreenWidth() / 2f) - (getStringWidth(subTitle) / 2f), 20, 0xFFFFFF);
        renderString(defaultMessageText, (getScreenWidth() / 2f) - 130, CraftPresence.GUIS.getButtonY(1, 5), 0xFFFFFF);

        proceedButton.setControlEnabled(!StringUtils.isNullOrEmpty(defaultMessage.getControlMessage()));
        biomeMessagesButton.setControlEnabled(CraftPresence.BIOMES.enabled);
    }

    @Override
    public void postRender() {
        final String defaultMessageText = ModUtils.TRANSLATOR.translate("gui.config.message.default.biome");
        // Hovering over Default Biome Message Label
        if (CraftPresence.GUIS.isMouseOver(getMouseX(), getMouseY(), (getScreenWidth() / 2f) - 130, CraftPresence.GUIS.getButtonY(1, 5), getStringWidth(defaultMessageText), getFontHeight())) {
            CraftPresence.GUIS.drawMultiLineString(
                    StringUtils.splitTextByNewLine(
                            ModUtils.TRANSLATOR.translate("gui.config.comment.biome_messages.biome_messages",
                                    CraftPresence.BIOMES.generateArgumentMessage())
                    ), this, true
            );
        }
    }
}
