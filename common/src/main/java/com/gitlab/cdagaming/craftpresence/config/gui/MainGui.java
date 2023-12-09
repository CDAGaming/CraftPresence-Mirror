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
import com.gitlab.cdagaming.craftpresence.ModUtils;
import com.gitlab.cdagaming.craftpresence.config.Config;
import com.gitlab.cdagaming.craftpresence.core.Constants;
import com.gitlab.cdagaming.craftpresence.core.utils.StringUtils;
import com.gitlab.cdagaming.craftpresence.utils.gui.controls.ExtendedButtonControl;
import com.gitlab.cdagaming.craftpresence.utils.gui.impl.ConfigurationGui;
import com.gitlab.cdagaming.craftpresence.utils.gui.widgets.TextDisplayWidget;
import net.minecraft.client.gui.GuiScreen;
import org.lwjgl.input.Keyboard;

@SuppressWarnings("DuplicatedCode")
public class MainGui extends ConfigurationGui<Config> {
    private final Config INSTANCE, DEFAULTS;
    private ExtendedButtonControl biomeSet,
            dimensionSet,
            serverSet;

    public MainGui(GuiScreen parentScreen) {
        super(parentScreen, "gui.config.title");
        DEFAULTS = getCurrentData().getDefaults();
        INSTANCE = getCurrentData().copy();
    }

