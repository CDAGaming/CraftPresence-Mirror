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
import com.gitlab.cdagaming.craftpresence.config.category.General;
import com.gitlab.cdagaming.craftpresence.impl.discord.PartyPrivacy;
import com.gitlab.cdagaming.craftpresence.utils.StringUtils;
import com.gitlab.cdagaming.craftpresence.utils.discord.assets.DiscordAssetUtils;
import com.gitlab.cdagaming.craftpresence.utils.gui.controls.CheckBoxControl;
import com.gitlab.cdagaming.craftpresence.utils.gui.controls.ExtendedButtonControl;
import com.gitlab.cdagaming.craftpresence.utils.gui.controls.ExtendedTextControl;
import com.gitlab.cdagaming.craftpresence.utils.gui.controls.ScrollableListControl.RenderType;
import com.gitlab.cdagaming.craftpresence.utils.gui.impl.SelectorGui;
import com.gitlab.cdagaming.craftpresence.utils.gui.integrations.ExtendedScreen;
import com.jagrosh.discordipc.entities.DiscordBuild;
import net.minecraft.client.gui.screens.Screen;

@SuppressWarnings("DuplicatedCode")
public class GeneralSettingsGui extends ExtendedScreen {
    private final General CONFIG;
    private ExtendedButtonControl proceedButton, partyPrivacyLevelButton, preferredClientLevelButton;
    private CheckBoxControl detectCurseManifestButton, detectMultiMCManifestButton,
            detectMCUpdaterInstanceButton, detectTechnicPackButton, showTimeButton,
            detectBiomeDataButton, detectDimensionDataButton, detectWorldDataButton,
            enableJoinRequestButton, resetTimeOnInitButton, autoRegisterButton;
    private ExtendedTextControl clientId;
    private int currentPartyPrivacy = PartyPrivacy.Public.ordinal();
    private int currentPreferredClient = DiscordBuild.ANY.ordinal();

    GeneralSettingsGui(Screen parentScreen) {
        super(parentScreen);
        CONFIG = CraftPresence.CONFIG.generalSettings;
    }

