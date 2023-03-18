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
import com.gitlab.cdagaming.craftpresence.impl.LockObject;
import com.gitlab.cdagaming.craftpresence.impl.discord.DiscordStatus;

import java.io.IOException;
import java.time.Instant;
import java.util.List;
import java.util.Random;

/**
 * System and General Use Utilities
 *
 * @author CDAGaming
 */
public class SystemUtils {
    /**
     * An instance of a random number generator, used for select parts of the mod
     */
    public static final Random RANDOM = new Random();
    /**
     * The minimum time to wait by default (In Seconds) before callbacks refresh
     */
    public static final int MINIMUM_REFRESH_RATE = 2;
    /**
     * The Name of the User's Operating System
     */
    public static final String OS_NAME = System.getProperty("os.name");
    /**
     * The Architecture of the User's System
     */
    public static final String OS_ARCH = System.getProperty("os.arch");
    /**
     * The Directory the Application is running in
     */
    public static final String USER_DIR = System.getProperty("user.dir");
    /**
     * If the {@link SystemUtils#OS_NAME} can be classified as LINUX
     */
    public static final boolean IS_LINUX = OS_NAME.startsWith("Linux") || OS_NAME.startsWith("LINUX");
    /**
     * If the {@link SystemUtils#OS_NAME} can be classified as MAC
     */
    public static final boolean IS_MAC = OS_NAME.startsWith("Mac");
    /**
     * If the {@link SystemUtils#OS_NAME} can be classified as WINDOWS
     */
    public static final boolean IS_WINDOWS = OS_NAME.startsWith("Windows");
    /**
     * If the {@link SystemUtils#OS_ARCH} is 64-bit or x64
     */
    public static final boolean IS_64_BIT = OS_ARCH.contains("amd64") || OS_ARCH.contains("x86_64");
    /**
     * An instance of {@link LockObject} to await certain tasks
     */
    public final LockObject TICK_LOCK = new LockObject();
    /**
     * The Current Time Remaining on the Timer
     */
    public int TIMER = 0;
    /**
     * If Loading of critical data has been completed<p>
     * Becomes true after callbacks synchronize once if previously false
     */
    public boolean HAS_LOADED = false;
    /**
     * If Loading of game data has been completed<p>
     * Becomes true after callbacks synchronize if previously false but game is loaded
     */
    public boolean HAS_GAME_LOADED = false;
    /**
     * If the Mod is Currently Closing and Clearing Data
     */
    public boolean IS_GAME_CLOSING = false;
    /**
     * The Current Epoch Unix Timestamp in Milliseconds
     */
    public Instant CURRENT_INSTANT;
    /**
     * Whether the Timer is Currently Active
     */
    private boolean isTiming = false;
    /**
     * Whether the Callbacks related to the Mod have been refreshed
     * <p>
     * In this case, the RPC Updates every 2 Seconds with this check ensuring such
     */
    private boolean refreshedCallbacks = false;
    /**
     * The Beginning Unix Timestamp to count down from
     */
    private Instant BEGINNING_INSTANT;
    /**
     * The Elapsed Time since the application started (In Seconds)
     */
    private long ELAPSED_TIME;

    /**
     * Initialize OS and Timer Information
     */
    public SystemUtils() {
        try {
            CURRENT_INSTANT = TimeUtils.getCurrentTime();
            ELAPSED_TIME = 0;

            TICK_LOCK.unlock();
        } catch (Exception ex) {
            ModUtils.LOG.error(ModUtils.TRANSLATOR.translate("craftpresence.logger.error.system"));
            if (CommandUtils.isVerboseMode()) {
                ex.printStackTrace();
            }
        }
    }

    /**
     * Attempt to browse to the specified command utilizing the OS-Specific APIs
     *
     * @param cmd The command to interpret
     * @return {@link Boolean#TRUE} upon success
     */
    public static boolean browseWithSystem(final String cmd) {
        if (IS_LINUX) {
            if (isXDG()) {
                if (runCommand("xdg-open", "%s", cmd)) {
                    return true;
                }
            }
            if (isKDE()) {
                if (runCommand("kde-open", "%s", cmd)) {
                    return true;
                }
            }
            if (isGNOME()) {
                if (runCommand("gnome-open", "%s", cmd)) {
                    return true;
                }
            }
            if (runCommand("kde-open", "%s", cmd)) {
                return true;
            }
            if (runCommand("gnome-open", "%s", cmd)) {
                return true;
            }
        }

        if (IS_MAC) {
            if (runCommand("open", "%s", cmd)) {
                return true;
            }
        }

        if (IS_WINDOWS) {
            if (runCommand("explorer", "%s", cmd)) {
                return true;
            }
        }

        return false;
    }

