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
import com.gitlab.cdagaming.craftpresence.config.category.General;
import com.gitlab.cdagaming.craftpresence.impl.discord.PartyPrivacy;
import com.gitlab.cdagaming.craftpresence.utils.StringUtils;
import com.gitlab.cdagaming.craftpresence.utils.discord.assets.DiscordAssetUtils;
import com.gitlab.cdagaming.craftpresence.utils.gui.controls.CheckBoxControl;
import com.gitlab.cdagaming.craftpresence.utils.gui.controls.ExtendedButtonControl;
import com.gitlab.cdagaming.craftpresence.utils.gui.controls.ExtendedTextControl;
import com.gitlab.cdagaming.craftpresence.utils.gui.controls.ScrollableListControl.RenderType;
import com.gitlab.cdagaming.craftpresence.utils.gui.impl.ConfigurationGui;
import com.gitlab.cdagaming.craftpresence.utils.gui.impl.SelectorGui;
import com.gitlab.cdagaming.craftpresence.utils.gui.widgets.TextWidget;
import com.jagrosh.discordipc.entities.DiscordBuild;
import net.minecraft.client.gui.GuiScreen;

@SuppressWarnings("DuplicatedCode")
public class GeneralSettingsGui extends ConfigurationGui<General> {
    private final General INSTANCE;
    private ExtendedButtonControl partyPrivacyLevelButton, preferredClientLevelButton;
    private CheckBoxControl detectCurseManifestButton, detectMultiMCManifestButton,
            detectMCUpdaterInstanceButton, detectTechnicPackButton, detectATLauncherButton,
            detectBiomeDataButton, detectDimensionDataButton, detectWorldDataButton,
            enableJoinRequestButton, resetTimeOnInitButton, autoRegisterButton;
    private ExtendedTextControl clientId;
    private int currentPartyPrivacy = PartyPrivacy.Public.ordinal();
    private int currentPreferredClient = DiscordBuild.ANY.ordinal();

