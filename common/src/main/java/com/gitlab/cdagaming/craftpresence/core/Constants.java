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
import io.github.cdagaming.unicore.utils.TranslationUtils;
import com.gitlab.cdagaming.unilib.core.CoreUtils;

import java.io.File;

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
    public static final String MOD_ID = "@MOD_ID@";

    /**
     * The Application's Configuration Directory
     */
    public static final String configDir = OSUtils.USER_DIR + File.separator + "config";

    /**
     * The URL to receive Update Information from
     */
    public static final String UPDATE_JSON = "https://raw.githubusercontent.com/CDAGaming/VersionLibrary/master/CraftPresence/update.json";

    /**
     * The URL for the Application's "README"
     */
    public static final String URL_README = "https://gitlab.com/CDAGaming/CraftPresence/-/wikis/home";

    /**
     * The URL for the Application's "Source"
     */
    public static final String URL_SOURCE = "https://gitlab.com/CDAGaming/CraftPresence";

    /**
     * The Application's Instance of {@link LoggingImpl} for Logging Information
     */
    public static final LoggingImpl LOG = CoreUtils.IS_LEGACY_SOFT ? new JavaLogger(MOD_ID) : new ApacheLogger(MOD_ID);

    /**
     * The Application's Instance of {@link TranslationUtils} for Localization and Translating Data Strings
     */
    public static final TranslationUtils TRANSLATOR = new TranslationUtils(
            MOD_ID, true
    )
            .setDefaultLanguage(CoreUtils.getDefaultLanguage())
            .build();

    /**
     * If Loading of game data has been completed<p>
     * Becomes true after callbacks synchronize if previously false but game is loaded
     */
    public static boolean HAS_GAME_LOADED = false;

    /**
     * If the Mod has checked for "Replay Mod" availability
     */
    private static boolean CHECKED_REPLAY_MOD = false;

    /**
     * If the Mod should enable "Replay Mod" Integration
     */
    private static boolean HAS_REPLAY_MOD = false;

    /**
     * Retrieve if the Mod should enable "Replay Mod" Integration
     *
     * @return {@link Boolean#TRUE} if the Mod should enable "Replay Mod" Integration
     */
    public static boolean hasReplayMod() {
        if (!CHECKED_REPLAY_MOD) {
            HAS_REPLAY_MOD = FileUtils.findClass("com.replaymod.core.ReplayMod") != null;
            CHECKED_REPLAY_MOD = true;
        }
        return HAS_REPLAY_MOD;
    }
}
