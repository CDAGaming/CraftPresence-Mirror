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

package com.gitlab.cdagaming.craftpresence;

import com.gitlab.cdagaming.craftpresence.config.Config;
import com.gitlab.cdagaming.craftpresence.impl.Tuple;
import com.gitlab.cdagaming.craftpresence.impl.discord.ArgumentType;
import com.gitlab.cdagaming.craftpresence.impl.discord.DiscordStatus;
import com.gitlab.cdagaming.craftpresence.utils.CommandUtils;
import com.gitlab.cdagaming.craftpresence.utils.KeyUtils;
import com.gitlab.cdagaming.craftpresence.utils.StringUtils;
import com.gitlab.cdagaming.craftpresence.utils.SystemUtils;
import com.gitlab.cdagaming.craftpresence.utils.discord.DiscordUtils;
import com.gitlab.cdagaming.craftpresence.utils.entity.EntityUtils;
import com.gitlab.cdagaming.craftpresence.utils.entity.TileEntityUtils;
import com.gitlab.cdagaming.craftpresence.utils.gui.GuiUtils;
import com.gitlab.cdagaming.craftpresence.utils.server.ServerUtils;
import com.gitlab.cdagaming.craftpresence.utils.world.BiomeUtils;
import com.gitlab.cdagaming.craftpresence.utils.world.DimensionUtils;
import com.jagrosh.discordipc.IPCClient;
import net.minecraft.client.Minecraft;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.util.Session;
import net.minecraftforge.fml.common.Mod;

import java.lang.reflect.Modifier;
import java.util.Timer;
import java.util.TimerTask;

/**
 * The Primary Application Class and Utilities
 *
 * @author CDAGaming
 */
@SuppressWarnings("ConstantConditions")
@Mod(modid = ModUtils.MOD_ID, name = "@MOD_NAME@", version = "@VERSION_ID@", clientSideOnly = true, guiFactory = ModUtils.GUI_FACTORY, canBeDeactivated = true, updateJSON = ModUtils.UPDATE_JSON, acceptedMinecraftVersions = "*")
public class CraftPresence {
    /**
     * Whether Pack Data was able to be Found and Parsed
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
     * Whether the Mod has completed it's Initialization Phase
     */
    private boolean initialized = false;

    /**
     * Begins Scheduling Ticks on Class Initialization
     */
    public CraftPresence() {
        scheduleTick();
    }

    /**
     * The Mod's Initialization Event
     * <p>
     * Comprises of Data Initialization and RPC Setup
     */
    private void init() {
        // If running in Developer Mode, Warn of Possible Issues and Log OS Info
        ModUtils.LOG.debugWarn(ModUtils.TRANSLATOR.translate(true, "craftpresence.logger.warning.debug_mode"));
        ModUtils.LOG.debugInfo(ModUtils.TRANSLATOR.translate(true, "craftpresence.logger.info.os", SYSTEM.OS_NAME, SYSTEM.OS_ARCH, SYSTEM.IS_64_BIT));

        // Check for Updates before continuing
        ModUtils.UPDATER.checkForUpdates(() -> {
            if (ModUtils.UPDATER.isInvalidVersion) {
                // If the Updater found our version to be an invalid one
                // Then replace the Version ID, Name, and Type
                StringUtils.updateField(ModUtils.class, null, new Tuple<>("VERSION_ID", "v" + ModUtils.UPDATER.targetVersion, ~Modifier.FINAL));
                StringUtils.updateField(ModUtils.class, null, new Tuple<>("VERSION_TYPE", ModUtils.UPDATER.currentState.getDisplayName(), ~Modifier.FINAL));
                StringUtils.updateField(ModUtils.class, null, new Tuple<>("VERSION_LABEL", ModUtils.UPDATER.currentState.getDisplayName(), ~Modifier.FINAL));
                StringUtils.updateField(ModUtils.class, null, new Tuple<>("NAME", CraftPresence.class.getSimpleName(), ~Modifier.FINAL));

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
            ex.printStackTrace();
        } finally {
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
     * Comprises of Synchronizing Data, and Updating RPC Data as needed
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

                if (!CONFIG.hasChanged) {
                    if (!SYSTEM.HAS_LOADED) {
                        // Ensure Loading Presence has already passed, before any other type of presence displays
                        CommandUtils.setLoadingPresence();
                    } else if (!CommandUtils.isInMainMenu && (!DIMENSIONS.isInUse && !BIOMES.isInUse && !TILE_ENTITIES.isInUse && !ENTITIES.isInUse && !SERVER.isInUse)) {
                        CommandUtils.setMainMenuPresence();
                    } else if (player != null && (CommandUtils.isLoadingGame || CommandUtils.isInMainMenu)) {
                        CommandUtils.isInMainMenu = false;
                        CommandUtils.isLoadingGame = false;
                        CLIENT.initArgument(ArgumentType.Text, "&MAINMENU&");
                        CLIENT.initArgument(ArgumentType.Image, "&MAINMENU&");
                    }

                    if (SYSTEM.HAS_LOADED) {
                        if (CLIENT.awaitingReply && SYSTEM.TIMER == 0) {
                            StringUtils.sendMessageToPlayer(player, ModUtils.TRANSLATOR.translate("craftpresence.command.request.ignored", CLIENT.REQUESTER_USER.getName()));
                            CLIENT.ipcInstance.respondToJoinRequest(CLIENT.REQUESTER_USER, IPCClient.ApprovalMode.DENY);
                            CLIENT.awaitingReply = false;
                            CLIENT.STATUS = DiscordStatus.Ready;
                        } else if (!CLIENT.awaitingReply && CLIENT.REQUESTER_USER != null) {
                            CLIENT.REQUESTER_USER = null;
                            CLIENT.STATUS = DiscordStatus.Ready;
                        }
                    }
                }
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
