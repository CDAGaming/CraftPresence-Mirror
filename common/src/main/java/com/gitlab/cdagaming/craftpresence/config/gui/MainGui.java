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
import com.gitlab.cdagaming.craftpresence.utils.KeyUtils;
import com.gitlab.cdagaming.craftpresence.utils.StringUtils;
import com.gitlab.cdagaming.craftpresence.utils.gui.controls.ExtendedButtonControl;
import com.gitlab.cdagaming.craftpresence.utils.gui.impl.CommandsGui;
import com.gitlab.cdagaming.craftpresence.utils.gui.impl.ControlsGui;
import com.gitlab.cdagaming.craftpresence.utils.gui.integrations.ExtendedScreen;
import net.minecraft.client.gui.GuiScreen;
import org.lwjgl.input.Keyboard;

import java.util.List;

@SuppressWarnings("DuplicatedCode")
public class MainGui extends ExtendedScreen {
    private ExtendedButtonControl biomeSet, dimensionSet, serverSet, controlsButton, proceedButton, commandGUIButton;

    public MainGui(GuiScreen parentScreen) {
        super(parentScreen);
    }

    @Override
    public void initializeUi() {
        CraftPresence.GUIS.configGUIOpened = true;

        final int calc1 = (getScreenWidth() / 2) - 183;
        final int calc2 = (getScreenWidth() / 2) + 3;

        // Added General Settings Button
        addControl(
                new ExtendedButtonControl(
                        calc1, CraftPresence.GUIS.getButtonY(1),
                        180, 20,
                        "gui.config.title.general",
                        () -> CraftPresence.GUIS.openScreen(new GeneralSettingsGui(currentScreen)),
                        () -> CraftPresence.GUIS.drawMultiLineString(
                                StringUtils.splitTextByNewLine(
                                        ModUtils.TRANSLATOR.translate("gui.config.comment.title.general")
                                ), this, true
                        )
                )
        );
        biomeSet = addControl(
                new ExtendedButtonControl(
                        calc2, CraftPresence.GUIS.getButtonY(1),
                        180, 20,
                        "gui.config.title.biome_messages",
                        () -> CraftPresence.GUIS.openScreen(new BiomeSettingsGui(currentScreen)),
                        () -> {
                            if (!biomeSet.isControlEnabled()) {
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
                                                        CraftPresence.CLIENT.generateArgumentMessage("biome."))
                                        ), this, true
                                );
                            }
                        }
                )
        );
        dimensionSet = addControl(
                new ExtendedButtonControl(
                        calc1, CraftPresence.GUIS.getButtonY(2),
                        180, 20,
                        "gui.config.title.dimension_messages",
                        () -> CraftPresence.GUIS.openScreen(new DimensionSettingsGui(currentScreen)),
                        () -> {
                            if (!dimensionSet.isControlEnabled()) {
                                CraftPresence.GUIS.drawMultiLineString(
                                        StringUtils.splitTextByNewLine(
                                                ModUtils.TRANSLATOR.translate("gui.config.message.hover.access",
                                                        ModUtils.TRANSLATOR.translate("gui.config.name.general.detect_dimension_data"))
                                        ), this, true
                                );
                            } else {
                                CraftPresence.GUIS.drawMultiLineString(
                                        StringUtils.splitTextByNewLine(
                                                ModUtils.TRANSLATOR.translate("gui.config.comment.dimension_messages.dimension_messages",
                                                        CraftPresence.CLIENT.generateArgumentMessage("dimension."))
                                        ), this, true
                                );
                            }
                        }
                )
        );
        serverSet = addControl(
                new ExtendedButtonControl(
                        calc2, CraftPresence.GUIS.getButtonY(2),
                        180, 20,
                        "gui.config.title.server_messages",
                        () -> CraftPresence.GUIS.openScreen(new ServerSettingsGui(currentScreen)),
                        () -> {
                            if (!serverSet.isControlEnabled()) {
                                CraftPresence.GUIS.drawMultiLineString(
                                        StringUtils.splitTextByNewLine(
                                                ModUtils.TRANSLATOR.translate("gui.config.message.hover.access",
                                                        ModUtils.TRANSLATOR.translate("gui.config.name.general.detect_world_data"))
                                        ), this, true
                                );
                            } else {
                                CraftPresence.GUIS.drawMultiLineString(
                                        StringUtils.splitTextByNewLine(
                                                ModUtils.TRANSLATOR.translate("gui.config.comment.server_messages.server_messages",
                                                        CraftPresence.CLIENT.generateArgumentMessage("server.", "world.", "player."))
                                        ), this, true
                                );
                            }
                        }
                )
        );
        // Added Status Settings Button
        addControl(
                new ExtendedButtonControl(
                        calc1, CraftPresence.GUIS.getButtonY(3),
                        180, 20,
                        "gui.config.title.status_messages",
                        () -> CraftPresence.GUIS.openScreen(new StatusMessagesGui(currentScreen)),
                        () -> CraftPresence.GUIS.drawMultiLineString(
                                StringUtils.splitTextByNewLine(
                                        ModUtils.TRANSLATOR.translate("gui.config.comment.title.status_messages")
                                ), this, true
                        )
                )
        );
        // Added Advanced Settings Button
        addControl(
                new ExtendedButtonControl(
                        calc2, CraftPresence.GUIS.getButtonY(3),
                        180, 20,
                        "gui.config.title.advanced",
                        () -> CraftPresence.GUIS.openScreen(new AdvancedSettingsGui(currentScreen)),
                        () -> CraftPresence.GUIS.drawMultiLineString(
                                StringUtils.splitTextByNewLine(
                                        ModUtils.TRANSLATOR.translate("gui.config.comment.title.advanced")
                                ), this, true
                        )
                )
        );
        // Added Accessibility Settings Button
        addControl(
                new ExtendedButtonControl(
                        calc1, CraftPresence.GUIS.getButtonY(4),
                        180, 20,
                        "gui.config.title.accessibility",
                        () -> CraftPresence.GUIS.openScreen(new AccessibilitySettingsGui(currentScreen)),
                        () -> CraftPresence.GUIS.drawMultiLineString(
                                StringUtils.splitTextByNewLine(
                                        ModUtils.TRANSLATOR.translate("gui.config.comment.title.accessibility")
                                ), this, true
                        )
                )
        );
        // Added Presence Settings Button
        addControl(
                new ExtendedButtonControl(
                        calc2, CraftPresence.GUIS.getButtonY(4),
                        180, 20,
                        "gui.config.title.presence_settings",
                        () -> CraftPresence.GUIS.openScreen(new PresenceSettingsGui(currentScreen)),
                        () -> CraftPresence.GUIS.drawMultiLineString(
                                StringUtils.splitTextByNewLine(
                                        ModUtils.TRANSLATOR.translate("gui.config.comment.presence_settings")
                                ), this, true
                        )
                )
        );
        // Adding Controls Button
        final List<String> controlInfo = StringUtils.newArrayList("key.craftpresence.category");
        KeyUtils.FilterMode controlMode = KeyUtils.FilterMode.Category;
        if (ModUtils.IS_LEGACY_SOFT) {
            controlInfo.clear();
            StringUtils.addEntriesNotPresent(controlInfo, CraftPresence.KEYBINDINGS.getRawKeyMappings().keySet());

            controlMode = KeyUtils.FilterMode.Name;
        }

        final KeyUtils.FilterMode finalControlMode = controlMode;
        controlsButton = addControl(
                new ExtendedButtonControl(
                        (getScreenWidth() / 2) - 90, (getScreenHeight() - 55),
                        180, 20,
                        "gui.config.message.button.controls",
                        () -> CraftPresence.GUIS.openScreen(
                                new ControlsGui(
                                        currentScreen, finalControlMode,
                                        controlInfo
                                )
                        )
                )
        );
        proceedButton = addControl(
                new ExtendedButtonControl(
                        (getScreenWidth() / 2) - 90, (getScreenHeight() - 30),
                        180, 20,
                        "gui.config.message.button.back",
                        () -> {
                            if (CraftPresence.CONFIG.hasChanged) {
                                CraftPresence.CONFIG.save();
                                ModUtils.LOG.info(ModUtils.TRANSLATOR.translate(true, "craftpresence.logger.info.config.save"));
                            }
                            CraftPresence.GUIS.configGUIOpened = false;
                            CraftPresence.GUIS.openScreen(parentScreen);
                        }
                )
        );
        // Added About Button
        addControl(
                new ExtendedButtonControl(
                        (getScreenWidth() - 105), (getScreenHeight() - 55),
                        95, 20,
                        "gui.config.message.button.about",
                        () -> CraftPresence.GUIS.openScreen(new AboutGui(currentScreen))
                )
        );
        commandGUIButton = addControl(
                new ExtendedButtonControl(
                        (getScreenWidth() - 105), (getScreenHeight() - 30),
                        95, 20,
                        "gui.config.message.button.commands",
                        () -> CraftPresence.GUIS.openScreen(new CommandsGui(currentScreen))
                )
        );
        // Added Reset Config Button
        addControl(
                new ExtendedButtonControl(
                        10, (getScreenHeight() - 30),
                        95, 20,
                        "gui.config.message.button.reset",
                        () -> CraftPresence.CONFIG = Config.loadOrCreate(true)
                )
        );
        // Added Sync Config Button
        addControl(
                new ExtendedButtonControl(
                        10, (getScreenHeight() - 55),
                        95, 20,
                        "gui.config.message.button.sync.config",
                        () -> {
                            final String configData = CraftPresence.CONFIG.toString();
                            CraftPresence.CONFIG = Config.loadOrCreate();

                            // Only Mark to Save if there have been Changes in the File
                            if (!CraftPresence.CONFIG.toString().equals(configData)) {
                                CraftPresence.CONFIG.hasChanged = true;
                                CraftPresence.CONFIG.needsReboot = true;

                                syncRenderStates();
                            }
                        },
                        () -> CraftPresence.GUIS.drawMultiLineString(
                                StringUtils.splitTextByNewLine(
                                        ModUtils.TRANSLATOR.translate("gui.config.comment.button.sync.config")
                                ), this, true
                        )
                )
        );

        super.initializeUi();
    }

    @Override
    public void preRender() {
        final String mainTitle = ModUtils.TRANSLATOR.translate("gui.config.title");
        final String releaseNotice = ModUtils.TRANSLATOR.translate("gui.config.message.tentative", ModUtils.VERSION_ID);

        renderString(mainTitle, (getScreenWidth() / 2f) - (getStringWidth(mainTitle) / 2f), 15, 0xFFFFFF);

        if (ModUtils.VERSION_TYPE.equalsIgnoreCase("alpha")) {
            renderString(releaseNotice, (getScreenWidth() / 2f) - (getStringWidth(releaseNotice) / 2f), getScreenHeight() - 85, 0xFFFFFF);
        }

        syncRenderStates();

        super.preRender();
    }

    @Override
    public void postRender() {
        final String mainTitle = ModUtils.TRANSLATOR.translate("gui.config.title");

        // Hovering over Title Label
        if (CraftPresence.GUIS.isMouseOver(getMouseX(), getMouseY(), (getScreenWidth() / 2f) - (getStringWidth(mainTitle) / 2f), 15, getStringWidth(mainTitle), getFontHeight())) {
            CraftPresence.GUIS.drawMultiLineString(
                    StringUtils.splitTextByNewLine(
                            ModUtils.TRANSLATOR.translate("gui.config.comment.title", ModUtils.VERSION_ID, CraftPresence.CONFIG._schemaVersion)
                    ), this, true
            );
        }

        super.postRender();
    }

    @Override
    protected void keyTyped(char typedChar, int keyCode) {
        if (keyCode == Keyboard.KEY_ESCAPE) {
            if (CraftPresence.CONFIG.hasChanged) {
                CraftPresence.CONFIG = Config.loadOrCreate();
            }
            CraftPresence.GUIS.configGUIOpened = false;
        }
        super.keyTyped(typedChar, keyCode);
    }

    private void syncRenderStates() {
        biomeSet.setControlEnabled(CraftPresence.BIOMES.enabled);
        dimensionSet.setControlEnabled(CraftPresence.DIMENSIONS.enabled);
        serverSet.setControlEnabled(CraftPresence.SERVER.enabled);
        commandGUIButton.setControlEnabled(CraftPresence.CONFIG.advancedSettings.enableCommands);
        controlsButton.setControlEnabled(CraftPresence.KEYBINDINGS.areKeysRegistered());

        proceedButton.setControlMessage(CraftPresence.CONFIG.hasChanged ? "gui.config.message.button.save" : "gui.config.message.button.back");
    }
}
