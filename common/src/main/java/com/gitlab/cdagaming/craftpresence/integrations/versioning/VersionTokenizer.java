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
    private final String _versionString;
    /**
     * The length of the token string
     */
    private final int _length;

    /**
     * The current token iteration position
     */
    private int _position;
    /**
     * The characterized identifier, based on position
     */
    private int _number;
    /**
     * The character suffix for the token string, if any
     */
    private String _suffix;
    /**
     * Whether parsable values still remain in this token
     */
    private boolean _hasValue;

    /**
     * Retrieve the characterized identifier, based on position
     *
     * @return the resulting integer
     */
    public int getNumber() {
        return _number;
    }

    /**
     * Retrieve the character suffix for the token string, if any
     *
     * @return the resulting string
     */
    public String getSuffix() {
        return _suffix;
    }

    /**
     * Retrieve whether parsable values still remain in this token
     *
     * @return {@link Boolean#TRUE} if condition is satisfied
     */
    public boolean hasValue() {
        return _hasValue;
    }

    /**
     * Initialize the tokenizer, with the specified arguments
     *
     * @param versionString The specified version string
     */
    public VersionTokenizer(String versionString) {
        if (versionString == null)
            throw new IllegalArgumentException("versionString is null");

        _versionString = versionString;
        _length = versionString.length();
    }

    /**
     * Iterate to the next position in the string
     *
     * @return {@link Boolean#TRUE} if we have more values to iterate through
     */
    public boolean MoveNext() {
        _number = 0;
        _suffix = "";
        _hasValue = false;

        // No more characters
        if (_position >= _length)
            return false;

        _hasValue = true;

        while (_position < _length) {
            char c = _versionString.charAt(_position);
            if (c < '0' || c > '9') break;
            _number = _number * 10 + (c - '0');
            _position++;
        }

        int suffixStart = _position;

        while (_position < _length) {
            char c = _versionString.charAt(_position);
            if (c == '.') break;
            _position++;
        }

        _suffix = _versionString.substring(suffixStart, _position);

        if (_position < _length) _position++;

        return true;
    }
}
