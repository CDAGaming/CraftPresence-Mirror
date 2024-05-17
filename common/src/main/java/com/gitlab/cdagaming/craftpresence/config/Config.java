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

package com.gitlab.cdagaming.craftpresence.config;

import com.gitlab.cdagaming.craftpresence.ModUtils;
import com.gitlab.cdagaming.craftpresence.config.category.*;
import com.gitlab.cdagaming.craftpresence.config.migration.HypherConverter;
import com.gitlab.cdagaming.craftpresence.config.migration.Legacy2Modern;
import com.gitlab.cdagaming.craftpresence.config.migration.TextReplacer;
import com.gitlab.cdagaming.craftpresence.core.Constants;
import com.gitlab.cdagaming.craftpresence.core.config.Module;
import com.gitlab.cdagaming.craftpresence.core.config.element.*;
import com.gitlab.cdagaming.craftpresence.core.impl.KeyConverter;
import com.gitlab.cdagaming.craftpresence.core.impl.TranslationConverter;
import com.gitlab.cdagaming.craftpresence.utils.CommandUtils;
import com.gitlab.cdagaming.craftpresence.utils.KeyUtils;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import io.github.cdagaming.unicore.impl.HashMapBuilder;
import io.github.cdagaming.unicore.impl.Pair;
import io.github.cdagaming.unicore.impl.Tuple;
import io.github.cdagaming.unicore.utils.FileUtils;
import io.github.cdagaming.unicore.utils.MathUtils;
import io.github.cdagaming.unicore.utils.OSUtils;
import io.github.cdagaming.unicore.utils.StringUtils;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.Serializable;
import java.nio.file.NoSuchFileException;
import java.util.Arrays;
import java.util.List;
import java.util.Map;
import java.util.Objects;

public final class Config extends Module implements Serializable {
    // Constants
    private static final long serialVersionUID = -4853238501768086595L;
    private static final int MC_VERSION = ModUtils.MCProtocolID;
    private static final int VERSION = 6;
    private static final List<String> keyCodeTriggers = StringUtils.newArrayList("keycode", "keybinding");
    private static final List<String> languageTriggers = StringUtils.newArrayList("language", "lang", "langId", "languageId");
    private static final Config DEFAULT = new Config().applyDefaults();
    private static final Config INSTANCE = loadOrCreate();
    public transient boolean hasChanged = false, isNewFile = false;
    // Global Settings
    public String _README = "https://gitlab.com/CDAGaming/CraftPresence/-/wikis/home";
    public String _SOURCE = "https://gitlab.com/CDAGaming/CraftPresence";
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

    public Config(final Config other) {
        transferFrom(other);
    }

    public Config() {
        // N/A
    }

    public static Config getInstance() {
        return new Config(INSTANCE);
    }

