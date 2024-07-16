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

package com.gitlab.cdagaming.craftpresence.utils.gui.controls;

import com.gitlab.cdagaming.craftpresence.CraftPresence;
import com.gitlab.cdagaming.craftpresence.core.Constants;
import com.gitlab.cdagaming.craftpresence.core.impl.discord.CompiledPresence;
import com.gitlab.cdagaming.unilib.core.impl.screen.ScreenConstants;
import com.gitlab.cdagaming.unilib.utils.gui.RenderUtils;
import com.gitlab.cdagaming.unilib.utils.gui.controls.ExtendedButtonControl;
import com.gitlab.cdagaming.unilib.utils.gui.integrations.ExtendedScreen;
import com.gitlab.cdagaming.unilib.utils.gui.widgets.ScrollableTextWidget;
import com.gitlab.cdagaming.unilib.utils.gui.widgets.TexturedWidget;
import io.github.cdagaming.unicore.utils.StringUtils;
import io.github.cdagaming.unicore.utils.UrlUtils;

import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.function.Supplier;

public class PresenceVisualizer {
    private final List<ExtendedButtonControl> buttons = StringUtils.newArrayList();
    private final List<ScrollableTextWidget> lines = StringUtils.newArrayList();
    private final ExtendedScreen screen;
    private final boolean autoRefresh;
    private ScreenConstants.ColorData largeImageData, smallImageData;
    private ScrollableTextWidget titleText;
    private TexturedWidget largeWidget, smallWidget;
    private Supplier<CompiledPresence> presenceSupplier;
    private CompiledPresence lastCompiledPresence;

    public PresenceVisualizer(final ExtendedScreen screen, final boolean autoRefresh) {
        this.screen = screen;
        this.autoRefresh = autoRefresh;
    }

    public PresenceVisualizer(final ExtendedScreen screen) {
        this(screen, false);
    }

    public void setupVisualizer(final int calc1, final int calc2,
                                final int controlIndex,
                                final boolean canLoad,
                                final ExtendedScreen childFrame,
                                final Supplier<CompiledPresence> richPresence) {
        // Ensure all Visualizer Fields are at their default values
        largeWidget = null;
        smallWidget = null;
        lines.clear();
        buttons.clear();
        lastCompiledPresence = null;
        titleText = null;
        largeImageData = null;
        smallImageData = null;

        presenceSupplier = richPresence;

        if (canLoad) {
            loadVisualizer(calc1, calc2, controlIndex, childFrame, presenceSupplier);
        }
    }

    public void setupVisualizer(final int controlIndex,
                                final boolean canLoad,
                                final ExtendedScreen childFrame,
                                final Supplier<CompiledPresence> richPresence) {
        final int calc1 = (screen.getScreenWidth() / 2) - 183;
        final int calc2 = (screen.getScreenWidth() / 2) + 3;

        setupVisualizer(
                calc1, calc2,
                controlIndex,
                canLoad,
                childFrame,
                richPresence
        );
    }

    private void loadVisualizer(final int calc1, final int calc2,
                                int controlIndex,
                                final ExtendedScreen childFrame,
                                final Supplier<CompiledPresence> richPresence) {
        if (CraftPresence.CONFIG.accessibilitySettings.stripExtraGuiElements) {
            return;
        }

        final int rightButtonPos = calc2 + 85;

        // Visualizer Section
        childFrame.addWidget(new ScrollableTextWidget(
                calc1, screen.getButtonY(controlIndex),
                rightButtonPos - calc1,
                Constants.TRANSLATOR.translate("gui.config.message.editor.presence.visualizer")
        ));

        // Adding Refresh Button
        if (!autoRefresh) {
            childFrame.addControl(
                    new ExtendedButtonControl(
                            rightButtonPos, screen.getButtonY(controlIndex),
                            95, 20,
                            Constants.TRANSLATOR.translate("gui.config.message.button.refresh"),
                            () -> refreshVisualizer(richPresence)
                    )
            );
        }

        controlIndex++;

        // Adding Large Image Visualizer
        largeWidget = childFrame.addWidget(new TexturedWidget(
                calc1, screen.getButtonY(controlIndex, 1),
                45, 45,
                0.0D, () -> 1.0F,
                () -> largeImageData, false
        ));
        // Adding Small Image Visualizer
        smallWidget = childFrame.addWidget(new TexturedWidget(
                largeWidget.getRight() - 13, screen.getButtonY(controlIndex, 32),
                16, 16,
                0.0D, () -> 1.0F,
                () -> smallImageData, false
        ));

        // Adding Text Elements
        final int textOffset = largeWidget.getRight() + 8;
        final int textWidth = rightButtonPos - textOffset;

        // Adding Title Bar (Client ID Title)
        titleText = childFrame.addWidget(new ScrollableTextWidget(
                textOffset, screen.getButtonY(controlIndex, -4),
                textWidth, ""
        ));
        // Adding RPC Lines
        lines.add(childFrame.addWidget(new ScrollableTextWidget(
                textOffset, screen.getButtonY(controlIndex, 9),
                textWidth, ""
        )));
        lines.add(childFrame.addWidget(new ScrollableTextWidget(
                textOffset, screen.getButtonY(controlIndex, 20),
                textWidth, ""
        )));
        lines.add(childFrame.addWidget(new ScrollableTextWidget(
                textOffset, screen.getButtonY(controlIndex, 31),
                textWidth, ""
        )));

        // Adding Additional Buttons
        buttons.add(childFrame.addControl(
                new ExtendedButtonControl(
                        rightButtonPos, screen.getButtonY(controlIndex++),
                        95, 20, ""
                )
        ));
        buttons.add(childFrame.addControl(
                new ExtendedButtonControl(
                        rightButtonPos, screen.getButtonY(controlIndex++),
                        95, 20, ""
                )
        ));

        refreshVisualizer(richPresence);
    }

