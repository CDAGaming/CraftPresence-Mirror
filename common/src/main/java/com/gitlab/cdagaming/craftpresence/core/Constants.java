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

package com.gitlab.cdagaming.craftpresence.core;

import com.gitlab.cdagaming.craftpresence.core.integrations.logging.ApacheLogger;
import edu.umd.cs.findbugs.annotations.SuppressFBWarnings;
import io.github.cdagaming.unicore.integrations.logging.JavaLogger;
import io.github.cdagaming.unicore.integrations.logging.LoggingImpl;
import io.github.cdagaming.unicore.utils.FileUtils;
import io.github.cdagaming.unicore.utils.OSUtils;
import io.github.cdagaming.unicore.utils.StringUtils;
import io.github.cdagaming.unicore.utils.TranslationUtils;

import java.io.File;
import java.util.List;

/**
 * Constant Variables and Methods used throughout the Application
 *
 * @author CDAGaming
 */
@SuppressFBWarnings("MS_CANNOT_BE_FINAL")
public class Constants {
    /**
     * The Application's Name
     */
    public static final String NAME = "@MOD_NAME@";

    /**
     * The Application's Version ID
     */
    public static final String VERSION_ID = "v@VERSION_ID@";

    /**
     * The Application's Version Release Type
     */
    public static final String VERSION_TYPE = "@VERSION_TYPE@";

    /**
     * The Application's Identifier
     */
    public static final String MOD_ID = "craftpresence";

    /**
     * The Minecraft Version this Mod was compiled with
     */
    public static final String MCBuildVersion = "@MC_VERSION@";

    /**
     * The Protocol Version this Mod was compiled with
     */
    public static final int MCBuildProtocol = StringUtils.getValidInteger("@MC_PROTOCOL@").getSecond();

    /**
     * The Application's Configuration Directory
     */
    public static final String configDir = OSUtils.USER_DIR + File.separator + "config";

    /**
     * The Application's "mods" Directory
     */
    public static final String modsDir = OSUtils.USER_DIR + File.separator + "mods";

    /**
     * The URL to receive Update Information from
     */
    public static final String UPDATE_JSON = "https://raw.githubusercontent.com/CDAGaming/VersionLibrary/master/CraftPresence/update.json";

    /**
     * If this Application is within the Soft Floor of Legacy Mode
     * <p>This variable becomes true only on versions before 13w41a (When the protocol number was reset)
     */
    public static final boolean IS_LEGACY_SOFT = StringUtils.getValidBoolean("@IS_LEGACY@").getSecond();

    /**
     * If this Application is flagged to be run in a Developer or Debug State
     */
    public static final boolean IS_DEV_FLAG = StringUtils.getValidBoolean("@IS_DEV@").getSecond();

    /**
     * If this Application is flagged to be running in a de-obfuscated or Developer environment
     */
    public static final boolean IS_VERBOSE_FLAG = StringUtils.getValidBoolean("@IS_VERBOSE@").getSecond();

    /**
     * The Application's Instance of {@link LoggingImpl} for Logging Information
     */
    public static final LoggingImpl LOG = IS_LEGACY_SOFT ? new JavaLogger(MOD_ID) : new ApacheLogger(MOD_ID);

    /**
     * The Application's Instance of {@link TranslationUtils} for Localization and Translating Data Strings
     */
    public static final TranslationUtils TRANSLATOR = new TranslationUtils(MOD_ID, true).build();

    /**
     * If Loading of game data has been completed<p>
     * Becomes true after callbacks synchronize if previously false but game is loaded
     */
    public static boolean HAS_GAME_LOADED = false;

    /**
     * If the Mod is Currently Closing and Clearing Data
     */
    public static boolean IS_GAME_CLOSING = false;

    /**
     * Retrieve the Amount of Active Mods in the instance
     *
     * @return The Mods that are active in the instance
     */
    public static int getModCount() {
        int modCount = -1;
        final Class<?> fmlLoader = FileUtils.findValidClass("net.minecraftforge.fml.common.Loader");
        final Class<?> quiltLoader = FileUtils.findValidClass("org.quiltmc.loader.api.QuiltLoader");
        final Class<?> fabricLoader = FileUtils.findValidClass("net.fabricmc.loader.api.FabricLoader");
        if (fmlLoader != null) {
            final Object loaderInstance = StringUtils.executeMethod(fmlLoader, null, null, null, "instance");
            if (loaderInstance != null) {
                final Object mods = StringUtils.executeMethod(fmlLoader, loaderInstance, null, null, "getModList");
                if (mods instanceof List<?>) {
                    modCount = ((List<?>) mods).size();
                }
            }
        } else if (quiltLoader != null) {
            final Object mods = StringUtils.executeMethod(quiltLoader, null, null, null, "getAllMods");
            if (mods instanceof List<?>) {
                modCount = ((List<?>) mods).size();
            }
        } else if (fabricLoader != null) {
            final Object loaderInstance = StringUtils.executeMethod(fabricLoader, null, null, null, "getInstance");
            if (loaderInstance != null) {
                final Object mods = StringUtils.executeMethod(fabricLoader, loaderInstance, null, null, "getAllMods");
                if (mods instanceof List<?>) {
                    modCount = ((List<?>) mods).size();
                }
            }
        }
        return modCount > 0 ? modCount : getRawModCount();
    }

    /**
     * Retrieve the Amount of Active Mods in the {@link Constants#modsDir}
     *
     * @return The Mods that are active in the directory
     */
    public static int getRawModCount() {
        // Mod is within ClassLoader if in a Dev Environment
        // and is thus automatically counted if this is the case
        int modCount = 0;
        final File[] mods = new File(Constants.modsDir).listFiles();

        if (mods != null) {
            for (File modFile : mods) {
                if (FileUtils.getFileExtension(modFile).equals(".jar")) {
                    modCount++;
                }
            }
        }
        return Math.max(1, modCount);
    }
}
