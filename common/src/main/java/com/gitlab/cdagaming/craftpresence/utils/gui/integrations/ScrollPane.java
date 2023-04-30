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

package com.gitlab.cdagaming.craftpresence.utils.gui.integrations;

import com.gitlab.cdagaming.craftpresence.utils.MathUtils;
import com.gitlab.cdagaming.craftpresence.utils.StringUtils;
import com.gitlab.cdagaming.craftpresence.utils.gui.RenderUtils;
import com.gitlab.cdagaming.craftpresence.utils.gui.widgets.DynamicWidget;

import java.awt.*;

/**
 * Implementation for a Scrollable Screen Pane
 *
 * @author CDAGaming
 */
public class ScrollPane extends ExtendedScreen {
    private static final Color NERO = StringUtils.getColorFrom(32, 32, 32);
    private static final Color NONE = StringUtils.getColorFrom(0, 0, 0, 0);
    private static final int DEFAULT_PADDING = 4;
    private static final int DEFAULT_BAR_WIDTH = 6;
    private static final int DEFAULT_HEIGHT_PER_SCROLL = 8;
    private boolean clickedScrollbar;
    private int padding;
    private float amountScrolled;
    // remove in 1.13+
    private int mousePrevX = 0;
    // remove in 1.13+
    private int mousePrevY = 0;

    /**
     * Initialization Event for this Control, assigning defined arguments
     *
     * @param startX  The starting X position of the widget
     * @param startY  The starting Y position of the widget
     * @param width   The width of the widget
     * @param height  The height of the widget
     * @param padding The padding for the widget
     */
    public ScrollPane(final int startX, final int startY, final int width, final int height, final int padding) {
        super();
        setScreenX(startX);
        setScreenY(startY);
        setScreenWidth(width - startX);
        setScreenHeight(height - startY);
        setPadding(padding);
        setScroll(0);
    }

    /**
     * Initialization Event for this Control, assigning defined arguments
     *
     * @param startX The starting X position of the widget
     * @param startY The starting Y position of the widget
     * @param width  The width of the widget
     * @param height The height of the widget
     */
    public ScrollPane(final int startX, final int startY, final int width, final int height) {
        this(startX, startY, width, height, DEFAULT_PADDING);
    }

    /**
     * Initialization Event for this Control, assigning defined arguments
     *
     * @param width   The width of the widget
     * @param height  The height of the widget
     * @param padding The padding for the widget
     */
    public ScrollPane(final int width, final int height, final int padding) {
        this(0, 0, width, height, padding);
    }

    /**
     * Initialization Event for this Control, assigning defined arguments
     *
     * @param width  The width of the widget
     * @param height The height of the widget
     */
    public ScrollPane(final int width, final int height) {
        this(width, height, DEFAULT_PADDING);
    }

    @Override
    public void resetMouseScroll() {
        super.resetMouseScroll();
        setScroll(getMouseScroll());
    }

    @Override
    public void refreshContentHeight() {
        super.refreshContentHeight();

        setContentHeight((int) (getContentHeight() + getAmountScrolled()));
    }

    @Override
    public double getOffset() {
        return getAmountScrolled();
    }

    @Override
    public Color getTint() {
        return NERO;
    }

    @Override
    public void postRender() {
        // Render Depth Decorations
        RenderUtils.drawGradient(
                getLeft(), getRight(), getTop(), getTop() + getPadding(),
                -100.0D,
                Color.black,
                NONE
        );
        RenderUtils.drawGradient(
                getLeft(), getRight(), getBottom() - getPadding(), getBottom(),
                -100.0D,
                NONE,
                Color.black
        );

        // Render Scrollbar Elements
        if (needsScrollbar()) {
            final int scrollBarX = getScrollBarX();
            final int scrollBarRight = scrollBarX + getScrollBarWidth();
            final int bottom = getBottom();
            final int top = getTop();
            final int maxScroll = getMaxScroll();
            final int screenHeight = getScreenHeight();
            final int height = getBarHeight();
            float barTop = getAmountScrolled() * (screenHeight - height) / maxScroll + top;
            if (barTop < top) {
                barTop = top;
            }

            RenderUtils.drawGradient(
                    scrollBarX, scrollBarRight, top, bottom,
                    0.0D,
                    Color.black, Color.black
            );

            RenderUtils.drawGradient(
                    scrollBarX, scrollBarRight, barTop, barTop + height,
                    0.0D,
                    Color.gray, Color.gray
            );

            RenderUtils.drawGradient(
                    scrollBarX, scrollBarRight - 1, barTop, barTop + height - 1,
                    0.0D,
                    Color.lightGray, Color.lightGray
            );
        }

        super.postRender();
    }

    // remove in 1.13+
    @Override
    protected void mouseClicked(int mouseX, int mouseY, int mouseButton) {
        if (isLoaded()) {
            checkScrollbarClick(mouseX, mouseY, mouseButton);
            mousePrevX = mouseX;
            mousePrevY = mouseY;

            super.mouseClicked(mouseX, mouseY, mouseButton);
        }
    }

