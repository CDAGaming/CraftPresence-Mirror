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

import com.gitlab.cdagaming.craftpresence.core.Constants;
import com.gitlab.cdagaming.craftpresence.core.config.element.ColorData;
import com.gitlab.cdagaming.craftpresence.core.config.element.ColorSection;
import com.gitlab.cdagaming.craftpresence.utils.gui.controls.ExtendedButtonControl;
import com.gitlab.cdagaming.craftpresence.utils.gui.controls.SliderControl;
import com.gitlab.cdagaming.craftpresence.utils.gui.impl.ConfigurationGui;
import com.gitlab.cdagaming.craftpresence.utils.gui.widgets.ScrollableTextWidget;
import com.gitlab.cdagaming.craftpresence.utils.gui.widgets.TextWidget;
import com.gitlab.cdagaming.craftpresence.utils.gui.widgets.TexturedWidget;
import io.github.cdagaming.unicore.impl.Pair;
import io.github.cdagaming.unicore.impl.Tuple;
import io.github.cdagaming.unicore.utils.StringUtils;

@SuppressWarnings("DuplicatedCode")
public class ColorEditorGui extends ConfigurationGui<ColorData> {
    private final ColorData DEFAULTS, INSTANCE, CURRENT;
    private ColorData storedStart, storedEnd;
    // Start Color Data
    private SliderControl startRed, startGreen, startBlue, startAlpha;
    // End Color Data
    private SliderControl endRed, endGreen, endBlue, endAlpha;
    // General Data
    private TextWidget textureLocationText, startColorText, endColorText;
    private SliderControl tintFactor;

    ColorEditorGui(ColorData moduleData, ColorData defaultData) {
        super("gui.config.title", "gui.config.title.editor.color");
        DEFAULTS = defaultData;
        INSTANCE = moduleData.copy();
        CURRENT = moduleData;
    }

