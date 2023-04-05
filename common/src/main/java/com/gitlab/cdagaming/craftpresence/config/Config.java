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

package com.gitlab.cdagaming.craftpresence.config;

import com.gitlab.cdagaming.craftpresence.ModUtils;
import com.gitlab.cdagaming.craftpresence.config.category.*;
import com.gitlab.cdagaming.craftpresence.config.element.Button;
import com.gitlab.cdagaming.craftpresence.config.element.ModuleData;
import com.gitlab.cdagaming.craftpresence.config.element.PresenceData;
import com.gitlab.cdagaming.craftpresence.config.migration.HypherConverter;
import com.gitlab.cdagaming.craftpresence.config.migration.Legacy2Modern;
import com.gitlab.cdagaming.craftpresence.config.migration.TextReplacer;
import com.gitlab.cdagaming.craftpresence.impl.HashMapBuilder;
import com.gitlab.cdagaming.craftpresence.impl.KeyConverter;
import com.gitlab.cdagaming.craftpresence.impl.Pair;
import com.gitlab.cdagaming.craftpresence.impl.Tuple;
import com.gitlab.cdagaming.craftpresence.utils.*;
import com.google.gson.JsonElement;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.Serializable;
import java.nio.file.NoSuchFileException;
import java.util.Arrays;
import java.util.List;
import java.util.Map;

