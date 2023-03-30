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

public interface Widget {
    default void draw(ExtendedScreen screen) {
        // N/A
    }

    default int getLeft() {
        return getX();
    }

    default int getRight() {
        return getX() + getWidth();
    }

    default int getBottom() {
        return getY() + getHeight();
    }

    default int getTop() {
        return getY();
    }

    /**
     * Retrieves the Current X Position of this Control
     *
     * @return the Current X Position of this Control
     */
    int getX();

    void setX(int posX);

    /**
     * Retrieves the Current Y Position of this Control
     *
     * @return the Current Y Position of this Control
     */
    int getY();

    void setY(int posY);

    /**
     * Retrieves the Current Width of this Control
     *
     * @return The Current Width of this Control
     */
    int getWidth();

    void setWidth(int width);

    /**
     * Retrieves the Current Height of this Control
     *
     * @return The Current Height of this Control
     */
    int getHeight();

    void setHeight(int height);
}
