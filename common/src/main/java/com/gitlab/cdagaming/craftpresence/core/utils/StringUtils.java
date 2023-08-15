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

package com.gitlab.cdagaming.craftpresence.core.utils;

import com.gitlab.cdagaming.craftpresence.core.impl.Pair;
import com.gitlab.cdagaming.craftpresence.core.impl.Tuple;
import net.lenni0451.reflect.stream.RStream;
import net.lenni0451.reflect.stream.field.FieldStream;
import net.lenni0451.reflect.stream.field.FieldWrapper;
import net.lenni0451.reflect.stream.method.MethodStream;
import net.lenni0451.reflect.stream.method.MethodWrapper;

import java.awt.*;
import java.io.PrintWriter;
import java.io.StringWriter;
import java.lang.reflect.Array;
import java.nio.charset.Charset;
import java.util.List;
import java.util.*;
import java.util.function.Predicate;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.stream.Collectors;

/**
 * String Utilities for interpreting Strings and Basic Data Types
 *
 * @author CDAGaming
 */
public class StringUtils {
    /**
     * The unknown identifier for Base64 data
     * <p>Used to implicitly specify a Sting is meant to be Base64
     */
    public static final String UNKNOWN_BASE64_ID = "data:image/unknown;base64";
    /**
     * The Character to be interpreted as the start to a Formatting Character
     */
    public static final char COLOR_CHAR = '§';
    /**
     * The character set representing data that is too large to display
     */
    public static final String TOO_LARGE = "<...>";
    /**
     * The character set representing a Tab in the form of four spaces
     */
    public static final String TAB_SPACE = "    ";
    /**
     * A conditional statement for determining if a String is null or empty
     */
    public static final Predicate<String> NULL_OR_EMPTY = StringUtils::isNullOrEmpty;
    /**
     * Regex Pattern for Possible New Line Characters
     */
    public static final Pattern NEW_LINE_PATTERN = Pattern.compile("(\\r\\n|\\r|\\n|\\\\n)");
    /**
     * Regex Pattern for Color and Formatting Codes
     */
    public static final Pattern STRIP_COLOR_PATTERN = Pattern.compile("(?i)" + COLOR_CHAR + "[0-9A-FK-OR]");
    /**
     * The Default Charset to use for String Operations
     */
    public static final Charset DEFAULT_CHARSET = Charset.defaultCharset();
    /**
     * Regex Pattern for Base64 Detection
     */
    private static final Pattern BASE64_PATTERN = Pattern.compile("data:(?<type>.+?);base64,(?<data>.+)");
    /**
     * Regex Pattern for Trimmed Uuid Detection
     */
    private static final Pattern TRIMMED_UUID_PATTERN = Pattern.compile("(\\w{8})(\\w{4})(\\w{4})(\\w{4})(\\w{12})");
    /**
     * Regex Pattern for Full Uuid Detection
     */
    private static final Pattern FULL_UUID_PATTERN = Pattern.compile("(\\w{8})-(\\w{4})-(\\w{4})-(\\w{4})-(\\w{12})");
    /**
     * Regex Pattern for Brackets containing Digits
     */
    private static final Pattern BRACKET_PATTERN = Pattern.compile("\\([^0-9]*\\d+[^0-9]*\\)");
    /**
     * Regex Pattern for Whitespace characters within a string
     */
    private static final Pattern WHITESPACE_PATTERN = Pattern.compile("(.*?)\\s(.*?)");
    /**
     * Regex Pattern for Alphanumeric characters within a string
     */
    private static final Pattern ALPHANUMERIC_PATTERN = Pattern.compile(".*[a-zA-Z0-9].*");

    /**
     * Attempts to Convert the specified data into a Valid interpretable Java Color
     *
     * @param red   the red component
     * @param green the green component
     * @param blue  the blue component
     * @param alpha the alpha component
     * @return A Valid Java Color, if successful
     */
    public static Color getColorFrom(final int red, final int green, final int blue, final int alpha) {
        return new Color(red, green, blue, alpha);
    }

    /**
     * Attempts to Convert the specified data into a Valid interpretable Java Color
     *
     * @param red   the red component
     * @param green the green component
     * @param blue  the blue component
     * @return A Valid Java Color, if successful
     */
    public static Color getColorFrom(final int red, final int green, final int blue) {
        return getColorFrom(red, green, blue, 255);
    }

    /**
     * Attempts to Convert the specified data into a Valid interpretable Java Color
     *
     * @param data the raw interpretable data
     * @return A Valid Java Color, if successful
     */
    public static Color getColorFrom(final int data) {
        return getColorFrom(
                (data >> 16 & 255),
                (data >> 8 & 255),
                (data & 255),
                (data >> 24 & 255)
        );
    }

    /**
     * Attempts to Convert the specified data into a Valid interpretable Java Color
     *
     * @param hexColor The inputted Hexadecimal Color String
     * @return A Valid Java Color, if successful
     */
    public static Color getColorFrom(final String hexColor) {
        final Pair<Boolean, Matcher> matchData = isValidColor(hexColor);
        if (!matchData.getFirst()) {
            return Color.white;
        }
        final Matcher m = matchData.getSecond();
        String s = m.group(1);
        if (s == null) s = m.group(2);
        if (s == null) throw new IllegalStateException();
        long l = Long.parseLong(s, 16);
        int a = m.group(1) != null ? (int) ((l >> 24) & 0xFF) : 0xFF;
        int r = (int) ((l >> 16) & 0xFF);
        int g = (int) ((l >> 8) & 0xFF);
        int b = (int) (l & 0xFF);
        return getColorFrom(r, g, b, a);
    }

    /**
     * Attempt to retrieve color info for the specified entries
     *
     * @param startColorCode The Starting Color Object
     * @param endColorCode   The Ending Color Object
     * @return the processed output
     */
    public static Pair<Color, Color> findColor(final String startColorCode, final String endColorCode) {
        Color startColorObj = null, endColorObj = null;
        int startColor = 0xFFFFFF, endColor = 0xFFFFFF;

        if (!isNullOrEmpty(startColorCode)) {
            if (isValidColor(startColorCode).getFirst()) {
                startColorObj = getColorFrom(startColorCode);
                endColorObj = (!isNullOrEmpty(endColorCode) && isValidColor(endColorCode).getFirst()) ? getColorFrom(endColorCode) : startColorObj;
            } else {
                // Determine if Start Color Code is a Valid Number
                final Pair<Boolean, Integer> startColorData = getValidInteger(startColorCode),
                        endColorData = getValidInteger(endColorCode);

                // Check and ensure that at least one of the Color Codes are correct
                if (startColorData.getFirst() || endColorData.getFirst()) {
                    startColor = startColorData.getFirst() ? startColorData.getSecond() : endColor;
                    endColor = endColorData.getFirst() ? endColorData.getSecond() : startColor;
                }
            }
        }

        if (startColorObj == null) {
            startColorObj = getColorFrom(startColor);
        }
        if (endColorObj == null) {
            endColorObj = getColorFrom(endColor);
        }
        return new Pair<>(startColorObj, endColorObj);
    }

