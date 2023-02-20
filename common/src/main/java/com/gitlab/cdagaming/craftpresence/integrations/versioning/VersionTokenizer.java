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

package com.gitlab.cdagaming.craftpresence.integrations.versioning;

/**
 * Parser for Version Strings
 *
 * @author CDAGaming, StackOverflow (<a href="https://stackoverflow.com/a/10034633">Markus Jarderot</a>)
 */
public class VersionTokenizer {
    /**
     * The effected string being interpreted
     */
    private final String versionString;
    /**
     * The length of the token string
     */
    private final int length;

    /**
     * The current token iteration position
     */
    private int position;
    /**
     * The characterized identifier, based on position
     */
    private int number;
    /**
     * The character suffix for the token string, if any
     */
    private String suffix;
    /**
     * Whether parsable values still remain in this token
     */
    private boolean hasValue;

    /**
     * Initialize the tokenizer, with the specified arguments
     *
     * @param versionString The specified version string
     */
    public VersionTokenizer(String versionString) {
        if (versionString == null)
            throw new IllegalArgumentException("versionString is null");

        this.versionString = versionString;
        length = versionString.length();
    }

    /**
     * Retrieve the characterized identifier, based on position
     *
     * @return the resulting integer
     */
    public int getNumber() {
        return number;
    }

    /**
     * Retrieve the character suffix for the token string, if any
     *
     * @return the resulting string
     */
    public String getSuffix() {
        return suffix;
    }

    /**
     * Retrieve whether parsable values still remain in this token
     *
     * @return {@link Boolean#TRUE} if condition is satisfied
     */
    public boolean hasValue() {
        return hasValue;
    }

    /**
     * Iterate to the next position in the string
     *
     * @return {@link Boolean#TRUE} if we have more values to iterate through
     */
    public boolean next() {
        number = 0;
        suffix = "";
        hasValue = false;

        // No more characters
        if (position >= length)
            return false;

        hasValue = true;

        while (position < length) {
            char c = versionString.charAt(position);
            if (c < '0' || c > '9') break;
            number = number * 10 + (c - '0');
            position++;
        }

        int suffixStart = position;

        while (position < length) {
            char c = versionString.charAt(position);
            if (c == '.') break;
            position++;
        }

        suffix = versionString.substring(suffixStart, position);

        if (position < length) position++;

        return true;
    }
}
