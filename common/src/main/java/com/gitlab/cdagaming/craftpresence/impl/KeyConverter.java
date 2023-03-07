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

package com.gitlab.cdagaming.craftpresence.impl;

import com.gitlab.cdagaming.craftpresence.ModUtils;
import org.apache.commons.lang3.tuple.Pair;

import java.util.Collections;
import java.util.HashMap;
import java.util.Map;

/**
 * KeyCode Conversion Layer used to translate between other Keyboard Data Types
 *
 * @author CDAGaming, deftware
 */
public class KeyConverter {
    /**
     * Mapping from lwjgl2 to lwjgl3
     * Note: Characters that are Unavailable in lwjgl3 are listed as lwjgl3's Unknown Keycode (-1)
     * Format: LWJGL2 Key;[LWJGL3 Key, Universal Key Name]
     */
    public static final Map<Integer, Pair<Integer, String>> toGlfw = Collections.unmodifiableMap(new HashMap<Integer, Pair<Integer, String>>() {
        /**
         * The serialized unique version identifier
         */
        private static final long serialVersionUID = 1L;

        {
            put(0, Pair.of(-1, "None"));
            put(1, Pair.of(256, "Escape"));
            put(2, Pair.of(49, "1"));
            put(3, Pair.of(50, "2"));
            put(4, Pair.of(51, "3"));
            put(5, Pair.of(52, "4"));
            put(6, Pair.of(53, "5"));
            put(7, Pair.of(54, "6"));
            put(8, Pair.of(55, "7"));
            put(9, Pair.of(56, "8"));
            put(10, Pair.of(57, "9"));
            put(11, Pair.of(48, "0"));
            put(12, Pair.of(45, "Minus"));
            put(13, Pair.of(61, "Equals"));
            put(14, Pair.of(259, "Backspace"));
            put(15, Pair.of(258, "Tab"));
            put(16, Pair.of(81, "Q"));
            put(17, Pair.of(87, "W"));
            put(18, Pair.of(69, "E"));
            put(19, Pair.of(82, "R"));
            put(20, Pair.of(84, "T"));
            put(21, Pair.of(89, "Y"));
            put(22, Pair.of(85, "U"));
            put(23, Pair.of(73, "I"));
            put(24, Pair.of(79, "O"));
            put(25, Pair.of(80, "P"));
            put(26, Pair.of(91, "Left Bracket"));
            put(27, Pair.of(93, "Right Bracket"));
            put(28, Pair.of(257, "Return"));
            put(29, Pair.of(341, "Left Control"));
            put(30, Pair.of(65, "A"));
            put(31, Pair.of(83, "S"));
            put(32, Pair.of(68, "D"));
            put(33, Pair.of(70, "F"));
            put(34, Pair.of(71, "G"));
            put(35, Pair.of(72, "H"));
            put(36, Pair.of(74, "J"));
            put(37, Pair.of(75, "K"));
            put(38, Pair.of(76, "L"));
            put(39, Pair.of(59, "Semicolon"));
            put(40, Pair.of(39, "Apostrophe"));
            put(41, Pair.of(96, "Grave"));
            put(42, Pair.of(340, "Left Shift"));
            put(43, Pair.of(92, "Backslash"));
            put(44, Pair.of(90, "Z"));
            put(45, Pair.of(88, "X"));
            put(46, Pair.of(67, "C"));
            put(47, Pair.of(86, "V"));
            put(48, Pair.of(66, "B"));
            put(49, Pair.of(78, "N"));
            put(50, Pair.of(77, "M"));
            put(51, Pair.of(44, "Comma"));
            put(52, Pair.of(46, "Period"));
            put(53, Pair.of(47, "Slash"));
            put(54, Pair.of(344, "Right Shift"));
            put(55, Pair.of(332, "Keypad - Multiply"));
            put(56, Pair.of(342, "Left Alt"));
            put(57, Pair.of(32, "Space"));
            put(58, Pair.of(280, "Caps Lock"));
            put(59, Pair.of(290, "F1"));
            put(60, Pair.of(291, "F2"));
            put(61, Pair.of(292, "F3"));
            put(62, Pair.of(293, "F4"));
            put(63, Pair.of(294, "F5"));
            put(64, Pair.of(295, "F6"));
            put(65, Pair.of(296, "F7"));
            put(66, Pair.of(297, "F8"));
            put(67, Pair.of(298, "F9"));
            put(68, Pair.of(299, "F10"));
            put(69, Pair.of(282, "Number Lock"));
            put(70, Pair.of(281, "Scroll Lock"));
            put(71, Pair.of(327, "Keypad - 7"));
            put(72, Pair.of(328, "Keypad - 8"));
            put(73, Pair.of(329, "Keypad - 9"));
            put(74, Pair.of(333, "Keypad - Subtract"));
            put(75, Pair.of(324, "Keypad - 4"));
            put(76, Pair.of(325, "Keypad - 5"));
            put(77, Pair.of(326, "Keypad - 6"));
            put(78, Pair.of(334, "Keypad - Add"));
            put(79, Pair.of(321, "Keypad - 1"));
            put(80, Pair.of(322, "Keypad - 2"));
            put(81, Pair.of(323, "Keypad - 3"));
            put(82, Pair.of(320, "Keypad - 0"));
            put(83, Pair.of(330, "Keypad - Decimal"));
            put(87, Pair.of(300, "F11"));
            put(88, Pair.of(301, "F12"));
            put(100, Pair.of(302, "F13"));
            put(101, Pair.of(303, "F14"));
            put(102, Pair.of(304, "F15"));
            put(103, Pair.of(305, "F16"));
            put(104, Pair.of(306, "F17"));
            put(105, Pair.of(307, "F18"));
            put(112, Pair.of(-1, "Kana"));
            put(113, Pair.of(308, "F19"));
            put(121, Pair.of(-1, "Convert"));
            put(123, Pair.of(-1, "NoConvert"));
            put(125, Pair.of(-1, "Symbol - Yen"));
            put(141, Pair.of(336, "Keypad - Equals"));
            put(144, Pair.of(-1, "Symbol - Circumflex"));
            put(145, Pair.of(-1, "Symbol - At"));
            put(146, Pair.of(-1, "Symbol - Colon"));
            put(147, Pair.of(-1, "Underline"));
            put(148, Pair.of(-1, "Kanji"));
            put(149, Pair.of(-1, "Stop"));
            put(150, Pair.of(-1, "AX"));
            put(151, Pair.of(-1, "Unlabeled"));
            put(156, Pair.of(335, "Keypad - Enter"));
            put(157, Pair.of(345, "Right Control"));
            put(179, Pair.of(-1, "Keypad - Comma"));
            put(181, Pair.of(331, "Keypad - Divide"));
            put(183, Pair.of(-1, "SysRq"));
            put(184, Pair.of(346, "Right Alt"));
            put(196, Pair.of(-1, "Function"));
            put(197, Pair.of(284, "Pause"));
            put(199, Pair.of(268, "Home"));
            put(200, Pair.of(265, "Up Arrow"));
            put(201, Pair.of(266, "Page Up"));
            put(203, Pair.of(263, "Left Arrow"));
            put(205, Pair.of(262, "Right Arrow"));
            put(207, Pair.of(269, "End"));
            put(208, Pair.of(264, "Down Arrow"));
            put(209, Pair.of(267, "Page Down"));
            put(210, Pair.of(260, "Insert"));
            put(211, Pair.of(261, "Delete"));
            put(219, Pair.of(343, "Left Meta"));
            put(220, Pair.of(347, "Right Meta"));
            put(221, Pair.of(-1, "Apps"));
            put(222, Pair.of(-1, "Power"));
            put(223, Pair.of(-1, "Sleep"));
        }
    });

