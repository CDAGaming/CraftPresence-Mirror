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
import com.gitlab.cdagaming.craftpresence.core.Constants;
import com.gitlab.cdagaming.craftpresence.core.utils.*;
import com.gitlab.cdagaming.craftpresence.utils.CommandUtils;
import com.gitlab.cdagaming.craftpresence.utils.KeyUtils;
import com.gitlab.cdagaming.craftpresence.utils.discord.DiscordUtils;
import com.gitlab.cdagaming.craftpresence.utils.entity.EntityUtils;
import com.gitlab.cdagaming.craftpresence.utils.entity.TileEntityUtils;
import com.gitlab.cdagaming.craftpresence.utils.gui.GuiUtils;
import com.gitlab.cdagaming.craftpresence.utils.server.ServerUtils;
import com.gitlab.cdagaming.craftpresence.utils.world.BiomeUtils;
import com.gitlab.cdagaming.craftpresence.utils.world.DimensionUtils;
import edu.umd.cs.findbugs.annotations.SuppressFBWarnings;
import net.minecraft.client.Minecraft;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.util.Session;

/**
 * The Primary Application Class and Utilities
 *
 * @author CDAGaming
 */
@SuppressFBWarnings("MS_CANNOT_BE_FINAL")
public class CraftPresence {
    /**
     * The {@link KeyUtils} Instance for this Mod
     */
    public static final KeyUtils KEYBINDINGS = new KeyUtils();
    /**
     * The {@link DiscordUtils} Instance for this Mod
     */
    public static final DiscordUtils CLIENT = new DiscordUtils();
    /**
     * The {@link ServerUtils} Instance for this Mod
     */
    public static final ServerUtils SERVER = new ServerUtils();
    /**
     * The {@link BiomeUtils} Instance for this Mod
     */
    public static final BiomeUtils BIOMES = new BiomeUtils();
    /**
     * The {@link DimensionUtils} Instance for this Mod
     */
    public static final DimensionUtils DIMENSIONS = new DimensionUtils();
    /**
     * The {@link EntityUtils} Instance for this Mod
     */
    public static final EntityUtils ENTITIES = new EntityUtils();
    /**
     * The {@link TileEntityUtils} Instance for this Mod
     */
    public static final TileEntityUtils TILE_ENTITIES = new TileEntityUtils();
    /**
     * The {@link GuiUtils} Instance for this Mod
     */
    public static final GuiUtils GUIS = new GuiUtils();
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
     * The {@link ScheduleUtils} Instance for this Mod
     */
    public static final ScheduleUtils SCHEDULER = new ScheduleUtils(CommandUtils::onTick);
    /**
     * The {@link Config} Instance for this Mod
     */
    public static Config CONFIG;
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
        initCallback = callback;
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
        // Initialize Dynamic Mappings and Critical Data
        FileUtils.detectClasses();
        MappingUtils.getClassMap();

        // If running in Developer Mode, Warn of Possible Issues and Log OS Info
        Constants.LOG.debugWarn(ModUtils.TRANSLATOR.translate(true, "craftpresence.logger.warning.debug_mode"));
        Constants.LOG.debugInfo(ModUtils.TRANSLATOR.translate(true, "craftpresence.logger.info.os", OSUtils.OS_NAME, OSUtils.OS_ARCH, OSUtils.IS_64_BIT));

        // Check for Updates before continuing
        ModUtils.UPDATER.checkForUpdates();

        CONFIG = Config.getInstance();
        CommandUtils.init();

        CLIENT.setup();
        CommandUtils.setupRPC();

        if (initCallback != null) {
            initCallback.run();
        }
        initialized = true;
    }

    /**
     * Schedules the Next Tick to Occur if not currently closing
     */
    private void scheduleTick() {
        if (!Constants.IS_GAME_CLOSING) {
            Constants.getThreadPool().scheduleAtFixedRate(
                    this::clientTick,
                    0, 50, TimeUtils.getTimeUnitFrom("MILLISECONDS")
            );
        }
    }

    /**
     * The Event to Run on each Client Tick, if passed initialization events and not closing
     * <p>
     * Consists of Synchronizing Data, and Updating RPC Data as needed
     */
    private void clientTick() {
        if (!Constants.IS_GAME_CLOSING) {
            instance = Minecraft.getMinecraft();
            if (initialized) {
                session = instance.getSession();
                player = instance.player;

                CommandUtils.reloadData(false);
            } else if (instance != null) {
                session = instance.getSession();
                if (session != null) {
                    init();
                }
            }
        }
    }
}
