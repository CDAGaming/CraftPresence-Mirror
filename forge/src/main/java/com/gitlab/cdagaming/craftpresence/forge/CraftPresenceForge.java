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

package com.gitlab.cdagaming.craftpresence.forge;

import com.gitlab.cdagaming.craftpresence.CraftPresence;
import com.gitlab.cdagaming.craftpresence.ModUtils;
import com.gitlab.cdagaming.craftpresence.forge.integrations.replaymod.ReplayModUtils;
import com.gitlab.cdagaming.craftpresence.utils.CommandUtils;
import com.gitlab.cdagaming.craftpresence.utils.FileUtils;
import com.gitlab.cdagaming.craftpresence.utils.MappingUtils;
import net.minecraftforge.fml.common.Mod;

/**
 * The Primary Application Class and Utilities
 *
 * @author CDAGaming
 */
@Mod(modid = "craftpresence", name = "@MOD_NAME@", version = "@VERSION_ID@", clientSideOnly = true, guiFactory = "com.gitlab.cdagaming.craftpresence.forge.config.ConfigGuiDataFactory", canBeDeactivated = true, updateJSON = ModUtils.UPDATE_JSON, acceptedMinecraftVersions = "*")
public class CraftPresenceForge {
    /**
     * Begins Scheduling Ticks on Class Initialization
     */
    @SuppressWarnings({"Convert2Lambda", "Anonymous2MethodRef"})
    public CraftPresenceForge() {
        if (MappingUtils.JAVA_SPEC < 1.8f) {
            throw new UnsupportedOperationException("Incompatible JVM!!! @MOD_NAME@ requires Java 8 or above to work properly!");
        }
        MappingUtils.setFilePath("/mappings-forge.srg");
        new CraftPresence(new Runnable() {
            @Override
            public void run() {
                setupIntegrations();
            }
        });
    }

    /**
     * Setup external integrations and attachments to the primary application
     */
    public void setupIntegrations() {
        if (FileUtils.findValidClass("com.replaymod.core.ReplayMod") != null) {
            CommandUtils.modules.put("integration.replaymod", new ReplayModUtils());
        }
    }
}