    /**
     * Offset the specified {@link Color} by the specified factor
     *
     * @param color  the {@link Color} to offset
     * @param factor the offset factor
     * @return the modified {@link Color} instance
     */
    public static Color offsetColor(final Color color, final float factor) {
        return new Color(
                Math.max((int) (color.getRed() * factor), 0),
                Math.max((int) (color.getGreen() * factor), 0),
                Math.max((int) (color.getBlue() * factor), 0),
                color.getAlpha()
        );
    }

    /**
     * Attempt to retrieve color info for the specified entries
     *
     * @param startColorObj The Starting Color Object
     * @param endColorObj   The Ending Color Object
     * @return the processed output
     */
    public static Pair<Color, Color> findColor(Object startColorObj, Object endColorObj) {
        Color startColor = null, endColor = null;
        endColorObj = endColorObj == null ? startColorObj : endColorObj;
        if (startColorObj instanceof String) {
            final Pair<Color, Color> colorData = findColor(
                    (String) startColorObj,
                    endColorObj instanceof String ? (String) endColorObj : null
            );
            startColor = colorData.getFirst();
            endColor = colorData.getSecond();
        } else if (startColorObj instanceof Color) {
            startColor = (Color) startColorObj;
            endColor = endColorObj instanceof Color ? (Color) endColorObj : startColor;
        }
        return new Pair<>(startColor, endColor);
    }

    /**
     * Attempt to retrieve color info for the specified entries
     *
     * @param startColorObj The Starting Color Object
     * @return the processed output
     */
    public static Color findColor(Object startColorObj) {
        return findColor(startColorObj, null).getFirst();
    }

    /**
     * Determines whether an inputted String classifies as a valid Color Code
     *
     * @param entry The String to evaluate
     * @return {@link Boolean#TRUE} if Entry is classified as a valid Color Code, alongside extra data
     */
    public static Pair<Boolean, Matcher> isValidColor(final String entry) {
        final Matcher m = Pattern.compile("^(?:0x([\\dA-Fa-f]{1,8})|#?([\\dA-Fa-f]{6}))$").matcher(entry);
        return new Pair<>(m.find(), m);
    }

    /**
     * Converts a String to that of the Specified Charset, in byte form
     *
     * @param original The original String to interpret
     * @param encoding The Charset to encode the bytes under
     * @return The processed byte array
     */
    public static byte[] getBytes(final String original, final String encoding) {
        try {
            if (!isNullOrEmpty(encoding)) {
                return original.getBytes(encoding);
            } else {
                return getBytes(original, DEFAULT_CHARSET.name());
            }
        } catch (Exception ex) {
            return getBytes(original, DEFAULT_CHARSET.name());
        }
    }

    /**
     * Converts a String to that of the Specified Charset, in byte form
     *
     * @param original The original String to interpret
     * @return The processed byte array
     */
    public static byte[] getBytes(final String original) {
        return getBytes(original, null);
    }

    /**
     * Retrieve the stacktrace from an {@link Throwable}
     *
     * @param ex The exception to interpret
     * @return The string representation of the {@link Throwable}
     */
    public static String getStackTrace(final Throwable ex) {
        if (ex == null) {
            return "";
        }
        final StringWriter sw = new StringWriter();
        final PrintWriter pw = new PrintWriter(sw);
        ex.printStackTrace(pw);
        return sw.toString();
    }

    /**
     * Converts a String and it's bytes to that of the Specified Charset
     *
     * @param original The original String to interpret
     * @param encoding The Charset to encode the String under
     * @param decode   If we are Decoding an already encoded String
     * @return The converted UTF_8 String, if successful
     */
    public static String convertString(final String original, final String encoding, final boolean decode) {
        try {
            if (decode) {
                return new String(getBytes(original), encoding);
            } else {
                final byte[] bytes = getBytes(original, encoding);
                return new String(bytes, 0, bytes.length, DEFAULT_CHARSET);
            }
        } catch (Exception ex) {
            return original;
        }
    }

    /**
     * Attempt to convert the specified object into an array
     *
     * @param original The object to interpret
     * @return the converted array, if able (Returns null if errored)
     */
    public static Object[] getDynamicArray(final Object original) {
        if (!(original instanceof Object[])) {
            try {
                final int len = Array.getLength(original);
                final Object[] objects = new Object[len];
                for (int i = 0; i < len; i++)
                    objects[i] = Array.get(original, i);
                return objects;
            } catch (Throwable ex) {
                return null;
            }
        } else {
            return (Object[]) original;
        }
    }

    /**
     * Retrieve the primary value if non-empty; Otherwise, use the secondary value
     *
     * @param primary   The primary value to interpret
     * @param secondary The secondary value to interpret
     * @param condition The conditional statement to interpret
     * @return the resulting value
     */
    public static String getOrDefault(final String primary, final String secondary, final Predicate<String> condition) {
        return condition.test(primary) ? primary : secondary;
    }

    /**
     * Retrieve the primary value if non-empty; Otherwise, use the secondary value
     *
     * @param primary   The primary value to interpret
     * @param secondary The secondary value to interpret
     * @return the resulting value
     */
    public static String getOrDefault(final String primary, final String secondary) {
        return getOrDefault(primary, secondary, NULL_OR_EMPTY.negate());
    }

    /**
     * Retrieve the primary value if non-empty; Otherwise, use the secondary value
     *
     * @param primary The primary value to interpret
     * @return the resulting value
     */
    public static String getOrDefault(final String primary) {
        return getOrDefault(primary, "");
    }

    /**
     * Reverse the specified list recursively
     *
     * @param list The specified list to interpret
     * @param <T>  The list type
     */
    public static <T> void revlist(List<T> list) {
        // base condition when the list size is 0
        if (list == null || list.size() <= 1)
            return;

        T value = list.remove(0);

        // call the recursive function to reverse
        // the list after removing the first element
        revlist(list);

        // now after the rest of the list has been
        // reversed by the upper recursive call,
        // add the first value at the end
        list.add(value);
    }

