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

package com.gitlab.cdagaming.craftpresence.config;

import com.gitlab.cdagaming.craftpresence.ModUtils;
import com.gitlab.cdagaming.craftpresence.config.category.*;
import com.gitlab.cdagaming.craftpresence.config.migration.Legacy2Modern;
import com.gitlab.cdagaming.craftpresence.impl.KeyConverter;
import com.gitlab.cdagaming.craftpresence.impl.Pair;
import com.gitlab.cdagaming.craftpresence.impl.Tuple;
import com.gitlab.cdagaming.craftpresence.utils.*;
import com.google.common.collect.Lists;
import com.google.gson.JsonElement;

import java.io.File;
import java.io.Serializable;
import java.lang.reflect.Field;
import java.lang.reflect.Modifier;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@SuppressWarnings({"ConstantConditions", "unchecked", "rawtypes"})
public final class Config implements Serializable {
    // Constants
    public static final int VERSION = 1;
    private static final long serialVersionUID = -4853238501768086595L;
    private static final List<Field> CATEGORIES = getCategoryList();
    public static int MC_VERSION;
    private static final Config INSTANCE = loadOrCreate();
    private static Config DEFAULT;
    public transient boolean hasChanged = false, hasClientPropertiesChanged = false, flushClientProperties = false, isNewFile = false;
    // Global Settings
    public String _README = "https://gitlab.com/CDAGaming/CraftPresence/-/wikis/home";
    public int _schemaVersion = 0;
    public int _lastMCVersionId = 0;
    // Other Settings
    public General generalSettings = new General();
    public Biome biomeSettings = new Biome();
    public Dimension dimensionSettings = new Dimension();
    public Server serverSettings = new Server();
    public Status statusMessages = new Status();
    public Advanced advancedSettings = new Advanced();
    public Accessibility accessibilitySettings = Accessibility.getDefaults();
    public Display displaySettings = new Display();

    public static Config getDefaults() {
        if (DEFAULT == null) {
            DEFAULT = new Config();
        }
        return DEFAULT;
    }

    public static Config getInstance() {
        return INSTANCE;
    }

    public static File getConfigFile() {
        return new File(ModUtils.configDir + File.separator + ModUtils.MOD_ID + ".json");
    }

    public static Config loadOrCreate(final boolean forceCreate) {
        Config config = null;
        JsonElement rawJson = null;

        // Ensure critical data is setup
        MC_VERSION = Integer.parseInt("@MC_PROTOCOL@");

        try {
            config = FileUtils.getJsonData(getConfigFile(), Config.class,
                    FileUtils.Modifiers.DISABLE_ESCAPES, FileUtils.Modifiers.PRETTY_PRINT);
            rawJson = FileUtils.getJsonData(getConfigFile(), JsonElement.class);
            boolean isNew = (config._schemaVersion <= 0 || config._lastMCVersionId <= 0);
            if (forceCreate || isNew) {
                config = new Config();
                config.isNewFile = isNew;
            }
        } catch (Exception ex) {
            ModUtils.LOG.error(ModUtils.TRANSLATOR.translate(true, "craftpresence.logger.error.config.save"));
            ex.printStackTrace();
        }

        if (config == null) {
            config = new Config();
            config.isNewFile = true;
        }
        config.handleSync(rawJson);
        config.save();
        if (config.isNewFile) {
            ModUtils.LOG.info(ModUtils.TRANSLATOR.translate(true, "craftpresence.logger.info.config.new"));
        } else {
            ModUtils.LOG.info(ModUtils.TRANSLATOR.translate(true, "craftpresence.logger.info.config.save"));
        }
        return config;
    }

    public static Config loadOrCreate() {
        return loadOrCreate(false);
    }

    public static List<Field> getCategoryList() {
        final List<Field> results = Lists.newArrayList();
        for (Field f : Config.class.getDeclaredFields()) {
            if (Modifier.isPublic(f.getModifiers()) && Module.class.isAssignableFrom(f.getType())) {
                results.add(f);
            }
        }
        return results;
    }

    public void applyData() {
        if (hasChanged) {
            if (hasClientPropertiesChanged) {
                CommandUtils.rebootRPC(flushClientProperties);
                hasClientPropertiesChanged = false;
            }
            CommandUtils.reloadData(true);
            flushClientProperties = false;
            hasChanged = false;
        }
        isNewFile = false;
    }

    public JsonElement handleMigrations(JsonElement rawJson, int oldVer, final int newVer) {
        if (isNewFile) {
            final File legacyFile = new File(ModUtils.configDir + File.separator + ModUtils.MOD_ID + ".properties");
            if (legacyFile.exists()) {
                new Legacy2Modern(legacyFile, "UTF-8").apply(this, rawJson);
                try {
                    rawJson = FileUtils.getJsonData(getConfigFile(), JsonElement.class);
                } catch (Exception ex) {
                    ModUtils.LOG.error(ModUtils.TRANSLATOR.translate(true, "craftpresence.logger.error.config.save"));
                    ex.printStackTrace();
                }
            }
        }
        return rawJson;
    }

