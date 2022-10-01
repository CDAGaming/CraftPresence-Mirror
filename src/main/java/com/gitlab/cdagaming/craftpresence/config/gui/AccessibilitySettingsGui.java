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
import com.gitlab.cdagaming.craftpresence.utils.StringUtils;
import com.gitlab.cdagaming.craftpresence.utils.gui.controls.CheckBoxControl;
import com.gitlab.cdagaming.craftpresence.utils.gui.controls.ExtendedButtonControl;
import com.gitlab.cdagaming.craftpresence.utils.gui.controls.ExtendedTextControl;
import com.gitlab.cdagaming.craftpresence.utils.gui.impl.ColorEditorGui;
import com.gitlab.cdagaming.craftpresence.utils.gui.integrations.ExtendedScreen;
import net.minecraft.client.gui.GuiScreen;

public class AccessibilitySettingsGui extends ExtendedScreen {

    private ExtendedTextControl languageIdText;
    private CheckBoxControl showBackgroundAsDarkButton, stripTranslationColorsButton, showLoggingInChatButton, stripExtraGuiElementsButton;
    private ExtendedButtonControl proceedButton;

    AccessibilitySettingsGui(GuiScreen parentScreen) {
        super(parentScreen);
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
                                            if (pageNumber == 0 && !screenInstance.currentNormalHexValue.equals(CraftPresence.CONFIG.tooltipBackgroundColor)) {
                                                CraftPresence.CONFIG.hasChanged = true;
                                                CraftPresence.CONFIG.tooltipBackgroundColor = screenInstance.currentNormalHexValue;
                                            } else if (pageNumber == 1) {
                                                final String adjustValue = screenInstance.usingExternalTexture ? CraftPresence.CONFIG.tooltipBackgroundColor : CraftPresence.CONFIG.tooltipBackgroundColor.replace(CraftPresence.CONFIG.splitCharacter, ":");
                                                if (!screenInstance.currentNormalTexturePath.equals(adjustValue)) {
                                                    CraftPresence.CONFIG.hasChanged = true;
                                                    CraftPresence.CONFIG.tooltipBackgroundColor = adjustValue;
                                                }
                                            }
                                        },
                                        (screenInstance) -> {
                                            if (StringUtils.isValidColorCode(CraftPresence.CONFIG.tooltipBackgroundColor)) {
                                                screenInstance.startingHexValue = CraftPresence.CONFIG.tooltipBackgroundColor;
                                            } else if (!StringUtils.isNullOrEmpty(CraftPresence.CONFIG.tooltipBackgroundColor)) {
                                                screenInstance.startingTexturePath = CraftPresence.CONFIG.tooltipBackgroundColor;
                                            }
                                        }
                                )
                        ),
                        () -> CraftPresence.GUIS.drawMultiLineString(
                                StringUtils.splitTextByNewLine(
                                        ModUtils.TRANSLATOR.translate("gui.config.comment.accessibility.tooltip_background_color")
                                ),
                                getMouseX(), getMouseY(),
                                getScreenWidth(), getScreenHeight(),
                                getWrapWidth(),
                                getFontRenderer(),
                                true
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
                                            if (pageNumber == 0 && !screenInstance.currentNormalHexValue.equals(CraftPresence.CONFIG.tooltipBorderColor)) {
                                                CraftPresence.CONFIG.hasChanged = true;
                                                CraftPresence.CONFIG.tooltipBorderColor = screenInstance.currentNormalHexValue;
                                            } else if (pageNumber == 1) {
                                                final String adjustValue = screenInstance.usingExternalTexture ? CraftPresence.CONFIG.tooltipBorderColor : CraftPresence.CONFIG.tooltipBorderColor.replace(CraftPresence.CONFIG.splitCharacter, ":");
                                                if (!screenInstance.currentNormalTexturePath.equals(adjustValue)) {
                                                    CraftPresence.CONFIG.hasChanged = true;
                                                    CraftPresence.CONFIG.tooltipBorderColor = adjustValue;
                                                }
                                            }
                                        },
                                        (screenInstance) -> {
                                            if (StringUtils.isValidColorCode(CraftPresence.CONFIG.tooltipBorderColor)) {
                                                screenInstance.startingHexValue = CraftPresence.CONFIG.tooltipBorderColor;
                                            } else if (!StringUtils.isNullOrEmpty(CraftPresence.CONFIG.tooltipBorderColor)) {
                                                screenInstance.startingTexturePath = CraftPresence.CONFIG.tooltipBorderColor;
                                            }
                                        }
                                )
                        ),
                        () -> CraftPresence.GUIS.drawMultiLineString(
                                StringUtils.splitTextByNewLine(
                                        ModUtils.TRANSLATOR.translate("gui.config.comment.accessibility.tooltip_border_color")
                                ),
                                getMouseX(), getMouseY(),
                                getScreenWidth(), getScreenHeight(),
                                getWrapWidth(),
                                getFontRenderer(),
                                true
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
                                            if (pageNumber == 0 && !screenInstance.currentNormalHexValue.equals(CraftPresence.CONFIG.guiBackgroundColor)) {
                                                CraftPresence.CONFIG.hasChanged = true;
                                                CraftPresence.CONFIG.guiBackgroundColor = screenInstance.currentNormalHexValue;
                                            } else if (pageNumber == 1) {
                                                final String adjustValue = screenInstance.usingExternalTexture ? CraftPresence.CONFIG.guiBackgroundColor : CraftPresence.CONFIG.guiBackgroundColor.replace(CraftPresence.CONFIG.splitCharacter, ":");
                                                if (!screenInstance.currentNormalTexturePath.equals(adjustValue)) {
                                                    CraftPresence.CONFIG.hasChanged = true;
                                                    CraftPresence.CONFIG.guiBackgroundColor = adjustValue;
                                                }
                                            }
                                        },
                                        (screenInstance) -> {
                                            if (StringUtils.isValidColorCode(CraftPresence.CONFIG.guiBackgroundColor)) {
                                                screenInstance.startingHexValue = CraftPresence.CONFIG.guiBackgroundColor;
                                            } else if (!StringUtils.isNullOrEmpty(CraftPresence.CONFIG.guiBackgroundColor)) {
                                                screenInstance.startingTexturePath = CraftPresence.CONFIG.guiBackgroundColor;
                                            }
                                        }
                                )
                        ),
                        () -> CraftPresence.GUIS.drawMultiLineString(
                                StringUtils.splitTextByNewLine(
                                        ModUtils.TRANSLATOR.translate("gui.config.comment.accessibility.gui_background_color")
                                ),
                                getMouseX(), getMouseY(),
                                getScreenWidth(), getScreenHeight(),
                                getWrapWidth(),
                                getFontRenderer(),
                                true
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
                                            if (pageNumber == 0 && !screenInstance.currentNormalHexValue.equals(CraftPresence.CONFIG.buttonBackgroundColor)) {
                                                CraftPresence.CONFIG.hasChanged = true;
                                                CraftPresence.CONFIG.buttonBackgroundColor = screenInstance.currentNormalHexValue;
                                            } else if (pageNumber == 1) {
                                                final String adjustValue = screenInstance.usingExternalTexture ? CraftPresence.CONFIG.buttonBackgroundColor : CraftPresence.CONFIG.buttonBackgroundColor.replace(CraftPresence.CONFIG.splitCharacter, ":");
                                                if (!screenInstance.currentNormalTexturePath.equals(adjustValue)) {
                                                    CraftPresence.CONFIG.hasChanged = true;
                                                    CraftPresence.CONFIG.buttonBackgroundColor = adjustValue;
                                                }
                                            }
                                        },
                                        (screenInstance) -> {
                                            if (StringUtils.isValidColorCode(CraftPresence.CONFIG.buttonBackgroundColor)) {
                                                screenInstance.startingHexValue = CraftPresence.CONFIG.buttonBackgroundColor;
                                            } else if (!StringUtils.isNullOrEmpty(CraftPresence.CONFIG.buttonBackgroundColor)) {
                                                screenInstance.startingTexturePath = CraftPresence.CONFIG.buttonBackgroundColor;
                                            }
                                        }
                                )
                        ),
                        () -> CraftPresence.GUIS.drawMultiLineString(
                                StringUtils.splitTextByNewLine(
                                        ModUtils.TRANSLATOR.translate("gui.config.comment.accessibility.button_background_color")
                                ),
                                getMouseX(), getMouseY(),
                                getScreenWidth(), getScreenHeight(),
                                getWrapWidth(),
                                getFontRenderer(),
                                true
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
        languageIdText.setControlMessage(CraftPresence.CONFIG.languageId);

        showBackgroundAsDarkButton = addControl(
                new CheckBoxControl(
                        calc1, CraftPresence.GUIS.getButtonY(4),
                        "gui.config.name.accessibility.show_background_as_dark",
                        CraftPresence.CONFIG.showBackgroundAsDark,
                        null,
                        () -> CraftPresence.GUIS.drawMultiLineString(
                                StringUtils.splitTextByNewLine(
                                        ModUtils.TRANSLATOR.translate("gui.config.comment.accessibility.show_background_as_dark")
                                ),
                                getMouseX(), getMouseY(),
                                getScreenWidth(), getScreenHeight(),
                                getWrapWidth(),
                                getFontRenderer(),
                                true
                        )
                )
        );
        stripTranslationColorsButton = addControl(
                new CheckBoxControl(
                        calc2, CraftPresence.GUIS.getButtonY(4),
                        "gui.config.name.accessibility.strip_translation_colors",
                        CraftPresence.CONFIG.stripTranslationColors,
                        null,
                        () -> CraftPresence.GUIS.drawMultiLineString(
                                StringUtils.splitTextByNewLine(
                                        ModUtils.TRANSLATOR.translate("gui.config.comment.accessibility.strip_translation_colors")
                                ),
                                getMouseX(), getMouseY(),
                                getScreenWidth(), getScreenHeight(),
                                getWrapWidth(),
                                getFontRenderer(),
                                true
                        )
                )
        );
        showLoggingInChatButton = addControl(
                new CheckBoxControl(
                        calc1, CraftPresence.GUIS.getButtonY(5, -10),
                        "gui.config.name.accessibility.show_logging_in_chat",
                        CraftPresence.CONFIG.showLoggingInChat,
                        null,
                        () -> CraftPresence.GUIS.drawMultiLineString(
                                StringUtils.splitTextByNewLine(
                                        ModUtils.TRANSLATOR.translate("gui.config.comment.accessibility.show_logging_in_chat")
                                ),
                                getMouseX(), getMouseY(),
                                getScreenWidth(), getScreenHeight(),
                                getWrapWidth(),
                                getFontRenderer(),
                                true
                        )
                )
        );
        stripExtraGuiElementsButton = addControl(
                new CheckBoxControl(
                        calc2, CraftPresence.GUIS.getButtonY(5, -10),
                        "gui.config.name.accessibility.strip_extra_gui_elements",
                        CraftPresence.CONFIG.stripExtraGuiElements,
                        null,
                        () -> CraftPresence.GUIS.drawMultiLineString(
                                StringUtils.splitTextByNewLine(
                                        ModUtils.TRANSLATOR.translate("gui.config.comment.accessibility.strip_extra_gui_elements")
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
                        10, (getScreenHeight() - 30),
                        95, 20,
                        "gui.config.message.button.back",
                        () -> {
                            if (!languageIdText.getControlMessage().equals(CraftPresence.CONFIG.languageId)) {
                                CraftPresence.CONFIG.hasChanged = true;
                                CraftPresence.CONFIG.languageId = languageIdText.getControlMessage();
                            }
                            if (showBackgroundAsDarkButton.isChecked() != CraftPresence.CONFIG.showBackgroundAsDark) {
                                CraftPresence.CONFIG.hasChanged = true;
                                CraftPresence.CONFIG.showBackgroundAsDark = showBackgroundAsDarkButton.isChecked();
                            }
                            if (stripTranslationColorsButton.isChecked() != CraftPresence.CONFIG.stripTranslationColors) {
                                CraftPresence.CONFIG.hasChanged = true;
                                CraftPresence.CONFIG.stripTranslationColors = stripTranslationColorsButton.isChecked();
                            }
                            if (showLoggingInChatButton.isChecked() != CraftPresence.CONFIG.showLoggingInChat) {
                                CraftPresence.CONFIG.hasChanged = true;
                                CraftPresence.CONFIG.showLoggingInChat = showLoggingInChatButton.isChecked();
                            }
                            if (stripExtraGuiElementsButton.isChecked() != CraftPresence.CONFIG.stripExtraGuiElements) {
                                CraftPresence.CONFIG.hasChanged = true;
                                CraftPresence.CONFIG.stripExtraGuiElements = stripExtraGuiElementsButton.isChecked();
                            }
                            CraftPresence.GUIS.openScreen(parentScreen);
                        }
                )
        );

        super.initializeUi();
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
    }

    @Override
    public void postRender() {
        final String languageIdTitle = ModUtils.TRANSLATOR.translate("gui.config.name.accessibility.language_id");

        // Hovering over Language Id Label
        if (CraftPresence.GUIS.isMouseOver(getMouseX(), getMouseY(), (getScreenWidth() / 2f) - 130, CraftPresence.GUIS.getButtonY(3, 5), getStringWidth(languageIdTitle), getFontHeight())) {
            CraftPresence.GUIS.drawMultiLineString(
                    StringUtils.splitTextByNewLine(
                            ModUtils.TRANSLATOR.translate("gui.config.comment.accessibility.language_id")
                    ),
                    getMouseX(), getMouseY(),
                    getScreenWidth(), getScreenHeight(),
                    getWrapWidth(),
                    getFontRenderer(),
                    true
            );
        }
    }

    @Override
    protected void keyTyped(char typedChar, int keyCode) {
        super.keyTyped(typedChar, keyCode);
    }
}
