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

package com.gitlab.cdagaming.craftpresence.config.migrators;

import com.gitlab.cdagaming.craftpresence.ModUtils;
import com.gitlab.cdagaming.craftpresence.config.Config;
import com.gitlab.cdagaming.craftpresence.impl.Pair;
import com.gitlab.cdagaming.craftpresence.impl.Tuple;
import com.gitlab.cdagaming.craftpresence.utils.StringUtils;
import com.google.common.collect.ImmutableMap;
import com.google.common.collect.Lists;
import com.google.gson.JsonElement;

import java.io.File;
import java.io.FileInputStream;
import java.io.InputStreamReader;
import java.io.Reader;
import java.nio.charset.Charset;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Properties;

@SuppressWarnings({"ConstantConditions", "unchecked", "rawtypes"})
public class Legacy2Modern implements DataMigrator {
    private final File configFile;
    private final String encoding;
    private final Properties properties = new Properties();
    private final String translationPrefix = "gui.config.name.";

    // oldName -> newName
    private final Map<String, String> configNameMappings = ImmutableMap.<String, String>builder()
            .put("preferredClient", "preferredClientLevel")
            .put("enablePerGuiSystem", "enablePerGui")
            .put("gameStateMessageFormat", "gameStateMessage")
            .put("enablePerEntitySystem", "enablePerEntity")
            .put("enablePerItemSystem", "enablePerItem")
            .put("playerCoordinatePlaceholder", "playerCoordinatePlaceholderMessage")
            .put("configGuiKeybind", "configKeyCode")
            .put("largeImageTextFormat", "largeImageMessage")
            .put("playerInnerInfoPlaceholder", "innerPlayerPlaceholderMessage")
            .put("detectMcupdaterInstance", "detectMCUpdaterInstance") // Corrected casing due to camel-case side-effect
            .put("singleplayerGameMessage", "singlePlayerMessage")
            .put("playerOuterInfoPlaceholder", "outerPlayerPlaceholderMessage")
            .put("smallImageKeyFormat", "smallImageKey")
            .put("showElapsedTime", "showTime")
            .put("playerHealthPlaceholder", "playerHealthPlaceholderMessage")
            .put("worldDataPlaceholder", "worldPlaceholderMessage")
            .put("detectMultimcInstance", "detectMultiMCManifest")
            .put("playerListPlaceholder", "playerAmountPlaceholderMessage")
            .put("lanGameMessage", "lanMessage")
            .put("fallbackPackPlaceholder", "fallbackPackPlaceholderMessage")
            .put("playerItemsPlaceholder", "playerItemsPlaceholderMessage")
            .put("modsPlaceholder", "modsPlaceholderMessage")
            .put("roundingSize", "roundSize")
            .put("partyPrivacy", "partyPrivacyLevel")
            .put("extraButtonMessages", "buttonMessages")
            .put("detailsMessageFormat", "detailsMessage")
            .put("reducedBackgroundTint", "showBackgroundAsDark")
            .put("largeImageKeyFormat", "largeImageKey")
            .put("modpackMessage", "packPlaceholderMessage")
            .put("smallImageTextFormat", "smallImageMessage")
            .build();
    private final List<String> excludedOptions = Lists.newArrayList(
            "schemaVersion", "splitCharacter",
            "guiBackgroundColor", "buttonBackgroundColor", "tooltipBackgroundColor", "tooltipBorderColor"
    );

    public Legacy2Modern(File configFile, String encoding) {
        this.configFile = configFile;
        this.encoding = encoding;
    }

