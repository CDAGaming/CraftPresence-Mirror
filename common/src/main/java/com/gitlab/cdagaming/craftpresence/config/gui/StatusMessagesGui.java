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
import com.gitlab.cdagaming.craftpresence.config.category.Status;
import com.gitlab.cdagaming.craftpresence.core.Constants;
import com.gitlab.cdagaming.craftpresence.core.config.element.ModuleData;
import com.gitlab.cdagaming.craftpresence.core.config.element.PresenceData;
import com.gitlab.cdagaming.craftpresence.utils.discord.assets.DiscordAssetUtils;
import com.gitlab.cdagaming.craftpresence.utils.gui.controls.ExtendedButtonControl;
import com.gitlab.cdagaming.craftpresence.utils.gui.controls.ScrollableListControl;
import com.gitlab.cdagaming.craftpresence.utils.gui.impl.ConfigurationGui;
import com.gitlab.cdagaming.craftpresence.utils.gui.impl.DynamicEditorGui;
import com.gitlab.cdagaming.craftpresence.utils.gui.impl.SelectorGui;
import io.github.cdagaming.unicore.impl.HashMapBuilder;
import io.github.cdagaming.unicore.impl.Pair;
import io.github.cdagaming.unicore.utils.StringUtils;
import net.minecraft.client.gui.GuiScreen;

import java.util.Map;

@SuppressWarnings("DuplicatedCode")
public class StatusMessagesGui extends ConfigurationGui<Status> {
    private final Status INSTANCE;
    // nameTranslation, [configPath,commentTranslation]
    private final Map<String, Pair<String, Runnable>> eventMappings = new HashMapBuilder<String, Pair<String, Runnable>>()
            .put("gui.config.name.status_messages.main_menu_message", new Pair<>(
                    "mainMenuData", () -> drawMultiLineString(
                    StringUtils.splitTextByNewLine(
                            Constants.TRANSLATOR.translate("gui.config.comment.status_messages.main_menu_message",
                                    CraftPresence.CLIENT.generateArgumentMessage("general.", "custom."))
                    )
            )
            ))
            .put("gui.config.name.status_messages.loading_message", new Pair<>(
                    "loadingData", () -> drawMultiLineString(
                    StringUtils.splitTextByNewLine(
                            Constants.TRANSLATOR.translate("gui.config.comment.status_messages.loading_message",
                                    CraftPresence.CLIENT.generateArgumentMessage("general.", "custom."))
                    )
            )
            ))
            .put("gui.config.name.status_messages.lan_message", new Pair<>(
                    "lanData", () -> drawMultiLineString(
                    StringUtils.splitTextByNewLine(
                            Constants.TRANSLATOR.translate("gui.config.comment.status_messages.lan_message",
                                    CraftPresence.CLIENT.generateArgumentMessage("server.", "world.", "player."))
                    )
            )
            ))
            .put("gui.config.name.status_messages.single_player_message", new Pair<>(
                    "singleplayerData", () -> drawMultiLineString(
                    StringUtils.splitTextByNewLine(
                            Constants.TRANSLATOR.translate("gui.config.comment.status_messages.single_player_message",
                                    CraftPresence.CLIENT.generateArgumentMessage("server.", "world.", "player."))
                    )
            )
            ))
            .build();

    StatusMessagesGui(GuiScreen parentScreen) {
        super(parentScreen, "gui.config.title", "gui.config.title.status_messages");
        INSTANCE = getCurrentData().copy();
    }

