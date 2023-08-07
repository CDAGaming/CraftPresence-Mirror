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

import com.gitlab.cdagaming.craftpresence.core.Constants;

import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.nio.charset.Charset;
import java.util.List;
import java.util.Map;
import java.util.function.BiFunction;
import java.util.function.Function;

/**
 * Translation and Localization Utilities based on Language Code
 *
 * @author CDAGaming
 */
public class TranslationUtils {
    /**
     * The Stored Mapping of Language Request History
     * <p>
     * Format: languageId:doesExist
     */
    private final Map<String, Map<String, String>> requestMap = StringUtils.newHashMap();
    /**
     * The default/fallback Language ID to Locate and Retrieve Translations
     */
    private String defaultLanguageId = Constants.MCBuildProtocol >= 315 ? "en_us" : "en_US";
    /**
     * The current Language ID to Locate and Retrieve Translations
     */
    private String languageId = defaultLanguageId;
    /**
     * The Target ID to locate the Language File
     */
    private String modId;
    /**
     * The Charset Encoding to parse translations in
     */
    private String encoding;
    /**
     * If using a .Json or .Lang Language File
     */
    private boolean usingJson = false;
    /**
     * If using the modern "assets/xxx" file-path
     */
    private boolean usingAssetsPath = true;
    /**
     * Whether to Remove Color and Formatting Codes from Translated Strings
     */
    private boolean stripColors = false;
    /**
     * The function to use when retrieving additional {@link InputStream} data for resources
     * <p>
     * Function: [modId, langPath] => List of {@link InputStream} instances
     */
    private BiFunction<String, String, List<InputStream>> resourceSupplier = (id, langPath) -> StringUtils.newArrayList();
    /**
     * The function to use when retrieving the current language to use
     * <p>
     * Function: [fallbackLanguage] => currentLanguage
     */
    private Function<String, String> languageSupplier = (fallback) -> fallback;
    /**
     * If this module needs a full sync
     */
    private boolean needsSync;

    /**
     * Sets initial Data and Retrieves Valid Translations
     */
    public TranslationUtils() {
        this(false);
    }

    /**
     * Sets initial Data and Retrieves Valid Translations
     *
     * @param useJson Toggles whether to use .Json or .Lang, if present
     */
    public TranslationUtils(final boolean useJson) {
        this("", useJson);
    }

    /**
     * Sets initial Data and Retrieves Valid Translations
     *
     * @param modId Sets the Target Mod ID to locate Language Files
     */
    public TranslationUtils(final String modId) {
        this(modId, false);
    }

    /**
     * Sets initial Data and Retrieves Valid Translations
     *
     * @param modId   Sets the Target Mod ID to locate Language Files
     * @param useJson Toggles whether to use .Json or .Lang, if present
     */
    public TranslationUtils(final String modId, final boolean useJson) {
        this(modId, useJson, "UTF-8");
    }