    /**
     * Retrieve Matching Values from an input that matches the defined regex
     *
     * @param regexValue The Regex Value to test against
     * @param original   The original Object to get matches from
     * @param flags      The bit mask for Pattern compilation, see {@link Pattern#compile(String, int)}
     * @return A Pair with the Format of originalString:listOfMatches
     */
    public static Pair<String, List<String>> getMatches(final String regexValue, final Object original, final int flags) {
        return original != null ? getMatches(regexValue, original.toString(), flags) : new Pair<>("", newArrayList());
    }

    /**
     * Retrieve Matching Values from an input that matches the defined regex
     *
     * @param regexValue The Regex Value to test against
     * @param original   The original Object to get matches from
     * @return A Pair with the Format of originalString:listOfMatches
     */
    public static Pair<String, List<String>> getMatches(final String regexValue, final Object original) {
        return getMatches(regexValue, original, 0);
    }

    /**
     * Retrieve Matching Values from an input that matches the defined regex
     *
     * @param regexValue The Regex Value to test against
     * @param original   The original String to get matches from
     * @param flags      The bit mask for Pattern compilation, see {@link Pattern#compile(String, int)}
     * @return A Pair with the Format of originalString:listOfMatches
     */
    public static Pair<String, List<String>> getMatches(final String regexValue, final String original, final int flags) {
        final List<String> matches = newArrayList();

        if (!isNullOrEmpty(original)) {
            final Pattern pattern = Pattern.compile(regexValue, flags);
            final Matcher m = pattern.matcher(original);

            while (m.find()) {
                matches.add(m.group());
            }
        }

        return new Pair<>(original, matches);
    }

    /**
     * Retrieve Matching Values from an input that matches the defined regex
     *
     * @param regexValue The Regex Value to test against
     * @param original   The original String to get matches from
     * @return A Pair with the Format of originalString:listOfMatches
     */
    public static Pair<String, List<String>> getMatches(final String regexValue, final String original) {
        return getMatches(regexValue, original, 0);
    }

    /**
     * Remove an Amount of Matches from an inputted Match Set
     *
     * @param matchData       The Match Data to remove from with the form of originalString:listOfMatches
     * @param parsedMatchData The Parsed Argument Data to match against, if available, to prevent Null Arguments
     * @param maxMatches      The maximum amount of matches to remove (Set to -1 to Remove All)
     * @return The original String from Match Data with the matches up to maxMatches removed
     */
    public static String removeMatches(final Pair<String, List<String>> matchData, final List<Pair<String, String>> parsedMatchData, final int maxMatches) {
        String finalString = "";

        if (matchData != null) {
            finalString = matchData.getFirst();
            final List<String> matchList = matchData.getSecond();

            if (!matchList.isEmpty()) {
                int foundMatches = 0;

                for (String match : matchList) {
                    final boolean isValidScan = foundMatches >= maxMatches;
                    boolean alreadyRemoved = false;

                    if (parsedMatchData != null && !parsedMatchData.isEmpty()) {
                        // Scan through Parsed Argument Data if Possible
                        for (Pair<String, String> parsedArgument : parsedMatchData) {
                            // If found a matching argument to the match, and the parsed argument is null
                            // Remove the match without counting it as a found match
                            if (parsedArgument.getFirst().equalsIgnoreCase(match) && isNullOrEmpty(parsedArgument.getSecond())) {
                                finalString = finalString.replaceFirst(match, "");
                                alreadyRemoved = true;
                                break;
                            }
                        }
                    }

                    if (!alreadyRemoved) {
                        if (isValidScan) {
                            finalString = finalString.replaceFirst(match, "");
                        }
                        foundMatches++;
                    }
                }
            }
        }

        return finalString;
    }

    /**
     * Replaces Data in a String
     *
     * @param source          The original String to replace within
     * @param targetToReplace The value to replace on
     * @param replaceWith     The value to replace the target with
     * @param matchCase       Whether to match via exact-capitalization
     * @param matchWholeWord  Whether to match the whole world
     * @param useRegex        Whether to allow regex or to escape it
     * @return The completed and replaced String
     */
    public static String replace(final String source, final String targetToReplace, final String replaceWith,
                                 final boolean matchCase, final boolean matchWholeWord, final boolean useRegex) {
        if (!isNullOrEmpty(source)) {
            String patternString;
            if (useRegex) {
                patternString = targetToReplace;
            } else {
                if (matchWholeWord) {
                    patternString = "(?i)\\b" + Pattern.quote(targetToReplace) + "\\b";
                } else {
                    patternString = Pattern.quote(targetToReplace);
                }
            }
            int flags = Pattern.LITERAL;
            if (!matchCase) {
                flags |= Pattern.CASE_INSENSITIVE | Pattern.UNICODE_CASE;
            }
            return Pattern.compile(patternString, flags).matcher(source)
                    .replaceAll(Matcher.quoteReplacement(replaceWith));
        } else {
            return "";
        }
    }

    /**
     * Replaces Data in a sequential order
     *
     * @param source         The original String to replace within
     * @param matchCase      Whether to match via exact-capitalization
     * @param matchWholeWord Whether to match the whole world
     * @param useRegex       Whether to allow regex or to escape it
     * @param replaceArgs    The replacement list to follow with the form of: targetToReplace:replaceWithValue
     * @return The completed and replaced String
     */
    @SafeVarargs
    public static String sequentialReplace(final String source, final boolean matchCase, final boolean matchWholeWord, final boolean useRegex, final Map<String, String>... replaceArgs) {
        if (!isNullOrEmpty(source)) {
            String finalResult = source;

            for (Map<String, String> replaceData : replaceArgs) {
                if (!replaceData.isEmpty()) {
                    for (Map.Entry<String, String> replacementData : replaceData.entrySet()) {
                        finalResult = replace(finalResult, replacementData.getKey(), replacementData.getValue(), matchCase, matchWholeWord, useRegex);
                    }
                }
            }
            return finalResult;
        } else {
            return "";
        }
    }

    /**
     * Reduces the Length of a String to the Specified Length
     *
     * @param source The String to evaluate
     * @param length The Maximum Length to reduce the String down towards, beginning at 0
     * @return The newly reduced/minified String
     */
    public static String minifyString(final String source, final int length) {
        if (!isNullOrEmpty(source)) {
            return length >= 0 ? source.substring(0, length) : source;
        } else {
            return "";
        }
    }

    /**
     * Determines whether a String classifies as NULL or EMPTY
     *
     * @param entry           The String to evaluate
     * @param allowWhitespace Whether to allow whitespace strings
     * @return {@link Boolean#TRUE} if Entry is classified as NULL or EMPTY
     */
    public static boolean isNullOrEmpty(String entry, final boolean allowWhitespace) {
        if (entry != null) {
            entry = allowWhitespace ? entry : entry.trim();
        }
        return entry == null || entry.isEmpty() || entry.equalsIgnoreCase("null");
    }

