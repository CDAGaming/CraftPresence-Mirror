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
import com.gitlab.cdagaming.craftpresence.config.Config;
import com.gitlab.cdagaming.craftpresence.config.category.Advanced;
import com.gitlab.cdagaming.craftpresence.config.element.PresenceData;
import com.gitlab.cdagaming.craftpresence.impl.Pair;
import com.gitlab.cdagaming.craftpresence.utils.StringUtils;
import com.gitlab.cdagaming.craftpresence.utils.SystemUtils;
import com.gitlab.cdagaming.craftpresence.utils.discord.assets.DiscordAssetUtils;
import com.gitlab.cdagaming.craftpresence.utils.gui.controls.CheckBoxControl;
import com.gitlab.cdagaming.craftpresence.utils.gui.controls.ExtendedButtonControl;
import com.gitlab.cdagaming.craftpresence.utils.gui.controls.ExtendedTextControl;
import com.gitlab.cdagaming.craftpresence.utils.gui.controls.ScrollableListControl.RenderType;
import com.gitlab.cdagaming.craftpresence.utils.gui.impl.DynamicEditorGui;
import com.gitlab.cdagaming.craftpresence.utils.gui.impl.SelectorGui;
import com.gitlab.cdagaming.craftpresence.utils.gui.integrations.ExtendedScreen;
import net.minecraft.client.gui.GuiScreen;

public class AdvancedSettingsGui extends ExtendedScreen {
    private final Advanced CONFIG;
    private ExtendedButtonControl proceedButton, guiMessagesButton, itemMessagesButton, entityTargetMessagesButton, entityRidingMessagesButton;
    private CheckBoxControl enableCommandsButton, enablePerGuiButton, enablePerItemButton, enablePerEntityButton,
            renderTooltipsButton, formatWordsButton, debugModeButton, verboseModeButton,
            allowPlaceholderPreviewsButton, allowPlaceholderOperatorsButton, allowEndpointIconsButton;
    private ExtendedTextControl splitCharacter, refreshRate;

    AdvancedSettingsGui(GuiScreen parentScreen) {
        super(parentScreen);
        CONFIG = CraftPresence.CONFIG.advancedSettings;
    }