    /**
     * Mapping from lwjgl3 to lwjgl2
     * Note: Characters that are Unavailable in lwjgl2 are listed as lwjgl2's Unknown Keycode (0)
     * Format: LWJGL3 Key;[LWJGL2 Key, Universal Key Name]
     */
    public static final Map<Integer, Pair<Integer, String>> fromGlfw = Collections.unmodifiableMap(new HashMap<Integer, Pair<Integer, String>>() {
        /**
         * The serialized unique version identifier
         */
        private static final long serialVersionUID = 1L;

        {
            put(-1, Pair.of(0, "None"));
            put(32, Pair.of(57, "Space"));
            put(39, Pair.of(40, "Apostrophe"));
            put(44, Pair.of(51, "Comma"));
            put(45, Pair.of(12, "Minus"));
            put(46, Pair.of(52, "Period"));
            put(47, Pair.of(53, "Slash"));
            put(48, Pair.of(11, "0"));
            put(49, Pair.of(2, "1"));
            put(50, Pair.of(3, "2"));
            put(51, Pair.of(4, "3"));
            put(52, Pair.of(5, "4"));
            put(53, Pair.of(6, "5"));
            put(54, Pair.of(7, "6"));
            put(55, Pair.of(8, "7"));
            put(56, Pair.of(9, "8"));
            put(57, Pair.of(10, "9"));
            put(59, Pair.of(39, "Semicolon"));
            put(61, Pair.of(13, "Equals"));
            put(65, Pair.of(30, "A"));
            put(66, Pair.of(48, "B"));
            put(67, Pair.of(46, "C"));
            put(68, Pair.of(32, "D"));
            put(69, Pair.of(18, "E"));
            put(70, Pair.of(33, "F"));
            put(71, Pair.of(34, "G"));
            put(72, Pair.of(35, "H"));
            put(73, Pair.of(23, "I"));
            put(74, Pair.of(36, "J"));
            put(75, Pair.of(37, "K"));
            put(76, Pair.of(38, "L"));
            put(77, Pair.of(50, "M"));
            put(78, Pair.of(49, "N"));
            put(79, Pair.of(24, "O"));
            put(80, Pair.of(25, "P"));
            put(81, Pair.of(16, "Q"));
            put(82, Pair.of(19, "R"));
            put(83, Pair.of(31, "S"));
            put(84, Pair.of(20, "T"));
            put(85, Pair.of(22, "U"));
            put(86, Pair.of(47, "V"));
            put(87, Pair.of(17, "W"));
            put(88, Pair.of(45, "X"));
            put(89, Pair.of(21, "Y"));
            put(90, Pair.of(44, "Z"));
            put(91, Pair.of(26, "Left Bracket"));
            put(92, Pair.of(43, "Backslash"));
            put(93, Pair.of(27, "Right Bracket"));
            put(96, Pair.of(41, "Grave"));
            put(161, Pair.of(0, "WORLD_1"));
            put(162, Pair.of(0, "WORLD_2"));
            put(256, Pair.of(1, "Escape"));
            put(257, Pair.of(28, "Return / Enter"));
            put(258, Pair.of(15, "Tab"));
            put(259, Pair.of(14, "Backspace"));
            put(260, Pair.of(210, "Insert"));
            put(261, Pair.of(211, "Delete"));
            put(262, Pair.of(205, "Right Arrow"));
            put(263, Pair.of(203, "Left Arrow"));
            put(264, Pair.of(208, "Down Arrow"));
            put(265, Pair.of(200, "Up Arrow"));
            put(266, Pair.of(201, "Page Up"));
            put(267, Pair.of(209, "Page Down"));
            put(268, Pair.of(199, "Home"));
            put(269, Pair.of(207, "End"));
            put(280, Pair.of(58, "Caps Lock"));
            put(281, Pair.of(70, "Scroll Lock"));
            put(282, Pair.of(69, "Number Lock"));
            put(283, Pair.of(0, "Print Screen"));
            put(284, Pair.of(197, "Pause"));
            put(290, Pair.of(59, "F1"));
            put(291, Pair.of(60, "F2"));
            put(292, Pair.of(61, "F3"));
            put(293, Pair.of(62, "F4"));
            put(294, Pair.of(63, "F5"));
            put(295, Pair.of(64, "F6"));
            put(296, Pair.of(65, "F7"));
            put(297, Pair.of(66, "F8"));
            put(298, Pair.of(67, "F9"));
            put(299, Pair.of(68, "F10"));
            put(300, Pair.of(87, "F11"));
            put(301, Pair.of(88, "F12"));
            put(302, Pair.of(100, "F13"));
            put(303, Pair.of(101, "F14"));
            put(304, Pair.of(102, "F15"));
            put(305, Pair.of(103, "F16"));
            put(306, Pair.of(104, "F17"));
            put(307, Pair.of(105, "F18"));
            put(308, Pair.of(113, "F19"));
            put(309, Pair.of(0, "F20"));
            put(310, Pair.of(0, "F21"));
            put(311, Pair.of(0, "F22"));
            put(312, Pair.of(0, "F23"));
            put(313, Pair.of(0, "F24"));
            put(314, Pair.of(0, "F25"));
            put(320, Pair.of(82, "Keypad - 0"));
            put(321, Pair.of(79, "Keypad - 1"));
            put(322, Pair.of(80, "Keypad - 2"));
            put(323, Pair.of(81, "Keypad - 3"));
            put(324, Pair.of(75, "Keypad - 4"));
            put(325, Pair.of(76, "Keypad - 5"));
            put(326, Pair.of(77, "Keypad - 6"));
            put(327, Pair.of(71, "Keypad - 7"));
            put(328, Pair.of(72, "Keypad - 8"));
            put(329, Pair.of(73, "Keypad - 9"));
            put(330, Pair.of(83, "Keypad - Decimal"));
            put(331, Pair.of(181, "Keypad - Divide"));
            put(332, Pair.of(55, "Keypad - Multiply"));
            put(333, Pair.of(74, "Keypad - Subtract"));
            put(334, Pair.of(78, "Keypad - Add"));
            put(335, Pair.of(156, "Keypad - Enter"));
            put(336, Pair.of(141, "Keypad - Equals"));
            put(340, Pair.of(42, "Left Shift"));
            put(341, Pair.of(29, "Left Control"));
            put(342, Pair.of(56, "Left Alt"));
            put(343, Pair.of(219, "Left Meta"));
            put(344, Pair.of(54, "Right Shift"));
            put(345, Pair.of(157, "Right Control"));
            put(346, Pair.of(184, "Right Alt"));
            put(347, Pair.of(220, "Right Meta"));
            put(348, Pair.of(0, "KEY_MENU"));
        }
    });

