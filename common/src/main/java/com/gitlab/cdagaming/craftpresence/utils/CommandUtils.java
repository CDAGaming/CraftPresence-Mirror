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

package com.gitlab.cdagaming.craftpresence.utils;

import com.gitlab.cdagaming.craftpresence.CraftPresence;
import com.gitlab.cdagaming.craftpresence.ModUtils;
import com.gitlab.cdagaming.craftpresence.config.Config;
import com.gitlab.cdagaming.craftpresence.core.Constants;
import com.gitlab.cdagaming.craftpresence.core.config.element.ModuleData;
import com.gitlab.cdagaming.craftpresence.core.config.element.PresenceData;
import com.gitlab.cdagaming.craftpresence.core.impl.Module;
import com.gitlab.cdagaming.craftpresence.core.integrations.pack.Pack;
import com.gitlab.cdagaming.craftpresence.core.integrations.pack.atlauncher.ATLauncherUtils;
import com.gitlab.cdagaming.craftpresence.core.integrations.pack.curse.CurseUtils;
import com.gitlab.cdagaming.craftpresence.core.integrations.pack.mcupdater.MCUpdaterUtils;
import com.gitlab.cdagaming.craftpresence.core.integrations.pack.modrinth.ModrinthUtils;
import com.gitlab.cdagaming.craftpresence.core.integrations.pack.multimc.MultiMCUtils;
import com.gitlab.cdagaming.craftpresence.core.integrations.pack.technic.TechnicUtils;
import com.gitlab.cdagaming.craftpresence.impl.TranslationManager;
import com.gitlab.cdagaming.craftpresence.integrations.replaymod.ReplayModUtils;
import com.gitlab.cdagaming.craftpresence.utils.gui.RenderUtils;
import com.jagrosh.discordipc.entities.DiscordBuild;
import io.github.cdagaming.unicore.impl.TreeMapBuilder;
import io.github.cdagaming.unicore.utils.FileUtils;
import io.github.cdagaming.unicore.utils.StringUtils;

import java.util.Map;

/**
 * Command Utilities for Synchronizing and Initializing Data
 *
 * @author CDAGaming
 */
public class CommandUtils {
    /**
     * A mapping of currently loaded {@link TranslationManager} instances
     */
    private static final Map<String, TranslationManager> translationManagerList = StringUtils.newHashMap();
    /**
     * A mapping of the currently loaded Rich Presence Modules
     */
    private static final Map<String, Module> modules = new TreeMapBuilder<String, Module>()
            .put("_biome", CraftPresence.BIOMES)
            .put("_dimension", CraftPresence.DIMENSIONS)
            .put("_item", CraftPresence.TILE_ENTITIES)
            .put("_entity", CraftPresence.ENTITIES)
            .put("_server", CraftPresence.SERVER)
            .put("_screen", CraftPresence.GUIS)
            .build();
    /**
     * A mapping of the currently loaded Pack Extension Modules
     */
    private static final Map<String, Pack> packModules = new TreeMapBuilder<String, Pack>()
            .put("atlauncher", new ATLauncherUtils(
                    () -> CraftPresence.CONFIG.generalSettings.detectATLauncherInstance
            ))
            .put("curse", new CurseUtils(
                    () -> CraftPresence.CONFIG.generalSettings.detectCurseManifest
            ))
            .put("multimc", new MultiMCUtils(
                    () -> CraftPresence.CONFIG.generalSettings.detectMultiMCManifest
            ))
            .put("mcupdater", new MCUpdaterUtils(
                    () -> CraftPresence.CONFIG.generalSettings.detectMCUpdaterInstance
            ))
            .put("technic", new TechnicUtils(
                    () -> CraftPresence.CONFIG.generalSettings.detectTechnicPack
            ))
            .put("modrinth", new ModrinthUtils(
                    () -> CraftPresence.CONFIG.generalSettings.detectModrinthPack
            ))
            .build();
    /**
     * The currently loaded pack data
     */
    private static Map.Entry<String, Pack> loadedPack = null;
    /**
     * The currently loaded menu data
     */
    private static ModuleData loadedMenu = null;
    /**
     * The Current {@link MenuStatus} representing where we are at in the load process
     */
    private static MenuStatus status = MenuStatus.None;

