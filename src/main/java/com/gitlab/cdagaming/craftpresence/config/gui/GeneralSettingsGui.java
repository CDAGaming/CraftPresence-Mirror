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
import net.minecraft.client.gui.GuiScreen;

@SuppressWarnings("DuplicatedCode")
public class GeneralSettingsGui extends ExtendedScreen {
    private ExtendedButtonControl proceedButton, partyPrivacyLevelButton, preferredClientLevelButton;
    private CheckBoxControl detectCurseManifestButton, detectMultiMCManifestButton,
            detectMCUpdaterInstanceButton, detectTechnicPackButton, showTimeButton,
            detectBiomeDataButton, detectDimensionDataButton, detectWorldDataButton,
            enableJoinRequestButton, resetTimeOnInitButton, autoRegisterButton;
    private ExtendedTextControl clientId;

    private int currentPartyPrivacy = PartyPrivacy.Public.ordinal();
    private int currentPreferredClient = DiscordBuild.ANY.ordinal();

    GeneralSettingsGui(GuiScreen parentScreen) {
        super(parentScreen);
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
        clientId.setControlMessage(CraftPresence.CONFIG.clientId);
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
                                        CraftPresence.CONFIG.defaultIcon, null,
                                        true, false, RenderType.DiscordAsset,
                                        (attributeName, currentValue) -> {
                                            CraftPresence.CONFIG.hasChanged = true;
                                            CraftPresence.CONFIG.hasClientPropertiesChanged = true;
                                            CraftPresence.CONFIG.defaultIcon = currentValue;
                                        }, null
                                )
                        ),
                        () -> CraftPresence.GUIS.drawMultiLineString(
                                StringUtils.splitTextByNewLine(
                                        ModUtils.TRANSLATOR.translate("gui.config.comment.general.default_icon")
                                ),
                                getMouseX(), getMouseY(),
                                getScreenWidth(), getScreenHeight(),
                                getWrapWidth(),
                                getFontRenderer(),
                                true
                        )
                )
        );
        currentPartyPrivacy = CraftPresence.CONFIG.partyPrivacyLevel;
        partyPrivacyLevelButton = addControl(
                new ExtendedButtonControl(
                        buttonCalc2, CraftPresence.GUIS.getButtonY(2),
                        180, 20,
                        CraftPresence.CONFIG.NAME_partyPrivacyLevel.replaceAll("_", " ") + " => " + PartyPrivacy.from(currentPartyPrivacy).name(),
                        () -> currentPartyPrivacy = (currentPartyPrivacy + 1) % PartyPrivacy.values().length,
                        () -> CraftPresence.GUIS.drawMultiLineString(
                                StringUtils.splitTextByNewLine(
                                        ModUtils.TRANSLATOR.translate("gui.config.comment.general.party_privacy")
                                ),
                                getMouseX(), getMouseY(),
                                getScreenWidth(), getScreenHeight(),
                                getWrapWidth(),
                                getFontRenderer(),
                                true
                        )
                )
        );
        detectCurseManifestButton = addControl(
                new CheckBoxControl(
                        checkboxCalc1, CraftPresence.GUIS.getButtonY(3),
                        "gui.config.name.general.detect_curse_manifest",
                        CraftPresence.CONFIG.detectCurseManifest,
                        null,
                        () -> CraftPresence.GUIS.drawMultiLineString(
                                StringUtils.splitTextByNewLine(
                                        ModUtils.TRANSLATOR.translate("gui.config.comment.general.detect_curse_manifest")
                                ),
                                getMouseX(), getMouseY(),
                                getScreenWidth(), getScreenHeight(),
                                getWrapWidth(),
                                getFontRenderer(),
                                true
                        )
                )
        );
        detectMultiMCManifestButton = addControl(
                new CheckBoxControl(
                        checkboxCalc2, CraftPresence.GUIS.getButtonY(3),
                        "gui.config.name.general.detect_multimc_manifest",
                        CraftPresence.CONFIG.detectMultiMCManifest,
                        null,
                        () -> CraftPresence.GUIS.drawMultiLineString(
                                StringUtils.splitTextByNewLine(
                                        ModUtils.TRANSLATOR.translate("gui.config.comment.general.detect_multimc_manifest")
                                ),
                                getMouseX(), getMouseY(),
                                getScreenWidth(), getScreenHeight(),
                                getWrapWidth(),
                                getFontRenderer(),
                                true
                        )
                )
        );
        detectMCUpdaterInstanceButton = addControl(
                new CheckBoxControl(
                        checkboxCalc1, CraftPresence.GUIS.getButtonY(4, -10),
                        "gui.config.name.general.detect_mcupdater_instance",
                        CraftPresence.CONFIG.detectMCUpdaterInstance,
                        null,
                        () -> CraftPresence.GUIS.drawMultiLineString(
                                StringUtils.splitTextByNewLine(
                                        ModUtils.TRANSLATOR.translate("gui.config.comment.general.detect_mcupdater_instance")
                                ),
                                getMouseX(), getMouseY(),
                                getScreenWidth(), getScreenHeight(),
                                getWrapWidth(),
                                getFontRenderer(),
                                true
                        )
                )
        );
        detectTechnicPackButton = addControl(
                new CheckBoxControl(
                        checkboxCalc2, CraftPresence.GUIS.getButtonY(4, -10),
                        "gui.config.name.general.detect_technic_pack",
                        CraftPresence.CONFIG.detectTechnicPack,
                        null,
                        () -> CraftPresence.GUIS.drawMultiLineString(
                                StringUtils.splitTextByNewLine(
                                        ModUtils.TRANSLATOR.translate("gui.config.comment.general.detect_technic_pack")
                                ),
                                getMouseX(), getMouseY(),
                                getScreenWidth(), getScreenHeight(),
                                getWrapWidth(),
                                getFontRenderer(),
                                true
                        )
                )
        );
        showTimeButton = addControl(
                new CheckBoxControl(
                        checkboxCalc1, CraftPresence.GUIS.getButtonY(5, -20),
                        "gui.config.name.general.show_time",
                        CraftPresence.CONFIG.showTime,
                        null,
                        () -> CraftPresence.GUIS.drawMultiLineString(
                                StringUtils.splitTextByNewLine(
                                        ModUtils.TRANSLATOR.translate("gui.config.comment.general.show_time")
                                ),
                                getMouseX(), getMouseY(),
                                getScreenWidth(), getScreenHeight(),
                                getWrapWidth(),
                                getFontRenderer(),
                                true
                        )
                )
        );
        detectBiomeDataButton = addControl(
                new CheckBoxControl(
                        checkboxCalc2, CraftPresence.GUIS.getButtonY(5, -20),
                        "gui.config.name.general.detect_biome_data",
                        CraftPresence.CONFIG.detectBiomeData,
                        null,
                        () -> CraftPresence.GUIS.drawMultiLineString(
                                StringUtils.splitTextByNewLine(
                                        ModUtils.TRANSLATOR.translate("gui.config.comment.general.detect_biome_data")
                                ),
                                getMouseX(), getMouseY(),
                                getScreenWidth(), getScreenHeight(),
                                getWrapWidth(),
                                getFontRenderer(),
                                true
                        )
                )
        );
        detectDimensionDataButton = addControl(
                new CheckBoxControl(
                        checkboxCalc1, CraftPresence.GUIS.getButtonY(6, -30),
                        "gui.config.name.general.detect_dimension_data",
                        CraftPresence.CONFIG.detectDimensionData,
                        null,
                        () -> CraftPresence.GUIS.drawMultiLineString(
                                StringUtils.splitTextByNewLine(
                                        ModUtils.TRANSLATOR.translate("gui.config.comment.general.detect_dimension_data")
                                ),
                                getMouseX(), getMouseY(),
                                getScreenWidth(), getScreenHeight(),
                                getWrapWidth(),
                                getFontRenderer(),
                                true
                        )
                )
        );
        detectWorldDataButton = addControl(
                new CheckBoxControl(
                        checkboxCalc2, CraftPresence.GUIS.getButtonY(6, -30),
                        "gui.config.name.general.detect_world_data",
                        CraftPresence.CONFIG.detectWorldData,
                        null,
                        () -> CraftPresence.GUIS.drawMultiLineString(
                                StringUtils.splitTextByNewLine(
                                        ModUtils.TRANSLATOR.translate("gui.config.comment.general.detect_world_data")
                                ),
                                getMouseX(), getMouseY(),
                                getScreenWidth(), getScreenHeight(),
                                getWrapWidth(),
                                getFontRenderer(),
                                true
                        )
                )
        );
        enableJoinRequestButton = addControl(
                new CheckBoxControl(
                        checkboxCalc1, CraftPresence.GUIS.getButtonY(7, -40),
                        "gui.config.name.general.enable_join_request",
                        CraftPresence.CONFIG.enableJoinRequest,
                        null,
                        () -> CraftPresence.GUIS.drawMultiLineString(
                                StringUtils.splitTextByNewLine(
                                        ModUtils.TRANSLATOR.translate("gui.config.comment.general.enable_join_request")
                                ),
                                getMouseX(), getMouseY(),
                                getScreenWidth(), getScreenHeight(),
                                getWrapWidth(),
                                getFontRenderer(),
                                true
                        )
                )
        );
        resetTimeOnInitButton = addControl(
                new CheckBoxControl(
                        checkboxCalc2, CraftPresence.GUIS.getButtonY(7, -40),
                        "gui.config.name.general.reset_time_on_init",
                        CraftPresence.CONFIG.resetTimeOnInit,
                        null,
                        () -> CraftPresence.GUIS.drawMultiLineString(
                                StringUtils.splitTextByNewLine(
                                        ModUtils.TRANSLATOR.translate("gui.config.comment.general.reset_time_on_init")
                                ),
                                getMouseX(), getMouseY(),
                                getScreenWidth(), getScreenHeight(),
                                getWrapWidth(),
                                getFontRenderer(),
                                true
                        )
                )
        );
        autoRegisterButton = addControl(
                new CheckBoxControl(
                        checkboxCalc1, CraftPresence.GUIS.getButtonY(8, -50),
                        "gui.config.name.general.auto_register",
                        CraftPresence.CONFIG.autoRegister,
                        null,
                        () -> CraftPresence.GUIS.drawMultiLineString(
                                StringUtils.splitTextByNewLine(
                                        ModUtils.TRANSLATOR.translate("gui.config.comment.general.auto_register")
                                ),
                                getMouseX(), getMouseY(),
                                getScreenWidth(), getScreenHeight(),
                                getWrapWidth(),
                                getFontRenderer(),
                                true
                        )
                )
        );
        currentPreferredClient = CraftPresence.CONFIG.preferredClientLevel;
        preferredClientLevelButton = addControl(
                new ExtendedButtonControl(
                        (getScreenWidth() / 2) - 90, (getScreenHeight() - 55),
                        180, 20,
                        CraftPresence.CONFIG.NAME_preferredClientLevel.replaceAll("_", " ") + " => " + DiscordBuild.from(currentPreferredClient).name(),
                        () -> currentPreferredClient = (currentPreferredClient + 1) % DiscordBuild.values().length,
                        () -> CraftPresence.GUIS.drawMultiLineString(
                                StringUtils.splitTextByNewLine(
                                        ModUtils.TRANSLATOR.translate("gui.config.comment.general.preferred_client")
                                ),
                                getMouseX(), getMouseY(),
                                getScreenWidth(), getScreenHeight(),
                                getWrapWidth(),
                                getFontRenderer(),
                                true
                        )
                )
        );
        proceedButton = addControl(
                new ExtendedButtonControl(
                        (getScreenWidth() / 2) - 90, (getScreenHeight() - 30),
                        180, 20,
                        "gui.config.message.button.back",
                        () -> {
                            if (!clientId.getControlMessage().equals(CraftPresence.CONFIG.clientId)) {
                                CraftPresence.CONFIG.hasChanged = true;
                                CraftPresence.CONFIG.hasClientPropertiesChanged = true;
                                CraftPresence.CONFIG.clientId = clientId.getControlMessage();
                            }
                            if (currentPartyPrivacy != CraftPresence.CONFIG.partyPrivacyLevel) {
                                CraftPresence.CONFIG.hasChanged = true;
                                CraftPresence.CONFIG.hasClientPropertiesChanged = true;
                                CraftPresence.CONFIG.partyPrivacyLevel = currentPartyPrivacy;
                            }
                            if (currentPreferredClient != CraftPresence.CONFIG.preferredClientLevel) {
                                CraftPresence.CONFIG.hasChanged = true;
                                CraftPresence.CONFIG.hasClientPropertiesChanged = true;
                                CraftPresence.CONFIG.preferredClientLevel = currentPreferredClient;
                            }
                            if (detectCurseManifestButton.isChecked() != CraftPresence.CONFIG.detectCurseManifest) {
                                CraftPresence.CONFIG.hasChanged = true;
                                CraftPresence.CONFIG.hasClientPropertiesChanged = true;
                                CraftPresence.CONFIG.detectCurseManifest = detectCurseManifestButton.isChecked();
                            }
                            if (detectMultiMCManifestButton.isChecked() != CraftPresence.CONFIG.detectMultiMCManifest) {
                                CraftPresence.CONFIG.hasChanged = true;
                                CraftPresence.CONFIG.hasClientPropertiesChanged = true;
                                CraftPresence.CONFIG.detectMultiMCManifest = detectMultiMCManifestButton.isChecked();
                            }
                            if (detectMCUpdaterInstanceButton.isChecked() != CraftPresence.CONFIG.detectMCUpdaterInstance) {
                                CraftPresence.CONFIG.hasChanged = true;
                                CraftPresence.CONFIG.hasClientPropertiesChanged = true;
                                CraftPresence.CONFIG.detectMCUpdaterInstance = detectMCUpdaterInstanceButton.isChecked();
                            }
                            if (detectTechnicPackButton.isChecked() != CraftPresence.CONFIG.detectTechnicPack) {
                                CraftPresence.CONFIG.hasChanged = true;
                                CraftPresence.CONFIG.hasClientPropertiesChanged = true;
                                CraftPresence.CONFIG.detectTechnicPack = detectTechnicPackButton.isChecked();
                            }
                            if (showTimeButton.isChecked() != CraftPresence.CONFIG.showTime) {
                                CraftPresence.CONFIG.hasChanged = true;
                                CraftPresence.CONFIG.hasClientPropertiesChanged = true;
                                CraftPresence.CONFIG.showTime = showTimeButton.isChecked();
                            }
                            if (detectBiomeDataButton.isChecked() != CraftPresence.CONFIG.detectBiomeData) {
                                CraftPresence.CONFIG.hasChanged = true;
                                CraftPresence.CONFIG.hasClientPropertiesChanged = true;
                                CraftPresence.CONFIG.detectBiomeData = detectBiomeDataButton.isChecked();
                            }
                            if (detectDimensionDataButton.isChecked() != CraftPresence.CONFIG.detectDimensionData) {
                                CraftPresence.CONFIG.hasChanged = true;
                                CraftPresence.CONFIG.hasClientPropertiesChanged = true;
                                CraftPresence.CONFIG.detectDimensionData = detectDimensionDataButton.isChecked();
                            }
                            if (detectWorldDataButton.isChecked() != CraftPresence.CONFIG.detectWorldData) {
                                CraftPresence.CONFIG.hasChanged = true;
                                CraftPresence.CONFIG.hasClientPropertiesChanged = true;
                                CraftPresence.CONFIG.detectWorldData = detectWorldDataButton.isChecked();
                            }
                            if (enableJoinRequestButton.isChecked() != CraftPresence.CONFIG.enableJoinRequest) {
                                CraftPresence.CONFIG.hasChanged = true;
                                CraftPresence.CONFIG.hasClientPropertiesChanged = true;
                                CraftPresence.CONFIG.enableJoinRequest = enableJoinRequestButton.isChecked();
                            }
                            if (resetTimeOnInitButton.isChecked() != CraftPresence.CONFIG.resetTimeOnInit) {
                                CraftPresence.CONFIG.hasChanged = true;
                                CraftPresence.CONFIG.hasClientPropertiesChanged = true;
                                CraftPresence.CONFIG.resetTimeOnInit = resetTimeOnInitButton.isChecked();
                            }
                            if (autoRegisterButton.isChecked() != CraftPresence.CONFIG.autoRegister) {
                                CraftPresence.CONFIG.hasChanged = true;
                                CraftPresence.CONFIG.hasClientPropertiesChanged = true;
                                CraftPresence.CONFIG.autoRegister = autoRegisterButton.isChecked();
                            }
                            CraftPresence.GUIS.openScreen(parentScreen);
                        },
                        () -> {
                            if (!proceedButton.isControlEnabled()) {
                                CraftPresence.GUIS.drawMultiLineString(
                                        StringUtils.splitTextByNewLine(
                                                ModUtils.TRANSLATOR.translate("gui.config.message.hover.empty.default")
                                        ),
                                        getMouseX(), getMouseY(),
                                        getScreenWidth(), getScreenHeight(),
                                        getWrapWidth(),
                                        getFontRenderer(),
                                        true
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

        partyPrivacyLevelButton.setControlMessage(CraftPresence.CONFIG.NAME_partyPrivacyLevel.replaceAll("_", " ") + " => " + PartyPrivacy.from(currentPartyPrivacy).name());
        preferredClientLevelButton.setControlMessage(CraftPresence.CONFIG.NAME_preferredClientLevel.replaceAll("_", " ") + " => " + DiscordBuild.from(currentPreferredClient).name());
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
                    ),
                    getMouseX(), getMouseY(),
                    getScreenWidth(), getScreenHeight(),
                    getWrapWidth(),
                    getFontRenderer(),
                    true
            );
        }
    }
}
