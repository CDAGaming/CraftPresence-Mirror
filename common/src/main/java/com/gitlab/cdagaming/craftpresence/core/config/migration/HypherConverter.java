/*
 * MIT License
 *
 * Copyright (c) 2018 - 2025 CDAGaming (cstack2011@yahoo.com)
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
import com.gitlab.cdagaming.craftpresence.core.config.element.Button;
import com.gitlab.cdagaming.craftpresence.core.config.element.ModuleData;
import com.gitlab.cdagaming.craftpresence.core.config.element.PresenceData;
import com.google.gson.JsonElement;
import com.jagrosh.discordipc.entities.ActivityType;
import edu.umd.cs.findbugs.annotations.SuppressFBWarnings;
import io.github.cdagaming.unicore.impl.HashMapBuilder;
import io.github.cdagaming.unicore.utils.MathUtils;
import io.github.cdagaming.unicore.utils.StringUtils;
import me.hypherionmc.moonconfig.core.AbstractConfig;
import me.hypherionmc.moonconfig.core.UnmodifiableConfig;
import me.hypherionmc.moonconfig.core.file.FileConfig;

import java.io.File;
import java.util.List;
import java.util.Map;
import java.util.regex.Pattern;

/**
 * Migration from SimpleRPC (Hypherion) Config to our {@link Config} format
 *
 * @author CDAGaming, HypherionSA
 */
public class HypherConverter implements DataMigrator {
    private static final Pattern EXPR_PATTERN = Pattern.compile("\\{(.*?)}");
    private static final int LOWEST_SUPPORTED = 13;
    private static final int HIGHEST_SUPPORTED = 26;
    private final int fileVersion;
    private final String configPath, serverEntriesPath, replayModPath;
    // oldName -> newName (v18 and below)
    private final Map<String, String> placeholderMappings = new HashMapBuilder<String, String>()
            .put("%player%", "{player.name}")
            .put("%world%", "{dimension.name}")
            .put("%mods%", "{general.mods}")
            .put("%difficulty%", "{world.difficulty}")
            .put("%position%", "{'x: ' + player.position.x + ', y: ' + player.position.y + ', z: ' + player.position.z}")
            .put("%biome%", "{biome.name}")
            .put("%mcver%", "{general.version}")
            .put("%instance%", "{pack.name}")
            .put("%launcher%", "{general.brand}")
            .put("%server%", "{replace(server.address.short, '.', '_')}")
            .put("%launchername%", "{toLower(general.brand)}")
            .put("%savename%", "{world.name}")
            .put("%playerhead%", "{player.icon}")
            .put("%gametime12%", "{world.time.format_12}")
            .put("%gametime%", "{world.time.format_24}")
            .put("%day%", "{world.day}")
            .put("%weather%", "{world.weather.name}")
            .put("%replayframe%", "{replaymod.frames.current}")
            .put("%replaytotal%", "{replaymod.frames.total}")
            .put("%replaytime%", "{replaymod.time.current}")
            .put("%replaytimeleft%", "{replaymod.time.remaining}")
            //
            .put("%serverip%", "{server.address.short}")
            .put("%servername%", "{server.name}")
            .put("%players%", "{server.players.current}")
            .put("%playersexcl%", "{server.players.current - 1}")
            .put("%maxplayers%", "{server.players.max}")
            .put("%motd%", "{server.motd.raw}")
            .put("%servericon%", "{server.icon}")
            //
            .put("%realmname%", "{server.name}")
            .put("%realmdescription%", "{server.motd.raw}")
            .put("%realmgame%", "{server.minigame}")
            .put("%realmicon%", "{server.icon}")
            .build();
    // oldName -> newName (v19 and above)
    private final Map<String, String> placeholderMappingsV2 = new HashMapBuilder<String, String>()
            .put("{{game.version}}", "{general.version}")
            .put("{{game.mods}}", "{general.mods}")
            .put("{{player.name}}", "{player.name}")
            .put("{{player.uuid}}", "{player.uuid.full}")
            .put("{{world.name}}", "{dimension.name}")
            .put("{{world.difficulty}}", "{world.difficulty}")
            .put("{{world.savename}}", "{world.name}")
            .put("{{world.time.12}}", "{world.time.format_12}")
            .put("{{world.time.24}}", "{world.time.format_24}")
            .put("{{world.time.day}}", "{world.day}")
            .put("{{world.weather}}", "{world.weather.name}")
            .put("{{world.biome}}", "{biome.name}")
            .put("{{player.position}}", "{'x: ' + player.position.x + ', y: ' + player.position.y + ', z: ' + player.position.z}")
            .put("{{player.health.current}}", "{player.health.current}")
            .put("{{player.health.max}}", "{player.health.max}")
            .put("{{player.health.percent}}", "{(player.health.current / player.health.max) * 100}")
            .put("{{player.item.off_hand}}", "{item.off_hand.name}")
            .put("{{player.item.main_hand}}", "{item.main_hand.name}")
            .put("{{images.player}}", "https://skinatar.firstdark.dev/avatar/{getOrDefault(player.uuid.short, player.name)}")
            .put("{{images.player.head}}", "https://skinatar.firstdark.dev/head/{getOrDefault(player.uuid.short, player.name)}")
            .put("{{images.realm}}", "{server.icon}")
            .put("{{images.server}}", "{server.icon}")
            .put("{{server.ip}}", "{server.address.short}")
            .put("{{server.ip_underscore}}", "{replace(server.address.short, '.', '_')}")
            .put("{{server.name}}", "{server.name}")
            .put("{{server.motd}}", "{server.motd.raw}")
            .put("{{server.players.count}}", "{server.players.current}")
            .put("{{server.players.countexcl}}", "{server.players.current - 1}")
            .put("{{server.players.max}}", "{server.players.max}")
            .put("{{realm.name}}", "{server.name}")
            .put("{{realm.description}}", "{server.motd.raw}")
            .put("{{realm.world}}", "{server.type}")
            .put("{{realm.game}}", "{server.minigame}")
            .put("{{realm.players.count}}", "{server.players.count}")
            .put("{{realm.players.max}}", "{server.players.max}")
            .put("{{replaymod.time.elapsed}}", "{replaymod.time.current}")
            .put("{{replaymod.time.left}}", "{replaymod.time.remaining}")
            .put("{{replaymod.frames.current}}", "{replaymod.frames.current}")
            .put("{{replaymod.frames.total}}", "{replaymod.frames.total}")
            .put("{{launcher.name}}", "{general.brand}")
            .put("{{launcher.pack}}", "{pack.name}")
            .put("{{launcher.icon}}", "{pack.icon}")
            .build();
    private int configVersion = -1, serverEntryVersion = -1, replayModVersion = -1;

