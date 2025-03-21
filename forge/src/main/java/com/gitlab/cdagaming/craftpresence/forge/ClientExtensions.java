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

import com.gitlab.cdagaming.craftpresence.config.gui.MainGui;
import com.gitlab.cdagaming.unilib.core.CoreUtils;
import io.github.cdagaming.unicore.utils.StringUtils;
import net.minecraftforge.fml.ExtensionPoint;
import net.minecraftforge.fml.ModList;
import net.minecraftforge.fml.ModLoadingContext;
import net.minecraftforge.fml.loading.moddiscovery.ModInfo;
import net.minecraftforge.fml.network.FMLNetworkConstants;
import org.apache.commons.lang3.tuple.Pair;

import java.util.List;

/**
 * The Primary Application Class and Utilities
 *
 * @author CDAGaming
 */
public class ClientExtensions {
    /**
     * Begins Scheduling Ticks on Class Initialization
     */
    public static void Setup() {
        try {
            // Workaround: Client-side only fix for Forge Clients
            // - Reference => https://gitlab.com/CDAGaming/CraftPresence/-/issues/99
            ModLoadingContext.get().registerExtensionPoint(ExtensionPoint.DISPLAYTEST, () -> Pair.of(() -> FMLNetworkConstants.IGNORESERVERONLY, (a, b) -> true));
        } catch (Throwable ignored) {
            // before forge-1.13.2-25.0.103
        }

        try {
            // Workaround: Modify "ModInfo#hasConfigUI" for certain Forge Clients
            // - Reference => https://github.com/MinecraftForge/MinecraftForge/pull/6208
            final ModList modList = ModList.get();
            final List<ModInfo> sortedList = (List<ModInfo>) StringUtils.getField(ModList.class, modList, "sortedList");
            final ModInfo modInfo = sortedList.stream().filter(info -> info.getModId().equals("craftpresence")).findFirst().get();
            sortedList.set(sortedList.indexOf(modInfo), new CraftPresenceModInfo(modInfo));
            StringUtils.updateField(ModList.class, modList, sortedList, "sortedList");

            // Register The Config GUI Factory, used in Forge for Mod Menu integration
            ModLoadingContext.get().registerExtensionPoint(ExtensionPoint.CONFIGGUIFACTORY, () -> (mc, parentScreen) -> new MainGui(parentScreen));
        } catch (Throwable ex) {
            CoreUtils.LOG.error("Failed to register Config GUI Factory for @MOD_NAME@.", ex);
        }
    }

    private static class CraftPresenceModInfo extends ModInfo {
        public CraftPresenceModInfo(ModInfo modInfo) {
            super(modInfo.getOwningFile(), modInfo.getModConfig());
        }

        @Override
        public boolean hasConfigUI() {
            return true;
        }
    }
}
