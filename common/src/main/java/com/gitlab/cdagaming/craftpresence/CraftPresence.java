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

package com.gitlab.cdagaming.craftpresence;

import com.gitlab.cdagaming.craftpresence.core.Constants;
import com.gitlab.cdagaming.craftpresence.core.config.Config;
import com.gitlab.cdagaming.craftpresence.core.integrations.discord.DiscordUtils;
import com.gitlab.cdagaming.craftpresence.utils.CommandUtils;
import com.gitlab.cdagaming.craftpresence.utils.entity.EntityUtils;
import com.gitlab.cdagaming.craftpresence.utils.entity.TileEntityUtils;
import com.gitlab.cdagaming.craftpresence.utils.gui.GuiUtils;
import com.gitlab.cdagaming.craftpresence.utils.server.ServerUtils;
import com.gitlab.cdagaming.craftpresence.utils.world.BiomeUtils;
import com.gitlab.cdagaming.craftpresence.utils.world.DimensionUtils;
import com.gitlab.cdagaming.unilib.ModUtils;
import com.gitlab.cdagaming.unilib.UniLib;
import com.gitlab.cdagaming.unilib.core.CoreUtils;
import com.gitlab.cdagaming.unilib.core.utils.ModUpdaterUtils;
import com.gitlab.cdagaming.unilib.utils.GameUtils;
import com.gitlab.cdagaming.unilib.utils.KeyUtils;
import com.gitlab.cdagaming.unilib.utils.WorldUtils;
import edu.umd.cs.findbugs.annotations.SuppressFBWarnings;
import io.github.cdagaming.unicore.utils.OSUtils;
import io.github.cdagaming.unicore.utils.ScheduleUtils;
import net.minecraft.client.Minecraft;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.util.Session;
import net.minecraft.world.World;

/**
 * The Primary Application Class and Utilities
 *
 * @author CDAGaming
 */
@SuppressFBWarnings("MS_CANNOT_BE_FINAL")
public class CraftPresence {
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
     * The Application's Instance of {@link ModUpdaterUtils} for Retrieving if the Application has an update
     */
    public static final ModUpdaterUtils UPDATER = new ModUpdaterUtils(
            Constants.MOD_ID,
            Constants.UPDATE_JSON,
            Constants.VERSION_ID,
            ModUtils.MCVersion
    );
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
     * The Current World detected from the Minecraft Instance
     */
    public static World world;
    /**
     * The current player username
     */
    public static String username;
    /**
     * The current player UUID
     */
    public static String uuid;
    /**
     * The {@link Config} Instance for this Mod
     */
    public static Config CONFIG;
    /**
     * The {@link KeyUtils} Instance for this Mod
     */
    public static final KeyUtils KEYBINDINGS = new KeyUtils()
            .setCanCheckKeys(() -> CONFIG != null)
            .setCanSyncKeys(() -> !CONFIG.hasChanged());
    /**
     * The {@link ScheduleUtils} Instance for this Mod
     */
    public static final ScheduleUtils SCHEDULER = new ScheduleUtils(CommandUtils::onTick);
    /**
     * Whether module data for the Mod has completed its Initialization Phase
     */
    public static boolean isDataLoaded = false;
    /**
     * Whether the Mod has completed its Initialization Phase
     */
    private static boolean initialized = false;
    /**
     * If specified, this callback runs on initial launch, once initialized
     */
    private final Runnable initCallback;

    /**
     * Begins Scheduling Ticks on Class Initialization
     *
     * @param callback The callback to run upon post-initialization
     */
    public CraftPresence(final Runnable callback) {
        // Ensure UniLib is loaded
        UniLib.assertLoaded();

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
        // Check for Updates before continuing
        UPDATER.checkForUpdates();

        CONFIG = Config.loadOrCreate(
                config -> config.applyEvents(
                        (instance -> CommandUtils.reloadData(true)),
                        CommandUtils::applyData
                ).setGameVersion(ModUtils.MCProtocolID)
        );

        CommandUtils.init();

        // If running in Debug Mode, Warn of Possible Issues and Log OS Info
        Constants.LOG.debugWarn(Constants.TRANSLATOR.translate("craftpresence.logger.warning.debug_mode"));
        Constants.LOG.debugInfo(Constants.TRANSLATOR.translate("craftpresence.logger.info.os", OSUtils.OS_NAME, OSUtils.OS_ARCH, OSUtils.IS_64_BIT));

        if (initCallback != null) {
            initCallback.run();
        }
        isDataLoaded = true;

        CLIENT.setup();
        CommandUtils.syncPlaceholders();
        CommandUtils.setupRPC();

        initialized = true;
    }

    /**
     * Schedules the Next Tick to Occur if not currently closing
     */
    private void scheduleTick() {
        CoreUtils.registerTickEvent(Constants.MOD_ID, this::clientTick);
    }

    /**
     * The Event to Run on each Client Tick, if passed initialization events and not closing
     * <p>
     * Consists of Synchronizing Data, and Updating RPC Data as needed
     */
    private void clientTick() {
        if (!CoreUtils.IS_CLOSING) {
            instance = ModUtils.getMinecraft();
            if (initialized || instance != null) {
                session = GameUtils.getSession(instance);

                if (initialized) {
                    player = WorldUtils.getPlayer(instance);
                    world = WorldUtils.getWorld(player);

                    username = GameUtils.getUsername(instance);
                    uuid = GameUtils.getUuid(instance);

                    CommandUtils.reloadData(false);
                } else if (session != null) {
                    init();
                }
            }
        }
    }
}
