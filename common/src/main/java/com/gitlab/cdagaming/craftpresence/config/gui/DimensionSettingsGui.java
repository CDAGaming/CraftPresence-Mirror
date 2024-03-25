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
import com.gitlab.cdagaming.craftpresence.config.Config;
import com.gitlab.cdagaming.craftpresence.config.category.Dimension;
import com.gitlab.cdagaming.craftpresence.core.Constants;
import com.gitlab.cdagaming.craftpresence.core.config.element.ModuleData;
import com.gitlab.cdagaming.craftpresence.core.config.element.PresenceData;
import com.gitlab.cdagaming.craftpresence.utils.discord.assets.DiscordAssetUtils;
import com.gitlab.cdagaming.craftpresence.utils.gui.controls.ExtendedButtonControl;
import com.gitlab.cdagaming.craftpresence.utils.gui.controls.ScrollableListControl.RenderType;
import com.gitlab.cdagaming.craftpresence.utils.gui.impl.ConfigurationGui;
import com.gitlab.cdagaming.craftpresence.utils.gui.impl.DynamicEditorGui;
import com.gitlab.cdagaming.craftpresence.utils.gui.impl.SelectorGui;
import com.gitlab.cdagaming.craftpresence.utils.gui.widgets.TextWidget;
import io.github.cdagaming.unicore.utils.StringUtils;
import net.minecraft.client.gui.GuiScreen;

@SuppressWarnings("DuplicatedCode")
public class DimensionSettingsGui extends ConfigurationGui<Dimension> {
    private final Dimension INSTANCE, DEFAULTS;
    private ExtendedButtonControl dimensionMessagesButton;
    private TextWidget defaultMessage, defaultIcon;

    DimensionSettingsGui(GuiScreen parentScreen) {
        super(parentScreen, "gui.config.title", "gui.config.title.dimension_messages");
        DEFAULTS = getCurrentData().getDefaults();
        INSTANCE = getCurrentData().copy();
    }