    /**
     * Attempt to execute the specified command utilizing the OS-Specific APIs
     *
     * @param command The command to interpret
     * @param args    The arguments to interpret
     * @param file    The file or extra data to interpret
     * @return {@link Boolean#TRUE} upon success
     */
    private static boolean runCommand(final String command, final String args, final String file) {
        ModUtils.LOG.debugInfo("Trying to exec: [cmd=\"%s\", args=\"%s\", file=\"%s\"", command, args, file);
        final String[] parts = prepareCommand(command, args, file);

        try {
            final Process p = Runtime.getRuntime().exec(parts);

            try {
                int retval = p.exitValue();
                if (retval == 0) {
                    ModUtils.LOG.error("Process ended immediately.");
                } else {
                    ModUtils.LOG.error("Process crashed.");
                }
                return false;
            } catch (IllegalThreadStateException itse) {
                ModUtils.LOG.error("Process is running.");
                return true;
            }
        } catch (IOException e) {
            ModUtils.LOG.error("Error running command.", e);
            return false;
        }
    }

    /**
     * Attempt to prepare the specified command for {@link SystemUtils#runCommand(String, String, String)}
     *
     * @param command The command to interpret
     * @param args    The arguments to interpret
     * @param file    The file or extra data to interpret
     * @return {@link Boolean#TRUE} upon success
     */
    private static String[] prepareCommand(final String command, final String args, final String file) {
        final List<String> parts = StringUtils.newArrayList();
        parts.add(command);

        if (args != null) {
            for (String s : args.split(" ")) {
                s = String.format(s, file); // put in the filename thing
                parts.add(s.trim());
            }
        }

        return parts.toArray(new String[0]);
    }

    /**
     * Checks if the current session is running under the XDG session protocol.
     *
     * @return {@link Boolean#TRUE} if the session is running under XDG, false otherwise.
     */
    public static boolean isXDG() {
        final String xdgSessionId = System.getenv("XDG_SESSION_ID");
        return xdgSessionId != null && !xdgSessionId.isEmpty();
    }

    /**
     * Checks if the current desktop environment is GNOME.
     *
     * @return {@link Boolean#TRUE} if the desktop environment is GNOME, false otherwise.
     */
    public static boolean isGNOME() {
        final String gdmSession = System.getenv("GDMSESSION");
        return gdmSession != null && gdmSession.toLowerCase().contains("gnome");
    }

    /**
     * Checks if the current desktop environment is KDE.
     *
     * @return {@link Boolean#TRUE} if the desktop environment is KDE, false otherwise.
     */
    public static boolean isKDE() {
        final String gdmSession = System.getenv("GDMSESSION");
        return gdmSession != null && gdmSession.toLowerCase().contains("kde");
    }

    /**
     * The Event to Run on each Client Tick, if passed initialization events
     * <p>
     * Consists of Synchronizing Data, and Updating Timer-Related Data as needed
     */
    void onTick() {
        ELAPSED_TIME = TimeUtils.getDurationFrom(CURRENT_INSTANT).getSeconds();

        if (TIMER > 0) {
            if (!isTiming) {
                startTimer();
            } else {
                checkTimer();
            }
        }

        // Every <passTime> Seconds, refresh Callbacks and load state status
        if (ELAPSED_TIME % getRefreshRate() == 0) {
            if (!refreshedCallbacks) {
                if (!HAS_LOADED && CraftPresence.CLIENT.STATUS == DiscordStatus.Ready) {
                    HAS_LOADED = true;
                }
                if (HAS_LOADED && !HAS_GAME_LOADED && CraftPresence.instance.currentScreen != null) {
                    HAS_GAME_LOADED = true;
                }
                refreshedCallbacks = true;
            }
        } else {
            refreshedCallbacks = false;
        }
    }

    /**
     * The Event to Run on each Client Tick, after passing initialization events
     * <p>
     * Consists of Scheduling awaited tasks, after a successful {@link SystemUtils#onTick()}
     */
    void postTick() {
        if (refreshedCallbacks) {
            try {
                TICK_LOCK.waitForUnlock(CraftPresence.CLIENT::updatePresence);
            } catch (Exception ex) {
                ex.printStackTrace();
            }
        }
    }

    /**
     * Retrieve the current rate at which to execute callbacks
     *
     * @return the current refresh rate
     */
    private int getRefreshRate() {
        int result = CraftPresence.CONFIG.advancedSettings.refreshRate;
        if (result < MINIMUM_REFRESH_RATE) {
            result = MINIMUM_REFRESH_RATE;
        }
        return result;
    }

    /**
     * Begins the Timer, counting down from {@link SystemUtils#BEGINNING_INSTANT}
     */
    private void startTimer() {
        BEGINNING_INSTANT = TimeUtils.getCurrentTime().plusSeconds(TIMER);
        isTiming = true;
    }

    /**
     * Determines the Remaining Time until 0, and Stops the Timer @ 0 remaining
     */
    private void checkTimer() {
        if (TIMER > 0) {
            final long remainingTime = BEGINNING_INSTANT.getEpochSecond() - TimeUtils.getCurrentTime().getEpochSecond();
            TIMER = (int) remainingTime;
        } else if (isTiming) {
            isTiming = false;
        }
    }
}