    /**
     * Initializes this {@link DataMigrator}
     *
     * @param entry A mapping containing the fileVersion and configFolder to be used
     */
    public HypherConverter(Map.Entry<Integer, String> entry) {
        this.fileVersion = entry.getKey();
        this.configPath = entry.getValue() + "simple-rpc.toml";
        this.serverEntriesPath = entry.getValue() + "server-entries.toml";
        this.replayModPath = entry.getValue() + "simple-rpc-replaymod.toml";
    }

    @SuppressFBWarnings("RCN_REDUNDANT_NULLCHECK_WOULD_HAVE_BEEN_A_NPE")
    @Override
    public Config apply(Config instance, JsonElement rawJson, Object... args) {
        Constants.LOG.info("Simple RPC (By: HypherionSA) config data found, attempting to migrate settings to CraftPresence...");
        Constants.LOG.info("Note: If CraftPresence fails to load after this point, please verify your SimpleRPC config settings or file a ticket.");
        try (FileConfig conf = FileConfig.of(configPath)) {
            conf.load();
            configVersion = conf.getOrElse("general.version", -1);
            Constants.LOG.debugInfo("Main Config file found (Version: %d, File Version: %d), interpreting data...", configVersion, fileVersion);
            if (!MathUtils.isWithinValue(configVersion, LOWEST_SUPPORTED, HIGHEST_SUPPORTED, true, true)) {
                Constants.LOG.error("You are using an unsupported Simple RPC config file (Supported Versions: v%d - v%d, Found Version: v%d), skipping...", LOWEST_SUPPORTED, HIGHEST_SUPPORTED, configVersion);
                return instance;
            }

            if (MathUtils.isWithinValue(configVersion, 18, 24, false, false)) {
                Constants.LOG.error("You are using a Simple RPC config file from an unsupported mod release, please update your config using a newer mod version and try again...");
                return instance;
            }

            // Main Conversion
            final Object clientId = getProperty(conf, "general.applicationID", "general.clientID");
            if (clientId != null) {
                instance.generalSettings.clientId = clientId.toString();
            }
            instance.advancedSettings.debugMode = conf.get("general.debugging");
            final boolean launcherIntegration = conf.get("general.launcherIntegration");
            instance.generalSettings.detectATLauncherInstance = launcherIntegration;
            instance.generalSettings.detectCurseManifest = launcherIntegration;
            instance.generalSettings.detectMCUpdaterInstance = launcherIntegration;
            instance.generalSettings.detectTechnicPack = launcherIntegration;
            instance.generalSettings.detectMultiMCManifest = launcherIntegration;

            final boolean areOverridesEnabled = conf.get("dimension_overrides.enabled");
            final Object dimensionList = conf.get("dimension_overrides.dimensions");
            if (dimensionList instanceof List<?> dimensions) {
                for (Object entryObj : dimensions) {
                    if (entryObj instanceof AbstractConfig entry) {
                        String name = entry.get("name").toString();
                        final boolean isBiome = name.startsWith("biome:");
                        if (isBiome) {
                            name = name.replaceFirst("biome:", "");
                        }
                        final AbstractConfig target = getPresenceEntry(entry);
                        if (target != null) {
                            final ModuleData data = new ModuleData()
                                    .setData(convertPresenceData(target, areOverridesEnabled, true));
                            (isBiome ? instance.biomeSettings.biomeData : instance.dimensionSettings.dimensionData).put(name, data);
                        }
                    }
                }
            }

            // Custom Variables
            final boolean areCustomsEnabled = conf.get("custom.enabled");
            if (areCustomsEnabled) {
                if (conf.get("custom.variables") instanceof List<?> customVars) {
                    for (Object entryObj : customVars) {
                        if (entryObj instanceof AbstractConfig entry) {
                            String name = entry.get("name").toString();
                            String value = entry.get("value").toString();

                            instance.displaySettings.dynamicVariables.put(name, processPlaceholder(value));
                        }
                    }
                }
            }

            // Per-GUI Events
            instance.advancedSettings.enablePerGui = true;

            final AbstractConfig serverListEvent = getPresenceEntry(conf.get("server_list"));
            if (serverListEvent != null) {
                instance.advancedSettings.guiSettings.guiData.put("GuiSelectServer", new ModuleData()
                        .setData(convertPresenceData(serverListEvent)));
                instance.advancedSettings.guiSettings.guiData.put("GuiConnectFailed", new ModuleData()
                        .setData(convertPresenceData(serverListEvent)));
            }

            final AbstractConfig joinGameEvent = getPresenceEntry(conf.get("join_game"));
            if (joinGameEvent != null) {
                instance.advancedSettings.guiSettings.guiData.put("GuiDownloadTerrain", new ModuleData()
                        .setData(convertPresenceData(joinGameEvent)));
            }

            if (isActive(ConfigFlag.USE_PAUSE_EVENT)) {
                final AbstractConfig pauseEvent = getPresenceEntry(conf.get("paused"));
                if (pauseEvent != null) {
                    instance.advancedSettings.guiSettings.guiData.put("GuiIngameMenu", new ModuleData()
                            .setData(convertPresenceData(pauseEvent)));
                }
            }

            final AbstractConfig initEvent = getPresenceEntry(conf.get("init"));
            if (initEvent != null) {
                instance.statusMessages.loadingData.setData(convertPresenceData(initEvent));
            }

            final AbstractConfig mainMenuEvent = getPresenceEntry(conf.get("main_menu"));
            if (mainMenuEvent != null) {
                instance.statusMessages.mainMenuData.setData(convertPresenceData(mainMenuEvent));
            }

            final AbstractConfig realmsEvent = getPresenceEntry(conf.get("realms"));
            if (realmsEvent != null) {
                instance.statusMessages.realmData.setData(convertPresenceData(realmsEvent));
            }

            final AbstractConfig singlePlayerEvent = getPresenceEntry(conf.get("single_player"));
            if (singlePlayerEvent != null) {
                instance.statusMessages.singleplayerData.setData(convertPresenceData(singlePlayerEvent));
            }

            final AbstractConfig multiPlayerEvent = getPresenceEntry(conf.get("multi_player"));
            if (multiPlayerEvent != null) {
                instance.serverSettings.serverData.get("default").setData(convertPresenceData(multiPlayerEvent));
            }

            final AbstractConfig genericEvent = getPresenceEntry(conf.get("generic"));
            if (genericEvent != null) {
                instance.displaySettings.presenceData = convertPresenceData(genericEvent);
            }

            instance.save();
        }

        // Server Entries Conversion
        final File serverEntriesFile = new File(serverEntriesPath);
        if (serverEntriesFile.exists()) {
            try (FileConfig conf = FileConfig.of(serverEntriesFile)) {
                conf.load();
                serverEntryVersion = conf.get("version");
                Constants.LOG.debugInfo("Server Entries file found (Version: %d, File Version: %d), interpreting data...", serverEntryVersion, fileVersion);

                final boolean areOverridesEnabled = conf.get("enabled");
                if (conf.get("entry") instanceof List<?> entries) {
                    for (Object entryObj : entries) {
                        if (entryObj instanceof AbstractConfig entry) {
                            final AbstractConfig target = getPresenceEntry(entry);
                            if (target != null) {
                                instance.serverSettings.serverData.put(entry.get("ip"), new ModuleData()
                                        .setData(convertPresenceData(target, areOverridesEnabled, true)));
                            }
                        }
                    }
                }
                instance.save();
            }
        }

        // Replay Mod Integration Conversion
        final File replayModFile = new File(replayModPath);
        if (replayModFile.exists()) {
            try (FileConfig conf = FileConfig.of(replayModFile)) {
                conf.load();
                replayModVersion = conf.get("general.version");
                Constants.LOG.debugInfo("Replay Mod Integration file found (Version: %d, File Version: %d), interpreting data...", replayModVersion, fileVersion);

                instance.advancedSettings.enablePerGui = true;

                final AbstractConfig replayViewerEvent = getPresenceEntry(conf.get("replay_viewer"));
                if (replayViewerEvent != null) {
                    instance.advancedSettings.guiSettings.guiData.put("GuiReplayViewer", new ModuleData()
                            .setData(convertPresenceData(replayViewerEvent)));
                }

                final AbstractConfig replayEditorEvent = getPresenceEntry(conf.get("replay_editor"));
                if (replayEditorEvent != null) {
                    instance.advancedSettings.guiSettings.guiData.put("GuiReplayOverlay", new ModuleData()
                            .setData(convertPresenceData(replayEditorEvent)));
                }

                final AbstractConfig replayRenderEvent = getPresenceEntry(conf.get("replay_render"));
                if (replayRenderEvent != null) {
                    instance.advancedSettings.guiSettings.guiData.put("GuiVideoRenderer", new ModuleData()
                            .setData(convertPresenceData(replayRenderEvent)));
                }

                instance.save();
            }
        }

        Constants.LOG.info("Migration complete, thanks for using our mods! ~~ CDAGaming and HypherionSA");
        return instance;
    }

