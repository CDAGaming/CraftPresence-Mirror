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

package com.gitlab.cdagaming.craftpresence.core.config.element;

import com.gitlab.cdagaming.craftpresence.core.config.Module;

import java.awt.*;
import java.io.Serializable;
import java.util.Objects;

public class ColorSection extends Module implements Serializable {
    private static final ColorSection DEFAULT = new ColorSection();

    public int red;
    public int green;
    public int blue;
    public int alpha;

    public ColorSection(final int red, final int green, final int blue, final int alpha) {
        this.red = red;
        this.green = green;
        this.blue = blue;
        this.alpha = alpha;
    }

    public ColorSection(final Color color) {
        this(color.getRed(), color.getGreen(), color.getBlue(), color.getAlpha());
    }

    public ColorSection(final ColorSection other) {
        this(other.red, other.green, other.blue, other.alpha);
    }

    public ColorSection() {
        this(Color.white);
    }

    public Color getColor() {
        return new Color(this.red, this.green, this.blue, this.alpha);
    }

    @Override
    public ColorSection getDefaults() {
        return new ColorSection(DEFAULT);
    }

    @Override
    public void transferFrom(Module target) {
        if (target instanceof ColorSection && !equals(target)) {
            final ColorSection data = (ColorSection) target;

            red = data.red;
            green = data.green;
            blue = data.blue;
            alpha = data.alpha;
        }
    }

    @Override
    public ColorSection copy() {
        return new ColorSection(this);
    }

    @Override
    public Object getProperty(String name) {
        switch (name) {
            case "red":
                return red;
            case "green":
                return green;
            case "blue":
                return blue;
            case "alpha":
                return alpha;
            default:
                return null;
        }
    }

    @Override
    public void setProperty(String name, Object value) {
        try {
            switch (name) {
                case "red":
                    red = (Integer) value;
                    break;
                case "green":
                    green = (Integer) value;
                    break;
                case "blue":
                    blue = (Integer) value;
                    break;
                case "alpha":
                    alpha = (Integer) value;
                    break;
                default:
                    break;
            }
        } catch (Throwable ex) {
            printException(ex);
        }
    }

    @Override
    public boolean equals(Object obj) {
        if (obj == this) {
            return true;
        }

        if (!(obj instanceof ColorSection)) {
            return false;
        }

        final ColorSection other = (ColorSection) obj;

        return Objects.equals(other.red, red) &&
                Objects.equals(other.green, green) &&
                Objects.equals(other.blue, blue) &&
                Objects.equals(other.alpha, alpha);
    }

    @Override
    public int hashCode() {
        return Objects.hash(red, green, blue, alpha);
    }
}
