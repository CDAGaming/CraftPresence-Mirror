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
import com.gitlab.cdagaming.craftpresence.core.Constants;
import com.gitlab.cdagaming.craftpresence.core.config.Config;
import com.gitlab.cdagaming.craftpresence.utils.gui.impl.ConfigurationGui;
import com.gitlab.cdagaming.unilib.ModUtils;
import com.gitlab.cdagaming.unilib.core.CoreUtils;
import com.gitlab.cdagaming.unilib.utils.gui.controls.ExtendedButtonControl;
import com.gitlab.cdagaming.unilib.utils.gui.widgets.ScrollableTextWidget;
import io.github.cdagaming.unicore.utils.StringUtils;
import net.minecraft.client.gui.GuiScreen;

public class MainGui extends ConfigurationGui<Config> {
    private final Config INSTANCE, DEFAULTS;
    private ExtendedButtonControl biomeSet,
            dimensionSet,
            serverSet;

    public MainGui(GuiScreen parentScreen) {
        super(
                Constants.TRANSLATOR.translate("gui.config.title")
        );
        setParent(parentScreen);

        DEFAULTS = getCurrentData().getDefaults();
        INSTANCE = getCurrentData().copy();
    }

    public MainGui() {
        this(null);
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
        } else if (!ModUtils.MCVersion.equalsIgnoreCase(CoreUtils.MCBuildVersion)) {
            releaseNotice = Constants.TRANSLATOR.translate("gui.config.message.version_difference", ModUtils.MCVersion, CoreUtils.MCBuildVersion);
        }
        if (!StringUtils.isNullOrEmpty(releaseNotice)) {
            currentY++;
            childFrame.addWidget(new ScrollableTextWidget(
                    true,
                    0, getButtonY(currentY),
                    childFrame.getScreenWidth(), releaseNotice
            ));
        }

        currentY++;
        // Adding General Settings Button
        childFrame.addControl(
                new ExtendedButtonControl(
                        calc1, getButtonY(currentY),
                        180, 20,
                        Constants.TRANSLATOR.translate("gui.config.title.general"),
                        () -> openScreen(new GeneralSettingsGui()),
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
                        Constants.TRANSLATOR.translate("gui.config.title.biome_messages"),
                        () -> openScreen(new BiomeSettingsGui()),
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
                        Constants.TRANSLATOR.translate("gui.config.title.dimension_messages"),
                        () -> openScreen(new DimensionSettingsGui()),
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
                        Constants.TRANSLATOR.translate("gui.config.title.server_messages"),
                        () -> openScreen(new ServerSettingsGui()),
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
        // Adding Status Settings Button
        childFrame.addControl(
                new ExtendedButtonControl(
                        calc1, getButtonY(currentY),
                        180, 20,
                        Constants.TRANSLATOR.translate("gui.config.title.status_messages"),
                        () -> openScreen(new StatusMessagesGui()),
                        () -> drawMultiLineString(
                                StringUtils.splitTextByNewLine(
                                        Constants.TRANSLATOR.translate("gui.config.comment.title.status_messages")
                                )
                        )
                )
        );
        // Adding Advanced Settings Button
        childFrame.addControl(
                new ExtendedButtonControl(
                        calc2, getButtonY(currentY),
                        180, 20,
                        Constants.TRANSLATOR.translate("gui.config.title.advanced"),
                        () -> openScreen(new AdvancedSettingsGui()),
                        () -> drawMultiLineString(
                                StringUtils.splitTextByNewLine(
                                        Constants.TRANSLATOR.translate("gui.config.comment.title.advanced")
                                )
                        )
                )
        );
        currentY++;
        // Adding Accessibility Settings Button
        childFrame.addControl(
                new ExtendedButtonControl(
                        calc1, getButtonY(currentY),
                        180, 20,
                        Constants.TRANSLATOR.translate("gui.config.title.accessibility"),
                        () -> openScreen(new AccessibilitySettingsGui()),
                        () -> drawMultiLineString(
                                StringUtils.splitTextByNewLine(
                                        Constants.TRANSLATOR.translate("gui.config.comment.title.accessibility")
                                )
                        )
                )
        );
        // Adding Presence Settings Button
        childFrame.addControl(
                new ExtendedButtonControl(
                        calc2, getButtonY(currentY),
                        180, 20,
                        Constants.TRANSLATOR.translate("gui.config.title.display_settings"),
                        () -> openScreen(new DisplaySettingsGui()),
                        () -> drawMultiLineString(
                                StringUtils.splitTextByNewLine(
                                        Constants.TRANSLATOR.translate("gui.config.comment.display_settings")
                                )
                        )
                )
        );
    }

    @Override
    protected void keyTyped(char typedChar, int keyCode) {
        if (isEscapeKey(keyCode) && getCurrentData().hasChanged()) {
            syncData();
        }
        super.keyTyped(typedChar, keyCode);
    }

    @Override
    protected void syncRenderStates() {
        // Ensure Critical Data is correct before continuing
        super.syncRenderStates();
        getCurrentData().setChanged(hasChangesBetween(getCurrentData(), getInstanceData()));

        biomeSet.setControlEnabled(CraftPresence.BIOMES.isEnabled());
        dimensionSet.setControlEnabled(CraftPresence.DIMENSIONS.isEnabled());
        serverSet.setControlEnabled(CraftPresence.SERVER.isEnabled());

        proceedButton.setControlMessage(Constants.TRANSLATOR.translate(
                getCurrentData().hasChanged() ? "gui.config.message.button.save" : "gui.config.message.button.back"
        ));
    }

    @Override
    protected boolean canReset() {
        return allowedToReset() && hasChangesBetween(getCurrentData(), getDefaultData());
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
    protected boolean resetData() {
        return setCurrentData(getDefaultData());
    }

    @Override
    protected void applySettings() {
        if (getCurrentData().hasChanged()) {
            getCurrentData().save();
            Constants.LOG.info(Constants.TRANSLATOR.translate("craftpresence.logger.info.config.save"));
            getCurrentData().applyFrom(getInstanceData());
        }
    }

    @Override
    protected Config getInstanceData() {
        return INSTANCE;
    }

    @Override
    protected Config getCurrentData() {
        return CraftPresence.CONFIG;
    }

    @Override
    protected Config getDefaultData() {
        // Hotfix: Preserve `dynamicIcons` as a cache setting
        DEFAULTS.displaySettings.dynamicIcons = getCurrentData().displaySettings.dynamicIcons;

        return DEFAULTS;
    }

    @Override
    protected Config getSyncData() {
        return Config.loadOrCreate();
    }

    @Override
    protected boolean syncData() {
        final Config data = getSyncData();
        if (setCurrentData(data)) {
            getCurrentData().applyFrom(getInstanceData());
            return setInstanceData(data);
        }
        return false;
    }

    @Override
    protected boolean setData(Config source, Config target) {
        if (hasChangesBetween(source, target)) {
            source.transferSettings(target);
            return true;
        }
        return false;
    }

    @Override
    protected boolean hasChangesBetween(Config source, Config target) {
        return target != null && !source.areSettingsEqual(target);
    }
}