    private String processPlaceholder(final String original, final boolean addMetadata) {
        String result = original;
        if (addMetadata) {
            result = "'" + original + "'";
        }
        if (!StringUtils.isNullOrEmpty(result)) {
            for (Map.Entry<String, String> entry : (isActive(ConfigFlag.USE_MULTI_RPC) ? placeholderMappingsV2 : placeholderMappings).entrySet()) {
                result = result.replace(entry.getKey(), entry.getValue());
            }
        }
        return result;
    }

    private String processPlaceholder(final String original) {
        return processPlaceholder(original, false);
    }

    private ActivityType processActivityType(final String original) {
        if (StringUtils.isNullOrEmpty(original)) return ActivityType.Playing;
        return switch (original.toLowerCase()) {
            case "streaming" -> ActivityType.Streaming;
            case "listening" -> ActivityType.Listening;
            case "watching" -> ActivityType.Watching;
            case "custom" -> ActivityType.Custom;
            case "competing" -> ActivityType.Competing;
            default -> ActivityType.Playing;
        };
    }

    private PresenceData convertPresenceData(final AbstractConfig entry, final boolean isEnabled, final boolean useAsMain) {
        final PresenceData data = new PresenceData();
        data.enabled = isEnabled;
        data.useAsMain = useAsMain;
        if (isActive(ConfigFlag.USE_MULTI_RPC)) {
            data.activityType = processActivityType(entry.get("type")).ordinal();
        }
        data.details = processPlaceholder(entry.get("description"));
        data.gameState = processPlaceholder(entry.get("state"));
        if (isActive(ConfigFlag.USE_IMAGE_POOLS)) {
            data.largeImageKey = tryCombineData(entry.get("largeImageKey"));
            data.smallImageKey = tryCombineData(entry.get("smallImageKey"));
        } else {
            data.largeImageKey = processPlaceholder(entry.get("largeImageKey"));
            data.smallImageKey = processPlaceholder(entry.get("smallImageKey"));
        }
        data.largeImageText = processPlaceholder(entry.get("largeImageText"));
        data.smallImageText = processPlaceholder(entry.get("smallImageText"));

        // SimpleRPC always has the Start Timestamp enabled, so we prefill it here
        data.startTimestamp = "{data.general.time}";

        int buttonIndex = 1;
        if (entry.get("buttons") instanceof List<?> buttons) {
            for (Object buttonEntryObj : buttons) {
                if (buttonEntryObj instanceof AbstractConfig buttonEntry) {
                    final Button buttonData = new Button(
                            processPlaceholder(buttonEntry.get("label")),
                            processPlaceholder(buttonEntry.get("url"))
                    );
                    data.addButton("button_" + buttonIndex, buttonData);
                    buttonIndex++;
                }
            }
        }

        return data;
    }

