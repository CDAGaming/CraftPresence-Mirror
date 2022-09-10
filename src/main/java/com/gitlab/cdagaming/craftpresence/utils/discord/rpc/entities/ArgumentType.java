/*
 * MIT License
 *
 * Copyright (c) 2018 - 2022 CDAGaming (cstack2011@yahoo.com)
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

package com.gitlab.cdagaming.craftpresence.utils.discord.rpc.entities;

import com.gitlab.cdagaming.craftpresence.utils.StringUtils;

/**
 * Constants representing various Discord client statuses,
 * such as Ready, Errored, Disconnected, and event trigger names
 */
public enum ArgumentType {
    /**
     * Constant for the "Image" Argument Type.
     */
    Image,

    /**
     * Constant for the "Text" Argument Type.
     */
    Text,

    /**
     * Constant for the "Button" Argument Type.
     */
    Button,

    /**
     * 'Wildcard' build constant used to specify an errored or invalid status
     */
    Invalid;

    private final String displayName;

    ArgumentType() {
        displayName = StringUtils.formatWord(name());
    }

    ArgumentType(final String displayName) {
        this.displayName = displayName;
    }

    /**
     * Gets a {@link ArgumentType} matching the specified display name.
     * <p>
     * This is only internally implemented.
     *
     * @param displayName The display name to get from.
     * @return The DiscordStatus corresponding to the display name, or
     * {@link ArgumentType#Invalid} if none match.
     */
    public static ArgumentType from(String displayName) {
        for (ArgumentType value : values()) {
            if (value.getDisplayName() != null && value.getDisplayName().equals(displayName)) {
                return value;
            }
        }
        return Invalid;
    }

    /**
     * Retrieves the display name for the specified {@link ArgumentType}
     *
     * @return The display name corresponding to the {@link ArgumentType}
     */
    public String getDisplayName() {
        return displayName;
    }
}
