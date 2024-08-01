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
import com.jagrosh.discordipc.entities.ActivityType;
import io.github.cdagaming.unicore.utils.StringUtils;
import io.github.cdagaming.unicore.utils.UrlUtils;

import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.function.Supplier;

/**
 * Gui Widget for a Rich Presence Visualizer
 *
 * @author CDAGaming
 */
public class PresenceVisualizer {
    /**
     * The stored button elements for this widget
     */
    private final List<ExtendedButtonControl> buttons = StringUtils.newArrayList();
    /**
     * The stored text line elements for this widget
     */
    private final List<ScrollableTextWidget> lines = StringUtils.newArrayList();
    /**
     * The current screen instance
     */
    private final ExtendedScreen screen;
    /**
     * Whether the visualizer should allow toggling party info
     */
    private final boolean showPartyToggle;
    /**
     * Whether the visualizer should auto-refresh
     */
    private final boolean autoRefresh;
    /**
     * Whether to show the visualizer in a "party-style" format
     */
    private boolean showPartyData;
    /**
     * The {@link ScreenConstants.ColorData} for the Large Image, if any
     */
    private ScreenConstants.ColorData largeImageData;
    /**
     * The {@link ScreenConstants.ColorData} for the Small Image, if any
     */
    private ScreenConstants.ColorData smallImageData;
    /**
     * The Texture Widget used for the Large Image Data
     */
    private TexturedWidget largeWidget;
    /**
     * The Texture Widget used for the Small Image Data
     */
    private TexturedWidget smallWidget;
    /**
     * The Supplier for the compiled rich presence
     */
    private Supplier<CompiledPresence> presenceSupplier;
    /**
     * The last compiled rich presence result
     */
    private CompiledPresence lastCompiledPresence;

    /**
     * Initializes this Gui Widget
     *
     * @param screen      The current screen instance
     * @param autoRefresh Whether the visualizer should auto-refresh
     */
    public PresenceVisualizer(final ExtendedScreen screen, final boolean autoRefresh) {
        this.screen = screen;
        this.autoRefresh = autoRefresh;
        this.showPartyToggle = !autoRefresh;
    }

    /**
     * Initializes this Gui Widget
     *
     * @param screen The current screen instance
     */
    public PresenceVisualizer(final ExtendedScreen screen) {
        this(screen, false);
    }

    /**
     * Set up the visualizer based on specified args
     *
     * @param calc1        The left-side render position
     * @param calc2        The right-side render position
     * @param controlIndex The row to begin rendering elements at
     * @param canLoad      Whether the visualizer can fully load
     * @param childFrame   The screen instance to add elements to
     * @param richPresence The Supplier for the compiled rich presence
     */
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
        largeImageData = null;
        smallImageData = null;

        presenceSupplier = richPresence;

