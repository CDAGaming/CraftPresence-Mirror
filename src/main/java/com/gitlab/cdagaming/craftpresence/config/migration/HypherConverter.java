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

package com.gitlab.cdagaming.craftpresence.config.migration;

import com.gitlab.cdagaming.craftpresence.ModUtils;
import com.gitlab.cdagaming.craftpresence.config.Config;
import com.gitlab.cdagaming.craftpresence.config.element.Button;
import com.gitlab.cdagaming.craftpresence.config.element.ModuleData;
import com.gitlab.cdagaming.craftpresence.config.element.PresenceData;
import com.gitlab.cdagaming.craftpresence.utils.StringUtils;
import com.google.common.collect.ImmutableMap;
import com.google.gson.JsonElement;
import me.hypherionmc.moonconfig.core.AbstractConfig;
import me.hypherionmc.moonconfig.core.file.FileConfig;
import net.minecraft.client.gui.GuiDownloadTerrain;
import net.minecraft.client.gui.GuiMultiplayer;
import net.minecraft.client.gui.GuiScreenRealmsProxy;
import net.minecraft.client.gui.GuiScreenWorking;

import java.io.File;
import java.util.List;
import java.util.Map;

/**
 * Migration from SimpleRPC (Hypherion) Config to our {@link Config} format
 */
@SuppressWarnings({"unchecked", "SameParameterValue"})
public class HypherConverter implements DataMigrator {
    private final int fileVersion;
    private final String configPath, serverEntriesPath, replayModPath;
    private final String EMPTY_QUOTES = "{''}";
    // oldName -> newName
    private final Map<String, String> placeholderMappings = ImmutableMap.<String, String>builder()
            .put("%player%", "{player.name}")
            .put("%world%", "{dimension.name}")
            .put("%mods%", "{general.mods}")
            .put("%difficulty%", "{world.difficulty}")
            .put("%position%", "{custom.player_info_coordinate}")
            .put("%biome%", "{biome.name}")
            .put("%mcver%", "{general.version}")
            .put("%instance%", "{pack.name}")
            .put("%launcher%", "{general.brand}")
            .put("%server%", "{replace(server.address, '.', '_')}")
            .put("%launchername%", "{toLower(general.brand)}")
            .put("%savename%", "{world.name}")
            .put("%playerhead%", "{player.icon}")
            .put("%gametime12%", "{world.time.12}")
            .put("%gametime%", "{world.time.24}")
            .put("%day%", "{world.day}")
            .put("%weather%", EMPTY_QUOTES) // TODO
            .put("%replayframe%", "{replaymod.frames.current}")
            .put("%replaytotal%", "{replaymod.frames.total}")
            .put("%replaytime%", "{replaymod.time.current}")
            .put("%replaytimeleft%", "{replaymod.time.remaining")
            //
            .put("%serverip%", "{server.address}")
            .put("%servername%", "{server.name}")
            .put("%players%", "{server.players.current}")
            .put("%playersexcl%", "{server.players.current.exclude}")
            .put("%maxplayers%", "{server.players.max}")
            .put("%motd%", "{server.motd.raw}")
            .put("%servericon%", "{server.icon}")
            //
            .put("%realmname%", EMPTY_QUOTES) // Realm Event - Unimplemented
            .put("%realmdescription%", EMPTY_QUOTES) // Realm Event - Unimplemented
            .put("%realmgame%", EMPTY_QUOTES) // Realm Event - Unimplemented
            .put("%realmicon%", EMPTY_QUOTES) // Realm Event - Unimplemented
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

