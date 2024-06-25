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

package com.gitlab.cdagaming.craftpresence.forge;

import com.gitlab.cdagaming.craftpresence.CraftPresence;
import com.gitlab.cdagaming.craftpresence.config.gui.MainGui;
import com.gitlab.cdagaming.craftpresence.core.Constants;
import io.github.cdagaming.unicore.utils.MappingUtils;
import io.github.cdagaming.unicore.utils.OSUtils;
import net.minecraftforge.fml.ExtensionPoint;
import net.minecraftforge.fml.ModList;
import net.minecraftforge.fml.ModLoadingContext;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.loading.FMLEnvironment;
import net.minecraftforge.fml.network.FMLNetworkConstants;
import org.apache.commons.lang3.tuple.Pair;

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
        if (OSUtils.JAVA_SPEC < 1.8f) {
            throw new UnsupportedOperationException("Incompatible JVM!!! @MOD_NAME@ requires Java 8 or above to work properly!");
        }

        try {
            // Workaround: Client-side only fix for Forge Clients
            // - Reference => https://gitlab.com/CDAGaming/CraftPresence/-/issues/99
            ModLoadingContext.get().registerExtensionPoint(ExtensionPoint.DISPLAYTEST, () -> Pair.of(() -> FMLNetworkConstants.IGNORESERVERONLY, (a, b) -> true));
        } catch (Throwable ignored) {
            // before forge-1.13.2-25.0.103
        }

        try {
            // Register The Config GUI Factory, used in Forge for Mod Menu integration
            ModLoadingContext.get().registerExtensionPoint(ExtensionPoint.CONFIGGUIFACTORY, () -> (mc, parentScreen) -> new MainGui(parentScreen));
        } catch (Throwable ex) {
            Constants.LOG.error("Failed to register Config GUI Factory for @MOD_NAME@.", ex);
        }

        if (FMLEnvironment.dist.isClient()) {
            MappingUtils.setFilePath("/mappings-forge.srg");
            Constants.MOD_COUNT_SUPPLIER = () -> ModList.get().getMods().size();
            new CraftPresence(this::setupIntegrations);
        } else {
            Constants.LOG.info("Disabling @MOD_NAME@, as it is client side only.");
        }
    }

    /**
     * Setup external integrations and attachments to the primary application
     */
    public void setupIntegrations() {
        // N/A
    }
}