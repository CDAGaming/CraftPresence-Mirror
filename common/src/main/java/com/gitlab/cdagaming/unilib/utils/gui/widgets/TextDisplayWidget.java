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

import edu.umd.cs.findbugs.annotations.SuppressFBWarnings;
import io.github.cdagaming.unicore.utils.MathUtils;
import io.github.cdagaming.unicore.utils.StringUtils;
import com.gitlab.cdagaming.unilib.utils.gui.integrations.ExtendedScreen;
import com.gitlab.cdagaming.unilib.utils.gui.integrations.ScrollPane;

import java.util.List;
import java.util.Objects;

/**
 * Implementation for a Scrollable Text-Only Widget
 * <p>This is designed for multi-line text, use {@link ScrollableTextWidget} for single-line
 *
 * @author CDAGaming
 */
public class TextDisplayWidget implements DynamicWidget {
    /**
     * Whether render content needs an update
     */
    private boolean needsUpdate = false;
    /**
     * The starting X position of the widget
     */
    private int startX;
    /**
     * The starting Y position of the widget
     */
    private int startY;
    /**
     * The width of the widget
     */
    private int width;
    /**
     * The height of the widget's content, used for scrolling
     */
    private int contentHeight;
    /**
     * The text to be rendered with this widget
     */
    private String message;
    /**
     * The multi-lined version of the interpreting message
     */
    private List<String> renderLines;
    /**
     * Whether the text should be center-aligned
     */
    private boolean centered;

    /**
     * Initialization Event for this Control, assigning defined arguments
     *
     * @param centered Whether the text should be center-aligned
     * @param startX   The starting X position of the widget
     * @param startY   The starting Y position of the widget
     * @param width    The width of the widget
     * @param message  The text to be rendered with this widget
     */
    @SuppressFBWarnings("EI_EXPOSE_REP2")
    public TextDisplayWidget(final boolean centered, final int startX, final int startY, final int width, final String message) {
        setCentered(centered);
        setControlPosX(startX);
        setControlPosY(startY);
        setControlWidth(width);
        setMessage(message);
    }

    /**
     * Initialization Event for this Control, assigning defined arguments
     *
     * @param startX  The starting X position of the widget
     * @param startY  The starting Y position of the widget
     * @param width   The width of the widget
     * @param message The text to be rendered with this widget
     */
    public TextDisplayWidget(final int startX, final int startY, final int width, final String message) {
        this(false, startX, startY, width, message);
    }

    /**
     * Initialization Event for this Control, assigning defined arguments
     *
     * @param centered Whether the text should be center-aligned
     * @param startX   The starting X position of the widget
     * @param startY   The starting Y position of the widget
     * @param width    The width of the widget
     */
    public TextDisplayWidget(final boolean centered, final int startX, final int startY, final int width) {
        this(centered, startX, startY, width, "");
    }

    /**
     * Initialization Event for this Control, assigning defined arguments
     *
     * @param startX The starting X position of the widget
     * @param startY The starting Y position of the widget
     * @param width  The width of the widget
     */
    public TextDisplayWidget(final int startX, final int startY, final int width) {
        this(false, startX, startY, width);
    }

    /**
     * Initialization Event for this Control, assigning defined arguments
     *
     * @param centered Whether the text should be center-aligned
     * @param width    The width of the widget
     * @param message  The text to be rendered with this widget
     */
    public TextDisplayWidget(final boolean centered, final int width, final String message) {
        this(centered, 0, 0, width, message);
    }

    /**
     * Initialization Event for this Control, assigning defined arguments
     *
     * @param width   The width of the widget
     * @param message The text to be rendered with this widget
     */
    public TextDisplayWidget(final int width, final String message) {
        this(false, width, message);
    }

    /**
     * Initialization Event for this Control, assigning defined arguments
     *
     * @param centered Whether the text should be center-aligned
     * @param width    The width of the widget
     */
    public TextDisplayWidget(final boolean centered, final int width) {
        this(centered, width, "");
    }

    /**
     * Initialization Event for this Control, assigning defined arguments
     *
     * @param width The width of the widget
     */
    public TextDisplayWidget(final int width) {
        this(false, width);
    }

    /**
     * Retrieve the text to be rendered with this widget
     *
     * @return the current render message
     */
    public String getMessage() {
        return message;
    }

    /**
     * Set the text to be rendered with this widget
     *
     * @param newMessage The new message to be rendered
     * @return the current instance, used for chain-building
     */
    public TextDisplayWidget setMessage(final String newMessage) {
        if (!Objects.equals(newMessage, message)) {
            message = newMessage;
            needsUpdate = true;
        }
        return this;
    }

    /**
     * Retrieve whether the text should be center-aligned
     *
     * @return the current render alignment state
     */
    public boolean isCentered() {
        return centered;
    }

    /**
     * Set whether the text should be center-aligned
     *
     * @param centered The new render alignment state
     * @return the current instance, used for chain-building
     */
    public TextDisplayWidget setCentered(final boolean centered) {
        this.centered = centered;
        return this;
    }

    /**
     * Retrieve the multi-lined version of the interpreting message
     *
     * @return the multi-lined render message
     */
    public List<String> getRenderLines() {
        return StringUtils.newArrayList(renderLines);
    }

    @Override
    public void preDraw(ExtendedScreen screen) {
        if (needsUpdate) {
            renderLines = refreshContent(screen);
            needsUpdate = false;
        }
    }

    @Override
    public void draw(ExtendedScreen screen) {
        int padding = 0, barWidth = 0;
        if (screen instanceof ScrollPane pane) {
            padding = pane.getPadding();
            barWidth = pane.getScrollBarWidth();
        }
        screen.drawMultiLineString(
                getRenderLines(),
                getControlPosX() + padding, getControlPosY() + padding,
                getControlWidth() - padding - barWidth, -1, -1,
                isCentered(), false
        );
    }

    @Override
    public void postDraw(ExtendedScreen screen) {
        // N/A
    }

    @Override
    public int getControlPosX() {
        return this.startX;
    }

    @Override
    public void setControlPosX(int posX) {
        this.startX = posX;
    }

    @Override
    public int getControlPosY() {
        return this.startY;
    }

    @Override
    public void setControlPosY(int posY) {
        this.startY = posY;
    }

    @Override
    public int getControlWidth() {
        return this.width;
    }

    @Override
    public void setControlWidth(int width) {
        this.width = width;
    }

    @Override
    public int getControlHeight() {
        return this.contentHeight;
    }

    @Override
    public void setControlHeight(int height) {
        this.contentHeight = height;
    }

    /**
     * Refresh the widget content, scaling the text accordingly
     *
     * @param screen The Screen instance we're rendering to
     * @return the modified render lines for the widget
     */
    public List<String> refreshContent(final ExtendedScreen screen) {
        int padding = 0, barWidth = 0;
        if (screen instanceof ScrollPane pane) {
            padding = pane.getPadding();
            barWidth = pane.getScrollBarWidth();
        }

        final int width = MathUtils.clamp((getControlWidth() - getControlPosX()) - (padding * 2) - barWidth, 0, screen.getMaxWidth());
        final List<String> content = screen.createRenderLines(
                getMessage(),
                width
        );
        final int height = content.size() * (screen.getFontHeight() + 1);
        setControlHeight(height + 2);

        screen.refreshContentHeight();
        return content;
    }
}
