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
import com.gitlab.cdagaming.craftpresence.core.config.category.General;
import com.gitlab.cdagaming.craftpresence.core.integrations.discord.assets.DiscordAssetUtils;
import com.gitlab.cdagaming.craftpresence.utils.gui.impl.ConfigurationGui;
import com.gitlab.cdagaming.unilib.utils.gui.controls.CheckBoxControl;
import com.gitlab.cdagaming.unilib.utils.gui.controls.ExtendedButtonControl;
import com.gitlab.cdagaming.unilib.utils.gui.widgets.TextWidget;
import com.jagrosh.discordipc.entities.DiscordBuild;
import io.github.cdagaming.unicore.utils.StringUtils;

public class GeneralSettingsGui extends ConfigurationGui<General> {
    private final General INSTANCE, DEFAULTS;
    private ExtendedButtonControl preferredClientLevelButton;
    private CheckBoxControl detectCurseManifestButton, detectMultiMCManifestButton,
            detectMCUpdaterInstanceButton, detectTechnicPackButton,
            detectATLauncherButton, detectModrinthPackButton,
            detectBiomeDataButton, detectDimensionDataButton, detectWorldDataButton,
            enableJoinRequestButton, resetTimeOnInitButton, autoRegisterButton;
    private TextWidget clientId, defaultIcon;
    private int currentPreferredClient = DiscordBuild.ANY.ordinal();

    GeneralSettingsGui() {
        super(
                Constants.TRANSLATOR.translate("gui.config.title"),
                Constants.TRANSLATOR.translate("gui.config.title.general")
        );
        DEFAULTS = getCurrentData().getDefaults();
        INSTANCE = getCurrentData().copy();
    }