    @Override
    protected void appendControls() {
        super.appendControls();
        // Page 1 Items
        final int calc1 = (getScreenWidth() / 2) - 183;
        final int calc2 = (getScreenWidth() / 2) + 3;
        final int midCalc = (getScreenWidth() / 2) - 90;

        int buttonRow = 0, index = 1;
        for (Map.Entry<String, Pair<String, Runnable>> entry : eventMappings.entrySet()) {
            final boolean isEven = (index % 2 == 0);
            int startX = isEven ? calc2 : calc1;
            if (index >= eventMappings.size() && startX == calc1) {
                startX = midCalc;
            }

            childFrame.addControl(
                    new ExtendedButtonControl(
                            startX, getButtonY(buttonRow),
                            180, 20,
                            entry.getKey(),
                            () -> openScreen(
                                    new DynamicEditorGui(
                                            currentScreen, entry.getValue().getFirst(),
                                            (attributeName, screenInstance) -> {
                                                // Event to occur when initializing new data
                                                screenInstance.defaultData = (ModuleData) getCurrentData().getDefaults().getProperty(attributeName);
                                                screenInstance.primaryMessage = screenInstance.originalPrimaryMessage = Config.getProperty(screenInstance.defaultData, "textOverride") != null ? screenInstance.defaultData.getTextOverride() : "";
                                                screenInstance.resetText = "gui.config.message.button.reset_to_default";
                                            },
                                            (attributeName, screenInstance) -> {
                                                // Event to occur when initializing existing data
                                                screenInstance.defaultData = (ModuleData) getCurrentData().getDefaults().getProperty(attributeName);
                                                screenInstance.currentData = (ModuleData) getCurrentData().getProperty(attributeName);
                                                screenInstance.isPreliminaryData = screenInstance.currentData == null;
                                                screenInstance.mainTitle = Constants.TRANSLATOR.translate("gui.config.title.gui.edit_specific_gui", attributeName);
                                                screenInstance.originalPrimaryMessage = Config.getProperty(screenInstance.defaultData, "textOverride") != null ? screenInstance.defaultData.getTextOverride() : "";
                                                screenInstance.primaryMessage = Config.getProperty(screenInstance.currentData, "textOverride") != null ? screenInstance.currentData.getTextOverride() : screenInstance.originalPrimaryMessage;
                                                screenInstance.resetText = "gui.config.message.button.reset_to_default";
                                            },
                                            (screenInstance, attributeName, inputText) -> {
                                                // Event to occur when adjusting set data
                                                screenInstance.currentData.setTextOverride(inputText);
                                                CraftPresence.CONFIG.hasChanged = true;
                                                getCurrentData().setProperty(attributeName, screenInstance.currentData);
                                                if (!CraftPresence.GUIS.GUI_NAMES.contains(attributeName)) {
                                                    CraftPresence.GUIS.GUI_NAMES.add(attributeName);
                                                }
                                            },
                                            (screenInstance, attributeName, inputText) -> {
                                                // Event to occur when removing set data
                                                CraftPresence.CONFIG.hasChanged = true;
                                                getCurrentData().resetProperty(attributeName);
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
                                                    final String defaultIcon = Config.getProperty(screenInstance.defaultData, "iconOverride") != null ? screenInstance.defaultData.getIconOverride() : CraftPresence.CONFIG.generalSettings.defaultIcon;
                                                    final String specificIcon = Config.getProperty(screenInstance.currentData, "iconOverride") != null ? screenInstance.currentData.getIconOverride() : defaultIcon;
                                                    openScreen(
                                                            new SelectorGui(
                                                                    screenInstance,
                                                                    Constants.TRANSLATOR.translate("gui.config.title.selector.icon"), DiscordAssetUtils.ASSET_LIST.keySet(),
                                                                    specificIcon, attributeName,
                                                                    true, false, ScrollableListControl.RenderType.DiscordAsset,
                                                                    (innerAttributeName, innerCurrentValue) -> {
                                                                        // Inner-Event to occur when proceeding with adjusted data
                                                                        screenInstance.currentData.setIconOverride(innerCurrentValue);
                                                                    }, null
                                                            )
                                                    );
                                                }
                                            }
                                    )
                            ),
                            entry.getValue().getSecond()
                    )
            );

            if (isEven) {
                buttonRow++;
            }
            index++;
        }
    }

    @Override
    protected Status getInstanceData() {
        return INSTANCE;
    }

    @Override
    protected Status getCurrentData() {
        return CraftPresence.CONFIG.statusMessages;
    }

    @Override
    protected boolean setCurrentData(Status data) {
        if (!getCurrentData().equals(data)) {
            getCurrentData().transferFrom(data);
            CraftPresence.CONFIG.hasChanged = true;
            return true;
        }
        return false;
    }
}
