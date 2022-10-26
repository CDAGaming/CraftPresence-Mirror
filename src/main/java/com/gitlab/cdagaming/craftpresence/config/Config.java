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
import com.gitlab.cdagaming.craftpresence.config.migrators.Legacy2Modern;
import com.gitlab.cdagaming.craftpresence.impl.Pair;
import com.gitlab.cdagaming.craftpresence.impl.Tuple;
import com.gitlab.cdagaming.craftpresence.impl.discord.PartyPrivacy;
import com.gitlab.cdagaming.craftpresence.utils.CommandUtils;
import com.gitlab.cdagaming.craftpresence.utils.FileUtils;
import com.gitlab.cdagaming.craftpresence.utils.StringUtils;
import com.google.common.collect.ImmutableMap;
import com.google.common.collect.Lists;
import com.google.gson.JsonElement;
import com.jagrosh.discordipc.entities.DiscordBuild;

import java.io.File;
import java.io.Serializable;
import java.util.List;
import java.util.Map;

public final class Config implements Serializable {
    private static final long serialVersionUID = -4853238501768086595L;
    private static final Config INSTANCE = loadOrCreate();
    private static Config DEFAULT;

    public transient boolean hasChanged = false, hasClientPropertiesChanged = false, flushClientProperties = false, isNewFile = false;

    public static Config getDefaults() {
        if (DEFAULT == null) {
            DEFAULT = new Config();
        }
        return DEFAULT;
    }

    public static Config getInstance() {
        return INSTANCE;
    }

    public static final int VERSION = 1;

    public static int MC_VERSION;

    @SuppressWarnings("unused")
    public String _README = "https://gitlab.com/CDAGaming/CraftPresence/-/wikis/home";

    public int _schemaVersion = 0;
    public int _lastMCVersionId = 0;

