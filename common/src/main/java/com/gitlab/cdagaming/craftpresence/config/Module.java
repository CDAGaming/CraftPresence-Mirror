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

package com.gitlab.cdagaming.craftpresence.config;

import com.gitlab.cdagaming.craftpresence.utils.FileUtils;

/**
 * Module Section defining properties to be used in a configuration
 *
 * @author CDAGaming
 */
public abstract class Module {
    /**
     * Retrieve the default instance for this {@link Module}
     *
     * @return the default instance of this {@link Module}
     */
    protected abstract Module getDefaults();

    /**
     * Retrieve the specified property for this {@link Module}
     *
     * @param name the name of the property
     * @return the property value, if found
     */
    protected abstract Object getProperty(final String name);

    /**
     * Sets the specified property for this {@link Module}
     *
     * @param name  the name of the property
     * @param value the property value to assign
     */
    protected abstract void setProperty(final String name, final Object value);

    /**
     * Resets the specified property to that which matches the default instance for this {@link Module}
     *
     * @param name the name of the property
     */
    public void resetProperty(final String name) {
        setProperty(name, getDefaults().getProperty(name));
    }

    @Override
    public String toString() {
        return FileUtils.toJsonData(this);
    }

    @Override
    public boolean equals(Object obj) {
        if (obj == this) {
            return true;
        }

        if (!(obj instanceof Module)) {
            return false;
        }

        Module p = (Module) obj;
        return toString().equals(p.toString());
    }

    @Override
    public int hashCode() {
        return toString().hashCode();
    }
}
