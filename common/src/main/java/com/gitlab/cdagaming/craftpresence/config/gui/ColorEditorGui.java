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
import com.gitlab.cdagaming.craftpresence.config.element.ColorData;
import com.gitlab.cdagaming.craftpresence.config.element.ColorSection;
import com.gitlab.cdagaming.craftpresence.impl.Pair;
import com.gitlab.cdagaming.craftpresence.impl.Tuple;
import com.gitlab.cdagaming.craftpresence.utils.gui.controls.ExtendedTextControl;
import com.gitlab.cdagaming.craftpresence.utils.gui.controls.SliderControl;
import com.gitlab.cdagaming.craftpresence.utils.gui.impl.ConfigurationGui;
import com.gitlab.cdagaming.craftpresence.utils.gui.widgets.TextDisplayWidget;
import com.gitlab.cdagaming.craftpresence.utils.gui.widgets.TexturedWidget;
import net.minecraft.client.gui.GuiScreen;

public class ColorEditorGui extends ConfigurationGui<ColorData> {
    private final ColorData ORIGINAL, INSTANCE, CURRENT;

    // Start Color Data
    private SliderControl startRed, startGreen, startBlue, startAlpha;
    // End Color Data
    private SliderControl endRed, endGreen, endBlue, endAlpha;
    // General Data
    private ExtendedTextControl textureLocationText;

    ColorEditorGui(GuiScreen parentScreen, ColorData moduleData) {
        super(parentScreen, "gui.config.title", "gui.config.title.editor.color");
        ORIGINAL = moduleData.copy();
        INSTANCE = moduleData.copy();
        CURRENT = moduleData;
    }

