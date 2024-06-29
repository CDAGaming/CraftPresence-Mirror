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

package com.gitlab.cdagaming.unilib.utils.gui.widgets;

import com.gitlab.cdagaming.unilib.utils.gui.integrations.ExtendedScreen;
import com.gitlab.cdagaming.unilib.utils.gui.integrations.ScrollPane;

import java.util.List;

/**
 * Implementation for a Scrollable Text-Only Widget
 * <p>This is designed for single-line text, use {@link TextDisplayWidget} for multi-line
 *
 * @author CDAGaming
 */
public class ScrollableTextWidget extends TextDisplayWidget {

    /**
     * Initialization Event for this Control, assigning defined arguments
     *
     * @param centered Whether the text should be center-aligned
     * @param startX   The starting X position of the widget
     * @param startY   The starting Y position of the widget
     * @param width    The width of the widget
     * @param message  The text to be rendered with this widget
     */
    public ScrollableTextWidget(final boolean centered, final int startX, final int startY, final int width, final String message) {
        super(centered, startX, startY, width, message);
    }

    /**
     * Initialization Event for this Control, assigning defined arguments
     *
     * @param startX  The starting X position of the widget
     * @param startY  The starting Y position of the widget
     * @param width   The width of the widget
     * @param message The text to be rendered with this widget
     */
    public ScrollableTextWidget(final int startX, final int startY, final int width, final String message) {
        super(startX, startY, width, message);
    }

    /**
     * Initialization Event for this Control, assigning defined arguments
     *
     * @param centered Whether the text should be center-aligned
     * @param startX   The starting X position of the widget
     * @param startY   The starting Y position of the widget
     * @param width    The width of the widget
     */
    public ScrollableTextWidget(final boolean centered, final int startX, final int startY, final int width) {
        super(centered, startX, startY, width);
    }

    /**
     * Initialization Event for this Control, assigning defined arguments
     *
     * @param startX The starting X position of the widget
     * @param startY The starting Y position of the widget
     * @param width  The width of the widget
     */
    public ScrollableTextWidget(final int startX, final int startY, final int width) {
        super(startX, startY, width);
    }

    /**
     * Initialization Event for this Control, assigning defined arguments
     *
     * @param centered Whether the text should be center-aligned
     * @param width    The width of the widget
     * @param message  The text to be rendered with this widget
     */
    public ScrollableTextWidget(final boolean centered, final int width, final String message) {
        super(centered, width, message);
    }

    /**
     * Initialization Event for this Control, assigning defined arguments
     *
     * @param width   The width of the widget
     * @param message The text to be rendered with this widget
     */
    public ScrollableTextWidget(final int width, final String message) {
        super(width, message);
    }

    /**
     * Initialization Event for this Control, assigning defined arguments
     *
     * @param centered Whether the text should be center-aligned
     * @param width    The width of the widget
     */
    public ScrollableTextWidget(final boolean centered, final int width) {
        super(centered, width);
    }

    /**
     * Initialization Event for this Control, assigning defined arguments
     *
     * @param width The width of the widget
     */
    public ScrollableTextWidget(final int width) {
        super(width);
    }

    @Override
    public void draw(ExtendedScreen screen) {
        int padding = 0, barWidth = 0;
        if (screen instanceof ScrollPane pane) {
            padding = pane.getPadding();
            barWidth = pane.getScrollBarWidth();
        }

        final int minX = getControlPosX() + padding;
        final int maxX = getRight() - padding - barWidth;
        int centerX;
        if (isCentered()) {
            centerX = maxX - ((maxX - minX) / 2);
        } else {
            centerX = minX + (screen.getStringWidth(getMessage()) / 2);
        }
        screen.renderScrollingString(
                getMessage(),
                centerX,
                minX,
                getControlPosY() + padding,
                maxX,
                getBottom() - padding,
                0xFFFFFF
        );
    }

    @Override
    public List<String> refreshContent(final ExtendedScreen screen) {
        return null;
    }

    @Override
    public List<String> getRenderLines() {
        return null;
    }

    @Override
    public int getControlHeight() {
        return 20;
    }
}
