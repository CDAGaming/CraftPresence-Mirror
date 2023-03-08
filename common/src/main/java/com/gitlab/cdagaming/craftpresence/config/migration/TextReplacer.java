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

package com.gitlab.cdagaming.craftpresence.config.migration;

import com.gitlab.cdagaming.craftpresence.ModUtils;
import com.gitlab.cdagaming.craftpresence.config.Config;
import com.gitlab.cdagaming.craftpresence.config.Module;
import com.gitlab.cdagaming.craftpresence.utils.StringUtils;
import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import com.google.gson.JsonElement;
import org.apache.commons.lang3.tuple.Pair;

import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@SuppressWarnings({"unchecked", "rawtypes"})
public class TextReplacer implements DataMigrator {
    final Map<String, String> replacers;
    final boolean placeholderMode, matchCase, matchWholeWorld, useRegex;

    public TextReplacer(final Map<String, String> replacers, final boolean placeholderMode,
                        final boolean matchCase, final boolean matchWholeWorld, final boolean useRegex) {
        this.replacers = Maps.newHashMap(replacers);
        this.placeholderMode = placeholderMode;
        this.matchCase = matchCase;
        this.matchWholeWorld = matchWholeWorld;
        this.useRegex = useRegex;
    }

    @Override
    public Config apply(Config instance, JsonElement rawJson, Object... args) {
        processElement(instance, rawJson);
        return instance;
    }

    private String processReplacement(final String original) {
        String result = original;
        if (placeholderMode) {
            final Pair<String, List<String>> expressions = StringUtils.getMatches("\\{[^{}]*\\}", original);
            if (!expressions.getRight().isEmpty()) {
                for (String match : expressions.getRight()) {
                    result = result.replace(match,
                            StringUtils.sequentialReplace(match, matchCase, matchWholeWorld, useRegex, replacers)
                    );
                }
            }
        } else {
            result = StringUtils.sequentialReplace(result, matchCase, matchWholeWorld, useRegex, replacers);
        }
        return result;
    }

    // Cloned from Config#handleVerification
    private JsonElement processElement(final Config instance, final JsonElement rawJson, final String... path) {
        // Verify Type Safety, reset value if anything is null or invalid for it's type
        String pathPrefix = StringUtils.join(".", Arrays.asList(path));
        if (!StringUtils.isNullOrEmpty(pathPrefix)) {
            pathPrefix += ".";
        }

        if (rawJson != null) {
            for (Map.Entry<String, JsonElement> entry : rawJson.getAsJsonObject().entrySet()) {
                final String rawName = pathPrefix + entry.getKey();
                final List<String> configPath = Lists.newArrayList(path);
                configPath.add(entry.getKey());
                final String[] pathData = configPath.toArray(new String[0]);

                Object defaultValue = instance.getDefaults().getProperty(pathData);
                Object currentValue = instance.getProperty(pathData);
                boolean shouldContinue = true;

                if (defaultValue == null) {
                    if (currentValue == null) {
                        shouldContinue = false;
                    } else {
                        defaultValue = currentValue;
                    }
                }

                if (shouldContinue) {
                    if (Module.class.isAssignableFrom(defaultValue.getClass())) {
                        final List<String> paths = Lists.newArrayList(path);
                        paths.add(entry.getKey());
                        processElement(instance, entry.getValue(), paths.toArray(new String[0]));
                    } else if (currentValue instanceof String) {
                        final String originalResult = (String) currentValue;
                        final String processResult = processReplacement(originalResult);
                        if (!processResult.equals(originalResult)) {
                            ModUtils.LOG.debugInfo("Modified property \"%s\": \"%s\" => \"%s\"", rawName, originalResult, processResult);
                            instance.setProperty((Object) processResult, pathData);
                        }
                    } else if (currentValue instanceof Map) {
                        final Map newData = new HashMap((Map) currentValue);
                        if (entry.getValue().isJsonObject()) {
                            for (Object dataEntry : newData.keySet()) {
                                final List<String> paths = Lists.newArrayList(path);
                                paths.add(entry.getKey());
                                paths.add(dataEntry.toString());
                                final JsonElement dataValue = entry.getValue().getAsJsonObject().get(dataEntry.toString());
                                if (dataValue.isJsonObject()) {
                                    processElement(instance, dataValue, paths.toArray(new String[0]));
                                }
                            }
                        }
                    }
                }
            }
        }
        return rawJson;
    }
}
