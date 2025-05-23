/*
 * MIT License
 *
 * Copyright (c) 2018 - 2025 CDAGaming (cstack2011@yahoo.com)
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
import com.gitlab.cdagaming.craftpresence.core.integrations.discord.assets.DiscordAsset;
import com.gitlab.cdagaming.craftpresence.core.integrations.discord.assets.DiscordAssetUtils;
import com.gitlab.cdagaming.craftpresence.utils.gui.controls.DynamicScrollableList;
import com.gitlab.cdagaming.craftpresence.utils.gui.controls.PresenceVisualizer;
import com.gitlab.cdagaming.craftpresence.utils.gui.impl.ConfigurationGui;
import com.gitlab.cdagaming.craftpresence.utils.gui.impl.DynamicEditorGui;
import com.gitlab.cdagaming.craftpresence.utils.gui.impl.DynamicSelectorGui;
import com.gitlab.cdagaming.unilib.utils.gui.controls.ExtendedButtonControl;
import io.github.cdagaming.unicore.utils.StringUtils;

public class DisplaySettingsGui extends ConfigurationGui<Display> {
    private final Display INSTANCE, DEFAULTS;
    private final PresenceVisualizer visualizer;

    DisplaySettingsGui() {
        super(
                Constants.TRANSLATOR.translate("gui.config.title"),
                Constants.TRANSLATOR.translate("gui.config.title.display_settings")
        );
        DEFAULTS = getCurrentData().getDefaults();
        INSTANCE = getCurrentData().copy();
        visualizer = new PresenceVisualizer(this, true);
    }

    @Override
    protected void appendControls() {
        super.appendControls();

        // Adding Presence Editor Button
        childFrame.addControl(
                new ExtendedButtonControl(
                        (getScreenWidth() / 2) - 90, getButtonY(0),
                        180, 20,
                        Constants.TRANSLATOR.translate("gui.config.title.editor.presence"),
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
                        Constants.TRANSLATOR.translate("gui.config.name.display.dynamic_icons"),
                        () -> openScreen(
                                new DynamicSelectorGui(
                                        Constants.TRANSLATOR.translate("gui.config.title.selector.icon"), DiscordAssetUtils.CUSTOM_ASSET_LIST.keySet(),
                                        null, null,
                                        true, true, DynamicScrollableList.RenderType.CustomDiscordAsset,
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
                                                                screenInstance.primaryMessage = screenInstance.originalPrimaryMessage = "";
                                                            },
                                                            (attributeName, screenInstance) -> {
                                                                // Event to occur when initializing existing data
                                                                screenInstance.primaryText = Constants.TRANSLATOR.translate("gui.config.message.editor.url");
                                                                screenInstance.maxPrimaryLength = 32767;
                                                                screenInstance.secondaryText = Constants.TRANSLATOR.translate("gui.config.message.editor.label");
                                                                screenInstance.maxSecondaryLength = 32;
                                                                screenInstance.setScreenTitle(Constants.TRANSLATOR.translate("gui.config.title.display.edit_specific_icon", attributeName));
                                                                screenInstance.originalPrimaryMessage = "";
                                                                screenInstance.primaryMessage = getInstanceData().dynamicIcons.getOrDefault(attributeName, screenInstance.originalPrimaryMessage);
                                                            },
                                                            (screenInstance) -> {
                                                                // Event to occur when adjusting set data
                                                                final String attributeName = screenInstance.getSecondaryEntry();
                                                                final String inputText = screenInstance.getPrimaryEntry();

                                                                getInstanceData().dynamicIcons.put(attributeName, inputText);
                                                                final DiscordAsset asset = new DiscordAsset()
                                                                        .setName(attributeName)
                                                                        .setUrl(inputText)
                                                                        .setType(DiscordAsset.AssetType.CUSTOM);
                                                                if (!DiscordAssetUtils.CUSTOM_ASSET_LIST.containsKey(asset.getName())) {
                                                                    DiscordAssetUtils.CUSTOM_ASSET_LIST.put(asset.getName(), asset);
                                                                }
                                                                // If a Discord Icon exists with the same name, give priority to the custom one
                                                                DiscordAssetUtils.ASSET_LIST.put(asset.getName(), asset);
                                                            },
                                                            (screenInstance) -> {
                                                                // Event to occur when removing set data
                                                                final String attributeName = screenInstance.getSecondaryEntry();

                                                                getInstanceData().dynamicIcons.remove(attributeName);
                                                                if (DiscordAssetUtils.CUSTOM_ASSET_LIST.containsKey(attributeName)) {
                                                                    DiscordAssetUtils.CUSTOM_ASSET_LIST.remove(attributeName);
                                                                    DiscordAssetUtils.ASSET_LIST.remove(attributeName);
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
                        Constants.TRANSLATOR.translate("gui.config.name.display.dynamic_variables"),
                        () -> openScreen(
                                new DynamicSelectorGui(
                                        Constants.TRANSLATOR.translate("gui.config.title.selector.item"), getInstanceData().dynamicVariables.keySet(),
                                        null, null,
                                        true, true, DynamicScrollableList.RenderType.None,
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
                                                                screenInstance.primaryMessage = screenInstance.originalPrimaryMessage = "";
                                                            },
                                                            (attributeName, screenInstance) -> {
                                                                // Event to occur when initializing existing data
                                                                screenInstance.maxPrimaryLength = 32767;
                                                                screenInstance.maxSecondaryLength = 32;
                                                                screenInstance.setScreenTitle(Constants.TRANSLATOR.translate("gui.config.title.item.edit_specific_item", attributeName));
                                                                screenInstance.originalPrimaryMessage = "";
                                                                screenInstance.primaryMessage = getInstanceData().dynamicVariables.getOrDefault(attributeName, screenInstance.originalPrimaryMessage);
                                                            },
                                                            (screenInstance) -> {
                                                                // Event to occur when adjusting set data
                                                                final String attributeName = screenInstance.getSecondaryEntry();
                                                                final String inputText = screenInstance.getPrimaryEntry();

                                                                getInstanceData().dynamicVariables.put(attributeName, inputText);
                                                            },
                                                            (screenInstance) -> {
                                                                // Event to occur when removing set data
                                                                final String attributeName = screenInstance.getSecondaryEntry();

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

        visualizer.setupVisualizer(
                3,
                true,
                childFrame,
                () -> CraftPresence.CLIENT.PRESENCE
        );
    }

    @Override
    public void postRender() {
        super.postRender();
        visualizer.postRender(childFrame);
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