    /**
     * Retrieve the current {@link MenuStatus} for this instance
     *
     * @return the current {@link MenuStatus}
     */
    public static MenuStatus getMenuState() {
        return status;
    }

    /**
     * Sets the current {@link MenuStatus} for this instance
     *
     * @param newState the new {@link MenuStatus}
     */
    public static void setMenuState(final MenuStatus newState) {
        final MenuStatus oldState = status;
        status = newState;
        if (oldState != newState) {
            updateMenuPresence();
        }
    }

    /**
     * Reset the {@link MenuStatus} for this instance
     */
    public static void clearMenuState() {
        setMenuState(MenuStatus.None);
    }

    /**
     * Synchronize Presence Data with the current {@link MenuStatus}
     */
    public static void updateMenuPresence() {
        switch (status) {
            case Loading:
                syncMenuData(CraftPresence.CONFIG.statusMessages.loadingData);
                break;
            case MainMenu:
                syncMenuData(CraftPresence.CONFIG.statusMessages.mainMenuData);
                break;
            default:
                clearMenuPresence();
                break;
        }
    }

    /**
     * Determines if this Application is running in a Developer or Debug State
     *
     * @return {@link Boolean#TRUE} if condition is satisfied
     */
    public static boolean isDebugMode() {
        return Constants.IS_DEV_FLAG ||
                isVerboseMode() || (CraftPresence.CONFIG != null && CraftPresence.CONFIG.advancedSettings.debugMode);
    }

    /**
     * Determines if this Application is running in a de-obfuscated or Developer environment
     *
     * @return {@link Boolean#TRUE} if condition is satisfied
     */
    public static boolean isVerboseMode() {
        return Constants.IS_VERBOSE_FLAG ||
                (CraftPresence.CONFIG != null && CraftPresence.CONFIG.advancedSettings.verboseMode);
    }

    /**
     * Synchronizes Module Placeholder Data, meant for RPC usage
     */
    public static void syncModuleArguments() {
        for (String key : modules.keySet()) {
            final String name = (key.startsWith("_") ? "" : "_") + key;
            CraftPresence.CLIENT.syncFunction(name + ".instance", () -> modules.get(key));
        }
    }

    /**
     * Synchronizes the `pack` Arguments, based on any found Launcher Pack/Instance Data
     */
    public static void syncPackArguments() {
        if (loadedPack == null) return;

        final Map.Entry<String, Pack> pack = loadedPack;
        final Pack data = pack.getValue();
        if (!data.hasPackType()) {
            data.setPackType(pack.getKey());
        }

        if (data.hasPackName()) {
            CraftPresence.CLIENT.syncFunction("pack.type", data::getPackType, true);
            CraftPresence.CLIENT.syncFunction("pack.name", data::getPackName, true);
            CraftPresence.CLIENT.syncFunction("pack.icon",
                    () -> CraftPresence.CLIENT.imageOf("pack.icon", true,
                            data.getPackIcon(), data.getPackType())
                    , true);
        }
    }

    /**
     * Adds a module for ticking and RPC Synchronization
     *
     * @param moduleId The name of the module
     * @param instance The instance of the module
     */
    public static void addModule(final String moduleId, final Module instance) {
        if (!CraftPresence.isDataLoaded) {
            modules.put(moduleId, instance);
        }
    }

    /**
     * Adds a module for ticking and RPC Synchronization
     *
     * @param moduleId The name of the module
     * @param instance The instance of the module
     */
    public static void addModule(final String moduleId, final Pack instance) {
        if (!CraftPresence.isDataLoaded) {
            packModules.put(moduleId, instance);
        }
    }

    /**
     * Adds a module for ticking and RPC Synchronization
     *
     * @param moduleId The name of the module
     * @param instance The instance of the module
     */
    public static void addModule(final String moduleId, final TranslationManager instance) {
        translationManagerList.put(moduleId, instance);
    }