    /**
     * Sets initial Data and Retrieves Valid Translations
     *
     * @param modId    Sets the Target Mod ID to locate Language Files
     * @param useJson  Toggles whether to use .Json or .Lang, if present
     * @param encoding The Charset Encoding to parse Language Files
     */
    public TranslationUtils(final String modId, final boolean useJson, final String encoding) {
        setUsingJson(useJson);
        setModId(modId);
        setEncoding(encoding);
    }

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
            Constants.LOG.debugWarn(Constants.TRANSLATOR.translate("craftpresence.logger.warning.convert.invalid", resultId, mode.name()));
        }

        return resultId.trim();
    }

    /**
     * Attempt to retrieve the localized equivalent of the specified string
     *
     * @param original The string to interpret
     * @return The equivalent localized string, if present
     */
    public String getLocalizedMessage(final String original) {
        String result = original.trim();
        if (result.contains(" ")) {
            String adjusted = result;
            for (String dataPart : result.split(" ")) {
                if (hasTranslation(dataPart)) {
                    adjusted = adjusted.replace(dataPart, translate(dataPart));
                }
            }
            result = adjusted;
        } else if (hasTranslation(original)) {
            result = translate(result);
        }
        return result;
    }

    /**
     * Build and Perform Synchronization on this instance
     *
     * @return the current instance, used for chain-building
     */
    public TranslationUtils build() {
        // Retrieve localized default translations
        syncTranslations(getDefaultLanguage());

        needsSync = true;
        return this;
    }

    /**
     * The Event to Run on each Client Tick, if passed initialization events
     * <p>
     * Consists of Synchronizing Data, and Updating Translation Data as needed
     */
    public void onTick() {
        final String currentLanguageId = getCurrentLanguage();
        final boolean hasLanguageChanged = (!languageId.equals(currentLanguageId) &&
                (!hasTranslationsFrom(currentLanguageId) || !requestMap.get(currentLanguageId).isEmpty()));
        if (Constants.HAS_GAME_LOADED) {
            if (needsSync) {
                // Sync All if we need to (Normally for initialization or reload purposes)
                final List<String> requestedKeys = StringUtils.newArrayList(requestMap.keySet());
                for (String key : requestedKeys) {
                    syncTranslations(key, false);
                }
                needsSync = false;
            } else if (hasLanguageChanged) {
                // Otherwise, only sync the current language if needed
                syncTranslations(currentLanguageId);
            }
        }
    }

    /**
     * Synchronize the translation mappings for the specified language ID
     *
     * @param languageId  the language ID to interpret
     * @param setLanguage Whether we want the language ID to be the one in use
     */
    public void syncTranslations(final String languageId, final boolean setLanguage) {
        if (setLanguage) {
            setLanguage(languageId);
        }
        getTranslationMapFrom(languageId, encoding);
    }

    /**
     * Synchronize the translation mappings for the specified language ID
     *
     * @param languageId the language ID to interpret
     */
    public void syncTranslations(final String languageId) {
        syncTranslations(languageId, true);
    }

    /**
     * Synchronize the translation mappings for all language ids
     */
    public void syncTranslations() {
        needsSync = true;
    }

    /**
     * Determine the current language ID to be using
     *
     * @return the current language id to be used
     */
    private String getCurrentLanguage() {
        final String result = languageSupplier.apply(defaultLanguageId);
        return usingJson ? result.toLowerCase() : result;
    }

    /**
     * Determine the default language ID to be using
     *
     * @return the default language id to be used
     */
    public String getDefaultLanguage() {
        return usingJson ? defaultLanguageId.toLowerCase() : defaultLanguageId;
    }

    /**
     * Sets the Default Language ID to Retrieve Translations for, if present
     *
     * @param languageId The Language ID (Default: en_US)
     * @return the current instance, for chain-building
     */
    public TranslationUtils setDefaultLanguage(final String languageId) {
        this.defaultLanguageId = languageId;
        return this;
    }

    /**
     * Toggles whether to use the modern "assets/xxx" file path when locating translations
     *
     * @param usingAssetsPath Toggles whether to use the modern "assets/xxx" file path
     * @return the current instance, used for chain-building
     */
    public TranslationUtils setUsingAssetsPath(final boolean usingAssetsPath) {
        this.usingAssetsPath = usingAssetsPath;
        return this;
    }

    /**
     * Toggles whether to remove Color and Formatting Codes from Translated Strings
     *
     * @param stripColors the new "Strip Colors" status
     * @return the current instance, used for chain-building
     */
    public TranslationUtils setStripColors(final boolean stripColors) {
        this.stripColors = stripColors;
        return this;
    }

    /**
     * Sets the function to use when retrieving additional {@link InputStream} data for resources
     *
     * @param resourceSupplier the new resource-supplying function
     * @return the current instance, used for chain-building
     */
    public TranslationUtils setResourceSupplier(final BiFunction<String, String, List<InputStream>> resourceSupplier) {
        this.resourceSupplier = resourceSupplier;
        return this;
    }

    /**
     * Sets the function to use when retrieving additional {@link InputStream} data for resources
     *
     * @param resourceSupplier the new resource-supplying function
     * @return the current instance, used for chain-building
     */
    public TranslationUtils setResourceSupplier(final List<InputStream> resourceSupplier) {
        this.resourceSupplier = (id, langPath) -> resourceSupplier;
        return this;
    }

    /**
     * Sets the function to use when retrieving the current language to use
     *
     * @param languageSupplier the new language-supplying function
     * @return the current instance, used for chain-building
     */
    public TranslationUtils setLanguageSupplier(final Function<String, String> languageSupplier) {
        this.languageSupplier = languageSupplier;
        return this;
    }

    /**
     * Sets the function to use when retrieving the current language to use
     *
     * @param languageSupplier the new language-supplying function
     * @return the current instance, used for chain-building
     */
    public TranslationUtils setLanguageSupplier(final String languageSupplier) {
        this.languageSupplier = (fallback) -> languageSupplier;
        return this;
    }

    /**
     * Toggles whether to use .Lang or .Json Language Files
     *
     * @param usingJson Toggles whether to use .Json or .Lang, if present
     * @return the current instance, for chain-building
     */
    public TranslationUtils setUsingJson(final boolean usingJson) {
        this.usingJson = usingJson;
        return this;
    }

    /**
     * Sets the Language ID to Retrieve Translations for, if present
     *
     * @param languageId The Language ID (Default: en_US)
     * @return the current instance, for chain-building
     */
    public TranslationUtils setLanguage(final String languageId) {
        final String result = StringUtils.getOrDefault(languageId, defaultLanguageId);
        this.languageId = usingJson ? result.toLowerCase() : result;
        return this;
    }

    /**
     * Sets the Charset Encoding to parse Translations in, if present
     *
     * @param encoding The Charset Encoding (Default: UTF-8)
     * @return the current instance, for chain-building
     */
    public TranslationUtils setEncoding(final String encoding) {
        this.encoding = StringUtils.getOrDefault(encoding, "UTF-8");
        return this;
    }

    /**
     * Sets the Mod ID to target when locating Language Files
     *
     * @param modId The Mod ID to target
     * @return the current instance, for chain-building
     */
    public TranslationUtils setModId(final String modId) {
        this.modId = StringUtils.getOrDefault(modId);
        return this;
    }

    /**
     * Fetches a list of valid {@link InputStream}'s that can be used for the specified language
     *
     * @param languageId The language ID to interpret
     * @param ext        The file extension to look for (Default: lang or json)
     * @return the interpreted list of valid {@link InputStream}'s
     */
    private List<InputStream> getLocaleStreamsFrom(final String languageId, final String ext) {
        final String assetsPath = usingAssetsPath ? String.format("/assets/%s/", modId) : "/";
        final String langPath = String.format("lang/%s.%s", languageId, ext);
        final List<InputStream> results = StringUtils.newArrayList();

        final InputStream local = FileUtils.getResourceAsStream(TranslationUtils.class, assetsPath + langPath);
        if (local != null) {
            results.add(local);
        }
        results.addAll(resourceSupplier.apply(modId, langPath));
        return results;
    }

    /**
     * Fetches a list of valid {@link InputStream}'s that can be used for the specified language
     *
     * @param languageId The language ID to interpret
     * @return the interpreted list of valid {@link InputStream}'s
     */
    private List<InputStream> getLocaleStreamsFrom(final String languageId) {
        return getLocaleStreamsFrom(languageId, (usingJson ? "json" : "lang"));
    }

    /**
     * Fetches a list of valid {@link InputStream}'s that can be used for the current language
     *
     * @param ext The file extension to look for (Default: lang or json)
     * @return the interpreted list of valid {@link InputStream}'s
     */
    private List<InputStream> getLocaleStreams(final String ext) {
        return getLocaleStreamsFrom(languageId, ext);
    }

    /**
     * Fetches a list of valid {@link InputStream}'s that can be used for the current language
     *
     * @return the interpreted list of valid {@link InputStream}'s
     */
    private List<InputStream> getLocaleStreams() {
        return getLocaleStreamsFrom(languageId);
    }

    /**
     * Retrieves and Synchronizes a List of Translations from a Language File
     *
     * @param languageId The language ID to interpret
     * @param encoding   The Charset Encoding (Default: UTF-8)
     * @param data       The {@link InputStream}'s to accept data from
     * @return the processed list of translations
     */
    private Map<String, String> getTranslationMapFrom(final String languageId, final String encoding, final List<InputStream> data) {
        boolean hasError = false, hadBefore = hasTranslationsFrom(languageId);
        requestMap.remove(languageId);
        final Map<String, String> translationMap = StringUtils.newHashMap();

        if (data != null && !data.isEmpty()) {
            for (InputStream in : data) {
                if (in != null) {
                    try (BufferedReader reader = new BufferedReader(new InputStreamReader(in, Charset.forName(encoding)))) {
                        String currentString;
                        while ((currentString = reader.readLine()) != null) {
                            currentString = currentString.trim();
                            if (!currentString.startsWith("#") && !currentString.startsWith("[{}]") && (usingJson ? currentString.contains(":") : currentString.contains("="))) {
                                final String[] splitTranslation = usingJson ? currentString.split(":", 2) : currentString.split("=", 2);
                                if (usingJson) {
                                    String str1 = splitTranslation[0].substring(1, splitTranslation[0].length() - 1).trim();
                                    String str2 = splitTranslation[1].substring(2, splitTranslation[1].length() - (splitTranslation[1].endsWith(",") ? 2 : 1)).trim();
                                    translationMap.put(
                                            str1.replaceAll("(?s)\\\\(.)", "$1"),
                                            str2.replaceAll("(?s)\\\\(.)", "$1")
                                    );
                                } else {
                                    translationMap.put(splitTranslation[0].trim(), splitTranslation[1].trim());
                                }
                            }
                        }

                        in.close();
                    } catch (Exception ex) {
                        Constants.LOG.error("An exception has occurred while loading Translation Mappings, aborting scan to prevent issues...");
                        Constants.LOG.debugError(ex);
                        hasError = true;
                        break;
                    }
                } else {
                    hasError = true;
                    break;
                }
            }
        } else {
            hasError = true;
        }

        if (hasError) {
            Constants.LOG.error("Translations for " + modId + " do not exist for " + languageId);
            translationMap.clear();
            requestMap.put(languageId, translationMap);
            setLanguage(defaultLanguageId);
        } else {
            Constants.LOG.debugInfo((hadBefore ? "Refreshed" : "Added") + " translations for " + modId + " for " + languageId);
            requestMap.put(languageId, translationMap);
        }
        return translationMap;
    }

    /**
     * Retrieves and Synchronizes a List of Translations from a Language File
     *
     * @param languageId The language ID to interpret
     * @param encoding   The Charset Encoding (Default: UTF-8)
     */
    private Map<String, String> getTranslationMapFrom(final String languageId, final String encoding) {
        return getTranslationMapFrom(languageId, encoding, getLocaleStreamsFrom(languageId));
    }

    /**
     * Retrieves and Synchronizes a List of Translations from a Language File
     *
     * @param languageId The language ID to interpret
     */
    private Map<String, String> getTranslationMapFrom(final String languageId) {
        return getTranslationMapFrom(languageId, "UTF-8");
    }

    /**
     * Retrieves and Synchronizes a List of Translations from a Language File
     */
    private Map<String, String> getTranslationMap() {
        return getTranslationMapFrom(languageId);
    }

    /**
     * Translates an Unlocalized String, based on the translations retrieved for the specified language
     *
     * @param languageId     The language ID to interpret
     * @param stripColors    Whether to Remove Color and Formatting Codes
     * @param translationKey The unLocalized String to translate
     * @param parameters     Extra Formatting Arguments, if needed
     * @return The Localized Translated String
     */
    public String translateFrom(final String languageId, final boolean stripColors, final String translationKey, final Object... parameters) {
        boolean hasError = false;
        String translatedString = translationKey;
        try {
            if (hasTranslationFrom(languageId, translationKey)) {
                String rawString = getTranslationFrom(languageId, translationKey);
                translatedString = parameters.length > 0 ? String.format(rawString, parameters) : rawString;
            } else {
                hasError = true;
            }
        } catch (Exception ex) {
            Constants.LOG.error("Exception parsing " + translationKey + " from " + languageId);
            Constants.LOG.debugError(ex);
            return translationKey;
        }

        if (hasError) {
            Constants.LOG.debugError("Unable to retrieve a translation for " + translationKey + " from " + languageId);
            if (!languageId.equals(getDefaultLanguage())) {
                Constants.LOG.debugError("Attempting to retrieve default translation for " + translationKey);
                return translateFrom(getDefaultLanguage(), stripColors, translationKey, parameters);
            }
        }
        return stripColors ? StringUtils.stripColors(translatedString) : translatedString;
    }

    /**
     * Translates an Unlocalized String, based on the translations retrieved for the specified language
     *
     * @param stripColors    Whether to Remove Color and Formatting Codes
     * @param translationKey The unLocalized String to translate
     * @param parameters     Extra Formatting Arguments, if needed
     * @return The Localized Translated String
     */
    public String translateFrom(final boolean stripColors, final String translationKey, final Object... parameters) {
        return translateFrom(getDefaultLanguage(), stripColors, translationKey, parameters);
    }

    /**
     * Translates an Unlocalized String, based on the translations retrieved for the current language
     *
     * @param stripColors    Whether to Remove Color and Formatting Codes
     * @param translationKey The unLocalized String to translate
     * @param parameters     Extra Formatting Arguments, if needed
     * @return The Localized Translated String
     */
    public String translate(final boolean stripColors, final String translationKey, final Object... parameters) {
        return translateFrom(languageId, stripColors, translationKey, parameters);
    }

    /**
     * Translates an Unlocalized String, based on the translations retrieved for the specified language
     *
     * @param languageId     The language ID to interpret
     * @param translationKey The unLocalized String to translate
     * @param parameters     Extra Formatting Arguments, if needed
     * @return The Localized Translated String
     */
    public String translateFrom(final String languageId, final String translationKey, final Object... parameters) {
        return translateFrom(languageId, stripColors, translationKey, parameters);
    }

    /**
     * Translates an Unlocalized String, based on the translations retrieved for the specified language
     *
     * @param translationKey The unLocalized String to translate
     * @param parameters     Extra Formatting Arguments, if needed
     * @return The Localized Translated String
     */
    public String translateFrom(final String translationKey, final Object... parameters) {
        return translateFrom(getDefaultLanguage(), translationKey, parameters);
    }

    /**
     * Translates an Unlocalized String, based on the translations retrieved for the current language
     *
     * @param translationKey The unLocalized String to translate
     * @param parameters     Extra Formatting Arguments, if needed
     * @return The Localized Translated String
     */
    public String translate(final String translationKey, final Object... parameters) {
        return translateFrom(languageId, translationKey, parameters);
    }

    /**
     * Determines whether translations are present for the specified language
     *
     * @param languageId The language ID to interpret
     * @return whether translations are present for this language
     */
    public boolean hasTranslationsFrom(final String languageId) {
        return requestMap.containsKey(languageId);
    }

    /**
     * Determines whether the specified translation exists for the specified language
     *
     * @param languageId     The language ID to interpret
     * @param translationKey The unLocalized String to interpret
     * @return whether the specified translation exists
     */
    public boolean hasTranslationFrom(final String languageId, final String translationKey) {
        if (hasTranslationsFrom(languageId)) {
            return requestMap.get(languageId).containsKey(translationKey);
        } else {
            return getTranslationMapFrom(languageId).containsKey(translationKey);
        }
    }

    /**
     * Determines whether the specified translation exists for the current language
     *
     * @param translationKey The unLocalized String to interpret
     * @return whether the specified translation exists
     */
    public boolean hasTranslation(final String translationKey) {
        return hasTranslationFrom(languageId, translationKey);
    }

    /**
     * Retrieves the specified translation, if it exists for the specified language
     *
     * @param languageId     The language ID to interpret
     * @param translationKey The unLocalized String to interpret
     * @return whether the specified translation exists
     */
    public String getTranslationFrom(final String languageId, final String translationKey) {
        if (hasTranslationFrom(languageId, translationKey)) {
            return requestMap.get(languageId).get(translationKey);
        }
        return null;
    }

    /**
     * Retrieves the specified translation, if it exists for the current language
     *
     * @param translationKey The unLocalized String to interpret
     * @return whether the specified translation exists
     */
    public String getTranslation(final String translationKey) {
        return getTranslationFrom(languageId, translationKey);
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
