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
import com.gitlab.cdagaming.craftpresence.core.config.category.Display;
import com.gitlab.cdagaming.craftpresence.utils.discord.assets.DiscordAsset;
import com.gitlab.cdagaming.craftpresence.utils.discord.assets.DiscordAssetUtils;
import com.gitlab.cdagaming.craftpresence.utils.gui.controls.ExtendedButtonControl;
import com.gitlab.cdagaming.craftpresence.utils.gui.controls.ScrollableListControl;
import com.gitlab.cdagaming.craftpresence.utils.gui.impl.ConfigurationGui;
import com.gitlab.cdagaming.craftpresence.utils.gui.impl.DynamicEditorGui;
import com.gitlab.cdagaming.craftpresence.utils.gui.impl.SelectorGui;
import io.github.cdagaming.unicore.utils.StringUtils;

public class DisplaySettingsGui extends ConfigurationGui<Display> {
    private final Display INSTANCE, DEFAULTS;

    DisplaySettingsGui() {
        super("gui.config.title", "gui.config.title.display_settings");
        DEFAULTS = getCurrentData().getDefaults();
        INSTANCE = getCurrentData().copy();
    }

    @Override
    protected void appendControls() {
        super.appendControls();

        // Adding Presence Editor Button
        childFrame.addControl(
                new ExtendedButtonControl(
                        (getScreenWidth() / 2) - 90, getButtonY(0),
                        180, 20,
                        "gui.config.title.editor.presence",
                        () -> openScreen(
                                new PresenceEditorGui(
                                        getInstanceData().presenceData,
                                        getDefaultData().presenceData,
                                        true,
                                        (output) -> getInstanceData().presenceData.transferFrom(output)
                                )
                        ),
                        () -> drawMultiLineString(
                                StringUtils.splitTextByNewLine(
                                        Constants.TRANSLATOR.translate("gui.config.message.hover.presence_editor")
                                )
                        )
                )
        );

        // Adding Dynamic Icons Button
        childFrame.addControl(
                new ExtendedButtonControl(
                        (getScreenWidth() / 2) - 90, getButtonY(1),
                        180, 20,
                        "gui.config.name.display.dynamic_icons",
                        () -> openScreen(
                                new SelectorGui(
                                        Constants.TRANSLATOR.translate("gui.config.title.selector.icon"), DiscordAssetUtils.CUSTOM_ASSET_LIST.keySet(),
                                        null, null,
                                        true, true, ScrollableListControl.RenderType.CustomDiscordAsset,
                                        null,
                                        (currentValue, parentScreen) -> {
                                            // Event to occur when Setting Dynamic/Specific Data
                                            openScreen(
                                                    new DynamicEditorGui(
                                                            currentValue,
                                                            (attributeName, screenInstance) -> {
                                                                // Event to occur when initializing new data
                                                                screenInstance.primaryText = Constants.TRANSLATOR.translate("gui.config.message.editor.url");
                                                                screenInstance.maxPrimaryLength = 32767;
                                                                screenInstance.secondaryText = Constants.TRANSLATOR.translate("gui.config.message.editor.label");
                                                                screenInstance.maxSecondaryLength = 32;
                                                                screenInstance.primaryMessage = screenInstance.originalPrimaryMessage = getInstanceData().dynamicIcons.getOrDefault("default", "");
                                                            },
                                                            (attributeName, screenInstance) -> {
                                                                // Event to occur when initializing existing data
                                                                screenInstance.primaryText = Constants.TRANSLATOR.translate("gui.config.message.editor.url");
                                                                screenInstance.maxPrimaryLength = 32767;
                                                                screenInstance.secondaryText = Constants.TRANSLATOR.translate("gui.config.message.editor.label");
                                                                screenInstance.maxSecondaryLength = 32;
                                                                screenInstance.mainTitle = Constants.TRANSLATOR.translate("gui.config.title.display.edit_specific_icon", attributeName);
                                                                screenInstance.originalPrimaryMessage = getInstanceData().dynamicIcons.getOrDefault("default", "");
                                                                screenInstance.primaryMessage = getInstanceData().dynamicIcons.getOrDefault(attributeName, screenInstance.originalPrimaryMessage);
                                                            },
                                                            (screenInstance, attributeName, inputText) -> {
                                                                // Event to occur when adjusting set data
                                                                getInstanceData().dynamicIcons.put(attributeName, inputText);
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
                                                                getInstanceData().dynamicIcons.remove(attributeName);
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
                                                                                Constants.TRANSLATOR.translate("gui.config.comment.display.dynamic_icons")
                                                                        )
                                                                );
                                                            },
                                                            (attributeName, screenInstance) -> {
                                                                // Event to occur when Hovering over Secondary Label
                                                                screenInstance.drawMultiLineString(
                                                                        StringUtils.splitTextByNewLine(
                                                                                Constants.TRANSLATOR.translate("gui.config.comment.display.dynamic_icons")
                                                                        )
                                                                );
                                                            }
                                                    ), parentScreen
                                            );
                                        }
                                )
                        ),
                        () -> drawMultiLineString(
                                StringUtils.splitTextByNewLine(
                                        Constants.TRANSLATOR.translate("gui.config.comment.display.dynamic_icons")
                                )
                        )
                )
        );

        // Adding Dynamic Variables Button
        childFrame.addControl(
                new ExtendedButtonControl(
                        (getScreenWidth() / 2) - 90, getButtonY(2),
                        180, 20,
                        "gui.config.name.display.dynamic_variables",
                        () -> openScreen(
                                new SelectorGui(
                                        Constants.TRANSLATOR.translate("gui.config.title.selector.item"), getInstanceData().dynamicVariables.keySet(),
                                        null, null,
                                        true, true, ScrollableListControl.RenderType.None,
                                        null,
                                        (currentValue, parentScreen) -> {
                                            // Event to occur when Setting Dynamic/Specific Data
                                            openScreen(
                                                    new DynamicEditorGui(
                                                            currentValue,
                                                            (attributeName, screenInstance) -> {
                                                                // Event to occur when initializing new data
                                                                screenInstance.maxPrimaryLength = 32767;
                                                                screenInstance.maxSecondaryLength = 32;
                                                                screenInstance.primaryMessage = screenInstance.originalPrimaryMessage = getInstanceData().dynamicVariables.getOrDefault("default", "");
                                                            },
                                                            (attributeName, screenInstance) -> {
                                                                // Event to occur when initializing existing data
                                                                screenInstance.maxPrimaryLength = 32767;
                                                                screenInstance.maxSecondaryLength = 32;
                                                                screenInstance.mainTitle = Constants.TRANSLATOR.translate("gui.config.title.item.edit_specific_item", attributeName);
                                                                screenInstance.originalPrimaryMessage = getInstanceData().dynamicVariables.getOrDefault("default", "");
                                                                screenInstance.primaryMessage = getInstanceData().dynamicVariables.getOrDefault(attributeName, screenInstance.originalPrimaryMessage);
                                                            },
                                                            (screenInstance, attributeName, inputText) -> {
                                                                // Event to occur when adjusting set data
                                                                getInstanceData().dynamicVariables.put(attributeName, inputText);
                                                            },
                                                            (screenInstance, attributeName, inputText) -> {
                                                                // Event to occur when removing set data
                                                                getInstanceData().dynamicVariables.remove(attributeName);
                                                            }, null,
                                                            (attributeName, screenInstance) -> {
                                                                // Event to occur when Hovering over Primary Label
                                                                screenInstance.drawMultiLineString(
                                                                        StringUtils.splitTextByNewLine(
                                                                                Constants.TRANSLATOR.translate("gui.config.comment.display.dynamic_variables")
                                                                        )
                                                                );
                                                            },
                                                            (attributeName, screenInstance) -> {
                                                                // Event to occur when Hovering over Secondary Label
                                                                screenInstance.drawMultiLineString(
                                                                        StringUtils.splitTextByNewLine(
                                                                                Constants.TRANSLATOR.translate("gui.config.comment.display.dynamic_variables")
                                                                        )
                                                                );
                                                            }
                                                    ), parentScreen
                                            );
                                        }
                                )
                        ),
                        () -> drawMultiLineString(
                                StringUtils.splitTextByNewLine(
                                        Constants.TRANSLATOR.translate("gui.config.comment.display.dynamic_variables")
                                )
                        )
                )
        );
    }

    @Override
    protected boolean allowedToReset() {
        return true;
    }

    @Override
    protected Display getInstanceData() {
        return INSTANCE;
    }

    @Override
    protected Display getCurrentData() {
        return CraftPresence.CONFIG.displaySettings;
    }

    @Override
    protected Display getDefaultData() {
        // Hotfix: Preserve `dynamicIcons` as a cache setting
        DEFAULTS.dynamicIcons = getCurrentData().dynamicIcons;

        return DEFAULTS;
    }
}
