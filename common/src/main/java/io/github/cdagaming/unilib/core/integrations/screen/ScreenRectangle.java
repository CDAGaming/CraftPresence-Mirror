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

package io.github.cdagaming.unilib.core.integrations.screen;

import javax.annotation.Nullable;

/**
 * Represents a rectangular area on the screen, defined by its position (X and Y coordinates)
 * and its dimensions (width and height). This class provides methods to access its properties
 * and to compute the intersection with another {@link ScreenRectangle}, which can be used
 * in various graphical computations, such as scissor tests in OpenGL.
 *
 * @param posX   The X coordinate of the rectangle.
 * @param posY   The Y coordinate of the rectangle.
 * @param width  The width of the rectangle.
 * @param height The height of the rectangle.
 * @author CDAGaming
 */
public record ScreenRectangle(int posX, int posY, int width, int height) {
    /**
     * A constant representing an empty {@link ScreenRectangle} with no area (width and height are 0).
     */
    private static final ScreenRectangle EMPTY = new ScreenRectangle(0, 0, 0, 0);

    /**
     * Constructs a new {@code ScreenRectangle} with the specified position and dimensions.
     *
     * @param posX   The X coordinate of the rectangle.
     * @param posY   The Y coordinate of the rectangle.
     * @param width  The width of the rectangle.
     * @param height The height of the rectangle.
     */
    public ScreenRectangle {
    }

    /**
     * Returns a {@link ScreenRectangle} that represents an empty area.
     *
     * @return A {@link ScreenRectangle} with zero width and height.
     */
    public static ScreenRectangle empty() {
        return EMPTY;
    }

    /**
     * Computes the intersection of this rectangle with another {@link ScreenRectangle}.
     * If the rectangles do not overlap, this method returns {@code null}.
     *
     * @param rectangle The {@link ScreenRectangle} to intersect with this rectangle.
     * @return A new {@link ScreenRectangle} representing the intersection of the two rectangles,
     * or {@code null} if they do not overlap.
     */
    @Nullable
    public ScreenRectangle intersection(final ScreenRectangle rectangle) {
        final int i = Math.max(getLeft(), rectangle.getLeft());
        final int j = Math.max(getTop(), rectangle.getTop());
        final int k = Math.min(getRight(), rectangle.getRight());
        final int l = Math.min(getBottom(), rectangle.getBottom());
        return i < k && j < l ? new ScreenRectangle(i, j, k - i, l - j) : null;
    }

    /**
     * Returns the X coordinate of the rectangle.
     *
     * @return The X coordinate of the rectangle.
     */
    @Override
    public int posX() {
        return posX;
    }

    /**
     * Returns the Y coordinate of the rectangle.
     *
     * @return The Y coordinate of the rectangle.
     */
    @Override
    public int posY() {
        return posY;
    }

    /**
     * Returns the width of the rectangle.
     *
     * @return The width of the rectangle.
     */
    @Override
    public int width() {
        return width;
    }

    /**
     * Returns the height of the rectangle.
     *
     * @return The height of the rectangle.
     */
    @Override
    public int height() {
        return height;
    }

    /**
     * Returns the top-most coordinate of the rectangle.
     *
     * @return The top-most coordinate of the rectangle.
     */
    public int getTop() {
        return posY();
    }

    /**
     * Returns the bottom-most coordinate of the rectangle.
     *
     * @return The bottom-most coordinate of the rectangle.
     */
    public int getBottom() {
        return posY() + height();
    }

    /**
     * Returns the left-most coordinate of the rectangle.
     *
     * @return The left-most coordinate of the rectangle.
     */
    public int getLeft() {
        return posX();
    }

    /**
     * Returns the right-most coordinate of the rectangle.
     *
     * @return The right-most coordinate of the rectangle.
     */
    public int getRight() {
        return posX() + width();
    }
}