    @Override
    public Config apply(Config instance, JsonElement rawJson, Object... args) {
        ModUtils.LOG.info("Simple RPC (By: HypherionSA) config data found, attempting to migrate settings to CraftPresence...");
        try (FileConfig conf = FileConfig.of(configPath)) {
            conf.load();
            configVersion = conf.get("general.version");
            ModUtils.LOG.debugInfo(String.format("Main Config file found (Version: %d, File Version: %d), interpreting data...", configVersion, fileVersion));

            // Main Conversion
            instance.generalSettings.clientId = conf.get("general.applicationID").toString();
            instance.advancedSettings.debugMode = conf.get("general.debugging");
            final boolean launcherIntegration = conf.get("general.launcherIntegration");
            instance.generalSettings.detectCurseManifest = launcherIntegration;
            instance.generalSettings.detectMCUpdaterInstance = launcherIntegration;
            instance.generalSettings.detectTechnicPack = launcherIntegration;
            instance.generalSettings.detectMultiMCManifest = launcherIntegration;

            final boolean areOverridesEnabled = conf.get("dimension_overrides.enabled");
            if (conf.get("dimension_overrides.dimensions") != null) {
                for (AbstractConfig entry : (List<AbstractConfig>) conf.get("dimension_overrides.dimensions")) {
                    String name = entry.get("name").toString();
                    final boolean isBiome = name.startsWith("biome:");
                    if (isBiome) {
                        name = name.replaceFirst("biome:", "");
                    }
                    final ModuleData data = new ModuleData()
                            .setData(convertPresenceData(entry, areOverridesEnabled, true));
                    (isBiome ? instance.biomeSettings.biomeData : instance.dimensionSettings.dimensionData).put(name, data);
                }
            }

            // Custom Variables (Enabled state is ignored)
            if (conf.get("custom.variables") != null) {
                for (AbstractConfig entry : (List<AbstractConfig>) conf.get("custom.variables")) {
                    String name = entry.get("name").toString();
                    String value = entry.get("value").toString();

                    instance.displaySettings.dynamicVariables.put(name, processPlaceholder(value));
                }
            }

            // Per-GUI Events
            instance.advancedSettings.enablePerGui = true;
            instance.advancedSettings.guiSettings.guiData.put(GuiScreenRealmsProxy.class.getSimpleName(), new ModuleData()
                    .setData(convertPresenceData(conf.get("realms_list"))));
            instance.advancedSettings.guiSettings.guiData.put(GuiMultiplayer.class.getSimpleName(), new ModuleData()
                    .setData(convertPresenceData(conf.get("server_list"))));
            instance.advancedSettings.guiSettings.guiData.put(GuiScreenWorking.class.getSimpleName(), new ModuleData()
                    .setData(convertPresenceData(conf.get("join_game"))));
            instance.advancedSettings.guiSettings.guiData.put(GuiDownloadTerrain.class.getSimpleName(), new ModuleData()
                    .setData(convertPresenceData(conf.get("join_game"))));

            instance.statusMessages.loadingData.setData(convertPresenceData(conf.get("init")));
            instance.statusMessages.mainMenuData.setData(convertPresenceData(conf.get("main_menu")));
            instance.statusMessages.singleplayerData.setData(convertPresenceData(conf.get("single_player"), !areOverridesEnabled));
            instance.serverSettings.serverData.get("default").setData(convertPresenceData(conf.get("multi_player"), !areOverridesEnabled));
            instance.displaySettings.presenceData = convertPresenceData(conf.get("generic"));

            instance.save();
        }

        // Server Entries Conversion
        final File serverEntriesFile = new File(serverEntriesPath);
        if (serverEntriesFile.exists()) {
            try (FileConfig conf = FileConfig.of(serverEntriesFile)) {
                conf.load();
                serverEntryVersion = conf.get("version");
                ModUtils.LOG.debugInfo(String.format("Server Entries file found (Version: %d, File Version: %d), interpreting data...", serverEntryVersion, fileVersion));

                final boolean areOverridesEnabled = conf.get("enabled");
                if (conf.get("entry") != null) {
                    for (AbstractConfig entry : (List<AbstractConfig>) conf.get("entry")) {
                        instance.serverSettings.serverData.put(entry.get("ip"), new ModuleData()
                                .setData(convertPresenceData(entry, areOverridesEnabled, true)));
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
                ModUtils.LOG.debugInfo(String.format("Replay Mod Integration file found (Version: %d, File Version: %d), interpreting data...", replayModVersion, fileVersion));

                instance.advancedSettings.enablePerGui = true;
                instance.advancedSettings.guiSettings.guiData.put("GuiReplayViewer", new ModuleData()
                        .setData(convertPresenceData(conf.get("replay_viewer"))));
                instance.advancedSettings.guiSettings.guiData.put("GuiReplayOverlay", new ModuleData()
                        .setData(convertPresenceData(conf.get("replay_editor"))));
                instance.advancedSettings.guiSettings.guiData.put("GuiVideoRenderer", new ModuleData()
                        .setData(convertPresenceData(conf.get("replay_render"))));

                instance.save();
            }
        }

        ModUtils.LOG.info("Migration complete, thanks for using our mods! ~~ CDAGaming and HypherionSA");
        return instance;
    }

    private String processPlaceholder(final String original) {
        String result = original;
        if (!StringUtils.isNullOrEmpty(result)) {
            for (Map.Entry<String, String> entry : placeholderMappings.entrySet()) {
                result = StringUtils.replaceAnyCase(result, entry.getKey(), entry.getValue());
            }
        }
        return result;
    }

    private PresenceData convertPresenceData(final AbstractConfig entry, final boolean isEnabled, final boolean useAsMain, final ConfigFlag... flags) {
        final PresenceData data = new PresenceData();
        data.enabled = isEnabled;
        data.useAsMain = useAsMain;
        data.details = processPlaceholder(entry.get("description"));
        data.gameState = processPlaceholder(entry.get("state"));
        if (isActive(ConfigFlag.USE_IMAGE_POOLS)) {
            // TODO: Implement *full* Image Pool Support
            final List<String> largeImages = entry.get("largeImageKey");
            final List<String> smallImages = entry.get("smallImageKey");
            data.largeImageKey = processPlaceholder(largeImages.get(0));
            data.smallImageKey = processPlaceholder(smallImages.get(0));
        } else {
            data.largeImageKey = processPlaceholder(entry.get("largeImageKey"));
            data.smallImageKey = processPlaceholder(entry.get("smallImageKey"));
        }
        data.largeImageText = processPlaceholder(entry.get("largeImageText"));
        data.smallImageText = processPlaceholder(entry.get("smallImageText"));

        int buttonIndex = 1;
        if (entry.get("buttons") != null) {
            for (AbstractConfig buttonEntry : (List<AbstractConfig>) entry.get("buttons")) {
                final Button buttonData = new Button(
                        processPlaceholder(buttonEntry.get("label")),
                        processPlaceholder(buttonEntry.get("url"))
                );
                data.addButton("button_" + buttonIndex, buttonData);
                buttonIndex++;
            }
        }

        return data;
    }

    private PresenceData convertPresenceData(final AbstractConfig entry, final boolean useAsMain, final ConfigFlag... flags) {
        return convertPresenceData(entry, entry.getOrElse("enabled", true), useAsMain, flags);
    }

    private PresenceData convertPresenceData(final AbstractConfig entry, final ConfigFlag... flags) {
        return convertPresenceData(entry, true, flags);
    }

    private boolean isActive(final ConfigFlag flag) {
        return (configVersion < 0 || configVersion >= flag.configVersion) &&
                (serverEntryVersion < 0 || serverEntryVersion >= flag.serverEntryVersion) &&
                (replayModVersion < 0 || replayModVersion >= flag.replayModVersion);
    }

    private enum ConfigFlag {
        USE_IMAGE_POOLS(17, 2, 1);

        private final int configVersion;
        private final int serverEntryVersion;
        private final int replayModVersion;

        ConfigFlag(int configVersion, int serverEntryVersion, int replayModVersion) {
            this.configVersion = configVersion;
            this.serverEntryVersion = serverEntryVersion;
            this.replayModVersion = replayModVersion;
        }
    }
}
