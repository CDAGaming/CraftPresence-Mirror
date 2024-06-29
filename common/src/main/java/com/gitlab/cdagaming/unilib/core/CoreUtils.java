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

package com.gitlab.cdagaming.unilib.core;

import com.gitlab.cdagaming.unilib.core.integrations.logging.ApacheLogger;
import edu.umd.cs.findbugs.annotations.SuppressFBWarnings;
import io.github.cdagaming.unicore.integrations.logging.JavaLogger;
import io.github.cdagaming.unicore.integrations.logging.LoggingImpl;
import io.github.cdagaming.unicore.utils.FileUtils;
import io.github.cdagaming.unicore.utils.OSUtils;
import io.github.cdagaming.unicore.utils.StringUtils;
import io.github.cdagaming.unicore.utils.TranslationUtils;

import java.io.File;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.ThreadFactory;
import java.util.function.Function;
import java.util.function.Supplier;

/**
 * Constant Variables and Methods used throughout the Application
 *
 * @author CDAGaming
 */
@SuppressFBWarnings("MS_CANNOT_BE_FINAL")
// TODO: Replace values with gradle flags, once UniLib is seperated
public class CoreUtils {
    /**
     * The Application's Name
     */
    public static final String NAME = "UniCore";

    /**
     * The Application's Version
     */
    public static final String VERSION = "0.0.0";

    /**
     * The Application's Version ID
     */
    public static final String VERSION_ID = "v" + VERSION;

    /**
     * The Application's Version Release Type
     */
    public static final String VERSION_TYPE = "Release";

    /**
     * The Application's Identifier
     */
    public static final String MOD_ID = "unicore";

    /**
     * The Minecraft Version this Mod was compiled with
     */
    public static final String MCBuildVersion = "@MC_VERSION@";

    /**
     * The Protocol Version this Mod was compiled with
     */
    public static final int MCBuildProtocol = StringUtils.getValidInteger("@MC_PROTOCOL@").getSecond();

    /**
     * The Application's "mods" Directory
     */
    public static final String modsDir = OSUtils.USER_DIR + File.separator + "mods";

    /**
     * If this Application is within the Soft Floor of Legacy Mode
     * <p>This variable becomes true only on versions before 13w41a (When the protocol number was reset)
     */
    public static final boolean IS_LEGACY_SOFT = StringUtils.getValidBoolean("@IS_LEGACY@").getSecond();
    /**
     * The Application's Instance of {@link LoggingImpl} for Logging Information
     */
    public static final LoggingImpl LOG = IS_LEGACY_SOFT ? new JavaLogger(MOD_ID) : new ApacheLogger(MOD_ID);
    /**
     * If this Application is in the Hard Floor of Legacy Mode
     * <p>This variable becomes true only on versions at or before 1.5.2 (Or when critical APIs are missing)
     */
    private static final Function<Integer, Boolean> IS_LEGACY_HARD_SUPPLIER = (protocol) -> IS_LEGACY_SOFT && protocol <= 61;
    /**
     * If this Application is in the Hard Floor of Legacy Mode
     * <p>This variable becomes true only on versions at or before 1.5.2 (Or when critical APIs are missing)
     */
    private static final boolean IS_LEGACY_HARD = isLegacyHard(MCBuildProtocol);
    /**
     * If this Application is in the Alpha Floor of Legacy Mode
     * <p>This variable becomes true only on versions at or before a1.1.2_01 (Where resource paths are different)
     */
    private static final Function<Integer, Boolean> IS_LEGACY_ALPHA_SUPPLIER = (protocol) -> IS_LEGACY_SOFT && protocol <= 2;
    /**
     * If this Application is in the Alpha Floor of Legacy Mode
     * <p>This variable becomes true only on versions at or before a1.1.2_01 (Where resource paths are different)
     */
    private static final boolean IS_LEGACY_ALPHA = isLegacyAlpha(MCBuildProtocol);
    /**
     * Flag used for determining if Text Formatting Codes are blocked
     */
    private static final Function<Integer, Boolean> IS_TEXT_FORMATTING_BLOCKED_SUPPLIER = (protocol) -> IS_LEGACY_SOFT && protocol <= 23;
    /**
     * Flag used for determining if Text Formatting Codes are blocked
     */
    private static final boolean IS_TEXT_FORMATTING_BLOCKED = isTextFormattingBlocked(MCBuildProtocol);
    /**
     * The default language ID to be using
     */
    private static final Function<Integer, String> DEFAULT_LANGUAGE_SUPPLIER = (protocol) -> protocol >= 315 ? "en_us" : "en_US";
    /**
     * The default language ID to be using
     */
    private static final String DEFAULT_LANGUAGE = getDefaultLanguage(MCBuildProtocol);
    /**
     * If the Application is Currently Closing and Clearing Data
     */
    public static boolean IS_CLOSING = false;
    /**
     * The Supplier for the Mod Count, used in {@link CoreUtils#getModCount()}
     */
    public static Supplier<Integer> MOD_COUNT_SUPPLIER = null;

    /**
     * The Amount of Active Mods in the instance
     */
    private static int DETECTED_MOD_COUNT = -1;

    static {
        Runtime.getRuntime().addShutdownHook(
                getThreadFactory().newThread(() -> {
                    IS_CLOSING = true;
                    FileUtils.shutdownSchedulers();
                })
        );
    }