    /**
     * Converts a KeyCode using the Specified Conversion Mode, if possible
     * <p>
     * Note: If None is Used on a Valid Value, this function can be used as verification, if any
     *
     * @param originalKey The original Key to Convert
     * @param mode        The Conversion Mode to convert the keycode to
     * @return The resulting converted KeyCode, or the mode's unknown key
     */
    public static int convertKey(final int originalKey, final ConversionMode mode) {
        final Pair<Integer, String> unknownKeyData = mode == ConversionMode.Lwjgl2 ? fromGlfw.get(-1) : toGlfw.get(0);
        int resultKey = originalKey;

        if (fromGlfw.containsKey(originalKey) || toGlfw.containsKey(originalKey)) {
            if (mode == ConversionMode.Lwjgl2 || (mode == ConversionMode.None && fromGlfw.containsKey(originalKey) && toGlfw.containsValue(Pair.of(originalKey, fromGlfw.get(originalKey).getRight())) && ModUtils.MCProtocolID <= 340)) {
                resultKey = fromGlfw.getOrDefault(originalKey, unknownKeyData).getLeft();
            } else if (mode == ConversionMode.Lwjgl3 || (mode == ConversionMode.None && toGlfw.containsKey(originalKey) && fromGlfw.containsValue(Pair.of(originalKey, toGlfw.get(originalKey).getRight())) && ModUtils.MCProtocolID > 340)) {
                resultKey = toGlfw.getOrDefault(originalKey, unknownKeyData).getLeft();
            }
        }

        if (resultKey == originalKey && mode != ConversionMode.None) {
            ModUtils.LOG.debugWarn(ModUtils.TRANSLATOR.translate("craftpresence.logger.warning.convert.invalid", Integer.toString(resultKey), mode.name()));
        }

        return resultKey;
    }

    /**
     * A Mapping storing the possible Conversion Modes for this module
     */
    public enum ConversionMode {
        /**
         * Constant for the "LWJGL2" Conversion Mode.
         */
        Lwjgl2,
        /**
         * Constant for the "LWJGL3" Conversion Mode.
         */
        Lwjgl3,
        /**
         * Constant for the "None" Conversion Mode.
         */
        None,
        /**
         * Constant for the "Unknown" Conversion Mode.
         */
        Unknown
    }

}