    private void refreshVisualizer(final Supplier<CompiledPresence> richPresence) {
        // Compile the RichPresence data from current instance
        lastCompiledPresence = richPresence.get();

        final String titlePrefix = !CraftPresence.CONFIG.accessibilitySettings.stripTranslationFormatting ? "§l" : "";
        titleText.setMessage(titlePrefix + CraftPresence.CLIENT.CURRENT_TITLE);

        // Assign compiled data to the various fields
        if (lastCompiledPresence.largeAsset() != null) {
            largeImageData = new ScreenConstants.ColorData(
                    lastCompiledPresence.largeAsset().getUrl()
            );

            if (lastCompiledPresence.smallAsset() != null) {
                smallImageData = new ScreenConstants.ColorData(
                        lastCompiledPresence.smallAsset().getUrl()
                );
            } else {
                smallImageData = null;
            }
        } else {
            largeImageData = null;
            smallImageData = null;
        }

        updateLineTexts(
                lastCompiledPresence.details(),
                lastCompiledPresence.state(),
                lastCompiledPresence.getTimeString()
        );

        updateButtonTexts(
                lastCompiledPresence.getButtonData()
        );
    }

    private void updateButtonTexts(final Map<String, String> validButtons) {
        final Iterator<Map.Entry<String, String>> iterator = validButtons.entrySet().iterator();

        for (int i = 0; i < buttons.size(); i++) {
            final ExtendedButtonControl button = buttons.get(i);
            final boolean hasData = i < validButtons.size();
            if (hasData) {
                final Map.Entry<String, String> entry = iterator.next();
                button.setControlMessage(entry.getKey());
                button.setOnClick(() -> UrlUtils.openUrl(entry.getValue()));
            } else {
                button.setControlMessage("");
                button.setOnClick(null);
            }

            button.setControlEnabled(hasData);
            button.setControlVisible(hasData);
        }
    }

    private void updateLineTexts(final String... strings) {
        final List<String> validStrings = StringUtils.newArrayList();
        for (String string : strings) {
            if (!StringUtils.isNullOrEmpty(string)) {
                validStrings.add(string);
            }
        }

        for (int i = 0; i < lines.size(); i++) {
            final ScrollableTextWidget lineWidget = lines.get(i);
            if (i < validStrings.size()) {
                lineWidget.setMessage(validStrings.get(i));
            } else {
                lineWidget.setMessage("");
            }
        }
    }

    public void postRender(final ExtendedScreen childFrame) {
        if (autoRefresh && presenceSupplier != null) {
            refreshVisualizer(presenceSupplier);
        }

        if (lastCompiledPresence != null && childFrame.isOverScreen()) {
            final int mouseX = screen.getMouseX();
            final int mouseY = screen.getMouseY();

            if (smallImageData != null && RenderUtils.isMouseOver(
                    mouseX, mouseY,
                    smallWidget
            ) && !StringUtils.isNullOrEmpty(lastCompiledPresence.smallImageText())) {
                screen.drawMultiLineString(
                        StringUtils.splitTextByNewLine(
                                lastCompiledPresence.smallImageText()
                        )
                );
            } else if (largeImageData != null && RenderUtils.isMouseOver(
                    mouseX, mouseY,
                    largeWidget
            ) && !StringUtils.isNullOrEmpty(lastCompiledPresence.largeImageText())) {
                screen.drawMultiLineString(
                        StringUtils.splitTextByNewLine(
                                lastCompiledPresence.largeImageText()
                        )
                );
            }
        }
    }
}