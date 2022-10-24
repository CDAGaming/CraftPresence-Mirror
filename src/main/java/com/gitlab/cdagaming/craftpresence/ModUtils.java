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

import com.gitlab.cdagaming.craftpresence.utils.TranslationUtils;
import com.gitlab.cdagaming.craftpresence.utils.updater.ModUpdaterUtils;

import java.io.File;

/**
 * Constant Variables and Methods used throughout the Application
 *
 * @author CDAGaming
 */
@SuppressWarnings({"DuplicatedCode", "ConstantConditions", "PointlessBooleanExpression"})
public class ModUtils {
    /**
     * The Application's Name
     */
    public static final String NAME;

    /**
     * The Application's Version ID
     */
    public static final String VERSION_ID;

    /**
     * The Application's Version Release Type
     */
    public static final String VERSION_TYPE;

    /**
     * The Application's Version Release Type Display Name
     */
    public static final String VERSION_LABEL;

    /**
     * The Application's Identifier
     */
    public static final String MOD_ID = "craftpresence";

    /**
     * The Application's Configuration Schema Version ID
     */
    public static final int MOD_SCHEMA_VERSION = 2;

    /**
     * The Detected Minecraft Version
     */
    public static final String MCVersion;

    /**
     * The Detected Minecraft Protocol Version
     */
    public static final int MCProtocolID;

    /**
     * The Detected Brand Information within Minecraft
     */
    public static final String BRAND = "vanilla";

    /**
     * The Application's Configuration Directory
     */
    public static final String configDir = CraftPresence.SYSTEM.USER_DIR + File.separator + "config";

    /**
     * The Application's "mods" Directory
     */
    public static final String modsDir = CraftPresence.SYSTEM.USER_DIR + File.separator + "mods";

    /**
     * The URL to receive Update Information from
     */
    public static final String UPDATE_JSON = "https://raw.githubusercontent.com/CDAGaming/VersionLibrary/master/CraftPresence/update.json";

    /**
     * The Application's Instance of {@link ModLogger} for Logging Information
     */
    public static final ModLogger LOG = new ModLogger(MOD_ID);

    /**
     * The Current Thread's Class Loader, used to dynamically receive data as needed
     */
    public static final ClassLoader CLASS_LOADER = Thread.currentThread().getContextClassLoader();

    /**
     * The Application's Instance of {@link TranslationUtils} for Localization and Translating Data Strings
     */
    public static final TranslationUtils TRANSLATOR;

    /**
     * The Application's Instance of {@link ModUpdaterUtils} for Retrieving if the Application has an update
     */
    public static final ModUpdaterUtils UPDATER;
    /**
     * If this Application is in the Alpha Floor of Legacy Mode
     * <p>This variable becomes true only on versions at or before a1.1.2_01 (Where resource paths are different)
     */
    public final static boolean IS_LEGACY_ALPHA = true;
    /**
     * If this Application is in the Hard Floor of Legacy Mode
     * <p>This variable becomes true only on versions at or before 1.5.2 (Or when critical APIs are missing)
     */
    public final static boolean IS_LEGACY_HARD = IS_LEGACY_ALPHA || true;
    /**
     * If this Application is within the Soft Floor of Legacy Mode
     * <p>This variable becomes true only on versions before 13w41a (When the protocol number was reset)
     */
    @SuppressWarnings("PointlessBooleanExpression")
    public final static boolean IS_LEGACY_SOFT = IS_LEGACY_HARD || true;
    /**
     * Whether to forcibly block any tooltips related to this Application from rendering
     */
    public static boolean forceBlockTooltipRendering = false;
    /**
     * If this Application should be run in a Developer or Debug State
     */
    public static boolean IS_DEV = false;
    /**
     * If this Application is running in a de-obfuscated or Developer environment
     */
    public static boolean IS_VERBOSE = false;

    static {
        NAME = "@MOD_NAME@";
        VERSION_ID = "v@VERSION_ID@";
        VERSION_TYPE = "@VERSION_TYPE@";
        VERSION_LABEL = "@VERSION_LABEL@";
        MCVersion = "@MC_VERSION@";
        MCProtocolID = Integer.parseInt("@MC_PROTOCOL@");
        TRANSLATOR = new TranslationUtils(MOD_ID, true);
        UPDATER = new ModUpdaterUtils(MOD_ID, UPDATE_JSON, VERSION_ID, MCVersion);
    }
}
