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

import com.gitlab.cdagaming.craftpresence.utils.SystemUtils;
import com.gitlab.cdagaming.craftpresence.utils.TranslationUtils;
import com.gitlab.cdagaming.craftpresence.utils.updater.ModUpdaterUtils;
import edu.umd.cs.findbugs.annotations.SuppressFBWarnings;
import net.minecraft.client.ClientBrandRetriever;

import java.io.File;

/**
 * Constant Variables and Methods used throughout the Application
 *
 * @author CDAGaming
 */
@SuppressWarnings("ConstantValue")
@SuppressFBWarnings("MS_SHOULD_BE_FINAL")
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
     * The Application's Identifier
     */
    public static final String MOD_ID = "craftpresence";

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
    public static final String BRAND = ClientBrandRetriever.getClientModName();

    /**
     * The Application's Configuration Directory
     */
    public static final String configDir = SystemUtils.USER_DIR + File.separator + "config";

    /**
     * The Application's "mods" Directory
     */
    public static final String modsDir = SystemUtils.USER_DIR + File.separator + "mods";

    /**
     * The URL to receive Update Information from
     */
    public static final String UPDATE_JSON = "https://raw.githubusercontent.com/CDAGaming/VersionLibrary/master/CraftPresence/update.json";

    /**
     * The Application's Instance of {@link ModLogger} for Logging Information
     */
    public static final ModLogger LOG = new ModLogger(MOD_ID);

    /**
     * The Application's Instance of {@link TranslationUtils} for Localization and Translating Data Strings
     */
    public static final TranslationUtils TRANSLATOR;

    /**
     * The Main Game's Instance of {@link TranslationUtils} for Localization and Translating Data Strings
     */
    public static final TranslationUtils RAW_TRANSLATOR;

    /**
     * The Application's Instance of {@link ModUpdaterUtils} for Retrieving if the Application has an update
     */
    public static final ModUpdaterUtils UPDATER;
    /**
     * If this Application is in the Alpha Floor of Legacy Mode
     * <p>This variable becomes true only on versions at or before a1.1.2_01 (Where resource paths are different)
     */
    public final static boolean IS_LEGACY_ALPHA = false;
    /**
     * If this Application is in the Hard Floor of Legacy Mode
     * <p>This variable becomes true only on versions at or before 1.5.2 (Or when critical APIs are missing)
     */
    @SuppressWarnings("PointlessBooleanExpression")
    public final static boolean IS_LEGACY_HARD = IS_LEGACY_ALPHA || false;
    /**
     * If this Application is within the Soft Floor of Legacy Mode
     * <p>This variable becomes true only on versions before 13w41a (When the protocol number was reset)
     */
    @SuppressWarnings("PointlessBooleanExpression")
    public final static boolean IS_LEGACY_SOFT = IS_LEGACY_HARD || false;
    /**
     * If this Application is flagged to be run in a Developer or Debug State
     */
    public static final boolean IS_DEV_FLAG;
    /**
     * If this Application is flagged to be running in a de-obfuscated or Developer environment
     */
    public static final boolean IS_VERBOSE_FLAG;

    static {
        NAME = "@MOD_NAME@";
        VERSION_ID = "v@VERSION_ID@";
        VERSION_TYPE = "@VERSION_TYPE@";
        MCVersion = "@MC_VERSION@";
        MCProtocolID = Integer.parseInt("@MC_PROTOCOL@");
        IS_DEV_FLAG = Boolean.parseBoolean("@IS_DEV@");
        IS_VERBOSE_FLAG = Boolean.parseBoolean("@IS_VERBOSE@");
        TRANSLATOR = new TranslationUtils(MOD_ID, true).build();
        final boolean hasVanillaTranslations = !IS_LEGACY_SOFT || MCProtocolID >= 7;
        RAW_TRANSLATOR = hasVanillaTranslations ? new TranslationUtils(
                "minecraft", !IS_LEGACY_SOFT && MCProtocolID >= 353
        ).setUsingAssetsPath(!IS_LEGACY_SOFT || MCProtocolID >= 72).build() : null;
        UPDATER = new ModUpdaterUtils(MOD_ID, UPDATE_JSON, VERSION_ID, MCVersion);
    }
}