    @Override
    public void initializeUi() {
        final int calc1 = (getScreenWidth() / 2) - 160;
        final int calc2 = (getScreenWidth() / 2) + 3;

        allowEndpointIconsButton = addControl(
                new CheckBoxControl(
                        calc1 + 2, CraftPresence.GUIS.getButtonY(1, 3),
                        "gui.config.name.advanced.allow_endpoint_icons",
                        CONFIG.allowEndpointIcons,
                        null,
                        () -> CraftPresence.GUIS.drawMultiLineString(
                                StringUtils.splitTextByNewLine(
                                        ModUtils.TRANSLATOR.translate("gui.config.comment.advanced.allow_endpoint_icons")
                                ), this, true
                        )
                )
        );

        refreshRate = addControl(
                new ExtendedTextControl(
                        getFontRenderer(),
                        (getScreenWidth() / 2) + 103, CraftPresence.GUIS.getButtonY(1),
                        45, 20
                )
        );
        refreshRate.setControlMessage(Integer.toString(CONFIG.refreshRate));
        refreshRate.setControlMaxLength(3);

        guiMessagesButton = addControl(
                new ExtendedButtonControl(
                        calc1, CraftPresence.GUIS.getButtonY(2),
                        160, 20,
                        "gui.config.name.advanced.gui_messages",
                        () -> CraftPresence.GUIS.openScreen(
                                new SelectorGui(
                                        currentScreen,
                                        ModUtils.TRANSLATOR.translate("gui.config.title.selector.gui"), CraftPresence.GUIS.GUI_NAMES,
                                        null, null,
                                        true, true, RenderType.None,
                                        null,
                                        (currentValue, parentScreen) -> {
                                            // Event to occur when Setting Dynamic/Specific Data
                                            CraftPresence.GUIS.openScreen(
                                                    new DynamicEditorGui(
                                                            parentScreen, currentValue,
                                                            (attributeName, screenInstance) -> {
                                                                // Event to occur when initializing new data
                                                                screenInstance.defaultData = CONFIG.guiSettings.guiData.get("default");
                                                                screenInstance.primaryMessage = screenInstance.originalPrimaryMessage = Config.getProperty(screenInstance.defaultData, "textOverride") != null ? screenInstance.defaultData.getTextOverride() : "";
                                                            },
                                                            (attributeName, screenInstance) -> {
                                                                // Event to occur when initializing existing data
                                                                screenInstance.defaultData = CONFIG.guiSettings.guiData.get("default");
                                                                screenInstance.currentData = CONFIG.guiSettings.guiData.get(attributeName);
                                                                screenInstance.isPreliminaryData = screenInstance.currentData == null;
                                                                screenInstance.mainTitle = ModUtils.TRANSLATOR.translate("gui.config.title.gui.edit_specific_gui", attributeName);
                                                                screenInstance.originalPrimaryMessage = Config.getProperty(screenInstance.defaultData, "textOverride") != null ? screenInstance.defaultData.getTextOverride() : "";
                                                                screenInstance.primaryMessage = Config.getProperty(screenInstance.currentData, "textOverride") != null ? screenInstance.currentData.getTextOverride() : screenInstance.originalPrimaryMessage;
                                                            },
                                                            (screenInstance, attributeName, inputText) -> {
                                                                // Event to occur when adjusting set data
                                                                screenInstance.currentData.setTextOverride(inputText);
                                                                CraftPresence.CONFIG.hasChanged = true;
                                                                CONFIG.guiSettings.guiData.put(attributeName, screenInstance.currentData);
                                                                if (!CraftPresence.GUIS.GUI_NAMES.contains(attributeName)) {
                                                                    CraftPresence.GUIS.GUI_NAMES.add(attributeName);
                                                                }
                                                            },
                                                            (screenInstance, attributeName, inputText) -> {
                                                                // Event to occur when removing set data
                                                                CraftPresence.CONFIG.hasChanged = true;
                                                                CONFIG.guiSettings.guiData.remove(attributeName);
                                                                if (!screenInstance.isPreliminaryData) {
                                                                    CraftPresence.GUIS.GUI_NAMES.remove(attributeName);
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
                                                                    final String defaultIcon = Config.getProperty(screenInstance.defaultData, "iconOverride") != null ? screenInstance.defaultData.getIconOverride() : CONFIG.guiSettings.fallbackGuiIcon;
                                                                    final String specificIcon = Config.getProperty(screenInstance.currentData, "iconOverride") != null ? screenInstance.currentData.getIconOverride() : defaultIcon;
                                                                    CraftPresence.GUIS.openScreen(
                                                                            new SelectorGui(
                                                                                    screenInstance,
                                                                                    ModUtils.TRANSLATOR.translate("gui.config.title.selector.icon"), DiscordAssetUtils.ASSET_LIST.keySet(),
                                                                                    specificIcon, attributeName,
                                                                                    true, false, RenderType.DiscordAsset,
                                                                                    (innerAttributeName, innerCurrentValue) -> {
                                                                                        // Inner-Event to occur when proceeding with adjusted data
                                                                                        final String defaultMessage = Config.getProperty(screenInstance.defaultData, "textOverride") != null ? screenInstance.defaultData.getTextOverride() : "";
                                                                                        final String currentMessage = Config.getProperty(screenInstance.currentData, "textOverride") != null ? screenInstance.currentData.getTextOverride() : "";

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
                                                                                ModUtils.TRANSLATOR.translate("gui.config.comment.advanced.gui_messages",
                                                                                        CraftPresence.GUIS.generateArgumentMessage())
                                                                        ), screenInstance, true
                                                                );
                                                            }
                                                    )
                                            );
                                        }
                                )
                        ),
                        () -> {
                            if (!guiMessagesButton.isControlEnabled()) {
                                CraftPresence.GUIS.drawMultiLineString(
                                        StringUtils.splitTextByNewLine(
                                                ModUtils.TRANSLATOR.translate("gui.config.message.hover.access",
                                                        ModUtils.TRANSLATOR.translate("gui.config.name.advanced.enable_per_gui"))
                                        ), this, true);
                            } else {
                                CraftPresence.GUIS.drawMultiLineString(
                                        StringUtils.splitTextByNewLine(
                                                ModUtils.TRANSLATOR.translate("gui.config.comment.advanced.gui_messages",
                                                        CraftPresence.GUIS.generateArgumentMessage())
                                        ), this, true
                                );
                            }
                        }
                )
        );
        itemMessagesButton = addControl(
                new ExtendedButtonControl(
                        calc2, CraftPresence.GUIS.getButtonY(2),
                        160, 20,
                        "gui.config.name.advanced.item_messages",
                        () -> CraftPresence.GUIS.openScreen(
                                new SelectorGui(
                                        currentScreen,
                                        ModUtils.TRANSLATOR.translate("gui.config.title.selector.item"), CraftPresence.TILE_ENTITIES.TILE_ENTITY_NAMES,
                                        null, null,
                                        true, true, RenderType.ItemData,
                                        null,
                                        (currentValue, parentScreen) -> {
                                            // Event to occur when Setting Dynamic/Specific Data
                                            CraftPresence.GUIS.openScreen(
                                                    new DynamicEditorGui(
                                                            parentScreen, currentValue,
                                                            (attributeName, screenInstance) -> {
                                                                // Event to occur when initializing new data
                                                                screenInstance.primaryMessage = screenInstance.originalPrimaryMessage = CONFIG.itemMessages.getOrDefault("default", "");
                                                            },
                                                            (attributeName, screenInstance) -> {
                                                                // Event to occur when initializing existing data
                                                                screenInstance.mainTitle = ModUtils.TRANSLATOR.translate("gui.config.title.item.edit_specific_item", attributeName);
                                                                screenInstance.originalPrimaryMessage = CONFIG.itemMessages.getOrDefault("default", "");
                                                                screenInstance.primaryMessage = CONFIG.itemMessages.getOrDefault(attributeName, screenInstance.originalPrimaryMessage);
                                                            },
                                                            (screenInstance, attributeName, inputText) -> {
                                                                // Event to occur when adjusting set data
                                                                CraftPresence.CONFIG.hasChanged = true;
                                                                CONFIG.itemMessages.put(attributeName, inputText);
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
                                                                CraftPresence.CONFIG.hasChanged = true;
                                                                CONFIG.itemMessages.remove(attributeName);
                                                                CraftPresence.TILE_ENTITIES.ITEM_NAMES.remove(attributeName);
                                                                CraftPresence.TILE_ENTITIES.BLOCK_NAMES.remove(attributeName);
                                                                CraftPresence.TILE_ENTITIES.TILE_ENTITY_NAMES.remove(attributeName);
                                                            }, null,
                                                            (attributeName, screenInstance) -> {
                                                                // Event to occur when Hovering over Message Label
                                                                CraftPresence.GUIS.drawMultiLineString(
                                                                        StringUtils.splitTextByNewLine(
                                                                                ModUtils.TRANSLATOR.translate("gui.config.comment.advanced.item_messages",
                                                                                        CraftPresence.TILE_ENTITIES.generateArgumentMessage(),
                                                                                        ModUtils.TRANSLATOR.translate("gui.config.message.tags",
                                                                                                CraftPresence.TILE_ENTITIES.generatePlaceholderString(
                                                                                                        attributeName, screenInstance.isDebugMode(),
                                                                                                        CraftPresence.TILE_ENTITIES.getListFromName(attributeName)
                                                                                                )
                                                                                        )
                                                                                )
                                                                        ), screenInstance, true
                                                                );
                                                            }
                                                    )
                                            );
                                        }
                                )
                        ),
                        () -> {
                            if (!itemMessagesButton.isControlEnabled()) {
                                CraftPresence.GUIS.drawMultiLineString(
                                        StringUtils.splitTextByNewLine(
                                                ModUtils.TRANSLATOR.translate("gui.config.message.hover.access",
                                                        ModUtils.TRANSLATOR.translate("gui.config.name.advanced.enable_per_item"))
                                        ), this, true
                                );
                            } else {
                                CraftPresence.GUIS.drawMultiLineString(
                                        StringUtils.splitTextByNewLine(
                                                ModUtils.TRANSLATOR.translate("gui.config.comment.advanced.item_messages",
                                                        CraftPresence.TILE_ENTITIES.generateArgumentMessage(),
                                                        ModUtils.TRANSLATOR.translate("gui.config.message.tags",
                                                                CraftPresence.TILE_ENTITIES.generatePlaceholderString(
                                                                        "", isDebugMode(),
                                                                        CraftPresence.TILE_ENTITIES.getListFromName("")
                                                                )
                                                        )
                                                )
                                        ), this, true
                                );
                            }
                        }
                )
        );
        entityTargetMessagesButton = addControl(
                new ExtendedButtonControl(
                        calc1, CraftPresence.GUIS.getButtonY(3),
                        160, 20,
                        "gui.config.name.advanced.entity_target_messages",
                        () -> CraftPresence.GUIS.openScreen(
                                new SelectorGui(
                                        currentScreen,
                                        ModUtils.TRANSLATOR.translate("gui.config.title.selector.entity"), CraftPresence.ENTITIES.ENTITY_NAMES,
                                        null, null,
                                        true, true, RenderType.EntityData,
                                        null,
                                        (currentValue, parentScreen) -> {
                                            // Event to occur when Setting Dynamic/Specific Data
                                            CraftPresence.GUIS.openScreen(
                                                    new DynamicEditorGui(
                                                            parentScreen, currentValue,
                                                            (attributeName, screenInstance) -> {
                                                                // Event to occur when initializing new data
                                                                screenInstance.defaultData = CONFIG.entitySettings.targetData.get("default");
                                                                screenInstance.primaryMessage = screenInstance.originalPrimaryMessage = Config.getProperty(screenInstance.defaultData, "textOverride") != null ? screenInstance.defaultData.getTextOverride() : "";
                                                            },
                                                            (attributeName, screenInstance) -> {
                                                                // Event to occur when initializing existing data
                                                                screenInstance.defaultData = CONFIG.entitySettings.targetData.get("default");
                                                                screenInstance.currentData = CONFIG.entitySettings.targetData.get(attributeName);
                                                                screenInstance.isPreliminaryData = screenInstance.currentData == null;
                                                                screenInstance.mainTitle = ModUtils.TRANSLATOR.translate("gui.config.title.entity.edit_specific_entity", attributeName);
                                                                screenInstance.originalPrimaryMessage = Config.getProperty(screenInstance.defaultData, "textOverride") != null ? screenInstance.defaultData.getTextOverride() : "";
                                                                screenInstance.primaryMessage = Config.getProperty(screenInstance.currentData, "textOverride") != null ? screenInstance.currentData.getTextOverride() : screenInstance.originalPrimaryMessage;
                                                            },
                                                            (screenInstance, attributeName, inputText) -> {
                                                                // Event to occur when adjusting set data
                                                                screenInstance.currentData.setTextOverride(inputText);
                                                                CraftPresence.CONFIG.hasChanged = true;
                                                                CONFIG.entitySettings.targetData.put(attributeName, screenInstance.currentData);
                                                                if (!CraftPresence.ENTITIES.ENTITY_NAMES.contains(attributeName)) {
                                                                    CraftPresence.ENTITIES.ENTITY_NAMES.add(attributeName);
                                                                }
                                                            },
                                                            (screenInstance, attributeName, inputText) -> {
                                                                // Event to occur when removing set data
                                                                CraftPresence.CONFIG.hasChanged = true;
                                                                CONFIG.entitySettings.targetData.remove(attributeName);
                                                                if (!screenInstance.isPreliminaryData) {
                                                                    CraftPresence.ENTITIES.ENTITY_NAMES.remove(attributeName);
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
                                                                    final String defaultIcon = Config.getProperty(screenInstance.defaultData, "iconOverride") != null ? screenInstance.defaultData.getIconOverride() : CONFIG.entitySettings.fallbackEntityIcon;
                                                                    final String specificIcon = Config.getProperty(screenInstance.currentData, "iconOverride") != null ? screenInstance.currentData.getIconOverride() : defaultIcon;
                                                                    CraftPresence.GUIS.openScreen(
                                                                            new SelectorGui(
                                                                                    screenInstance,
                                                                                    ModUtils.TRANSLATOR.translate("gui.config.title.selector.icon"), DiscordAssetUtils.ASSET_LIST.keySet(),
                                                                                    specificIcon, attributeName,
                                                                                    true, false, RenderType.DiscordAsset,
                                                                                    (innerAttributeName, innerCurrentValue) -> {
                                                                                        // Inner-Event to occur when proceeding with adjusted data
                                                                                        final String defaultMessage = Config.getProperty(screenInstance.defaultData, "textOverride") != null ? screenInstance.defaultData.getTextOverride() : "";
                                                                                        final String currentMessage = Config.getProperty(screenInstance.currentData, "textOverride") != null ? screenInstance.currentData.getTextOverride() : "";

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
                                                                                ModUtils.TRANSLATOR.translate("gui.config.comment.advanced.entity_target_messages",
                                                                                        CraftPresence.ENTITIES.generateArgumentMessage("&TARGETENTITY&", "&TARGETENTITY:"),
                                                                                        ModUtils.TRANSLATOR.translate("gui.config.message.tags",
                                                                                                CraftPresence.ENTITIES.generatePlaceholderString(
                                                                                                        attributeName, screenInstance.isDebugMode(),
                                                                                                        CraftPresence.ENTITIES.getListFromName(attributeName)
                                                                                                )
                                                                                        )
                                                                                )
                                                                        ), screenInstance, true
                                                                );
                                                            }
                                                    )
                                            );
                                        }
                                )
                        ),
                        () -> {
                            if (!entityTargetMessagesButton.isControlEnabled()) {
                                CraftPresence.GUIS.drawMultiLineString(
                                        StringUtils.splitTextByNewLine(
                                                ModUtils.TRANSLATOR.translate("gui.config.message.hover.access",
                                                        ModUtils.TRANSLATOR.translate("gui.config.name.advanced.enable_per_entity"))
                                        ), this, true
                                );
                            } else {
                                CraftPresence.GUIS.drawMultiLineString(
                                        StringUtils.splitTextByNewLine(
                                                ModUtils.TRANSLATOR.translate("gui.config.comment.advanced.entity_target_messages",
                                                        CraftPresence.ENTITIES.generateArgumentMessage("&TARGETENTITY&", "&TARGETENTITY:"),
                                                        ModUtils.TRANSLATOR.translate("gui.config.message.tags",
                                                                CraftPresence.ENTITIES.generatePlaceholderString(
                                                                        CraftPresence.ENTITIES.getEntityName(
                                                                                CraftPresence.ENTITIES.CURRENT_TARGET, CraftPresence.ENTITIES.CURRENT_TARGET_NAME
                                                                        ), isDebugMode(), CraftPresence.ENTITIES.CURRENT_TARGET_TAGS
                                                                )
                                                        )
                                                )
                                        ), this, true
                                );
                            }
                        }
                )
        );
        entityRidingMessagesButton = addControl(
                new ExtendedButtonControl(
                        calc2, CraftPresence.GUIS.getButtonY(3),
                        160, 20,
                        "gui.config.name.advanced.entity_riding_messages",
                        () -> CraftPresence.GUIS.openScreen(
                                new SelectorGui(
                                        currentScreen,
                                        ModUtils.TRANSLATOR.translate("gui.config.title.selector.entity"), CraftPresence.ENTITIES.ENTITY_NAMES,
                                        null, null,
                                        true, true, RenderType.EntityData,
                                        null,
                                        (currentValue, parentScreen) -> {
                                            // Event to occur when Setting Dynamic/Specific Data
                                            CraftPresence.GUIS.openScreen(
                                                    new DynamicEditorGui(
                                                            parentScreen, currentValue,
                                                            (attributeName, screenInstance) -> {
                                                                // Event to occur when initializing new data
                                                                screenInstance.defaultData = CONFIG.entitySettings.ridingData.get("default");
                                                                screenInstance.primaryMessage = screenInstance.originalPrimaryMessage = Config.getProperty(screenInstance.defaultData, "textOverride") != null ? screenInstance.defaultData.getTextOverride() : "";
                                                            },
                                                            (attributeName, screenInstance) -> {
                                                                // Event to occur when initializing existing data
                                                                screenInstance.defaultData = CONFIG.entitySettings.ridingData.get("default");
                                                                screenInstance.currentData = CONFIG.entitySettings.ridingData.get(attributeName);
                                                                screenInstance.isPreliminaryData = screenInstance.currentData == null;
                                                                screenInstance.mainTitle = ModUtils.TRANSLATOR.translate("gui.config.title.entity.edit_specific_entity", attributeName);
                                                                screenInstance.originalPrimaryMessage = Config.getProperty(screenInstance.defaultData, "textOverride") != null ? screenInstance.defaultData.getTextOverride() : "";
                                                                screenInstance.primaryMessage = Config.getProperty(screenInstance.currentData, "textOverride") != null ? screenInstance.currentData.getTextOverride() : screenInstance.originalPrimaryMessage;
                                                            },
                                                            (screenInstance, attributeName, inputText) -> {
                                                                // Event to occur when adjusting set data
                                                                screenInstance.currentData.setTextOverride(inputText);
                                                                CraftPresence.CONFIG.hasChanged = true;
                                                                CONFIG.entitySettings.ridingData.put(attributeName, screenInstance.currentData);
                                                                if (!CraftPresence.ENTITIES.ENTITY_NAMES.contains(attributeName)) {
                                                                    CraftPresence.ENTITIES.ENTITY_NAMES.add(attributeName);
                                                                }
                                                            },
                                                            (screenInstance, attributeName, inputText) -> {
                                                                // Event to occur when removing set data
                                                                CraftPresence.CONFIG.hasChanged = true;
                                                                CONFIG.entitySettings.ridingData.remove(attributeName);
                                                                if (!screenInstance.isPreliminaryData) {
                                                                    CraftPresence.ENTITIES.ENTITY_NAMES.remove(attributeName);
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
                                                                    final String defaultIcon = Config.getProperty(screenInstance.defaultData, "iconOverride") != null ? screenInstance.defaultData.getIconOverride() : CONFIG.entitySettings.fallbackEntityIcon;
                                                                    final String specificIcon = Config.getProperty(screenInstance.currentData, "iconOverride") != null ? screenInstance.currentData.getIconOverride() : defaultIcon;
                                                                    CraftPresence.GUIS.openScreen(
                                                                            new SelectorGui(
                                                                                    screenInstance,
                                                                                    ModUtils.TRANSLATOR.translate("gui.config.title.selector.icon"), DiscordAssetUtils.ASSET_LIST.keySet(),
                                                                                    specificIcon, attributeName,
                                                                                    true, false, RenderType.DiscordAsset,
                                                                                    (innerAttributeName, innerCurrentValue) -> {
                                                                                        // Inner-Event to occur when proceeding with adjusted data
                                                                                        final String defaultMessage = Config.getProperty(screenInstance.defaultData, "textOverride") != null ? screenInstance.defaultData.getTextOverride() : "";
                                                                                        final String currentMessage = Config.getProperty(screenInstance.currentData, "textOverride") != null ? screenInstance.currentData.getTextOverride() : "";

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
                                                                                ModUtils.TRANSLATOR.translate("gui.config.comment.advanced.entity_riding_messages",
                                                                                        CraftPresence.ENTITIES.generateArgumentMessage("&RIDINGENTITY&", "&RIDINGENTITY:"),
                                                                                        ModUtils.TRANSLATOR.translate("gui.config.message.tags",
                                                                                                CraftPresence.ENTITIES.generatePlaceholderString(
                                                                                                        attributeName, screenInstance.isDebugMode(),
                                                                                                        CraftPresence.ENTITIES.getListFromName(attributeName)
                                                                                                )
                                                                                        )
                                                                                )
                                                                        ), screenInstance, true
                                                                );
                                                            }
                                                    )
                                            );
                                        }
                                )
                        ),
                        () -> {
                            if (!entityRidingMessagesButton.isControlEnabled()) {
                                CraftPresence.GUIS.drawMultiLineString(
                                        StringUtils.splitTextByNewLine(
                                                ModUtils.TRANSLATOR.translate("gui.config.message.hover.access",
                                                        ModUtils.TRANSLATOR.translate("gui.config.name.advanced.enable_per_entity"))
                                        ), this, true
                                );
                            } else {
                                CraftPresence.GUIS.drawMultiLineString(
                                        StringUtils.splitTextByNewLine(
                                                ModUtils.TRANSLATOR.translate("gui.config.comment.advanced.entity_riding_messages",
                                                        CraftPresence.ENTITIES.generateArgumentMessage("&RIDINGENTITY&", "&RIDINGENTITY:"),
                                                        ModUtils.TRANSLATOR.translate("gui.config.message.tags",
                                                                CraftPresence.ENTITIES.generatePlaceholderString(
                                                                        CraftPresence.ENTITIES.getEntityName(
                                                                                CraftPresence.ENTITIES.CURRENT_RIDING, CraftPresence.ENTITIES.CURRENT_RIDING_NAME
                                                                        ), isDebugMode(), CraftPresence.ENTITIES.CURRENT_RIDING_TAGS
                                                                )
                                                        )
                                                )
                                        ), this, true
                                );
                            }
                        }
                )
        );

        enableCommandsButton = addControl(
                new CheckBoxControl(
                        calc1, CraftPresence.GUIS.getButtonY(4),
                        "gui.config.name.advanced.enable_commands",
                        CONFIG.enableCommands,
                        null,
                        () -> CraftPresence.GUIS.drawMultiLineString(
                                StringUtils.splitTextByNewLine(
                                        ModUtils.TRANSLATOR.translate("gui.config.comment.advanced.enable_commands")
                                ), this, true
                        )
                )
        );
        enablePerGuiButton = addControl(
                new CheckBoxControl(
                        calc2, CraftPresence.GUIS.getButtonY(4),
                        "gui.config.name.advanced.enable_per_gui",
                        CONFIG.enablePerGui,
                        null,
                        () -> CraftPresence.GUIS.drawMultiLineString(
                                StringUtils.splitTextByNewLine(
                                        ModUtils.TRANSLATOR.translate("gui.config.comment.advanced.enable_per_gui")
                                ), this, true
                        )
                )
        );
        enablePerItemButton = addControl(
                new CheckBoxControl(
                        calc1, CraftPresence.GUIS.getButtonY(5, -10),
                        "gui.config.name.advanced.enable_per_item",
                        CONFIG.enablePerItem,
                        null,
                        () -> CraftPresence.GUIS.drawMultiLineString(
                                StringUtils.splitTextByNewLine(
                                        ModUtils.TRANSLATOR.translate("gui.config.comment.advanced.enable_per_item")
                                ), this, true
                        )
                )
        );
        enablePerEntityButton = addControl(
                new CheckBoxControl(
                        calc2, CraftPresence.GUIS.getButtonY(5, -10),
                        "gui.config.name.advanced.enable_per_entity",
                        CONFIG.enablePerEntity,
                        null,
                        () -> CraftPresence.GUIS.drawMultiLineString(
                                StringUtils.splitTextByNewLine(
                                        ModUtils.TRANSLATOR.translate("gui.config.comment.advanced.enable_per_entity")
                                ), this, true
                        )
                )
        );
        renderTooltipsButton = addControl(
                new CheckBoxControl(
                        calc1, CraftPresence.GUIS.getButtonY(6, -20),
                        "gui.config.name.advanced.render_tooltips",
                        CONFIG.renderTooltips,
                        null,
                        () -> CraftPresence.GUIS.drawMultiLineString(
                                StringUtils.splitTextByNewLine(
                                        ModUtils.TRANSLATOR.translate("gui.config.comment.advanced.render_tooltips")
                                ), this, true
                        )
                )
        );
        formatWordsButton = addControl(
                new CheckBoxControl(
                        calc2, CraftPresence.GUIS.getButtonY(6, -20),
                        "gui.config.name.advanced.format_words",
                        CONFIG.formatWords,
                        null,
                        () -> CraftPresence.GUIS.drawMultiLineString(
                                StringUtils.splitTextByNewLine(
                                        ModUtils.TRANSLATOR.translate("gui.config.comment.advanced.format_words")
                                ), this, true
                        )
                )
        );
        debugModeButton = addControl(
                new CheckBoxControl(
                        calc1, CraftPresence.GUIS.getButtonY(7, -30),
                        "gui.config.name.advanced.debug_mode",
                        CONFIG.debugMode,
                        null,
                        () -> CraftPresence.GUIS.drawMultiLineString(
                                StringUtils.splitTextByNewLine(
                                        ModUtils.TRANSLATOR.translate("gui.config.comment.advanced.debug_mode", CraftPresence.isDevStatusOverridden)
                                ), this, true
                        )
                )
        );
        verboseModeButton = addControl(
                new CheckBoxControl(
                        calc2, CraftPresence.GUIS.getButtonY(7, -30),
                        "gui.config.name.advanced.verbose_mode",
                        CONFIG.verboseMode,
                        null,
                        () -> CraftPresence.GUIS.drawMultiLineString(
                                StringUtils.splitTextByNewLine(
                                        ModUtils.TRANSLATOR.translate("gui.config.comment.advanced.verbose_mode", CraftPresence.isVerboseStatusOverridden)
                                ), this, true
                        )
                )
        );
        allowPlaceholderPreviewsButton = addControl(
                new CheckBoxControl(
                        calc1, CraftPresence.GUIS.getButtonY(8, -40),
                        "gui.config.name.advanced.allow_placeholder_previews",
                        CONFIG.allowPlaceholderPreviews,
                        null,
                        () -> CraftPresence.GUIS.drawMultiLineString(
                                StringUtils.splitTextByNewLine(
                                        ModUtils.TRANSLATOR.translate("gui.config.comment.advanced.allow_placeholder_previews")
                                ), this, true
                        )
                )
        );
        allowPlaceholderOperatorsButton = addControl(
                new CheckBoxControl(
                        calc2, CraftPresence.GUIS.getButtonY(8, -40),
                        "gui.config.name.advanced.allow_placeholder_operators",
                        CONFIG.allowPlaceholderOperators,
                        null,
                        () -> CraftPresence.GUIS.drawMultiLineString(
                                StringUtils.splitTextByNewLine(
                                        ModUtils.TRANSLATOR.translate("gui.config.comment.advanced.allow_placeholder_operators")
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
                            if (allowEndpointIconsButton.isChecked() != CONFIG.allowEndpointIcons) {
                                CraftPresence.CONFIG.hasChanged = true;
                                CONFIG.allowEndpointIcons = allowEndpointIconsButton.isChecked();
                            }
                            if (!refreshRate.getControlMessage().equals(Integer.toString(CONFIG.refreshRate))) {
                                CraftPresence.CONFIG.hasChanged = true;
                                CraftPresence.CONFIG.hasClientPropertiesChanged = true;
                                CONFIG.refreshRate = StringUtils.getValidInteger(refreshRate.getControlMessage()).getSecond();
                            }
                            if (enableCommandsButton.isChecked() != CONFIG.enableCommands) {
                                CraftPresence.CONFIG.hasChanged = true;
                                CONFIG.enableCommands = enableCommandsButton.isChecked();
                            }
                            if (enablePerGuiButton.isChecked() != CONFIG.enablePerGui) {
                                CraftPresence.CONFIG.hasChanged = true;
                                CraftPresence.CONFIG.hasClientPropertiesChanged = true;
                                CONFIG.enablePerGui = enablePerGuiButton.isChecked();
                            }
                            if (enablePerItemButton.isChecked() != CONFIG.enablePerItem) {
                                CraftPresence.CONFIG.hasChanged = true;
                                CraftPresence.CONFIG.hasClientPropertiesChanged = true;
                                CONFIG.enablePerItem = enablePerItemButton.isChecked();
                            }
                            if (enablePerEntityButton.isChecked() != CONFIG.enablePerEntity) {
                                CraftPresence.CONFIG.hasChanged = true;
                                CraftPresence.CONFIG.hasClientPropertiesChanged = true;
                                CONFIG.enablePerEntity = enablePerEntityButton.isChecked();
                            }
                            if (renderTooltipsButton.isChecked() != CONFIG.renderTooltips) {
                                CraftPresence.CONFIG.hasChanged = true;
                                CONFIG.renderTooltips = renderTooltipsButton.isChecked();
                            }
                            if (formatWordsButton.isChecked() != CONFIG.formatWords) {
                                CraftPresence.CONFIG.hasChanged = true;
                                CONFIG.formatWords = formatWordsButton.isChecked();
                            }
                            if (debugModeButton.isChecked() != CONFIG.debugMode) {
                                CraftPresence.CONFIG.hasChanged = true;
                                CraftPresence.CONFIG.hasClientPropertiesChanged = true;
                                CONFIG.debugMode = debugModeButton.isChecked();
                            }
                            if (verboseModeButton.isChecked() != CONFIG.verboseMode) {
                                CraftPresence.CONFIG.hasChanged = true;
                                CraftPresence.CONFIG.hasClientPropertiesChanged = true;
                                CONFIG.verboseMode = verboseModeButton.isChecked();
                            }
                            if (allowPlaceholderPreviewsButton.isChecked() != CONFIG.allowPlaceholderPreviews) {
                                CraftPresence.CONFIG.hasChanged = true;
                                CONFIG.allowPlaceholderPreviews = allowPlaceholderPreviewsButton.isChecked();
                            }
                            if (allowPlaceholderOperatorsButton.isChecked() != CONFIG.allowPlaceholderOperators) {
                                CraftPresence.CONFIG.hasChanged = true;
                                CraftPresence.CONFIG.hasClientPropertiesChanged = true;
                                CONFIG.allowPlaceholderOperators = allowPlaceholderOperatorsButton.isChecked();
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
        final String subTitle = ModUtils.TRANSLATOR.translate("gui.config.title.advanced");
        final String refreshRateText = ModUtils.TRANSLATOR.translate("gui.config.name.advanced.refresh_rate");

        renderString(mainTitle, (getScreenWidth() / 2f) - (getStringWidth(mainTitle) / 2f), 10, 0xFFFFFF);
        renderString(subTitle, (getScreenWidth() / 2f) - (getStringWidth(subTitle) / 2f), 20, 0xFFFFFF);
        renderString(refreshRateText, (getScreenWidth() / 2f) + 18, CraftPresence.GUIS.getButtonY(1, 5), 0xFFFFFF);

        final Pair<Boolean, Integer> refreshRateData = StringUtils.getValidInteger(refreshRate.getControlMessage());
        proceedButton.setControlEnabled(
                (refreshRateData.getFirst() && refreshRateData.getSecond() >= SystemUtils.MINIMUM_REFRESH_RATE)
        );

        guiMessagesButton.setControlEnabled(!CraftPresence.CONFIG.hasChanged ? CraftPresence.GUIS.enabled : guiMessagesButton.isControlEnabled());
        itemMessagesButton.setControlEnabled(!CraftPresence.CONFIG.hasChanged ? CraftPresence.TILE_ENTITIES.enabled : itemMessagesButton.isControlEnabled());
        entityTargetMessagesButton.setControlEnabled(!CraftPresence.CONFIG.hasChanged ? CraftPresence.ENTITIES.enabled : entityTargetMessagesButton.isControlEnabled());
        entityRidingMessagesButton.setControlEnabled(!CraftPresence.CONFIG.hasChanged ? CraftPresence.ENTITIES.enabled : entityRidingMessagesButton.isControlEnabled());
    }

    @Override
    public void postRender() {
        final String refreshRateText = ModUtils.TRANSLATOR.translate("gui.config.name.advanced.refresh_rate");

        // Hovering over Refresh Rate Message Label
        if (CraftPresence.GUIS.isMouseOver(getMouseX(), getMouseY(), (getScreenWidth() / 2f) + 18, CraftPresence.GUIS.getButtonY(1, 5), getStringWidth(refreshRateText), getFontHeight())) {
            CraftPresence.GUIS.drawMultiLineString(
                    StringUtils.splitTextByNewLine(
                            ModUtils.TRANSLATOR.translate("gui.config.comment.advanced.refresh_rate")
                    ), this, true
            );
        }
    }
}
