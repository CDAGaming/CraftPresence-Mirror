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
import com.gitlab.cdagaming.craftpresence.config.category.Accessibility;
import com.gitlab.cdagaming.craftpresence.utils.StringUtils;
import com.gitlab.cdagaming.craftpresence.utils.gui.controls.CheckBoxControl;
import com.gitlab.cdagaming.craftpresence.utils.gui.controls.ExtendedButtonControl;
import com.gitlab.cdagaming.craftpresence.utils.gui.controls.ExtendedTextControl;
import com.gitlab.cdagaming.craftpresence.utils.gui.impl.ColorEditorGui;
import com.gitlab.cdagaming.craftpresence.utils.gui.integrations.ExtendedScreen;
import net.minecraft.client.gui.GuiScreen;

@SuppressWarnings("DuplicatedCode")
public class AccessibilitySettingsGui extends ExtendedScreen {

    private Accessibility CONFIG;
    private ExtendedTextControl languageIdText;
    private CheckBoxControl showBackgroundAsDarkButton, stripTranslationColorsButton, showLoggingInChatButton, stripExtraGuiElementsButton;
    private ExtendedButtonControl resetConfigButton, proceedButton;

    AccessibilitySettingsGui(GuiScreen parentScreen) {
        super(parentScreen);
        CONFIG = CraftPresence.CONFIG.accessibilitySettings;
    }