    // remove in 1.13+
    @Override
    protected void mouseClickMove(int mouseX, int mouseY, int mouseButton, long timeSinceLastClick) {
        if (isLoaded()) {
            mouseDragged(mouseX, mouseY, mouseButton, mouseX - mousePrevX, mouseY - mousePrevY);
            mousePrevX = mouseX;
            mousePrevY = mouseY;

            super.mouseClickMove(mouseX, mouseY, mouseButton, timeSinceLastClick);
        }
    }

    public void mouseDragged(int mouseX, int mouseY, int button, int deltaX, int deltaY) {
        if (button == 0 && needsScrollbar() && clickedScrollbar) {
            if (mouseY < getTop()) {
                setScroll(0.0F);
            } else if (mouseY > getBottom()) {
                setScroll(getMaxScroll());
            } else {
                final int height = getBarHeight();
                final int scrollLimit = Math.max(1, getMaxScroll());
                final int heightPerScroll = Math.max(1, scrollLimit / (getScreenHeight() - height));
                scrollBy(deltaY * heightPerScroll);
            }
        }
    }

    @Override
    public void mouseScrolled(int mouseX, int mouseY, int wheelY) {
        scrollBy(-wheelY * getHeightPerScroll());
    }

    /**
     * Retrieve the padding for the widget
     *
     * @return the padding for the widget
     */
    public int getPadding() {
        return padding;
    }

    /**
     * Sets the padding for the widget
     *
     * @param newPadding the new padding for the widget
     */
    public void setPadding(final int newPadding) {
        padding = newPadding;
    }

    /**
     * Retrieve the current starting X position of the scroll bar
     *
     * @return the starting X position of the scroll bar
     */
    public int getScrollBarX() {
        return getRight() - getScrollBarWidth();
    }

    /**
     * Retrieve the scroll bar width
     *
     * @return the scroll bar width
     */
    public int getScrollBarWidth() {
        return DEFAULT_BAR_WIDTH;
    }

    /**
     * Retrieve the height to scroll by, per scroll
     *
     * @return the height per scroll
     */
    public int getHeightPerScroll() {
        return DEFAULT_HEIGHT_PER_SCROLL;
    }

    /**
     * Append the scroll by the specified amount
     *
     * @param amount The amount to append the scroll by
     */
    public void scrollBy(final float amount) {
        setScroll(getAmountScrolled() + amount);
    }

    /**
     * Set the scroll to the specified amount
     *
     * @param amount the new scroll amount
     */
    public void setScroll(final float amount) {
        final float prevScrollAmount = getAmountScrolled();
        setAmountScrolled(amount);
        bindAmountScrolled();

        if (getAmountScrolled() != prevScrollAmount) {
            final int scrollDiff = (int) (getAmountScrolled() - prevScrollAmount);
            for (DynamicWidget widget : getWidgets()) {
                widget.setControlPosY(widget.getControlPosY() - scrollDiff);
            }
        }
    }

    /**
     * Determine if the scrollbar has been clicked
     *
     * @param mouseX The Event Mouse X Coordinate
     * @param mouseY The Event Mouse Y Coordinate
     * @param button The Event Mouse Button Clicked
     */
    public void checkScrollbarClick(double mouseX, double mouseY, int button) {
        clickedScrollbar = button == 0 && MathUtils.isWithinValue(mouseX, getScrollBarX(), getScrollBarX() + getScrollBarWidth(), true, false);
    }

    /**
     * Clamp the scroll amount between 0 and {@link ScrollPane#getMaxScroll()}
     */
    public void bindAmountScrolled() {
        setAmountScrolled(MathUtils.clamp(getAmountScrolled(), 0, getMaxScroll()));
    }

    /**
     * Retrieve the current scroll amount
     *
     * @return the current scroll amount
     */
    public float getAmountScrolled() {
        return amountScrolled;
    }

    /**
     * Directly sets the current scroll amount
     * <p>It is recommended to use {@link ScrollPane#scrollBy(float)} or {@link ScrollPane#setScroll(float)} instead
     *
     * @param scrolled the new scroll amound
     */
    public void setAmountScrolled(final float scrolled) {
        this.amountScrolled = scrolled;
    }

    /**
     * Get the maximum scroll height
     *
     * @return the maximum scroll height
     */
    public int getMaxScroll() {
        return Math.max(0, getContentHeight() - (getBottom() - getPadding()));
    }

    /**
     * Retrieve the height of the scrollbar
     *
     * @return the total height of the scrollbar
     */
    public int getBarHeight() {
        if (!needsScrollbar()) return 0;
        final int barHeight = (getScreenHeight() * getScreenHeight()) / getContentHeight();
        return MathUtils.clamp(barHeight, 32, getScreenHeight() - (getPadding() * 2));
    }

    /**
     * Retrieve whether this widget needs a scrollbar
     *
     * @return {@link Boolean#TRUE} if a scrollbar is needed
     */
    public boolean needsScrollbar() {
        return getMaxScroll() > 0;
    }

    @Override
    public int getMaxWidth() {
        return getScreenWidth() - getPadding() - getScrollBarWidth();
    }
}