    /**
     * Reloads and Synchronizes Data, as needed, and performs onTick Events
     *
     * @param forceUpdateRPC Whether to Force an Update to the RPC Data
     */
    public static void reloadData(final boolean forceUpdateRPC) {
        for (TranslationManager manager : translationManagerList.values()) {
            manager.onTick();
        }
        CraftPresence.SCHEDULER.onTick();
        CraftPresence.instance.addScheduledTask(CraftPresence.KEYBINDINGS::onTick);

        CraftPresence.SCHEDULER.TICK_LOCK.lock();
        try {
            for (Module module : modules.values()) {
                if (module.canBeLoaded()) {
                    module.onTick();
                    if (forceUpdateRPC && module.isInUse()) {
                        module.updatePresence();
                    }
                }
            }
            if (forceUpdateRPC) {
                updateMenuPresence();
            }
            CraftPresence.CLIENT.onTick();
        } catch (Throwable ex) {
            final String messagePrefix = Constants.TRANSLATOR.translate("gui.config.message.editor.message");
            final String verbosePrefix = Constants.TRANSLATOR.translate("craftpresence.logger.error.verbose");

            Constants.LOG.error(Constants.TRANSLATOR.translate("craftpresence.logger.error.module"));
            Constants.LOG.printStackTrace(ex, messagePrefix, verbosePrefix);
            CraftPresence.CLIENT.shutDown();
        } finally {
            CraftPresence.SCHEDULER.TICK_LOCK.unlock();
            CraftPresence.SCHEDULER.postTick();
        }
    }

    /**
     * Restarts and Initializes the RPC Data
     */
    public static void setupRPC() {
        CraftPresence.CLIENT.shutDown();

        CraftPresence.CLIENT.CLIENT_ID = CraftPresence.CONFIG.generalSettings.clientId;
        CraftPresence.CLIENT.AUTO_REGISTER = CraftPresence.CONFIG.generalSettings.autoRegister;
        CraftPresence.CLIENT.PREFERRED_CLIENT = DiscordBuild.from(CraftPresence.CONFIG.generalSettings.preferredClientLevel);
        CraftPresence.CLIENT.UPDATE_TIMESTAMP = CraftPresence.CONFIG.generalSettings.resetTimeOnInit;
        CraftPresence.CLIENT.ALLOW_DUPLICATE_PACKETS = CraftPresence.CONFIG.advancedSettings.allowDuplicatePackets;
        CraftPresence.CLIENT.MAX_CONNECTION_ATTEMPTS = CraftPresence.CONFIG.advancedSettings.maxConnectionAttempts;

        CraftPresence.CLIENT.init();
    }

    /**
     * Initializes Essential Module Data
     */
    public static void init() {
        updateModes();
        setDefaultTooltip();
        setupClassScan(false);

        for (Map.Entry<String, Pack> pack : packModules.entrySet()) {
            final String type = pack.getKey();
            final Pack data = pack.getValue();
            if (data.isEnabled()) {
                Constants.LOG.info(Constants.TRANSLATOR.translate("craftpresence.logger.info.pack.init", type));
                if (data.load()) {
                    Constants.LOG.info(Constants.TRANSLATOR.translate("craftpresence.logger.info.pack.loaded", type, data.getPackName(), data.getPackIcon()));
                    loadedPack = pack;
                    break; // Only iterate until the first pack is found
                } else {
                    Constants.LOG.error(Constants.TRANSLATOR.translate("craftpresence.logger.error.pack", type));
                }
            }
        }
        CraftPresence.KEYBINDINGS.register();

        // Setup Mod Integrations that are not Platform-Dependent
        // Use the loader-specific `setupIntegrations` methods for platform-dependent modules
        if (Constants.hasReplayMod()) {
            addModule("integration.replaymod", new ReplayModUtils());
        }
    }

    /**
     * The Event to Run on each Scheduled Tick, if passed postTick events
     * <p>
     * Consists of Synchronizing Data, and Updating related Data as needed
     */
    public static void onTick() {
        if (!Constants.HAS_GAME_LOADED) {
            Constants.HAS_GAME_LOADED = CraftPresence.instance.currentScreen != null || CraftPresence.player != null;
            if (Constants.HAS_GAME_LOADED) {
                addModule(Constants.MOD_ID, new TranslationManager(
                        Constants.TRANSLATOR
                                .setStripColors(
                                        CraftPresence.CONFIG != null && CraftPresence.CONFIG.accessibilitySettings.stripTranslationColors
                                )
                                .setStripFormatting(
                                        CraftPresence.CONFIG != null && CraftPresence.CONFIG.accessibilitySettings.stripTranslationFormatting
                                )
                ));
                if (ModUtils.RAW_TRANSLATOR != null) {
                    addModule("minecraft", new TranslationManager(ModUtils.RAW_TRANSLATOR));
                }
            }
        }
        CraftPresence.CLIENT.updatePresence();
    }

