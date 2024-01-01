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
import com.gitlab.cdagaming.craftpresence.core.utils.ModUpdaterUtils;
import com.gitlab.cdagaming.craftpresence.core.utils.StringUtils;
import com.gitlab.cdagaming.craftpresence.core.utils.TranslationUtils;
import net.minecraft.client.ClientBrandRetriever;
import net.minecraft.realms.RealmsSharedConstants;

/**
 * Constant Variables and Methods used throughout the Application
 * <p>
 * See {@link Constants} for more General Purpose Data
 *
 * @author CDAGaming
 */
public class ModUtils {
    /**
     * The Detected Minecraft Version
     */
    public static final String MCVersion = RealmsSharedConstants.VERSION_STRING;

    /**
     * The Detected Minecraft Protocol Version
     */
    public static final int MCProtocolID = RealmsSharedConstants.NETWORK_PROTOCOL_VERSION;

    /**
     * The Detected Brand Information within Minecraft
     */
    public static final String BRAND = findGameBrand();

    /**
     * If this Application is in the Hard Floor of Legacy Mode
     * <p>This variable becomes true only on versions at or before 1.5.2 (Or when critical APIs are missing)
     */
    public static final boolean IS_LEGACY_HARD = Constants.IS_LEGACY_SOFT && MCProtocolID <= 61;

    /**
     * If this Application is in the Alpha Floor of Legacy Mode
     * <p>This variable becomes true only on versions at or before a1.1.2_01 (Where resource paths are different)
     */
    public static final boolean IS_LEGACY_ALPHA = IS_LEGACY_HARD && MCProtocolID <= 2;

    /**
     * The Application's Instance of {@link ModUpdaterUtils} for Retrieving if the Application has an update
     */
    public static final ModUpdaterUtils UPDATER = new ModUpdaterUtils(
            Constants.MOD_ID,
            Constants.UPDATE_JSON,
            Constants.VERSION_ID,
            MCVersion
    );

    /**
     * The Main Game's Instance of {@link TranslationUtils} for Localization and Translating Data Strings
     */
    public static final TranslationUtils RAW_TRANSLATOR = findGameTranslations();

    /**
     * Flag used for determining if Text Formatting Codes are blocked
     */
    public static final boolean IS_TEXT_FORMATTING_BLOCKED = Constants.IS_LEGACY_SOFT && MCProtocolID <= 23;

    private static String findGameBrand() {
        String result = null;
        try {
            result = System.getProperty("minecraft.launcher.brand");
        } catch (Throwable ignored) {
        }
        return StringUtils.getOrDefault(
                result, ClientBrandRetriever.getClientModName()
        );
    }

    private static TranslationUtils findGameTranslations() {
        final boolean hasVanillaTranslations = !Constants.IS_LEGACY_SOFT || MCProtocolID >= 7;
        return hasVanillaTranslations ? new TranslationUtils(
                "minecraft", !Constants.IS_LEGACY_SOFT && MCProtocolID >= 353
        ).setUsingAssetsPath(!Constants.IS_LEGACY_SOFT || MCProtocolID >= 72).build() : null;
    }
}