    /**
     * Determines whether a String classifies as NULL or EMPTY
     *
     * @param entry The String to evaluate
     * @return {@link Boolean#TRUE} if Entry is classified as NULL or EMPTY
     */
    public static boolean isNullOrEmpty(final String entry) {
        return isNullOrEmpty(entry, false);
    }

    /**
     * Determines whether the Object's String Interpretation classifies as a valid Boolean
     *
     * @param entry The Object to evaluate
     * @return {@link Boolean#TRUE} if Entry is classified as a valid Boolean
     */
    public static boolean isValidBoolean(final Object entry) {
        return entry != null && isValidBoolean(entry.toString());
    }

    /**
     * Determines whether a String classifies as a valid Boolean
     *
     * @param entry The String to evaluate
     * @return {@link Boolean#TRUE} if Entry is classified as a valid Boolean
     */
    public static boolean isValidBoolean(final String entry) {
        return !isNullOrEmpty(entry) && (entry.equalsIgnoreCase("true") || entry.equalsIgnoreCase("false"));
    }

    /**
     * Determines whether an inputted String classifies as a valid Color Code
     *
     * @param entry The String to evaluate
     * @return {@link Boolean#TRUE} if Entry is classified as a valid Color Code
     */
    public static boolean isValidColorCode(final String entry) {
        return !isNullOrEmpty(entry) && (isValidColor(entry).getFirst() || getValidInteger(entry).getFirst());
    }

    /**
     * Determine whether an inputted Object classifies as a valid Integer
     *
     * @param entry The Object to evaluate
     * @return A Pair with the format of isValid:parsedIntegerIfTrue
     */
    public static Pair<Boolean, Integer> getValidInteger(final Object entry) {
        return entry != null ? getValidInteger(entry.toString()) : new Pair<>(false, 0);
    }

    /**
     * Determine whether an inputted String classifies as a valid Integer
     *
     * @param entry The String to evaluate
     * @return A Pair with the format of isValid:parsedIntegerIfTrue
     */
    public static Pair<Boolean, Integer> getValidInteger(final String entry) {
        final Pair<Boolean, Integer> finalSet = new Pair<>();

        if (!isNullOrEmpty(entry)) {
            try {
                finalSet.setSecond(Integer.parseInt(entry));
                finalSet.setFirst(true);
            } catch (Exception ex) {
                finalSet.setFirst(false);
            }
        } else {
            finalSet.setFirst(false);
        }

        return finalSet;
    }

    /**
     * Determine whether an inputted Object classifies as a valid Long
     *
     * @param entry The Object to evaluate
     * @return A Pair with the format of isValid:parsedLongIfTrue
     */
    public static Pair<Boolean, Long> getValidLong(final Object entry) {
        return entry != null ? getValidLong(entry.toString()) : new Pair<>(false, 0L);
    }

    /**
     * Determine whether an inputted String classifies as a valid Long
     *
     * @param entry The String to evaluate
     * @return A Pair with the format of isValid:parsedLongIfTrue
     */
    public static Pair<Boolean, Long> getValidLong(final String entry) {
        final Pair<Boolean, Long> finalSet = new Pair<>();

        if (!isNullOrEmpty(entry)) {
            try {
                finalSet.setSecond(Long.parseLong(entry));
                finalSet.setFirst(true);
            } catch (Exception ex) {
                finalSet.setFirst(false);
            }
        } else {
            finalSet.setFirst(false);
        }

        return finalSet;
    }

    /**
     * Determine whether an inputted Object classifies as a valid Boolean
     *
     * @param entry The Object to evaluate
     * @return A Pair with the format of isValid:parsedBoolIfTrue
     */
    public static Pair<Boolean, Boolean> getValidBoolean(final Object entry) {
        return entry != null ? getValidBoolean(entry.toString()) : new Pair<>(false, false);
    }

    /**
     * Determine whether an inputted String classifies as a valid Boolean
     *
     * @param entry The String to evaluate
     * @return A Pair with the format of isValid:parsedBoolIfTrue
     */
    public static Pair<Boolean, Boolean> getValidBoolean(final String entry) {
        final Pair<Boolean, Boolean> finalSet = new Pair<>();

        if (!isNullOrEmpty(entry)) {
            try {
                finalSet.setSecond(Boolean.parseBoolean(entry));
                finalSet.setFirst(true);
            } catch (Exception ex) {
                finalSet.setFirst(false);
            }
        } else {
            finalSet.setFirst(false);
        }

        return finalSet;
    }

    /**
     * Formats an IP Address based on Input
     *
     * @param input      The original String to evaluate
     * @param returnPort Whether to return the port or the IP without the Port
     * @return Either the IP or the port on their own, depending on conditions
     */
    public static String formatAddress(final String input, final boolean returnPort) {
        if (!isNullOrEmpty(input)) {
            final String[] formatted = input.split(":", 2);
            return !returnPort ? (elementExists(formatted, 0) ? formatted[0].trim() : "127.0.0.1") : (elementExists(formatted, 1) ? formatted[1].trim() : "25565");
        } else {
            return !returnPort ? "127.0.0.1" : "25565";
        }
    }

    /**
     * Whether the specified string contains whitespace characters
     *
     * @param original The original String to evaluate
     * @return the processed result
     */
    public static boolean containsWhitespace(final String original) {
        return isNullOrEmpty(original) || WHITESPACE_PATTERN.matcher(original).find();
    }

    /**
     * Whether the specified string contains alphanumeric characters
     *
     * @param original The original String to evaluate
     * @return the processed result
     */
    public static boolean containsAlphaNumeric(final String original) {
        return !isNullOrEmpty(original) && ALPHANUMERIC_PATTERN.matcher(original).find();
    }

    /**
     * Converts a String into a Valid and Acceptable Camel-Case Format
     *
     * @param original The original String to evaluate
     * @return The converted and valid String, in camel-case Format
     */
    public static String formatToCamel(final String original) {
        if (isNullOrEmpty(original)) {
            return original;
        } else {
            final String[] words = original.split("[\\W_]+");
            final StringBuilder builder = new StringBuilder();
            for (int i = 0; i < words.length; i++) {
                String word = words[i];
                if (i == 0) {
                    word = word.isEmpty() ? word : word.toLowerCase();
                } else {
                    word = word.isEmpty() ? word : Character.toUpperCase(word.charAt(0)) + word.substring(1).toLowerCase();
                }
                builder.append(word);
            }
            return builder.toString();
        }
    }

