/*
 * MIT License
 *
 * Copyright (c) 2018 - 2025 CDAGaming (cstack2011@yahoo.com)
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

package com.gitlab.cdagaming.craftpresence.forge;

import com.gitlab.cdagaming.craftpresence.CraftPresence;
import com.gitlab.cdagaming.craftpresence.config.gui.MainGui;
import com.gitlab.cdagaming.unilib.core.CoreUtils;
import io.github.cdagaming.unicore.utils.OSUtils;
import net.minecraftforge.client.ConfigScreenHandler;
import net.minecraftforge.fml.IExtensionPoint;
import net.minecraftforge.fml.ModList;
import net.minecraftforge.fml.ModLoadingContext;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.loading.FMLEnvironment;

/**
 * The Primary Application Class and Utilities
 *
 * @author CDAGaming
 */
@Mod("@MOD_ID@")
public class CraftPresenceForge {
    /**
     * Begins Scheduling Ticks on Class Initialization
     */
    public CraftPresenceForge() {
        try {
            if (OSUtils.JAVA_SPEC < 1.8f) {
                throw new UnsupportedOperationException("Incompatible JVM!!! @MOD_NAME@ requires Java 8 or above to work properly!");
            }

            try {
                // Workaround: Client-side only fix for Forge Clients
                // - Reference => https://gitlab.com/CDAGaming/CraftPresence/-/issues/99
                ModLoadingContext.get().registerExtensionPoint(IExtensionPoint.DisplayTest.class, () -> new IExtensionPoint.DisplayTest(() -> IExtensionPoint.DisplayTest.IGNORESERVERONLY, (a, b) -> true));
            } catch (Throwable ignored) {
                // before forge-1.13.2-25.0.103
            }

            try {
                // Register The Config GUI Factory, used in Forge for Mod Menu integration
                ModLoadingContext.get().registerExtensionPoint(ConfigScreenHandler.ConfigScreenFactory.class, () -> new ConfigScreenHandler.ConfigScreenFactory((mc, parentScreen) -> new MainGui(parentScreen)));
            } catch (Throwable ex) {
                CoreUtils.LOG.error("Failed to register Config GUI Factory for @MOD_NAME@.", ex);
            }

            if (FMLEnvironment.dist.isClient()) {
                new CraftPresence(this::setupIntegrations);
            } else {
                CoreUtils.LOG.info("Disabling @MOD_NAME@, as it is client side only.");
            }
        } catch (NoClassDefFoundError ex) {
            throw new UnsupportedOperationException("Unable to initialize @MOD_NAME@! @UNILIB_NAME@ (unilib) is required to run this mod (Requires @UNILIB_MIN_VERSION@ or above)", ex);
        }
    }

    /**
     * Setup external integrations and attachments to the primary application
     */
    public void setupIntegrations() {
        // N/A
    }
}