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

import java.util.List;
import java.util.Map;

@SuppressWarnings("unchecked")
public class HypherConverter implements DataMigrator {
    private final int fileVersion;
    private final String configPath;
    // oldName -> newName
    private final Map<String, String> placeholderMappings = ImmutableMap.<String, String>builder()
            .put("%player%", "&IGN:NAME&")
            .put("%world%", "&DIMENSION:DIMENSION&")
            .put("%mods%", "&MODS:MODCOUNT&")
            .put("%difficulty%", "&SERVER:WORLDINFO:DIFFICULTY&")
            .put("%position%", "&SERVER:PLAYERINFO:COORDS&")
            .put("%biome%", "&BIOME:BIOME&")
            .put("%mcver%", "&MCVERSION&")
            .put("%instance%", "&PACK:NAME&")
            .put("%launcher%", "&BRAND&")
            .put("%server%", "&unknown&") // TODO
            .put("%launchername%", "&BRAND&") // Lower-case?
            .put("%savename%", "&SERVER:WORLDINFO:WORLDNAME&")
            .put("%playerhead%", "&unknown&") // TODO
            .put("%gametime12%", "&SERVER:WORLDINFO:WORLDTIME12&")
            .put("%gametime%", "&SERVER:WORLDINFO:WORLDTIME&")
            .put("%day%", "&SERVER:WORLDINFO:WORLDDAY&")
            .put("%weather%", "&unknown&") // TODO
            .put("%replayframe%", "&unknown&") // TODO
            .put("%replaytotal%", "&unknown&") // TODO
            .put("%replaytime%", "&unknown&") // TODO
            .put("%replaytimeleft%", "&unknown&") // TODO
            //
            .put("%serverip%", "&SERVER:IP&")
            .put("%servername%", "&SERVER:NAME&")
            .put("%players%", "&SERVER:PLAYERS:CURRENT&")
            .put("%playersexcl%", "&unknown&") // TODO
            .put("%maxplayers%", "&SERVER:PLAYERS:MAX&")
            .put("%motd%", "&SERVER:MOTD&")
            .put("%servericon%", "&unknown&") // TODO
            //
            .put("%realmname%", "&unknown&") // TODO
            .put("%realmdescription%", "&unknown&") // TODO
            .put("%realmgame%", "&unknown&") // TODO
            .put("%realmicon%", "&unknown&") // TODO
            .build();
    private int configVersion;

    public HypherConverter(Map.Entry<Integer, String> entry) {
        this.fileVersion = entry.getKey();
        this.configPath = entry.getValue();
    }

    @Override
    public Config apply(Config instance, JsonElement rawJson, Object... args) {
        try (FileConfig conf = FileConfig.of(configPath)) {
            conf.load();
            configVersion = conf.get("general.version");
            ModUtils.LOG.info(String.format("Migrating Simple RPC (Version: %d) settings to CraftPresence...", configVersion));
            ModUtils.LOG.info("Thanks for using our mods! ~~ CDAGaming and HypherionSA");

            // Main Conversion
            instance.generalSettings.clientId = conf.get("general.applicationID").toString();
            instance.advancedSettings.debugMode = conf.get("general.debugging");
            boolean launcherIntegration = conf.get("general.launcherIntegration");
            instance.generalSettings.detectCurseManifest = launcherIntegration;
            instance.generalSettings.detectMCUpdaterInstance = launcherIntegration;
            instance.generalSettings.detectTechnicPack = launcherIntegration;
            instance.generalSettings.detectMultiMCManifest = launcherIntegration;

            boolean isOverridesEnabled = conf.get("dimension_overrides.enabled");
            if (conf.get("dimension_overrides.dimensions") != null) {
                for (AbstractConfig entry : (List<AbstractConfig>) conf.get("dimension_overrides.dimensions")) {
                    String name = entry.get("name").toString();
                    final boolean isBiome = name.startsWith("biome:");
                    if (isBiome) {
                        name = name.replaceFirst("biome:", "");
                    }
                    final ModuleData data = new ModuleData();
                    data.setData(convertPresenceData(entry, isOverridesEnabled, true));
                    (isBiome ? instance.biomeSettings.biomeData : instance.dimensionSettings.dimensionData).put(name, data);
                }
            }

            instance.statusMessages.loadingData.setData(convertPresenceData(conf.get("init")));
            instance.statusMessages.mainMenuData.setData(convertPresenceData(conf.get("main_menu")));
            instance.statusMessages.singleplayerData.setData(convertPresenceData(conf.get("single_player"), !isOverridesEnabled));
            instance.serverSettings.serverData.get("default").setData(convertPresenceData(conf.get("multi_player"), !isOverridesEnabled));
            instance.displaySettings.presenceData = convertPresenceData(conf.get("generic"));

            instance.save();
        }
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

    private PresenceData convertPresenceData(final AbstractConfig entry, final boolean isEnabled, final boolean useAsMain) {
        final PresenceData data = new PresenceData();
        data.enabled = isEnabled;
        data.useAsMain = useAsMain;
        data.details = processPlaceholder(entry.get("description"));
        data.gameState = processPlaceholder(entry.get("state"));
        if (configVersion < 17) {
            data.largeImageKey = processPlaceholder(entry.get("largeImageKey"));
            data.smallImageKey = processPlaceholder(entry.get("smallImageKey"));
        } else {
            // TODO: Implement Image Pool Support
        }
        data.largeImageText = processPlaceholder(entry.get("largeImageText"));
        data.smallImageText = processPlaceholder(entry.get("smallImageText"));

        int buttonIndex = 1;
        for (AbstractConfig buttonEntry : (List<AbstractConfig>) entry.get("buttons")) {
            final Button buttonData = new Button(
                    processPlaceholder(buttonEntry.get("label")),
                    processPlaceholder(buttonEntry.get("url"))
            );
            data.addButton("button_" + buttonIndex, buttonData);
            buttonIndex++;
        }

        return data;
    }

    private PresenceData convertPresenceData(final AbstractConfig entry, final boolean useAsMain) {
        return convertPresenceData(entry, entry.getOrElse("enabled", true), useAsMain);
    }

    private PresenceData convertPresenceData(final AbstractConfig entry) {
        return convertPresenceData(entry, true);
    }
}