    /**
     * Retrieve the Timer Instance for this Class, used for Scheduling Events
     *
     * @return the Timer Instance for this Class
     */
    public static ScheduledExecutorService getThreadPool() {
        return FileUtils.getThreadPool(NAME);
    }

    /**
     * Retrieve the Thread Factory Instance for this Class, used for Scheduling Events
     *
     * @return the Thread Factory Instance for this class
     */
    public static ThreadFactory getThreadFactory() {
        return FileUtils.getThreadFactory(NAME);
    }


    /**
     * Retrieve If this Application is in the Hard Floor of Legacy Mode
     * <p>This variable becomes true only on versions at or before 1.5.2 (Or when critical APIs are missing)
     *
     * @param protocol The Protocol to Target for this operation
     * @return {@link Boolean#TRUE} if condition is satisfied
     */
    public static boolean isLegacyHard(final int protocol) {
        return IS_LEGACY_HARD_SUPPLIER.apply(protocol);
    }

    /**
     * Retrieve If this Application is in the Hard Floor of Legacy Mode
     * <p>This variable becomes true only on versions at or before 1.5.2 (Or when critical APIs are missing)
     *
     * @return {@link Boolean#TRUE} if condition is satisfied
     */
    public static boolean isLegacyHard() {
        return IS_LEGACY_HARD;
    }

    /**
     * Retrieve If this Application is in the Alpha Floor of Legacy Mode
     * <p>This variable becomes true only on versions at or before a1.1.2_01 (Where resource paths are different)
     *
     * @param protocol The Protocol to Target for this operation
     * @return {@link Boolean#TRUE} if condition is satisfied
     */
    public static boolean isLegacyAlpha(final int protocol) {
        return IS_LEGACY_ALPHA_SUPPLIER.apply(protocol);
    }

    /**
     * Retrieve If this Application is in the Alpha Floor of Legacy Mode
     * <p>This variable becomes true only on versions at or before a1.1.2_01 (Where resource paths are different)
     *
     * @return {@link Boolean#TRUE} if condition is satisfied
     */
    public static boolean isLegacyAlpha() {
        return IS_LEGACY_ALPHA;
    }

    /**
     * Retrieve if Text Formatting Codes are blocked
     *
     * @param protocol The Protocol to Target for this operation
     * @return {@link Boolean#TRUE} if condition is satisfied
     */
    public static boolean isTextFormattingBlocked(final int protocol) {
        return IS_TEXT_FORMATTING_BLOCKED_SUPPLIER.apply(protocol);
    }

    /**
     * Retrieve if Text Formatting Codes are blocked
     *
     * @return {@link Boolean#TRUE} if condition is satisfied
     */
    public static boolean isTextFormattingBlocked() {
        return IS_TEXT_FORMATTING_BLOCKED;
    }

    /**
     * Determine the default language ID to be using
     *
     * @param protocol The Protocol to Target for this operation
     * @return the default language id to be used
     */
    public static String getDefaultLanguage(final int protocol) {
        return DEFAULT_LANGUAGE_SUPPLIER.apply(protocol);
    }

    /**
     * Determine the default language ID to be using
     *
     * @return the default language id to be used
     */
    public static String getDefaultLanguage() {
        return DEFAULT_LANGUAGE;
    }

    /**
     * Attempt to locate the Application's Brand Information
     *
     * @param fallback The string to default to, if `brandInfo` is null
     * @return the brand information, if any
     */
    public static String findGameBrand(final String fallback) {
        String result = null;
        try {
            result = System.getProperty("minecraft.launcher.brand");
        } catch (Throwable ignored) {
        }
        return StringUtils.getOrDefault(
                result, fallback
        );
    }

    /**
     * Attempt to find the Application's Instance of {@link TranslationUtils} for Localization
     *
     * @param protocol The Protocol to Target for this operation
     * @return the found {@link TranslationUtils}, or null if not found
     */
    public static TranslationUtils findGameTranslations(final int protocol) {
        final boolean hasVanillaTranslations = !IS_LEGACY_SOFT || protocol >= 7;
        return hasVanillaTranslations ? new TranslationUtils(
                "minecraft", !IS_LEGACY_SOFT && protocol >= 353
        )
                .setUsingAssetsPath(!IS_LEGACY_SOFT || protocol >= 72)
                .setDefaultLanguage(getDefaultLanguage(protocol))
                .build() : null;
    }

    /**
     * Retrieve the Amount of Active Mods in the instance
     *
     * @return The Mods that are active in the instance
     */
    public static int getModCount() {
        if (DETECTED_MOD_COUNT <= 0) {
            DETECTED_MOD_COUNT = MOD_COUNT_SUPPLIER != null ? MOD_COUNT_SUPPLIER.get() : getRawModCount();
        }
        return DETECTED_MOD_COUNT;
    }

    /**
     * Retrieve the Amount of Active Mods in the {@link CoreUtils#modsDir}
     *
     * @return The Mods that are active in the directory
     */
    private static int getRawModCount() {
        // Mod is within ClassLoader if in a Dev Environment
        // and is thus automatically counted if this is the case
        int modCount = 0;
        final File[] mods = new File(modsDir).listFiles();

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
