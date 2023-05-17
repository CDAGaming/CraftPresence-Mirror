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

package com.gitlab.cdagaming.craftpresence.config.element;

import com.gitlab.cdagaming.craftpresence.config.Module;
import com.gitlab.cdagaming.craftpresence.utils.StringUtils;

import java.awt.*;
import java.io.Serializable;

public class ColorData extends Module implements Serializable {
    private static final ColorData DEFAULT = new ColorData();

    private final ColorSection start = new ColorSection();
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
        if (target instanceof ColorData && !equals(target)) {
            final ColorData data = (ColorData) target;

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

    public ColorSection getEnd() {
        return new ColorSection(end != null ? end : start);
    }

    public Color getEndColor() {
        return getEnd().getColor();
    }

    public void setEndColor(final ColorSection color) {
        if (color != null) {
            if (end == null) {
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

    public String getTexLocation() {
        return texLocation;
    }

    public void setTexLocation(String texLocation) {
        this.texLocation = texLocation;
    }

    @Override
    protected Object getProperty(String name) {
        return StringUtils.getField(ColorData.class, this);
    }

    @Override
    protected void setProperty(String name, Object value) {
        StringUtils.updateField(ColorData.class, this, value, name);
    }
}
