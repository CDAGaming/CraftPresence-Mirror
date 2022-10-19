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
import com.gitlab.cdagaming.craftpresence.utils.CommandUtils;
import com.gitlab.cdagaming.craftpresence.utils.KeyUtils;
import com.gitlab.cdagaming.craftpresence.utils.StringUtils;
import com.gitlab.cdagaming.craftpresence.utils.commands.CommandsGui;
import com.gitlab.cdagaming.craftpresence.utils.gui.controls.ExtendedButtonControl;
import com.gitlab.cdagaming.craftpresence.utils.gui.impl.ControlsGui;
import com.gitlab.cdagaming.craftpresence.utils.gui.integrations.ExtendedScreen;
import com.google.common.collect.Lists;
import net.minecraft.client.gui.GuiScreen;
import org.lwjgl.input.Keyboard;

import java.util.List;

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
                        new Runnable() {
                            @Override
                            public void run() {
                                CraftPresence.GUIS.openScreen(new GeneralSettingsGui(currentScreen));
                            }
                        },
                        new Runnable() {
                            @Override
                            public void run() {
                                CraftPresence.GUIS.drawMultiLineString(
                                        StringUtils.splitTextByNewLine(
                                                ModUtils.TRANSLATOR.translate("gui.config.comment.title.general")
                                        ), MainGui.this, true
                                );
                            }
                        }
                )
        );
        biomeSet = addControl(
                new ExtendedButtonControl(
                        calc2, CraftPresence.GUIS.getButtonY(1),
                        180, 20,
                        "gui.config.title.biome_messages",
                        new Runnable() {
                            @Override
                            public void run() {
                                CraftPresence.GUIS.openScreen(new BiomeSettingsGui(currentScreen));
                            }
                        },
                        new Runnable() {
                            @Override
                            public void run() {
                                if (!biomeSet.isControlEnabled()) {
                                    CraftPresence.GUIS.drawMultiLineString(
                                            StringUtils.splitTextByNewLine(
                                                    ModUtils.TRANSLATOR.translate("gui.config.message.hover.access",
                                                            ModUtils.TRANSLATOR.translate("gui.config.name.general.detect_biome_data"))
                                            ), MainGui.this, true
                                    );
                                } else {
                                    CraftPresence.GUIS.drawMultiLineString(
                                            StringUtils.splitTextByNewLine(
                                                    ModUtils.TRANSLATOR.translate("gui.config.comment.biome_messages.biome_messages",
                                                            CraftPresence.BIOMES.generateArgumentMessage())
                                            ), MainGui.this, true
                                    );
                                }
                            }
                        }
                )
        );
        dimensionSet = addControl(
                new ExtendedButtonControl(
                        calc1, CraftPresence.GUIS.getButtonY(2),
                        180, 20,
                        "gui.config.title.dimension_messages",
                        new Runnable() {
                            @Override
                            public void run() {
                                CraftPresence.GUIS.openScreen(new DimensionSettingsGui(currentScreen));
                            }
                        },
                        new Runnable() {
                            @Override
                            public void run() {
                                if (!dimensionSet.isControlEnabled()) {
                                    CraftPresence.GUIS.drawMultiLineString(
                                            StringUtils.splitTextByNewLine(
                                                    ModUtils.TRANSLATOR.translate("gui.config.message.hover.access",
                                                            ModUtils.TRANSLATOR.translate("gui.config.name.general.detect_dimension_data"))
                                            ), MainGui.this, true
                                    );
                                } else {
                                    CraftPresence.GUIS.drawMultiLineString(
                                            StringUtils.splitTextByNewLine(
                                                    ModUtils.TRANSLATOR.translate("gui.config.comment.dimension_messages.dimension_messages",
                                                            CraftPresence.DIMENSIONS.generateArgumentMessage())
                                            ), MainGui.this, true
                                    );
                                }
                            }
                        }
                )
        );
        serverSet = addControl(
                new ExtendedButtonControl(
                        calc2, CraftPresence.GUIS.getButtonY(2),
                        180, 20,
                        "gui.config.title.server_messages",
                        new Runnable() {
                            @Override
                            public void run() {
                                CraftPresence.GUIS.openScreen(new ServerSettingsGui(currentScreen));
                            }
                        },
                        new Runnable() {
                            @Override
                            public void run() {
                                if (!serverSet.isControlEnabled()) {
                                    CraftPresence.GUIS.drawMultiLineString(
                                            StringUtils.splitTextByNewLine(
                                                    ModUtils.TRANSLATOR.translate("gui.config.message.hover.access",
                                                            ModUtils.TRANSLATOR.translate("gui.config.name.general.detect_world_data"))
                                            ), MainGui.this, true
                                    );
                                } else {
                                    CraftPresence.GUIS.drawMultiLineString(
                                            StringUtils.splitTextByNewLine(
                                                    ModUtils.TRANSLATOR.translate("gui.config.comment.server_messages.server_messages",
                                                            CraftPresence.SERVER.generateArgumentMessage())
                                            ), MainGui.this, true
                                    );
                                }
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
                        new Runnable() {
                            @Override
                            public void run() {
                                CraftPresence.GUIS.openScreen(new StatusMessagesGui(currentScreen));
                            }
                        },
                        new Runnable() {
                            @Override
                            public void run() {
                                CraftPresence.GUIS.drawMultiLineString(
                                        StringUtils.splitTextByNewLine(
                                                ModUtils.TRANSLATOR.translate("gui.config.comment.title.status_messages")
                                        ), MainGui.this, true
                                );
                            }
                        }
                )
        );
        // Added Advanced Settings Button
        addControl(
                new ExtendedButtonControl(
                        calc2, CraftPresence.GUIS.getButtonY(3),
                        180, 20,
                        "gui.config.title.advanced",
                        new Runnable() {
                            @Override
                            public void run() {
                                CraftPresence.GUIS.openScreen(new AdvancedSettingsGui(currentScreen));
                            }
                        },
                        new Runnable() {
                            @Override
                            public void run() {
                                CraftPresence.GUIS.drawMultiLineString(
                                        StringUtils.splitTextByNewLine(
                                                ModUtils.TRANSLATOR.translate("gui.config.comment.title.advanced")
                                        ), MainGui.this, true
                                );
                            }
                        }
                )
        );
        // Added Accessibility Settings Button
        addControl(
                new ExtendedButtonControl(
                        calc1, CraftPresence.GUIS.getButtonY(4),
                        180, 20,
                        "gui.config.title.accessibility",
                        new Runnable() {
                            @Override
                            public void run() {
                                CraftPresence.GUIS.openScreen(new AccessibilitySettingsGui(currentScreen));
                            }
                        },
                        new Runnable() {
                            @Override
                            public void run() {
                                CraftPresence.GUIS.drawMultiLineString(
                                        StringUtils.splitTextByNewLine(
                                                ModUtils.TRANSLATOR.translate("gui.config.comment.title.accessibility")
                                        ), MainGui.this, true
                                );
                            }
                        }
                )
        );
        // Added Presence Settings Button
        addControl(
                new ExtendedButtonControl(
                        calc2, CraftPresence.GUIS.getButtonY(4),
                        180, 20,
                        "gui.config.title.presence_settings",
                        new Runnable() {
                            @Override
                            public void run() {
                                CraftPresence.GUIS.openScreen(new PresenceSettingsGui(currentScreen));
                            }
                        },
                        new Runnable() {
                            @Override
                            public void run() {
                                CraftPresence.GUIS.drawMultiLineString(
                                        StringUtils.splitTextByNewLine(
                                                ModUtils.TRANSLATOR.translate("gui.config.comment.presence_settings")
                                        ), MainGui.this, true
                                );
                            }
                        }
                )
        );
        // Adding Controls Button
        final List<String> controlInfo = Lists.newArrayList("key.craftpresence.category");
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
                        new Runnable() {
                            @Override
                            public void run() {
                                CraftPresence.GUIS.openScreen(
                                        new ControlsGui(
                                                currentScreen, finalControlMode,
                                                controlInfo
                                        )
                                );
                            }
                        }
                )
        );
        proceedButton = addControl(
                new ExtendedButtonControl(
                        (getScreenWidth() / 2) - 90, (getScreenHeight() - 30),
                        180, 20,
                        "gui.config.message.button.back",
                        new Runnable() {
                            @Override
                            public void run() {
                                if (CraftPresence.CONFIG.hasChanged) {
                                    CraftPresence.CONFIG.updateConfig(false);
                                    CraftPresence.CONFIG.read(false, "UTF-8");
                                    if (CraftPresence.CONFIG.hasClientPropertiesChanged) {
                                        CommandUtils.rebootRPC(CraftPresence.CONFIG.flushClientProperties);
                                        CraftPresence.CONFIG.hasClientPropertiesChanged = false;
                                    }
                                    CommandUtils.reloadData(true);
                                    CraftPresence.CONFIG.flushClientProperties = false;
                                    CraftPresence.CONFIG.hasChanged = false;
                                }

                                CraftPresence.GUIS.configGUIOpened = false;
                                if (mc.thePlayer != null) {
                                    mc.thePlayer.closeScreen();
                                } else {
                                    CraftPresence.GUIS.openScreen(parentScreen);
                                }
                            }
                        }
                )
        );
        // Added About Button
        addControl(
                new ExtendedButtonControl(
                        (getScreenWidth() - 105), (getScreenHeight() - 55),
                        95, 20,
                        "gui.config.message.button.about",
                        new Runnable() {
                            @Override
                            public void run() {
                                CraftPresence.GUIS.openScreen(new AboutGui(currentScreen));
                            }
                        }
                )
        );
        commandGUIButton = addControl(
                new ExtendedButtonControl(
                        (getScreenWidth() - 105), (getScreenHeight() - 30),
                        95, 20,
                        "gui.config.message.button.commands",
                        new Runnable() {
                            @Override
                            public void run() {
                                CraftPresence.GUIS.openScreen(new CommandsGui(currentScreen));
                            }
                        }
                )
        );
        // Added Reset Config Button
        addControl(
                new ExtendedButtonControl(
                        10, (getScreenHeight() - 30),
                        95, 20,
                        "gui.config.message.button.reset",
                        new Runnable() {
                            @Override
                            public void run() {
                                CraftPresence.CONFIG.setupInitialValues();
                                CraftPresence.CONFIG.hasChanged = true;
                                CraftPresence.CONFIG.hasClientPropertiesChanged = true;
                                CraftPresence.CONFIG.flushClientProperties = true;
                                MainGui.this.syncRenderStates();
                            }
                        }
                )
        );
        // Added Sync Config Button
        addControl(
                new ExtendedButtonControl(
                        10, (getScreenHeight() - 55),
                        95, 20,
                        "gui.config.message.button.sync.config",
                        new Runnable() {
                            @Override
                            public void run() {
                                final List<Pair<String, Object>> currentConfigDataMappings = CraftPresence.CONFIG.configDataMappings;
                                CraftPresence.CONFIG.read(false, "UTF-8");

                                // Only Mark to Save if there have been Changes in the File
                                if (!CraftPresence.CONFIG.configDataMappings.equals(currentConfigDataMappings)) {
                                    CraftPresence.CONFIG.hasChanged = true;
                                    CraftPresence.CONFIG.hasClientPropertiesChanged = true;
                                    CraftPresence.CONFIG.flushClientProperties = true;
                                    MainGui.this.syncRenderStates();
                                }
                            }
                        },
                        new Runnable() {
                            @Override
                            public void run() {
                                CraftPresence.GUIS.drawMultiLineString(
                                        StringUtils.splitTextByNewLine(
                                                ModUtils.TRANSLATOR.translate("gui.config.comment.button.sync.config")
                                        ), MainGui.this, true
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
        final String releaseNotice = ModUtils.TRANSLATOR.translate("gui.config.message.tentative", ModUtils.VERSION_ID + " - " + StringUtils.formatWord(ModUtils.VERSION_LABEL));

        renderString(mainTitle, (getScreenWidth() / 2f) - (getStringWidth(mainTitle) / 2f), 15, 0xFFFFFF);

        if (!ModUtils.VERSION_TYPE.equalsIgnoreCase("release")) {
            renderString(releaseNotice, (getScreenWidth() / 2f) - (getStringWidth(releaseNotice) / 2f), getScreenHeight() - 85, 0xFFFFFF);
        }

        syncRenderStates();
    }

    @Override
    public void postRender() {
        final String mainTitle = ModUtils.TRANSLATOR.translate("gui.config.title");

        // Hovering over Title Label
        if (CraftPresence.GUIS.isMouseOver(getMouseX(), getMouseY(), (getScreenWidth() / 2f) - (getStringWidth(mainTitle) / 2f), 15, getStringWidth(mainTitle), getFontHeight())) {
            CraftPresence.GUIS.drawMultiLineString(
                    StringUtils.splitTextByNewLine(
                            ModUtils.TRANSLATOR.translate("gui.config.comment.title", ModUtils.VERSION_ID, ModUtils.MOD_SCHEMA_VERSION)
                    ), this, true
            );
        }
    }

    @Override
    protected void keyTyped(char typedChar, int keyCode) {
        if (keyCode == Keyboard.KEY_ESCAPE) {
            if (CraftPresence.CONFIG.hasChanged || CraftPresence.CONFIG.hasClientPropertiesChanged || CraftPresence.CONFIG.flushClientProperties) {
                CraftPresence.CONFIG.setupInitialValues();
                CraftPresence.CONFIG.read(false, "UTF-8");
                CraftPresence.CONFIG.hasChanged = false;
                CraftPresence.CONFIG.hasClientPropertiesChanged = false;
                CraftPresence.CONFIG.flushClientProperties = false;
            }
            CraftPresence.GUIS.configGUIOpened = false;
        }
        super.keyTyped(typedChar, keyCode);
    }

    private void syncRenderStates() {
        biomeSet.setControlEnabled(!CraftPresence.CONFIG.hasChanged ? CraftPresence.CONFIG.detectBiomeData : biomeSet.isControlEnabled());
        dimensionSet.setControlEnabled(!CraftPresence.CONFIG.hasChanged ? CraftPresence.CONFIG.detectDimensionData : dimensionSet.isControlEnabled());
        serverSet.setControlEnabled(!CraftPresence.CONFIG.hasChanged ? CraftPresence.CONFIG.detectWorldData : serverSet.isControlEnabled());
        commandGUIButton.setControlEnabled(!CraftPresence.CONFIG.hasChanged ? CraftPresence.CONFIG.enableCommands : commandGUIButton.isControlEnabled());
        controlsButton.setControlEnabled(CraftPresence.KEYBINDINGS.areKeysRegistered());

        proceedButton.setControlMessage(CraftPresence.CONFIG.hasChanged ? "gui.config.message.button.save" : "gui.config.message.button.back");
    }
}