    // General Settings
    public boolean detectCurseManifest = true;
    public boolean detectMultiMCManifest = true;
    public boolean detectMCUpdaterInstance = true;
    public boolean detectTechnicPack = true;
    public boolean showTime = true;
    public boolean detectBiomeData = true;
    public boolean detectDimensionData = true;
    public boolean detectWorldData = true;
    public String clientId = "450485984333660181";
    public String defaultIcon = "grass";
    public boolean enableJoinRequests = false;
    public int partyPrivacyLevel = PartyPrivacy.Public.ordinal();
    public int preferredClientLevel = DiscordBuild.ANY.ordinal();
    public boolean resetTimeOnInit = false;
    public boolean autoRegister = false;
    // Biome Messages
    public String defaultBiomeIcon = "unknown";
    public Map<String, Pair<String, String>> biomeMessages = ImmutableMap.<String, Pair<String, String>>builder()
            .put("default", new Pair<>(
                    ModUtils.TRANSLATOR.translate(true, "craftpresence.defaults.biome_messages.biome_messages"),
                    "" // Defaults to the Biome Name if nothing is supplied
            ))
            .build();
    // Dimension Messages
    public String defaultDimensionIcon = "unknown";
    public Map<String, Pair<String, String>> dimensionMessages = ImmutableMap.<String, Pair<String, String>>builder()
            .put("default", new Pair<>(
                    ModUtils.TRANSLATOR.translate(true, "craftpresence.defaults.dimension_messages.dimension_messages"),
                    "" // Defaults to the Dimension Name if nothing is supplied
            ))
            .build();
    // Server Messages
    public String defaultServerIcon = "default";
    public String defaultServerName = ModUtils.TRANSLATOR.translate(true, "craftpresence.defaults.server_messages.server_name");
    public String defaultServerMotd = ModUtils.TRANSLATOR.translate(true, "craftpresence.defaults.server_messages.server_motd");
    public Map<String, Pair<String, String>> serverMessages = ImmutableMap.<String, Pair<String, String>>builder()
            .put("default", new Pair<>(
                    ModUtils.TRANSLATOR.translate(true, "craftpresence.defaults.server_messages.server_messages"),
                    "" // Defaults to the Server Name if nothing is supplied
            ))
            .build();
    // Status Messages
    public String mainMenuMessage = ModUtils.TRANSLATOR.translate(true, "craftpresence.defaults.state.main_menu");
    public String loadingMessage = ModUtils.TRANSLATOR.translate(true, "craftpresence.defaults.state.loading");
    public String lanMessage = ModUtils.TRANSLATOR.translate(true, "craftpresence.defaults.state.lan");
    public String singlePlayerMessage = ModUtils.TRANSLATOR.translate(true, "craftpresence.defaults.state.single_player");
    public String packPlaceholderMessage = ModUtils.TRANSLATOR.translate(true, "craftpresence.defaults.placeholder.pack");
    public String outerPlayerPlaceholderMessage = ModUtils.TRANSLATOR.translate(true, "craftpresence.defaults.placeholder.player_info.out");
    public String innerPlayerPlaceholderMessage = ModUtils.TRANSLATOR.translate(true, "craftpresence.defaults.placeholder.player_info.in");
    public String playerCoordinatePlaceholderMessage = ModUtils.TRANSLATOR.translate(true, "craftpresence.defaults.placeholder.player_info.coordinate");
    public String playerHealthPlaceholderMessage = ModUtils.TRANSLATOR.translate(true, "craftpresence.defaults.placeholder.player_info.health");
    public String playerAmountPlaceholderMessage = ModUtils.TRANSLATOR.translate(true, "craftpresence.defaults.placeholder.players");
    public String playerItemsPlaceholderMessage = ModUtils.TRANSLATOR.translate(true, "craftpresence.defaults.placeholder.player_info.items");
    public String worldPlaceholderMessage = ModUtils.TRANSLATOR.translate(true, "craftpresence.defaults.placeholder.world_info");
    public String modsPlaceholderMessage = ModUtils.TRANSLATOR.translate(true, "craftpresence.defaults.placeholder.mods");
    public String vivecraftMessage = ModUtils.TRANSLATOR.translate(true, "craftpresence.defaults.special.vivecraft");
    public String fallbackPackPlaceholderMessage = "";
    // Advanced Settings
    public boolean enableCommands = true;
    public boolean enablePerGui = false;
    public boolean enablePerItem = false;
    public boolean enablePerEntity = false;
    public boolean renderTooltips = true;
    public boolean formatWords = true;
    public boolean debugMode = false;
    public boolean verboseMode = false;
    public int refreshRate = 2;
    public int roundSize = 3;
    public boolean includeExtraGuiClasses = false;
    public boolean allowPlaceholderPreviews = false;
    public boolean allowPlaceholderOperators = true;
    public Map<String, String> guiMessages = ImmutableMap.<String, String>builder()
            .put("default", ModUtils.TRANSLATOR.translate(true, "craftpresence.defaults.advanced.gui_messages"))
            .build();
    public Map<String, String> itemMessages = ImmutableMap.<String, String>builder()
            .put("default", ModUtils.TRANSLATOR.translate(true, "craftpresence.defaults.advanced.item_messages"))
            .build();
    public Map<String, String> entityTargetMessages = ImmutableMap.<String, String>builder()
            .put("default", ModUtils.TRANSLATOR.translate(true, "craftpresence.defaults.advanced.entity_target_messages"))
            .build();
    public Map<String, String> entityRidingMessages = ImmutableMap.<String, String>builder()
            .put("default", ModUtils.TRANSLATOR.translate(true, "craftpresence.defaults.advanced.entity_riding_messages"))
            .build();
    public String playerSkinEndpoint = ModUtils.TRANSLATOR.translate(true, "craftpresence.defaults.advanced.player_skin_endpoint");
    // Accessibility Settings
    public String tooltipBackgroundColor = "0xF0100010";
    public String tooltipBorderColor = "0x505000FF";
    public String guiBackgroundColor = "minecraft:" + (ModUtils.MCProtocolID <= 61 && ModUtils.IS_LEGACY_SOFT ? (ModUtils.IS_LEGACY_ALPHA ? "/dirt.png" : "/gui/background.png") : "textures/gui/options_background.png");
    public String buttonBackgroundColor = "minecraft:" + (ModUtils.MCProtocolID <= 61 && ModUtils.IS_LEGACY_SOFT ? "/gui/gui.png" : "textures/gui/widgets.png");
    public String languageId = ModUtils.TRANSLATOR.defaultLanguageId;
    public boolean showBackgroundAsDark = true;
    public boolean stripTranslationColors = false;
    public boolean showLoggingInChat = false;
    public boolean stripExtraGuiElements = ModUtils.IS_LEGACY_HARD;
    public int configKeyCode = ModUtils.MCProtocolID > 340 ? 96 : 41;
    // Display Settings
    public String gameStateMessage = "&SERVER& &PACK&";
    public String detailsMessage = "&MAINMENU&|&DIMENSION&";
    public String largeImageMessage = "&MAINMENU&|&DIMENSION&";
    public String smallImageMessage = "&SERVER& &PACK&";
    public String largeImageKey = "&MAINMENU&|&DIMENSION&";
    public String smallImageKey = "&SERVER&|&PACK&";
    public Map<String, Pair<String, String>> buttonMessages = ImmutableMap.<String, Pair<String, String>>builder()
            .put("default", new Pair<>(
                    ModUtils.TRANSLATOR.translate("craftpresence.defaults.display.button.label"),
                    ModUtils.TRANSLATOR.translate("craftpresence.defaults.display.button.url")
            ))
            .build();
    public Map<String, String> dynamicIcons = ImmutableMap.<String, String>builder()
            .put("default", ModUtils.TRANSLATOR.translate("craftpresence.defaults.display.image.url"))
            .build();