    @SuppressWarnings("unchecked")
    private String tryCombineData(final Object data) {
        switch (data) {
            case null -> {
                return "";
            }
            case String item -> {
                return combineData(item);
            }
            default -> {
                try {
                    return combineData((List<String>) data);
                } catch (Throwable ignored) {
                    return "";
                }
            }
        }
    }

    private String combineData(final String item) {
        return processPlaceholder(item);
    }

    private String combineData(final List<String> items) {
        final StringBuilder dataBuilder = new StringBuilder();
        if (!items.isEmpty()) {
            if (items.size() > 1) {
                dataBuilder.append("{randomString(");
                for (int i = 0; i < items.size(); i++) {
                    final String output = processPlaceholder(items.get(i), true);
                    final boolean hasExpr = EXPR_PATTERN.matcher(output).find();
                    dataBuilder
                            .append(hasExpr ? "getResult(" : "")
                            .append(output)
                            .append(hasExpr ? ")" : "");

                    if (i < items.size() - 1) {
                        dataBuilder.append(",");
                    }
                }
                dataBuilder.append(")}");
            } else {
                dataBuilder.append(processPlaceholder(items.getFirst()));
            }
        }
        return dataBuilder.toString();
    }

    private PresenceData convertPresenceData(final AbstractConfig entry, final boolean useAsMain) {
        return convertPresenceData(entry, entry.getOrElse("enabled", true), useAsMain);
    }

