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

package com.gitlab.cdagaming.craftpresence.utils;

import com.gitlab.cdagaming.craftpresence.CraftPresence;
import com.gitlab.cdagaming.craftpresence.ModUtils;
import com.gitlab.cdagaming.craftpresence.config.Config;
import com.gitlab.cdagaming.craftpresence.core.Constants;
import com.gitlab.cdagaming.craftpresence.core.config.element.ModuleData;
import com.gitlab.cdagaming.craftpresence.core.impl.Module;
import com.gitlab.cdagaming.craftpresence.core.impl.TreeMapBuilder;
import com.gitlab.cdagaming.craftpresence.core.integrations.pack.Pack;
import com.gitlab.cdagaming.craftpresence.core.integrations.pack.atlauncher.ATLauncherUtils;
import com.gitlab.cdagaming.craftpresence.core.integrations.pack.curse.CurseUtils;
import com.gitlab.cdagaming.craftpresence.core.integrations.pack.mcupdater.MCUpdaterUtils;
import com.gitlab.cdagaming.craftpresence.core.integrations.pack.multimc.MultiMCUtils;
import com.gitlab.cdagaming.craftpresence.core.integrations.pack.technic.TechnicUtils;
import com.gitlab.cdagaming.craftpresence.core.utils.FileUtils;
import com.gitlab.cdagaming.craftpresence.core.utils.StringUtils;
import com.gitlab.cdagaming.craftpresence.impl.TranslationManager;
import com.gitlab.cdagaming.craftpresence.integrations.replaymod.ReplayModUtils;
import com.jagrosh.discordipc.entities.DiscordBuild;

import java.util.List;
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
            .build();
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
        for (Map.Entry<String, Module> module : modules.entrySet()) {
            String name = module.getKey();
            name = (name.startsWith("_") ? "" : "_") + name;
            CraftPresence.CLIENT.syncArgument(name + ".instance", module.getValue());
        }
    }

    /**
     * Synchronizes the `pack` Arguments, based on any found Launcher Pack/Instance Data
     */
    public static void syncPackArguments() {
        boolean foundPack = false;
        for (Map.Entry<String, Pack> pack : packModules.entrySet()) {
            final Pack data = pack.getValue();
            if (!data.hasPackType()) {
                data.setPackType(pack.getKey());
            }

            if (data.isEnabled() && data.hasPackName()) {
                CraftPresence.CLIENT.syncArgument("pack.type", data.getPackType());
                CraftPresence.CLIENT.syncArgument("pack.name", data.getPackName());
                CraftPresence.CLIENT.syncArgument("pack.icon",
                        CraftPresence.CLIENT.imageOf("pack.icon", true,
                                data.getPackIcon(), data.getPackType())
                );

                foundPack = true;
                break;
            }
        }

        if (!foundPack) {
            CraftPresence.CLIENT.removeArguments("pack");
        }
    }

    /**
     * Adds a module for ticking and RPC Syncronization
     *
     * @param moduleId The name of the module
     * @param instance The instance of the module
     */
    public static void addModule(final String moduleId, final Module instance) {
        modules.put(moduleId, instance);
    }

    /**
     * Adds a module for ticking and RPC Syncronization
     *
     * @param moduleId The name of the module
     * @param instance The instance of the module
     */
    public static void addModule(final String moduleId, final Pack instance) {
        packModules.put(moduleId, instance);
    }

    /**
     * Adds a module for ticking and RPC Syncronization
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
            final List<String> splitEx = StringUtils.splitTextByNewLine(StringUtils.getStackTrace(ex));
            final String messagePrefix = Constants.TRANSLATOR.translate("gui.config.message.editor.message");

            Constants.LOG.error(Constants.TRANSLATOR.translate("craftpresence.logger.error.module"));
            if (Constants.LOG.isDebugMode()) {
                Constants.LOG.error(messagePrefix);
                Constants.LOG.error(ex);
            } else {
                Constants.LOG.error("%1$s \"%2$s\"", messagePrefix, splitEx.get(0));
                if (splitEx.size() > 1) {
                    Constants.LOG.error(Constants.TRANSLATOR.translate("craftpresence.logger.error.verbose"));
                }
            }
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
        for (Map.Entry<String, Pack> pack : packModules.entrySet()) {
            final String type = pack.getKey();
            final Pack data = pack.getValue();
            if (data.isEnabled()) {
                Constants.LOG.info(Constants.TRANSLATOR.translate("craftpresence.logger.info.pack.init", type));
                if (data.load()) {
                    Constants.LOG.info(Constants.TRANSLATOR.translate("craftpresence.logger.info.pack.loaded", type, data.getPackName(), data.getPackIcon()));
                    break; // Only iterate until the first pack is found
                } else {
                    Constants.LOG.error(Constants.TRANSLATOR.translate("craftpresence.logger.error.pack", type));
                }
            }
        }
        CraftPresence.KEYBINDINGS.register();

        // Setup Mod Integrations that are not Platform-Dependent
        // Use the loader-specific `setupIntegrations` methods for platform-dependent modules
        if (FileUtils.findValidClass("com.replaymod.core.ReplayMod") != null) {
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
                addModule(Constants.MOD_ID, new TranslationManager(Constants.TRANSLATOR.setStripColors(
                        CraftPresence.CONFIG != null && CraftPresence.CONFIG.accessibilitySettings.stripTranslationColors
                )));
                addModule("minecraft", new TranslationManager(ModUtils.RAW_TRANSLATOR));
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
        CraftPresence.SCHEDULER.setRefreshRate(CraftPresence.CONFIG.advancedSettings.refreshRate);
    }

    /**
     * Synchronizes RPC Data related to the current Menu Module that's Active
     *
     * @param currentData the current Menu {@link ModuleData}
     */
    public static void syncMenuData(final ModuleData currentData) {
        final String currentMessage = Config.isValidProperty(currentData, "textOverride") ? currentData.getTextOverride() : "";
        final String currentIcon = Config.isValidProperty(currentData, "iconOverride") ? currentData.getIconOverride() : CraftPresence.CONFIG.generalSettings.defaultIcon;
        final String formattedIcon = CraftPresence.CLIENT.imageOf("menu.icon", true, currentIcon);

        CraftPresence.CLIENT.clearPartyData();
        CraftPresence.CLIENT.syncOverride(currentData, "menu.message", "menu.icon");
        CraftPresence.CLIENT.syncArgument("menu.message", currentMessage);
        CraftPresence.CLIENT.syncArgument("menu.icon", formattedIcon);
    }

    /**
     * Clear the Menu Presence Data, derived from the Loading and Main Menu Events
     */
    public static void clearMenuPresence() {
        CraftPresence.CLIENT.clearOverride("menu.message", "menu.icon");
        CraftPresence.CLIENT.removeArguments("menu");
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
