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

import com.gitlab.cdagaming.craftpresence.utils.gui.integrations.ExtendedScreen;

/**
 * Interface for a Render-Only GUI Control
 * <p>
 * Use one of the available Extended Controls for interactive controls
 *
 * @author CDAGaming
 */
public interface Widget {
    /**
     * Renders this Control, including any extra data
     *
     * @param screen The Screen instance we're rendering to
     */
    default void draw(ExtendedScreen screen) {
        // N/A
    }

    /**
     * Get the left-most coordinate for this Control
     *
     * @return The left-most coordinate for this Control
     */
    default int getLeft() {
        return getX();
    }

    /**
     * Get the right-most coordinate for this Control
     *
     * @return The right-most coordinate for this Control
     */
    default int getRight() {
        return getX() + getWidth();
    }

    /**
     * Get the bottom-most coordinate for this Control
     *
     * @return The bottom-most coordinate for this Control
     */
    default int getBottom() {
        return getY() + getHeight();
    }

    /**
     * Get the top-most coordinate for this Control
     *
     * @return The top-most coordinate for this Control
     */
    default int getTop() {
        return getY();
    }

    /**
     * Retrieves the Current X Position of this Control
     *
     * @return the Current X Position of this Control
     */
    int getX();

    /**
     * Sets the Current X Position of this Control
     *
     * @param posX the new X Position of this Control
     */
    void setX(int posX);

    /**
     * Retrieves the Current Y Position of this Control
     *
     * @return the Current Y Position of this Control
     */
    int getY();

    /**
     * Sets the Current Y Position of this Control
     *
     * @param posY the new Y Position of this Control
     */
    void setY(int posY);

    /**
     * Retrieves the Current Width of this Control
     *
     * @return The Current Width of this Control
     */
    int getWidth();

    /**
     * Sets the Current Width of this Control
     *
     * @param width the new Width of this Control
     */
    void setWidth(int width);

    /**
     * Retrieves the Current Height of this Control
     *
     * @return The Current Height of this Control
     */
    int getHeight();

    /**
     * Sets the Current Height of this Control
     *
     * @param height the new Height of this Control
     */
    void setHeight(int height);
}