        if (canLoad) {
            loadVisualizer(calc1, calc2, controlIndex, childFrame, presenceSupplier);
        }
    }

    /**
     * Set up the visualizer based on specified args
     *
     * @param controlIndex The row to begin rendering elements at
     * @param canLoad      Whether the visualizer can fully load
     * @param childFrame   The screen instance to add elements to
     * @param richPresence The Supplier for the compiled rich presence
     */
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

    /**
     * Load visualizer data based on specified args
     *
     * @param calc1        The left-side render position
     * @param calc2        The right-side render position
     * @param controlIndex The row to begin rendering elements at
     * @param childFrame   The screen instance to add elements to
     * @param richPresence The Supplier for the compiled rich presence
     */
    private void loadVisualizer(final int calc1, final int calc2,
                                int controlIndex,
                                final ExtendedScreen childFrame,
                                final Supplier<CompiledPresence> richPresence) {
        if (CraftPresence.CONFIG.accessibilitySettings.stripExtraGuiElements) {
            return;
        }

        final int rightButtonPos = calc2 + 110;

        // Visualizer Section
        childFrame.addWidget(new ScrollableTextWidget(
                calc1, screen.getButtonY(controlIndex),
                calc2 - calc1,
                Constants.TRANSLATOR.translate("gui.config.message.visualizer")
        ));

        // Adding Party Toggle
        if (showPartyToggle) {
            childFrame.addControl(
                    new ExtendedButtonControl(
                            calc2, screen.getButtonY(controlIndex),
                            95, 20,
                            Constants.TRANSLATOR.translate("gui.config.message.visualizer.toggle_party"),
                            () -> showPartyData = !showPartyData,
                            () -> screen.drawMultiLineString(
                                    StringUtils.splitTextByNewLine(
                                            Constants.TRANSLATOR.translate("gui.config.message.hover.visualizer.toggle_party")
                                    )
                            )
                    )
            );
        }

        // Adding Refresh Button
        if (!autoRefresh) {
            childFrame.addControl(
                    new ExtendedButtonControl(
                            rightButtonPos, screen.getButtonY(controlIndex),
                            70, 20,
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

        // Adding RPC Lines (First Line = Title Bar)
        for (int i = 0; i < 4; i++) {
            lines.add(childFrame.addWidget(new ScrollableTextWidget(
                    textOffset, screen.getButtonY(controlIndex, -4 + ((screen.getFontHeight() + 2) * i)),
                    textWidth, ""
            )));
        }

        // Adding Additional Buttons
        for (int i = 0; i < 2; i++) {
            buttons.add(childFrame.addControl(
                    new ExtendedButtonControl(
                            rightButtonPos, screen.getButtonY(controlIndex++),
                            95, 20, ""
                    )
            ));
        }

        refreshVisualizer(richPresence);
    }

    /**
     * Refresh active visualizer data with the specified data
     *
     * @param richPresence The Supplier for the compiled rich presence
     */
    private void refreshVisualizer(final Supplier<CompiledPresence> richPresence) {
        if (richPresence == null) {
            return;
        }

        // Compile the RichPresence data from current instance
        lastCompiledPresence = richPresence.get();

        if (lastCompiledPresence == null) {
            return;
        }

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

        final boolean isMain = lastCompiledPresence.isMain();

        String details = lastCompiledPresence.details();
        String state = lastCompiledPresence.state();
        if (showPartyData || isMain) {
            final int size = isMain ? CraftPresence.CLIENT.PARTY_SIZE : 1;
            final int max = isMain ? CraftPresence.CLIENT.PARTY_MAX : 5;

            if (size > 0 && max >= size) {
                state = lastCompiledPresence.state(size, max);
            }
        }

        if (lastCompiledPresence.activityType() == ActivityType.Listening ||
                lastCompiledPresence.activityType() == ActivityType.Watching ||
                lastCompiledPresence.activityType() == ActivityType.Competing) {
            updateLineTexts(
                    details,
                    state,
                    lastCompiledPresence.largeImageText()
            );
        } else {
            updateLineTexts(
                    CraftPresence.CLIENT.CURRENT_TITLE,
                    details,
                    state,
                    lastCompiledPresence.getTimeString()
            );
        }

        updateButtonTexts(
                lastCompiledPresence.getButtonData()
        );
    }

    /**
     * Update the button widgets with the specified data
     *
     * @param validButtons The list of valid button data
     */
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

    /**
     * Retrieve the prefix normally used for title text
     *
     * @return The title string prefix
     */
    private String getTitlePrefix() {
        return !CraftPresence.CONFIG.accessibilitySettings.stripTranslationFormatting ? "§l" : "";
    }

    /**
     * Update the text widgets with the specified data
     *
     * @param strings The list of string data to interpret
     */
    private void updateLineTexts(final String... strings) {
        final List<String> validStrings = StringUtils.newArrayList();
        for (String string : strings) {
            if (!StringUtils.isNullOrEmpty(string)) {
                validStrings.add(string);
            }
        }

        boolean isTitleBar = true;
        for (int i = 0; i < lines.size(); i++) {
            final ScrollableTextWidget lineWidget = lines.get(i);
            if (i < validStrings.size()) {
                final String prefix = isTitleBar ? getTitlePrefix() : "";
                lineWidget.setMessage(prefix + validStrings.get(i));
                isTitleBar = false;
            } else {
                lineWidget.setMessage("");
            }
        }
    }

    /**
     * The post-render event to occur
     * <p>Should only be run in {@link ExtendedScreen#postRender()}
     *
     * @param childFrame The screen instance to use
     */
    public void postRender(final ExtendedScreen childFrame) {
        if (autoRefresh) {
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