    GeneralSettingsGui(GuiScreen parentScreen) {
        super(parentScreen, "gui.config.title", "gui.config.title.general");
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
                        "gui.config.name.general.client_id",
                        () -> drawMultiLineString(
                                StringUtils.splitTextByNewLine(
                                        ModUtils.TRANSLATOR.translate("gui.config.comment.general.client_id")
                                )
                        )
                )
        );
        clientId.setControlMessage(getCurrentData().clientId);
        clientId.setControlMaxLength(32);

        final int buttonCalc1 = (getScreenWidth() / 2) - 183;
        final int buttonCalc2 = (getScreenWidth() / 2) + 3;

        final int checkboxCalc1 = (getScreenWidth() / 2) - 168;
        final int checkboxCalc2 = (getScreenWidth() / 2) + 18;

        // Adding Default Icon Button
        childFrame.addControl(
                new ExtendedButtonControl(
                        buttonCalc1, getButtonY(1),
                        180, 20,
                        "gui.config.name.general.default_icon",
                        () -> openScreen(
                                new SelectorGui(
                                        currentScreen,
                                        ModUtils.TRANSLATOR.translate("gui.config.title.selector.icon"), DiscordAssetUtils.ASSET_LIST.keySet(),
                                        getCurrentData().defaultIcon, null,
                                        true, false, RenderType.DiscordAsset,
                                        (attributeName, currentValue) -> {
                                            CraftPresence.CONFIG.hasChanged = true;
                                            getCurrentData().defaultIcon = currentValue;
                                        }, null
                                )
                        ),
                        () -> drawMultiLineString(
                                StringUtils.splitTextByNewLine(
                                        ModUtils.TRANSLATOR.translate("gui.config.comment.general.default_icon")
                                )
                        )
                )
        );
        currentPartyPrivacy = getCurrentData().partyPrivacyLevel;
        partyPrivacyLevelButton = childFrame.addControl(
                new ExtendedButtonControl(
                        buttonCalc2, getButtonY(1),
                        180, 20,
                        "gui.config.name.general.party_privacy => " + PartyPrivacy.from(currentPartyPrivacy).name(),
                        () -> {
                            currentPartyPrivacy = (currentPartyPrivacy + 1) % PartyPrivacy.values().length;
                            partyPrivacyLevelButton.setControlMessage("gui.config.name.general.party_privacy => " + PartyPrivacy.from(currentPartyPrivacy).name());
                        },
                        () -> drawMultiLineString(
                                StringUtils.splitTextByNewLine(
                                        ModUtils.TRANSLATOR.translate("gui.config.comment.general.party_privacy")
                                )
                        )
                )
        );
        currentPreferredClient = getCurrentData().preferredClientLevel;
        preferredClientLevelButton = childFrame.addControl(
                new ExtendedButtonControl(
                        buttonCalc2, getButtonY(2),
                        180, 20,
                        "gui.config.name.general.preferred_client => " + DiscordBuild.from(currentPreferredClient).name(),
                        () -> {
                            currentPreferredClient = (currentPreferredClient + 1) % DiscordBuild.values().length;
                            preferredClientLevelButton.setControlMessage("gui.config.name.general.preferred_client => " + DiscordBuild.from(currentPreferredClient).name());
                        },
                        () -> drawMultiLineString(
                                StringUtils.splitTextByNewLine(
                                        ModUtils.TRANSLATOR.translate("gui.config.comment.general.preferred_client")
                                )
                        )
                )
        );
        detectCurseManifestButton = childFrame.addControl(
                new CheckBoxControl(
                        checkboxCalc1, getButtonY(3),
                        "gui.config.name.general.detect_curse_manifest",
                        getCurrentData().detectCurseManifest,
                        null,
                        () -> drawMultiLineString(
                                StringUtils.splitTextByNewLine(
                                        ModUtils.TRANSLATOR.translate("gui.config.comment.general.detect_curse_manifest")
                                )
                        )
                )
        );
        detectMultiMCManifestButton = childFrame.addControl(
                new CheckBoxControl(
                        checkboxCalc2, getButtonY(3),
                        "gui.config.name.general.detect_multimc_manifest",
                        getCurrentData().detectMultiMCManifest,
                        null,
                        () -> drawMultiLineString(
                                StringUtils.splitTextByNewLine(
                                        ModUtils.TRANSLATOR.translate("gui.config.comment.general.detect_multimc_manifest")
                                )
                        )
                )
        );
        detectMCUpdaterInstanceButton = childFrame.addControl(
                new CheckBoxControl(
                        checkboxCalc1, getButtonY(4, -10),
                        "gui.config.name.general.detect_mcupdater_instance",
                        getCurrentData().detectMCUpdaterInstance,
                        null,
                        () -> drawMultiLineString(
                                StringUtils.splitTextByNewLine(
                                        ModUtils.TRANSLATOR.translate("gui.config.comment.general.detect_mcupdater_instance")
                                )
                        )
                )
        );
        detectTechnicPackButton = childFrame.addControl(
                new CheckBoxControl(
                        checkboxCalc2, getButtonY(4, -10),
                        "gui.config.name.general.detect_technic_pack",
                        getCurrentData().detectTechnicPack,
                        null,
                        () -> drawMultiLineString(
                                StringUtils.splitTextByNewLine(
                                        ModUtils.TRANSLATOR.translate("gui.config.comment.general.detect_technic_pack")
                                )
                        )
                )
        );
        detectATLauncherButton = childFrame.addControl(
                new CheckBoxControl(
                        checkboxCalc1, getButtonY(5, -20),
                        "gui.config.name.general.detect_atlauncher_instance",
                        getCurrentData().detectATLauncherInstance,
                        null,
                        () -> drawMultiLineString(
                                StringUtils.splitTextByNewLine(
                                        ModUtils.TRANSLATOR.translate("gui.config.comment.general.detect_atlauncher_instance")
                                )
                        )
                )
        );
        detectDimensionDataButton = childFrame.addControl(
                new CheckBoxControl(
                        checkboxCalc2, getButtonY(5, -20),
                        "gui.config.name.general.detect_dimension_data",
                        getCurrentData().detectDimensionData,
                        null,
                        () -> drawMultiLineString(
                                StringUtils.splitTextByNewLine(
                                        ModUtils.TRANSLATOR.translate("gui.config.comment.general.detect_dimension_data")
                                )
                        )
                )
        );
        enableJoinRequestButton = childFrame.addControl(
                new CheckBoxControl(
                        checkboxCalc1, getButtonY(6, -30),
                        "gui.config.name.general.enable_join_request",
                        getCurrentData().enableJoinRequests,
                        null,
                        () -> drawMultiLineString(
                                StringUtils.splitTextByNewLine(
                                        ModUtils.TRANSLATOR.translate("gui.config.comment.general.enable_join_request")
                                )
                        )
                )
        );
        detectBiomeDataButton = childFrame.addControl(
                new CheckBoxControl(
                        checkboxCalc2, getButtonY(6, -30),
                        "gui.config.name.general.detect_biome_data",
                        getCurrentData().detectBiomeData,
                        null,
                        () -> drawMultiLineString(
                                StringUtils.splitTextByNewLine(
                                        ModUtils.TRANSLATOR.translate("gui.config.comment.general.detect_biome_data")
                                )
                        )
                )
        );
        autoRegisterButton = childFrame.addControl(
                new CheckBoxControl(
                        checkboxCalc1, getButtonY(7, -40),
                        "gui.config.name.general.auto_register",
                        getCurrentData().autoRegister,
                        null,
                        () -> drawMultiLineString(
                                StringUtils.splitTextByNewLine(
                                        ModUtils.TRANSLATOR.translate("gui.config.comment.general.auto_register")
                                )
                        )
                )
        );
        detectWorldDataButton = childFrame.addControl(
                new CheckBoxControl(
                        checkboxCalc2, getButtonY(7, -40),
                        "gui.config.name.general.detect_world_data",
                        getCurrentData().detectWorldData,
                        null,
                        () -> drawMultiLineString(
                                StringUtils.splitTextByNewLine(
                                        ModUtils.TRANSLATOR.translate("gui.config.comment.general.detect_world_data")
                                )
                        )
                )
        );
        resetTimeOnInitButton = childFrame.addControl(
                new CheckBoxControl(
                        checkboxCalc1, getButtonY(2, 10),
                        "gui.config.name.general.reset_time_on_init",
                        getCurrentData().resetTimeOnInit,
                        null,
                        () -> drawMultiLineString(
                                StringUtils.splitTextByNewLine(
                                        ModUtils.TRANSLATOR.translate("gui.config.comment.general.reset_time_on_init")
                                )
                        )
                )
        );

        proceedButton.setOnHover(() -> {
            if (!proceedButton.isControlEnabled()) {
                drawMultiLineString(
                        StringUtils.splitTextByNewLine(
                                ModUtils.TRANSLATOR.translate("gui.config.message.hover.empty.default")
                        )
                );
            }
        });
    }

    @Override
    protected void applySettings() {
        if (!clientId.getControlMessage().equals(getCurrentData().clientId)) {
            CraftPresence.CONFIG.hasChanged = true;
            getCurrentData().clientId = clientId.getControlMessage();
        }
        if (currentPartyPrivacy != getCurrentData().partyPrivacyLevel) {
            CraftPresence.CONFIG.hasChanged = true;
            getCurrentData().partyPrivacyLevel = currentPartyPrivacy;
        }
        if (currentPreferredClient != getCurrentData().preferredClientLevel) {
            CraftPresence.CONFIG.hasChanged = true;
            getCurrentData().preferredClientLevel = currentPreferredClient;
        }
        if (detectATLauncherButton.isChecked() != getCurrentData().detectATLauncherInstance) {
            CraftPresence.CONFIG.hasChanged = true;
            getCurrentData().detectATLauncherInstance = detectATLauncherButton.isChecked();
        }
        if (detectCurseManifestButton.isChecked() != getCurrentData().detectCurseManifest) {
            CraftPresence.CONFIG.hasChanged = true;
            getCurrentData().detectCurseManifest = detectCurseManifestButton.isChecked();
        }
        if (detectMultiMCManifestButton.isChecked() != getCurrentData().detectMultiMCManifest) {
            CraftPresence.CONFIG.hasChanged = true;
            getCurrentData().detectMultiMCManifest = detectMultiMCManifestButton.isChecked();
        }
        if (detectMCUpdaterInstanceButton.isChecked() != getCurrentData().detectMCUpdaterInstance) {
            CraftPresence.CONFIG.hasChanged = true;
            getCurrentData().detectMCUpdaterInstance = detectMCUpdaterInstanceButton.isChecked();
        }
        if (detectTechnicPackButton.isChecked() != getCurrentData().detectTechnicPack) {
            CraftPresence.CONFIG.hasChanged = true;
            getCurrentData().detectTechnicPack = detectTechnicPackButton.isChecked();
        }
        if (detectBiomeDataButton.isChecked() != getCurrentData().detectBiomeData) {
            CraftPresence.CONFIG.hasChanged = true;
            getCurrentData().detectBiomeData = detectBiomeDataButton.isChecked();
        }
        if (detectDimensionDataButton.isChecked() != getCurrentData().detectDimensionData) {
            CraftPresence.CONFIG.hasChanged = true;
            getCurrentData().detectDimensionData = detectDimensionDataButton.isChecked();
        }
        if (detectWorldDataButton.isChecked() != getCurrentData().detectWorldData) {
            CraftPresence.CONFIG.hasChanged = true;
            getCurrentData().detectWorldData = detectWorldDataButton.isChecked();
        }
        if (enableJoinRequestButton.isChecked() != getCurrentData().enableJoinRequests) {
            CraftPresence.CONFIG.hasChanged = true;
            getCurrentData().enableJoinRequests = enableJoinRequestButton.isChecked();
        }
        if (resetTimeOnInitButton.isChecked() != getCurrentData().resetTimeOnInit) {
            CraftPresence.CONFIG.hasChanged = true;
            getCurrentData().resetTimeOnInit = resetTimeOnInitButton.isChecked();
        }
        if (autoRegisterButton.isChecked() != getCurrentData().autoRegister) {
            CraftPresence.CONFIG.hasChanged = true;
            getCurrentData().autoRegister = autoRegisterButton.isChecked();
        }
    }

    @Override
    protected boolean canReset() {
        return !getCurrentData().equals(getOriginalData().getDefaults());
    }

    @Override
    protected boolean allowedToReset() {
        return true;
    }

    @Override
    protected boolean resetData() {
        return setCurrentData(getOriginalData().getDefaults());
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
        return setCurrentData(Config.loadOrCreate().generalSettings);
    }

    @Override
    protected void syncRenderStates() {
        super.syncRenderStates();

        proceedButton.setControlEnabled(
                DiscordAssetUtils.isValidId(clientId.getControlMessage())
        );
    }

    @Override
    protected General getOriginalData() {
        return INSTANCE;
    }

    @Override
    protected General getCurrentData() {
        return CraftPresence.CONFIG.generalSettings;
    }

    @Override
    protected boolean setCurrentData(General data) {
        if (!getCurrentData().equals(data)) {
            getCurrentData().transferFrom(data);
            CraftPresence.CONFIG.hasChanged = true;
            return true;
        }
        return false;
    }
}
