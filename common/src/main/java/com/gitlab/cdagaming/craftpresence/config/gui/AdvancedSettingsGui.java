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
import com.gitlab.cdagaming.craftpresence.config.category.Advanced;
import com.gitlab.cdagaming.craftpresence.core.Constants;
import com.gitlab.cdagaming.craftpresence.core.config.element.PresenceData;
import com.gitlab.cdagaming.craftpresence.utils.gui.controls.CheckBoxControl;
import com.gitlab.cdagaming.craftpresence.utils.gui.controls.ExtendedButtonControl;
import com.gitlab.cdagaming.craftpresence.utils.gui.controls.ExtendedTextControl;
import com.gitlab.cdagaming.craftpresence.utils.gui.controls.ScrollableListControl.IdentifierType;
import com.gitlab.cdagaming.craftpresence.utils.gui.controls.ScrollableListControl.RenderType;
import com.gitlab.cdagaming.craftpresence.utils.gui.impl.ConfigurationGui;
import com.gitlab.cdagaming.craftpresence.utils.gui.impl.DynamicEditorGui;
import com.gitlab.cdagaming.craftpresence.utils.gui.impl.SelectorGui;
import com.gitlab.cdagaming.craftpresence.utils.gui.widgets.ScrollableTextWidget;
import com.gitlab.cdagaming.craftpresence.utils.gui.widgets.TextWidget;
import io.github.cdagaming.unicore.impl.Pair;
import io.github.cdagaming.unicore.utils.ScheduleUtils;
import io.github.cdagaming.unicore.utils.StringUtils;
import net.minecraft.client.gui.GuiScreen;

@SuppressWarnings("DuplicatedCode")
public class AdvancedSettingsGui extends ConfigurationGui<Advanced> {
    private final Advanced INSTANCE, DEFAULTS;
    private ExtendedButtonControl guiMessagesButton, itemMessagesButton, entityTargetMessagesButton, entityRidingMessagesButton;
    private CheckBoxControl enablePerGuiButton, enablePerItemButton, enablePerEntityButton,
            formatWordsButton, debugModeButton, verboseModeButton,
            allowPlaceholderPreviewsButton, allowEndpointIconsButton, allowDuplicatePacketsButton;
    private ExtendedTextControl refreshRate, maxConnectionAttempts,
            playerSkinEndpoint, serverIconEndpoint;

    AdvancedSettingsGui(GuiScreen parentScreen) {
        super(parentScreen, "gui.config.title", "gui.config.title.advanced");
        DEFAULTS = getCurrentData().getDefaults();
        INSTANCE = getCurrentData().copy();
    }

