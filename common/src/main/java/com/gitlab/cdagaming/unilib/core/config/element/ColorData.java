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

package com.gitlab.cdagaming.unilib.core.config.element;

import com.gitlab.cdagaming.unilib.core.config.Module;
import io.github.cdagaming.unicore.utils.StringUtils;

import java.awt.*;
import java.io.Serializable;
import java.util.Objects;

public class ColorData extends Module implements Serializable {
    private static final ColorData DEFAULT = new ColorData();

    private ColorSection start = new ColorSection();
    private ColorSection end;
    private String texLocation;

    // `ColorSection` Constructors

    public ColorData(final ColorSection start, final ColorSection end, final String texLocation) {
        setStartColor(start);
        setEndColor(end);
        setTexLocation(texLocation);
    }

    public ColorData(final ColorSection start, final ColorSection end) {
        this(start, end, "");
    }

    public ColorData(final ColorSection start, final String texLocation) {
        this(start, null, texLocation);
    }

    public ColorData(final ColorSection start) {
        this(start, "");
    }

    // `java.awt.Color` Constructors

    public ColorData(final Color start, final Color end, final String texLocation) {
        this(new ColorSection(start), end != null ? new ColorSection(end) : null, texLocation);
    }

    public ColorData(final Color start, final Color end) {
        this(start, end, "");
    }

    public ColorData(final Color start, final String texLocation) {
        this(start, null, texLocation);
    }

    public ColorData(final Color start) {
        this(start, "");
    }

    // Default Constructors

    public ColorData(final String texLocation) {
        this(new ColorSection(), texLocation);
    }

    public ColorData() {
        this("");
    }

    public ColorData(final ColorData other) {
        this(other.start, other.end, other.texLocation);
    }

    @Override
    public ColorData getDefaults() {
        return new ColorData(DEFAULT);
    }

    @Override
    public void transferFrom(Module target) {
        if (target instanceof ColorData data && !equals(target)) {
            setStartColor(data.start);
            setEndColor(data.end);
            setTexLocation(data.texLocation);
        }
    }

    @Override
    public ColorData copy() {
        return new ColorData(this);
    }

    public ColorSection getStart() {
        return new ColorSection(start);
    }

    public Color getStartColor() {
        return getStart().getColor();
    }

    public void setStartColor(final ColorSection color) {
        if (color != null) {
            start.red = color.red;
            start.green = color.green;
            start.blue = color.blue;
            start.alpha = color.alpha;
        }
    }

    public boolean hasEnd() {
        return end != null;
    }

    public ColorSection getEnd() {
        return new ColorSection(hasEnd() ? end : start);
    }

    public Color getEndColor() {
        return getEnd().getColor();
    }

    public void setEndColor(final ColorSection color) {
        if (color != null && !color.equals(getStart())) {
            if (!hasEnd()) {
                end = new ColorSection();
            }
            end.red = color.red;
            end.green = color.green;
            end.blue = color.blue;
            end.alpha = color.alpha;
        } else {
            end = null;
        }
    }

    public boolean hasTexLocation() {
        return !StringUtils.isNullOrEmpty(texLocation);
    }

    public String getTexLocation() {
        return hasTexLocation() ? texLocation : "";
    }

    public void setTexLocation(String texLocation) {
        this.texLocation = !StringUtils.isNullOrEmpty(texLocation) ? texLocation : null;
    }

    @Override
    public Object getProperty(String name) {
        return switch (name) {
            case "start" -> start;
            case "end" -> end;
            case "texLocation" -> texLocation;
            default -> null;
        };
    }

    @Override
    public void setProperty(String name, Object value) {
        try {
            switch (name) {
                case "start":
                    start = (ColorSection) value;
                    break;
                case "end":
                    end = (ColorSection) value;
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

        if (!(obj instanceof ColorData other)) {
            return false;
        }

        return Objects.equals(other.start, start) &&
                Objects.equals(other.end, end) &&
                Objects.equals(other.texLocation, texLocation);
    }

    @Override
    public int hashCode() {
        return Objects.hash(start, end, texLocation);
    }
}
