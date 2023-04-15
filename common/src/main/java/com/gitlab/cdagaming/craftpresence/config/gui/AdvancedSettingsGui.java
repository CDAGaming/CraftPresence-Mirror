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
import com.gitlab.cdagaming.craftpresence.utils.gui.impl.ConfigurationGui;
import com.gitlab.cdagaming.craftpresence.utils.gui.impl.DynamicEditorGui;
import com.gitlab.cdagaming.craftpresence.utils.gui.impl.SelectorGui;
import net.minecraft.client.gui.GuiScreen;

@SuppressWarnings("DuplicatedCode")
public class AdvancedSettingsGui extends ConfigurationGui<Advanced> {
    private final Advanced INSTANCE;
    private ExtendedButtonControl guiMessagesButton, itemMessagesButton, entityTargetMessagesButton, entityRidingMessagesButton;
    private CheckBoxControl enablePerGuiButton, enablePerItemButton, enablePerEntityButton,
            renderTooltipsButton, formatWordsButton, debugModeButton, verboseModeButton,
            allowPlaceholderPreviewsButton, allowEndpointIconsButton;
    private ExtendedTextControl refreshRate;

    AdvancedSettingsGui(GuiScreen parentScreen) {
        super(parentScreen, "gui.config.title", "gui.config.title.advanced");
        INSTANCE = getCurrentData().copy();
    }