    @Override
    protected void appendControls() {
        super.appendControls();

        clientId = childFrame.addControl(
                new TextWidget(
                        getFontRenderer(),
                        getButtonY(0),
                        180, 20,
                        () -> getInstanceData().clientId = clientId.getControlMessage(),
                        Constants.TRANSLATOR.translate("gui.config.name.general.client_id"),
                        () -> drawMultiLineString(
                                StringUtils.splitTextByNewLine(
                                        Constants.TRANSLATOR.translate("gui.config.comment.general.client_id")
                                )
                        )
                )
        );
        clientId.setControlMessage(getInstanceData().clientId);
        clientId.setControlMaxLength(32);

        final int calc1 = (getScreenWidth() / 2) - 183;
        final int calc2 = (getScreenWidth() / 2) + 3;

        // Adding Default Icon Data
        defaultIcon = childFrame.addControl(
                new TextWidget(
                        getFontRenderer(),
                        getButtonY(1),
                        147, 20,
                        () -> getInstanceData().defaultIcon = defaultIcon.getControlMessage(),
                        Constants.TRANSLATOR.translate("gui.config.name.general.default_icon"),
                        () -> drawMultiLineString(
                                StringUtils.splitTextByNewLine(
                                        Constants.TRANSLATOR.translate("gui.config.comment.general.default_icon")
                                )
                        )
                )
        );
        addIconSelector(childFrame, () -> defaultIcon,
                (attributeName, currentValue) -> getInstanceData().defaultIcon = currentValue
        );
        defaultIcon.setControlMessage(getInstanceData().defaultIcon);

        currentPreferredClient = getInstanceData().preferredClientLevel;
        preferredClientLevelButton = childFrame.addControl(
                new ExtendedButtonControl(
                        calc2, getButtonY(2),
                        180, 20,
                        Constants.TRANSLATOR.translate("gui.config.name.general.preferred_client") + " => " + DiscordBuild.from(currentPreferredClient).name(),
                        () -> {
                            currentPreferredClient = (currentPreferredClient + 1) % DiscordBuild.values().length;
                            preferredClientLevelButton.setControlMessage(
                                    Constants.TRANSLATOR.translate("gui.config.name.general.preferred_client") + " => " + DiscordBuild.from(currentPreferredClient).name()
                            );
                            getInstanceData().preferredClientLevel = currentPreferredClient;
                        },
                        () -> drawMultiLineString(
                                StringUtils.splitTextByNewLine(
                                        Constants.TRANSLATOR.translate("gui.config.comment.general.preferred_client")
                                )
                        )
                )
        );
        detectCurseManifestButton = childFrame.addControl(
                new CheckBoxControl(
                        calc1, getButtonY(3),
                        Constants.TRANSLATOR.translate("gui.config.name.general.detect_curse_manifest"),
                        getInstanceData().detectCurseManifest,
                        () -> getInstanceData().detectCurseManifest = detectCurseManifestButton.isChecked(),
                        () -> drawMultiLineString(
                                StringUtils.splitTextByNewLine(
                                        Constants.TRANSLATOR.translate("gui.config.comment.general.detect_curse_manifest")
                                )
                        )
                )
        );
        detectMultiMCManifestButton = childFrame.addControl(
                new CheckBoxControl(
                        calc2, getButtonY(3),
                        Constants.TRANSLATOR.translate("gui.config.name.general.detect_multimc_manifest"),
                        getInstanceData().detectMultiMCManifest,
                        () -> getInstanceData().detectMultiMCManifest = detectMultiMCManifestButton.isChecked(),
                        () -> drawMultiLineString(
                                StringUtils.splitTextByNewLine(
                                        Constants.TRANSLATOR.translate("gui.config.comment.general.detect_multimc_manifest")
                                )
                        )
                )
        );
        detectMCUpdaterInstanceButton = childFrame.addControl(
                new CheckBoxControl(
                        calc1, getButtonY(4, -10),
                        Constants.TRANSLATOR.translate("gui.config.name.general.detect_mcupdater_instance"),
                        getInstanceData().detectMCUpdaterInstance,
                        () -> getInstanceData().detectMCUpdaterInstance = detectMCUpdaterInstanceButton.isChecked(),
                        () -> drawMultiLineString(
                                StringUtils.splitTextByNewLine(
                                        Constants.TRANSLATOR.translate("gui.config.comment.general.detect_mcupdater_instance")
                                )
                        )
                )
        );
        detectTechnicPackButton = childFrame.addControl(
                new CheckBoxControl(
                        calc2, getButtonY(4, -10),
                        Constants.TRANSLATOR.translate("gui.config.name.general.detect_technic_pack"),
                        getInstanceData().detectTechnicPack,
                        () -> getInstanceData().detectTechnicPack = detectTechnicPackButton.isChecked(),
                        () -> drawMultiLineString(
                                StringUtils.splitTextByNewLine(
                                        Constants.TRANSLATOR.translate("gui.config.comment.general.detect_technic_pack")
                                )
                        )
                )
        );
        detectATLauncherButton = childFrame.addControl(
                new CheckBoxControl(
                        calc1, getButtonY(5, -20),
                        Constants.TRANSLATOR.translate("gui.config.name.general.detect_atlauncher_instance"),
                        getInstanceData().detectATLauncherInstance,
                        () -> getInstanceData().detectATLauncherInstance = detectATLauncherButton.isChecked(),
                        () -> drawMultiLineString(
                                StringUtils.splitTextByNewLine(
                                        Constants.TRANSLATOR.translate("gui.config.comment.general.detect_atlauncher_instance")
                                )
                        )
                )
        );
        detectModrinthPackButton = childFrame.addControl(
                new CheckBoxControl(
                        calc2, getButtonY(5, -20),
                        Constants.TRANSLATOR.translate("gui.config.name.general.detect_modrinth_pack"),
                        getInstanceData().detectModrinthPack,
                        () -> getInstanceData().detectModrinthPack = detectModrinthPackButton.isChecked(),
                        () -> drawMultiLineString(
                                StringUtils.splitTextByNewLine(
                                        Constants.TRANSLATOR.translate("gui.config.comment.general.detect_modrinth_pack")
                                )
                        )
                )
        );
        enableJoinRequestButton = childFrame.addControl(
                new CheckBoxControl(
                        calc1, getButtonY(6, -30),
                        Constants.TRANSLATOR.translate("gui.config.name.general.enable_join_request"),
                        getInstanceData().enableJoinRequests,
                        () -> getInstanceData().enableJoinRequests = enableJoinRequestButton.isChecked(),
                        () -> drawMultiLineString(
                                StringUtils.splitTextByNewLine(
                                        Constants.TRANSLATOR.translate("gui.config.comment.general.enable_join_request")
                                )
                        )
                )
        );
        detectDimensionDataButton = childFrame.addControl(
                new CheckBoxControl(
                        calc2, getButtonY(6, -30),
                        Constants.TRANSLATOR.translate("gui.config.name.general.detect_dimension_data"),
                        getInstanceData().detectDimensionData,
                        () -> getInstanceData().detectDimensionData = detectDimensionDataButton.isChecked(),
                        () -> drawMultiLineString(
                                StringUtils.splitTextByNewLine(
                                        Constants.TRANSLATOR.translate("gui.config.comment.general.detect_dimension_data")
                                )
                        )
                )
        );
        autoRegisterButton = childFrame.addControl(
                new CheckBoxControl(
                        calc1, getButtonY(7, -40),
                        Constants.TRANSLATOR.translate("gui.config.name.general.auto_register"),
                        getInstanceData().autoRegister,
                        () -> getInstanceData().autoRegister = autoRegisterButton.isChecked(),
                        () -> drawMultiLineString(
                                StringUtils.splitTextByNewLine(
                                        Constants.TRANSLATOR.translate("gui.config.comment.general.auto_register")
                                )
                        )
                )
        );
        detectBiomeDataButton = childFrame.addControl(
                new CheckBoxControl(
                        calc2, getButtonY(7, -40),
                        Constants.TRANSLATOR.translate("gui.config.name.general.detect_biome_data"),
                        getInstanceData().detectBiomeData,
                        () -> getInstanceData().detectBiomeData = detectBiomeDataButton.isChecked(),
                        () -> drawMultiLineString(
                                StringUtils.splitTextByNewLine(
                                        Constants.TRANSLATOR.translate("gui.config.comment.general.detect_biome_data")
                                )
                        )
                )
        );
        resetTimeOnInitButton = childFrame.addControl(
                new CheckBoxControl(
                        calc1, getButtonY(8, -50),
                        Constants.TRANSLATOR.translate("gui.config.name.general.reset_time_on_init"),
                        getInstanceData().resetTimeOnInit,
                        () -> getInstanceData().resetTimeOnInit = resetTimeOnInitButton.isChecked(),
                        () -> drawMultiLineString(
                                StringUtils.splitTextByNewLine(
                                        Constants.TRANSLATOR.translate("gui.config.comment.general.reset_time_on_init")
                                )
                        )
                )
        );
        detectWorldDataButton = childFrame.addControl(
                new CheckBoxControl(
                        calc2, getButtonY(8, -50),
                        Constants.TRANSLATOR.translate("gui.config.name.general.detect_world_data"),
                        getInstanceData().detectWorldData,
                        () -> getInstanceData().detectWorldData = detectWorldDataButton.isChecked(),
                        () -> drawMultiLineString(
                                StringUtils.splitTextByNewLine(
                                        Constants.TRANSLATOR.translate("gui.config.comment.general.detect_world_data")
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
    }

    @Override
    protected boolean allowedToReset() {
        return true;
    }

    @Override
    protected void syncRenderStates() {
        super.syncRenderStates();

        proceedButton.setControlEnabled(
                DiscordAssetUtils.isValidId(clientId.getControlMessage())
        );
    }

    @Override
    protected General getInstanceData() {
        return INSTANCE;
    }

    @Override
    protected General getCurrentData() {
        return CraftPresence.CONFIG.generalSettings;
    }

    @Override
    protected General getDefaultData() {
        return DEFAULTS;
    }
}
