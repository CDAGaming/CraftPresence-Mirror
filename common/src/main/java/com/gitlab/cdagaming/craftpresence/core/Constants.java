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

package com.gitlab.cdagaming.craftpresence.core;

import com.gitlab.cdagaming.craftpresence.core.utils.StringUtils;
import com.gitlab.cdagaming.craftpresence.utils.SystemUtils;

import java.io.File;

/**
 * Constant Variables and Methods used throughout the Application
 *
 * @author CDAGaming
 */
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
     * If this Application is within the Soft Floor of Legacy Mode
     * <p>This variable becomes true only on versions before 13w41a (When the protocol number was reset)
     */
    public final static boolean IS_LEGACY_SOFT = StringUtils.getValidBoolean("@IS_LEGACY@").getSecond();

    /**
     * If this Application is flagged to be run in a Developer or Debug State
     */
    public static final boolean IS_DEV_FLAG = StringUtils.getValidBoolean("@IS_DEV@").getSecond();

    /**
     * If this Application is flagged to be running in a de-obfuscated or Developer environment
     */
    public static final boolean IS_VERBOSE_FLAG = StringUtils.getValidBoolean("@IS_VERBOSE@").getSecond();

    /**
     * The Application's Instance of {@link ModLogger} for Logging Information
     */
    public static final ModLogger LOG = new ModLogger(MOD_ID);
}