    @Override
    protected void appendControls() {
        super.appendControls();

        storedStart = new ColorData(getInstanceData().getStart());
        storedEnd = new ColorData(getInstanceData().getEnd());

        final int calc1 = (getScreenWidth() / 2) - 183;
        final int calc2 = (getScreenWidth() / 2) + 3;
        final int calcAlt = calc2 + 180;

        final String startColorTitle = Constants.TRANSLATOR.translate("gui.config.message.editor.color.start");
        final String endColorTitle = Constants.TRANSLATOR.translate("gui.config.message.editor.color.end");
        final String previewTitle = Constants.TRANSLATOR.translate("gui.config.message.editor.color.preview");

        final String redTitle = Constants.TRANSLATOR.translate("gui.config.message.editor.color.value.red");
        final String greenTitle = Constants.TRANSLATOR.translate("gui.config.message.editor.color.value.green");
        final String blueTitle = Constants.TRANSLATOR.translate("gui.config.message.editor.color.value.blue");
        final String alphaTitle = Constants.TRANSLATOR.translate("gui.config.message.editor.color.value.alpha");

        final String tintFactorTitle = Constants.TRANSLATOR.translate("gui.config.message.editor.color.tint_factor");
        final String syncEndTitle = Constants.TRANSLATOR.translate("gui.config.message.editor.color.sync_end_color");

        // Start Color Section
        childFrame.addWidget(new ScrollableTextWidget(
                calc1, getButtonY(0),
                childFrame.getScreenWidth(),
                startColorTitle
        ));
        startColorText = childFrame.addControl(
                new TextWidget(
                        getFontRenderer(),
                        getButtonY(1),
                        180, 20,
                        () -> {
                            final String newText = startColorText.getControlMessage();
                            if (StringUtils.isValidColorCode(newText)) {
                                loadStartData(new ColorSection(
                                        StringUtils.findColor(newText)
                                ));
                            }
                        },
                        "gui.config.message.editor.hex_code"
                )
        );
        startRed = childFrame.addControl(
                new SliderControl(
                        new Pair<>(calc1, getButtonY(2)),
                        new Pair<>(180, 20),
                        getInstanceData().getStart().red,
                        0.0f, 255.0f, 1.0f,
                        redTitle,
                        new Tuple<>(
                                this::setStartData,
                                null,
                                this::setStartData
                        )
                )
        );
        startRed.setValueFormat("%.0f");
        startGreen = childFrame.addControl(
                new SliderControl(
                        new Pair<>(calc1, getButtonY(3)),
                        new Pair<>(180, 20),
                        getInstanceData().getStart().green,
                        0.0f, 255.0f, 1.0f,
                        greenTitle,
                        new Tuple<>(
                                this::setStartData,
                                null,
                                this::setStartData
                        )
                )
        );
        startGreen.setValueFormat("%.0f");
        startBlue = childFrame.addControl(
                new SliderControl(
                        new Pair<>(calc1, getButtonY(4)),
                        new Pair<>(180, 20),
                        getInstanceData().getStart().blue,
                        0.0f, 255.0f, 1.0f,
                        blueTitle,
                        new Tuple<>(
                                this::setStartData,
                                null,
                                this::setStartData
                        )
                )
        );
        startBlue.setValueFormat("%.0f");
        startAlpha = childFrame.addControl(
                new SliderControl(
                        new Pair<>(calc1, getButtonY(5)),
                        new Pair<>(180, 20),
                        getInstanceData().getStart().alpha,
                        0.0f, 255.0f, 1.0f,
                        alphaTitle,
                        new Tuple<>(
                                this::setStartData,
                                null,
                                this::setStartData
                        )
                )
        );
        startAlpha.setValueFormat("%.0f");
        childFrame.addWidget(new TexturedWidget(
                calc2, getButtonY(2, 1),
                calcAlt, 93,
                0.0D, () -> 1.0F,
                () -> storedStart, true
        ));

        // End Color Section
        childFrame.addWidget(new ScrollableTextWidget(
                calc1, getButtonY(6),
                childFrame.getScreenWidth(),
                endColorTitle
        ));
        endColorText = childFrame.addControl(
                new TextWidget(
                        getFontRenderer(),
                        getButtonY(7),
                        180, 20,
                        () -> {
                            final String newText = endColorText.getControlMessage();
                            if (StringUtils.isValidColorCode(newText)) {
                                loadEndData(new ColorSection(
                                        StringUtils.findColor(newText)
                                ));
                            }
                        },
                        "gui.config.message.editor.hex_code"
                )
        );
        endRed = childFrame.addControl(
                new SliderControl(
                        new Pair<>(calc1, getButtonY(8)),
                        new Pair<>(180, 20),
                        getInstanceData().getEnd().red,
                        0.0f, 255.0f, 1.0f,
                        redTitle,
                        new Tuple<>(
                                this::setEndData,
                                null,
                                this::setEndData
                        )
                )
        );
        endRed.setValueFormat("%.0f");
        endGreen = childFrame.addControl(
                new SliderControl(
                        new Pair<>(calc1, getButtonY(9)),
                        new Pair<>(180, 20),
                        getInstanceData().getEnd().green,
                        0.0f, 255.0f, 1.0f,
                        greenTitle,
                        new Tuple<>(
                                this::setEndData,
                                null,
                                this::setEndData
                        )
                )
        );
        endGreen.setValueFormat("%.0f");
        endBlue = childFrame.addControl(
                new SliderControl(
                        new Pair<>(calc1, getButtonY(10)),
                        new Pair<>(180, 20),
                        getInstanceData().getEnd().blue,
                        0.0f, 255.0f, 1.0f,
                        blueTitle,
                        new Tuple<>(
                                this::setEndData,
                                null,
                                this::setEndData
                        )
                )
        );
        endBlue.setValueFormat("%.0f");
        endAlpha = childFrame.addControl(
                new SliderControl(
                        new Pair<>(calc1, getButtonY(11)),
                        new Pair<>(180, 20),
                        getInstanceData().getEnd().alpha,
                        0.0f, 255.0f, 1.0f,
                        alphaTitle,
                        new Tuple<>(
                                this::setEndData,
                                null,
                                this::setEndData
                        )
                )
        );
        endAlpha.setValueFormat("%.0f");
        childFrame.addWidget(new TexturedWidget(
                calc2, getButtonY(8, 1),
                calcAlt, 93,
                0.0D, () -> 1.0F,
                () -> storedEnd, true
        ));

        // Preview Section
        childFrame.addWidget(new ScrollableTextWidget(
                calc1, getButtonY(12),
                childFrame.getScreenWidth(),
                previewTitle
        ));
        textureLocationText = childFrame.addControl(
                new TextWidget(
                        getFontRenderer(),
                        getButtonY(13),
                        180, 20,
                        () -> getInstanceData().setTexLocation(textureLocationText.getControlMessage()),
                        "gui.config.message.editor.texture_path"
                )
        );
        textureLocationText.setControlMessage(getInstanceData().getTexLocation());
        tintFactor = childFrame.addControl(
                new SliderControl(
                        new Pair<>(calc1, getButtonY(14)),
                        new Pair<>(180, 20),
                        100,
                        0.0f, 100.0f, 1.0f,
                        tintFactorTitle
                )
        );
        tintFactor.setValueFormat("%.0f%%");
        // Adding "Sync End Color" Button
        childFrame.addControl(
                new ExtendedButtonControl(
                        calc1, getButtonY(15),
                        180, 20,
                        syncEndTitle,
                        () -> loadEndData(getInstanceData().getStart())
                )
        );
        childFrame.addWidget(new TexturedWidget(
                calc2, getButtonY(14, 1),
                calcAlt, 93,
                0.0D, () -> tintFactor.getSliderValue(true),
                this::getInstanceData, true
        ));
    }

