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
import io.github.cdagaming.unicore.utils.OSUtils;
import net.minecraftforge.fml.common.Mod;

/**
 * The Primary Application Class and Utilities
 *
 * @author CDAGaming
 */
@Mod(modid = "@MOD_ID@", name = "@MOD_NAME@", version = "@VERSION_ID@", clientSideOnly = true, guiFactory = "com.gitlab.cdagaming.craftpresence.forge.config.ConfigGuiDataFactory", canBeDeactivated = true, updateJSON = "https://raw.githubusercontent.com/CDAGaming/VersionLibrary/master/CraftPresence/update.json", acceptedMinecraftVersions = "*", dependencies = "required-after:unilib@[1.0.1,]")
public class CraftPresenceForge {
    /**
     * Begins Scheduling Ticks on Class Initialization
     */
    public CraftPresenceForge() {
        try {
            if (OSUtils.JAVA_SPEC < 1.8f) {
                throw new UnsupportedOperationException("Incompatible JVM!!! @MOD_NAME@ requires Java 8 or above to work properly!");
            }
            new CraftPresence(this::setupIntegrations);
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