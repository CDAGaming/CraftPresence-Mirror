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
import com.gitlab.cdagaming.craftpresence.utils.StringUtils;
import com.gitlab.cdagaming.craftpresence.utils.gui.controls.SliderControl;
import com.gitlab.cdagaming.craftpresence.utils.gui.impl.ConfigurationGui;
import com.gitlab.cdagaming.craftpresence.utils.gui.widgets.TextDisplayWidget;
import com.gitlab.cdagaming.craftpresence.utils.gui.widgets.TextWidget;
import com.gitlab.cdagaming.craftpresence.utils.gui.widgets.TexturedWidget;
import net.minecraft.client.gui.GuiScreen;

import java.awt.*;
import java.util.function.Supplier;

@SuppressWarnings("DuplicatedCode")
public class ColorEditorGui extends ConfigurationGui<ColorData> {
    private final ColorData DEFAULTS, INSTANCE, CURRENT;
    private final ColorData storedStart, storedEnd;
    private final Supplier<ColorData> syncSupplier;

    // Start Color Data
    private SliderControl startRed, startGreen, startBlue, startAlpha;
    // End Color Data
    private SliderControl endRed, endGreen, endBlue, endAlpha;
    // General Data
    private TextWidget textureLocationText, startColorText, endColorText;
    private SliderControl tintFactor;

    ColorEditorGui(GuiScreen parentScreen, ColorData moduleData, ColorData defaultData, Supplier<ColorData> syncData) {
        super(parentScreen, "gui.config.title", "gui.config.title.editor.color");
        DEFAULTS = defaultData;
        INSTANCE = moduleData.copy();
        CURRENT = moduleData;
        syncSupplier = syncData;

        storedStart = new ColorData(getInstance().getStart());
        storedEnd = new ColorData(getInstance().getEnd());
    }

