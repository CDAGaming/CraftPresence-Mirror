/*
 * MIT License
 *
 * Copyright (c) 2018 - 2026 CDAGaming (cstack2011@yahoo.com)
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
import com.gitlab.cdagaming.craftpresence.core.config.category.Accessibility;
import com.gitlab.cdagaming.craftpresence.utils.gui.impl.ConfigurationGui;
import com.gitlab.cdagaming.unilib.core.CoreUtils;
import com.gitlab.cdagaming.unilib.utils.KeyUtils;
import com.gitlab.cdagaming.unilib.utils.gui.controls.CheckBoxControl;
import com.gitlab.cdagaming.unilib.utils.gui.controls.ExtendedButtonControl;
import com.gitlab.cdagaming.unilib.utils.gui.controls.ExtendedTextControl;
import com.gitlab.cdagaming.unilib.utils.gui.impl.ControlsGui;
import com.gitlab.cdagaming.unilib.utils.gui.widgets.TextWidget;
import io.github.cdagaming.unicore.utils.StringUtils;

public class AccessibilitySettingsGui extends ConfigurationGui<Accessibility> {
    private final Accessibility INSTANCE, DEFAULTS;
    private final boolean isTextFormattingBlocked;
    private ExtendedTextControl languageIdText;
    private CheckBoxControl stripTranslationColorsButton, stripTranslationFormattingButton,
            stripExtraGuiElementsButton;
    private ExtendedButtonControl controlsButton;

    AccessibilitySettingsGui() {
        super(
                Constants.TRANSLATOR.translate("gui.config.title"),
                Constants.TRANSLATOR.translate("gui.config.title.accessibility")
        );
        DEFAULTS = getCurrentData().getDefaults();
        INSTANCE = getCurrentData().copy();
        isTextFormattingBlocked = CoreUtils.isTextFormattingBlocked(getProtocol());
    }

    @Override
    public void appendElements() {
        super.appendElements();

        final int calc1 = (getScreenWidth() / 2) - 183;
        final int calc2 = (getScreenWidth() / 2) + 3;

        languageIdText = childFrame.addControl(
                new TextWidget(
                        getFontRenderer(),
                        getButtonY(0),
                        180, 20,
                        () -> getInstanceData().languageId = languageIdText.getControlMessage(),
                        Constants.TRANSLATOR.translate("gui.config.name.accessibility.language_id"),
                        () -> drawMultiLineString(
                                StringUtils.splitTextByNewLine(
                                        Constants.TRANSLATOR.translate("gui.config.comment.accessibility.language_id")
                                )
                        )
                )
        );
        languageIdText.setControlMessage(getInstanceData().languageId);

        stripTranslationColorsButton = childFrame.addControl(
                new CheckBoxControl(
                        calc1, getButtonY(1),
                        Constants.TRANSLATOR.translate("gui.config.name.accessibility.strip_translation_colors"),
                        getInstanceData().stripTranslationColors,
                        () -> getInstanceData().stripTranslationColors = stripTranslationColorsButton.isChecked(),
                        () -> drawMultiLineString(
                                StringUtils.splitTextByNewLine(
                                        Constants.TRANSLATOR.translate("gui.config.comment.accessibility.strip_translation_colors")
                                )
                        )
                )
        );
        stripTranslationFormattingButton = childFrame.addControl(
                new CheckBoxControl(
                        calc1, getButtonY(2, -10),
                        Constants.TRANSLATOR.translate("gui.config.name.accessibility.strip_translation_formatting"),
                        getInstanceData().stripTranslationFormatting,
                        () -> getInstanceData().stripTranslationFormatting = stripTranslationFormattingButton.isChecked(),
                        () -> {
                            if (stripTranslationFormattingButton.isControlEnabled()) {
                                drawMultiLineString(
                                        StringUtils.splitTextByNewLine(
                                                Constants.TRANSLATOR.translate("gui.config.comment.accessibility.strip_translation_formatting")
                                        )
                                );
                            } else if (isTextFormattingBlocked) {
                                drawMultiLineString(
                                        StringUtils.splitTextByNewLine(
                                                Constants.TRANSLATOR.translate("craftpresence.message.unsupported")
                                        )
                                );
                            }
                        }
                )
        );
        stripTranslationFormattingButton.setControlEnabled(!isTextFormattingBlocked);
        stripExtraGuiElementsButton = childFrame.addControl(
                new CheckBoxControl(
                        calc1, getButtonY(3, -20),
                        Constants.TRANSLATOR.translate("gui.config.name.accessibility.strip_extra_gui_elements"),
                        getInstanceData().stripExtraGuiElements,
                        () -> getInstanceData().stripExtraGuiElements = stripExtraGuiElementsButton.isChecked(),
                        () -> drawMultiLineString(
                                StringUtils.splitTextByNewLine(
                                        Constants.TRANSLATOR.translate("gui.config.comment.accessibility.strip_extra_gui_elements")
                                )
                        )
                )
        );

        // Adding Controls Button
        controlsButton = childFrame.addControl(
                new ExtendedButtonControl(
                        calc2, getButtonY(1),
                        180, 20,
                        "Controls",
                        () -> openScreen(
                                new ControlsGui(
                                        KeyUtils.INSTANCE,
                                        this::markAsChanged,
                                        "key.craftpresence.category"
                                )
                        )
                )
        );
    }

    @Override
    protected boolean allowedToReset() {
        return true;
    }

    @Override
    protected void syncRenderStates() {
        super.syncRenderStates();

        controlsButton.setControlEnabled(KeyUtils.INSTANCE.areKeysRegistered());
        proceedButton.setControlEnabled(!StringUtils.isNullOrEmpty(languageIdText.getControlMessage()));
    }

    @Override
    protected Accessibility getInstanceData() {
        return INSTANCE;
    }

    @Override
    protected Accessibility getCurrentData() {
        return CraftPresence.CONFIG.accessibilitySettings;
    }

    @Override
    protected Accessibility getDefaultData() {
        return DEFAULTS;
    }
}
