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
 * Constants representing various Discord client party privacy levels,
 * such as Public or Private
 */
public enum PartyPrivacy {
    /**
     * Constant for the "Private" Discord RPC Party privacy level.
     */
    Private,

    /**
     * Constant for the "Public" Discord RPC Party privacy level.
     */
    Public;

    /**
     * Gets a {@link PartyPrivacy} matching the specified index.
     * <p>
     * This is only internally implemented.
     *
     * @param index The index to get from.
     * @return The {@link PartyPrivacy} corresponding to the parameters, or
     * {@link PartyPrivacy#Public} if none match.
     */
    public static PartyPrivacy from(int index) {
        for (PartyPrivacy value : values()) {
            if (value.ordinal() == index) {
                return value;
            }
        }
        return Public;
    }

    /**
     * Gets a {@link PartyPrivacy} matching the specified display name.
     * <p>
     * This is only internally implemented.
     *
     * @param name The name to get from.
     * @return The {@link PartyPrivacy} corresponding to the parameters, or
     * {@link PartyPrivacy#Public} if none match.
     */
    public static PartyPrivacy from(String name) {
        for (PartyPrivacy value : values()) {
            if (!StringUtils.isNullOrEmpty(value.name()) && value.name().equalsIgnoreCase(name)) {
                return value;
            }
        }
        return Public;
    }

    /**
     * Gets a {@link PartyPrivacy} matching the specified name.
     * <p>
     * This is only internally implemented.
     *
     * @param index The index to get from.
     * @param name  The name to get from.
     * @return The {@link PartyPrivacy} corresponding to the parameters, or
     * {@link PartyPrivacy#Public} if none match.
     */
    public static PartyPrivacy from(int index, String name) {
        for (PartyPrivacy value : values()) {
            if (!StringUtils.isNullOrEmpty(value.name()) && value.name().equalsIgnoreCase(name) && value.ordinal() == index) {
                return value;
            }
        }
        return Public;
    }
}
