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

package com.gitlab.cdagaming.craftpresence.impl;

import com.gitlab.cdagaming.craftpresence.CraftPresence;
import io.github.cdagaming.unicore.utils.FileUtils;
import io.github.cdagaming.unicore.utils.StringUtils;
import io.github.cdagaming.unicore.utils.TranslationUtils;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import net.minecraft.core.lang.I18n;
import net.minecraft.core.lang.Language;
import net.minecraft.core.lang.LanguageSeeker;

import java.io.File;
import java.io.InputStream;
import java.util.Enumeration;
import java.util.List;
import java.util.Properties;
import java.util.zip.ZipEntry;
import java.util.zip.ZipFile;

/**
 * Utilities for Hooking a {@link TranslationUtils} instance to the Game Resource Manager
 *
 * @param instance The currently linked {@link TranslationUtils} instance
 * @author CDAGaming
 */
public record TranslationManager(TranslationUtils instance) {
    /**
     * Initializes a new manager for the {@link TranslationUtils} instance
     *
     * @param instance the {@link TranslationUtils} instance to attach to
     */
    public TranslationManager(final TranslationUtils instance) {
        this.instance = instance;

        instance().setLanguageSupplier((fallback) -> {
            final String result;
            if (CraftPresence.CONFIG != null) {
                result = CraftPresence.CONFIG.accessibilitySettings.languageId;
            } else {
                result = fallback;
            }
            return result;
        });

        instance().setOnLanguageSync((entries) -> {
            final Language language = I18n.getInstance().getCurrentLanguage();
            Properties data = (Properties) StringUtils.getField(Language.class, language, "entries");
            data.putAll(entries);
            StringUtils.updateField(Language.class, language, data, "entries");
        });

        // Fix: Add support for BTAs Translation style
        instance().setResourceSupplier((modId, assetsPath, langPath) -> {
            final List<InputStream> results = StringUtils.newArrayList();
            final String fullPath = assetsPath + langPath;
            final String fileExt = FileUtils.getFileExtension(fullPath);
            final String id = FileUtils.getFileNameWithoutExtension(
                    fullPath.substring(fullPath.lastIndexOf("/") + 1)
            );

            String filePath = FileUtils.getFileNameWithoutExtension(fullPath);
            // Halplibe Support - Replace "/lang" with "/lang/[modId]"
            // if not raw translations and !usingAssetsPath
            if (!modId.equals("minecraft") && assetsPath.equals("/")) {
                filePath = filePath.replace("/lang", "/lang/" + modId);
            }

            // Case 1: Internal Folders (Ex: /lang/en_US/xx_XX.[lang|json])
            // CODE COPY: Language#Default<init>
            for (String path : FileUtils.filesInDir(TranslationUtils.class, filePath)) {
                if (path.endsWith(fileExt)) {
                    try {
                        final InputStream stream = FileUtils.getResourceAsStream(
                                TranslationUtils.class, path
                        );
                        if (stream != null) {
                            results.add(stream);
                        }
                    } catch (Exception ignored) {
                    }
                }
            }

            // Case 2: LanguageSeeker#LANGUAGE_DIR (zip archives, only supports .lang)
            if (fileExt.equals(".lang")) {
                final File LANGUAGE_DIR = LanguageSeeker.LANGUAGE_DIR;
                if (LANGUAGE_DIR.exists() && LANGUAGE_DIR.isDirectory()) {
                    final File[] files = LANGUAGE_DIR.listFiles();
                    if (files != null) {
                        for (File file : files) {
                            try {
                                if (file.getName().endsWith(".zip")) {
                                    final ZipFile zip = new ZipFile(file);
                                    final InputStream stream = zip.getInputStream(zip.getEntry("lang_info.json"));

                                    final String content = FileUtils.fileToString(stream, "UTF-8");
                                    final JsonObject json = FileUtils.parseJson(content).getAsJsonObject();
                                    final String langId = json.getAsJsonPrimitive("id").getAsString();
                                    final String langName = json.getAsJsonPrimitive("name").getAsString();
                                    final List<String> langCredits = StringUtils.newArrayList();

                                    for (JsonElement element : json.getAsJsonArray("credits")) {
                                        String string = element.getAsString();
                                        if (string != null) {
                                            langCredits.add(string);
                                        }
                                    }

                                    if (langId != null && langName != null && !langCredits.isEmpty() && langId.equals(id)) {
                                        final Enumeration<? extends ZipEntry> zipEntries = zip.entries();
                                        while (zipEntries.hasMoreElements()) {
                                            ZipEntry entry = zipEntries.nextElement();
                                            if (entry.getName().endsWith(fileExt)) {
                                                InputStream langStream = zip.getInputStream(entry);
                                                if (langStream != null) {
                                                    // Attempt to workaround ZipStream being lost after closure
                                                    final String langContent = FileUtils.fileToString(langStream, "UTF-8");
                                                    langStream = FileUtils.stringToStream(langContent, "UTF-8");
                                                    results.add(langStream);
                                                }
                                            }
                                        }
                                    }
                                    zip.close();
                                }
                            } catch (Exception ignored) {
                            }
                        }
                    }
                }
            }
            return results;
        });
    }

    /**
     * The Event to Run on each Client Tick, if passed initialization events
     * <p>
     * Consists of Synchronizing Data, and Updating Translation Data as needed
     */
    public void onTick() {
        instance().onTick();
    }
}