    @Override
    public void initializeUi() {
        clientId = addControl(
                new ExtendedTextControl(
                        getFontRenderer(),
                        (getScreenWidth() / 2) + 3, CraftPresence.GUIS.getButtonY(1),
                        180, 20
                )
        );
        clientId.setControlMessage(CONFIG.clientId);
        clientId.setControlMaxLength(32);

        final int buttonCalc1 = (getScreenWidth() / 2) - 183;
        final int buttonCalc2 = (getScreenWidth() / 2) + 3;

        final int checkboxCalc1 = (getScreenWidth() / 2) - 168;
        final int checkboxCalc2 = (getScreenWidth() / 2) + 18;

        // Adding Default Icon Button
        addControl(
                new ExtendedButtonControl(
                        buttonCalc1, CraftPresence.GUIS.getButtonY(2),
                        180, 20,
                        "gui.config.name.general.default_icon",
                        () -> CraftPresence.GUIS.openScreen(
                                new SelectorGui(
                                        currentScreen,
                                        ModUtils.TRANSLATOR.translate("gui.config.title.selector.icon"), DiscordAssetUtils.ASSET_LIST.keySet(),
                                        CONFIG.defaultIcon, null,
                                        true, false, RenderType.DiscordAsset,
                                        (attributeName, currentValue) -> {
                                            CraftPresence.CONFIG.hasChanged = true;
                                            CraftPresence.CONFIG.hasClientPropertiesChanged = true;
                                            CONFIG.defaultIcon = currentValue;
                                        }, null
                                )
                        ),
                        () -> CraftPresence.GUIS.drawMultiLineString(
                                StringUtils.splitTextByNewLine(
                                        ModUtils.TRANSLATOR.translate("gui.config.comment.general.default_icon")
                                ), this, true
                        )
                )
        );
        currentPartyPrivacy = CONFIG.partyPrivacyLevel;
        partyPrivacyLevelButton = addControl(
                new ExtendedButtonControl(
                        buttonCalc2, CraftPresence.GUIS.getButtonY(2),
                        180, 20,
                        "gui.config.name.general.party_privacy => " + PartyPrivacy.from(currentPartyPrivacy).name(),
                        () -> currentPartyPrivacy = (currentPartyPrivacy + 1) % PartyPrivacy.values().length,
                        () -> CraftPresence.GUIS.drawMultiLineString(
                                StringUtils.splitTextByNewLine(
                                        ModUtils.TRANSLATOR.translate("gui.config.comment.general.party_privacy")
                                ), this, true
                        )
                )
        );
        detectCurseManifestButton = addControl(
                new CheckBoxControl(
                        checkboxCalc1, CraftPresence.GUIS.getButtonY(3),
                        "gui.config.name.general.detect_curse_manifest",
                        CONFIG.detectCurseManifest,
                        null,
                        () -> CraftPresence.GUIS.drawMultiLineString(
                                StringUtils.splitTextByNewLine(
                                        ModUtils.TRANSLATOR.translate("gui.config.comment.general.detect_curse_manifest")
                                ), this, true
                        )
                )
        );
        detectMultiMCManifestButton = addControl(
                new CheckBoxControl(
                        checkboxCalc2, CraftPresence.GUIS.getButtonY(3),
                        "gui.config.name.general.detect_multimc_manifest",
                        CONFIG.detectMultiMCManifest,
                        null,
                        () -> CraftPresence.GUIS.drawMultiLineString(
                                StringUtils.splitTextByNewLine(
                                        ModUtils.TRANSLATOR.translate("gui.config.comment.general.detect_multimc_manifest")
                                ), this, true
                        )
                )
        );
        detectMCUpdaterInstanceButton = addControl(
                new CheckBoxControl(
                        checkboxCalc1, CraftPresence.GUIS.getButtonY(4, -10),
                        "gui.config.name.general.detect_mcupdater_instance",
                        CONFIG.detectMCUpdaterInstance,
                        null,
                        () -> CraftPresence.GUIS.drawMultiLineString(
                                StringUtils.splitTextByNewLine(
                                        ModUtils.TRANSLATOR.translate("gui.config.comment.general.detect_mcupdater_instance")
                                ), this, true
                        )
                )
        );
        detectTechnicPackButton = addControl(
                new CheckBoxControl(
                        checkboxCalc2, CraftPresence.GUIS.getButtonY(4, -10),
                        "gui.config.name.general.detect_technic_pack",
                        CONFIG.detectTechnicPack,
                        null,
                        () -> CraftPresence.GUIS.drawMultiLineString(
                                StringUtils.splitTextByNewLine(
                                        ModUtils.TRANSLATOR.translate("gui.config.comment.general.detect_technic_pack")
                                ), this, true
                        )
                )
        );
        showTimeButton = addControl(
                new CheckBoxControl(
                        checkboxCalc1, CraftPresence.GUIS.getButtonY(5, -20),
                        "gui.config.name.general.show_time",
                        CONFIG.showTime,
                        null,
                        () -> CraftPresence.GUIS.drawMultiLineString(
                                StringUtils.splitTextByNewLine(
                                        ModUtils.TRANSLATOR.translate("gui.config.comment.general.show_time")
                                ), this, true
                        )
                )
        );
        detectBiomeDataButton = addControl(
                new CheckBoxControl(
                        checkboxCalc2, CraftPresence.GUIS.getButtonY(5, -20),
                        "gui.config.name.general.detect_biome_data",
                        CONFIG.detectBiomeData,
                        null,
                        () -> CraftPresence.GUIS.drawMultiLineString(
                                StringUtils.splitTextByNewLine(
                                        ModUtils.TRANSLATOR.translate("gui.config.comment.general.detect_biome_data")
                                ), this, true
                        )
                )
        );
        detectDimensionDataButton = addControl(
                new CheckBoxControl(
                        checkboxCalc1, CraftPresence.GUIS.getButtonY(6, -30),
                        "gui.config.name.general.detect_dimension_data",
                        CONFIG.detectDimensionData,
                        null,
                        () -> CraftPresence.GUIS.drawMultiLineString(
                                StringUtils.splitTextByNewLine(
                                        ModUtils.TRANSLATOR.translate("gui.config.comment.general.detect_dimension_data")
                                ), this, true
                        )
                )
        );
        detectWorldDataButton = addControl(
                new CheckBoxControl(
                        checkboxCalc2, CraftPresence.GUIS.getButtonY(6, -30),
                        "gui.config.name.general.detect_world_data",
                        CONFIG.detectWorldData,
                        null,
                        () -> CraftPresence.GUIS.drawMultiLineString(
                                StringUtils.splitTextByNewLine(
                                        ModUtils.TRANSLATOR.translate("gui.config.comment.general.detect_world_data")
                                ), this, true
                        )
                )
        );
        enableJoinRequestButton = addControl(
                new CheckBoxControl(
                        checkboxCalc1, CraftPresence.GUIS.getButtonY(7, -40),
                        "gui.config.name.general.enable_join_request",
                        CONFIG.enableJoinRequests,
                        null,
                        () -> CraftPresence.GUIS.drawMultiLineString(
                                StringUtils.splitTextByNewLine(
                                        ModUtils.TRANSLATOR.translate("gui.config.comment.general.enable_join_request")
                                ), this, true
                        )
                )
        );
        resetTimeOnInitButton = addControl(
                new CheckBoxControl(
                        checkboxCalc2, CraftPresence.GUIS.getButtonY(7, -40),
                        "gui.config.name.general.reset_time_on_init",
                        CONFIG.resetTimeOnInit,
                        null,
                        () -> CraftPresence.GUIS.drawMultiLineString(
                                StringUtils.splitTextByNewLine(
                                        ModUtils.TRANSLATOR.translate("gui.config.comment.general.reset_time_on_init")
                                ), this, true
                        )
                )
        );
        autoRegisterButton = addControl(
                new CheckBoxControl(
                        checkboxCalc1, CraftPresence.GUIS.getButtonY(8, -50),
                        "gui.config.name.general.auto_register",
                        CONFIG.autoRegister,
                        null,
                        () -> CraftPresence.GUIS.drawMultiLineString(
                                StringUtils.splitTextByNewLine(
                                        ModUtils.TRANSLATOR.translate("gui.config.comment.general.auto_register")
                                ), this, true
                        )
                )
        );
        currentPreferredClient = CONFIG.preferredClientLevel;
        preferredClientLevelButton = addControl(
                new ExtendedButtonControl(
                        (getScreenWidth() / 2) - 90, (getScreenHeight() - 55),
                        180, 20,
                        "gui.config.name.general.preferred_client => " + DiscordBuild.from(currentPreferredClient).name(),
                        () -> currentPreferredClient = (currentPreferredClient + 1) % DiscordBuild.values().length,
                        () -> CraftPresence.GUIS.drawMultiLineString(
                                StringUtils.splitTextByNewLine(
                                        ModUtils.TRANSLATOR.translate("gui.config.comment.general.preferred_client")
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
                            if (!clientId.getControlMessage().equals(CONFIG.clientId)) {
                                CraftPresence.CONFIG.hasChanged = true;
                                CraftPresence.CONFIG.hasClientPropertiesChanged = true;
                                CONFIG.clientId = clientId.getControlMessage();
                            }
                            if (currentPartyPrivacy != CONFIG.partyPrivacyLevel) {
                                CraftPresence.CONFIG.hasChanged = true;
                                CraftPresence.CONFIG.hasClientPropertiesChanged = true;
                                CONFIG.partyPrivacyLevel = currentPartyPrivacy;
                            }
                            if (currentPreferredClient != CONFIG.preferredClientLevel) {
                                CraftPresence.CONFIG.hasChanged = true;
                                CraftPresence.CONFIG.hasClientPropertiesChanged = true;
                                CONFIG.preferredClientLevel = currentPreferredClient;
                            }
                            if (detectCurseManifestButton.isChecked() != CONFIG.detectCurseManifest) {
                                CraftPresence.CONFIG.hasChanged = true;
                                CraftPresence.CONFIG.hasClientPropertiesChanged = true;
                                CONFIG.detectCurseManifest = detectCurseManifestButton.isChecked();
                            }
                            if (detectMultiMCManifestButton.isChecked() != CONFIG.detectMultiMCManifest) {
                                CraftPresence.CONFIG.hasChanged = true;
                                CraftPresence.CONFIG.hasClientPropertiesChanged = true;
                                CONFIG.detectMultiMCManifest = detectMultiMCManifestButton.isChecked();
                            }
                            if (detectMCUpdaterInstanceButton.isChecked() != CONFIG.detectMCUpdaterInstance) {
                                CraftPresence.CONFIG.hasChanged = true;
                                CraftPresence.CONFIG.hasClientPropertiesChanged = true;
                                CONFIG.detectMCUpdaterInstance = detectMCUpdaterInstanceButton.isChecked();
                            }
                            if (detectTechnicPackButton.isChecked() != CONFIG.detectTechnicPack) {
                                CraftPresence.CONFIG.hasChanged = true;
                                CraftPresence.CONFIG.hasClientPropertiesChanged = true;
                                CONFIG.detectTechnicPack = detectTechnicPackButton.isChecked();
                            }
                            if (showTimeButton.isChecked() != CONFIG.showTime) {
                                CraftPresence.CONFIG.hasChanged = true;
                                CraftPresence.CONFIG.hasClientPropertiesChanged = true;
                                CONFIG.showTime = showTimeButton.isChecked();
                            }
                            if (detectBiomeDataButton.isChecked() != CONFIG.detectBiomeData) {
                                CraftPresence.CONFIG.hasChanged = true;
                                CraftPresence.CONFIG.hasClientPropertiesChanged = true;
                                CONFIG.detectBiomeData = detectBiomeDataButton.isChecked();
                            }
                            if (detectDimensionDataButton.isChecked() != CONFIG.detectDimensionData) {
                                CraftPresence.CONFIG.hasChanged = true;
                                CraftPresence.CONFIG.hasClientPropertiesChanged = true;
                                CONFIG.detectDimensionData = detectDimensionDataButton.isChecked();
                            }
                            if (detectWorldDataButton.isChecked() != CONFIG.detectWorldData) {
                                CraftPresence.CONFIG.hasChanged = true;
                                CraftPresence.CONFIG.hasClientPropertiesChanged = true;
                                CONFIG.detectWorldData = detectWorldDataButton.isChecked();
                            }
                            if (enableJoinRequestButton.isChecked() != CONFIG.enableJoinRequests) {
                                CraftPresence.CONFIG.hasChanged = true;
                                CraftPresence.CONFIG.hasClientPropertiesChanged = true;
                                CONFIG.enableJoinRequests = enableJoinRequestButton.isChecked();
                            }
                            if (resetTimeOnInitButton.isChecked() != CONFIG.resetTimeOnInit) {
                                CraftPresence.CONFIG.hasChanged = true;
                                CraftPresence.CONFIG.hasClientPropertiesChanged = true;
                                CONFIG.resetTimeOnInit = resetTimeOnInitButton.isChecked();
                            }
                            if (autoRegisterButton.isChecked() != CONFIG.autoRegister) {
                                CraftPresence.CONFIG.hasChanged = true;
                                CraftPresence.CONFIG.hasClientPropertiesChanged = true;
                                CONFIG.autoRegister = autoRegisterButton.isChecked();
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
        final String subTitle = ModUtils.TRANSLATOR.translate("gui.config.title.general");
        final String clientIdText = ModUtils.TRANSLATOR.translate("gui.config.name.general.client_id");

        renderString(mainTitle, (getScreenWidth() / 2f) - (getStringWidth(mainTitle) / 2f), 10, 0xFFFFFF);
        renderString(subTitle, (getScreenWidth() / 2f) - (getStringWidth(subTitle) / 2f), 20, 0xFFFFFF);
        renderString(clientIdText, (getScreenWidth() / 2f) - 130, CraftPresence.GUIS.getButtonY(1, 5), 0xFFFFFF);

        partyPrivacyLevelButton.setControlMessage("gui.config.name.general.party_privacy => " + PartyPrivacy.from(currentPartyPrivacy).name());
        preferredClientLevelButton.setControlMessage("gui.config.name.general.preferred_client => " + DiscordBuild.from(currentPreferredClient).name());
        proceedButton.setControlEnabled(!StringUtils.isNullOrEmpty(clientId.getControlMessage()) && clientId.getControlMessage().length() >= 18 && StringUtils.getValidLong(clientId.getControlMessage()).getFirst());
    }

    @Override
    public void postRender() {
        final String clientIdText = ModUtils.TRANSLATOR.translate("gui.config.name.general.client_id");
        // Hovering over Client ID Label
        if (CraftPresence.GUIS.isMouseOver(getMouseX(), getMouseY(), (getScreenWidth() / 2f) - 130, CraftPresence.GUIS.getButtonY(1, 5), getStringWidth(clientIdText), getFontHeight())) {
            CraftPresence.GUIS.drawMultiLineString(
                    StringUtils.splitTextByNewLine(
                            ModUtils.TRANSLATOR.translate("gui.config.comment.general.client_id")
                    ), this, true
            );
        }
    }
}
