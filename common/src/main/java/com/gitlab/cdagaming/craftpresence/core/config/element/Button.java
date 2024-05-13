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
import io.github.cdagaming.unicore.utils.StringUtils;

import java.io.Serializable;
import java.util.Objects;

public class Button extends Module implements Serializable {
    private static final long serialVersionUID = 636718807992670138L;
    private static final Button DEFAULT = new Button();

    public String label;
    public String url;

    public Button(final String label, final String url) {
        this.label = label;
        this.url = url;
    }

    public Button(final Button other) {
        this(other.label, other.url);
    }

    public Button() {
        this("", "");
    }

    @Override
    public Button getDefaults() {
        return new Button(DEFAULT);
    }

    @Override
    public Button copy() {
        return new Button(this);
    }

    @Override
    public void transferFrom(Module target) {
        if (target instanceof Button && !equals(target)) {
            final Button data = (Button) target;

            label = data.label;
            url = data.url;
        }
    }

    @Override
    public Object getProperty(final String name) {
        return StringUtils.getField(Button.class, this, name);
    }

    @Override
    public void setProperty(final String name, final Object value) {
        StringUtils.updateField(Button.class, this, value, name);
    }

    @Override
    public boolean equals(Object obj) {
        if (obj == this) {
            return true;
        }

        if (!(obj instanceof Button)) {
            return false;
        }

        final Button other = (Button) obj;

        return Objects.equals(other.label, label) &&
                Objects.equals(other.url, url);
    }

    @Override
    public int hashCode() {
        return Objects.hash(label, url);
    }
}
