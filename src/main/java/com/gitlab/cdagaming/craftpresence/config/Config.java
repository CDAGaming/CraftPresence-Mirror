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
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@SuppressWarnings({"ConstantConditions", "unchecked", "rawtypes"})
public final class Config extends Module implements Serializable {
    // Constants
    public static final int VERSION = 1;
    private static final long serialVersionUID = -4853238501768086595L;
    public static int MC_VERSION;
    private static List<String> keyCodeTriggers;
    private static List<String> languageTriggers;
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
    public Accessibility accessibilitySettings = new Accessibility();
    public Display displaySettings = new Display();

    public static Config getInstance() {
        return INSTANCE;
    }

    public static File getConfigFile() {
        return new File(ModUtils.configDir + File.separator + ModUtils.MOD_ID + ".json");
    }

    public static Config loadOrCreate(final boolean forceCreate) {
        Config config = null;
        JsonElement rawJson = null;
        setupCriticalData();

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

    public static void setupCriticalData() {
        // Setup other critical data
        MC_VERSION = Integer.parseInt("@MC_PROTOCOL@");
        keyCodeTriggers = Lists.newArrayList("keycode", "keybinding");
        languageTriggers = Lists.newArrayList("language", "lang", "langId", "languageId");
    }

    public static boolean isValidProperty(final Config instance, final String... path) {
        if (instance == null) {
            return false;
        }
        final Object property = instance.getProperty(path);
        return property != null && !StringUtils.isNullOrEmpty(property.toString());
    }

    public static boolean isValidProperty(final Config instance, final String name) {
        return isValidProperty(instance, name.split("\\."));
    }

    public static boolean isValidProperty(final Module instance, final String name) {
        if (instance == null) {
            return false;
        }
        final Object property = instance.getProperty(name);
        return property != null && !StringUtils.isNullOrEmpty(property.toString());
    }

    @Override
    public Config getDefaults() {
        if (DEFAULT == null) {
            DEFAULT = new Config();
        }
        return DEFAULT;
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

    public JsonElement handleVerification(final JsonElement rawJson, final KeyConverter.ConversionMode keyCodeMigrationId, final TranslationUtils.ConversionMode languageMigrationId, final String... path) {
        // Verify Type Safety, reset value if anything is null or invalid for it's type
        String pathPrefix = StringUtils.join(".", Arrays.asList(path));
        if (!StringUtils.isNullOrEmpty(pathPrefix)) {
            pathPrefix += ".";
        }

        if (rawJson != null) {
            for (Map.Entry<String, JsonElement> entry : rawJson.getAsJsonObject().entrySet()) {
                final String rawName = pathPrefix + entry.getKey();
                final JsonElement rawValue = entry.getValue();
                final Object defaultValue = getDefaults().getProperty(rawName);
                boolean shouldReset = false;

                if (defaultValue == null) {
                    ModUtils.LOG.error(ModUtils.TRANSLATOR.translate(true, "craftpresence.logger.error.config.prop.invalid", rawName));
                } else if (Module.class.isAssignableFrom(defaultValue.getClass())) {
                    final List<String> paths = Lists.newArrayList(path);
                    paths.add(entry.getKey());
                    handleVerification(entry.getValue(), keyCodeMigrationId, languageMigrationId, paths.toArray(new String[0]));
                } else if (!rawName.contains("presence")) { // Avoidance Filter
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
                        resetProperty(rawName);
                    }
                }
            }
        }
        return rawJson;
    }

    public JsonElement handleSync(JsonElement rawJson) {
        if (_schemaVersion != VERSION) {
            int oldVer = _schemaVersion;
            rawJson = handleMigrations(rawJson, oldVer, VERSION);
            _schemaVersion = VERSION;
        }
        int oldMCVer = _lastMCVersionId;
        if (_lastMCVersionId != MC_VERSION) {
            _lastMCVersionId = MC_VERSION;
        }

        // Sync Migration Data for later usage
        final KeyConverter.ConversionMode keyCodeMigrationId;
        final TranslationUtils.ConversionMode languageMigrationId;

        // Global Case 1 Notes (KeyCode):
        // In this situation, if the currently parsed protocol version differs and
        // is a newer version then 1.12.2 (340), then
        // we need to ensure any keycode assignments are in an LWJGL 3 format
        // Otherwise, if using a config from above 1.12.2 (340) on it or anything lower,
        // we need to ensure any keycode assignments are in an LWJGL 2 format.
        // If neither is true, then we mark the migration data as None, and it will be verified
        if (oldMCVer <= 340 && MC_VERSION > 340) {
            keyCodeMigrationId = KeyConverter.ConversionMode.Lwjgl3;
        } else if (oldMCVer > 340 && MC_VERSION <= 340) {
            keyCodeMigrationId = KeyConverter.ConversionMode.Lwjgl2;
        } else if (oldMCVer >= 0 && MC_VERSION >= 0) {
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
        if (oldMCVer < 315 && MC_VERSION >= 315) {
            languageMigrationId = TranslationUtils.ConversionMode.PackFormat3;
        } else if (oldMCVer >= 315 && MC_VERSION < 315) {
            languageMigrationId = TranslationUtils.ConversionMode.PackFormat2;
        } else if (oldMCVer >= 0 && MC_VERSION >= 0) {
            languageMigrationId = TranslationUtils.ConversionMode.None;
        } else {
            languageMigrationId = TranslationUtils.ConversionMode.Unknown;
        }

        ModUtils.LOG.debugInfo(ModUtils.TRANSLATOR.translate(true, "craftpresence.logger.info.migration.add", keyCodeTriggers.toString(), keyCodeMigrationId, keyCodeMigrationId.equals(KeyConverter.ConversionMode.None) ? "Verification" : "Setting Change"));
        ModUtils.LOG.debugInfo(ModUtils.TRANSLATOR.translate(true, "craftpresence.logger.info.migration.add", languageTriggers.toString(), languageMigrationId, languageMigrationId.equals(TranslationUtils.ConversionMode.None) ? "Verification" : "Setting Change"));
        return !isNewFile ? handleVerification(rawJson, keyCodeMigrationId, languageMigrationId) : rawJson;
    }

    public void save() {
        // Ensure Critical Data is setup
        applyData();
        FileUtils.writeJsonData(this, getConfigFile(), "UTF-8",
                FileUtils.Modifiers.DISABLE_ESCAPES, FileUtils.Modifiers.PRETTY_PRINT);
    }

    public Pair<Object, Tuple<Class<?>, Object, String>> lookupProperty(final String... path) {
        Class<?> classObj = Config.class;
        Object instance = this;
        Object result = null;

        String name = null;
        for (int i = 0; i < path.length; i++) {
            name = path[i];
            result = StringUtils.lookupObject(classObj, instance, name);
            if (result != null) {
                if (i < path.length - 1) {
                    classObj = result.getClass();
                    instance = result;
                }
            } else {
                break;
            }
        }
        return new Pair<>(result, new Tuple<>(classObj, instance, name));
    }

    public Object getProperty(final String... path) {
        return lookupProperty(path).getFirst();
    }

    @Override
    public Object getProperty(final String name) {
        return getProperty(name.split("\\."));
    }

    public void setProperty(final Object value, final String... path) {
        final Pair<Object, Tuple<Class<?>, Object, String>> propertyData = lookupProperty(path);
        if (propertyData.getFirst() != null) {
            final Tuple<Class<?>, Object, String> fieldData = propertyData.getSecond();
            StringUtils.updateField(fieldData.getFirst(), fieldData.getSecond(), new Tuple<>(fieldData.getThird(), value, null));
        }
    }

    @Override
    public void setProperty(final String name, final Object value) {
        setProperty(value, name.split("\\."));
    }

    public void resetProperty(final String... path) {
        setProperty(getDefaults().getProperty(path), path);
    }

    @Override
    public void resetProperty(final String name) {
        resetProperty(name.split("\\."));
    }
}
