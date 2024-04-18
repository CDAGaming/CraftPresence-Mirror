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
import com.gitlab.cdagaming.craftpresence.utils.gui.controls.SliderControl;
import com.gitlab.cdagaming.craftpresence.utils.gui.impl.ConfigurationGui;
import com.gitlab.cdagaming.craftpresence.utils.gui.widgets.ScrollableTextWidget;
import com.gitlab.cdagaming.craftpresence.utils.gui.widgets.TextWidget;
import com.gitlab.cdagaming.craftpresence.utils.gui.widgets.TexturedWidget;
import io.github.cdagaming.unicore.impl.Pair;
import io.github.cdagaming.unicore.impl.Tuple;
import io.github.cdagaming.unicore.utils.StringUtils;
import net.minecraft.client.gui.GuiScreen;

import java.awt.*;

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

    ColorEditorGui(GuiScreen parentScreen, ColorData moduleData, ColorData defaultData) {
        super(parentScreen, "gui.config.title", "gui.config.title.editor.color");
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

        // Start Color Section
        childFrame.addWidget(new ScrollableTextWidget(
                childFrame,
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
                                final Color newColor = StringUtils.findColor(newText);
                                startRed.setSliderValue(newColor.getRed());
                                startGreen.setSliderValue(newColor.getGreen());
                                startBlue.setSliderValue(newColor.getBlue());
                                startAlpha.setSliderValue(newColor.getAlpha());
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
                                () -> {
                                    final ColorSection sect = getInstanceData().getStart();
                                    sect.red = (int) startRed.getSliderValue();
                                    setStartColor(sect);
                                },
                                null,
                                () -> {
                                    final ColorSection sect = getInstanceData().getStart();
                                    sect.red = (int) startRed.getSliderValue();
                                    setStartColor(sect);
                                }
                        )
                )
        );
        startGreen = childFrame.addControl(
                new SliderControl(
                        new Pair<>(calc1, getButtonY(3)),
                        new Pair<>(180, 20),
                        getInstanceData().getStart().green,
                        0.0f, 255.0f, 1.0f,
                        greenTitle,
                        new Tuple<>(
                                () -> {
                                    final ColorSection sect = getInstanceData().getStart();
                                    sect.green = (int) startGreen.getSliderValue();
                                    setStartColor(sect);
                                },
                                null,
                                () -> {
                                    final ColorSection sect = getInstanceData().getStart();
                                    sect.green = (int) startGreen.getSliderValue();
                                    setStartColor(sect);
                                }
                        )
                )
        );
        startBlue = childFrame.addControl(
                new SliderControl(
                        new Pair<>(calc1, getButtonY(4)),
                        new Pair<>(180, 20),
                        getInstanceData().getStart().blue,
                        0.0f, 255.0f, 1.0f,
                        blueTitle,
                        new Tuple<>(
                                () -> {
                                    final ColorSection sect = getInstanceData().getStart();
                                    sect.blue = (int) startBlue.getSliderValue();
                                    setStartColor(sect);
                                },
                                null,
                                () -> {
                                    final ColorSection sect = getInstanceData().getStart();
                                    sect.blue = (int) startBlue.getSliderValue();
                                    setStartColor(sect);
                                }
                        )
                )
        );
        startAlpha = childFrame.addControl(
                new SliderControl(
                        new Pair<>(calc1, getButtonY(5)),
                        new Pair<>(180, 20),
                        getInstanceData().getStart().alpha,
                        0.0f, 255.0f, 1.0f,
                        alphaTitle,
                        new Tuple<>(
                                () -> {
                                    final ColorSection sect = getInstanceData().getStart();
                                    sect.alpha = (int) startAlpha.getSliderValue();
                                    setStartColor(sect);
                                },
                                null,
                                () -> {
                                    final ColorSection sect = getInstanceData().getStart();
                                    sect.alpha = (int) startAlpha.getSliderValue();
                                    setStartColor(sect);
                                }
                        )
                )
        );
        childFrame.addWidget(new TexturedWidget(
                childFrame,
                calc2, getButtonY(2, 1),
                calcAlt, 93,
                0.0D, () -> 1.0F,
                () -> storedStart, true
        ));

        // End Color Section
        childFrame.addWidget(new ScrollableTextWidget(
                childFrame,
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
                                final Color newColor = StringUtils.findColor(newText);
                                endRed.setSliderValue(newColor.getRed());
                                endGreen.setSliderValue(newColor.getGreen());
                                endBlue.setSliderValue(newColor.getBlue());
                                endAlpha.setSliderValue(newColor.getAlpha());
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
                                () -> {
                                    final ColorSection sect = getInstanceData().getEnd();
                                    sect.red = (int) endRed.getSliderValue();
                                    setEndColor(sect);
                                },
                                null,
                                () -> {
                                    final ColorSection sect = getInstanceData().getEnd();
                                    sect.red = (int) endRed.getSliderValue();
                                    setEndColor(sect);
                                }
                        )
                )
        );
        endGreen = childFrame.addControl(
                new SliderControl(
                        new Pair<>(calc1, getButtonY(9)),
                        new Pair<>(180, 20),
                        getInstanceData().getEnd().green,
                        0.0f, 255.0f, 1.0f,
                        greenTitle,
                        new Tuple<>(
                                () -> {
                                    final ColorSection sect = getInstanceData().getEnd();
                                    sect.green = (int) endGreen.getSliderValue();
                                    setEndColor(sect);
                                },
                                null,
                                () -> {
                                    final ColorSection sect = getInstanceData().getEnd();
                                    sect.green = (int) endGreen.getSliderValue();
                                    setEndColor(sect);
                                }
                        )
                )
        );
        endBlue = childFrame.addControl(
                new SliderControl(
                        new Pair<>(calc1, getButtonY(10)),
                        new Pair<>(180, 20),
                        getInstanceData().getEnd().blue,
                        0.0f, 255.0f, 1.0f,
                        blueTitle,
                        new Tuple<>(
                                () -> {
                                    final ColorSection sect = getInstanceData().getEnd();
                                    sect.blue = (int) endBlue.getSliderValue();
                                    setEndColor(sect);
                                },
                                null,
                                () -> {
                                    final ColorSection sect = getInstanceData().getEnd();
                                    sect.blue = (int) endBlue.getSliderValue();
                                    setEndColor(sect);
                                }
                        )
                )
        );
        endAlpha = childFrame.addControl(
                new SliderControl(
                        new Pair<>(calc1, getButtonY(11)),
                        new Pair<>(180, 20),
                        getInstanceData().getEnd().alpha,
                        0.0f, 255.0f, 1.0f,
                        alphaTitle,
                        new Tuple<>(
                                () -> {
                                    final ColorSection sect = getInstanceData().getEnd();
                                    sect.alpha = (int) endAlpha.getSliderValue();
                                    setEndColor(sect);
                                },
                                null,
                                () -> {
                                    final ColorSection sect = getInstanceData().getEnd();
                                    sect.alpha = (int) endAlpha.getSliderValue();
                                    setEndColor(sect);
                                }
                        )
                )
        );
        childFrame.addWidget(new TexturedWidget(
                childFrame,
                calc2, getButtonY(8, 1),
                calcAlt, 93,
                0.0D, () -> 1.0F,
                () -> storedEnd, true
        ));

        // Preview Section
        childFrame.addWidget(new ScrollableTextWidget(
                childFrame,
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
        childFrame.addWidget(new TexturedWidget(
                childFrame,
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

    @Override
    protected boolean setCurrentData(ColorData data) {
        // Hotfix: Ensure Optional Data Persistence
        if (data.getStart() != null && data.getStart().equals(data.getEnd())) {
            data.setEndColor(null);
        }
        if (StringUtils.isNullOrEmpty(data.getTexLocation())) {
            data.setTexLocation(null);
        }

        return super.setCurrentData(data);
    }

    private void setStartColor(final ColorSection sect) {
        getInstanceData().setStartColor(sect);
        storedStart.setStartColor(sect);
    }

    private void setEndColor(final ColorSection sect) {
        getInstanceData().setEndColor(sect);
        storedEnd.setStartColor(sect);
    }
}
