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

import com.gitlab.cdagaming.craftpresence.core.config.element.ColorData;
import com.gitlab.cdagaming.unilib.utils.gui.RenderUtils;
import com.gitlab.cdagaming.unilib.utils.gui.integrations.ExtendedScreen;
import edu.umd.cs.findbugs.annotations.SuppressFBWarnings;

import java.util.function.Supplier;

/**
 * Implementation for a Simple Textured Graphic Widget
 *
 * @author CDAGaming
 */
public class TexturedWidget implements DynamicWidget {
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
    private int height;
    /**
     * The vertical offset to render the content to
     */
    private double offset;
    /**
     * Supplier for the factor at which to tint the content to
     */
    private Supplier<Float> tintSupplier;
    /**
     * Supplier for the {@link ColorData} to be used to render the content
     */
    private Supplier<ColorData> infoSupplier;
    /**
     * Whether the content should have a surrounding border
     */
    private boolean hasBorder;

    /**
     * Initialization Event for this Control, assigning defined arguments
     *
     * @param startX       The starting X position of the widget
     * @param startY       The starting Y position of the widget
     * @param width        The width of the widget
     * @param height       The height of the widget
     * @param offset       The vertical offset to render the content to
     * @param tintSupplier The Supplier for the factor at which to tint the content to
     * @param infoSupplier The Supplier for the {@link ColorData} to be used to render the content
     * @param hasBorder    Whether the content should have a surrounding border
     */
    @SuppressFBWarnings("EI_EXPOSE_REP2")
    public TexturedWidget(
            final int startX, final int startY,
            final int width, final int height,
            final double offset,
            final Supplier<Float> tintSupplier,
            final Supplier<ColorData> infoSupplier,
            final boolean hasBorder) {
        setControlPosX(startX);
        setControlPosY(startY);
        setControlWidth(width);
        setControlHeight(height);
        setOffset(offset);
        setTintSupplier(tintSupplier);
        setInfoSupplier(infoSupplier);
        setBorderState(hasBorder);
    }

    @Override
    public void preDraw(ExtendedScreen screen) {
        // N/A
    }

    @Override
    public void draw(ExtendedScreen screen) {
        if (hasBorder()) {
            RenderUtils.drawGradientBox(
                    getControlPosX() - 1, getControlPosY() - 1,
                    getControlWidth() + 2, getControlHeight() + 2,
                    300,
                    "#000000", "#000000",
                    1,
                    null, null
            );
        }
        screen.drawBackground(
                getLeft(), getRight(),
                getTop(), getBottom(),
                getOffset(), getTintFactor(),
                0.0D, 0.0D,
                getInfo()
        );
    }

    @Override
    public void postDraw(ExtendedScreen screen) {
        // N/A
    }

    /**
     * Retrieve the vertical offset to render the content to
     *
     * @return the current vertical offset
     */
    public double getOffset() {
        return offset;
    }

    /**
     * Set the vertical offset to render the content to
     *
     * @param offset the new vertical offset
     */
    public void setOffset(double offset) {
        this.offset = offset;
    }

    /**
     * Retrieve the Supplier for the factor at which to tint the content to
     *
     * @return the current tint factor supplier
     */
    public Supplier<Float> getTintSupplier() {
        return tintSupplier;
    }

    /**
     * Set the Supplier for the factor at which to tint the content to
     *
     * @param tintSupplier the new tint factor supplier
     */
    public void setTintSupplier(Supplier<Float> tintSupplier) {
        this.tintSupplier = tintSupplier;
    }

    /**
     * Retrieve the factor at which to tint the content to
     *
     * @return the current tint factor
     */
    public float getTintFactor() {
        return getTintSupplier().get();
    }

    /**
     * Retrieve the Supplier for the {@link ColorData} to be used to render the content
     *
     * @return the current supplier for the {@link ColorData} info
     */
    public Supplier<ColorData> getInfoSupplier() {
        return infoSupplier;
    }

    /**
     * Set the Supplier for the {@link ColorData} to be used to render the content
     *
     * @param infoSupplier the new supplier for the {@link ColorData} info
     */
    public void setInfoSupplier(Supplier<ColorData> infoSupplier) {
        this.infoSupplier = infoSupplier;
    }

    /**
     * Retrieve the {@link ColorData} to be used to render the content
     *
     * @return the {@link ColorData} info
     */
    public ColorData getInfo() {
        return getInfoSupplier().get();
    }

    /**
     * Retrieve whether the content should have a surrounding border
     *
     * @return the current border state
     */
    public boolean hasBorder() {
        return hasBorder;
    }

    /**
     * Set whether the content should have a surrounding border
     *
     * @param hasBorder the new border state
     */
    public void setBorderState(boolean hasBorder) {
        this.hasBorder = hasBorder;
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
        return height;
    }

    @Override
    public void setControlHeight(int height) {
        this.height = height;
    }
}