    @Override
    protected void appendControls() {
        super.appendControls();

        final ModuleData defaultData = getInstanceData().dimensionData.get("default");
        final String defaultDimensionMessage = Config.getProperty(defaultData, "textOverride") != null ? defaultData.getTextOverride() : "";

        defaultMessage = childFrame.addControl(
                new TextWidget(
                        getFontRenderer(),
                        getButtonY(0),
                        180, 20,
                        () -> {
                            final ModuleData defaultDimensionData = getInstanceData().dimensionData.getOrDefault("default", new ModuleData());
                            defaultDimensionData.setTextOverride(defaultMessage.getControlMessage());
                            getInstanceData().dimensionData.put("default", defaultDimensionData);
                        },
                        "gui.config.message.default.dimension",
                        () -> drawMultiLineString(
                                StringUtils.splitTextByNewLine(
                                        Constants.TRANSLATOR.translate("gui.config.comment.dimension_messages.dimension_messages",
                                                CraftPresence.CLIENT.generateArgumentMessage("dimension."))
                                )
                        )
                )
        );
        defaultMessage.setControlMessage(defaultDimensionMessage);

        // Adding Default Icon Data
        defaultIcon = childFrame.addControl(
                new TextWidget(
                        getFontRenderer(),
                        getButtonY(1),
                        147, 20,
                        () -> getInstanceData().fallbackDimensionIcon = defaultIcon.getControlMessage(),
                        "gui.config.name.dimension_messages.dimension_icon",
                        () -> drawMultiLineString(
                                StringUtils.splitTextByNewLine(
                                        Constants.TRANSLATOR.translate("gui.config.comment.dimension_messages.dimension_icon")
                                )
                        )
                )
        );
        addIconSelector(childFrame, () -> defaultIcon,
                (attributeName, currentValue) -> getInstanceData().fallbackDimensionIcon = currentValue
        );
        defaultIcon.setControlMessage(getInstanceData().fallbackDimensionIcon);

        dimensionMessagesButton = childFrame.addControl(
                new ExtendedButtonControl(
                        (getScreenWidth() / 2) - 90, getButtonY(2),
                        180, 20,
                        "gui.config.name.dimension_messages.dimension_messages",
                        () -> openScreen(
                                new SelectorGui(
                                        currentScreen,
                                        Constants.TRANSLATOR.translate("gui.config.title.selector.dimension"), CraftPresence.DIMENSIONS.DIMENSION_NAMES,
                                        null, null,
                                        true, true, RenderType.None,
                                        (attributeName, currentValue) -> {
                                            final ModuleData defaultDimensionData = getCurrentData().dimensionData.get("default");
                                            final ModuleData currentDimensionData = getCurrentData().dimensionData.get(attributeName);
                                            final String defaultMessage = Config.getProperty(defaultDimensionData, "textOverride") != null ? defaultDimensionData.getTextOverride() : "";
                                            final String currentMessage = Config.getProperty(currentDimensionData, "textOverride") != null ? currentDimensionData.getTextOverride() : "";

                                            markAsChanged();
                                            final ModuleData newData = new ModuleData();
                                            if (StringUtils.isNullOrEmpty(currentMessage) || currentMessage.equals(defaultMessage)) {
                                                newData.setTextOverride(defaultMessage);
                                            }
                                            newData.setIconOverride(currentValue);
                                            getCurrentData().dimensionData.put(attributeName, newData);
                                        },
                                        (currentValue, parentScreen) -> {
                                            // Event to occur when Setting Dynamic/Specific Data
                                            openScreen(
                                                    new DynamicEditorGui(
                                                            parentScreen, currentValue,
                                                            (attributeName, screenInstance) -> {
                                                                // Event to occur when initializing new data
                                                                screenInstance.defaultData = getCurrentData().dimensionData.get("default");
                                                                screenInstance.primaryMessage = screenInstance.originalPrimaryMessage = Config.getProperty(screenInstance.defaultData, "textOverride") != null ? screenInstance.defaultData.getTextOverride() : "";
                                                            },
                                                            (attributeName, screenInstance) -> {
                                                                // Event to occur when initializing existing data
                                                                screenInstance.defaultData = getCurrentData().dimensionData.get("default");
                                                                screenInstance.currentData = getCurrentData().dimensionData.get(attributeName);
                                                                screenInstance.isPreliminaryData = screenInstance.currentData == null;
                                                                screenInstance.mainTitle = Constants.TRANSLATOR.translate("gui.config.title.dimension.edit_specific_dimension", attributeName);
                                                                screenInstance.originalPrimaryMessage = Config.getProperty(screenInstance.defaultData, "textOverride") != null ? screenInstance.defaultData.getTextOverride() : "";
                                                                screenInstance.primaryMessage = Config.getProperty(screenInstance.currentData, "textOverride") != null ? screenInstance.currentData.getTextOverride() : screenInstance.originalPrimaryMessage;
                                                            },
                                                            (screenInstance, attributeName, inputText) -> {
                                                                // Event to occur when adjusting set data
                                                                screenInstance.currentData.setTextOverride(inputText);
                                                                markAsChanged();
                                                                getCurrentData().dimensionData.put(attributeName, screenInstance.currentData);
                                                                if (!CraftPresence.DIMENSIONS.DIMENSION_NAMES.contains(attributeName)) {
                                                                    CraftPresence.DIMENSIONS.DIMENSION_NAMES.add(attributeName);
                                                                }
                                                            },
                                                            (screenInstance, attributeName, inputText) -> {
                                                                // Event to occur when removing set data
                                                                markAsChanged();
                                                                getCurrentData().dimensionData.remove(attributeName);
                                                                if (!CraftPresence.DIMENSIONS.DEFAULT_NAMES.contains(attributeName)) {
                                                                    CraftPresence.DIMENSIONS.DIMENSION_NAMES.remove(attributeName);
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
                                                                    final String defaultIcon = Config.getProperty(screenInstance.defaultData, "iconOverride") != null ? screenInstance.defaultData.getIconOverride() : getCurrentData().fallbackDimensionIcon;
                                                                    final String specificIcon = Config.getProperty(screenInstance.currentData, "iconOverride") != null ? screenInstance.currentData.getIconOverride() : defaultIcon;
                                                                    openScreen(
                                                                            new SelectorGui(
                                                                                    screenInstance,
                                                                                    Constants.TRANSLATOR.translate("gui.config.title.selector.icon"), DiscordAssetUtils.ASSET_LIST.keySet(),
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
                                                                screenInstance.drawMultiLineString(
                                                                        StringUtils.splitTextByNewLine(
                                                                                Constants.TRANSLATOR.translate("gui.config.comment.dimension_messages.dimension_messages",
                                                                                        CraftPresence.CLIENT.generateArgumentMessage("dimension."))
                                                                        )
                                                                );
                                                            }
                                                    )
                                            );
                                        }
                                )
                        ),
                        () -> {
                            if (!dimensionMessagesButton.isControlEnabled()) {
                                drawMultiLineString(
                                        StringUtils.splitTextByNewLine(
                                                Constants.TRANSLATOR.translate("gui.config.message.hover.access",
                                                        Constants.TRANSLATOR.translate("gui.config.name.general.detect_dimension_data"))
                                        )
                                );
                            } else {
                                drawMultiLineString(
                                        StringUtils.splitTextByNewLine(
                                                Constants.TRANSLATOR.translate("gui.config.comment.dimension_messages.dimension_messages",
                                                        CraftPresence.CLIENT.generateArgumentMessage("dimension."))
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
    protected boolean allowedToSync() {
        return true;
    }

    @Override
    protected void syncRenderStates() {
        super.syncRenderStates();

        proceedButton.setControlEnabled(!StringUtils.isNullOrEmpty(defaultMessage.getControlMessage()));
        dimensionMessagesButton.setControlEnabled(CraftPresence.DIMENSIONS.enabled);
    }

    @Override
    protected Dimension getInstanceData() {
        return INSTANCE;
    }

    @Override
    protected Dimension getCurrentData() {
        return CraftPresence.CONFIG.dimensionSettings;
    }

    @Override
    protected Dimension getDefaultData() {
        return DEFAULTS;
    }

    @Override
    protected Dimension getSyncData() {
        return Config.loadOrCreate().dimensionSettings;
    }
}