    /**
     * Converts a String into a Valid and Acceptable Icon Format
     *
     * @param original        The original String to evaluate
     * @param whitespaceIndex The string to replace whitespace with
     * @return The converted and valid String, in an iconKey Format
     */
    public static String formatAsIcon(final String original, final String whitespaceIndex) {
        String formattedKey = original;
        if (isNullOrEmpty(formattedKey)) {
            return formattedKey;
        } else {
            if (containsWhitespace(formattedKey)) {
                formattedKey = formattedKey.replaceAll("\\s+", whitespaceIndex);
            }
            formattedKey = formattedKey.replaceAll("[^a-zA-Z0-9_-]", "_");
            return formattedKey.toLowerCase().trim();
        }
    }

    /**
     * Converts a String into a Valid and Acceptable Icon Format
     *
     * @param original The original String to evaluate
     * @return The converted and valid String, in an iconKey Format
     */
    public static String formatAsIcon(final String original) {
        return formatAsIcon(original, "");
    }

    /**
     * Checks via Regex whether the specified String classifies as a Base64 Image
     *
     * @param original The original string
     * @return Base64 data in the format of isBase64:imageId:formattedImageString
     */
    public static Tuple<Boolean, String, String> isBase64(final String original) {
        String formattedKey = original, imageIdentifier = "";
        final Tuple<Boolean, String, String> finalData = new Tuple<>(false, imageIdentifier, formattedKey);

        if (!isNullOrEmpty(formattedKey)) {
            if (formattedKey.contains(",")) {
                final String[] splitData = formattedKey.split(",", 2);
                imageIdentifier = splitData[0];
                formattedKey = splitData[1];
            }
            finalData.setFirst(BASE64_PATTERN.matcher(imageIdentifier + "," + formattedKey).find());
            finalData.setSecond(imageIdentifier);
            finalData.setThird(formattedKey);
        }
        return finalData;
    }

    /**
     * Checks via Regex whether the specified String classifies as a valid Uuid
     *
     * @param input The original string
     * @return Whether the specified String classifies as a valid Uuid
     */
    public static boolean isValidUuid(final String input) {
        return !isNullOrEmpty(input) &&
                (input.contains("-") ? FULL_UUID_PATTERN : TRIMMED_UUID_PATTERN).matcher(input).find();
    }

    /**
     * Converts a UUID into a String, presuming it is valid and not-null
     * <p>
     * Use {@link StringUtils#isValidUuid(String)} to ensure validity
     *
     * @param input   The original string
     * @param trimmed Whether to return the full or trimmed format of the UUID
     * @return the resulting UUID
     */
    public static String getFromUuid(final String input, final boolean trimmed) {
        if (!isValidUuid(input)) {
            return input;
        }
        if (trimmed) {
            return input.replace("-", "");
        } else {
            final Pattern pattern = (input.contains("-") ? FULL_UUID_PATTERN : TRIMMED_UUID_PATTERN);
            return pattern.matcher(input).find() ? pattern.matcher(input).replaceFirst("$1-$2-$3-$4-$5") : input;
        }
    }

    /**
     * Converts a UUID into a String, presuming it is valid and not-null
     * <p>
     * Use {@link StringUtils#isValidUuid(String)} to ensure validity
     *
     * @param input The original string
     * @return the resulting UUID
     */
    public static String getFromUuid(final String input) {
        return getFromUuid(input, false);
    }

    /**
     * Converts a UUID into a String, presuming it is valid and not-null
     *
     * @param input The original string
     * @return the resulting UUID
     */
    public static String getFromUuid(final UUID input) {
        return getFromUuid(input.toString());
    }

    /**
     * Converts a String into a UUID, presuming it is valid and not-null
     * <p>
     * Use {@link StringUtils#isValidUuid(String)} to ensure validity
     *
     * @param input The original string
     * @return the resulting UUID
     */
    public static UUID getAsUuid(final String input) {
        return UUID.fromString(getFromUuid(input, false));
    }

    /**
     * Add entries from the specified list, to the original list, if not present already
     *
     * @param original The original list to interpret
     * @param newList  The new list to interpret
     * @param <T>      The list type
     * @return the resulting list
     */
    public static <T> List<T> addEntriesNotPresent(final List<T> original, List<T> newList) {
        for (T entry : newList) {
            if (!original.contains(entry)) {
                original.add(entry);
            }
        }
        return original;
    }

    /**
     * Add entries from the specified list, to the original list, if it passes the filter
     *
     * @param original The original list to interpret
     * @param filter   The filter, at which to interpret the newList through
     * @param newList  The new list to interpret
     * @param <T>      The list type
     * @return the resulting list
     */
    public static <T> List<T> addEntriesNotPresent(final List<T> original, final Predicate<? super T> filter, List<T> newList) {
        newList = newList.stream().filter(filter).collect(Collectors.toList());
        return addEntriesNotPresent(original, newList);
    }

    /**
     * Add entries from the specified list, to the original list, if not present already
     *
     * @param original The original list to interpret
     * @param newList  The new list to interpret
     * @param <T>      The list type
     * @return the resulting list
     */
    public static <T> List<T> addEntriesNotPresent(final List<T> original, Set<T> newList) {
        return addEntriesNotPresent(original, newArrayList(newList));
    }

    /**
     * Add entries from the specified list, to the original list, if it passes the filter
     *
     * @param original The original list to interpret
     * @param filter   The filter, at which to interpret the newList through
     * @param newList  The new list to interpret
     * @param <T>      The list type
     * @return the resulting list
     */
    public static <T> List<T> addEntriesNotPresent(final List<T> original, final Predicate<? super T> filter, Set<T> newList) {
        newList = newList.stream().filter(filter).collect(Collectors.toSet());
        return addEntriesNotPresent(original, newList);
    }

    /**
     * Add entries from the specified list, to the original list, if not present already
     *
     * @param original The original list to interpret
     * @param newList  The new list to interpret
     * @param <T>      The list type
     * @return the resulting list
     */
    public static <T> List<T> addEntriesNotPresent(List<T> original, T[] newList) {
        return addEntriesNotPresent(original, Arrays.asList(newList));
    }

    /**
     * Converts input into a Properly Readable String
     *
     * @param original The original String to format
     * @return The formatted and evaluated String
     */
    public static String formatWord(final String original) {
        return formatWord(original, false);
    }

    /**
     * Converts input into a Properly Readable String
     *
     * @param original The original String to format
     * @param avoid    Flag to ignore method if true
     * @return The formatted and evaluated String
     */
    public static String formatWord(final String original, final boolean avoid) {
        return formatWord(original, avoid, false);
    }

