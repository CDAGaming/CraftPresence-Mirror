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

package com.gitlab.cdagaming.craftpresence.integrations.pack;

import com.gitlab.cdagaming.craftpresence.utils.StringUtils;

/**
 * Set of Utilities used to Parse Pack Instance Information
 *
 * @author CDAGaming
 */
public abstract class Pack {
    /**
     * The Pack Instance Name
     */
    private String packName;
    /**
     * The Icon Key to use for this Pack
     */
    private String packIcon;

    public boolean isEnabled() {
        return true;
    }

    /**
     * Attempts to retrieve and load Instance Information, if any
     *
     * @return {@link Boolean#TRUE} if Instance Information was found
     */
    public abstract boolean load();

    /**
     * Retrieve the pack instance name
     *
     * @return the name of the pack, if any
     */
    public String getPackName() {
        return packName;
    }

    /**
     * Set the pack name for this instance
     *
     * @param packName the new pack name
     */
    public void setPackName(final String packName) {
        this.packName = packName;
    }

    /**
     * Determine whether a valid pack name is present
     *
     * @return {@link Boolean#TRUE} if a pack name is present
     */
    public boolean hasPackName() {
        return !StringUtils.isNullOrEmpty(packName);
    }

    /**
     * Retrieve the pack icon key
     *
     * @return the icon key of the pack, if any
     */
    public String getPackIcon() {
        return hasPackIcon() ? StringUtils.formatAsIcon(packIcon) : "";
    }

    /**
     * Set the pack icon key for this instance
     *
     * @param packIcon the new pack icon key
     */
    public void setPackIcon(final String packIcon) {
        this.packIcon = packIcon;
    }

    /**
     * Determine whether a valid pack icon is present
     *
     * @return {@link Boolean#TRUE} if a pack icon is present
     */
    public boolean hasPackIcon() {
        return !StringUtils.isNullOrEmpty(packIcon);
    }
}