    public static String getConfigPath() {
        return Constants.configDir + File.separator + Constants.MOD_ID + ".json";
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
            rawJson = FileUtils.getJsonData(getConfigFile(), JsonElement.class);
        } catch (Exception ex) {
            if (ex.getClass() != FileNotFoundException.class && ex.getClass() != NoSuchFileException.class) {
                Constants.LOG.error(Constants.TRANSLATOR.translate("craftpresence.logger.error.config.save"));
                Constants.LOG.debugError(ex);

                if (!getConfigFile().renameTo(new File(getConfigPath() + ".bak"))) {
                    Constants.LOG.error(Constants.TRANSLATOR.translate("craftpresence.logger.error.config.backup"));
                }
            }
        }
        return new Pair<>(config, rawJson);
    }

    public static Config loadOrCreate(final boolean forceCreate) {
        final Pair<Config, JsonElement> data = read();
        Config config = data.getFirst();
        JsonElement rawJson = data.getSecond();

        final boolean hasNoData = config == null;
        final boolean isInvalidData = !hasNoData && (forceCreate || (config._schemaVersion <= 0 || config._lastMCVersionId <= 0));
        if (hasNoData || isInvalidData) {
            config = hasNoData ? DEFAULT : config.getDefaults();
            config.isNewFile = true;
            config.hasChanged = isInvalidData;
        }

        final boolean wasNewFile = config.isNewFile;
        config.handleSync(rawJson);
        if (!forceCreate) {
            config.save();
        }
        if (wasNewFile) {
            Constants.LOG.info(Constants.TRANSLATOR.translate("craftpresence.logger.info.config.new"));
        } else {
            Constants.LOG.info(Constants.TRANSLATOR.translate("craftpresence.logger.info.config.save"));
        }
        return config;
    }

    public static Config loadOrCreate() {
        return loadOrCreate(false);
    }

    public static Object getProperty(final Config instance, final String... path) {
        if (instance == null) {
            return null;
        }
        return instance.findProperty(path);
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

    public static int getGameVersion() {
        return MC_VERSION;
    }

    public static int getSchemaVersion() {
        return VERSION;
    }

    public Config applyDefaults(final Config config) {
        config._schemaVersion = getSchemaVersion();
        config._lastMCVersionId = getGameVersion();
        return config;
    }

    public Config applyDefaults() {
        return applyDefaults(this);
    }

    @Override
    public Config getDefaults() {
        return new Config(DEFAULT);
    }

    @Override
    public Config copy() {
        return new Config(this);
    }

    @Override
    public void transferFrom(Module target) {
        if (target instanceof Config && !equals(target)) {
            final Config data = (Config) target;
            hasChanged = data.hasChanged;
            isNewFile = data.isNewFile;

            _README = data._README;
            _SOURCE = data._SOURCE;
            _schemaVersion = data._schemaVersion;
            _lastMCVersionId = data._lastMCVersionId;

            generalSettings = new General(data.generalSettings);
            biomeSettings = new Biome(data.biomeSettings);
            dimensionSettings = new Dimension(data.dimensionSettings);
            serverSettings = new Server(data.serverSettings);
            statusMessages = new Status(data.statusMessages);
            advancedSettings = new Advanced(data.advancedSettings);
            accessibilitySettings = new Accessibility(data.accessibilitySettings);
            displaySettings = new Display(data.displaySettings);
        }
    }

    public void applySettings() {
        if (hasChanged) {
            CommandUtils.reloadData(true);
            hasChanged = false;
        }
        isNewFile = false;
    }

    public void applyFrom(final Config old) {
        boolean needsReboot = false;
        if (!generalSettings.clientId.equals(old.generalSettings.clientId)) {
            needsReboot = true; // Client ID changed
        } else if (generalSettings.preferredClientLevel != old.generalSettings.preferredClientLevel) {
            needsReboot = true; // Preferred Client Level changed
        } else if (generalSettings.resetTimeOnInit != old.generalSettings.resetTimeOnInit) {
            needsReboot = true; // Reset Time On Init changed
        } else if (generalSettings.autoRegister != old.generalSettings.autoRegister) {
            needsReboot = true; // Auto Register changed
        } else if (!accessibilitySettings.languageId.equals(old.accessibilitySettings.languageId)) {
            Constants.TRANSLATOR.syncTranslations(); // Fallback Language ID Changed
        } else if (advancedSettings.allowDuplicatePackets != old.advancedSettings.allowDuplicatePackets) {
            needsReboot = true; // Allow Duplicate Packets changed
        } else if (advancedSettings.maxConnectionAttempts != old.advancedSettings.maxConnectionAttempts) {
            needsReboot = true; // Max Connection Attempts changed
        }

        if (accessibilitySettings.renderTooltips != old.accessibilitySettings.renderTooltips
                || !accessibilitySettings.tooltipBackground.equals(old.accessibilitySettings.tooltipBackground) ||
                !accessibilitySettings.tooltipBorder.equals(old.accessibilitySettings.tooltipBorder)) {
            CommandUtils.setDefaultTooltip(); // Render Tooltips, Tooltip Background, or Tooltip Border changed
        }

        if (advancedSettings.debugMode != old.advancedSettings.debugMode ||
                advancedSettings.verboseMode != old.advancedSettings.verboseMode ||
                advancedSettings.refreshRate != old.advancedSettings.refreshRate) {
            CommandUtils.updateModes(); // Debug Mode, Verbose Mode, or Refresh Rate changed
        }

        if (advancedSettings.enableClassGraph != old.advancedSettings.enableClassGraph) {
            CommandUtils.setupClassScan(true); // Enable Class Graph changed
        }

        if (displaySettings.dynamicVariables != old.displaySettings.dynamicVariables) {
            CommandUtils.syncDynamicVariables(old.displaySettings.dynamicVariables); // Dynamic Variables changed
        }

        if (accessibilitySettings.stripTranslationColors != old.accessibilitySettings.stripTranslationColors) {
            Constants.TRANSLATOR.setStripColors(accessibilitySettings.stripTranslationColors); // Strip Translation Colors changed
        }
        if (accessibilitySettings.stripTranslationFormatting != old.accessibilitySettings.stripTranslationFormatting) {
            Constants.TRANSLATOR.setStripFormatting(accessibilitySettings.stripTranslationFormatting); // Strip Translation Formatting changed
        }

        if (needsReboot) {
            CommandUtils.setupRPC();
        }
    }

    public JsonElement handleMigrations(JsonElement rawJson, final int oldVer, final int newVer) {
        if (isNewFile) {
            final File legacyFile = new File(Constants.configDir + File.separator + Constants.MOD_ID + ".properties");
            if (legacyFile.exists()) {
                new Legacy2Modern(legacyFile, "UTF-8").apply(this, rawJson);
            } else {
                // fileVersion, configDirectories[main,server-entries]
                final Map<Integer, String> hypherionFiles = new HashMapBuilder<Integer, String>()
                        .put(0, Constants.configDir + File.separator)
                        .put(31, OSUtils.USER_DIR + File.separator + "simple-rpc" + File.separator)
                        .put(32, Constants.configDir + File.separator + "simple-rpc" + File.separator)
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
                Constants.LOG.info(Constants.TRANSLATOR.translate("craftpresence.logger.info.config.outdated", currentVer, newVer));

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
                if (MathUtils.isWithinValue(currentVer, 3, 4, true, false)) {
                    // Schema Changes (v3 -> v4)
                    //  - Migrate Color-Related Settings to new System
                    final JsonObject oldData = rawJson.getAsJsonObject()
                            .getAsJsonObject("accessibilitySettings");
                    final boolean showBackgroundAsDark = oldData
                            .getAsJsonPrimitive("showBackgroundAsDark").getAsBoolean();
                    final Map<String, String> propsToChange = new HashMapBuilder<String, String>()
                            .put("tooltipBackgroundColor", "tooltipBackground")
                            .put("tooltipBorderColor", "tooltipBorder")
                            .put("guiBackgroundColor", "guiBackground")
                            .build();

                    for (Map.Entry<String, String> entry : propsToChange.entrySet()) {
                        final String oldValue = oldData.getAsJsonPrimitive(entry.getKey()).getAsString();
                        final ColorData newValue = new ColorData();

                        if (!StringUtils.isNullOrEmpty(oldValue)) {
                            if (StringUtils.isValidColorCode(oldValue)) {
                                final ColorSection startColor = new ColorSection(
                                        StringUtils.findColor(oldValue)
                                );
                                newValue.setStartColor(startColor);

                                if (entry.getKey().equalsIgnoreCase("tooltipBorderColor")) {
                                    final int borderColorCode = startColor.getColor().getRGB();
                                    final String borderColorEnd = Integer.toString((borderColorCode & 0xFEFEFE) >> 1 | borderColorCode & 0xFF000000);
                                    newValue.setEndColor(new ColorSection(
                                            StringUtils.findColor(borderColorEnd)
                                    ));
                                }
                            } else {
                                final boolean applyTint = showBackgroundAsDark && entry.getKey().equalsIgnoreCase("guiBackgroundColor");
                                if (applyTint) {
                                    newValue.setStartColor(
                                            new ColorSection(64, 64, 64, 255)
                                    );
                                }
                                newValue.setTexLocation(oldValue);
                            }
                        }

                        accessibilitySettings.setProperty(entry.getValue(), newValue);
                    }
                    currentVer = 4;
                }
                if (MathUtils.isWithinValue(currentVer, 4, 5, true, false)) {
                    // Schema Changes (v4 -> v5)
                    //  - Placeholder: `data.screen.class` -> `getClass(data.screen.instance)`
                    new TextReplacer(
                            new HashMapBuilder<String, String>()
                                    .put("data.screen.class", "getClass(data.screen.instance)")
                                    .build(),
                            true,
                            true, false, true
                    ).apply(this, rawJson);
                    currentVer = 5;
                }
                if (MathUtils.isWithinValue(currentVer, 5, 6, true, false)) {
                    // Schema Changes (v5 -> v6)
                    //  - Property: `advancedSettings.renderTooltips` -> `accessibilitySettings.renderTooltips`
                    accessibilitySettings.renderTooltips = rawJson.getAsJsonObject()
                            .getAsJsonObject("advancedSettings")
                            .getAsJsonPrimitive("renderTooltips").getAsBoolean();
                    currentVer = 6;
                }

                save();
            }

            // Refresh the raw json contents, in case of any changes
            rawJson = read().getSecond();
        }
        return rawJson;
    }

    public JsonElement handleVerification(final JsonElement rawJson, final KeyConverter.ConversionMode keyCodeMigrationId, final TranslationConverter.ConversionMode languageMigrationId, final String... path) {
        // Verify Type Safety, reset value if anything is null or invalid for it's type
        String pathPrefix = String.join(".", path);
        if (!StringUtils.isNullOrEmpty(pathPrefix)) {
            pathPrefix += ".";
        }

        if (rawJson != null) {
            final Object parentValue = findProperty(path);
            for (Map.Entry<String, JsonElement> entry : rawJson.getAsJsonObject().entrySet()) {
                final String rawName = pathPrefix + entry.getKey();
                final List<String> configPath = StringUtils.newArrayList(path);
                configPath.add(entry.getKey());
                final String[] pathData = configPath.toArray(new String[0]);

                final JsonElement rawValue = entry.getValue();
                Object defaultValue = getDefaults().findProperty(pathData);
                Object currentValue = findProperty(pathData);
                boolean shouldReset = false, shouldContinue = true;

                if (defaultValue == null) {
                    if (currentValue == null || !(parentValue instanceof ColorData || parentValue instanceof ColorSection || parentValue instanceof PresenceData || parentValue instanceof ModuleData || parentValue instanceof Button)) {
                        Constants.LOG.error(Constants.TRANSLATOR.translate("craftpresence.logger.error.config.prop.invalid", rawName));
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
                                                final int migratedKeyCode = KeyConverter.convertKey(boolData.getSecond(), getGameVersion(), keyCodeMigrationId);
                                                if (migratedKeyCode != boolData.getSecond()) {
                                                    Constants.LOG.info(Constants.TRANSLATOR.translate("craftpresence.logger.info.migration.apply", "KEYCODE", keyCodeMigrationId.name(), rawName, boolData.getSecond(), migratedKeyCode));
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
                                    Constants.LOG.error(Constants.TRANSLATOR.translate("craftpresence.logger.error.config.missing.default", rawName));
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
                                        if (languageMigrationId != TranslationConverter.ConversionMode.Unknown) {
                                            final String migratedLanguageId = TranslationConverter.convertId(rawStringValue, getGameVersion(), languageMigrationId);
                                            if (!migratedLanguageId.equals(rawStringValue)) {
                                                Constants.LOG.info(Constants.TRANSLATOR.translate("craftpresence.logger.info.migration.apply", "LANGUAGE", languageMigrationId.name(), rawName, rawStringValue, migratedLanguageId));
                                                setProperty((Object) migratedLanguageId, pathData);
                                            }
                                        }
                                        break;
                                    }
                                }
                            }
                        }

                        if (shouldReset) {
                            Constants.LOG.error(Constants.TRANSLATOR.translate("craftpresence.logger.error.config.prop.empty", rawName));
                            resetProperty(pathData);
                        }
                    }
                }
            }
        }
        return rawJson;
    }

    public JsonElement handleSync(JsonElement rawJson) {
        final int newSchemaVer = getSchemaVersion();
        if (isNewFile || _schemaVersion != newSchemaVer) {
            int oldVer = _schemaVersion;
            rawJson = handleMigrations(rawJson, oldVer, newSchemaVer);
            _schemaVersion = newSchemaVer;
        }
        final int oldMCVer = _lastMCVersionId;
        final int newMCVer = getGameVersion();
        if (oldMCVer != newMCVer) {
            _lastMCVersionId = newMCVer;

            // Reset some config settings when game version changes
            final Accessibility accessibilityDefaults = accessibilitySettings.getDefaults();
            accessibilitySettings.guiBackground = accessibilityDefaults.guiBackground;
            accessibilitySettings.altGuiBackground = accessibilityDefaults.altGuiBackground;
        }

        // Sync Flag Data
        if (ModUtils.IS_TEXT_FORMATTING_BLOCKED) {
            accessibilitySettings.stripTranslationFormatting = true;
        }

        // Sync Migration Data for later usage
        final KeyConverter.ConversionMode keyCodeMigrationId;
        final TranslationConverter.ConversionMode languageMigrationId;

        // Case 1 Notes (KeyCode):
        // In this situation, if the currently parsed protocol version differs and
        // is a newer version then or exactly 17w43a (1.13, 341), then
        // we need to ensure any keycode assignments are in an LWJGL 3 format.
        // Otherwise, if our current protocol version is anything less than 17w43a (1.13, 341),
        // we need to ensure any keycode assignments are in an LWJGL 2 format.
        // If neither is true, then we mark the migration data as None, and it will be verified
        if (oldMCVer < 341 && newMCVer >= 341) {
            keyCodeMigrationId = KeyConverter.ConversionMode.Lwjgl3;
        } else if (oldMCVer >= 341 && newMCVer < 341) {
            keyCodeMigrationId = KeyConverter.ConversionMode.Lwjgl2;
        } else if (oldMCVer >= 0 && newMCVer >= 0) {
            keyCodeMigrationId = KeyConverter.ConversionMode.None;
        } else {
            keyCodeMigrationId = KeyConverter.ConversionMode.Unknown;
        }

        // Case 2 Notes (Language ID):
        // In this situation, if the currently parsed protocol version differs and
        // is a newer version then or exactly 16w32a (1.11, 301), then
        // we need to ensure any Language Locale's are complying with Pack Format 3 and above.
        // Otherwise, if our current protocol version is anything less than 16w32a (1.11, 301),
        // we need to ensure any Language Locale's are complying with Pack Format 2 and below.
        // If neither is true, then we mark the migration data as None, and it will be verified
        if (oldMCVer < 301 && newMCVer >= 301) {
            languageMigrationId = TranslationConverter.ConversionMode.PackFormat3;
        } else if (oldMCVer >= 301 && newMCVer < 301) {
            languageMigrationId = TranslationConverter.ConversionMode.PackFormat2;
        } else if (oldMCVer >= 0 && newMCVer >= 0) {
            languageMigrationId = TranslationConverter.ConversionMode.None;
        } else {
            languageMigrationId = TranslationConverter.ConversionMode.Unknown;
        }

        Constants.LOG.debugInfo(Constants.TRANSLATOR.translate("craftpresence.logger.info.migration.add", keyCodeTriggers.toString(), keyCodeMigrationId, keyCodeMigrationId.equals(KeyConverter.ConversionMode.None) ? "Verification" : "Setting Change"));
        Constants.LOG.debugInfo(Constants.TRANSLATOR.translate("craftpresence.logger.info.migration.add", languageTriggers.toString(), languageMigrationId, languageMigrationId.equals(TranslationConverter.ConversionMode.None) ? "Verification" : "Setting Change"));
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
                } else if (instance instanceof Module) {
                    result = ((Module) instance).getProperty(name);
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

    public Object findProperty(final String... path) {
        return lookupProperty(path).getFirst();
    }

    @Override
    public Object getProperty(final String name) {
        switch (name) {
            case "hasChanged":
                return hasChanged;
            case "isNewFile":
                return isNewFile;
            case "_README":
                return _README;
            case "_SOURCE":
                return _SOURCE;
            case "_schemaVersion":
                return _schemaVersion;
            case "_lastMCVersionId":
                return _lastMCVersionId;
            case "generalSettings":
                return generalSettings;
            case "biomeSettings":
                return biomeSettings;
            case "dimensionSettings":
                return dimensionSettings;
            case "serverSettings":
                return serverSettings;
            case "statusMessages":
                return statusMessages;
            case "advancedSettings":
                return advancedSettings;
            case "accessibilitySettings":
                return accessibilitySettings;
            case "displaySettings":
                return displaySettings;
            default:
                return null;
        }
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

                if (parentData.getSecond() instanceof Module) {
                    ((Module) parentData.getSecond()).setProperty(parentData.getThird(), data);
                } else {
                    StringUtils.updateField(parentData.getFirst(), parentData.getSecond(), data, parentData.getThird());
                }
            } else {
                if (fieldData.getSecond() instanceof Module) {
                    ((Module) fieldData.getSecond()).setProperty(fieldData.getThird(), value);
                } else {
                    StringUtils.updateField(fieldData.getFirst(), fieldData.getSecond(), value, fieldData.getThird());
                }
            }
        }
    }

    @Override
    public void setProperty(final String name, final Object value) {
        try {
            switch (name) {
                case "hasChanged":
                    hasChanged = (Boolean) value;
                    break;
                case "isNewFile":
                    isNewFile = (Boolean) value;
                    break;
                case "_README":
                    _README = (String) value;
                    break;
                case "_SOURCE":
                    _SOURCE = (String) value;
                    break;
                case "_schemaVersion":
                    _schemaVersion = (Integer) value;
                    break;
                case "_lastMCVersionId":
                    _lastMCVersionId = (Integer) value;
                    break;
                case "generalSettings":
                    generalSettings = (General) value;
                    break;
                case "biomeSettings":
                    biomeSettings = (Biome) value;
                    break;
                case "dimensionSettings":
                    dimensionSettings = (Dimension) value;
                    break;
                case "serverSettings":
                    serverSettings = (Server) value;
                    break;
                case "statusMessages":
                    statusMessages = (Status) value;
                    break;
                case "advancedSettings":
                    advancedSettings = (Advanced) value;
                    break;
                case "accessibilitySettings":
                    accessibilitySettings = (Accessibility) value;
                    break;
                case "displaySettings":
                    displaySettings = (Display) value;
                    break;
                default:
                    break;
            }
        } catch (Throwable ex) {
            printException(ex);
        }
    }

    public void resetProperty(final String... path) {
        setProperty(getDefaults().findProperty(path), path);
    }

    @Override
    public boolean equals(Object obj) {
        if (obj == this) {
            return true;
        }

        if (!(obj instanceof Config)) {
            return false;
        }

        final Config other = (Config) obj;

        return Objects.equals(other.hasChanged, hasChanged) &&
                Objects.equals(other.isNewFile, isNewFile) &&
                Objects.equals(other._README, _README) &&
                Objects.equals(other._SOURCE, _SOURCE) &&
                Objects.equals(other._schemaVersion, _schemaVersion) &&
                Objects.equals(other._lastMCVersionId, _lastMCVersionId) &&
                Objects.equals(other.generalSettings, generalSettings) &&
                Objects.equals(other.biomeSettings, biomeSettings) &&
                Objects.equals(other.dimensionSettings, dimensionSettings) &&
                Objects.equals(other.serverSettings, serverSettings) &&
                Objects.equals(other.statusMessages, statusMessages) &&
                Objects.equals(other.advancedSettings, advancedSettings) &&
                Objects.equals(other.accessibilitySettings, accessibilitySettings) &&
                Objects.equals(other.displaySettings, displaySettings);
    }

    @Override
    public int hashCode() {
        return Objects.hash(
                hasChanged, isNewFile,
                _README, _SOURCE,
                _schemaVersion, _lastMCVersionId,
                generalSettings, biomeSettings,
                dimensionSettings, serverSettings,
                statusMessages, advancedSettings,
                accessibilitySettings, displaySettings
        );
    }
}
