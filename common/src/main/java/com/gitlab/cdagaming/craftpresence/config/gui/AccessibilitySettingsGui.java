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
import com.gitlab.cdagaming.craftpresence.utils.gui.impl.ConfigurationGui;
import com.gitlab.cdagaming.craftpresence.utils.gui.widgets.TextWidget;
import net.minecraft.client.gui.GuiScreen;

public class AccessibilitySettingsGui extends ConfigurationGui<Accessibility> {

    private final Accessibility INSTANCE;
    private ExtendedTextControl languageIdText;
    private CheckBoxControl showBackgroundAsDarkButton, stripTranslationColorsButton, showLoggingInChatButton, stripExtraGuiElementsButton;

    AccessibilitySettingsGui(GuiScreen parentScreen) {
        super(parentScreen, "gui.config.title", "gui.config.title.accessibility");
        INSTANCE = getCurrentData().copy();
    }

    @Override
    protected void appendControls() {
        super.appendControls();

        final int calc1 = (getScreenWidth() / 2) - 183;
        final int calc2 = (getScreenWidth() / 2) + 3;

        // Adding Tooltip Background Button
        childFrame.addControl(
                new ExtendedButtonControl(
                        calc1, CraftPresence.GUIS.getButtonY(0),
                        180, 20,
                        "gui.config.name.accessibility.tooltip_background_color",
                        () -> CraftPresence.GUIS.openScreen(
                                new ColorEditorGui(
                                        currentScreen, ModUtils.TRANSLATOR.translate("gui.config.name.accessibility.tooltip_background_color"),
                                        (pageNumber, screenInstance) -> {
                                            if (pageNumber == 0 && !screenInstance.currentNormalHexValue.equals(getCurrentData().tooltipBackgroundColor)) {
                                                CraftPresence.CONFIG.hasChanged = true;
                                                getCurrentData().tooltipBackgroundColor = screenInstance.currentNormalHexValue;
                                            } else if (pageNumber == 1 && !screenInstance.currentTexturePath.equals(getCurrentData().tooltipBackgroundColor)) {
                                                CraftPresence.CONFIG.hasChanged = true;
                                                getCurrentData().tooltipBackgroundColor = screenInstance.currentTexturePath;
                                            }
                                        },
                                        (screenInstance) -> {
                                            if (StringUtils.isValidColorCode(getCurrentData().tooltipBackgroundColor)) {
                                                screenInstance.startingHexValue = getCurrentData().tooltipBackgroundColor;
                                            } else if (!StringUtils.isNullOrEmpty(getCurrentData().tooltipBackgroundColor)) {
                                                screenInstance.startingTexturePath = getCurrentData().tooltipBackgroundColor;
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
        childFrame.addControl(
                new ExtendedButtonControl(
                        calc2, CraftPresence.GUIS.getButtonY(0),
                        180, 20,
                        "gui.config.name.accessibility.tooltip_border_color",
                        () -> CraftPresence.GUIS.openScreen(
                                new ColorEditorGui(
                                        currentScreen, ModUtils.TRANSLATOR.translate("gui.config.name.accessibility.tooltip_border_color"),
                                        (pageNumber, screenInstance) -> {
                                            if (pageNumber == 0 && !screenInstance.currentNormalHexValue.equals(getCurrentData().tooltipBorderColor)) {
                                                CraftPresence.CONFIG.hasChanged = true;
                                                getCurrentData().tooltipBorderColor = screenInstance.currentNormalHexValue;
                                            } else if (pageNumber == 1 && !screenInstance.currentTexturePath.equals(getCurrentData().tooltipBorderColor)) {
                                                CraftPresence.CONFIG.hasChanged = true;
                                                getCurrentData().tooltipBorderColor = screenInstance.currentTexturePath;
                                            }
                                        },
                                        (screenInstance) -> {
                                            if (StringUtils.isValidColorCode(getCurrentData().tooltipBorderColor)) {
                                                screenInstance.startingHexValue = getCurrentData().tooltipBorderColor;
                                            } else if (!StringUtils.isNullOrEmpty(getCurrentData().tooltipBorderColor)) {
                                                screenInstance.startingTexturePath = getCurrentData().tooltipBorderColor;
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
        childFrame.addControl(
                new ExtendedButtonControl(
                        calc1, CraftPresence.GUIS.getButtonY(1),
                        180, 20,
                        "gui.config.name.accessibility.gui_background_color",
                        () -> CraftPresence.GUIS.openScreen(
                                new ColorEditorGui(
                                        currentScreen, ModUtils.TRANSLATOR.translate("gui.config.name.accessibility.gui_background_color"),
                                        (pageNumber, screenInstance) -> {
                                            if (pageNumber == 0 && !screenInstance.currentNormalHexValue.equals(getCurrentData().guiBackgroundColor)) {
                                                CraftPresence.CONFIG.hasChanged = true;
                                                getCurrentData().guiBackgroundColor = screenInstance.currentNormalHexValue;
                                            } else if (pageNumber == 1 && !screenInstance.currentTexturePath.equals(getCurrentData().guiBackgroundColor)) {
                                                CraftPresence.CONFIG.hasChanged = true;
                                                getCurrentData().guiBackgroundColor = screenInstance.currentTexturePath;
                                            }
                                        },
                                        (screenInstance) -> {
                                            if (StringUtils.isValidColorCode(getCurrentData().guiBackgroundColor)) {
                                                screenInstance.startingHexValue = getCurrentData().guiBackgroundColor;
                                            } else if (!StringUtils.isNullOrEmpty(getCurrentData().guiBackgroundColor)) {
                                                screenInstance.startingTexturePath = getCurrentData().guiBackgroundColor;
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
        childFrame.addControl(
                new ExtendedButtonControl(
                        calc2, CraftPresence.GUIS.getButtonY(1),
                        180, 20,
                        "gui.config.name.accessibility.button_background_color",
                        () -> CraftPresence.GUIS.openScreen(
                                new ColorEditorGui(
                                        currentScreen, ModUtils.TRANSLATOR.translate("gui.config.name.accessibility.button_background_color"),
                                        (pageNumber, screenInstance) -> {
                                            if (pageNumber == 0 && !screenInstance.currentNormalHexValue.equals(getCurrentData().buttonBackgroundColor)) {
                                                CraftPresence.CONFIG.hasChanged = true;
                                                getCurrentData().buttonBackgroundColor = screenInstance.currentNormalHexValue;
                                            } else if (pageNumber == 1 && !screenInstance.currentTexturePath.equals(getCurrentData().buttonBackgroundColor)) {
                                                CraftPresence.CONFIG.hasChanged = true;
                                                getCurrentData().buttonBackgroundColor = screenInstance.currentTexturePath;
                                            }
                                        },
                                        (screenInstance) -> {
                                            if (StringUtils.isValidColorCode(getCurrentData().buttonBackgroundColor)) {
                                                screenInstance.startingHexValue = getCurrentData().buttonBackgroundColor;
                                            } else if (!StringUtils.isNullOrEmpty(getCurrentData().buttonBackgroundColor)) {
                                                screenInstance.startingTexturePath = getCurrentData().buttonBackgroundColor;
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

        languageIdText = childFrame.addControl(
                new TextWidget(
                        getFontRenderer(),
                        CraftPresence.GUIS.getButtonY(2),
                        180, 20,
                        "gui.config.name.accessibility.language_id",
                        () -> CraftPresence.GUIS.drawMultiLineString(
                                StringUtils.splitTextByNewLine(
                                        ModUtils.TRANSLATOR.translate("gui.config.comment.accessibility.language_id")
                                ), this, true
                        )
                )
        );
        languageIdText.setControlMessage(getCurrentData().languageId);

        showBackgroundAsDarkButton = childFrame.addControl(
                new CheckBoxControl(
                        calc1, CraftPresence.GUIS.getButtonY(3),
                        "gui.config.name.accessibility.show_background_as_dark",
                        getCurrentData().showBackgroundAsDark,
                        null,
                        () -> CraftPresence.GUIS.drawMultiLineString(
                                StringUtils.splitTextByNewLine(
                                        ModUtils.TRANSLATOR.translate("gui.config.comment.accessibility.show_background_as_dark")
                                ), this, true
                        )
                )
        );
        stripTranslationColorsButton = childFrame.addControl(
                new CheckBoxControl(
                        calc2, CraftPresence.GUIS.getButtonY(3),
                        "gui.config.name.accessibility.strip_translation_colors",
                        getCurrentData().stripTranslationColors,
                        null,
                        () -> CraftPresence.GUIS.drawMultiLineString(
                                StringUtils.splitTextByNewLine(
                                        ModUtils.TRANSLATOR.translate("gui.config.comment.accessibility.strip_translation_colors")
                                ), this, true
                        )
                )
        );
        showLoggingInChatButton = childFrame.addControl(
                new CheckBoxControl(
                        calc1, CraftPresence.GUIS.getButtonY(4, -10),
                        "gui.config.name.accessibility.show_logging_in_chat",
                        getCurrentData().showLoggingInChat,
                        null,
                        () -> CraftPresence.GUIS.drawMultiLineString(
                                StringUtils.splitTextByNewLine(
                                        ModUtils.TRANSLATOR.translate("gui.config.comment.accessibility.show_logging_in_chat")
                                ), this, true
                        )
                )
        );
        stripExtraGuiElementsButton = childFrame.addControl(
                new CheckBoxControl(
                        calc2, CraftPresence.GUIS.getButtonY(4, -10),
                        "gui.config.name.accessibility.strip_extra_gui_elements",
                        getCurrentData().stripExtraGuiElements,
                        null,
                        () -> CraftPresence.GUIS.drawMultiLineString(
                                StringUtils.splitTextByNewLine(
                                        ModUtils.TRANSLATOR.translate("gui.config.comment.accessibility.strip_extra_gui_elements")
                                ), this, true
                        )
                )
        );
    }

    @Override
    protected void syncRenderStates() {
        super.syncRenderStates();

        //noinspection ConstantConditions
        stripExtraGuiElementsButton.setControlEnabled(!ModUtils.IS_LEGACY_HARD);
        proceedButton.setControlEnabled(!StringUtils.isNullOrEmpty(languageIdText.getControlMessage()));
    }

    @Override
    protected void applySettings() {
        if (!languageIdText.getControlMessage().equals(getCurrentData().languageId)) {
            CraftPresence.CONFIG.hasChanged = true;
            getCurrentData().languageId = languageIdText.getControlMessage();
        }
        if (showBackgroundAsDarkButton.isChecked() != getCurrentData().showBackgroundAsDark) {
            CraftPresence.CONFIG.hasChanged = true;
            getCurrentData().showBackgroundAsDark = showBackgroundAsDarkButton.isChecked();
        }
        if (stripTranslationColorsButton.isChecked() != getCurrentData().stripTranslationColors) {
            CraftPresence.CONFIG.hasChanged = true;
            getCurrentData().stripTranslationColors = stripTranslationColorsButton.isChecked();
        }
        if (showLoggingInChatButton.isChecked() != getCurrentData().showLoggingInChat) {
            CraftPresence.CONFIG.hasChanged = true;
            getCurrentData().showLoggingInChat = showLoggingInChatButton.isChecked();
        }
        if (stripExtraGuiElementsButton.isChecked() != getCurrentData().stripExtraGuiElements) {
            CraftPresence.CONFIG.hasChanged = true;
            getCurrentData().stripExtraGuiElements = stripExtraGuiElementsButton.isChecked();
        }
    }

    @Override
    protected Accessibility getOriginalData() {
        return INSTANCE;
    }

    @Override
    protected Accessibility getCurrentData() {
        return CraftPresence.CONFIG.accessibilitySettings;
    }

    @Override
    protected void setCurrentData(Accessibility data) {
        CraftPresence.CONFIG.accessibilitySettings = data;
    }
}