    @Override
    protected void appendControls() {
        super.appendControls();

        final int calc1 = (getScreenWidth() / 2) - 183;
        final int calc2 = (getScreenWidth() / 2) + 3;

        final String redTitle = ModUtils.TRANSLATOR.translate("gui.config.message.editor.color.value.red");
        final String greenTitle = ModUtils.TRANSLATOR.translate("gui.config.message.editor.color.value.green");
        final String blueTitle = ModUtils.TRANSLATOR.translate("gui.config.message.editor.color.value.blue");
        final String alphaTitle = ModUtils.TRANSLATOR.translate("gui.config.message.editor.color.value.alpha");

        // Start Color Section
        childFrame.addWidget(new TextDisplayWidget(
                childFrame, true,
                0, getButtonY(0),
                getScreenWidth(),
                "Start Color"
        ));
        startRed = childFrame.addControl(
                new SliderControl(
                        new Pair<>(calc1, getButtonY(1)),
                        new Pair<>(180, 20),
                        getInstance().getStart().red,
                        0.0f, 255.0f, 1.0f,
                        redTitle,
                        new Tuple<>(
                                () -> {
                                    final ColorSection sect = getInstance().getStart();
                                    sect.red = (int) startRed.getSliderValue();
                                    getInstance().setStartColor(sect);
                                },
                                () -> {},
                                () -> {
                                    final ColorSection sect = getInstance().getStart();
                                    sect.red = (int) startRed.getSliderValue();
                                    getInstance().setStartColor(sect);
                                }
                        )
                )
        );
        startGreen = childFrame.addControl(
                new SliderControl(
                        new Pair<>(calc2, getButtonY(1)),
                        new Pair<>(180, 20),
                        getInstance().getStart().green,
                        0.0f, 255.0f, 1.0f,
                        greenTitle,
                        new Tuple<>(
                                () -> {
                                    final ColorSection sect = getInstance().getStart();
                                    sect.green = (int) startGreen.getSliderValue();
                                    getInstance().setStartColor(sect);
                                },
                                () -> {},
                                () -> {
                                    final ColorSection sect = getInstance().getStart();
                                    sect.green = (int) startGreen.getSliderValue();
                                    getInstance().setStartColor(sect);
                                }
                        )
                )
        );
        startBlue = childFrame.addControl(
                new SliderControl(
                        new Pair<>(calc1, getButtonY(2)),
                        new Pair<>(180, 20),
                        getInstance().getStart().blue,
                        0.0f, 255.0f, 1.0f,
                        blueTitle,
                        new Tuple<>(
                                () -> {
                                    final ColorSection sect = getInstance().getStart();
                                    sect.blue = (int) startBlue.getSliderValue();
                                    getInstance().setStartColor(sect);
                                },
                                () -> {},
                                () -> {
                                    final ColorSection sect = getInstance().getStart();
                                    sect.blue = (int) startBlue.getSliderValue();
                                    getInstance().setStartColor(sect);
                                }
                        )
                )
        );
        startAlpha = childFrame.addControl(
                new SliderControl(
                        new Pair<>(calc2, getButtonY(2)),
                        new Pair<>(180, 20),
                        getInstance().getStart().alpha,
                        0.0f, 255.0f, 1.0f,
                        alphaTitle,
                        new Tuple<>(
                                () -> {
                                    final ColorSection sect = getInstance().getStart();
                                    sect.alpha = (int) startAlpha.getSliderValue();
                                    getInstance().setStartColor(sect);
                                },
                                () -> {},
                                () -> {
                                    final ColorSection sect = getInstance().getStart();
                                    sect.alpha = (int) startAlpha.getSliderValue();
                                    getInstance().setStartColor(sect);
                                }
                        )
                )
        );

        // End Color Section
        childFrame.addWidget(new TextDisplayWidget(
                childFrame, true,
                0, getButtonY(3),
                getScreenWidth(),
                "End Color"
        ));
        endRed = childFrame.addControl(
                new SliderControl(
                        new Pair<>(calc1, getButtonY(4)),
                        new Pair<>(180, 20),
                        getInstance().getEnd().red,
                        0.0f, 255.0f, 1.0f,
                        redTitle,
                        new Tuple<>(
                                () -> {
                                    final ColorSection sect = getInstance().getEnd();
                                    sect.red = (int) endRed.getSliderValue();
                                    getInstance().setEndColor(sect);
                                },
                                () -> {},
                                () -> {
                                    final ColorSection sect = getInstance().getEnd();
                                    sect.red = (int) endRed.getSliderValue();
                                    getInstance().setEndColor(sect);
                                }
                        )
                )
        );
        endGreen = childFrame.addControl(
                new SliderControl(
                        new Pair<>(calc2, getButtonY(4)),
                        new Pair<>(180, 20),
                        getInstance().getEnd().green,
                        0.0f, 255.0f, 1.0f,
                        greenTitle,
                        new Tuple<>(
                                () -> {
                                    final ColorSection sect = getInstance().getEnd();
                                    sect.green = (int) endGreen.getSliderValue();
                                    getInstance().setEndColor(sect);
                                },
                                () -> {},
                                () -> {
                                    final ColorSection sect = getInstance().getEnd();
                                    sect.green = (int) endGreen.getSliderValue();
                                    getInstance().setEndColor(sect);
                                }
                        )
                )
        );
        endBlue = childFrame.addControl(
                new SliderControl(
                        new Pair<>(calc1, getButtonY(5)),
                        new Pair<>(180, 20),
                        getInstance().getEnd().blue,
                        0.0f, 255.0f, 1.0f,
                        blueTitle,
                        new Tuple<>(
                                () -> {
                                    final ColorSection sect = getInstance().getEnd();
                                    sect.blue = (int) endBlue.getSliderValue();
                                    getInstance().setEndColor(sect);
                                },
                                () -> {},
                                () -> {
                                    final ColorSection sect = getInstance().getEnd();
                                    sect.blue = (int) endBlue.getSliderValue();
                                    getInstance().setEndColor(sect);
                                }
                        )
                )
        );
        endAlpha = childFrame.addControl(
                new SliderControl(
                        new Pair<>(calc2, getButtonY(5)),
                        new Pair<>(180, 20),
                        getInstance().getEnd().alpha,
                        0.0f, 255.0f, 1.0f,
                        alphaTitle,
                        new Tuple<>(
                                () -> {
                                    final ColorSection sect = getInstance().getEnd();
                                    sect.alpha = (int) endAlpha.getSliderValue();
                                    getInstance().setEndColor(sect);
                                },
                                () -> {},
                                () -> {
                                    final ColorSection sect = getInstance().getEnd();
                                    sect.alpha = (int) endAlpha.getSliderValue();
                                    getInstance().setEndColor(sect);
                                }
                        )
                )
        );

        // Preview Section
        childFrame.addWidget(new TextDisplayWidget(
                childFrame, true,
                0, getButtonY(6),
                getScreenWidth(),
                "Preview"
        ));
        childFrame.addWidget(new TexturedWidget(
                childFrame,
                30, getButtonY(7),
                getScreenWidth() - 30, 120,
                0.0D, 1.0F,
                this::getInstance, true
        ));
    }

    @Override
    protected void applySettings() {
        setCurrentData(getInstance());
    }

    @Override
    protected ColorData getOriginalData() {
        return ORIGINAL;
    }

    @Override
    protected ColorData getCurrentData() {
        return CURRENT;
    }

    @Override
    protected boolean setCurrentData(ColorData data) {
        if (!getCurrentData().equals(data)) {
            getCurrentData().transferFrom(data);
            CraftPresence.CONFIG.hasChanged = true;
            return true;
        }
        return false;
    }

    protected ColorData getInstance() {
        return INSTANCE;
    }
}
