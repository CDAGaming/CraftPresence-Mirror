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

package com.gitlab.cdagaming.craftpresence.core.impl;

import com.gitlab.cdagaming.craftpresence.core.Constants;

import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;

/**
 * KeyCode Conversion Layer used to translate between other Keyboard Data Types
 *
 * @author CDAGaming, deftware
 */
public class KeyConverter {
    /**
     * Internal Mappings for all available KeyBinds within LWJGL
     */
    private static final Set<KeyBindMapping> keyMappings = Set.of(
            new KeyBindMapping(0, -1, "None"),
            new KeyBindMapping(1, 256, "Escape"),
            new KeyBindMapping(2, 49, "1"),
            new KeyBindMapping(3, 50, "2"),
            new KeyBindMapping(4, 51, "3"),
            new KeyBindMapping(5, 52, "4"),
            new KeyBindMapping(6, 53, "5"),
            new KeyBindMapping(7, 54, "6"),
            new KeyBindMapping(8, 55, "7"),
            new KeyBindMapping(9, 56, "8"),
            new KeyBindMapping(10, 57, "9"),
            new KeyBindMapping(11, 48, "0"),
            new KeyBindMapping(12, 45, "Minus"),
            new KeyBindMapping(13, 61, "Equals"),
            new KeyBindMapping(14, 259, "Backspace"),
            new KeyBindMapping(15, 258, "Tab"),
            new KeyBindMapping(16, 81, "Q"),
            new KeyBindMapping(17, 87, "W"),
            new KeyBindMapping(18, 69, "E"),
            new KeyBindMapping(19, 82, "R"),
            new KeyBindMapping(20, 84, "T"),
            new KeyBindMapping(21, 89, "Y"),
            new KeyBindMapping(22, 85, "U"),
            new KeyBindMapping(23, 73, "I"),
            new KeyBindMapping(24, 79, "O"),
            new KeyBindMapping(25, 80, "P"),
            new KeyBindMapping(26, 91, "Left Bracket"),
            new KeyBindMapping(27, 93, "Right Bracket"),
            new KeyBindMapping(28, 257, "Return"),
            new KeyBindMapping(29, 341, "Left Control"),
            new KeyBindMapping(30, 65, "A"),
            new KeyBindMapping(31, 83, "S"),
            new KeyBindMapping(32, 68, "D"),
            new KeyBindMapping(33, 70, "F"),
            new KeyBindMapping(34, 71, "G"),
            new KeyBindMapping(35, 72, "H"),
            new KeyBindMapping(36, 74, "J"),
            new KeyBindMapping(37, 75, "K"),
            new KeyBindMapping(38, 76, "L"),
            new KeyBindMapping(39, 59, "Semicolon"),
            new KeyBindMapping(40, 39, "Apostrophe"),
            new KeyBindMapping(41, 96, "Grave"),
            new KeyBindMapping(42, 340, "Left Shift"),
            new KeyBindMapping(43, 92, "Backslash"),
            new KeyBindMapping(44, 90, "Z"),
            new KeyBindMapping(45, 88, "X"),
            new KeyBindMapping(46, 67, "C"),
            new KeyBindMapping(47, 86, "V"),
            new KeyBindMapping(48, 66, "B"),
            new KeyBindMapping(49, 78, "N"),
            new KeyBindMapping(50, 77, "M"),
            new KeyBindMapping(51, 44, "Comma"),
            new KeyBindMapping(52, 46, "Period"),
            new KeyBindMapping(53, 47, "Slash"),
            new KeyBindMapping(54, 344, "Right Shift"),
            new KeyBindMapping(55, 332, "Keypad - Multiply"),
            new KeyBindMapping(56, 342, "Left Alt"),
            new KeyBindMapping(57, 32, "Space"),
            new KeyBindMapping(58, 280, "Caps Lock"),
            new KeyBindMapping(59, 290, "F1"),
            new KeyBindMapping(60, 291, "F2"),
            new KeyBindMapping(61, 292, "F3"),
            new KeyBindMapping(62, 293, "F4"),
            new KeyBindMapping(63, 294, "F5"),
            new KeyBindMapping(64, 295, "F6"),
            new KeyBindMapping(65, 296, "F7"),
            new KeyBindMapping(66, 297, "F8"),
            new KeyBindMapping(67, 298, "F9"),
            new KeyBindMapping(68, 299, "F10"),
            new KeyBindMapping(69, 282, "Number Lock"),
            new KeyBindMapping(70, 281, "Scroll Lock"),
            new KeyBindMapping(71, 327, "Keypad - 7"),
            new KeyBindMapping(72, 328, "Keypad - 8"),
            new KeyBindMapping(73, 329, "Keypad - 9"),
            new KeyBindMapping(74, 333, "Keypad - Subtract"),
            new KeyBindMapping(75, 324, "Keypad - 4"),
            new KeyBindMapping(76, 325, "Keypad - 5"),
            new KeyBindMapping(77, 326, "Keypad - 6"),
            new KeyBindMapping(78, 334, "Keypad - Add"),
            new KeyBindMapping(79, 321, "Keypad - 1"),
            new KeyBindMapping(80, 322, "Keypad - 2"),
            new KeyBindMapping(81, 323, "Keypad - 3"),
            new KeyBindMapping(82, 320, "Keypad - 0"),
            new KeyBindMapping(83, 330, "Keypad - Decimal"),
            new KeyBindMapping(87, 300, "F11"),
            new KeyBindMapping(88, 301, "F12"),
            new KeyBindMapping(100, 302, "F13"),
            new KeyBindMapping(101, 303, "F14"),
            new KeyBindMapping(102, 304, "F15"),
            new KeyBindMapping(103, 305, "F16"),
            new KeyBindMapping(104, 306, "F17"),
            new KeyBindMapping(105, 307, "F18"),
            new KeyBindMapping(112, -1, "Kana"),
            new KeyBindMapping(113, 308, "F19"),
            new KeyBindMapping(121, -1, "Convert"),
            new KeyBindMapping(123, -1, "NoConvert"),
            new KeyBindMapping(125, -1, "Symbol - Yen"),
            new KeyBindMapping(141, 336, "Keypad - Equals"),
            new KeyBindMapping(144, -1, "Symbol - Circumflex"),
            new KeyBindMapping(145, -1, "Symbol - At"),
            new KeyBindMapping(146, -1, "Symbol - Colon"),
            new KeyBindMapping(147, -1, "Underline"),
            new KeyBindMapping(148, -1, "Kanji"),
            new KeyBindMapping(149, -1, "Stop"),
            new KeyBindMapping(150, -1, "AX"),
            new KeyBindMapping(151, -1, "Unlabeled"),
            new KeyBindMapping(156, 335, "Keypad - Enter"),
            new KeyBindMapping(157, 345, "Right Control"),
            new KeyBindMapping(179, -1, "Keypad - Comma"),
            new KeyBindMapping(181, 331, "Keypad - Divide"),
            new KeyBindMapping(183, -1, "SysRq"),
            new KeyBindMapping(184, 346, "Right Alt"),
            new KeyBindMapping(196, -1, "Function"),
            new KeyBindMapping(197, 284, "Pause"),
            new KeyBindMapping(199, 268, "Home"),
            new KeyBindMapping(200, 265, "Up Arrow"),
            new KeyBindMapping(201, 266, "Page Up"),
            new KeyBindMapping(203, 263, "Left Arrow"),
            new KeyBindMapping(205, 262, "Right Arrow"),
            new KeyBindMapping(207, 269, "End"),
            new KeyBindMapping(208, 264, "Down Arrow"),
            new KeyBindMapping(209, 267, "Page Down"),
            new KeyBindMapping(210, 260, "Insert"),
            new KeyBindMapping(211, 261, "Delete"),
            new KeyBindMapping(219, 343, "Left Meta"),
            new KeyBindMapping(220, 347, "Right Meta"),
            new KeyBindMapping(221, -1, "Apps"),
            new KeyBindMapping(222, -1, "Power"),
            new KeyBindMapping(223, -1, "Sleep"),
            new KeyBindMapping(-1, 0, "None"),
            new KeyBindMapping(32, 57, "Space"),
            new KeyBindMapping(39, 40, "Apostrophe"),
            new KeyBindMapping(44, 51, "Comma"),
            new KeyBindMapping(45, 12, "Minus"),
            new KeyBindMapping(46, 52, "Period"),
            new KeyBindMapping(47, 53, "Slash"),
            new KeyBindMapping(48, 11, "0"),
            new KeyBindMapping(49, 2, "1"),
            new KeyBindMapping(50, 3, "2"),
            new KeyBindMapping(51, 4, "3"),
            new KeyBindMapping(52, 5, "4"),
            new KeyBindMapping(53, 6, "5"),
            new KeyBindMapping(54, 7, "6"),
            new KeyBindMapping(55, 8, "7"),
            new KeyBindMapping(56, 9, "8"),
            new KeyBindMapping(57, 10, "9"),
            new KeyBindMapping(59, 39, "Semicolon"),
            new KeyBindMapping(61, 13, "Equals"),
            new KeyBindMapping(65, 30, "A"),
            new KeyBindMapping(66, 48, "B"),
            new KeyBindMapping(67, 46, "C"),
            new KeyBindMapping(68, 32, "D"),
            new KeyBindMapping(69, 18, "E"),
            new KeyBindMapping(70, 33, "F"),
            new KeyBindMapping(71, 34, "G"),
            new KeyBindMapping(72, 35, "H"),
            new KeyBindMapping(73, 23, "I"),
            new KeyBindMapping(74, 36, "J"),
            new KeyBindMapping(75, 37, "K"),
            new KeyBindMapping(76, 38, "L"),
            new KeyBindMapping(77, 50, "M"),
            new KeyBindMapping(78, 49, "N"),
            new KeyBindMapping(79, 24, "O"),
            new KeyBindMapping(80, 25, "P"),
            new KeyBindMapping(81, 16, "Q"),
            new KeyBindMapping(82, 19, "R"),
            new KeyBindMapping(83, 31, "S"),
            new KeyBindMapping(84, 20, "T"),
            new KeyBindMapping(85, 22, "U"),
            new KeyBindMapping(86, 47, "V"),
            new KeyBindMapping(87, 17, "W"),
            new KeyBindMapping(88, 45, "X"),
            new KeyBindMapping(89, 21, "Y"),
            new KeyBindMapping(90, 44, "Z"),
            new KeyBindMapping(91, 26, "Left Bracket"),
            new KeyBindMapping(92, 43, "Backslash"),
            new KeyBindMapping(93, 27, "Right Bracket"),
            new KeyBindMapping(96, 41, "Grave"),
            new KeyBindMapping(161, 0, "WORLD_1"),
            new KeyBindMapping(162, 0, "WORLD_2"),
            new KeyBindMapping(256, 1, "Escape"),
            new KeyBindMapping(257, 28, "Return / Enter"),
            new KeyBindMapping(258, 15, "Tab"),
            new KeyBindMapping(259, 14, "Backspace"),
            new KeyBindMapping(260, 210, "Insert"),
            new KeyBindMapping(261, 211, "Delete"),
            new KeyBindMapping(262, 205, "Right Arrow"),
            new KeyBindMapping(263, 203, "Left Arrow"),
            new KeyBindMapping(264, 208, "Down Arrow"),
            new KeyBindMapping(265, 200, "Up Arrow"),
            new KeyBindMapping(266, 201, "Page Up"),
            new KeyBindMapping(267, 209, "Page Down"),
            new KeyBindMapping(268, 199, "Home"),
            new KeyBindMapping(269, 207, "End"),
            new KeyBindMapping(280, 58, "Caps Lock"),
            new KeyBindMapping(281, 70, "Scroll Lock"),
            new KeyBindMapping(282, 69, "Number Lock"),
            new KeyBindMapping(283, 0, "Print Screen"),
            new KeyBindMapping(284, 197, "Pause"),
            new KeyBindMapping(290, 59, "F1"),
            new KeyBindMapping(291, 60, "F2"),
            new KeyBindMapping(292, 61, "F3"),
            new KeyBindMapping(293, 62, "F4"),
            new KeyBindMapping(294, 63, "F5"),
            new KeyBindMapping(295, 64, "F6"),
            new KeyBindMapping(296, 65, "F7"),
            new KeyBindMapping(297, 66, "F8"),
            new KeyBindMapping(298, 67, "F9"),
            new KeyBindMapping(299, 68, "F10"),
            new KeyBindMapping(300, 87, "F11"),
            new KeyBindMapping(301, 88, "F12"),
            new KeyBindMapping(302, 100, "F13"),
            new KeyBindMapping(303, 101, "F14"),
            new KeyBindMapping(304, 102, "F15"),
            new KeyBindMapping(305, 103, "F16"),
            new KeyBindMapping(306, 104, "F17"),
            new KeyBindMapping(307, 105, "F18"),
            new KeyBindMapping(308, 113, "F19"),
            new KeyBindMapping(309, 0, "F20"),
            new KeyBindMapping(310, 0, "F21"),
            new KeyBindMapping(311, 0, "F22"),
            new KeyBindMapping(312, 0, "F23"),
            new KeyBindMapping(313, 0, "F24"),
            new KeyBindMapping(314, 0, "F25"),
            new KeyBindMapping(320, 82, "Keypad - 0"),
            new KeyBindMapping(321, 79, "Keypad - 1"),
            new KeyBindMapping(322, 80, "Keypad - 2"),
            new KeyBindMapping(323, 81, "Keypad - 3"),
            new KeyBindMapping(324, 75, "Keypad - 4"),
            new KeyBindMapping(325, 76, "Keypad - 5"),
            new KeyBindMapping(326, 77, "Keypad - 6"),
            new KeyBindMapping(327, 71, "Keypad - 7"),
            new KeyBindMapping(328, 72, "Keypad - 8"),
            new KeyBindMapping(329, 73, "Keypad - 9"),
            new KeyBindMapping(330, 83, "Keypad - Decimal"),
            new KeyBindMapping(331, 181, "Keypad - Divide"),
            new KeyBindMapping(332, 55, "Keypad - Multiply"),
            new KeyBindMapping(333, 74, "Keypad - Subtract"),
            new KeyBindMapping(334, 78, "Keypad - Add"),
            new KeyBindMapping(335, 156, "Keypad - Enter"),
            new KeyBindMapping(336, 141, "Keypad - Equals"),
            new KeyBindMapping(340, 42, "Left Shift"),
            new KeyBindMapping(341, 29, "Left Control"),
            new KeyBindMapping(342, 56, "Left Alt"),
            new KeyBindMapping(343, 219, "Left Meta"),
            new KeyBindMapping(344, 54, "Right Shift"),
            new KeyBindMapping(345, 157, "Right Control"),
            new KeyBindMapping(346, 184, "Right Alt"),
            new KeyBindMapping(347, 220, "Right Meta"),
            new KeyBindMapping(348, 0, "KEY_MENU")
    );
    /**
     * Mapping from lwjgl2 to lwjgl3
     * Note: Characters that are Unavailable in lwjgl3 are listed as lwjgl3's Unknown Keycode (-1)
     * Format: LWJGL2 Key;KeyMapping
     */
    public static final Map<Integer, KeyBindMapping> toGlfw = keyMappings.stream()
            .collect(Collectors.toMap(KeyBindMapping::lwjgl2Key, mapping -> mapping));
    /**
     * Mapping from lwjgl3 to lwjgl2
     * Note: Characters that are Unavailable in lwjgl2 are listed as lwjgl2's Unknown Keycode (0)
     * Format: LWJGL3 Key;KeyMapping
     */
    public static final Map<Integer, KeyBindMapping> fromGlfw = keyMappings.stream()
            .collect(Collectors.toMap(KeyBindMapping::lwjgl3Key, mapping -> mapping));