    /**
     * Converts input into a Properly Readable String
     *
     * @param original              The original String to format
     * @param avoid                 Flag to ignore method if true
     * @param skipSymbolReplacement Flag to Skip Symbol Replacement if true
     * @return The formatted and evaluated String
     */
    public static String formatWord(final String original, final boolean avoid, final boolean skipSymbolReplacement) {
        return formatWord(original, avoid, skipSymbolReplacement, -1);
    }

    /**
     * Converts input into a Properly Readable String
     *
     * @param original              The original String to format
     * @param avoid                 Flag to ignore method if true
     * @param skipSymbolReplacement Flag to Skip Symbol Replacement if true
     * @param caseCheckTimes        Times to replace Parts of the String during Capitalization (Use -1 for Infinite)
     * @return The formatted and evaluated String
     */
    public static String formatWord(final String original, final boolean avoid, final boolean skipSymbolReplacement, final int caseCheckTimes) {
        String formattedKey = original;
        if (isNullOrEmpty(formattedKey) || avoid) {
            return formattedKey;
        } else {
            if (containsWhitespace(formattedKey)) {
                formattedKey = formattedKey.replaceAll("\\s+", " ");
            }

            if (!skipSymbolReplacement) {
                if (formattedKey.contains("_")) {
                    formattedKey = formattedKey.replaceAll("_", " ");
                }
                if (formattedKey.contains("-")) {
                    formattedKey = formattedKey.replaceAll("-", " ");
                }
                if (BRACKET_PATTERN.matcher(formattedKey).find()) {
                    formattedKey = BRACKET_PATTERN.matcher(formattedKey).replaceAll("");
                }
                if (STRIP_COLOR_PATTERN.matcher(formattedKey).find()) {
                    formattedKey = STRIP_COLOR_PATTERN.matcher(formattedKey).replaceAll("");
                }
            }

            return removeRepeatWords(capitalizeWord(formattedKey, caseCheckTimes)).trim();
        }
    }

    /**
     * Removes Duplicated Words within an inputted String
     *
     * @param original The original String
     * @return The evaluated String without duplicate words
     */
    public static String removeRepeatWords(final String original) {
        if (isNullOrEmpty(original)) {
            return original;
        } else {
            String lastWord = "";
            StringBuilder finalString = new StringBuilder();
            String[] wordList = original.split(" ");

            for (String word : wordList) {
                if (isNullOrEmpty(lastWord) || !word.equalsIgnoreCase(lastWord)) {
                    finalString.append(word).append(" ");
                    lastWord = word;
                }
            }

            return finalString.toString().trim();
        }
    }

    /**
     * Converts an Identifier into a properly formatted and interpretable Name
     * <p>
     * Note: Additional Logic in Place for Older MC Versions
     *
     * @param originalId The Identifier to format
     * @param formatToId Whether to format as an Icon Key
     * @return The formatted name/icon key
     */
    public static String formatIdentifier(final String originalId, final boolean formatToId) {
        return formatIdentifier(originalId, formatToId, false);
    }

    /**
     * Converts an Identifier into a properly formatted and interpretable Name
     * <p>
     * Note: Additional Logic in Place for Older MC Versions
     *
     * @param originalId The Identifier to format
     * @param formatToId Whether to format as an Icon Key
     * @param avoid      Flag to ignore formatting identifier, if formatToId is false
     * @return The formatted name/icon key
     */
    public static String formatIdentifier(final String originalId, final boolean formatToId, final boolean avoid) {
        StringBuilder formattedKey = new StringBuilder(originalId);
        if (isNullOrEmpty(formattedKey.toString())) {
            return formattedKey.toString();
        } else {
            if (formattedKey.toString().equals("WorldProvider")) {
                formattedKey = new StringBuilder("overworld");
            } else if (formattedKey.toString().contains("WorldProvider")) {
                formattedKey = new StringBuilder(formattedKey.toString().replace("WorldProvider", ""));
            }

            if (formattedKey.toString().contains("BiomeGen")) {
                formattedKey = new StringBuilder(formattedKey.toString().replace("BiomeGen", ""));
            }
            if (formattedKey.toString().contains("MobSpawner")) {
                formattedKey = new StringBuilder(formattedKey.toString().replace("MobSpawner", ""));
            }

            if (containsWhitespace(formattedKey.toString())) {
                formattedKey = new StringBuilder(formattedKey.toString().replaceAll("\\s+", " "));
            }

            if (formattedKey.toString().contains(":")) {
                formattedKey = new StringBuilder(formattedKey.toString().split(":", 2)[1]);
            }

            if (formattedKey.toString().contains("{") || formattedKey.toString().contains("}")) {
                formattedKey = new StringBuilder(formattedKey.toString().replaceAll("[{}]", ""));
            }

            if (formattedKey.toString().equalsIgnoreCase("surface")) {
                formattedKey = new StringBuilder("overworld");
            } else if (formattedKey.toString().equalsIgnoreCase("hell") || formattedKey.toString().equalsIgnoreCase("nether")) {
                formattedKey = new StringBuilder("the_nether");
            } else if (formattedKey.toString().equalsIgnoreCase("end") || formattedKey.toString().equalsIgnoreCase("sky")) {
                formattedKey = new StringBuilder("the_end");
            }

            if (formatToId) {
                return formatAsIcon(formattedKey.toString(), "_");
            } else {
                return formatWord(formattedKey.toString(), avoid);
            }
        }
    }

    /**
     * Returns the Color and Formatting Characters within a String<p>
     * Defined by {@link StringUtils#STRIP_COLOR_PATTERN}
     *
     * @param text The original String to evaluate
     * @return The formatting and color codes found within the input
     */
    public static String getFormatFromString(final String text) {
        final int stringLength = text.length();
        StringBuilder s = new StringBuilder();
        int index = -1;

        while ((index = text.indexOf(167, index + 1)) != -1) {
            if (index < stringLength - 1) {
                final char currentCharacter = text.charAt(index + 1);
                final String charString = String.valueOf(currentCharacter);
                if (STRIP_COLOR_PATTERN.matcher(charString).find()) {
                    s = new StringBuilder(COLOR_CHAR + charString);
                }
            }
        }

        return s.toString();
    }

    /**
     * Capitalizes the words within a specified string
     *
     * @param str          The String to capitalize
     * @param timesToCheck The amount of times to replace within the String (Use -1 for Infinite)
     * @return The capitalized output string
     */
    public static String capitalizeWord(final String str, final int timesToCheck) {
        final StringBuilder s = new StringBuilder();

        // Declare a character of space
        // To identify that the next character is the starting
        // of a new word
        char charIndex = ' ';
        int timesLeft = timesToCheck;
        for (int index = 0; index < str.length(); index++) {

            // If previous character is space and current
            // character is not space then it shows that
            // current letter is the starting of the word
            // We only replace however, whilst the times
            // remaining is more than 0 or is -1 (Infinite)
            if (charIndex == ' ' && str.charAt(index) != ' ' && (timesLeft > 0 || timesLeft == -1)) {
                s.append(Character.toUpperCase(str.charAt(index)));
                if (timesLeft > 0) {
                    timesLeft--;
                }
            } else {
                s.append(str.charAt(index));
            }

            charIndex = str.charAt(index);
        }

        // Return the string with trimming
        return s.toString().trim();
    }

