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

package com.gitlab.cdagaming.unilib;

import com.gitlab.cdagaming.unilib.core.CoreUtils;
import com.gitlab.cdagaming.unilib.core.utils.ModUpdaterUtils;
import com.gitlab.cdagaming.unilib.impl.TranslationListener;
import com.gitlab.cdagaming.unilib.impl.TranslationManager;
import io.github.cdagaming.unicore.utils.MappingUtils;
import net.minecraft.client.Minecraft;

/**
 * The Primary Application Class and Utilities
 *
 * @author CDAGaming
 */
public class UniLib {
    /**
     * The Application's Instance of {@link ModUpdaterUtils} for Retrieving if the Application has an update
     */
    public static final ModUpdaterUtils UPDATER = new ModUpdaterUtils(
            CoreUtils.MOD_ID,
            CoreUtils.UPDATE_JSON,
            CoreUtils.VERSION_ID,
            ModUtils.MCVersion
    );
    /**
     * The Minecraft Instance attached to this Mod
     */
    private static Minecraft instance;
    /**
     * Whether the Mod has started its Initialization Phase
     */
    private static boolean initializing = false;
    /**
     * Whether the Mod has completed its Initialization Phase
     */
    private static boolean initialized = false;

    /**
     * Assert whether this mod has been initialized
     *
     * @return {@link Boolean#TRUE} if condition is satisfied
     */
    public static boolean assertLoaded() {
        if (!initialized && !initializing) {
            initializing = true;
            setup();
        }
        return initialized;
    }

    /**
     * Setup Important Data for this Module
     */
    private static void setup() {
        CoreUtils.setup();
        CoreUtils.registerTickEvent(UniLib::clientTick);
    }

    /**
     * The Mod's Initialization Event
     */
    private static void init() {
        // Register Resource Reload Listeners
        ModUtils.executeOnMainThread(() -> ModUtils.registerReloadListener(
                CoreUtils.MOD_ID + ":translation_listener",
                TranslationListener.INSTANCE
        ));

        if (ModUtils.RAW_TRANSLATOR != null) {
            TranslationListener.INSTANCE.addModule("minecraft", new TranslationManager(
                    UniLib.instance,
                    ModUtils.RAW_TRANSLATOR
                            .setLanguageSupplier(ModUtils::getLanguage)
            ));
        }

        // Initialize Dynamic Mappings and Critical Data
        MappingUtils.getClassMap();

        // Check for Updates before continuing
        UPDATER.checkForUpdates();

        initialized = true;
        initializing = false;
    }

    /**
     * The Event to Run on each Client Tick, if initialized
     */
    private static void onTick() {
        TranslationListener.INSTANCE.onTick();
    }

    /**
     * The Event to Run on each Client Tick
     */
    private static void clientTick() {
        if (!CoreUtils.IS_CLOSING) {
            instance = ModUtils.getMinecraft();
            if (initialized) {
                onTick();
            } else if (instance != null) {
                init();
            }
        }
    }
}
