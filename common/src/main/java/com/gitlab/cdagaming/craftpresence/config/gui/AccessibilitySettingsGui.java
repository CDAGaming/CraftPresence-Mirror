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
import com.gitlab.cdagaming.craftpresence.core.config.category.Accessibility;
import com.gitlab.cdagaming.craftpresence.core.config.element.ColorData;
import com.gitlab.cdagaming.craftpresence.utils.gui.controls.ScrollableListControl;
import com.gitlab.cdagaming.craftpresence.utils.gui.impl.ConfigurationGui;
import com.gitlab.cdagaming.craftpresence.utils.gui.impl.SelectorGui;
import com.gitlab.cdagaming.unilib.core.CoreUtils;
import com.gitlab.cdagaming.unilib.utils.gui.controls.CheckBoxControl;
import com.gitlab.cdagaming.unilib.utils.gui.controls.ExtendedButtonControl;
import com.gitlab.cdagaming.unilib.utils.gui.controls.ExtendedTextControl;
import com.gitlab.cdagaming.unilib.utils.gui.impl.ControlsGui;
import com.gitlab.cdagaming.unilib.utils.gui.widgets.TextWidget;
import io.github.cdagaming.unicore.impl.Pair;
import io.github.cdagaming.unicore.utils.StringUtils;

import java.util.Map;

public class AccessibilitySettingsGui extends ConfigurationGui<Accessibility> {

    private final Accessibility INSTANCE, DEFAULTS;
    private final boolean isTextFormattingBlocked;
    // configName, [moduleData,defaultData]
    private final Map<String, Pair<ColorData, ColorData>> colorSettings = StringUtils.newHashMap();
    private ExtendedTextControl languageIdText;
    private CheckBoxControl stripTranslationColorsButton, stripTranslationFormattingButton,
            stripExtraGuiElementsButton, renderTooltipsButton;
    private ExtendedButtonControl controlsButton;

    AccessibilitySettingsGui() {
        super("gui.config.title", "gui.config.title.accessibility");
        DEFAULTS = getCurrentData().getDefaults();
        INSTANCE = getCurrentData().copy();
        isTextFormattingBlocked = CoreUtils.isTextFormattingBlocked(getProtocol());
    }

    @Override
    protected void appendControls() {
        super.appendControls();

        colorSettings.clear();

        colorSettings.put(
                "tooltipBackground",
                new Pair<>(getInstanceData().tooltipBackground, getDefaultData().tooltipBackground)
        );
        colorSettings.put(
                "tooltipBorder",
                new Pair<>(getInstanceData().tooltipBorder, getDefaultData().tooltipBorder)
        );
        colorSettings.put(
                "guiBackground",
                new Pair<>(getInstanceData().guiBackground, getDefaultData().guiBackground)
        );
        colorSettings.put(
                "altGuiBackground",
                new Pair<>(getInstanceData().altGuiBackground, getDefaultData().altGuiBackground)
        );

        final int calc1 = (getScreenWidth() / 2) - 183;
        final int calc2 = (getScreenWidth() / 2) + 3;

        languageIdText = childFrame.addControl(
                new TextWidget(
                        getFontRenderer(),
                        getButtonY(0),
                        180, 20,
                        () -> getInstanceData().languageId = languageIdText.getControlMessage(),
                        "gui.config.name.accessibility.language_id",
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
                        "gui.config.name.accessibility.strip_translation_colors",
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
                        "gui.config.name.accessibility.strip_translation_formatting",
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
                        "gui.config.name.accessibility.strip_extra_gui_elements",
                        getInstanceData().stripExtraGuiElements,
                        () -> getInstanceData().stripExtraGuiElements = stripExtraGuiElementsButton.isChecked(),
                        () -> drawMultiLineString(
                                StringUtils.splitTextByNewLine(
                                        Constants.TRANSLATOR.translate("gui.config.comment.accessibility.strip_extra_gui_elements")
                                )
                        )
                )
        );
        renderTooltipsButton = childFrame.addControl(
                new CheckBoxControl(
                        calc1, getButtonY(4, -30),
                        "gui.config.name.accessibility.render_tooltips",
                        getInstanceData().renderTooltips,
                        () -> getInstanceData().renderTooltips = renderTooltipsButton.isChecked(),
                        () -> drawMultiLineString(
                                StringUtils.splitTextByNewLine(
                                        Constants.TRANSLATOR.translate("gui.config.comment.accessibility.render_tooltips")
                                )
                        )
                )
        );

        // Adding Controls Button
        controlsButton = childFrame.addControl(
                new ExtendedButtonControl(
                        calc2, getButtonY(1),
                        180, 20,
                        "gui.config.message.button.controls",
                        () -> openScreen(
                                new ControlsGui(
                                        CraftPresence.KEYBINDINGS,
                                        "key.craftpresence.category"
                                )
                        )
                )
        );

        // Adding Color Editor Button
        childFrame.addControl(
                new ExtendedButtonControl(
                        calc2, getButtonY(2),
                        180, 20,
                        "gui.config.title.editor.color",
                        () -> openScreen(
                                new SelectorGui(
                                        Constants.TRANSLATOR.translate("gui.config.title.selector.item"),
                                        colorSettings.keySet(),
                                        null, null,
                                        true, false, ScrollableListControl.RenderType.None,
                                        null,
                                        (currentValue, parentScreen) -> {
                                            // Event to occur when Setting Dynamic/Specific Data
                                            final Pair<ColorData, ColorData> settings = colorSettings.get(currentValue);
                                            openScreen(
                                                    new ColorEditorGui(
                                                            settings.getFirst(),
                                                            settings.getSecond()
                                                    ), parentScreen
                                            );
                                        }
                                )
                        ),
                        () -> drawMultiLineString(
                                StringUtils.splitTextByNewLine(
                                        Constants.TRANSLATOR.translate("gui.config.message.hover.color_editor")
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

        controlsButton.setControlEnabled(CraftPresence.KEYBINDINGS.areKeysRegistered());
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