    @Override
    public Config apply(Config instance, JsonElement rawJson, Object... args) {
        Reader configReader = null;
        FileInputStream inputStream = null;

        try {
            inputStream = new FileInputStream(configFile);
            configReader = new InputStreamReader(inputStream, Charset.forName(encoding));
            properties.load(configReader);
        } catch (Exception ex) {
            ModUtils.LOG.error(ModUtils.TRANSLATOR.translate(true, "craftpresence.logger.error.config.save"));
            ex.printStackTrace();
        } finally {
            String originalName, newName;
            Object originalValue, currentValue, newValue;
            Class<?> expectedClass;

            final String splitCharacter = properties.getProperty("splitCharacter", ";");
            for (String property : properties.stringPropertyNames()) {
                originalName = StringUtils.formatToCamel(property);
                if (!excludedOptions.contains(originalName)) {
                    newName = configNameMappings.getOrDefault(originalName, originalName);
                    originalValue = properties.get(property);
                    newValue = currentValue = StringUtils.lookupObject(Config.class, instance, newName);

                    if (currentValue != null) {
                        expectedClass = currentValue.getClass();
                        if ((expectedClass == boolean.class || expectedClass == Boolean.class) &&
                                StringUtils.isValidBoolean(originalValue)) {
                            newValue = Boolean.parseBoolean(originalValue.toString());
                        } else if ((expectedClass == int.class || expectedClass == Integer.class) &&
                                StringUtils.getValidInteger(originalValue).getFirst()) {
                            final Pair<Boolean, Integer> boolData = StringUtils.getValidInteger(originalValue);
                            if (boolData.getFirst()) {
                                newValue = boolData.getSecond();
                            }
                        } else if (currentValue instanceof Map) {
                            final String convertedString = StringUtils.removeMatches(StringUtils.getMatches("\\[([^\\s]+?)\\]", originalValue), null, 1);
                            final String[] oldArray;

                            final Map newData = new HashMap((Map) currentValue);
                            final Class<?> expectedSecondaryClass = newData.get("default").getClass();

                            if (!StringUtils.isNullOrEmpty(convertedString) &&
                                    (convertedString.startsWith("[") && convertedString.endsWith("]"))) {
                                // If Valid, interpret into formatted Array
                                final String preArrayString = convertedString.replaceAll("\\[", "").replaceAll("]", "");
                                if (preArrayString.contains(", ")) {
                                    oldArray = preArrayString.split(", ");
                                } else if (preArrayString.contains(",")) {
                                    oldArray = preArrayString.split(",");
                                } else {
                                    oldArray = new String[]{preArrayString};
                                }
                            } else {
                                oldArray = null;
                            }

                            if (oldArray != null) {
                                newData.clear();
                                for (String entry : oldArray) {
                                    if (!StringUtils.isNullOrEmpty(entry)) {
                                        final String[] part = entry.split(splitCharacter);
                                        if (!StringUtils.isNullOrEmpty(part[0])) {
                                            if (expectedSecondaryClass == Pair.class) {
                                                newData.put(part[0], new Pair<>(
                                                        part.length >= 2 ? part[1] : "",
                                                        part.length >= 3 ? part[2] : ""
                                                ));
                                            } else if (expectedSecondaryClass == Tuple.class) {
                                                newData.put(part[0], new Tuple<>(
                                                        part.length >= 2 ? part[1] : "",
                                                        part.length >= 3 ? part[2] : "",
                                                        part.length >= 4 ? part[3] : ""
                                                ));
                                            } else {
                                                newData.put(part[0], part.length >= 2 ? part[1] : "");
                                            }
                                        }
                                    }
                                }
                                newValue = newData;
                            }
                        } else {
                            newValue = originalValue.toString();
                        }

                        if (!currentValue.equals(newValue)) {
                            ModUtils.LOG.info("Migrating modified legacy property " + originalName + " to JSON property " + newName);
                            StringUtils.updateField(Config.class, instance, new Tuple<>(newName, newValue, null));
                        }
                    }
                }
            }
        }

        try {
            if (configReader != null) {
                configReader.close();
            }
            if (inputStream != null) {
                inputStream.close();
            }
        } catch (Exception ex) {
            ModUtils.LOG.error(ModUtils.TRANSLATOR.translate(true, "craftpresence.logger.error.data.close"));
            ex.printStackTrace();
        } finally {
            if (!configFile.delete()) {
                ModUtils.LOG.error("Failed to remove: " + configFile.getName());
            }
        }
        return instance;
    }
}
