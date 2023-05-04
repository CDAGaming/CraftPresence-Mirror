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

package com.gitlab.cdagaming.craftpresence.utils.gui.widgets;

import com.gitlab.cdagaming.craftpresence.utils.MathUtils;
import com.gitlab.cdagaming.craftpresence.utils.StringUtils;
import com.gitlab.cdagaming.craftpresence.utils.gui.integrations.ExtendedScreen;
import com.gitlab.cdagaming.craftpresence.utils.gui.integrations.ScrollPane;
import edu.umd.cs.findbugs.annotations.SuppressFBWarnings;

import java.util.List;
import java.util.Objects;

/**
 * Implementation for a Scrollable Text-Only Widget
 *
 * @author CDAGaming
 */
public class TextDisplayWidget implements DynamicWidget {
    /**
     * The parent or source screen to refer to
     */
    private final ExtendedScreen parent;
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
    private boolean centered = false;

    /**
     * Initialization Event for this Control, assigning defined arguments
     *
     * @param parent   The parent or source screen to refer to
     * @param centered Whether the text should be center-aligned
     * @param startX   The starting X position of the widget
     * @param startY   The starting Y position of the widget
     * @param width    The width of the widget
     * @param message  The text to be rendered with this widget
     */
    @SuppressFBWarnings("EI_EXPOSE_REP2")
    public TextDisplayWidget(final ExtendedScreen parent, final boolean centered, final int startX, final int startY, final int width, final String message) {
        this.parent = parent;
        setCentered(centered);
        setControlPosX(startX);
        setControlPosY(startY);
        setControlWidth(width);
        setMessage(message);
    }

    /**
     * Initialization Event for this Control, assigning defined arguments
     *
     * @param parent  The parent or source screen to refer to
     * @param startX  The starting X position of the widget
     * @param startY  The starting Y position of the widget
     * @param width   The width of the widget
     * @param message The text to be rendered with this widget
     */
    public TextDisplayWidget(final ExtendedScreen parent, final int startX, final int startY, final int width, final String message) {
        this(parent, false, startX, startY, width, message);
    }

    /**
     * Initialization Event for this Control, assigning defined arguments
     *
     * @param parent   The parent or source screen to refer to
     * @param centered Whether the text should be center-aligned
     * @param startX   The starting X position of the widget
     * @param startY   The starting Y position of the widget
     * @param width    The width of the widget
     */
    public TextDisplayWidget(final ExtendedScreen parent, final boolean centered, final int startX, final int startY, final int width) {
        this(parent, centered, startX, startY, width, "");
    }

    /**
     * Initialization Event for this Control, assigning defined arguments
     *
     * @param parent The parent or source screen to refer to
     * @param startX The starting X position of the widget
     * @param startY The starting Y position of the widget
     * @param width  The width of the widget
     */
    public TextDisplayWidget(final ExtendedScreen parent, final int startX, final int startY, final int width) {
        this(parent, false, startX, startY, width);
    }

    /**
     * Initialization Event for this Control, assigning defined arguments
     *
     * @param parent   The parent or source screen to refer to
     * @param centered Whether the text should be center-aligned
     * @param width    The width of the widget
     * @param message  The text to be rendered with this widget
     */
    public TextDisplayWidget(final ExtendedScreen parent, final boolean centered, final int width, final String message) {
        this(parent, centered, 0, 0, width, message);
    }

    /**
     * Initialization Event for this Control, assigning defined arguments
     *
     * @param parent  The parent or source screen to refer to
     * @param width   The width of the widget
     * @param message The text to be rendered with this widget
     */
    public TextDisplayWidget(final ExtendedScreen parent, final int width, final String message) {
        this(parent, false, width, message);
    }

    /**
     * Initialization Event for this Control, assigning defined arguments
     *
     * @param parent   The parent or source screen to refer to
     * @param centered Whether the text should be center-aligned
     * @param width    The width of the widget
     */
    public TextDisplayWidget(final ExtendedScreen parent, final boolean centered, final int width) {
        this(parent, centered, width, "");
    }

    /**
     * Initialization Event for this Control, assigning defined arguments
     *
     * @param parent The parent or source screen to refer to
     * @param width  The width of the widget
     */
    public TextDisplayWidget(final ExtendedScreen parent, final int width) {
        this(parent, false, width);
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
            renderLines = refreshContent();
            parent.refreshContentHeight();
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
     * Retrieve the multi-lined version of the interpreting messag
     *
     * @return the multi-lined render message
     */
    public List<String> getRenderLines() {
        return StringUtils.newArrayList(renderLines);
    }

    @Override
    public void draw(ExtendedScreen screen) {
        int padding = 0;
        if (screen instanceof ScrollPane) {
            padding = ((ScrollPane) screen).getPadding();
        }
        int xPos = getControlPosX() + padding;
        int currentY = getControlPosY() + padding;
        for (String line : getRenderLines()) {
            if (isCentered()) {
                screen.renderCenteredString(line, getControlWidth() / 2f, currentY, 0xFFFFFF);
            } else {
                screen.renderString(line, xPos, currentY, 0xFFFFFF);
            }
            currentY += screen.getFontHeight() + 1;
        }
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
        return width - startX;
    }

    @Override
    public void setControlWidth(int width) {
        this.width = width;
    }

    @Override
    public int getControlHeight() {
        return contentHeight;
    }

    @Override
    public void setControlHeight(int height) {
        throw new UnsupportedOperationException();
    }

    /**
     * Refresh the widget content, scaling the text accordingly
     *
     * @return the modified render lines for the widget
     */
    private List<String> refreshContent() {
        final int width = MathUtils.clamp(getControlWidth(), 0, parent.getMaxWidth());
        final List<String> content = parent.createRenderLines(
                getMessage(),
                width
        );
        contentHeight = content.size() * (parent.getFontHeight() + 1);
        return content;
    }
}