    public static File getConfigFile() {
        return new File(ModUtils.configDir + File.separator + ModUtils.MOD_ID + ".json");
    }

    public static File getLegacyFile() {
        return new File(ModUtils.configDir + File.separator + ModUtils.MOD_ID + ".properties");
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

    public void handleMigrations(final JsonElement rawJson, final int oldVer, final int newVer) {
        if (isNewFile && getLegacyFile().exists()) {
            new Legacy2Modern(getLegacyFile(), "UTF-8").apply(this, rawJson);
        }
    }

    public void handleVersionChange(final JsonElement rawJson, final int oldVer, final int newVer) {
        if (!isNewFile) {
            // TODO
            // If we transition between LWJGL versions, reset all keyCode triggers to their defaults (Which should have checks to assign depending on version)
            // This is better then the old format, where we'd migrate other's keyCodes, which in most cases failed
            // If we transition between Pack Format Versions, we want to ensure any language settings are reset to their defaults
        }
    }

    public void handleVerification(final JsonElement rawJson) {
        // TODO
        // - Verify Type Safety, reset value if anything is null or invalid for it's type
        // - If the field name contains a known keyCode trigger, ensure it is a valid keycode (Otherwise reset)
        if (rawJson != null) {
            final List<String> propsToReset = Lists.newArrayList();
            for (Map.Entry<String, JsonElement> entry : rawJson.getAsJsonObject().entrySet()) {
                final String rawName = entry.getKey();
                final JsonElement rawValue = entry.getValue();
                final Object defaultValue = getProperty(getDefaults(), rawName);
                boolean shouldReset = false;

                if (defaultValue == null) {
                    ModUtils.LOG.error(ModUtils.TRANSLATOR.translate(true, "craftpresence.logger.error.config.prop.invalid", rawName));
                } else {
                    final Object currentValue = getProperty(this, rawName);
                    if (!StringUtils.isNullOrEmpty(defaultValue.toString()) && StringUtils.isNullOrEmpty(currentValue.toString())) {
                        shouldReset = true;
                    } else {
                        final Class<?> expectedClass = currentValue.getClass();
                        // TODO: Per-Type validation
                        if ((expectedClass == boolean.class || expectedClass == Boolean.class) &&
                                !StringUtils.isValidBoolean(rawValue.getAsString())) {
                            shouldReset = true;
                        }
                    }

                    if (shouldReset) {
                        ModUtils.LOG.error(ModUtils.TRANSLATOR.translate(true, "craftpresence.logger.error.config.prop.empty", rawName));
                        propsToReset.add(rawName);
                    }
                }
            }

            for (String propertyName : propsToReset) {
                resetProperty(this, propertyName);
            }
        }
    }

    public void handleSync(final JsonElement rawJson) {
        if (_schemaVersion != VERSION) {
            int oldVer = _schemaVersion;
            handleMigrations(rawJson, oldVer, VERSION);
            _schemaVersion = VERSION;
        }
        if (_lastMCVersionId != MC_VERSION) {
            int oldVer = _lastMCVersionId;
            handleVersionChange(rawJson, oldVer, MC_VERSION);
            _lastMCVersionId = MC_VERSION;
        }
        handleVerification(rawJson);
    }

    public void save() {
        // Ensure Critical Data is setup
        applyData();
        FileUtils.writeJsonData(this, getConfigFile(), "UTF-8",
                FileUtils.Modifiers.DISABLE_ESCAPES, FileUtils.Modifiers.PRETTY_PRINT);
    }

    public static Object getProperty(final Config instance, final String name) {
        return StringUtils.lookupObject(Config.class, instance, name);
    }

    public static void setProperty(final Config instance, final String name, final Object value) {
        StringUtils.updateField(Config.class, instance, new Tuple<>(name, value, null));
    }

    public static void resetProperty(final Config instance, final String name) {
        setProperty(instance, name, getProperty(getDefaults(), name));
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
        return config;
    }

    public static Config loadOrCreate() {
        return loadOrCreate(false);
    }
}