    @Override
    protected void appendControls() {
        super.appendControls();

        final int calc1 = (getScreenWidth() / 2) - 183;
        final int calc2 = (getScreenWidth() / 2) + 3;
        final int calcAlt = calc2 + 180;

        final String startColorTitle = ModUtils.TRANSLATOR.translate("gui.config.message.editor.color.start");
        final String endColorTitle = ModUtils.TRANSLATOR.translate("gui.config.message.editor.color.end");
        final String previewTitle = ModUtils.TRANSLATOR.translate("gui.config.message.editor.color.preview");

        final String redTitle = ModUtils.TRANSLATOR.translate("gui.config.message.editor.color.value.red");
        final String greenTitle = ModUtils.TRANSLATOR.translate("gui.config.message.editor.color.value.green");
        final String blueTitle = ModUtils.TRANSLATOR.translate("gui.config.message.editor.color.value.blue");
        final String alphaTitle = ModUtils.TRANSLATOR.translate("gui.config.message.editor.color.value.alpha");

        final String tintFactorTitle = ModUtils.TRANSLATOR.translate("gui.config.message.editor.color.tint_factor");

        // Start Color Section
        childFrame.addWidget(new TextDisplayWidget(
                childFrame, false,
                calc1, getButtonY(0),
                getScreenWidth(),
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
                        getInstance().getStart().red,
                        0.0f, 255.0f, 1.0f,
                        redTitle,
                        new Tuple<>(
                                () -> {
                                    final ColorSection sect = getInstance().getStart();
                                    sect.red = (int) startRed.getSliderValue();
                                    setStartColor(sect);
                                },
                                null,
                                () -> {
                                    final ColorSection sect = getInstance().getStart();
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
                        getInstance().getStart().green,
                        0.0f, 255.0f, 1.0f,
                        greenTitle,
                        new Tuple<>(
                                () -> {
                                    final ColorSection sect = getInstance().getStart();
                                    sect.green = (int) startGreen.getSliderValue();
                                    setStartColor(sect);
                                },
                                null,
                                () -> {
                                    final ColorSection sect = getInstance().getStart();
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
                        getInstance().getStart().blue,
                        0.0f, 255.0f, 1.0f,
                        blueTitle,
                        new Tuple<>(
                                () -> {
                                    final ColorSection sect = getInstance().getStart();
                                    sect.blue = (int) startBlue.getSliderValue();
                                    setStartColor(sect);
                                },
                                null,
                                () -> {
                                    final ColorSection sect = getInstance().getStart();
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
                        getInstance().getStart().alpha,
                        0.0f, 255.0f, 1.0f,
                        alphaTitle,
                        new Tuple<>(
                                () -> {
                                    final ColorSection sect = getInstance().getStart();
                                    sect.alpha = (int) startAlpha.getSliderValue();
                                    setStartColor(sect);
                                },
                                null,
                                () -> {
                                    final ColorSection sect = getInstance().getStart();
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
        childFrame.addWidget(new TextDisplayWidget(
                childFrame, false,
                calc1, getButtonY(6),
                getScreenWidth(),
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
                        getInstance().getEnd().red,
                        0.0f, 255.0f, 1.0f,
                        redTitle,
                        new Tuple<>(
                                () -> {
                                    final ColorSection sect = getInstance().getEnd();
                                    sect.red = (int) endRed.getSliderValue();
                                    setEndColor(sect);
                                },
                                null,
                                () -> {
                                    final ColorSection sect = getInstance().getEnd();
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
                        getInstance().getEnd().green,
                        0.0f, 255.0f, 1.0f,
                        greenTitle,
                        new Tuple<>(
                                () -> {
                                    final ColorSection sect = getInstance().getEnd();
                                    sect.green = (int) endGreen.getSliderValue();
                                    setEndColor(sect);
                                },
                                null,
                                () -> {
                                    final ColorSection sect = getInstance().getEnd();
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
                        getInstance().getEnd().blue,
                        0.0f, 255.0f, 1.0f,
                        blueTitle,
                        new Tuple<>(
                                () -> {
                                    final ColorSection sect = getInstance().getEnd();
                                    sect.blue = (int) endBlue.getSliderValue();
                                    setEndColor(sect);
                                },
                                null,
                                () -> {
                                    final ColorSection sect = getInstance().getEnd();
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
                        getInstance().getEnd().alpha,
                        0.0f, 255.0f, 1.0f,
                        alphaTitle,
                        new Tuple<>(
                                () -> {
                                    final ColorSection sect = getInstance().getEnd();
                                    sect.alpha = (int) endAlpha.getSliderValue();
                                    setEndColor(sect);
                                },
                                null,
                                () -> {
                                    final ColorSection sect = getInstance().getEnd();
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
        childFrame.addWidget(new TextDisplayWidget(
                childFrame, false,
                calc1, getButtonY(12),
                getScreenWidth(),
                previewTitle
        ));
        textureLocationText = childFrame.addControl(
                new TextWidget(
                        getFontRenderer(),
                        getButtonY(13),
                        180, 20,
                        () -> getInstance().setTexLocation(textureLocationText.getControlMessage()),
                        "gui.config.message.editor.texture_path"
                )
        );
        textureLocationText.setControlMessage(getInstance().getTexLocation());
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
                this::getInstance, true
        ));
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
        return setCurrentData(syncSupplier.get());
    }

    @Override
    protected void applySettings() {
        setCurrentData(getInstance());
    }

    @Override
    protected ColorData getOriginalData() {
        return DEFAULTS;
    }

    @Override
    protected ColorData getCurrentData() {
        return CURRENT;
    }

    @Override
    protected boolean setCurrentData(ColorData data) {
        // Hotfix: Ensure Optional Data Persistance
        if (data.getStart() != null && data.getStart().equals(data.getEnd())) {
            data.setEndColor(null);
        }
        if (StringUtils.isNullOrEmpty(data.getTexLocation())) {
            data.setTexLocation(null);
        }

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

    private void setStartColor(final ColorSection sect) {
        getInstance().setStartColor(sect);
        storedStart.setStartColor(sect);
    }

    private void setEndColor(final ColorSection sect) {
        getInstance().setEndColor(sect);
        storedEnd.setStartColor(sect);
    }
}