    /**
     * Converts a KeyCode using the Specified Conversion Mode, if possible
     * <p>
     * Note: If None is Used on a Valid Value, this function can be used as verification, if any
     *
     * @param originalKey The original Key to Convert
     * @param protocol    The Protocol to Target for this conversion
     * @param mode        The Conversion Mode to convert the keycode to
     * @return The resulting converted KeyCode, or the mode's unknown key
     */
    public static int convertKey(final int originalKey, final int protocol, final ConversionMode mode) {
        final KeyBindMapping unknownKeyData = mode == ConversionMode.Lwjgl2 ? fromGlfw.get(-1) : toGlfw.get(0);
        int resultKey = (protocol <= 340 ? -1 : 0);

        if (mode == ConversionMode.Lwjgl2) {
            resultKey = fromGlfw.getOrDefault(originalKey, unknownKeyData).lwjgl2Key();
        } else if (mode == ConversionMode.Lwjgl3) {
            resultKey = toGlfw.getOrDefault(originalKey, unknownKeyData).lwjgl3Key();
        } else if (mode == ConversionMode.None) {
            // If Input is a valid Integer and Valid KeyCode,
            // Retain the Original Value
            if (protocol <= 340 && toGlfw.containsKey(originalKey)) {
                resultKey = originalKey;
            } else if (protocol > 340 && fromGlfw.containsKey(originalKey)) {
                resultKey = originalKey;
            }
        }

        if (resultKey == originalKey && mode != ConversionMode.None) {
            Constants.LOG.debugWarn(Constants.TRANSLATOR.translate("craftpresence.logger.warning.convert.invalid", Integer.toString(resultKey), mode.name()));
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

    /**
     * A Mapping for KeyBind data across different LWJGL versions
     *
     * @param lwjgl2Key The KeyBind representation for LWJGL2
     * @param lwjgl3Key The KeyBind representation for LWJGL3
     * @param name      The name of the KeyBind
     */
    public record KeyBindMapping(int lwjgl2Key, int lwjgl3Key, String name) {
    }

}