    /**
     * Synchronize Data for the Logging Engine and IPC Instance
     */
    public static void updateModes() {
        Constants.LOG.setDebugMode(isVerboseMode());
        if (CraftPresence.CLIENT.isAvailable()) {
            CraftPresence.CLIENT.ipcInstance.setDebugMode(isDebugMode());
            CraftPresence.CLIENT.ipcInstance.setVerboseLogging(isVerboseMode());
        }
        if (CraftPresence.CONFIG != null) {
            CraftPresence.SCHEDULER.setRefreshRate(CraftPresence.CONFIG.advancedSettings.refreshRate);
        }
    }

    /**
     * Refresh Dynamic Variable Data, removing any data no longer in-play
     *
     * @param oldData The old data to interpret
     */
    public static void syncDynamicVariables(final Map<String, String> oldData) {
        CraftPresence.CLIENT.syncDynamicVariables(oldData, CraftPresence.CONFIG.displaySettings.dynamicVariables);
    }

    /**
     * Synchronize Data for Rendering Tooltips, using config data
     */
    public static void setDefaultTooltip() {
        RenderUtils.setDefaultTooltip(
                CraftPresence.CONFIG.accessibilitySettings.renderTooltips,
                CraftPresence.CONFIG.accessibilitySettings.tooltipBackground,
                CraftPresence.CONFIG.accessibilitySettings.tooltipBorder
        );
    }

    public static void setupClassScan(final boolean postLaunch) {
        final boolean oldState = FileUtils.isClassGraphEnabled();
        final boolean newState = CraftPresence.CONFIG.advancedSettings.enableClassGraph;
        FileUtils.setClassGraphEnabled(newState);
        if (oldState != newState) {
            if (newState) {
                FileUtils.detectClasses();
                if (postLaunch) {
                    // Ensure all Modules trigger a new internal scan
                    // if we are re-populating the Class Map Data
                    // after the game has launched
                    for (Module module : modules.values()) {
                        module.queueInternalScan();
                    }
                }
            } else if (FileUtils.hasScannedClasses()) {
                FileUtils.clearClassMap(true);
            }
        }
    }

    /**
     * Synchronizes RPC Data related to the current Menu Module that's Active
     *
     * @param currentData the current Menu {@link ModuleData}
     */
    public static void syncMenuData(final ModuleData currentData) {
        if (loadedMenu == null) {
            CraftPresence.CLIENT.syncFunction("menu.message", () ->
                    Config.isValidProperty(loadedMenu, "textOverride") ? loadedMenu.getTextOverride() : ""
            );
            CraftPresence.CLIENT.syncFunction("menu.icon", () -> {
                final String currentIcon = Config.isValidProperty(currentData, "iconOverride") ? currentData.getIconOverride() : CraftPresence.CONFIG.generalSettings.defaultIcon;
                return CraftPresence.CLIENT.imageOf("menu.icon", true, currentIcon);
            });
            CraftPresence.CLIENT.addForcedData("menu", () -> (PresenceData) Config.getProperty(loadedMenu, "data"));
        }
        loadedMenu = currentData;

        CraftPresence.CLIENT.clearPartyData();
    }

    /**
     * Clear the Menu Presence Data, derived from the Loading and Main Menu Events
     */
    public static void clearMenuPresence() {
        if (loadedMenu != null) {
            CraftPresence.CLIENT.removeForcedData("menu");
            CraftPresence.CLIENT.removeArguments("menu");
            loadedMenu = null;
        }
    }

    /**
     * Constants representing various Menu Status Levels,
     * such as MainMenu or Loading
     */
    public enum MenuStatus {
        /**
         * Constant for the "MainMenu" Status Level.
         */
        MainMenu,
        /**
         * Constant for the "Loading" Status Level.
         */
        Loading,
        /**
         * Constant for the "None" Status Level.
         */
        None
    }
}