    @Override
    protected void appendControls() {
        super.appendControls();

        final int calc1 = (getScreenWidth() / 2) - 160;
        final int calc2 = (getScreenWidth() / 2) + 3;

        allowEndpointIconsButton = childFrame.addControl(
                new CheckBoxControl(
                        calc1 + 2, CraftPresence.GUIS.getButtonY(0, 3),
                        "gui.config.name.advanced.allow_endpoint_icons",
                        getCurrentData().allowEndpointIcons,
                        null,
                        () -> CraftPresence.GUIS.drawMultiLineString(
                                StringUtils.splitTextByNewLine(
                                        ModUtils.TRANSLATOR.translate("gui.config.comment.advanced.allow_endpoint_icons")
                                ), this, true
                        )
                )
        );

        refreshRate = childFrame.addControl(
                new ExtendedTextControl(
                        getFontRenderer(),
                        (getScreenWidth() / 2) + 103, CraftPresence.GUIS.getButtonY(0),
                        45, 20
                )
        );
        refreshRate.setControlMessage(Integer.toString(getCurrentData().refreshRate));
        refreshRate.setControlMaxLength(3);

        guiMessagesButton = childFrame.addControl(
                new ExtendedButtonControl(
                        calc1, CraftPresence.GUIS.getButtonY(1),
                        160, 20,
                        "gui.config.name.advanced.gui_messages",
                        () -> CraftPresence.GUIS.openScreen(
                                new SelectorGui(
                                        currentScreen,
                                        ModUtils.TRANSLATOR.translate("gui.config.title.selector.gui"), CraftPresence.GUIS.GUI_NAMES,
                                        null, null,
                                        true, true, RenderType.None.setIdentifierType(
                                        RenderType.IdentifierType.Gui),
                                        null,
                                        (currentValue, parentScreen) -> {
                                            // Event to occur when Setting Dynamic/Specific Data
                                            CraftPresence.GUIS.openScreen(
                                                    new DynamicEditorGui(
                                                            parentScreen, currentValue,
                                                            (attributeName, screenInstance) -> {
                                                                // Event to occur when initializing new data
                                                                screenInstance.defaultData = getCurrentData().guiSettings.guiData.get("default");
                                                                screenInstance.primaryMessage = screenInstance.originalPrimaryMessage = Config.getProperty(screenInstance.defaultData, "textOverride") != null ? screenInstance.defaultData.getTextOverride() : "";
                                                            },
                                                            (attributeName, screenInstance) -> {
                                                                // Event to occur when initializing existing data
                                                                screenInstance.defaultData = getCurrentData().guiSettings.guiData.get("default");
                                                                screenInstance.currentData = getCurrentData().guiSettings.guiData.get(attributeName);
                                                                screenInstance.isPreliminaryData = screenInstance.currentData == null;
                                                                screenInstance.mainTitle = ModUtils.TRANSLATOR.translate("gui.config.title.gui.edit_specific_gui", attributeName);
                                                                screenInstance.originalPrimaryMessage = Config.getProperty(screenInstance.defaultData, "textOverride") != null ? screenInstance.defaultData.getTextOverride() : "";
                                                                screenInstance.primaryMessage = Config.getProperty(screenInstance.currentData, "textOverride") != null ? screenInstance.currentData.getTextOverride() : screenInstance.originalPrimaryMessage;
                                                            },
                                                            (screenInstance, attributeName, inputText) -> {
                                                                // Event to occur when adjusting set data
                                                                screenInstance.currentData.setTextOverride(inputText);
                                                                CraftPresence.CONFIG.hasChanged = true;
                                                                getCurrentData().guiSettings.guiData.put(attributeName, screenInstance.currentData);
                                                                if (!CraftPresence.GUIS.GUI_NAMES.contains(attributeName)) {
                                                                    CraftPresence.GUIS.GUI_NAMES.add(attributeName);
                                                                }
                                                            },
                                                            (screenInstance, attributeName, inputText) -> {
                                                                // Event to occur when removing set data
                                                                CraftPresence.CONFIG.hasChanged = true;
                                                                getCurrentData().guiSettings.guiData.remove(attributeName);
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
                                                                    final String defaultIcon = Config.getProperty(screenInstance.defaultData, "iconOverride") != null ? screenInstance.defaultData.getIconOverride() : getCurrentData().guiSettings.fallbackGuiIcon;
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
                                                                                ModUtils.TRANSLATOR.translate("gui.config.comment.advanced.gui_messages",
                                                                                        CraftPresence.CLIENT.generateArgumentMessage("screen."))
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
                                                        CraftPresence.CLIENT.generateArgumentMessage("screen."))
                                        ), this, true
                                );
                            }
                        }
                )
        );
        itemMessagesButton = childFrame.addControl(
                new ExtendedButtonControl(
                        calc2, CraftPresence.GUIS.getButtonY(1),
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
                                                                screenInstance.primaryMessage = screenInstance.originalPrimaryMessage = getCurrentData().itemMessages.getOrDefault("default", "");
                                                            },
                                                            (attributeName, screenInstance) -> {
                                                                // Event to occur when initializing existing data
                                                                screenInstance.mainTitle = ModUtils.TRANSLATOR.translate("gui.config.title.item.edit_specific_item", attributeName);
                                                                screenInstance.originalPrimaryMessage = getCurrentData().itemMessages.getOrDefault("default", "");
                                                                screenInstance.primaryMessage = getCurrentData().itemMessages.getOrDefault(attributeName, screenInstance.originalPrimaryMessage);
                                                            },
                                                            (screenInstance, attributeName, inputText) -> {
                                                                // Event to occur when adjusting set data
                                                                CraftPresence.CONFIG.hasChanged = true;
                                                                getCurrentData().itemMessages.put(attributeName, inputText);
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
                                                                getCurrentData().itemMessages.remove(attributeName);
                                                                CraftPresence.TILE_ENTITIES.ITEM_NAMES.remove(attributeName);
                                                                CraftPresence.TILE_ENTITIES.BLOCK_NAMES.remove(attributeName);
                                                                CraftPresence.TILE_ENTITIES.TILE_ENTITY_NAMES.remove(attributeName);
                                                            }, null,
                                                            (attributeName, screenInstance) -> {
                                                                // Event to occur when Hovering over Message Label
                                                                CraftPresence.GUIS.drawMultiLineString(
                                                                        StringUtils.splitTextByNewLine(
                                                                                ModUtils.TRANSLATOR.translate("gui.config.comment.advanced.item_messages",
                                                                                        CraftPresence.CLIENT.generateArgumentMessage("item.")
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
                                                        CraftPresence.CLIENT.generateArgumentMessage("item.")
                                                )
                                        ), this, true
                                );
                            }
                        }
                )
        );
        entityTargetMessagesButton = childFrame.addControl(
                new ExtendedButtonControl(
                        calc1, CraftPresence.GUIS.getButtonY(2),
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
                                                                screenInstance.defaultData = getCurrentData().entitySettings.targetData.get("default");
                                                                screenInstance.primaryMessage = screenInstance.originalPrimaryMessage = Config.getProperty(screenInstance.defaultData, "textOverride") != null ? screenInstance.defaultData.getTextOverride() : "";
                                                            },
                                                            (attributeName, screenInstance) -> {
                                                                // Event to occur when initializing existing data
                                                                screenInstance.defaultData = getCurrentData().entitySettings.targetData.get("default");
                                                                screenInstance.currentData = getCurrentData().entitySettings.targetData.get(attributeName);
                                                                screenInstance.isPreliminaryData = screenInstance.currentData == null;
                                                                screenInstance.mainTitle = ModUtils.TRANSLATOR.translate("gui.config.title.entity.edit_specific_entity", attributeName);
                                                                screenInstance.originalPrimaryMessage = Config.getProperty(screenInstance.defaultData, "textOverride") != null ? screenInstance.defaultData.getTextOverride() : "";
                                                                screenInstance.primaryMessage = Config.getProperty(screenInstance.currentData, "textOverride") != null ? screenInstance.currentData.getTextOverride() : screenInstance.originalPrimaryMessage;
                                                            },
                                                            (screenInstance, attributeName, inputText) -> {
                                                                // Event to occur when adjusting set data
                                                                screenInstance.currentData.setTextOverride(inputText);
                                                                CraftPresence.CONFIG.hasChanged = true;
                                                                getCurrentData().entitySettings.targetData.put(attributeName, screenInstance.currentData);
                                                                if (!CraftPresence.ENTITIES.ENTITY_NAMES.contains(attributeName)) {
                                                                    CraftPresence.ENTITIES.ENTITY_NAMES.add(attributeName);
                                                                }
                                                            },
                                                            (screenInstance, attributeName, inputText) -> {
                                                                // Event to occur when removing set data
                                                                CraftPresence.CONFIG.hasChanged = true;
                                                                getCurrentData().entitySettings.targetData.remove(attributeName);
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
                                                                    final String defaultIcon = Config.getProperty(screenInstance.defaultData, "iconOverride") != null ? screenInstance.defaultData.getIconOverride() : getCurrentData().entitySettings.fallbackEntityIcon;
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
                                                                                ModUtils.TRANSLATOR.translate("gui.config.comment.advanced.entity_target_messages",
                                                                                        CraftPresence.CLIENT.generateArgumentMessage("entity.target.")
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
                                                        CraftPresence.CLIENT.generateArgumentMessage("entity.target.")
                                                )
                                        ), this, true
                                );
                            }
                        }
                )
        );
        entityRidingMessagesButton = childFrame.addControl(
                new ExtendedButtonControl(
                        calc2, CraftPresence.GUIS.getButtonY(2),
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
                                                                screenInstance.defaultData = getCurrentData().entitySettings.ridingData.get("default");
                                                                screenInstance.primaryMessage = screenInstance.originalPrimaryMessage = Config.getProperty(screenInstance.defaultData, "textOverride") != null ? screenInstance.defaultData.getTextOverride() : "";
                                                            },
                                                            (attributeName, screenInstance) -> {
                                                                // Event to occur when initializing existing data
                                                                screenInstance.defaultData = getCurrentData().entitySettings.ridingData.get("default");
                                                                screenInstance.currentData = getCurrentData().entitySettings.ridingData.get(attributeName);
                                                                screenInstance.isPreliminaryData = screenInstance.currentData == null;
                                                                screenInstance.mainTitle = ModUtils.TRANSLATOR.translate("gui.config.title.entity.edit_specific_entity", attributeName);
                                                                screenInstance.originalPrimaryMessage = Config.getProperty(screenInstance.defaultData, "textOverride") != null ? screenInstance.defaultData.getTextOverride() : "";
                                                                screenInstance.primaryMessage = Config.getProperty(screenInstance.currentData, "textOverride") != null ? screenInstance.currentData.getTextOverride() : screenInstance.originalPrimaryMessage;
                                                            },
                                                            (screenInstance, attributeName, inputText) -> {
                                                                // Event to occur when adjusting set data
                                                                screenInstance.currentData.setTextOverride(inputText);
                                                                CraftPresence.CONFIG.hasChanged = true;
                                                                getCurrentData().entitySettings.ridingData.put(attributeName, screenInstance.currentData);
                                                                if (!CraftPresence.ENTITIES.ENTITY_NAMES.contains(attributeName)) {
                                                                    CraftPresence.ENTITIES.ENTITY_NAMES.add(attributeName);
                                                                }
                                                            },
                                                            (screenInstance, attributeName, inputText) -> {
                                                                // Event to occur when removing set data
                                                                CraftPresence.CONFIG.hasChanged = true;
                                                                getCurrentData().entitySettings.ridingData.remove(attributeName);
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
                                                                    final String defaultIcon = Config.getProperty(screenInstance.defaultData, "iconOverride") != null ? screenInstance.defaultData.getIconOverride() : getCurrentData().entitySettings.fallbackEntityIcon;
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
                                                                                ModUtils.TRANSLATOR.translate("gui.config.comment.advanced.entity_riding_messages",
                                                                                        CraftPresence.CLIENT.generateArgumentMessage("entity.riding.")
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
                                                        CraftPresence.CLIENT.generateArgumentMessage("entity.riding.")
                                                )
                                        ), this, true
                                );
                            }
                        }
                )
        );

        enablePerGuiButton = childFrame.addControl(
                new CheckBoxControl(
                        calc1, CraftPresence.GUIS.getButtonY(3),
                        "gui.config.name.advanced.enable_per_gui",
                        getCurrentData().enablePerGui,
                        null,
                        () -> CraftPresence.GUIS.drawMultiLineString(
                                StringUtils.splitTextByNewLine(
                                        ModUtils.TRANSLATOR.translate("gui.config.comment.advanced.enable_per_gui")
                                ), this, true
                        )
                )
        );
        enablePerItemButton = childFrame.addControl(
                new CheckBoxControl(
                        calc2, CraftPresence.GUIS.getButtonY(3),
                        "gui.config.name.advanced.enable_per_item",
                        getCurrentData().enablePerItem,
                        null,
                        () -> CraftPresence.GUIS.drawMultiLineString(
                                StringUtils.splitTextByNewLine(
                                        ModUtils.TRANSLATOR.translate("gui.config.comment.advanced.enable_per_item")
                                ), this, true
                        )
                )
        );
        enablePerEntityButton = childFrame.addControl(
                new CheckBoxControl(
                        calc1, CraftPresence.GUIS.getButtonY(4, -10),
                        "gui.config.name.advanced.enable_per_entity",
                        getCurrentData().enablePerEntity,
                        null,
                        () -> CraftPresence.GUIS.drawMultiLineString(
                                StringUtils.splitTextByNewLine(
                                        ModUtils.TRANSLATOR.translate("gui.config.comment.advanced.enable_per_entity")
                                ), this, true
                        )
                )
        );
        renderTooltipsButton = childFrame.addControl(
                new CheckBoxControl(
                        calc2, CraftPresence.GUIS.getButtonY(4, -10),
                        "gui.config.name.advanced.render_tooltips",
                        getCurrentData().renderTooltips,
                        null,
                        () -> CraftPresence.GUIS.drawMultiLineString(
                                StringUtils.splitTextByNewLine(
                                        ModUtils.TRANSLATOR.translate("gui.config.comment.advanced.render_tooltips")
                                ), this, true
                        )
                )
        );
        formatWordsButton = childFrame.addControl(
                new CheckBoxControl(
                        calc1, CraftPresence.GUIS.getButtonY(5, -20),
                        "gui.config.name.advanced.format_words",
                        getCurrentData().formatWords,
                        null,
                        () -> CraftPresence.GUIS.drawMultiLineString(
                                StringUtils.splitTextByNewLine(
                                        ModUtils.TRANSLATOR.translate("gui.config.comment.advanced.format_words")
                                ), this, true
                        )
                )
        );
        debugModeButton = childFrame.addControl(
                new CheckBoxControl(
                        calc2, CraftPresence.GUIS.getButtonY(5, -20),
                        "gui.config.name.advanced.debug_mode",
                        getCurrentData().debugMode,
                        null,
                        () -> CraftPresence.GUIS.drawMultiLineString(
                                StringUtils.splitTextByNewLine(
                                        ModUtils.TRANSLATOR.translate("gui.config.comment.advanced.debug_mode", ModUtils.IS_DEV_FLAG)
                                ), this, true
                        )
                )
        );
        verboseModeButton = childFrame.addControl(
                new CheckBoxControl(
                        calc1, CraftPresence.GUIS.getButtonY(6, -30),
                        "gui.config.name.advanced.verbose_mode",
                        getCurrentData().verboseMode,
                        null,
                        () -> CraftPresence.GUIS.drawMultiLineString(
                                StringUtils.splitTextByNewLine(
                                        ModUtils.TRANSLATOR.translate("gui.config.comment.advanced.verbose_mode", ModUtils.IS_VERBOSE_FLAG)
                                ), this, true
                        )
                )
        );
        allowPlaceholderPreviewsButton = childFrame.addControl(
                new CheckBoxControl(
                        calc2, CraftPresence.GUIS.getButtonY(6, -30),
                        "gui.config.name.advanced.allow_placeholder_previews",
                        getCurrentData().allowPlaceholderPreviews,
                        null,
                        () -> CraftPresence.GUIS.drawMultiLineString(
                                StringUtils.splitTextByNewLine(
                                        ModUtils.TRANSLATOR.translate("gui.config.comment.advanced.allow_placeholder_previews")
                                ), this, true
                        )
                )
        );
        proceedButton.setOnHover(() -> {
            if (!proceedButton.isControlEnabled()) {
                CraftPresence.GUIS.drawMultiLineString(
                        StringUtils.splitTextByNewLine(
                                ModUtils.TRANSLATOR.translate("gui.config.message.hover.empty.default")
                        ), this, true
                );
            }
        });
    }

    @Override
    public void preRender() {
        final String refreshRateText = ModUtils.TRANSLATOR.translate("gui.config.name.advanced.refresh_rate");
        final float renderY = refreshRate.getBottom() - (refreshRate.getControlHeight() / 2f) - (childFrame.getFontHeight() / 2f);
        childFrame.renderString(refreshRateText, (getScreenWidth() / 2f) + 18, renderY, 0xFFFFFF);

        super.preRender();
    }

    @Override
    public void postRender() {
        final String refreshRateText = ModUtils.TRANSLATOR.translate("gui.config.name.advanced.refresh_rate");
        final float renderY = refreshRate.getBottom() - (refreshRate.getControlHeight() / 2f) - (childFrame.getFontHeight() / 2f);

        // Hovering over Refresh Rate Message Label
        if (CraftPresence.GUIS.isMouseOver(getMouseX(), getMouseY(), (getScreenWidth() / 2f) + 18, renderY, getStringWidth(refreshRateText), getFontHeight())) {
            CraftPresence.GUIS.drawMultiLineString(
                    StringUtils.splitTextByNewLine(
                            ModUtils.TRANSLATOR.translate("gui.config.comment.advanced.refresh_rate")
                    ), childFrame, true
            );
        }

        super.postRender();
    }

    @Override
    protected void syncRenderStates() {
        final Pair<Boolean, Integer> refreshRateData = StringUtils.getValidInteger(refreshRate.getControlMessage());
        proceedButton.setControlEnabled(
                (refreshRateData.getFirst() && refreshRateData.getSecond() >= SystemUtils.MINIMUM_REFRESH_RATE)
        );

        guiMessagesButton.setControlEnabled(CraftPresence.GUIS.enabled);
        itemMessagesButton.setControlEnabled(CraftPresence.TILE_ENTITIES.enabled);
        entityTargetMessagesButton.setControlEnabled(CraftPresence.ENTITIES.enabled);
        entityRidingMessagesButton.setControlEnabled(CraftPresence.ENTITIES.enabled);
    }

    @Override
    protected boolean canReset() {
        return !getCurrentData().equals(getOriginalData().getDefaults());
    }

    @Override
    protected void resetData() {
        setCurrentData(getOriginalData().getDefaults());
    }

    @Override
    protected void applySettings() {
        if (allowEndpointIconsButton.isChecked() != getCurrentData().allowEndpointIcons) {
            CraftPresence.CONFIG.hasChanged = true;
            getCurrentData().allowEndpointIcons = allowEndpointIconsButton.isChecked();
        }
        if (!refreshRate.getControlMessage().equals(Integer.toString(getCurrentData().refreshRate))) {
            CraftPresence.CONFIG.hasChanged = true;
            getCurrentData().refreshRate = StringUtils.getValidInteger(refreshRate.getControlMessage()).getSecond();
        }
        if (enablePerGuiButton.isChecked() != getCurrentData().enablePerGui) {
            CraftPresence.CONFIG.hasChanged = true;
            getCurrentData().enablePerGui = enablePerGuiButton.isChecked();
        }
        if (enablePerItemButton.isChecked() != getCurrentData().enablePerItem) {
            CraftPresence.CONFIG.hasChanged = true;
            getCurrentData().enablePerItem = enablePerItemButton.isChecked();
        }
        if (enablePerEntityButton.isChecked() != getCurrentData().enablePerEntity) {
            CraftPresence.CONFIG.hasChanged = true;
            getCurrentData().enablePerEntity = enablePerEntityButton.isChecked();
        }
        if (renderTooltipsButton.isChecked() != getCurrentData().renderTooltips) {
            CraftPresence.CONFIG.hasChanged = true;
            getCurrentData().renderTooltips = renderTooltipsButton.isChecked();
        }
        if (formatWordsButton.isChecked() != getCurrentData().formatWords) {
            CraftPresence.CONFIG.hasChanged = true;
            getCurrentData().formatWords = formatWordsButton.isChecked();
        }
        if (debugModeButton.isChecked() != getCurrentData().debugMode) {
            CraftPresence.CONFIG.hasChanged = true;
            getCurrentData().debugMode = debugModeButton.isChecked();
        }
        if (verboseModeButton.isChecked() != getCurrentData().verboseMode) {
            CraftPresence.CONFIG.hasChanged = true;
            getCurrentData().verboseMode = verboseModeButton.isChecked();
        }
        if (allowPlaceholderPreviewsButton.isChecked() != getCurrentData().allowPlaceholderPreviews) {
            CraftPresence.CONFIG.hasChanged = true;
            getCurrentData().allowPlaceholderPreviews = allowPlaceholderPreviewsButton.isChecked();
        }
    }

    @Override
    protected Advanced getOriginalData() {
        return INSTANCE;
    }

    @Override
    protected Advanced getCurrentData() {
        return CraftPresence.CONFIG.advancedSettings;
    }

    @Override
    protected void setCurrentData(Advanced data) {
        CraftPresence.CONFIG.advancedSettings = data;
    }
}
