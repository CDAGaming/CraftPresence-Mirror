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

package com.gitlab.cdagaming.craftpresence;

import com.gitlab.cdagaming.craftpresence.config.Config;
import com.gitlab.cdagaming.craftpresence.impl.Pair;
import com.gitlab.cdagaming.craftpresence.utils.*;
import com.gitlab.cdagaming.craftpresence.utils.discord.DiscordUtils;
import com.gitlab.cdagaming.craftpresence.utils.entity.EntityUtils;
import com.gitlab.cdagaming.craftpresence.utils.entity.TileEntityUtils;
import com.gitlab.cdagaming.craftpresence.utils.gui.GuiUtils;
import com.gitlab.cdagaming.craftpresence.utils.server.ServerUtils;
import com.gitlab.cdagaming.craftpresence.utils.world.BiomeUtils;
import com.gitlab.cdagaming.craftpresence.utils.world.DimensionUtils;
import net.minecraft.client.Minecraft;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.util.Session;

import java.util.Timer;
import java.util.TimerTask;

/**
 * The Primary Application Class and Utilities
 *
 * @author CDAGaming
 */
public class CraftPresence {
    /**
     * Whether Pack Data could be found and parsed
     */
    public static boolean packFound = false;

    /**
     * If the Mod is Currently Closing and Clearing Data
     */
    public static boolean closing = false;

    /**
     * Timer Instance for this Class, used for Scheduling Events
     */
    public static Timer timerObj = new Timer(CraftPresence.class.getSimpleName());

    /**
     * The Minecraft Instance attached to this Mod
     */
    public static Minecraft instance;

    /**
     * The Minecraft Instance Session attached to this Mod
     */
    public static Session session;

    /**
     * The Current Player detected from the Minecraft Instance
     */
    public static EntityPlayer player;

    /**
     * The {@link Config} Instance for this Mod
     */
    public static Config CONFIG;

    /**
     * The {@link SystemUtils} Instance for this Mod
     */
    public static SystemUtils SYSTEM = new SystemUtils();

    /**
     * The {@link KeyUtils} Instance for this Mod
     */
    public static KeyUtils KEYBINDINGS = new KeyUtils();

    /**
     * The {@link DiscordUtils} Instance for this Mod
     */
    public static DiscordUtils CLIENT = new DiscordUtils();

    /**
     * The {@link ServerUtils} Instance for this Mod
     */
    public static ServerUtils SERVER = new ServerUtils();

    /**
     * The {@link BiomeUtils} Instance for this Mod
     */
    public static BiomeUtils BIOMES = new BiomeUtils();

    /**
     * The {@link DimensionUtils} Instance for this Mod
     */
    public static DimensionUtils DIMENSIONS = new DimensionUtils();

    /**
     * The {@link EntityUtils} Instance for this Mod
     */
    public static EntityUtils ENTITIES = new EntityUtils();

    /**
     * The {@link TileEntityUtils} Instance for this Mod
     */
    public static TileEntityUtils TILE_ENTITIES = new TileEntityUtils();

    /**
     * The {@link GuiUtils} Instance for this Mod
     */
    public static GuiUtils GUIS = new GuiUtils();

    /**
     * Whether {@link ModUtils#IS_DEV} has been overridden pre-setup
     */
    public static boolean isDevStatusOverridden = false;

    /**
     * Whether {@link ModUtils#IS_VERBOSE} has been overridden pre-setup
     */
    public static boolean isVerboseStatusOverridden = false;
    /**
     * If specified, this callback runs on initial launch, once initialized
     */
    private final Runnable initCallback;
    /**
     * Whether the Mod has completed its Initialization Phase
     */
    private boolean initialized = false;

    /**
     * Begins Scheduling Ticks on Class Initialization
     *
     * @param callback The callback to run upon post-initialization
     */
    public CraftPresence(Runnable callback) {
        this.initCallback = callback;
        scheduleTick();
    }

    /**
     * Begins Scheduling Ticks on Class Initialization
     */
    public CraftPresence() {
        this(null);
    }