    /**
     * Capitalizes the words within a specified string
     *
     * @param str The String to capitalize
     * @return The capitalized output string
     */
    public static String capitalizeWord(final String str) {
        return capitalizeWord(str, -1);
    }

    /**
     * Converts a String into a List of Strings, split up by new lines
     *
     * @param original        The original String
     * @param allowWhitespace Whether to allow whitespace strings
     * @return The converted, newline-split list from the original String
     */
    public static List<String> splitTextByNewLine(final String original, final boolean allowWhitespace) {
        if (!isNullOrEmpty(original, allowWhitespace)) {
            return newArrayList(NEW_LINE_PATTERN.split(original));
        } else {
            return newArrayList();
        }
    }

    /**
     * Converts a String into a List of Strings, split up by new lines
     *
     * @param original The original String
     * @return The converted, newline-split list from the original String
     */
    public static List<String> splitTextByNewLine(final String original) {
        return splitTextByNewLine(original, false);
    }

    /**
     * Joins a set of strings together by the specified separator
     *
     * @param separator The string the list should be linked together by
     * @param input     The list of data to interpret
     * @return The resulting string
     */
    public static String join(final String separator, final List<String> input) {
        if (input == null || input.isEmpty()) return "";

        StringBuilder sb = new StringBuilder();
        for (int i = 0; i < input.size(); i++) {
            sb.append(input.get(i));

            // if not the last item
            if (i < input.size() - 1) {
                sb.append(separator);
            }
        }

        return sb.toString();

    }

    /**
     * Determines if the Specified index exists in the List with a non-null value
     *
     * @param data  The Array to check within
     * @param index The index to check
     * @param <T>   The identified list type
     * @return {@link Boolean#TRUE} if the index element exists in the list with a non-null value
     */
    public static <T> boolean elementExists(final T[] data, final int index) {
        return elementExists(Arrays.asList(data), index);
    }

    /**
     * Determines if the Specified index exists in the List with a non-null value
     *
     * @param data  The List to check within
     * @param index The index to check
     * @param <T>   The identified list type
     * @return {@link Boolean#TRUE} if the index element exists in the list with a non-null value
     */
    public static <T> boolean elementExists(final List<T> data, final int index) {
        boolean result;
        try {
            result = data.size() >= index && data.get(index) != null;
        } catch (Exception ex) {
            result = false;
        }
        return result;
    }

    /**
     * <p>Copies the given array and adds the given element at the end of the new array.
     *
     * <p>The new array contains the same elements of the input
     * array plus the given element in the last position. The component type of
     * the new array is the same as that of the input array.
     *
     * <p>If the input array is {@code null}, a new one element array is returned
     * whose component type is the same as the element, unless the element itself is null,
     * in which case the return type is Object[]
     *
     * @param array   the array to "add" the element to, may be {@code null}
     * @param element the object to add, may be {@code null}
     * @param <T>     the component type of the array
     * @return A new array containing the existing elements plus the new element
     * The returned array type will be that of the input array (unless null),
     * in which case it will have the same type as the element.
     * If both are null, an IllegalArgumentException is thrown
     * @throws IllegalArgumentException if both arguments are null
     */
    public static <T> T[] addToArray(final T[] array, final T element) {
        if (array == null) {
            throw new IllegalArgumentException("Array cannot be null");
        }
        T[] result = Arrays.copyOf(array, array.length + 1);
        result[array.length] = element;
        return result;
    }

    /**
     * Creates a new ArrayList containing the specified elements.
     *
     * @param elements the elements to include in the new ArrayList
     * @param <T>      the type of elements in the list
     * @return a new ArrayList containing the specified elements
     */
    @SafeVarargs
    public static <T> List<T> newArrayList(final T... elements) {
        return new ArrayList<>(Arrays.asList(elements));
    }

    /**
     * Creates a new ArrayList containing the specified elements.
     *
     * @param <T> the type of elements in the list
     * @return a new ArrayList containing the specified elements
     */
    public static <T> List<T> newArrayList() {
        return new ArrayList<>();
    }

    /**
     * Creates a new ArrayList containing the specified elements.
     *
     * @param iterator the elements to include in the new ArrayList
     * @param <T>      the type of elements in the list
     * @return a new ArrayList containing the specified elements
     */
    public static <T> List<T> newArrayList(final Iterator<T> iterator) {
        final List<T> list = new ArrayList<>();
        while (iterator.hasNext()) {
            list.add(iterator.next());
        }
        return list;
    }

    /**
     * Creates a new ArrayList containing the specified elements.
     *
     * @param iterable the elements to include in the new ArrayList
     * @param <T>      the type of elements in the list
     * @return a new ArrayList containing the specified elements
     */
    public static <T> List<T> newArrayList(final Iterable<T> iterable) {
        return newArrayList(iterable.iterator());
    }

    /**
     * Creates a new instance of {@link HashMap} with the default initial capacity.
     *
     * @param <K> the type of keys maintained by the new map
     * @param <V> the type of mapped values
     * @return a new instance of {@link HashMap}
     */
    public static <K, V> Map<K, V> newHashMap() {
        return new HashMap<>();
    }

    /**
     * Creates a new instance of {@link HashMap} that contains the same key-value mappings as the input map.
     *
     * @param <K> the type of keys maintained by the new map
     * @param <V> the type of mapped values
     * @param map the input map whose mappings are to be copied to the new map
     * @return a new instance of {@link HashMap} that contains the same key-value mappings as the input map
     */
    public static <K, V> Map<K, V> newHashMap(final Map<? extends K, ? extends V> map) {
        return new HashMap<>(map);
    }

    /**
     * Creates a new instance of {@link TreeMap} that uses the natural ordering of its keys.
     *
     * @param <K> the type of keys maintained by the new map
     * @param <V> the type of mapped values
     * @return a new instance of {@link TreeMap}
     */
    public static <K extends Comparable<? super K>, V> TreeMap<K, V> newTreeMap() {
        return new TreeMap<>();
    }