    @Override
    protected boolean allowedToReset() {
        return DEFAULTS != null;
    }

    @Override
    protected ColorData getInstanceData() {
        return INSTANCE;
    }

    @Override
    protected ColorData getCurrentData() {
        return CURRENT;
    }

    @Override
    protected ColorData getDefaultData() {
        return DEFAULTS;
    }

    private void setStartColor(final ColorSection sect) {
        getInstanceData().setStartColor(sect);
        storedStart.setStartColor(sect);

        final boolean hasEnd = getInstanceData().hasEnd();
        final ColorSection endSection = storedEnd.getStart();
        if (!hasEnd && !sect.equals(endSection)) {
            // Create the `endColor` data, if the new `startColor` differs
            setEndData();
        } else if (hasEnd && sect.equals(endSection)) {
            // "Sync End Color" if we currently have `endColor` data,
            // but it equals the new `startColor` info
            loadEndData(getInstanceData().getStart());
        }
    }

    private void setEndColor(final ColorSection sect) {
        getInstanceData().setEndColor(sect);
        storedEnd.setStartColor(sect);
    }

    private void loadStartData(final ColorSection sect) {
        startRed.setSliderValue(sect.red);
        startGreen.setSliderValue(sect.green);
        startBlue.setSliderValue(sect.blue);
        startAlpha.setSliderValue(sect.alpha);
    }

    private void setStartData() {
        final ColorSection sect = getInstanceData().getStart();
        sect.red = (int) startRed.getSliderValue();
        sect.green = (int) startGreen.getSliderValue();
        sect.blue = (int) startBlue.getSliderValue();
        sect.alpha = (int) startAlpha.getSliderValue();
        setStartColor(sect);
    }

    private void loadEndData(final ColorSection sect) {
        endRed.setSliderValue(sect.red);
        endGreen.setSliderValue(sect.green);
        endBlue.setSliderValue(sect.blue);
        endAlpha.setSliderValue(sect.alpha);
    }

    private void setEndData() {
        final ColorSection sect = getInstanceData().getEnd();
        sect.red = (int) endRed.getSliderValue();
        sect.green = (int) endGreen.getSliderValue();
        sect.blue = (int) endBlue.getSliderValue();
        sect.alpha = (int) endAlpha.getSliderValue();
        setEndColor(sect);
    }
}
