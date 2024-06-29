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

package com.gitlab.cdagaming.unilib.core.impl;

import com.gitlab.cdagaming.unilib.core.CoreUtils;

/**
 * Translation Conversion Layer used to translate between other Pack Format Types
 *
 * @author CDAGaming
 */
public class TranslationConverter {
    /**
     * Converts a Language Identifier using the Specified Conversion Mode, if possible
     * <p>
     * Note: If None is Used on a Valid Value, this function can be used as verification, if any
     *
     * @param originalId The original Key to Convert (5-Character Limit)
     * @param protocol   The Protocol to Target for this conversion
     * @param mode       The Conversion Mode to convert the keycode to
     * @return The resulting converted Language Identifier, or the mode's unknown key
     */
    public static String convertId(final String originalId, final int protocol, final ConversionMode mode) {
        String resultId = originalId;

        if (originalId.length() == 5 && originalId.contains("_")) {
            if (mode == ConversionMode.PackFormat2 || (mode == ConversionMode.None && protocol < 315)) {
                resultId = resultId.substring(0, 3).toLowerCase() + resultId.substring(3).toUpperCase();
            } else if (mode == ConversionMode.PackFormat3 || mode == ConversionMode.None) {
                resultId = resultId.toLowerCase();
            }
        }

        if (resultId.equals(originalId) && mode != ConversionMode.None) {
            CoreUtils.LOG.warn(
                    "Unexpected TranslationConverter result for object \"%1$s\". Please report this issue. (Mode Attempted: %2$s)",
                    resultId, mode.name()
            );
        }

        return resultId.trim();
    }

    /**
     * A Mapping storing the possible Conversion Modes for this module
     */
    public enum ConversionMode {
        /**
         * Constant for the "Pack Format 2" Conversion Mode.
         */
        PackFormat2,
        /**
         * Constant for the "Pack Format 3" Conversion Mode.
         */
        PackFormat3,
        /**
         * Constant for the "None or Verification" Conversion Mode.
         */
        None,
        /**
         * Constant for the "Unknown" Conversion Mode.
         */
        Unknown
    }
}
