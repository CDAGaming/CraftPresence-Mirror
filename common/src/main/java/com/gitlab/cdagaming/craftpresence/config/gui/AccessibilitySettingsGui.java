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
import com.gitlab.cdagaming.craftpresence.ModUtils;
import com.gitlab.cdagaming.craftpresence.config.Config;
import com.gitlab.cdagaming.craftpresence.config.category.Accessibility;
import com.gitlab.cdagaming.craftpresence.core.Constants;
import com.gitlab.cdagaming.craftpresence.utils.KeyUtils;
import com.gitlab.cdagaming.craftpresence.utils.gui.controls.CheckBoxControl;
import com.gitlab.cdagaming.craftpresence.utils.gui.controls.ExtendedButtonControl;
import com.gitlab.cdagaming.craftpresence.utils.gui.controls.ExtendedTextControl;
import com.gitlab.cdagaming.craftpresence.utils.gui.impl.ConfigurationGui;
import com.gitlab.cdagaming.craftpresence.utils.gui.impl.ControlsGui;
import com.gitlab.cdagaming.craftpresence.utils.gui.widgets.TextWidget;
import io.github.cdagaming.unicore.utils.StringUtils;
import net.minecraft.client.gui.GuiScreen;

import java.util.List;

public class AccessibilitySettingsGui extends ConfigurationGui<Accessibility> {

    private final Accessibility INSTANCE, DEFAULTS;
    private ExtendedTextControl languageIdText;
    private CheckBoxControl stripTranslationColorsButton, stripTranslationFormattingButton,
            stripExtraGuiElementsButton, renderTooltipsButton;
    private ExtendedButtonControl controlsButton;