    public JsonElement handleVerification(JsonElement rawJson, final int currentProtocol, final int defaultProtocol) {
        // Sync Migration Data for later usage
        final List<String> keyCodeTriggers = Lists.newArrayList("keycode", "keybinding");
        final List<String> languageTriggers = Lists.newArrayList("language", "lang", "langId", "languageId");
        final KeyConverter.ConversionMode keyCodeMigrationId;
        final TranslationUtils.ConversionMode languageMigrationId;

        // Global Case 1 Notes (KeyCode):
        // In this situation, if the currently parsed protocol version differs and
        // is a newer version then 1.12.2 (340), then
        // we need to ensure any keycode assignments are in an LWJGL 3 format
        // Otherwise, if using a config from above 1.12.2 (340) on it or anything lower,
        // we need to ensure any keycode assignments are in an LWJGL 2 format.
        // If neither is true, then we mark the migration data as None, and it will be verified
        if (currentProtocol <= 340 && defaultProtocol > 340) {
            keyCodeMigrationId = KeyConverter.ConversionMode.Lwjgl3;
        } else if (currentProtocol > 340 && defaultProtocol <= 340) {
            keyCodeMigrationId = KeyConverter.ConversionMode.Lwjgl2;
        } else if (currentProtocol >= 0 && defaultProtocol >= 0) {
            keyCodeMigrationId = KeyConverter.ConversionMode.None;
        } else {
            keyCodeMigrationId = KeyConverter.ConversionMode.Unknown;
        }

        // Normal Case 1 Notes (Language ID):
        // In this situation, if the currently parsed protocol version differs and
        // is a newer version then or exactly 1.11 (315), then
        // we need to ensure any Language Locale's are complying with Pack Format 3 and above
        // Otherwise, if using a config from anything less then 1.11 (315),
        // we need to ensure any Language Locale's are complying with Pack Format 2 and below
        // If neither is true, then we mark the migration data as None, and it will be verified
        if (currentProtocol < 315 && defaultProtocol >= 315) {
            languageMigrationId = TranslationUtils.ConversionMode.PackFormat3;
        } else if (currentProtocol >= 315 && defaultProtocol < 315) {
            languageMigrationId = TranslationUtils.ConversionMode.PackFormat2;
        } else if (currentProtocol >= 0 && defaultProtocol >= 0) {
            languageMigrationId = TranslationUtils.ConversionMode.None;
        } else {
            languageMigrationId = TranslationUtils.ConversionMode.Unknown;
        }

        ModUtils.LOG.debugInfo(ModUtils.TRANSLATOR.translate(true, "craftpresence.logger.info.migration.add", keyCodeTriggers.toString(), keyCodeMigrationId, keyCodeMigrationId.equals(KeyConverter.ConversionMode.None) ? "Verification" : "Setting Change"));
        ModUtils.LOG.debugInfo(ModUtils.TRANSLATOR.translate(true, "craftpresence.logger.info.migration.add", languageTriggers.toString(), languageMigrationId, languageMigrationId.equals(TranslationUtils.ConversionMode.None) ? "Verification" : "Setting Change"));

        // Verify Type Safety, reset value if anything is null or invalid for it's type
        if (rawJson != null) {
            final List<String> propsToReset = Lists.newArrayList();
            for (Map.Entry<String, JsonElement> entry : rawJson.getAsJsonObject().entrySet()) {
                final String rawName = entry.getKey();
                final JsonElement rawValue = entry.getValue();
                final Object defaultValue = getDefaults().getProperty(rawName);
                boolean shouldReset = false;

                if (defaultValue == null) {
                    ModUtils.LOG.error(ModUtils.TRANSLATOR.translate(true, "craftpresence.logger.error.config.prop.invalid", rawName));
                } else if (!rawName.endsWith("Format")) { // Avoidance Filter
                    final Object currentValue = getProperty(rawName);
                    if (!StringUtils.isNullOrEmpty(defaultValue.toString()) && StringUtils.isNullOrEmpty(currentValue.toString())) {
                        shouldReset = true;
                    } else {
                        final Class<?> expectedClass = currentValue.getClass();
                        if ((expectedClass == boolean.class || expectedClass == Boolean.class) &&
                                !StringUtils.isValidBoolean(rawValue.getAsString())) {
                            shouldReset = true;
                        } else if ((expectedClass == int.class || expectedClass == Integer.class)) {
                            final Pair<Boolean, Integer> boolData = StringUtils.getValidInteger(rawValue.getAsString());
                            if (boolData.getFirst()) {
                                // This check will trigger if the Field Name contains KeyCode Triggers
                                // If the Property Name contains these values, move onwards
                                for (String keyTrigger : keyCodeTriggers) {
                                    if (rawName.toLowerCase().contains(keyTrigger.toLowerCase())) {
                                        if (!KeyUtils.isValidKeyCode(boolData.getSecond())) {
                                            shouldReset = true;
                                        } else if (keyCodeMigrationId != KeyConverter.ConversionMode.Unknown) {
                                            final int migratedKeyCode = KeyConverter.convertKey(boolData.getSecond(), keyCodeMigrationId);
                                            if (migratedKeyCode != boolData.getSecond()) {
                                                ModUtils.LOG.info(ModUtils.TRANSLATOR.translate(true, "craftpresence.logger.info.migration.apply", "KEYCODE", keyCodeMigrationId.name(), rawName, boolData.getSecond(), migratedKeyCode));
                                                setProperty(rawName, migratedKeyCode);
                                            }
                                        }
                                        break;
                                    }
                                }
                            } else {
                                shouldReset = true;
                            }
                        } else if (currentValue instanceof Map) {
                            final Map newData = new HashMap((Map) currentValue);
                            final Map defaultData = new HashMap((Map) defaultValue);
                            if (!newData.containsKey("default")) {
                                ModUtils.LOG.error(ModUtils.TRANSLATOR.translate(true, "craftpresence.logger.error.config.missing.default", rawName));
                                newData.putAll(defaultData);
                                setProperty(rawName, newData);
                            }
                        } else if (rawValue.isJsonPrimitive()) {
                            final String rawStringValue = rawValue.getAsString();
                            // This check will trigger if the Field Name contains Language Identifier Triggers
                            // If the Property Name contains these values, move onwards
                            for (String langTrigger : languageTriggers) {
                                if (rawName.toLowerCase().contains(langTrigger.toLowerCase())) {
                                    if (languageMigrationId != TranslationUtils.ConversionMode.Unknown) {
                                        final String migratedLanguageId = TranslationUtils.convertId(rawStringValue, languageMigrationId);
                                        if (!migratedLanguageId.equals(rawStringValue)) {
                                            ModUtils.LOG.info(ModUtils.TRANSLATOR.translate(true, "craftpresence.logger.info.migration.apply", "LANGUAGE", languageMigrationId.name(), rawName, rawStringValue, migratedLanguageId));
                                            setProperty(rawName, migratedLanguageId);
                                        }
                                    }
                                    break;
                                }
                            }
                        }
                    }

                    if (shouldReset) {
                        ModUtils.LOG.error(ModUtils.TRANSLATOR.translate(true, "craftpresence.logger.error.config.prop.empty", rawName));
                        propsToReset.add(rawName);
                    }
                }
            }

            for (String propertyName : propsToReset) {
                resetProperty(propertyName);
            }
        }
        return rawJson;
    }

