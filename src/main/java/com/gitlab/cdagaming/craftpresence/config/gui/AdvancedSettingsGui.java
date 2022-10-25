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
import com.gitlab.cdagaming.craftpresence.utils.SystemUtils;
import com.gitlab.cdagaming.craftpresence.utils.discord.DiscordUtils;
import com.gitlab.cdagaming.craftpresence.utils.gui.controls.CheckBoxControl;
import com.gitlab.cdagaming.craftpresence.utils.gui.controls.ExtendedButtonControl;
import com.gitlab.cdagaming.craftpresence.utils.gui.controls.ExtendedTextControl;
import com.gitlab.cdagaming.craftpresence.utils.gui.controls.ScrollableListControl.RenderType;
import com.gitlab.cdagaming.craftpresence.utils.gui.impl.DynamicEditorGui;
import com.gitlab.cdagaming.craftpresence.utils.gui.impl.SelectorGui;
import com.gitlab.cdagaming.craftpresence.utils.gui.integrations.ExtendedScreen;
import net.minecraft.client.gui.GuiScreen;

public class AdvancedSettingsGui extends ExtendedScreen {
    private ExtendedButtonControl proceedButton, guiMessagesButton, itemMessagesButton, entityTargetMessagesButton, entityRidingMessagesButton;
    private CheckBoxControl enableCommandsButton, enablePerGuiButton, enablePerItemButton, enablePerEntityButton,
            renderTooltipsButton, formatWordsButton, debugModeButton, verboseModeButton,
            allowPlaceholderPreviewsButton, allowPlaceholderOperatorsButton;
    private ExtendedTextControl splitCharacter, refreshRate;

    AdvancedSettingsGui(GuiScreen parentScreen) {
        super(parentScreen);
    }

