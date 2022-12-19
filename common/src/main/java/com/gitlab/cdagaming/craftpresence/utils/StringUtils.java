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

package com.gitlab.cdagaming.craftpresence.utils;

import com.gitlab.cdagaming.craftpresence.ModUtils;
import com.gitlab.cdagaming.craftpresence.impl.Pair;
import com.gitlab.cdagaming.craftpresence.impl.Tuple;
import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import net.minecraft.entity.Entity;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.util.text.TextComponentString;

import java.awt.*;
import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
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
    public static final char COLOR_CHAR = '\u00A7';
    /**
     * Regex Pattern for Color and Formatting Codes
     */
    public static final Pattern STRIP_COLOR_PATTERN = Pattern.compile("(?i)" + COLOR_CHAR + "[0-9A-FK-OR]");
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
     * Regex Pattern for Alpha-numeric characters within a string
     */
    private static final Pattern ALPHANUMERIC_PATTERN = Pattern.compile(".*[a-zA-Z0-9].*");

    /**
     * Attempts to Convert a Hexadecimal String into a Valid interpretable Java Color
     *
     * @param hexColor The inputted Hexadecimal Color String
     * @return A Valid Java Color, if successful
     */
    public static Color getColorFromHex(final String hexColor) {
        try {
            if (hexColor.length() == 7 && !StringUtils.isNullOrEmpty(hexColor.substring(1))) {
                int r = Integer.valueOf(hexColor.substring(1, 3), 16);
                int g = Integer.valueOf(hexColor.substring(3, 5), 16);
                int b = Integer.valueOf(hexColor.substring(5, 7), 16);

                return new Color(r, g, b);
            } else if (hexColor.length() == 6 && !hexColor.startsWith("#")) {
                int r = Integer.valueOf(hexColor.substring(0, 2), 16);
                int g = Integer.valueOf(hexColor.substring(2, 4), 16);
                int b = Integer.valueOf(hexColor.substring(4, 6), 16);

                return new Color(r, g, b);
            } else {
                return Color.white;
            }
        } catch (Exception ex) {
            return Color.white;
        }
    }

    /**
     * Converts a String and it's bytes to that of the Specified Charset
     *
     * @param original The original String
     * @param encoding The Charset to encode the String under
     * @param decode   If we are Decoding an already encoded String
     * @return The converted UTF_8 String, if successful
     */
    public static String convertString(String original, String encoding, boolean decode) {
        try {
            if (decode) {
                return new String(original.getBytes(), encoding);
            } else {
                return new String(original.getBytes(encoding));
            }
        } catch (Exception ex) {
            return original;
        }
    }

    /**
     * Rounds a Double to the defined decimal place, if possible
     *
     * @param value  the original value to round
     * @param places The amount of places to round upon
     * @return The rounded Double value
     */
    public static double roundDouble(double value, int places) {
        if (places >= 0) {
            double mod = 1;
            for (int i = 0; i < places; i++) mod /= 10;
            return value + mod * .5 - ((value + mod * .5) % mod);
        } else {
            // Do not Round if Places is less then 0
            return value;
        }
    }

    /**
     * Converts a Java Color Variable into a Hexadecimal String
     *
     * @param color The original Java Color Type to interpret
     * @return The converted hexadecimal String
     */
    public static String getHexFromColor(Color color) {
        return "0x" + toSafeHexValue(color.getAlpha()) + toSafeHexValue(color.getRed()) + toSafeHexValue(color.getGreen()) + toSafeHexValue(color.getBlue());
    }

    /**
     * Converts an inputted number to a compatible Hexadecimal String
     *
     * @param number The original number
     * @return The converted and compatible hexadecimal String
     */
    private static String toSafeHexValue(int number) {
        StringBuilder builder = new StringBuilder(Integer.toHexString(number & 0xff));
        while (builder.length() < 2) {
            builder.append("0");
        }
        return builder.toString().toUpperCase();
    }

    /**
     * Retrieve the primary value if non-empty; Otherwise, use the secondary value
     *
     * @param primary   The primary value to interpret
     * @param secondary The secondary value to interpret
     * @return the resulting value
     */
    public static String getOrDefault(final String primary, final String secondary) {
        return !StringUtils.isNullOrEmpty(primary) ? primary : secondary;
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
     * Retrieve Matching Values from an input that matches the defined regex
     *
     * @param regexValue The Regex Value to test against
     * @param original   The original Object to get matches from
     * @return A Pair with the Format of originalString:listOfMatches
     */
    public static Pair<String, List<String>> getMatches(final String regexValue, final Object original) {
        return original != null ? getMatches(regexValue, original.toString()) : new Pair<>("", Lists.newArrayList());
    }

    /**
     * Retrieve Matching Values from an input that matches the defined regex
     *
     * @param regexValue The Regex Value to test against
     * @param original   The original String to get matches from
     * @return A Pair with the Format of originalString:listOfMatches
     */
    public static Pair<String, List<String>> getMatches(final String regexValue, final String original) {
        final List<String> matches = Lists.newArrayList();

        if (!isNullOrEmpty(original)) {
            final Pattern pattern = Pattern.compile(regexValue);
            final Matcher m = pattern.matcher(original);

            while (m.find()) {
                matches.add(m.group());
            }
        }

        return new Pair<>(original, matches);
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
     * Replaces Data in a String with Case-Insensitivity
     *
     * @param source          The original String to replace within
     * @param targetToReplace The value to replace on
     * @param replaceWith     The value to replace the target with
     * @return The completed and replaced String
     */
    public static String replaceAnyCase(final String source, final String targetToReplace, final String replaceWith) {
        if (!isNullOrEmpty(source)) {
            return Pattern.compile(targetToReplace, Pattern.LITERAL | Pattern.CASE_INSENSITIVE | Pattern.UNICODE_CASE).matcher(source)
                    .replaceAll(Matcher.quoteReplacement(replaceWith));
        } else {
            return "";
        }
    }

    /**
     * Replaces Data in a sequential order, following Case-Insensitivity
     *
     * @param source      The original String to replace within
     * @param replaceArgs The replacement list to follow with the form of: targetToReplace:replaceWithValue
     * @return The completed and replaced String
     */
    @SafeVarargs
    public static String sequentialReplaceAnyCase(final String source, final Map<String, String>... replaceArgs) {
        if (!isNullOrEmpty(source)) {
            String finalResult = source;

            for (Map<String, String> replaceData : replaceArgs) {
                if (!replaceData.isEmpty()) {
                    for (Map.Entry<String, String> replacementData : replaceData.entrySet()) {
                        finalResult = replaceAnyCase(finalResult, replacementData.getKey(), replacementData.getValue());
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
     * @return {@code true} if Entry is classified as NULL or EMPTY
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
     * @return {@code true} if Entry is classified as NULL or EMPTY
     */
    public static boolean isNullOrEmpty(final String entry) {
        return isNullOrEmpty(entry, false);
    }

    /**
     * Determines whether the Object's String Interpretation classifies as a valid Boolean
     *
     * @param entry The Object to evaluate
     * @return {@code true} if Entry is classified as a valid Boolean
     */
    public static boolean isValidBoolean(final Object entry) {
        return entry != null && isValidBoolean(entry.toString());
    }

    /**
     * Determines whether a String classifies as a valid Boolean
     *
     * @param entry The String to evaluate
     * @return {@code true} if Entry is classified as a valid Boolean
     */
    public static boolean isValidBoolean(final String entry) {
        return !isNullOrEmpty(entry) && (entry.equalsIgnoreCase("true") || entry.equalsIgnoreCase("false"));
    }

    /**
     * Determines whether an inputted String classifies as a valid Color Code
     *
     * @param entry The String to evaluate
     * @return {@code true} if Entry is classified as a valid Color Code
     */
    public static boolean isValidColorCode(final String entry) {
        return !isNullOrEmpty(entry) && ((entry.startsWith("#") || entry.length() == 6) || entry.startsWith("0x") || getValidInteger(entry).getFirst());
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
     * Whether the specified string contains alpha-numeric characters
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
            String[] words = original.split("[\\W_]+");
            StringBuilder builder = new StringBuilder();
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
     * @param original The original String to evaluate
     * @return The converted and valid String, in an iconKey Format
     */
    public static String formatAsIcon(final String original) {
        String formattedKey = original;
        if (isNullOrEmpty(formattedKey)) {
            return formattedKey;
        } else {
            if (containsWhitespace(formattedKey)) {
                formattedKey = formattedKey.replaceAll("\\s+", "");
            }
            if (formattedKey.contains("'")) {
                formattedKey = formattedKey.replaceAll("'", "");
            }
            if (formattedKey.contains(".")) {
                formattedKey = formattedKey.replaceAll("\\.", "_");
            }
            if (formattedKey.contains("(")) {
                formattedKey = formattedKey.replaceAll("\\(", "_");
            }
            if (formattedKey.contains(")")) {
                formattedKey = formattedKey.replaceAll("\\)", "_");
            }
            if (BRACKET_PATTERN.matcher(formattedKey).find()) {
                formattedKey = BRACKET_PATTERN.matcher(formattedKey).replaceAll("");
            }
            if (STRIP_COLOR_PATTERN.matcher(formattedKey).find()) {
                formattedKey = STRIP_COLOR_PATTERN.matcher(formattedKey).replaceAll("");
            }
            return formattedKey.toLowerCase().trim();
        }
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
        return !StringUtils.isNullOrEmpty(input) &&
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
        if (!StringUtils.isValidUuid(input)) {
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
    public static <T> List<T> addEntriesNotPresent(List<T> original, List<T> newList) {
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
    public static <T> List<T> addEntriesNotPresent(List<T> original, Predicate<? super T> filter, List<T> newList) {
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
    public static <T> List<T> addEntriesNotPresent(List<T> original, Set<T> newList) {
        return addEntriesNotPresent(original, Lists.newArrayList(newList));
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
    public static <T> List<T> addEntriesNotPresent(List<T> original, Predicate<? super T> filter, Set<T> newList) {
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
            if (formattedKey.toString().contains("WorldProvider")) {
                if (ModUtils.IS_LEGACY_SOFT && ModUtils.MCProtocolID <= 11 && formattedKey.toString().equals("WorldProvider")) {
                    formattedKey = new StringBuilder("overworld");
                } else {
                    formattedKey = new StringBuilder(formattedKey.toString().replace("WorldProvider", ""));
                }
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
                return formatAsIcon(formattedKey.toString().replace(" ", "_"));
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
    public static String getFormatFromString(String text) {
        StringBuilder s = new StringBuilder();
        int index = -1;
        int stringLength = text.length();

        while ((index = text.indexOf(167, index + 1)) != -1) {
            if (index < stringLength - 1) {
                char currentCharacter = text.charAt(index + 1);

                String charString = String.valueOf(currentCharacter);
                if (STRIP_COLOR_PATTERN.matcher(charString).find()) {
                    s = new StringBuilder(COLOR_CHAR + charString);
                }
            }
        }

        return s.toString();
    }

    /**
     * Convert the specified string into the specified date format, if able
     *
     * @param original        The original string to interpret
     * @param originalPattern The original date format pattern to interpret
     * @param newPattern      The new date format pattern to interpret
     * @return The converted and parsed time string
     */
    public static String convertTime(final String original, final String originalPattern, final String newPattern) {
        try {
            final DateFormat oldFormat = new SimpleDateFormat(originalPattern);
            final Date oldInfo = oldFormat.parse(original);
            final DateFormat newFormat = new SimpleDateFormat(newPattern);
            return newFormat.format(oldInfo);
        } catch (Exception ex) {
            return original;
        }
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
            // remaining is more then 0 or is -1 (Infinite)
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
    public static String capitalizeWord(String str) {
        return capitalizeWord(str, -1);
    }

    /**
     * Converts a String into a List of Strings, split up by new lines
     *
     * @param original The original String
     * @return The converted, newline-split list from the original String
     */
    public static List<String> splitTextByNewLine(final String original) {
        if (!isNullOrEmpty(original)) {
            String formattedText = original;
            if (formattedText.contains("\n")) {
                formattedText = original.replace("\n", "&newline&");
            }
            if (formattedText.contains("\\n")) {
                formattedText = original.replace("\\n", "&newline&");
            }
            if (formattedText.contains("\\\\n+")) {
                formattedText = original.replace("\\\\n+", "&newline&");
            }
            return Lists.newArrayList(formattedText.split("&newline&"));
        } else {
            return Lists.newArrayList();
        }
    }

    /**
     * Joins a set of strings together by the specified separator
     *
     * @param separator The string the list should be linked together by
     * @param input     The list of data to interpret
     * @return The resulting string
     */
    public static String join(final String separator, final List<String> input) {
        if (input == null || input.size() == 0) return "";

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
     * Display a Message to the Player, via the in-game Chat Hud
     *
     * @param sender  The Entity to Send to (Must be a Player)
     * @param message The Message to send and display in chat
     */
    public static void sendMessageToPlayer(final Entity sender, final String message) {
        if (sender instanceof EntityPlayer) {
            final EntityPlayer player = (EntityPlayer) sender;
            final List<String> lines = splitTextByNewLine(message);
            if (!lines.isEmpty()) {
                for (String line : lines) {
                    player.sendMessage(new TextComponentString(line));
                }
            }
        }
    }

    /**
     * Determines if the Specified index exists in the List with a non-null value
     *
     * @param data  The Array of Strings to check within
     * @param index The index to check
     * @return {@code true} if the index element exists in the list with a non-null value
     */
    public static boolean elementExists(final String[] data, final int index) {
        return elementExists(Arrays.asList(data), index);
    }

    /**
     * Determines if the Specified index exists in the List with a non-null value
     *
     * @param data  The List of Strings to check within
     * @param index The index to check
     * @return {@code true} if the index element exists in the list with a non-null value
     */
    public static boolean elementExists(final List<String> data, final int index) {
        boolean result;
        try {
            result = data.size() >= index && !isNullOrEmpty(data.get(index));
        } catch (Exception ex) {
            result = false;
        }
        return result;
    }

    /**
     * Retrieves the Specified Inner Object from a List of Fields via Reflection
     *
     * @param fields   The field(s) to interpret
     * @param instance An Instance of the root class, if needed
     * @param name     The field name to search for
     * @return The Found Field Data, if any
     */
    public static Object lookupInnerObject(List<Field> fields, Object instance, String name) {
        for (Field f : fields) {
            try {
                if (doesClassContainField(f.getType(), name)) {
                    return lookupObject(f.getType(), f.get(instance), name);
                }
            } catch (Throwable ex) {
                if (ModUtils.IS_VERBOSE) {
                    ex.printStackTrace();
                }
            }
        }
        return null;
    }

    /**
     * Retrieves the Specified Field(s) via Reflection
     *
     * @param classToAccess The class to access with the field(s)
     * @param instance      An Instance of the Class, if needed
     * @param fieldNames    A List of Field Names to search for
     * @return The Found Field Data, if any
     */
    public static Object lookupObject(Class<?> classToAccess, Object instance, String... fieldNames) {
        for (String fieldName : fieldNames) {
            try {
                if (doesClassContainField(classToAccess, fieldName)) {
                    Field lookupField = classToAccess.getDeclaredField(fieldName);
                    lookupField.setAccessible(true);
                    return lookupField.get(instance);
                }
            } catch (Throwable ex) {
                if (ModUtils.IS_VERBOSE) {
                    ex.printStackTrace();
                }
            }
        }
        return null;
    }

    /**
     * Retrieves the Specified Field(s) via Reflection
     *
     * @param classToAccess The class to access with the field(s)
     * @param instance      An Instance of the Class, if needed
     * @param fieldNames    A List of Field Names to search for
     * @return The Found Field Data, if any
     */
    public static Object lookupObject(String classToAccess, Object instance, String... fieldNames) {
        final Class<?> foundClass = FileUtils.findValidClass(classToAccess);
        if (foundClass != null) {
            return lookupObject(foundClass, instance, fieldNames);
        }
        return null;
    }

    /**
     * Retrieves whether the specified class contains the specified field name
     *
     * @param classToAccess The class to access with the field(s)
     * @param fieldName     The Field name to search for
     * @return whether the specified class contains the specified field name
     */
    public static boolean doesClassContainField(final Class<?> classToAccess, final String fieldName) {
        return Lists.newArrayList(classToAccess.getDeclaredFields()).stream().anyMatch(f -> f.getName().equals(fieldName));
    }

    /**
     * Retrieves whether the specified class contains the specified field name
     *
     * @param classToAccess The class to access with the field(s)
     * @param fieldName     The Field name to search for
     * @return whether the specified class contains the specified field name
     */
    public static boolean doesClassContainField(final String classToAccess, final String fieldName) {
        final Class<?> foundClass = FileUtils.findValidClass(classToAccess);
        if (foundClass != null) {
            return doesClassContainField(foundClass, fieldName);
        }
        return false;
    }

    /**
     * Adjusts the specified Inner Object from a List of Fields via Reflection
     *
     * @param fields    The field(s) to interpret
     * @param instance  An Instance of the root class, if needed
     * @param fieldData A Pair with the format of fieldName:valueToSet:modifierData
     * @return {@link Boolean#TRUE} if the operation succeeded
     */
    public static boolean updateInnerObject(List<Field> fields, Object instance, Tuple<?, ?, ?> fieldData) {
        for (Field f : fields) {
            try {
                if (doesClassContainField(f.getType(), fieldData.getFirst().toString())) {
                    updateField(f.getType(), f.get(instance), fieldData);
                    return true;
                }
            } catch (Throwable ex) {
                if (ModUtils.IS_VERBOSE) {
                    ex.printStackTrace();
                }
            }
        }
        return false;
    }

    /**
     * Adjusts the Specified Field(s) in the Target Class via Reflection
     *
     * @param classToAccess The class to access with the field(s)
     * @param instance      An Instance of the Class, if needed
     * @param fieldData     A Pair with the format of fieldName:valueToSet:modifierData
     */
    public static void updateField(Class<?> classToAccess, Object instance, Tuple<?, ?, ?>... fieldData) {
        for (Tuple<?, ?, ?> currentData : fieldData) {
            try {
                Field lookupField = classToAccess.getDeclaredField(currentData.getFirst().toString());
                lookupField.setAccessible(true);

                if (currentData.getThird() != null) {
                    Field modifiersField = Field.class.getDeclaredField("modifiers");
                    modifiersField.setAccessible(true);
                    modifiersField.setInt(lookupField, lookupField.getModifiers() & Integer.parseInt(currentData.getThird().toString()));
                }

                lookupField.set(instance, currentData.getSecond());
                if (ModUtils.IS_VERBOSE) {
                    ModUtils.LOG.debugInfo(ModUtils.TRANSLATOR.translate("craftpresence.logger.info.update.dynamic", currentData.toString(), classToAccess.getName()));
                }
            } catch (Exception ex) {
                if (ModUtils.IS_VERBOSE) {
                    ex.printStackTrace();
                }
            }
        }
    }

    /**
     * Adjusts the Specified Field(s) in the Target Class via Reflection
     *
     * @param classToAccess The class to access with the field(s)
     * @param instance      An Instance of the Class, if needed
     * @param fieldData     A Pair with the format of fieldName:valueToSet:modifierData
     */
    public static void updateField(String classToAccess, Object instance, Tuple<?, ?, ?>... fieldData) {
        final Class<?> foundClass = FileUtils.findValidClass(classToAccess);
        if (foundClass != null) {
            updateField(foundClass, instance, fieldData);
        }
    }

    /**
     * Invokes the specified Method(s) in the Target Class via Reflection
     *
     * @param classToAccess The class to access with the method(s)
     * @param instance      An Instance of the Class, if needed
     * @param methodData    The Methods and Necessary Argument Data for execution, in the form of methodName:argsAndTypesForMethod
     * @return the resulting data mapping with the format of methodName:methodResult
     */
    @SafeVarargs
    public static Map<String, Object> executeMethod(final Class<?> classToAccess, final Object instance, final Pair<String, Pair<Object[], Class<?>[]>>... methodData) {
        final Map<String, Object> results = Maps.newHashMap();
        for (Pair<String, Pair<Object[], Class<?>[]>> methodInstance : methodData) {
            Object result = null;
            try {
                final Method lookupMethod = classToAccess.getDeclaredMethod(methodInstance.getFirst(), methodInstance.getSecond().getSecond());
                lookupMethod.setAccessible(true);
                result = lookupMethod.invoke(instance, methodInstance.getSecond().getFirst());
            } catch (Throwable ex) {
                if (ModUtils.IS_VERBOSE) {
                    ex.printStackTrace();
                }
            }
            results.put(methodInstance.getFirst(), result);
        }
        return results;
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
}