    public JsonElement handleSync(JsonElement rawJson) {
        if (_schemaVersion != VERSION) {
            int oldVer = _schemaVersion;
            //rawJson = handleMigrations(rawJson, oldVer, VERSION);
            _schemaVersion = VERSION;
        }
        int oldMCVer = _lastMCVersionId;
        if (_lastMCVersionId != MC_VERSION) {
            _lastMCVersionId = MC_VERSION;
        }
        return /*!isNewFile ? handleVerification(rawJson, oldMCVer, MC_VERSION) : */rawJson;
    }

    public void save() {
        // Ensure Critical Data is setup
        applyData();
        FileUtils.writeJsonData(this, getConfigFile(), "UTF-8",
                FileUtils.Modifiers.DISABLE_ESCAPES, FileUtils.Modifiers.PRETTY_PRINT);
    }

    public Object getProperty(final String name, final boolean ignoreCategories) {
        if (!ignoreCategories) {
            return StringUtils.lookupInnerObject(CATEGORIES, this, name);
        } else {
            return StringUtils.lookupObject(Config.class, this, name);
        }
    }

    public Object getProperty(final String name) {
        return getProperty(name, false);
    }

    public void setProperty(final String name, final Object value, final boolean ignoreCategories) {
        if (!ignoreCategories) {
            StringUtils.updateInnerObject(CATEGORIES, this, new Tuple<>(name, value, null));
        } else {
            StringUtils.updateField(Config.class, this, new Tuple<>(name, value, null));
        }
    }

    public void setProperty(final String name, final Object value) {
        setProperty(name, value, false);
    }

    public void resetProperty(final String name, final boolean ignoreCategories) {
        setProperty(name, getDefaults().getProperty(name), ignoreCategories);
    }

    public void resetProperty(final String name) {
        resetProperty(name, false);
    }
}