    AccessibilitySettingsGui(GuiScreen parentScreen) {
        super(parentScreen, "gui.config.title", "gui.config.title.accessibility");
        DEFAULTS = getCurrentData().getDefaults();
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
                        calc1, getButtonY(0),
                        180, 20,
                        "gui.config.name.accessibility.tooltip_background_color",
                        () -> openScreen(
                                new ColorEditorGui(
                                        currentScreen,
                                        getCurrentData().tooltipBackground,
                                        DEFAULTS.tooltipBackground,
                                        () -> Config.loadOrCreate().accessibilitySettings.tooltipBackground
                                )
                        ),
                        () -> drawMultiLineString(
                                StringUtils.splitTextByNewLine(
                                        Constants.TRANSLATOR.translate("gui.config.comment.accessibility.tooltip_background_color")
                                )
                        )
                )
        );
        // Adding Tooltip Border Color Button
        childFrame.addControl(
                new ExtendedButtonControl(
                        calc2, getButtonY(0),
                        180, 20,
                        "gui.config.name.accessibility.tooltip_border_color",
                        () -> openScreen(
                                new ColorEditorGui(
                                        currentScreen,
                                        getCurrentData().tooltipBorder,
                                        DEFAULTS.tooltipBorder,
                                        () -> Config.loadOrCreate().accessibilitySettings.tooltipBorder
                                )
                        ),
                        () -> drawMultiLineString(
                                StringUtils.splitTextByNewLine(
                                        Constants.TRANSLATOR.translate("gui.config.comment.accessibility.tooltip_border_color")
                                )
                        )
                )
        );
        // Adding Gui Background Color Button
        childFrame.addControl(
                new ExtendedButtonControl(
                        calc1, getButtonY(1),
                        180, 20,
                        "gui.config.name.accessibility.gui_background_color",
                        () -> openScreen(
                                new ColorEditorGui(
                                        currentScreen,
                                        getCurrentData().guiBackground,
                                        DEFAULTS.guiBackground,
                                        () -> Config.loadOrCreate().accessibilitySettings.guiBackground
                                )
                        ),
                        () -> drawMultiLineString(
                                StringUtils.splitTextByNewLine(
                                        Constants.TRANSLATOR.translate("gui.config.comment.accessibility.gui_background_color")
                                )
                        )
                )
        );
        // Adding World-Specific Gui Background Color Button
        childFrame.addControl(
                new ExtendedButtonControl(
                        calc2, getButtonY(1),
                        180, 20,
                        "gui.config.name.accessibility.world_gui_background_color",
                        () -> openScreen(
                                new ColorEditorGui(
                                        currentScreen,
                                        getCurrentData().worldGuiBackground,
                                        DEFAULTS.worldGuiBackground,
                                        () -> Config.loadOrCreate().accessibilitySettings.worldGuiBackground
                                )
                        ),
                        () -> drawMultiLineString(
                                StringUtils.splitTextByNewLine(
                                        Constants.TRANSLATOR.translate("gui.config.comment.accessibility.world_gui_background_color")
                                )
                        )
                )
        );

        languageIdText = childFrame.addControl(
                new TextWidget(
                        getFontRenderer(),
                        getButtonY(2),
                        180, 20,
                        "gui.config.name.accessibility.language_id",
                        () -> drawMultiLineString(
                                StringUtils.splitTextByNewLine(
                                        Constants.TRANSLATOR.translate("gui.config.comment.accessibility.language_id")
                                )
                        )
                )
        );
        languageIdText.setControlMessage(getCurrentData().languageId);

        stripTranslationColorsButton = childFrame.addControl(
                new CheckBoxControl(
                        calc1, getButtonY(3),
                        "gui.config.name.accessibility.strip_translation_colors",
                        getCurrentData().stripTranslationColors,
                        null,
                        () -> drawMultiLineString(
                                StringUtils.splitTextByNewLine(
                                        Constants.TRANSLATOR.translate("gui.config.comment.accessibility.strip_translation_colors")
                                )
                        )
                )
        );
        stripTranslationFormattingButton = childFrame.addControl(
                new CheckBoxControl(
                        calc1, getButtonY(4, -10),
                        "gui.config.name.accessibility.strip_translation_formatting",
                        getCurrentData().stripTranslationFormatting,
                        null,
                        () -> {
                            if (stripTranslationFormattingButton.isControlEnabled()) {
                                drawMultiLineString(
                                        StringUtils.splitTextByNewLine(
                                                Constants.TRANSLATOR.translate("gui.config.comment.accessibility.strip_translation_formatting")
                                        )
                                );
                            } else if (ModUtils.IS_TEXT_FORMATTING_BLOCKED) {
                                drawMultiLineString(
                                        StringUtils.splitTextByNewLine(
                                                Constants.TRANSLATOR.translate("craftpresence.message.unsupported")
                                        )
                                );
                            }
                        }
                )
        );
        stripExtraGuiElementsButton = childFrame.addControl(
                new CheckBoxControl(
                        calc1, getButtonY(5, -20),
                        "gui.config.name.accessibility.strip_extra_gui_elements",
                        getCurrentData().stripExtraGuiElements,
                        null,
                        () -> drawMultiLineString(
                                StringUtils.splitTextByNewLine(
                                        Constants.TRANSLATOR.translate("gui.config.comment.accessibility.strip_extra_gui_elements")
                                )
                        )
                )
        );
        renderTooltipsButton = childFrame.addControl(
                new CheckBoxControl(
                        calc1, getButtonY(6, -30),
                        "gui.config.name.accessibility.render_tooltips",
                        getCurrentData().renderTooltips,
                        null,
                        () -> drawMultiLineString(
                                StringUtils.splitTextByNewLine(
                                        Constants.TRANSLATOR.translate("gui.config.comment.accessibility.render_tooltips")
                                )
                        )
                )
        );

        // Adding Controls Button
        final List<String> controlInfo = StringUtils.newArrayList("key.craftpresence.category");
        KeyUtils.FilterMode controlMode = KeyUtils.FilterMode.Category;
        if (Constants.IS_LEGACY_SOFT) {
            controlInfo.clear();
            StringUtils.addEntriesNotPresent(controlInfo, CraftPresence.KEYBINDINGS.getRawKeyMappings().keySet());

            controlMode = KeyUtils.FilterMode.Name;
        }

        final KeyUtils.FilterMode finalControlMode = controlMode;
        controlsButton = childFrame.addControl(
                new ExtendedButtonControl(
                        calc2, getButtonY(3, 4),
                        180, 20,
                        "gui.config.message.button.controls",
                        () -> openScreen(
                                new ControlsGui(
                                        currentScreen, finalControlMode,
                                        controlInfo
                                )
                        )
                )
        );
    }

    @Override
    protected boolean canReset() {
        return !getCurrentData().equals(DEFAULTS);
    }

    @Override
    protected boolean allowedToReset() {
        return true;
    }

    @Override
    protected boolean resetData() {
        return setCurrentData(DEFAULTS);
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
        return setCurrentData(Config.loadOrCreate().accessibilitySettings);
    }

    @Override
    protected void syncRenderStates() {
        super.syncRenderStates();

        //noinspection ConstantConditions
        stripTranslationFormattingButton.setControlEnabled(!ModUtils.IS_TEXT_FORMATTING_BLOCKED);
        controlsButton.setControlEnabled(CraftPresence.KEYBINDINGS.areKeysRegistered());
        proceedButton.setControlEnabled(!StringUtils.isNullOrEmpty(languageIdText.getControlMessage()));
    }

    @Override
    protected void applySettings() {
        if (!languageIdText.getControlMessage().equals(getCurrentData().languageId)) {
            CraftPresence.CONFIG.hasChanged = true;
            getCurrentData().languageId = languageIdText.getControlMessage();
        }
        if (stripTranslationColorsButton.isChecked() != getCurrentData().stripTranslationColors) {
            CraftPresence.CONFIG.hasChanged = true;
            getCurrentData().stripTranslationColors = stripTranslationColorsButton.isChecked();
        }
        if (stripTranslationFormattingButton.isChecked() != getCurrentData().stripTranslationFormatting) {
            CraftPresence.CONFIG.hasChanged = true;
            getCurrentData().stripTranslationFormatting = stripTranslationFormattingButton.isChecked();
        }
        if (stripExtraGuiElementsButton.isChecked() != getCurrentData().stripExtraGuiElements) {
            CraftPresence.CONFIG.hasChanged = true;
            getCurrentData().stripExtraGuiElements = stripExtraGuiElementsButton.isChecked();
        }
        if (renderTooltipsButton.isChecked() != getCurrentData().renderTooltips) {
            CraftPresence.CONFIG.hasChanged = true;
            getCurrentData().renderTooltips = renderTooltipsButton.isChecked();
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
    protected boolean setCurrentData(Accessibility data) {
        if (!getCurrentData().equals(data)) {
            getCurrentData().transferFrom(data);
            CraftPresence.CONFIG.hasChanged = true;
            return true;
        }
        return false;
    }
}