    @Override
    protected void appendControls() {
        super.appendControls();

        final int calc1 = (getScreenWidth() / 2) - 183;
        final int calc2 = (getScreenWidth() / 2) + 3;

        refreshRate = childFrame.addControl(
                new TextWidget(
                        getFontRenderer(),
                        getButtonY(0),
                        180, 20,
                        () -> getInstanceData().refreshRate = StringUtils.getValidInteger(refreshRate.getControlMessage()).getSecond(),
                        "gui.config.name.advanced.refresh_rate",
                        () -> drawMultiLineString(
                                StringUtils.splitTextByNewLine(
                                        Constants.TRANSLATOR.translate("gui.config.comment.advanced.refresh_rate")
                                )
                        )
                )
        );
        refreshRate.setControlMessage(Integer.toString(getInstanceData().refreshRate));

        maxConnectionAttempts = childFrame.addControl(
                new TextWidget(
                        getFontRenderer(),
                        getButtonY(1),
                        180, 20,
                        () -> getInstanceData().maxConnectionAttempts = StringUtils.getValidInteger(maxConnectionAttempts.getControlMessage()).getSecond(),
                        "gui.config.name.advanced.max_connection_attempts",
                        () -> drawMultiLineString(
                                StringUtils.splitTextByNewLine(
                                        Constants.TRANSLATOR.translate("gui.config.comment.advanced.max_connection_attempts")
                                )
                        )
                )
        );
        maxConnectionAttempts.setControlMessage(Integer.toString(getInstanceData().maxConnectionAttempts));

        guiMessagesButton = childFrame.addControl(
                new ExtendedButtonControl(
                        calc1, getButtonY(2),
                        180, 20,
                        "gui.config.name.advanced.gui_messages",
                        () -> openScreen(
                                new SelectorGui(
                                        currentScreen,
                                        Constants.TRANSLATOR.translate("gui.config.title.selector.gui"), CraftPresence.GUIS.GUI_NAMES,
                                        null, null,
                                        true, true, RenderType.None,
                                        null,
                                        (currentValue, parentScreen) -> {
                                            // Event to occur when Setting Dynamic/Specific Data
                                            openScreen(
                                                    new DynamicEditorGui(
                                                            parentScreen, currentValue,
                                                            (attributeName, screenInstance) -> {
                                                                // Event to occur when initializing new data
                                                                screenInstance.defaultData = getInstanceData().guiSettings.guiData.get("default");
                                                                screenInstance.primaryMessage = screenInstance.originalPrimaryMessage = Config.getProperty(screenInstance.defaultData, "textOverride") != null ? screenInstance.defaultData.getTextOverride() : "";
                                                            },
                                                            (attributeName, screenInstance) -> {
                                                                // Event to occur when initializing existing data
                                                                screenInstance.defaultData = getInstanceData().guiSettings.guiData.get("default");
                                                                screenInstance.currentData = getInstanceData().guiSettings.guiData.get(attributeName);
                                                                screenInstance.isPreliminaryData = screenInstance.currentData == null;
                                                                screenInstance.mainTitle = Constants.TRANSLATOR.translate("gui.config.title.gui.edit_specific_gui", attributeName);
                                                                screenInstance.originalPrimaryMessage = Config.getProperty(screenInstance.defaultData, "textOverride") != null ? screenInstance.defaultData.getTextOverride() : "";
                                                                screenInstance.primaryMessage = Config.getProperty(screenInstance.currentData, "textOverride") != null ? screenInstance.currentData.getTextOverride() : screenInstance.originalPrimaryMessage;
                                                            },
                                                            (screenInstance, attributeName, inputText) -> {
                                                                // Event to occur when adjusting set data
                                                                screenInstance.currentData.setTextOverride(inputText);
                                                                getInstanceData().guiSettings.guiData.put(attributeName, screenInstance.currentData);
                                                                if (!CraftPresence.GUIS.GUI_NAMES.contains(attributeName)) {
                                                                    CraftPresence.GUIS.GUI_NAMES.add(attributeName);
                                                                }
                                                            },
                                                            (screenInstance, attributeName, inputText) -> {
                                                                // Event to occur when removing set data
                                                                getInstanceData().guiSettings.guiData.remove(attributeName);
                                                                if (!CraftPresence.GUIS.DEFAULT_NAMES.contains(attributeName)) {
                                                                    CraftPresence.GUIS.GUI_NAMES.remove(attributeName);
                                                                }
                                                            },
                                                            (attributeName, screenInstance, isPresenceButton) -> {
                                                                // Event to occur when adding specific info to set data
                                                                if (isPresenceButton) {
                                                                    final PresenceData defaultPresenceData = Config.getProperty(screenInstance.defaultData, "data") != null ? screenInstance.defaultData.getData() : new PresenceData();
                                                                    final PresenceData currentPresenceData = Config.getProperty(screenInstance.currentData, "data") != null ? screenInstance.currentData.getData() : defaultPresenceData;
                                                                    openScreen(
                                                                            new PresenceSettingsGui(
                                                                                    screenInstance,
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
                                                                                Constants.TRANSLATOR.translate("gui.config.comment.advanced.gui_messages",
                                                                                        CraftPresence.CLIENT.generateArgumentMessage("screen."))
                                                                        )
                                                                );
                                                            }
                                                    )
                                            );
                                        }
                                ).setIdentifierType(IdentifierType.Gui)
                        ),
                        () -> {
                            if (!guiMessagesButton.isControlEnabled()) {
                                drawMultiLineString(
                                        StringUtils.splitTextByNewLine(
                                                Constants.TRANSLATOR.translate("gui.config.message.hover.access",
                                                        Constants.TRANSLATOR.translate("gui.config.name.advanced.enable_per_gui"))
                                        ));
                            } else {
                                drawMultiLineString(
                                        StringUtils.splitTextByNewLine(
                                                Constants.TRANSLATOR.translate("gui.config.comment.advanced.gui_messages",
                                                        CraftPresence.CLIENT.generateArgumentMessage("screen."))
                                        )
                                );
                            }
                        }
                )
        );
        itemMessagesButton = childFrame.addControl(
                new ExtendedButtonControl(
                        calc2, getButtonY(2),
                        180, 20,
                        "gui.config.name.advanced.item_messages",
                        () -> openScreen(
                                new SelectorGui(
                                        currentScreen,
                                        Constants.TRANSLATOR.translate("gui.config.title.selector.item"), CraftPresence.TILE_ENTITIES.TILE_ENTITY_NAMES,
                                        null, null,
                                        true, true, RenderType.ItemData,
                                        null,
                                        (currentValue, parentScreen) -> {
                                            // Event to occur when Setting Dynamic/Specific Data
                                            openScreen(
                                                    new DynamicEditorGui(
                                                            parentScreen, currentValue,
                                                            (attributeName, screenInstance) -> {
                                                                // Event to occur when initializing new data
                                                                screenInstance.primaryMessage = screenInstance.originalPrimaryMessage = getInstanceData().itemMessages.getOrDefault("default", "");
                                                            },
                                                            (attributeName, screenInstance) -> {
                                                                // Event to occur when initializing existing data
                                                                screenInstance.mainTitle = Constants.TRANSLATOR.translate("gui.config.title.item.edit_specific_item", attributeName);
                                                                screenInstance.originalPrimaryMessage = getInstanceData().itemMessages.getOrDefault("default", "");
                                                                screenInstance.primaryMessage = getInstanceData().itemMessages.getOrDefault(attributeName, screenInstance.originalPrimaryMessage);
                                                            },
                                                            (screenInstance, attributeName, inputText) -> {
                                                                // Event to occur when adjusting set data
                                                                getInstanceData().itemMessages.put(attributeName, inputText);
                                                                if (!CraftPresence.TILE_ENTITIES.ITEM_NAMES.contains(attributeName)) {
                                                                    CraftPresence.TILE_ENTITIES.ITEM_NAMES.add(attributeName);
                                                                }
                                                                if (!CraftPresence.TILE_ENTITIES.BLOCK_NAMES.contains(attributeName)) {
                                                                    CraftPresence.TILE_ENTITIES.BLOCK_NAMES.add(attributeName);
                                                                }
                                                                CraftPresence.TILE_ENTITIES.TILE_ENTITY_NAMES.remove(attributeName);
                                                                CraftPresence.TILE_ENTITIES.verifyEntities();
                                                            },
                                                            (screenInstance, attributeName, inputText) -> {
                                                                // Event to occur when removing set data
                                                                getInstanceData().itemMessages.remove(attributeName);
                                                                CraftPresence.TILE_ENTITIES.ITEM_NAMES.remove(attributeName);
                                                                CraftPresence.TILE_ENTITIES.BLOCK_NAMES.remove(attributeName);
                                                                CraftPresence.TILE_ENTITIES.TILE_ENTITY_NAMES.remove(attributeName);
                                                            }, null,
                                                            (attributeName, screenInstance) -> {
                                                                // Event to occur when Hovering over Message Label
                                                                screenInstance.drawMultiLineString(
                                                                        StringUtils.splitTextByNewLine(
                                                                                Constants.TRANSLATOR.translate("gui.config.comment.advanced.item_messages",
                                                                                        CraftPresence.CLIENT.generateArgumentMessage("item.")
                                                                                )
                                                                        )
                                                                );
                                                            }
                                                    )
                                            );
                                        }
                                )
                        ),
                        () -> {
                            if (!itemMessagesButton.isControlEnabled()) {
                                drawMultiLineString(
                                        StringUtils.splitTextByNewLine(
                                                Constants.TRANSLATOR.translate("gui.config.message.hover.access",
                                                        Constants.TRANSLATOR.translate("gui.config.name.advanced.enable_per_item"))
                                        )
                                );
                            } else {
                                drawMultiLineString(
                                        StringUtils.splitTextByNewLine(
                                                Constants.TRANSLATOR.translate("gui.config.comment.advanced.item_messages",
                                                        CraftPresence.CLIENT.generateArgumentMessage("item.")
                                                )
                                        )
                                );
                            }
                        }
                )
        );
        entityTargetMessagesButton = childFrame.addControl(
                new ExtendedButtonControl(
                        calc1, getButtonY(3),
                        180, 20,
                        "gui.config.name.advanced.entity_target_messages",
                        () -> openScreen(
                                new SelectorGui(
                                        currentScreen,
                                        Constants.TRANSLATOR.translate("gui.config.title.selector.entity"), CraftPresence.ENTITIES.ENTITY_NAMES,
                                        null, null,
                                        true, true, RenderType.EntityData,
                                        null,
                                        (currentValue, parentScreen) -> {
                                            // Event to occur when Setting Dynamic/Specific Data
                                            openScreen(
                                                    new DynamicEditorGui(
                                                            parentScreen, currentValue,
                                                            (attributeName, screenInstance) -> {
                                                                // Event to occur when initializing new data
                                                                screenInstance.defaultData = getInstanceData().entitySettings.targetData.get("default");
                                                                screenInstance.primaryMessage = screenInstance.originalPrimaryMessage = Config.getProperty(screenInstance.defaultData, "textOverride") != null ? screenInstance.defaultData.getTextOverride() : "";
                                                            },
                                                            (attributeName, screenInstance) -> {
                                                                // Event to occur when initializing existing data
                                                                screenInstance.defaultData = getInstanceData().entitySettings.targetData.get("default");
                                                                screenInstance.currentData = getInstanceData().entitySettings.targetData.get(attributeName);
                                                                screenInstance.isPreliminaryData = screenInstance.currentData == null;
                                                                screenInstance.mainTitle = Constants.TRANSLATOR.translate("gui.config.title.entity.edit_specific_entity", attributeName);
                                                                screenInstance.originalPrimaryMessage = Config.getProperty(screenInstance.defaultData, "textOverride") != null ? screenInstance.defaultData.getTextOverride() : "";
                                                                screenInstance.primaryMessage = Config.getProperty(screenInstance.currentData, "textOverride") != null ? screenInstance.currentData.getTextOverride() : screenInstance.originalPrimaryMessage;
                                                            },
                                                            (screenInstance, attributeName, inputText) -> {
                                                                // Event to occur when adjusting set data
                                                                screenInstance.currentData.setTextOverride(inputText);
                                                                getInstanceData().entitySettings.targetData.put(attributeName, screenInstance.currentData);
                                                                if (!CraftPresence.ENTITIES.ENTITY_NAMES.contains(attributeName)) {
                                                                    CraftPresence.ENTITIES.ENTITY_NAMES.add(attributeName);
                                                                }
                                                            },
                                                            (screenInstance, attributeName, inputText) -> {
                                                                // Event to occur when removing set data
                                                                getInstanceData().entitySettings.targetData.remove(attributeName);
                                                                if (!CraftPresence.ENTITIES.DEFAULT_NAMES.contains(attributeName)) {
                                                                    CraftPresence.ENTITIES.ENTITY_NAMES.remove(attributeName);
                                                                }
                                                            },
                                                            (attributeName, screenInstance, isPresenceButton) -> {
                                                                // Event to occur when adding specific info to set data
                                                                if (isPresenceButton) {
                                                                    final PresenceData defaultPresenceData = Config.getProperty(screenInstance.defaultData, "data") != null ? screenInstance.defaultData.getData() : new PresenceData();
                                                                    final PresenceData currentPresenceData = Config.getProperty(screenInstance.currentData, "data") != null ? screenInstance.currentData.getData() : defaultPresenceData;
                                                                    openScreen(
                                                                            new PresenceSettingsGui(
                                                                                    screenInstance,
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
                                                                                Constants.TRANSLATOR.translate("gui.config.comment.advanced.entity_target_messages",
                                                                                        CraftPresence.CLIENT.generateArgumentMessage("entity.target.")
                                                                                )
                                                                        )
                                                                );
                                                            }
                                                    )
                                            );
                                        }
                                )
                        ),
                        () -> {
                            if (!entityTargetMessagesButton.isControlEnabled()) {
                                drawMultiLineString(
                                        StringUtils.splitTextByNewLine(
                                                Constants.TRANSLATOR.translate("gui.config.message.hover.access",
                                                        Constants.TRANSLATOR.translate("gui.config.name.advanced.enable_per_entity"))
                                        )
                                );
                            } else {
                                drawMultiLineString(
                                        StringUtils.splitTextByNewLine(
                                                Constants.TRANSLATOR.translate("gui.config.comment.advanced.entity_target_messages",
                                                        CraftPresence.CLIENT.generateArgumentMessage("entity.target.")
                                                )
                                        )
                                );
                            }
                        }
                )
        );
        entityRidingMessagesButton = childFrame.addControl(
                new ExtendedButtonControl(
                        calc2, getButtonY(3),
                        180, 20,
                        "gui.config.name.advanced.entity_riding_messages",
                        () -> openScreen(
                                new SelectorGui(
                                        currentScreen,
                                        Constants.TRANSLATOR.translate("gui.config.title.selector.entity"), CraftPresence.ENTITIES.ENTITY_NAMES,
                                        null, null,
                                        true, true, RenderType.EntityData,
                                        null,
                                        (currentValue, parentScreen) -> {
                                            // Event to occur when Setting Dynamic/Specific Data
                                            openScreen(
                                                    new DynamicEditorGui(
                                                            parentScreen, currentValue,
                                                            (attributeName, screenInstance) -> {
                                                                // Event to occur when initializing new data
                                                                screenInstance.defaultData = getInstanceData().entitySettings.ridingData.get("default");
                                                                screenInstance.primaryMessage = screenInstance.originalPrimaryMessage = Config.getProperty(screenInstance.defaultData, "textOverride") != null ? screenInstance.defaultData.getTextOverride() : "";
                                                            },
                                                            (attributeName, screenInstance) -> {
                                                                // Event to occur when initializing existing data
                                                                screenInstance.defaultData = getInstanceData().entitySettings.ridingData.get("default");
                                                                screenInstance.currentData = getInstanceData().entitySettings.ridingData.get(attributeName);
                                                                screenInstance.isPreliminaryData = screenInstance.currentData == null;
                                                                screenInstance.mainTitle = Constants.TRANSLATOR.translate("gui.config.title.entity.edit_specific_entity", attributeName);
                                                                screenInstance.originalPrimaryMessage = Config.getProperty(screenInstance.defaultData, "textOverride") != null ? screenInstance.defaultData.getTextOverride() : "";
                                                                screenInstance.primaryMessage = Config.getProperty(screenInstance.currentData, "textOverride") != null ? screenInstance.currentData.getTextOverride() : screenInstance.originalPrimaryMessage;
                                                            },
                                                            (screenInstance, attributeName, inputText) -> {
                                                                // Event to occur when adjusting set data
                                                                screenInstance.currentData.setTextOverride(inputText);
                                                                getInstanceData().entitySettings.ridingData.put(attributeName, screenInstance.currentData);
                                                                if (!CraftPresence.ENTITIES.ENTITY_NAMES.contains(attributeName)) {
                                                                    CraftPresence.ENTITIES.ENTITY_NAMES.add(attributeName);
                                                                }
                                                            },
                                                            (screenInstance, attributeName, inputText) -> {
                                                                // Event to occur when removing set data
                                                                getInstanceData().entitySettings.ridingData.remove(attributeName);
                                                                if (!CraftPresence.ENTITIES.DEFAULT_NAMES.contains(attributeName)) {
                                                                    CraftPresence.ENTITIES.ENTITY_NAMES.remove(attributeName);
                                                                }
                                                            },
                                                            (attributeName, screenInstance, isPresenceButton) -> {
                                                                // Event to occur when adding specific info to set data
                                                                if (isPresenceButton) {
                                                                    final PresenceData defaultPresenceData = Config.getProperty(screenInstance.defaultData, "data") != null ? screenInstance.defaultData.getData() : new PresenceData();
                                                                    final PresenceData currentPresenceData = Config.getProperty(screenInstance.currentData, "data") != null ? screenInstance.currentData.getData() : defaultPresenceData;
                                                                    openScreen(
                                                                            new PresenceSettingsGui(
                                                                                    screenInstance,
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
                                                                                Constants.TRANSLATOR.translate("gui.config.comment.advanced.entity_riding_messages",
                                                                                        CraftPresence.CLIENT.generateArgumentMessage("entity.riding.")
                                                                                )
                                                                        )
                                                                );
                                                            }
                                                    )
                                            );
                                        }
                                )
                        ),
                        () -> {
                            if (!entityRidingMessagesButton.isControlEnabled()) {
                                drawMultiLineString(
                                        StringUtils.splitTextByNewLine(
                                                Constants.TRANSLATOR.translate("gui.config.message.hover.access",
                                                        Constants.TRANSLATOR.translate("gui.config.name.advanced.enable_per_entity"))
                                        )
                                );
                            } else {
                                drawMultiLineString(
                                        StringUtils.splitTextByNewLine(
                                                Constants.TRANSLATOR.translate("gui.config.comment.advanced.entity_riding_messages",
                                                        CraftPresence.CLIENT.generateArgumentMessage("entity.riding.")
                                                )
                                        )
                                );
                            }
                        }
                )
        );

        enablePerGuiButton = childFrame.addControl(
                new CheckBoxControl(
                        calc1, getButtonY(4),
                        "gui.config.name.advanced.enable_per_gui",
                        getInstanceData().enablePerGui,
                        () -> getInstanceData().enablePerGui = enablePerGuiButton.isChecked(),
                        () -> drawMultiLineString(
                                StringUtils.splitTextByNewLine(
                                        Constants.TRANSLATOR.translate("gui.config.comment.advanced.enable_per_gui")
                                )
                        )
                )
        );
        enablePerItemButton = childFrame.addControl(
                new CheckBoxControl(
                        calc2, getButtonY(4),
                        "gui.config.name.advanced.enable_per_item",
                        getInstanceData().enablePerItem,
                        () -> getInstanceData().enablePerItem = enablePerItemButton.isChecked(),
                        () -> drawMultiLineString(
                                StringUtils.splitTextByNewLine(
                                        Constants.TRANSLATOR.translate("gui.config.comment.advanced.enable_per_item")
                                )
                        )
                )
        );
        enablePerEntityButton = childFrame.addControl(
                new CheckBoxControl(
                        calc1, getButtonY(5, -10),
                        "gui.config.name.advanced.enable_per_entity",
                        getInstanceData().enablePerEntity,
                        () -> getInstanceData().enablePerEntity = enablePerEntityButton.isChecked(),
                        () -> drawMultiLineString(
                                StringUtils.splitTextByNewLine(
                                        Constants.TRANSLATOR.translate("gui.config.comment.advanced.enable_per_entity")
                                )
                        )
                )
        );
        debugModeButton = childFrame.addControl(
                new CheckBoxControl(
                        calc2, getButtonY(5, -10),
                        "gui.config.name.advanced.debug_mode",
                        getInstanceData().debugMode,
                        () -> getInstanceData().debugMode = debugModeButton.isChecked(),
                        () -> drawMultiLineString(
                                StringUtils.splitTextByNewLine(
                                        Constants.TRANSLATOR.translate("gui.config.comment.advanced.debug_mode", Constants.IS_DEV_FLAG)
                                )
                        )
                )
        );
        formatWordsButton = childFrame.addControl(
                new CheckBoxControl(
                        calc1, getButtonY(6, -20),
                        "gui.config.name.advanced.format_words",
                        getInstanceData().formatWords,
                        () -> getInstanceData().formatWords = formatWordsButton.isChecked(),
                        () -> drawMultiLineString(
                                StringUtils.splitTextByNewLine(
                                        Constants.TRANSLATOR.translate("gui.config.comment.advanced.format_words")
                                )
                        )
                )
        );
        verboseModeButton = childFrame.addControl(
                new CheckBoxControl(
                        calc2, getButtonY(6, -20),
                        "gui.config.name.advanced.verbose_mode",
                        getInstanceData().verboseMode,
                        () -> getInstanceData().verboseMode = verboseModeButton.isChecked(),
                        () -> drawMultiLineString(
                                StringUtils.splitTextByNewLine(
                                        Constants.TRANSLATOR.translate("gui.config.comment.advanced.verbose_mode", Constants.IS_VERBOSE_FLAG)
                                )
                        )
                )
        );
        allowPlaceholderPreviewsButton = childFrame.addControl(
                new CheckBoxControl(
                        calc1, getButtonY(7, -30),
                        "gui.config.name.advanced.allow_placeholder_previews",
                        getInstanceData().allowPlaceholderPreviews,
                        () -> getInstanceData().allowPlaceholderPreviews = allowPlaceholderPreviewsButton.isChecked(),
                        () -> drawMultiLineString(
                                StringUtils.splitTextByNewLine(
                                        Constants.TRANSLATOR.translate("gui.config.comment.advanced.allow_placeholder_previews")
                                )
                        )
                )
        );
        allowEndpointIconsButton = childFrame.addControl(
                new CheckBoxControl(
                        calc2, getButtonY(7, -30),
                        "gui.config.name.advanced.allow_endpoint_icons",
                        getInstanceData().allowEndpointIcons,
                        () -> getInstanceData().allowEndpointIcons = allowEndpointIconsButton.isChecked(),
                        () -> drawMultiLineString(
                                StringUtils.splitTextByNewLine(
                                        Constants.TRANSLATOR.translate("gui.config.comment.advanced.allow_endpoint_icons")
                                )
                        )
                )
        );
        allowDuplicatePacketsButton = childFrame.addControl(
                new CheckBoxControl(
                        calc1, getButtonY(8, -40),
                        "gui.config.name.advanced.allow_duplicate_packets",
                        getInstanceData().allowDuplicatePackets,
                        () -> getInstanceData().allowDuplicatePackets = allowDuplicatePacketsButton.isChecked(),
                        () -> drawMultiLineString(
                                StringUtils.splitTextByNewLine(
                                        Constants.TRANSLATOR.translate("gui.config.comment.advanced.allow_duplicate_packets")
                                )
                        )
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

        // Endpoint Section
        childFrame.addWidget(new ScrollableTextWidget(
                childFrame,
                calc1, getButtonY(7),
                childFrame.getScreenWidth(),
                Constants.TRANSLATOR.translate("gui.config.message.endpoints")
        ));

        playerSkinEndpoint = childFrame.addControl(
                new TextWidget(
                        getFontRenderer(),
                        getButtonY(8),
                        180, 20,
                        () -> getInstanceData().playerSkinEndpoint = playerSkinEndpoint.getControlMessage(),
                        "gui.config.name.advanced.player_skin_endpoint",
                        () -> drawMultiLineString(
                                StringUtils.splitTextByNewLine(
                                        Constants.TRANSLATOR.translate("gui.config.comment.advanced.player_skin_endpoint")
                                )
                        )
                )
        );
        playerSkinEndpoint.setControlMessage(getInstanceData().playerSkinEndpoint);

        serverIconEndpoint = childFrame.addControl(
                new TextWidget(
                        getFontRenderer(),
                        getButtonY(9),
                        180, 20,
                        () -> getInstanceData().serverIconEndpoint = serverIconEndpoint.getControlMessage(),
                        "gui.config.name.advanced.server_icon_endpoint",
                        () -> drawMultiLineString(
                                StringUtils.splitTextByNewLine(
                                        Constants.TRANSLATOR.translate("gui.config.comment.advanced.server_icon_endpoint")
                                )
                        )
                )
        );
        serverIconEndpoint.setControlMessage(getInstanceData().serverIconEndpoint);
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

        final Pair<Boolean, Integer> refreshRateData = StringUtils.getValidInteger(refreshRate.getControlMessage());
        proceedButton.setControlEnabled(
                (refreshRateData.getFirst() && refreshRateData.getSecond() >= ScheduleUtils.MINIMUM_REFRESH_RATE)
        );

        guiMessagesButton.setControlEnabled(CraftPresence.GUIS.enabled);
        itemMessagesButton.setControlEnabled(CraftPresence.TILE_ENTITIES.enabled);
        entityTargetMessagesButton.setControlEnabled(CraftPresence.ENTITIES.enabled);
        entityRidingMessagesButton.setControlEnabled(CraftPresence.ENTITIES.enabled);
    }

    @Override
    protected Advanced getInstanceData() {
        return INSTANCE;
    }

    @Override
    protected Advanced getCurrentData() {
        return CraftPresence.CONFIG.advancedSettings;
    }

    @Override
    protected Advanced getDefaultData() {
        return DEFAULTS;
    }

    @Override
    protected Advanced getSyncData() {
        return Config.loadOrCreate().advancedSettings;
    }
}
