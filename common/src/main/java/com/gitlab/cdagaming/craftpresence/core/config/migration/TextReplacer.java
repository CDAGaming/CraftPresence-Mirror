/*
 * MIT License
 *
 * Copyright (c) 2018 - 2026 CDAGaming (cstack2011@yahoo.com)
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

package com.gitlab.cdagaming.craftpresence.core.config.migration;

import com.gitlab.cdagaming.craftpresence.core.Constants;
import com.gitlab.cdagaming.craftpresence.core.config.Config;
import com.gitlab.cdagaming.craftpresence.core.config.Module;
import com.google.gson.JsonElement;
import io.github.cdagaming.unicore.utils.StringUtils;

import java.util.List;
import java.util.Map;
import java.util.regex.Pattern;

public class TextReplacer implements DataMigrator {
    private static final Pattern EXPR_PATTERN = Pattern.compile("\\{[^{}]*}");

    final Map<String, String> replacers;
    final boolean placeholderMode;

    public TextReplacer(final Map<String, String> replacers, final boolean placeholderMode) {
        this.replacers = StringUtils.newHashMap(replacers);
        this.placeholderMode = placeholderMode;
    }

    @Override
    public Config apply(Config instance, JsonElement rawJson, Object... args) {
        processElement(instance, rawJson);
        return instance;
    }

    private String doReplacement(final String match, final Map<String, String> replacers) {
        String result = match;
        if (!StringUtils.isNullOrEmpty(match) && !replacers.isEmpty()) {
            for (Map.Entry<String, String> entry : replacers.entrySet()) {
                result = result.replace(entry.getKey(), entry.getValue());
            }
        }
        return result;
    }

    private String processReplacement(final String original) {
        String result = original;
        if (placeholderMode) {
            final List<String> expressions = StringUtils.getMatches(EXPR_PATTERN, original);
            if (!expressions.isEmpty()) {
                for (String match : expressions) {
                    result = result.replace(match,
                            doReplacement(match, replacers)
                    );
                }
            }
        } else {
            result = doReplacement(result, replacers);
        }
        return result;
    }

    // Cloned from Config#handleVerification
    private JsonElement processElement(final Config instance, final JsonElement rawJson, final String... path) {
        // Verify Type Safety, reset value if anything is null or invalid for it's type
        String pathPrefix = String.join(".", path);
        if (!StringUtils.isNullOrEmpty(pathPrefix)) {
            pathPrefix += ".";
        }

        if (rawJson != null) {
            for (Map.Entry<String, JsonElement> entry : rawJson.getAsJsonObject().entrySet()) {
                final String rawName = pathPrefix + entry.getKey();
                final List<String> configPath = StringUtils.newArrayList(path);
                configPath.add(entry.getKey());
                final String[] pathData = configPath.toArray(new String[0]);

                Object defaultValue = instance.getDefaults().findProperty(pathData);
                Object currentValue = instance.findProperty(pathData);
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
                        final List<String> paths = StringUtils.newArrayList(path);
                        paths.add(entry.getKey());
                        processElement(instance, entry.getValue(), paths.toArray(new String[0]));
                    } else if (currentValue instanceof String originalResult) {
                        final String processResult = processReplacement(originalResult);
                        if (!processResult.equals(originalResult)) {
                            Constants.LOG.debugInfo("Modified property \"%s\": \"%s\" => \"%s\"", rawName, originalResult, processResult);
                            instance.setProperty((Object) processResult, pathData);
                        }
                    } else if (currentValue instanceof Map<?, ?> map) {
                        final Map<Object, Object> newData = StringUtils.newHashMap(map);
                        if (entry.getValue().isJsonObject()) {
                            for (Object dataEntry : newData.keySet()) {
                                final List<String> paths = StringUtils.newArrayList(path);
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
