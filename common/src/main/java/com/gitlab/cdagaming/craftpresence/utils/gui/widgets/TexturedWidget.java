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

import com.gitlab.cdagaming.craftpresence.config.element.ColorData;
import com.gitlab.cdagaming.craftpresence.utils.gui.RenderUtils;
import com.gitlab.cdagaming.craftpresence.utils.gui.integrations.ExtendedScreen;
import edu.umd.cs.findbugs.annotations.SuppressFBWarnings;

import java.util.function.Supplier;

public class TexturedWidget implements DynamicWidget {
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
    private int height;
    private double offset;
    private float tintFactor;
    private Supplier<ColorData> infoSupplier;
    private boolean hasBorder;

    @SuppressFBWarnings("EI_EXPOSE_REP2")
    public TexturedWidget(final ExtendedScreen parent,
                          final int startX, final int startY,
                          final int width, final int height,
                          final double offset, final float tintFactor,
                          final Supplier<ColorData> infoSupplier,
                          final boolean hasBorder) {
        this.parent = parent;
        setControlPosX(startX);
        setControlPosY(startY);
        setControlWidth(width);
        setControlHeight(height);

        this.offset = offset;
        this.tintFactor = tintFactor;
        this.infoSupplier = infoSupplier;
        this.hasBorder = hasBorder;
    }

    @Override
    public void draw(ExtendedScreen screen) {
        if (hasBorder) {
            RenderUtils.drawGradientBox(
                    getControlPosX() - 1, getControlPosY() - 1,
                    getControlWidth() + 2, getControlHeight() + 2,
                    300,
                    "#000000", "#000000",
                    1,
                    null, null
            );
        }
        RenderUtils.drawBackground(
                parent.mc,
                getLeft(), getRight(),
                getTop(), getBottom(),
                offset, tintFactor, infoSupplier.get()
        );
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