    /**
     * The Mod's Initialization Event
     * <p>
     * Consists of Data Initialization and RPC Setup
     */
    private void init() {
        // Initialize Dynamic Mappings
        MappingUtils.getClassMap();

        // If running in Developer Mode, Warn of Possible Issues and Log OS Info
        ModUtils.LOG.debugWarn(ModUtils.TRANSLATOR.translate(true, "craftpresence.logger.warning.debug_mode"));
        ModUtils.LOG.debugInfo(ModUtils.TRANSLATOR.translate(true, "craftpresence.logger.info.os", SYSTEM.OS_NAME, SYSTEM.OS_ARCH, SYSTEM.IS_64_BIT));

        // Check for Updates before continuing
        ModUtils.UPDATER.checkForUpdates(() -> {
            if (ModUtils.UPDATER.isInvalidVersion) {
                // If the Updater found our version to be an invalid one
                // Then replace the Version ID, Name, and Type
                StringUtils.updateField(ModUtils.class, null, new Pair<>("VERSION_ID", "v" + ModUtils.UPDATER.targetVersion));
                StringUtils.updateField(ModUtils.class, null, new Pair<>("VERSION_TYPE", ModUtils.UPDATER.currentState.getDisplayName()));
                StringUtils.updateField(ModUtils.class, null, new Pair<>("NAME", CraftPresence.class.getSimpleName()));

                ModUtils.UPDATER.currentVersion = ModUtils.UPDATER.targetVersion;
                ModUtils.UPDATER.isInvalidVersion = false;
            }
        });

        SYSTEM = new SystemUtils();
        CONFIG = Config.getInstance();

        CommandUtils.init();

        // Synchronize Developer and Verbose Modes with Config Options, if they were not already true
        // If it is true (IE Modified from their Default Value), set the overridden flag to remember later
        if (!ModUtils.IS_DEV) {
            ModUtils.IS_DEV = CONFIG.advancedSettings.debugMode || ModUtils.IS_VERBOSE;
        } else {
            isDevStatusOverridden = true;
        }

        if (!ModUtils.IS_VERBOSE) {
            ModUtils.IS_VERBOSE = CONFIG.advancedSettings.verboseMode;
        } else {
            isVerboseStatusOverridden = true;
        }

        try {
            CLIENT.CLIENT_ID = CONFIG.generalSettings.clientId;
            CLIENT.AUTO_REGISTER = CONFIG.generalSettings.autoRegister;
            CLIENT.setup();
            CLIENT.init(true);
        } catch (Exception ex) {
            ModUtils.LOG.error(ModUtils.TRANSLATOR.translate("craftpresence.logger.error.load"));
            if (ModUtils.IS_VERBOSE) {
                ex.printStackTrace();
            }
        } finally {
            if (initCallback != null) {
                initCallback.run();
            }
            initialized = true;
        }
    }

    /**
     * Schedules the Next Tick to Occur if not currently closing
     */
    private void scheduleTick() {
        if (!closing) {
            timerObj.schedule(
                    new TimerTask() {
                        @Override
                        public void run() {
                            clientTick();
                        }
                    },
                    50
            );
        }
    }

    /**
     * The Event to Run on each Client Tick, if passed initialization events and not closing
     * <p>
     * Consists of Synchronizing Data, and Updating RPC Data as needed
     */
    private void clientTick() {
        if (!closing) {
            instance = Minecraft.getMinecraft();
            if (initialized) {
                session = instance.getSession();
                player = instance.player;
                // Synchronize Developer and Verbose Modes with Config Options, if they were not overridden pre-setup
                ModUtils.IS_DEV = !isDevStatusOverridden ? CONFIG.advancedSettings.debugMode : ModUtils.IS_DEV;
                ModUtils.IS_VERBOSE = !isVerboseStatusOverridden ? CONFIG.advancedSettings.verboseMode : ModUtils.IS_VERBOSE;

                CommandUtils.reloadData(false);
            } else if (instance != null) {
                session = instance.getSession();
                if (session != null) {
                    init();
                }
            }

            scheduleTick();
        }
    }
}
