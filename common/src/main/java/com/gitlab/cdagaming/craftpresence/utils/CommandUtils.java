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

package com.gitlab.cdagaming.craftpresence.utils;

import com.gitlab.cdagaming.craftpresence.CraftPresence;
import com.gitlab.cdagaming.craftpresence.ModUtils;
import com.gitlab.cdagaming.craftpresence.config.Config;
import com.gitlab.cdagaming.craftpresence.config.element.ModuleData;
import com.gitlab.cdagaming.craftpresence.impl.Module;
import com.gitlab.cdagaming.craftpresence.integrations.curse.CurseUtils;
import com.gitlab.cdagaming.craftpresence.integrations.mcupdater.MCUpdaterUtils;
import com.gitlab.cdagaming.craftpresence.integrations.multimc.MultiMCUtils;
import com.gitlab.cdagaming.craftpresence.integrations.technic.TechnicUtils;
import com.gitlab.cdagaming.craftpresence.utils.discord.assets.DiscordAssetUtils;
import com.jagrosh.discordipc.entities.DiscordBuild;

import java.util.TreeMap;

/**
 * Command Utilities for Synchronizing and Initializing Data
 *
 * @author CDAGaming
 */
public class CommandUtils {
    /**
     * Whether you are on the Main Menu in Minecraft
     */
    public static boolean isInMainMenu = false;

    /**
     * Whether you are on the Loading Stage in Minecraft
     */
    public static boolean isLoadingGame = false;

    /**
     * A mapping of the currently loaded Rich Presence Modules
     */
    public static TreeMap<String, Module> modules = new TreeMap<String, Module>() {
        private static final long serialVersionUID = 510350212503123679L;

        {
            put("_biome", CraftPresence.BIOMES);
            put("_dimension", CraftPresence.DIMENSIONS);
            put("_item", CraftPresence.TILE_ENTITIES);
            put("_entity", CraftPresence.ENTITIES);
            put("_server", CraftPresence.SERVER);
            put("_screen", CraftPresence.GUIS);
        }
    };

    /**
     * Reloads and Synchronizes Data, as needed, and performs onTick Events
     *
     * @param forceUpdateRPC Whether to Force an Update to the RPC Data
     */
    public static void reloadData(final boolean forceUpdateRPC) {
        ModUtils.TRANSLATOR.onTick();
        if (forceUpdateRPC) {
            ModUtils.TRANSLATOR.syncTranslations();
        }
        CraftPresence.SYSTEM.onTick();
        CraftPresence.instance.addScheduledTask(() -> CraftPresence.KEYBINDINGS.onTick());

        for (Module module : modules.values()) {
            if (module.canBeLoaded()) {
                module.onTick();
                if (forceUpdateRPC && module.isInUse()) {
                    module.updatePresence();
                }
            }
        }
    }

    /**
     * Restarts and Initializes the RPC Data
     *
     * @param flushOverride Whether to refresh RPC assets
     */
    public static void rebootRPC(boolean flushOverride) {
        flushOverride = flushOverride || !CraftPresence.CLIENT.CLIENT_ID.equals(
                CraftPresence.CONFIG.generalSettings.clientId
        );
        CraftPresence.CLIENT.shutDown();

        if (flushOverride) {
            DiscordAssetUtils.emptyData();
            CraftPresence.CLIENT.CLIENT_ID = CraftPresence.CONFIG.generalSettings.clientId;
        } else {
            DiscordAssetUtils.clearClientData();
        }
        if (!CraftPresence.CLIENT.PREFERRED_CLIENT.equals(DiscordBuild.from(CraftPresence.CONFIG.generalSettings.preferredClientLevel))) {
            CraftPresence.CLIENT.PREFERRED_CLIENT = DiscordBuild.from(CraftPresence.CONFIG.generalSettings.preferredClientLevel);
        }
        DiscordAssetUtils.loadAssets(CraftPresence.CONFIG.generalSettings.clientId, true);
        CraftPresence.CLIENT.init(CraftPresence.CONFIG.generalSettings.resetTimeOnInit);
    }

    /**
     * Restarts and Initializes the RPC Data
     */
    public static void rebootRPC() {
        rebootRPC(false);
    }

    /**
     * Initializes Essential Data<p>
     * (In this case, Pack Data and Available RPC Icons)
     */
    public static void init() {
        if (CraftPresence.CONFIG.generalSettings.detectCurseManifest && !CraftPresence.packFound) {
            CurseUtils.loadManifest();
        }
        if (CraftPresence.CONFIG.generalSettings.detectMultiMCManifest && !CraftPresence.packFound) {
            MultiMCUtils.loadInstance();
        }
        if (CraftPresence.CONFIG.generalSettings.detectMCUpdaterInstance && !CraftPresence.packFound) {
            MCUpdaterUtils.loadInstance();
        }
        if (CraftPresence.CONFIG.generalSettings.detectTechnicPack && !CraftPresence.packFound) {
            TechnicUtils.loadPack();
        }
        DiscordAssetUtils.loadAssets(CraftPresence.CONFIG.generalSettings.clientId, true);

        CraftPresence.KEYBINDINGS.register();
    }

    /**
     * Synchronizes RPC Data towards that of being in a Loading State
     */
    public static void setLoadingPresence() {
        final ModuleData currentData = CraftPresence.CONFIG.statusMessages.loadingData;
        final String currentMessage = Config.isValidProperty(currentData, "textOverride") ? currentData.getTextOverride() : "";
        final String currentIcon = Config.isValidProperty(currentData, "iconOverride") ? currentData.getIconOverride() : CraftPresence.CONFIG.generalSettings.defaultIcon;

        CraftPresence.CLIENT.clearPartyData(true, false);
        CraftPresence.CLIENT.syncOverride(currentData, "menu.message", "menu.icon");
        CraftPresence.CLIENT.syncArgument("menu.message", currentMessage);
        CraftPresence.CLIENT.syncArgument("menu.icon", CraftPresence.CLIENT.imageOf("menu.icon", true, currentIcon));

        isLoadingGame = true;
    }

    /**
     * Synchronizes RPC Data towards that of being in the Main Menu
     */
    public static void setMainMenuPresence() {
        // Clear Loading Game State, if applicable
        if (isLoadingGame) {
            clearMenuPresence();
            isLoadingGame = false;
        }

        final ModuleData currentData = CraftPresence.CONFIG.statusMessages.mainMenuData;
        final String currentMessage = Config.isValidProperty(currentData, "textOverride") ? currentData.getTextOverride() : "";
        final String currentIcon = Config.isValidProperty(currentData, "iconOverride") ? currentData.getIconOverride() : CraftPresence.CONFIG.generalSettings.defaultIcon;

        CraftPresence.CLIENT.syncOverride(currentData, "menu.message", "menu.icon");
        CraftPresence.CLIENT.syncArgument("menu.message", currentMessage);
        CraftPresence.CLIENT.syncArgument("menu.icon", CraftPresence.CLIENT.imageOf("menu.icon", true, currentIcon));

        isInMainMenu = true;
    }

    /**
     * Clear the Initial Presence Data set from the Loading and Main Menu Events
     */
    public static void clearInitialPresence() {
        isInMainMenu = false;
        isLoadingGame = false;
        clearMenuPresence();
    }

    /**
     * Clear the Menu Presence Data, derived from the Loading and Main Menu Events
     */
    public static void clearMenuPresence() {
        CraftPresence.CLIENT.clearOverride("menu.message", "menu.icon");
        CraftPresence.CLIENT.removeArguments("menu");
    }
}