    @Override
    public void initializeUi() {
        final int calc1 = (getScreenWidth() / 2) - 183;
        final int calc2 = (getScreenWidth() / 2) + 3;

        // Adding Tooltip Background Button
        addControl(
                new ExtendedButtonControl(
                        calc1, CraftPresence.GUIS.getButtonY(1),
                        180, 20,
                        "gui.config.name.accessibility.tooltip_background_color",
                        () -> CraftPresence.GUIS.openScreen(
                                new ColorEditorGui(
                                        currentScreen, ModUtils.TRANSLATOR.translate("gui.config.name.accessibility.tooltip_background_color"),
                                        (pageNumber, screenInstance) -> {
                                            if (pageNumber == 0 && !screenInstance.currentNormalHexValue.equals(CONFIG.tooltipBackgroundColor)) {
                                                CraftPresence.CONFIG.hasChanged = true;
                                                CONFIG.tooltipBackgroundColor = screenInstance.currentNormalHexValue;
                                            } else if (pageNumber == 1 && !screenInstance.currentTexturePath.equals(CONFIG.tooltipBackgroundColor)) {
                                                CraftPresence.CONFIG.hasChanged = true;
                                                CONFIG.tooltipBackgroundColor = screenInstance.currentTexturePath;
                                            }
                                        },
                                        (screenInstance) -> {
                                            if (StringUtils.isValidColorCode(CONFIG.tooltipBackgroundColor)) {
                                                screenInstance.startingHexValue = CONFIG.tooltipBackgroundColor;
                                            } else if (!StringUtils.isNullOrEmpty(CONFIG.tooltipBackgroundColor)) {
                                                screenInstance.startingTexturePath = CONFIG.tooltipBackgroundColor;
                                            }
                                        }
                                )
                        ),
                        () -> CraftPresence.GUIS.drawMultiLineString(
                                StringUtils.splitTextByNewLine(
                                        ModUtils.TRANSLATOR.translate("gui.config.comment.accessibility.tooltip_background_color")
                                ), this, true
                        )
                )
        );
        // Adding Tooltip Border Color Button
        addControl(
                new ExtendedButtonControl(
                        calc2, CraftPresence.GUIS.getButtonY(1),
                        180, 20,
                        "gui.config.name.accessibility.tooltip_border_color",
                        () -> CraftPresence.GUIS.openScreen(
                                new ColorEditorGui(
                                        currentScreen, ModUtils.TRANSLATOR.translate("gui.config.name.accessibility.tooltip_border_color"),
                                        (pageNumber, screenInstance) -> {
                                            if (pageNumber == 0 && !screenInstance.currentNormalHexValue.equals(CONFIG.tooltipBorderColor)) {
                                                CraftPresence.CONFIG.hasChanged = true;
                                                CONFIG.tooltipBorderColor = screenInstance.currentNormalHexValue;
                                            } else if (pageNumber == 1 && !screenInstance.currentTexturePath.equals(CONFIG.tooltipBorderColor)) {
                                                CraftPresence.CONFIG.hasChanged = true;
                                                CONFIG.tooltipBorderColor = screenInstance.currentTexturePath;
                                            }
                                        },
                                        (screenInstance) -> {
                                            if (StringUtils.isValidColorCode(CONFIG.tooltipBorderColor)) {
                                                screenInstance.startingHexValue = CONFIG.tooltipBorderColor;
                                            } else if (!StringUtils.isNullOrEmpty(CONFIG.tooltipBorderColor)) {
                                                screenInstance.startingTexturePath = CONFIG.tooltipBorderColor;
                                            }
                                        }
                                )
                        ),
                        () -> CraftPresence.GUIS.drawMultiLineString(
                                StringUtils.splitTextByNewLine(
                                        ModUtils.TRANSLATOR.translate("gui.config.comment.accessibility.tooltip_border_color")
                                ), this, true
                        )
                )
        );
        // Adding Gui Background Color Button
        addControl(
                new ExtendedButtonControl(
                        calc1, CraftPresence.GUIS.getButtonY(2),
                        180, 20,
                        "gui.config.name.accessibility.gui_background_color",
                        () -> CraftPresence.GUIS.openScreen(
                                new ColorEditorGui(
                                        currentScreen, ModUtils.TRANSLATOR.translate("gui.config.name.accessibility.gui_background_color"),
                                        (pageNumber, screenInstance) -> {
                                            if (pageNumber == 0 && !screenInstance.currentNormalHexValue.equals(CONFIG.guiBackgroundColor)) {
                                                CraftPresence.CONFIG.hasChanged = true;
                                                CONFIG.guiBackgroundColor = screenInstance.currentNormalHexValue;
                                            } else if (pageNumber == 1 && !screenInstance.currentTexturePath.equals(CONFIG.guiBackgroundColor)) {
                                                CraftPresence.CONFIG.hasChanged = true;
                                                CONFIG.guiBackgroundColor = screenInstance.currentTexturePath;
                                            }
                                        },
                                        (screenInstance) -> {
                                            if (StringUtils.isValidColorCode(CONFIG.guiBackgroundColor)) {
                                                screenInstance.startingHexValue = CONFIG.guiBackgroundColor;
                                            } else if (!StringUtils.isNullOrEmpty(CONFIG.guiBackgroundColor)) {
                                                screenInstance.startingTexturePath = CONFIG.guiBackgroundColor;
                                            }
                                        }
                                )
                        ),
                        () -> CraftPresence.GUIS.drawMultiLineString(
                                StringUtils.splitTextByNewLine(
                                        ModUtils.TRANSLATOR.translate("gui.config.comment.accessibility.gui_background_color")
                                ), this, true
                        )
                )
        );
        // Adding Button Background Color Button
        addControl(
                new ExtendedButtonControl(
                        calc2, CraftPresence.GUIS.getButtonY(2),
                        180, 20,
                        "gui.config.name.accessibility.button_background_color",
                        () -> CraftPresence.GUIS.openScreen(
                                new ColorEditorGui(
                                        currentScreen, ModUtils.TRANSLATOR.translate("gui.config.name.accessibility.button_background_color"),
                                        (pageNumber, screenInstance) -> {
                                            if (pageNumber == 0 && !screenInstance.currentNormalHexValue.equals(CONFIG.buttonBackgroundColor)) {
                                                CraftPresence.CONFIG.hasChanged = true;
                                                CONFIG.buttonBackgroundColor = screenInstance.currentNormalHexValue;
                                            } else if (pageNumber == 1 && !screenInstance.currentTexturePath.equals(CONFIG.buttonBackgroundColor)) {
                                                CraftPresence.CONFIG.hasChanged = true;
                                                CONFIG.buttonBackgroundColor = screenInstance.currentTexturePath;
                                            }
                                        },
                                        (screenInstance) -> {
                                            if (StringUtils.isValidColorCode(CONFIG.buttonBackgroundColor)) {
                                                screenInstance.startingHexValue = CONFIG.buttonBackgroundColor;
                                            } else if (!StringUtils.isNullOrEmpty(CONFIG.buttonBackgroundColor)) {
                                                screenInstance.startingTexturePath = CONFIG.buttonBackgroundColor;
                                            }
                                        }
                                )
                        ),
                        () -> CraftPresence.GUIS.drawMultiLineString(
                                StringUtils.splitTextByNewLine(
                                        ModUtils.TRANSLATOR.translate("gui.config.comment.accessibility.button_background_color")
                                ), this, true
                        )
                )
        );

        languageIdText = addControl(
                new ExtendedTextControl(
                        getFontRenderer(),
                        calc2, CraftPresence.GUIS.getButtonY(3),
                        180, 20
                )
        );

        showBackgroundAsDarkButton = addControl(
                new CheckBoxControl(
                        calc1, CraftPresence.GUIS.getButtonY(4),
                        "gui.config.name.accessibility.show_background_as_dark",
                        CONFIG.showBackgroundAsDark,
                        null,
                        () -> CraftPresence.GUIS.drawMultiLineString(
                                StringUtils.splitTextByNewLine(
                                        ModUtils.TRANSLATOR.translate("gui.config.comment.accessibility.show_background_as_dark")
                                ), this, true
                        )
                )
        );
        stripTranslationColorsButton = addControl(
                new CheckBoxControl(
                        calc2, CraftPresence.GUIS.getButtonY(4),
                        "gui.config.name.accessibility.strip_translation_colors",
                        CONFIG.stripTranslationColors,
                        null,
                        () -> CraftPresence.GUIS.drawMultiLineString(
                                StringUtils.splitTextByNewLine(
                                        ModUtils.TRANSLATOR.translate("gui.config.comment.accessibility.strip_translation_colors")
                                ), this, true
                        )
                )
        );
        showLoggingInChatButton = addControl(
                new CheckBoxControl(
                        calc1, CraftPresence.GUIS.getButtonY(5, -10),
                        "gui.config.name.accessibility.show_logging_in_chat",
                        CONFIG.showLoggingInChat,
                        null,
                        () -> CraftPresence.GUIS.drawMultiLineString(
                                StringUtils.splitTextByNewLine(
                                        ModUtils.TRANSLATOR.translate("gui.config.comment.accessibility.show_logging_in_chat")
                                ), this, true
                        )
                )
        );
        stripExtraGuiElementsButton = addControl(
                new CheckBoxControl(
                        calc2, CraftPresence.GUIS.getButtonY(5, -10),
                        "gui.config.name.accessibility.strip_extra_gui_elements",
                        CONFIG.stripExtraGuiElements,
                        null,
                        () -> CraftPresence.GUIS.drawMultiLineString(
                                StringUtils.splitTextByNewLine(
                                        ModUtils.TRANSLATOR.translate("gui.config.comment.accessibility.strip_extra_gui_elements")
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
                            if (!languageIdText.getControlMessage().equals(CONFIG.languageId)) {
                                CraftPresence.CONFIG.hasChanged = true;
                                CONFIG.languageId = languageIdText.getControlMessage();
                            }
                            if (showBackgroundAsDarkButton.isChecked() != CONFIG.showBackgroundAsDark) {
                                CraftPresence.CONFIG.hasChanged = true;
                                CONFIG.showBackgroundAsDark = showBackgroundAsDarkButton.isChecked();
                            }
                            if (stripTranslationColorsButton.isChecked() != CONFIG.stripTranslationColors) {
                                CraftPresence.CONFIG.hasChanged = true;
                                CONFIG.stripTranslationColors = stripTranslationColorsButton.isChecked();
                            }
                            if (showLoggingInChatButton.isChecked() != CONFIG.showLoggingInChat) {
                                CraftPresence.CONFIG.hasChanged = true;
                                CONFIG.showLoggingInChat = showLoggingInChatButton.isChecked();
                            }
                            if (stripExtraGuiElementsButton.isChecked() != CONFIG.stripExtraGuiElements) {
                                CraftPresence.CONFIG.hasChanged = true;
                                CONFIG.stripExtraGuiElements = stripExtraGuiElementsButton.isChecked();
                            }
                            CraftPresence.GUIS.openScreen(parentScreen);
                        }
                )
        );
        resetConfigButton = addControl(
                new ExtendedButtonControl(
                        10, (getScreenHeight() - 30),
                        95, 20,
                        "gui.config.message.button.reset",
                        () -> refreshData(CONFIG.getDefaults()),
                        () -> {
                            if (resetConfigButton.isControlEnabled()) {
                                CraftPresence.GUIS.drawMultiLineString(
                                        StringUtils.splitTextByNewLine(
                                                ModUtils.TRANSLATOR.translate("gui.config.comment.button.reset.config")
                                        ), this, true
                                );
                            }
                        }
                )
        );
        refreshData();

        super.initializeUi();
    }

    private void refreshData(final Accessibility newConfig) {
        if (newConfig != null) {
            CONFIG = newConfig;
        }
        languageIdText.setControlMessage(CONFIG.languageId);
        showBackgroundAsDarkButton.setIsChecked(CONFIG.showBackgroundAsDark);
        stripTranslationColorsButton.setIsChecked(CONFIG.stripTranslationColors);
        showLoggingInChatButton.setIsChecked(CONFIG.showLoggingInChat);
        stripExtraGuiElementsButton.setIsChecked(CONFIG.stripExtraGuiElements);
    }

    private void refreshData() {
        refreshData(null);
    }

    @Override
    public void preRender() {
        final String mainTitle = ModUtils.TRANSLATOR.translate("gui.config.title");
        final String subTitle = ModUtils.TRANSLATOR.translate("gui.config.title.accessibility");

        final String languageIdTitle = ModUtils.TRANSLATOR.translate("gui.config.name.accessibility.language_id");

        renderString(mainTitle, (getScreenWidth() / 2f) - (getStringWidth(mainTitle) / 2f), 10, 0xFFFFFF);
        renderString(subTitle, (getScreenWidth() / 2f) - (getStringWidth(subTitle) / 2f), 20, 0xFFFFFF);

        renderString(languageIdTitle, (getScreenWidth() / 2f) - 130, CraftPresence.GUIS.getButtonY(3, 5), 0xFFFFFF);

        //noinspection ConstantConditions
        stripExtraGuiElementsButton.setControlEnabled(!ModUtils.IS_LEGACY_HARD);
        proceedButton.setControlEnabled(!StringUtils.isNullOrEmpty(languageIdText.getControlMessage()));
        resetConfigButton.setControlEnabled(!CONFIG.isDefaults());

        super.preRender();
    }

    @Override
    public void postRender() {
        final String languageIdTitle = ModUtils.TRANSLATOR.translate("gui.config.name.accessibility.language_id");

        // Hovering over Language Id Label
        if (CraftPresence.GUIS.isMouseOver(getMouseX(), getMouseY(), (getScreenWidth() / 2f) - 130, CraftPresence.GUIS.getButtonY(3, 5), getStringWidth(languageIdTitle), getFontHeight())) {
            CraftPresence.GUIS.drawMultiLineString(
                    StringUtils.splitTextByNewLine(
                            ModUtils.TRANSLATOR.translate("gui.config.comment.accessibility.language_id")
                    ), this, true
            );
        }

        super.postRender();
    }

    @Override
    protected void keyTyped(char typedChar, int keyCode) {
        super.keyTyped(typedChar, keyCode);
    }
}
