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
import com.gitlab.cdagaming.craftpresence.core.config.Config;
import com.gitlab.cdagaming.craftpresence.core.config.category.Dimension;
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
public class DimensionSettingsGui extends ConfigurationGui<Dimension> {
    private final Dimension INSTANCE, DEFAULTS;
    private ExtendedButtonControl dimensionMessagesButton;
    private TextWidget defaultMessage, defaultIcon;

    DimensionSettingsGui() {
        super(
                Constants.TRANSLATOR.translate("gui.config.title"),
                Constants.TRANSLATOR.translate("gui.config.title.dimension_messages")
        );
        DEFAULTS = getCurrentData().getDefaults();
        INSTANCE = getCurrentData().copy();
    }

    @Override
    public void constructElements() {
        super.constructElements();

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
    public void appendElements() {
        super.appendElements();

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
                        Constants.TRANSLATOR.translate("gui.config.message.default.dimension"),
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
                        Constants.TRANSLATOR.translate("gui.config.name.dimension_messages.dimension_icon"),
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
                        Constants.TRANSLATOR.translate("gui.config.name.dimension_messages.dimension_messages"),
                        () -> openScreen(
                                new DynamicSelectorGui(
                                        Constants.TRANSLATOR.translate("gui.config.title.selector.dimension"), CraftPresence.DIMENSIONS.DIMENSION_NAMES,
                                        null, null,
                                        true, true, DynamicScrollableList.RenderType.None,
                                        (attributeName, currentValue) -> {
                                            final ModuleData defaultDimensionData = getInstanceData().dimensionData.get("default");
                                            final ModuleData currentDimensionData = getInstanceData().dimensionData.get(attributeName);
                                            final String defaultMessage = Config.getProperty(defaultDimensionData, "textOverride") != null ? defaultDimensionData.getTextOverride() : "";
                                            final String currentMessage = Config.getProperty(currentDimensionData, "textOverride") != null ? currentDimensionData.getTextOverride() : "";

                                            final ModuleData newData = new ModuleData();
                                            if (StringUtils.isNullOrEmpty(currentMessage) || currentMessage.equals(defaultMessage)) {
                                                newData.setTextOverride(defaultMessage);
                                            }
                                            newData.setIconOverride(currentValue);
                                            getInstanceData().dimensionData.put(attributeName, newData);
                                        },
                                        (currentValue, parentScreen) -> {
                                            // Event to occur when Setting Dynamic/Specific Data
                                            openScreen(
                                                    new DynamicEditorGui(
                                                            currentValue,
                                                            (attributeName, screenInstance) -> {
                                                                // Event to occur when initializing new data
                                                                screenInstance.defaultData = getInstanceData().dimensionData.get("default");
                                                                screenInstance.primaryMessage = screenInstance.originalPrimaryMessage = Config.getProperty(screenInstance.defaultData, "textOverride") != null ? screenInstance.defaultData.getTextOverride() : "";
                                                            },
                                                            (attributeName, screenInstance) -> {
                                                                // Event to occur when initializing existing data
                                                                screenInstance.isDefaultValue = "default".equals(attributeName);
                                                                screenInstance.defaultData = getInstanceData().dimensionData.get("default");
                                                                screenInstance.currentData = getInstanceData().dimensionData.get(attributeName);
                                                                screenInstance.isPreliminaryData = screenInstance.currentData == null;
                                                                screenInstance.setScreenTitle(Constants.TRANSLATOR.translate("gui.config.title.dimension.edit_specific_dimension", attributeName));
                                                                screenInstance.originalPrimaryMessage = Config.getProperty(screenInstance.defaultData, "textOverride") != null ? screenInstance.defaultData.getTextOverride() : "";
                                                                screenInstance.primaryMessage = Config.getProperty(screenInstance.currentData, "textOverride") != null ? screenInstance.currentData.getTextOverride() : screenInstance.originalPrimaryMessage;
                                                            },
                                                            (screenInstance) -> {
                                                                // Event to occur when adjusting set data
                                                                final String attributeName = screenInstance.getSecondaryEntry();
                                                                final String inputText = screenInstance.getPrimaryEntry();

                                                                screenInstance.currentData.setTextOverride(inputText);
                                                                getInstanceData().dimensionData.put(attributeName, screenInstance.currentData);
                                                                if (!CraftPresence.DIMENSIONS.DIMENSION_NAMES.contains(attributeName)) {
                                                                    CraftPresence.DIMENSIONS.DIMENSION_NAMES.add(attributeName);
                                                                }
                                                            },
                                                            (screenInstance) -> {
                                                                // Event to occur when removing set data
                                                                final String attributeName = screenInstance.getSecondaryEntry();

                                                                getInstanceData().dimensionData.remove(attributeName);
                                                                if (!CraftPresence.DIMENSIONS.DEFAULT_NAMES.contains(attributeName)) {
                                                                    CraftPresence.DIMENSIONS.DIMENSION_NAMES.remove(attributeName);
                                                                }
                                                            },
                                                            (attributeName, screenInstance) -> {
                                                                // Event to occur when adding specific info to set data
                                                                if (screenInstance.isPresenceButton) {
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
                                                                                Constants.TRANSLATOR.translate("gui.config.comment.dimension_messages.dimension_messages",
                                                                                        CraftPresence.CLIENT.generateArgumentMessage("dimension."))
                                                                        )
                                                                );
                                                            }
                                                    ), parentScreen
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
    }

    @Override
    protected boolean allowedToReset() {
        return true;
    }

    @Override
    protected void syncRenderStates() {
        super.syncRenderStates();

        proceedButton.setControlEnabled(!StringUtils.isNullOrEmpty(defaultMessage.getControlMessage()));
        dimensionMessagesButton.setControlEnabled(CraftPresence.DIMENSIONS.isEnabled());
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
}