    @Override
    protected void appendControls() {
        super.appendControls();

        final int calc1 = (getScreenWidth() / 2) - 183;
        final int calc2 = (getScreenWidth() / 2) + 3;
        int currentY = -1;

        // Add Tentative Release Notice, if able
        // Note: This only is meant for a one-line notice -- any bigger and `getButtonY` will be wrong
        String releaseNotice = "";
        if (Constants.VERSION_TYPE.equalsIgnoreCase("alpha")) {
            releaseNotice = Constants.TRANSLATOR.translate("gui.config.message.tentative", Constants.VERSION_ID);
        } else if (!ModUtils.MCVersion.equalsIgnoreCase(Constants.MCBuildVersion)) {
            releaseNotice = Constants.TRANSLATOR.translate("gui.config.message.version_difference", ModUtils.MCVersion, Constants.MCBuildVersion);
        }
        if (!StringUtils.isNullOrEmpty(releaseNotice)) {
            currentY++;
            childFrame.addWidget(
                    new TextDisplayWidget(
                            childFrame, true,
                            0, getButtonY(currentY) - (getFontHeight() / 2) + 3,
                            childFrame.getScreenWidth(), releaseNotice
                    )
            );
        }

        currentY++;
        // Added General Settings Button
        childFrame.addControl(
                new ExtendedButtonControl(
                        calc1, getButtonY(currentY),
                        180, 20,
                        "gui.config.title.general",
                        () -> openScreen(new GeneralSettingsGui(currentScreen)),
                        () -> drawMultiLineString(
                                StringUtils.splitTextByNewLine(
                                        Constants.TRANSLATOR.translate("gui.config.comment.title.general")
                                )
                        )
                )
        );
        biomeSet = childFrame.addControl(
                new ExtendedButtonControl(
                        calc2, getButtonY(currentY),
                        180, 20,
                        "gui.config.title.biome_messages",
                        () -> openScreen(new BiomeSettingsGui(currentScreen)),
                        () -> {
                            if (!biomeSet.isControlEnabled()) {
                                drawMultiLineString(
                                        StringUtils.splitTextByNewLine(
                                                Constants.TRANSLATOR.translate("gui.config.message.hover.access",
                                                        Constants.TRANSLATOR.translate("gui.config.name.general.detect_biome_data"))
                                        )
                                );
                            } else {
                                drawMultiLineString(
                                        StringUtils.splitTextByNewLine(
                                                Constants.TRANSLATOR.translate("gui.config.comment.biome_messages.biome_messages",
                                                        CraftPresence.CLIENT.generateArgumentMessage("biome."))
                                        )
                                );
                            }
                        }
                )
        );
        currentY++;
        dimensionSet = childFrame.addControl(
                new ExtendedButtonControl(
                        calc1, getButtonY(currentY),
                        180, 20,
                        "gui.config.title.dimension_messages",
                        () -> openScreen(new DimensionSettingsGui(currentScreen)),
                        () -> {
                            if (!dimensionSet.isControlEnabled()) {
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
        serverSet = childFrame.addControl(
                new ExtendedButtonControl(
                        calc2, getButtonY(currentY),
                        180, 20,
                        "gui.config.title.server_messages",
                        () -> openScreen(new ServerSettingsGui(currentScreen)),
                        () -> {
                            if (!serverSet.isControlEnabled()) {
                                drawMultiLineString(
                                        StringUtils.splitTextByNewLine(
                                                Constants.TRANSLATOR.translate("gui.config.message.hover.access",
                                                        Constants.TRANSLATOR.translate("gui.config.name.general.detect_world_data"))
                                        )
                                );
                            } else {
                                drawMultiLineString(
                                        StringUtils.splitTextByNewLine(
                                                Constants.TRANSLATOR.translate("gui.config.comment.server_messages.server_messages",
                                                        CraftPresence.CLIENT.generateArgumentMessage("server.", "world.", "player."))
                                        )
                                );
                            }
                        }
                )
        );
        currentY++;
        // Added Status Settings Button
        childFrame.addControl(
                new ExtendedButtonControl(
                        calc1, getButtonY(currentY),
                        180, 20,
                        "gui.config.title.status_messages",
                        () -> openScreen(new StatusMessagesGui(currentScreen)),
                        () -> drawMultiLineString(
                                StringUtils.splitTextByNewLine(
                                        Constants.TRANSLATOR.translate("gui.config.comment.title.status_messages")
                                )
                        )
                )
        );
        // Added Advanced Settings Button
        childFrame.addControl(
                new ExtendedButtonControl(
                        calc2, getButtonY(currentY),
                        180, 20,
                        "gui.config.title.advanced",
                        () -> openScreen(new AdvancedSettingsGui(currentScreen)),
                        () -> drawMultiLineString(
                                StringUtils.splitTextByNewLine(
                                        Constants.TRANSLATOR.translate("gui.config.comment.title.advanced")
                                )
                        )
                )
        );
        currentY++;
        // Added Accessibility Settings Button
        childFrame.addControl(
                new ExtendedButtonControl(
                        calc1, getButtonY(currentY),
                        180, 20,
                        "gui.config.title.accessibility",
                        () -> openScreen(new AccessibilitySettingsGui(currentScreen)),
                        () -> drawMultiLineString(
                                StringUtils.splitTextByNewLine(
                                        Constants.TRANSLATOR.translate("gui.config.comment.title.accessibility")
                                )
                        )
                )
        );
        // Added Presence Settings Button
        childFrame.addControl(
                new ExtendedButtonControl(
                        calc2, getButtonY(currentY),
                        180, 20,
                        "gui.config.title.presence_settings",
                        () -> openScreen(new PresenceSettingsGui(currentScreen)),
                        () -> drawMultiLineString(
                                StringUtils.splitTextByNewLine(
                                        Constants.TRANSLATOR.translate("gui.config.comment.presence_settings")
                                )
                        )
                )
        );
    }

    @Override
    protected void keyTyped(char typedChar, int keyCode) {
        if (keyCode == Keyboard.KEY_ESCAPE && getCurrentData().hasChanged) {
            syncData();
        }
        super.keyTyped(typedChar, keyCode);
    }

    @Override
    protected void syncRenderStates() {
        // Ensure Critical Data is correct before continuing
        super.syncRenderStates();
        getCurrentData().hasChanged = !getCurrentData().equals(getOriginalData());

        biomeSet.setControlEnabled(CraftPresence.BIOMES.enabled);
        dimensionSet.setControlEnabled(CraftPresence.DIMENSIONS.enabled);
        serverSet.setControlEnabled(CraftPresence.SERVER.enabled);

        proceedButton.setControlMessage(getCurrentData().hasChanged ? "gui.config.message.button.save" : "gui.config.message.button.back");
    }

    @Override
    protected boolean canReset() {
        // Hotfix: Preserve `dynamicIcons` as a cache setting
        DEFAULTS.displaySettings.dynamicIcons = getCurrentData().displaySettings.dynamicIcons;

        return !getCurrentData().equals(DEFAULTS);
    }

    @Override
    protected boolean allowedToReset() {
        return true;
    }

    @Override
    protected boolean resetData() {
        // Hotfix: Preserve `dynamicIcons` as a cache setting
        DEFAULTS.displaySettings.dynamicIcons = getCurrentData().displaySettings.dynamicIcons;

        return setCurrentData(DEFAULTS);
    }

    @Override
    protected boolean canSync() {
        return true;
    }

    @Override
    protected boolean allowedToSync() {
        return true;
    }

    @Override
    protected boolean syncData() {
        return setCurrentData(Config.loadOrCreate());
    }

    @Override
    protected void applySettings() {
        if (getCurrentData().hasChanged) {
            getCurrentData().save();
            Constants.LOG.info(Constants.TRANSLATOR.translate("craftpresence.logger.info.config.save"));
            getCurrentData().applyFrom(getOriginalData());
        }
    }

    @Override
    protected Config getOriginalData() {
        return INSTANCE;
    }

    @Override
    protected Config getCurrentData() {
        return CraftPresence.CONFIG;
    }

    @Override
    protected boolean setCurrentData(Config data) {
        if (!getCurrentData().equals(data)) {
            getCurrentData().transferFrom(data);
            getCurrentData().hasChanged = true;
            return true;
        }
        return false;
    }
}