    private PresenceData convertPresenceData(final AbstractConfig entry) {
        return convertPresenceData(entry, true);
    }

    private AbstractConfig getPresenceEntry(final AbstractConfig entry) {
        AbstractConfig target = null;
        if (isActive(ConfigFlag.USE_MULTI_RPC)) {
            final Object presenceList = entry.get("presence");
            if (presenceList instanceof List<?> presences) {
                if (presences.getFirst() instanceof AbstractConfig presenceEntry) {
                    target = presenceEntry;
                }
            }
        } else {
            target = entry;
        }
        return target;
    }

    private boolean isActive(final ConfigFlag flag) {
        return (configVersion < 0 || configVersion >= flag.configVersion) &&
                (serverEntryVersion < 0 || serverEntryVersion >= flag.serverEntryVersion) &&
                (replayModVersion < 0 || replayModVersion >= flag.replayModVersion);
    }

    private Object getProperty(final UnmodifiableConfig instance, final String... terms) {
        for (String term : terms) {
            if (instance.contains(term)) {
                return instance.get(term);
            }
        }
        return null;
    }

    private enum ConfigFlag {
        USE_IMAGE_POOLS(17, 2, 1),
        USE_MULTI_RPC(24, 3, 1),
        USE_PAUSE_EVENT(25, 3, 1);

        private final int configVersion;
        private final int serverEntryVersion;
        private final int replayModVersion;

        ConfigFlag(int configVersion, int serverEntryVersion, int replayModVersion) {
            this.configVersion = configVersion;
            this.serverEntryVersion = serverEntryVersion;
            this.replayModVersion = replayModVersion;
        }

        @Override
        public String toString() {
            return "ConfigFlag[key=" + (StringUtils.getOrDefault(name(), "N/A")) +
                    "; configVersion=" + configVersion +
                    "; serverEntryVersion=" + serverEntryVersion +
                    "; replayModVersion=" + replayModVersion + "]";
        }
    }
}