    @Override
    public void initializeUi() {
//        splitCharacter = addControl(
//                new ExtendedTextControl(
//                        getFontRenderer(),
//                        (getScreenWidth() / 2) - 60, CraftPresence.GUIS.getButtonY(1),
//                        45, 20
//                )
//        );
//        splitCharacter.setControlMessage(CraftPresence.CONFIG.splitCharacter);
//        splitCharacter.setControlMaxLength(1);

        refreshRate = addControl(
                new ExtendedTextControl(
                        getFontRenderer(),
                        (getScreenWidth() / 2) + 103, CraftPresence.GUIS.getButtonY(1),
                        45, 20
                )
        );
        refreshRate.setControlMessage(Integer.toString(CraftPresence.CONFIG.refreshRate));
        refreshRate.setControlMaxLength(3);

        final int calc1 = (getScreenWidth() / 2) - 160;
        final int calc2 = (getScreenWidth() / 2) + 3;

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
                                                                screenInstance.primaryMessage = screenInstance.originalPrimaryMessage = CraftPresence.CONFIG.guiMessages.getOrDefault("default", "");
                                                            },
                                                            (attributeName, screenInstance) -> {
                                                                // Event to occur when initializing existing data
                                                                screenInstance.mainTitle = ModUtils.TRANSLATOR.translate("gui.config.title.gui.edit_specific_gui", attributeName);
                                                                screenInstance.originalPrimaryMessage = CraftPresence.CONFIG.guiMessages.getOrDefault("default", "");
                                                                screenInstance.primaryMessage = CraftPresence.CONFIG.guiMessages.getOrDefault(attributeName, screenInstance.originalPrimaryMessage);
                                                            },
                                                            (screenInstance, attributeName, inputText) -> {
                                                                // Event to occur when adjusting set data
                                                                CraftPresence.CONFIG.hasChanged = true;
                                                                CraftPresence.CONFIG.guiMessages.put(attributeName, inputText);
                                                                if (!CraftPresence.GUIS.GUI_NAMES.contains(attributeName)) {
                                                                    CraftPresence.GUIS.GUI_NAMES.add(attributeName);
                                                                }
                                                            },
                                                            (screenInstance, attributeName, inputText) -> {
                                                                // Event to occur when removing set data
                                                                CraftPresence.CONFIG.hasChanged = true;
                                                                CraftPresence.CONFIG.guiMessages.remove(attributeName);
                                                                CraftPresence.GUIS.GUI_NAMES.remove(attributeName);
                                                            }, null,
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
                                                                screenInstance.primaryMessage = screenInstance.originalPrimaryMessage = CraftPresence.CONFIG.itemMessages.getOrDefault("default", "");
                                                            },
                                                            (attributeName, screenInstance) -> {
                                                                // Event to occur when initializing existing data
                                                                screenInstance.mainTitle = ModUtils.TRANSLATOR.translate("gui.config.title.item.edit_specific_item", attributeName);
                                                                screenInstance.originalPrimaryMessage = CraftPresence.CONFIG.itemMessages.getOrDefault("default", "");
                                                                screenInstance.primaryMessage = CraftPresence.CONFIG.itemMessages.getOrDefault(attributeName, screenInstance.originalPrimaryMessage);
                                                            },
                                                            (screenInstance, attributeName, inputText) -> {
                                                                // Event to occur when adjusting set data
                                                                CraftPresence.CONFIG.hasChanged = true;
                                                                CraftPresence.CONFIG.itemMessages.put(attributeName, inputText);
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
                                                                CraftPresence.CONFIG.itemMessages.remove(attributeName);
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
                                                                screenInstance.primaryMessage = screenInstance.originalPrimaryMessage = CraftPresence.CONFIG.entityTargetMessages.getOrDefault("default", "");
                                                            },
                                                            (attributeName, screenInstance) -> {
                                                                // Event to occur when initializing existing data
                                                                screenInstance.mainTitle = ModUtils.TRANSLATOR.translate("gui.config.title.entity.edit_specific_entity", attributeName);
                                                                screenInstance.originalPrimaryMessage = CraftPresence.CONFIG.entityTargetMessages.getOrDefault("default", "");
                                                                screenInstance.primaryMessage = CraftPresence.CONFIG.entityTargetMessages.getOrDefault(attributeName, screenInstance.originalPrimaryMessage);
                                                            },
                                                            (screenInstance, attributeName, inputText) -> {
                                                                // Event to occur when adjusting set data
                                                                CraftPresence.CONFIG.hasChanged = true;
                                                                CraftPresence.CONFIG.entityTargetMessages.put(attributeName, inputText);
                                                                if (!CraftPresence.ENTITIES.ENTITY_NAMES.contains(attributeName)) {
                                                                    CraftPresence.ENTITIES.ENTITY_NAMES.add(attributeName);
                                                                }
                                                            },
                                                            (screenInstance, attributeName, inputText) -> {
                                                                // Event to occur when removing set data
                                                                CraftPresence.CONFIG.hasChanged = true;
                                                                CraftPresence.CONFIG.entityTargetMessages.remove(attributeName);
                                                                CraftPresence.ENTITIES.ENTITY_NAMES.remove(attributeName);
                                                            }, null,
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
                                                                screenInstance.primaryMessage = screenInstance.originalPrimaryMessage = CraftPresence.CONFIG.entityRidingMessages.getOrDefault("default", "");
                                                            },
                                                            (attributeName, screenInstance) -> {
                                                                // Event to occur when initializing existing data
                                                                screenInstance.mainTitle = ModUtils.TRANSLATOR.translate("gui.config.title.entity.edit_specific_entity", attributeName);
                                                                screenInstance.originalPrimaryMessage = CraftPresence.CONFIG.entityRidingMessages.getOrDefault("default", "");
                                                                screenInstance.primaryMessage = CraftPresence.CONFIG.entityRidingMessages.getOrDefault(attributeName, screenInstance.originalPrimaryMessage);
                                                            },
                                                            (screenInstance, attributeName, inputText) -> {
                                                                // Event to occur when adjusting set data
                                                                CraftPresence.CONFIG.hasChanged = true;
                                                                CraftPresence.CONFIG.entityRidingMessages.put(attributeName, inputText);
                                                                if (!CraftPresence.ENTITIES.ENTITY_NAMES.contains(attributeName)) {
                                                                    CraftPresence.ENTITIES.ENTITY_NAMES.add(attributeName);
                                                                }
                                                            },
                                                            (screenInstance, attributeName, inputText) -> {
                                                                // Event to occur when removing set data
                                                                CraftPresence.CONFIG.hasChanged = true;
                                                                CraftPresence.CONFIG.entityRidingMessages.remove(attributeName);
                                                                CraftPresence.ENTITIES.ENTITY_NAMES.remove(attributeName);
                                                            }, null,
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
                        CraftPresence.CONFIG.enableCommands,
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
                        CraftPresence.CONFIG.enablePerGui,
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
                        CraftPresence.CONFIG.enablePerItem,
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
                        CraftPresence.CONFIG.enablePerEntity,
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
                        CraftPresence.CONFIG.renderTooltips,
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
                        CraftPresence.CONFIG.formatWords,
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
                        CraftPresence.CONFIG.debugMode,
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
                        CraftPresence.CONFIG.verboseMode,
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
                        CraftPresence.CONFIG.allowPlaceholderPreviews,
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
                        CraftPresence.CONFIG.allowPlaceholderOperators,
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
                            if (!refreshRate.getControlMessage().equals(Integer.toString(CraftPresence.CONFIG.refreshRate))) {
                                CraftPresence.CONFIG.hasChanged = true;
                                CraftPresence.CONFIG.hasClientPropertiesChanged = true;
                                CraftPresence.CONFIG.refreshRate = StringUtils.getValidInteger(refreshRate.getControlMessage()).getSecond();
                            }
                            if (enableCommandsButton.isChecked() != CraftPresence.CONFIG.enableCommands) {
                                CraftPresence.CONFIG.hasChanged = true;
                                CraftPresence.CONFIG.enableCommands = enableCommandsButton.isChecked();
                            }
                            if (enablePerGuiButton.isChecked() != CraftPresence.CONFIG.enablePerGui) {
                                CraftPresence.CONFIG.hasChanged = true;
                                CraftPresence.CONFIG.hasClientPropertiesChanged = true;
                                CraftPresence.CONFIG.enablePerGui = enablePerGuiButton.isChecked();
                            }
                            if (enablePerItemButton.isChecked() != CraftPresence.CONFIG.enablePerItem) {
                                CraftPresence.CONFIG.hasChanged = true;
                                CraftPresence.CONFIG.hasClientPropertiesChanged = true;
                                CraftPresence.CONFIG.enablePerItem = enablePerItemButton.isChecked();
                            }
                            if (enablePerEntityButton.isChecked() != CraftPresence.CONFIG.enablePerEntity) {
                                CraftPresence.CONFIG.hasChanged = true;
                                CraftPresence.CONFIG.hasClientPropertiesChanged = true;
                                CraftPresence.CONFIG.enablePerEntity = enablePerEntityButton.isChecked();
                            }
                            if (renderTooltipsButton.isChecked() != CraftPresence.CONFIG.renderTooltips) {
                                CraftPresence.CONFIG.hasChanged = true;
                                CraftPresence.CONFIG.renderTooltips = renderTooltipsButton.isChecked();
                            }
                            if (formatWordsButton.isChecked() != CraftPresence.CONFIG.formatWords) {
                                CraftPresence.CONFIG.hasChanged = true;
                                CraftPresence.CONFIG.formatWords = formatWordsButton.isChecked();
                            }
                            if (debugModeButton.isChecked() != CraftPresence.CONFIG.debugMode) {
                                CraftPresence.CONFIG.hasChanged = true;
                                CraftPresence.CONFIG.hasClientPropertiesChanged = true;
                                CraftPresence.CONFIG.debugMode = debugModeButton.isChecked();
                            }
                            if (verboseModeButton.isChecked() != CraftPresence.CONFIG.verboseMode) {
                                CraftPresence.CONFIG.hasChanged = true;
                                CraftPresence.CONFIG.hasClientPropertiesChanged = true;
                                CraftPresence.CONFIG.verboseMode = verboseModeButton.isChecked();
                            }
                            if (allowPlaceholderPreviewsButton.isChecked() != CraftPresence.CONFIG.allowPlaceholderPreviews) {
                                CraftPresence.CONFIG.hasChanged = true;
                                CraftPresence.CONFIG.allowPlaceholderPreviews = allowPlaceholderPreviewsButton.isChecked();
                            }
                            if (allowPlaceholderOperatorsButton.isChecked() != CraftPresence.CONFIG.allowPlaceholderOperators) {
                                CraftPresence.CONFIG.hasChanged = true;
                                CraftPresence.CONFIG.hasClientPropertiesChanged = true;
                                CraftPresence.CONFIG.allowPlaceholderOperators = allowPlaceholderOperatorsButton.isChecked();
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
        final String splitCharacterText = ModUtils.TRANSLATOR.translate("gui.config.name.advanced.split_character");
        final String refreshRateText = ModUtils.TRANSLATOR.translate("gui.config.name.advanced.refresh_rate");

        renderString(mainTitle, (getScreenWidth() / 2f) - (getStringWidth(mainTitle) / 2f), 10, 0xFFFFFF);
        renderString(subTitle, (getScreenWidth() / 2f) - (getStringWidth(subTitle) / 2f), 20, 0xFFFFFF);
        renderString(splitCharacterText, (getScreenWidth() / 2f) - 145, CraftPresence.GUIS.getButtonY(1, 5), 0xFFFFFF);
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
        final String splitCharacterText = ModUtils.TRANSLATOR.translate("gui.config.name.advanced.split_character");
        final String refreshRateText = ModUtils.TRANSLATOR.translate("gui.config.name.advanced.refresh_rate");
        // Hovering over Split Character Message Label
        if (CraftPresence.GUIS.isMouseOver(getMouseX(), getMouseY(), (getScreenWidth() / 2f) - 145, CraftPresence.GUIS.getButtonY(1, 5), getStringWidth(splitCharacterText), getFontHeight())) {
            CraftPresence.GUIS.drawMultiLineString(
                    StringUtils.splitTextByNewLine(
                            ModUtils.TRANSLATOR.translate("gui.config.comment.advanced.split_character")
                    ), this, true
            );
        }

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