public final class Config extends Module implements Serializable {
    // Constants
    public static final int VERSION = 3;
    private static final long serialVersionUID = -4853238501768086595L;
    private static int MC_VERSION;
    private static List<String> keyCodeTriggers;
    private static List<String> languageTriggers;
    private static final Config INSTANCE = loadOrCreate();
    private static Config DEFAULT;
    public transient boolean hasChanged = false, needsReboot = false, isNewFile = false;
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
        return copy(INSTANCE, Config.class);
    }

    public static String getConfigPath() {
        return ModUtils.configDir + File.separator + ModUtils.MOD_ID + ".json";
    }

    public static File getConfigFile() {
        return new File(getConfigPath());
    }

    public static Pair<Config, JsonElement> read() {
        Config config = null;
        JsonElement rawJson = null;

        try {
            config = FileUtils.getJsonData(getConfigFile(), Config.class,
                    FileUtils.Modifiers.DISABLE_ESCAPES, FileUtils.Modifiers.PRETTY_PRINT);
            rawJson = FileUtils.getJsonData(getConfigFile());
        } catch (Exception ex) {
            if (ex.getClass() != FileNotFoundException.class && ex.getClass() != NoSuchFileException.class) {
                ModUtils.LOG.error(ModUtils.TRANSLATOR.translate(true, "craftpresence.logger.error.config.save"));
                if (CommandUtils.isVerboseMode()) {
                    ex.printStackTrace();
                }

                if (!getConfigFile().renameTo(new File(getConfigPath() + ".bak"))) {
                    ModUtils.LOG.error(ModUtils.TRANSLATOR.translate(true, "craftpresence.logger.error.config.backup"));
                }
            }
        }
        return new Pair<>(config, rawJson);
    }

    public static Config loadOrCreate(final boolean forceCreate) {
        setupCriticalData();

        final Pair<Config, JsonElement> data = read();
        Config config = data.getFirst();
        JsonElement rawJson = data.getSecond();

        final boolean hasNoData = config == null;
        final boolean isInvalidData = !hasNoData && (forceCreate || (config._schemaVersion <= 0 || config._lastMCVersionId <= 0));
        if (hasNoData || isInvalidData) {
            config = new Config();
            config.isNewFile = true;
            config.hasChanged = config.needsReboot = isInvalidData;
            config._schemaVersion = VERSION;
            config._lastMCVersionId = MC_VERSION;
        }

        final boolean wasNewFile = config.isNewFile;
        config.handleSync(rawJson);
        if (!forceCreate) {
            config.save();
        }
        if (wasNewFile) {
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
        keyCodeTriggers = StringUtils.newArrayList("keycode", "keybinding");
        languageTriggers = StringUtils.newArrayList("language", "lang", "langId", "languageId");
    }

    public static Object getProperty(final Config instance, final String... path) {
        if (instance == null) {
            return null;
        }
        return instance.getProperty(path);
    }

    public static Object getProperty(final Module instance, final String name) {
        if (instance == null) {
            return null;
        }
        return instance.getProperty(name);
    }

    public static boolean isValidProperty(final Config instance, final String... path) {
        final Object property = getProperty(instance, path);
        return property != null && !StringUtils.isNullOrEmpty(property.toString());
    }

    public static boolean isValidProperty(final Config instance, final String name) {
        return isValidProperty(instance, name.split("\\."));
    }

    public static boolean isValidProperty(final Module instance, final String name) {
        final Object property = getProperty(instance, name);
        return property != null && !StringUtils.isNullOrEmpty(property.toString());
    }

    @Override
    public Config getDefaults() {
        if (DEFAULT == null) {
            DEFAULT = new Config();
        }
        return copy(DEFAULT, Config.class);
    }

    public void applySettings() {
        if (hasChanged) {
            if (needsReboot) {
                CommandUtils.rebootRPC();
                needsReboot = false;
            }
            CommandUtils.reloadData(true);
            hasChanged = false;
        }
        isNewFile = false;
    }

    public JsonElement handleMigrations(JsonElement rawJson, final int oldVer, final int newVer) {
        if (isNewFile) {
            final File legacyFile = new File(ModUtils.configDir + File.separator + ModUtils.MOD_ID + ".properties");
            if (legacyFile.exists()) {
                new Legacy2Modern(legacyFile, "UTF-8").apply(this, rawJson);
            } else {
                // fileVersion, configDirectories[main,server-entries]
                final Map<Integer, String> hypherionFiles = new HashMapBuilder<Integer, String>()
                        .put(0, ModUtils.configDir + File.separator)
                        .put(31, SystemUtils.USER_DIR + File.separator + "simple-rpc" + File.separator)
                        .put(32, ModUtils.configDir + File.separator + "simple-rpc" + File.separator)
                        .build();
                for (Map.Entry<Integer, String> entry : hypherionFiles.entrySet()) {
                    final File hypherionFile = new File(entry.getValue() + "simple-rpc.toml");
                    if (hypherionFile.exists()) {
                        new HypherConverter(entry).apply(this, rawJson);
                        break;
                    }
                }
            }
        }

        // Config Layers for prior existing files (Or recently made ones)
        if (!isNewFile) {
            int currentVer = oldVer;
            if (currentVer < newVer) {
                if (CommandUtils.isVerboseMode()) {
                    ModUtils.LOG.info(ModUtils.TRANSLATOR.translate(true, "craftpresence.logger.info.config.outdated", currentVer, newVer));
                }

                if (MathUtils.isWithinValue(currentVer, 1, 2, true, false)) {
                    // Schema Changes (v1 -> v2)
                    //  - Property: `generalSettings.showTime` -> `displaySettings.presenceData.startTimestamp`
                    final boolean showTime = rawJson.getAsJsonObject()
                            .getAsJsonObject("generalSettings")
                            .getAsJsonPrimitive("showTime").getAsBoolean();
                    displaySettings.presenceData.startTimestamp = showTime ? "{data.general.time}" : "";
                    currentVer = 2;
                }
                if (MathUtils.isWithinValue(currentVer, 2, 3, true, false)) {
                    // Schema Changes (v2 -> v3)
                    //  - Placeholder: `world.time24` -> `world.time.format_24`
                    //  - Placeholder: `world.time12` -> `world.time.format_12`
                    //  - Placeholder: `world.day` -> `world.time.day`
                    new TextReplacer(
                            new HashMapBuilder<String, String>()
                                    .put("world.time24", "world.time.format_24")
                                    .put("world.time12", "world.time.format_12")
                                    .put("world.day", "world.time.day")
                                    .build(),
                            true,
                            true, false, true
                    ).apply(this, rawJson);
                    currentVer = 3;
                }

                save();
            }

            // Refresh the raw json contents, in case of any changes
            rawJson = read().getSecond();
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
            final Object parentValue = getProperty(path);
            for (Map.Entry<String, JsonElement> entry : rawJson.getAsJsonObject().entrySet()) {
                final String rawName = pathPrefix + entry.getKey();
                final List<String> configPath = StringUtils.newArrayList(path);
                configPath.add(entry.getKey());
                final String[] pathData = configPath.toArray(new String[0]);

                final JsonElement rawValue = entry.getValue();
                Object defaultValue = getDefaults().getProperty(pathData);
                Object currentValue = getProperty(pathData);
                boolean shouldReset = false, shouldContinue = true;

                if (defaultValue == null) {
                    if (currentValue == null || !(parentValue instanceof PresenceData || parentValue instanceof ModuleData || parentValue instanceof Button)) {
                        ModUtils.LOG.error(ModUtils.TRANSLATOR.translate(true, "craftpresence.logger.error.config.prop.invalid", rawName));
                        shouldContinue = false;
                    } else {
                        defaultValue = currentValue;
                    }
                }

                if (shouldContinue) {
                    if (Module.class.isAssignableFrom(defaultValue.getClass())) {
                        final List<String> paths = StringUtils.newArrayList(path);
                        paths.add(entry.getKey());
                        handleVerification(entry.getValue(), keyCodeMigrationId, languageMigrationId, paths.toArray(new String[0]));
                    } else if (!rawName.contains("presence")) { // Avoidance Filter
                        if (!StringUtils.isNullOrEmpty(defaultValue.toString()) && StringUtils.isNullOrEmpty(currentValue.toString())) {
                            shouldReset = true;
                        } else {
                            final Class<?> expectedClass = currentValue.getClass();
                            if (expectedClass == Boolean.class &&
                                    !StringUtils.isValidBoolean(rawValue.getAsString())) {
                                shouldReset = true;
                            } else if (expectedClass == Integer.class) {
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
                                                    setProperty(migratedKeyCode, pathData);
                                                }
                                            }
                                            break;
                                        }
                                    }
                                } else {
                                    shouldReset = true;
                                }
                            } else if (currentValue instanceof Map<?, ?>) {
                                final Map<Object, Object> newData = StringUtils.newHashMap((Map<?, ?>) currentValue);
                                final Map<Object, Object> defaultData = StringUtils.newHashMap((Map<?, ?>) defaultValue);
                                if (!newData.containsKey("default")) {
                                    ModUtils.LOG.error(ModUtils.TRANSLATOR.translate(true, "craftpresence.logger.error.config.missing.default", rawName));
                                    newData.putAll(defaultData);
                                    setProperty(newData, pathData);
                                } else if (entry.getValue().isJsonObject()) {
                                    for (Object dataEntry : newData.keySet()) {
                                        final List<String> paths = StringUtils.newArrayList(path);
                                        paths.add(entry.getKey());
                                        paths.add(dataEntry.toString());
                                        final JsonElement dataValue = entry.getValue().getAsJsonObject().get(dataEntry.toString());
                                        if (dataValue.isJsonObject()) {
                                            handleVerification(dataValue, keyCodeMigrationId, languageMigrationId, paths.toArray(new String[0]));
                                        }
                                    }
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
                                                setProperty((Object) migratedLanguageId, pathData);
                                            }
                                        }
                                        break;
                                    }
                                }
                            }
                        }

                        if (shouldReset) {
                            ModUtils.LOG.error(ModUtils.TRANSLATOR.translate(true, "craftpresence.logger.error.config.prop.empty", rawName));
                            resetProperty(pathData);
                        }
                    }
                }
            }
        }
        return rawJson;
    }

    public JsonElement handleSync(JsonElement rawJson) {
        if (isNewFile || _schemaVersion != VERSION) {
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
        // is a newer version then or exactly 17w43a (1.13, 341), then
        // we need to ensure any keycode assignments are in an LWJGL 3 format.
        // Otherwise, if our current protocol version is anything less then 17w43a (1.13, 341),
        // we need to ensure any keycode assignments are in an LWJGL 2 format.
        // If neither is true, then we mark the migration data as None, and it will be verified
        if (oldMCVer < 341 && MC_VERSION >= 341) {
            keyCodeMigrationId = KeyConverter.ConversionMode.Lwjgl3;
        } else if (oldMCVer >= 341 && MC_VERSION < 341) {
            keyCodeMigrationId = KeyConverter.ConversionMode.Lwjgl2;
        } else if (oldMCVer >= 0 && MC_VERSION >= 0) {
            keyCodeMigrationId = KeyConverter.ConversionMode.None;
        } else {
            keyCodeMigrationId = KeyConverter.ConversionMode.Unknown;
        }

        // Normal Case 1 Notes (Language ID):
        // In this situation, if the currently parsed protocol version differs and
        // is a newer version then or exactly 16w32a (1.11, 301), then
        // we need to ensure any Language Locale's are complying with Pack Format 3 and above.
        // Otherwise, if our current protocol version is anything less then 16w32a (1.11, 301),
        // we need to ensure any Language Locale's are complying with Pack Format 2 and below.
        // If neither is true, then we mark the migration data as None, and it will be verified
        if (oldMCVer < 301 && MC_VERSION >= 301) {
            languageMigrationId = TranslationUtils.ConversionMode.PackFormat3;
        } else if (oldMCVer >= 301 && MC_VERSION < 301) {
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

    public void save(final boolean shouldApply) {
        FileUtils.writeJsonData(this, getConfigFile(), "UTF-8",
                FileUtils.Modifiers.DISABLE_ESCAPES, FileUtils.Modifiers.PRETTY_PRINT);
        if (shouldApply) {
            applySettings();
        }
    }

    public void save() {
        save(true);
    }

    public Pair<Object, Tuple<Class<?>, Object, String>> lookupProperty(final String... path) {
        Class<?> classObj = Config.class;
        Object instance = this;
        Object result = null;

        String name = null;
        for (int i = 0; i < path.length; i++) {
            if (!StringUtils.isNullOrEmpty(path[i])) {
                name = path[i];
                if (instance instanceof Map<?, ?>) {
                    result = StringUtils.newHashMap((Map<?, ?>) instance).get(name);
                } else {
                    result = StringUtils.getField(classObj, instance, name);
                }
                if (result != null) {
                    if (i < path.length - 1) {
                        classObj = result.getClass();
                        instance = result;
                    }
                } else {
                    break;
                }
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
            if (fieldData.getSecond() instanceof Map<?, ?>) {
                final String[] parentPath = Arrays.copyOf(path, path.length - 1);
                final Tuple<Class<?>, Object, String> parentData = lookupProperty(parentPath).getSecond();

                Map<Object, Object> data = StringUtils.newHashMap((Map<?, ?>) fieldData.getSecond());
                data.put(fieldData.getThird(), value);

                StringUtils.updateField(parentData.getFirst(), parentData.getSecond(), data, parentData.getThird());
            } else {
                StringUtils.updateField(fieldData.getFirst(), fieldData.getSecond(), value, fieldData.getThird());
            }
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

    @Override
    public String toString() {
        return FileUtils.toJsonData(this);
    }

    @Override
    public boolean equals(Object obj) {
        if (obj == this) {
            return true;
        }

        if (!(obj instanceof Config)) {
            return false;
        }

        Config p = (Config) obj;
        return toString().equals(p.toString());
    }

    @Override
    public int hashCode() {
        return toString().hashCode();
    }
}