    /**
     * Creates a new instance of {@link TreeMap} that uses the specified comparator to order its keys.
     *
     * @param <K>        the type of keys maintained by the new map
     * @param <V>        the type of mapped values
     * @param comparator the comparator to use for ordering the keys
     * @return a new instance of {@link TreeMap}
     */
    public static <K, V> TreeMap<K, V> newTreeMap(final Comparator<? super K> comparator) {
        return new TreeMap<>(comparator);
    }

    /**
     * Creates a new instance of {@link TreeMap} that contains the same key-value mappings as the input map.
     *
     * @param <K> the type of keys maintained by the new map
     * @param <V> the type of mapped values
     * @param map the input map whose mappings are to be copied to the new map
     * @return a new instance of {@link TreeMap} that contains the same key-value mappings as the input map
     */
    public static <K extends Comparable<? super K>, V> TreeMap<K, V> newTreeMap(final Map<? extends K, ? extends V> map) {
        return new TreeMap<>(map);
    }

    /**
     * Retrieve a Stream of all fields within the specified class
     *
     * @param classToAccess The class object to interpret
     * @return the output stream
     */
    public static FieldStream getFields(final Class<?> classToAccess) {
        return RStream.of(classToAccess).fields();
    }

    /**
     * Retrieve a Stream of all methods within the specified class
     *
     * @param classToAccess The class object to interpret
     * @return the output stream
     */
    public static MethodStream getMethods(final Class<?> classToAccess) {
        return RStream.of(classToAccess).methods();
    }

    /**
     * Retrieve the list of fields present in the specified class
     *
     * @param classToAccess The class object to interpret
     * @return the output String
     */
    public static String getFieldList(final Class<?> classToAccess) {
        final StringBuilder sb = new StringBuilder();
        if (classToAccess != null) {
            sb.append(classToAccess).append(": [\n");
            getFields(classToAccess).forEach(e ->
                    sb.append(TAB_SPACE)
                            .append(e.type())
                            .append(" ")
                            .append(e.name())
                            .append("\n")
            );
            sb.append("]");
        }
        return sb.toString();
    }

    /**
     * Retrieve the list of methods present in the specified class
     *
     * @param classToAccess The class object to interpret
     * @return the output String
     */
    public static String getMethodList(final Class<?> classToAccess) {
        final StringBuilder sb = new StringBuilder();
        if (classToAccess != null) {
            sb.append(classToAccess).append(": [\n");
            getMethods(classToAccess).forEach(e ->
                    sb.append(TAB_SPACE)
                            .append(e.returnType())
                            .append(" ")
                            .append(e.name())
                            .append("(")
                            .append(Arrays.stream(e.parameterTypes())
                                    .map(Class::toString)
                                    .collect(Collectors.joining(", "))
                            )
                            .append(")\n")
            );
            sb.append("]");
        }
        return sb.toString();
    }

    /**
     * Retrieves whether the specified class contains the specified field
     *
     * @param classToAccess The class to access
     * @param fieldNames    A List of Field Names to search for
     * @return whether the specified class contains the specified field
     */
    public static Optional<FieldWrapper> getValidField(final Class<?> classToAccess, final String... fieldNames) {
        if (fieldNames == null || fieldNames.length == 0) return Optional.empty();
        final List<String> names = newArrayList(fieldNames);
        return getFields(classToAccess)
                .filter(f -> names.contains(f.name()))
                .jstream()
                .findFirst();
    }

    /**
     * Retrieves whether the specified class contains the specified method
     *
     * @param classToAccess  The class to access
     * @param parameterTypes An array of Class objects representing the types of the method's parameters.
     * @param methodNames    A List of Method Names to search for
     * @return whether the specified class contains the specified method
     */
    public static Optional<MethodWrapper> getValidMethod(final Class<?> classToAccess, final Class<?>[] parameterTypes, final String... methodNames) {
        if (methodNames == null || methodNames.length == 0) return Optional.empty();
        final Class<?>[] params = parameterTypes != null ? parameterTypes : new Class<?>[0];
        final List<String> names = newArrayList(methodNames);
        return getMethods(classToAccess)
                .filter(f -> names.contains(f.name()) && Arrays.equals(params, f.parameterTypes()))
                .jstream()
                .findFirst();
    }

    /**
     * Retrieves the Specified Field(s) via Reflection
     *
     * @param classToAccess The class to access with the field(s)
     * @param instance      An Instance of the Class, if needed
     * @param fieldNames    A List of Field Names to search for
     * @return The Found Field Data, if any
     */
    public static Object getField(final Class<?> classToAccess, final Object instance, final String... fieldNames) {
        return getValidField(classToAccess, fieldNames).map(f -> f.get(instance)).orElse(null);
    }

    /**
     * Adjusts the Specified Field(s) in the Target Class via Reflection
     *
     * @param classToAccess The class to access with the field(s)
     * @param instance      An Instance of the Class, if needed
     * @param value         The value to set for the field
     * @param fieldNames    A List of Field Names to search for
     */
    public static void updateField(final Class<?> classToAccess, final Object instance, final Object value, final String... fieldNames) {
        getValidField(classToAccess, fieldNames).ifPresent(fieldWrapper -> fieldWrapper.set(instance, value));
    }

    /**
     * Invokes the specified Method in the Target Class via Reflection
     *
     * @param classToAccess  The class to access with the method(s)
     * @param instance       An Instance of the Class, if needed
     * @param parameterTypes An array of Class objects representing the types of the method's parameters.
     * @param parameters     An array of objects representing the method's actual parameters.
     * @param methodNames    A List of Method Names to search for
     * @return the resulting method result
     */
    public static Object executeMethod(final Class<?> classToAccess, final Object instance, final Class<?>[] parameterTypes, final Object[] parameters, final String... methodNames) {
        return getValidMethod(classToAccess, parameterTypes, methodNames)
                .map(methodWrapper -> methodWrapper.invokeInstance(instance, parameters))
                .orElse(null);
    }

    /**
     * Strips Color and Formatting Codes from the inputted String
     *
     * @param input The original String to evaluate
     * @return The Stripped and evaluated String
     */
    public static String stripColors(final String input) {
        return isNullOrEmpty(input) ? input : STRIP_COLOR_PATTERN.matcher(input).replaceAll("");
    }

    /**
     * Normalize Line Separator Characters within the inputted String
     *
     * @param input The original String to evaluate
     * @return The Normalized and evaluated String
     */
    public static String normalizeLines(final String input) {
        return isNullOrEmpty(input) ? input : NEW_LINE_PATTERN.matcher(input).replaceAll("\n");
    }

    /**
     * Normalize the Line Separator and Extra Color Data within the inputted String
     *
     * @param input The original String to evaluate
     * @return The Normalized and evaluated String
     */
    public static String normalize(final String input) {
        return stripColors(normalizeLines(input));
    }
}
