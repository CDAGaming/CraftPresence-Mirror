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

package com.gitlab.cdagaming.craftpresence.utils.world;

import com.gitlab.cdagaming.craftpresence.CraftPresence;
import com.gitlab.cdagaming.craftpresence.core.config.Config;
import com.gitlab.cdagaming.craftpresence.core.config.element.ModuleData;
import com.gitlab.cdagaming.craftpresence.core.impl.ExtendedModule;
import io.github.cdagaming.unicore.utils.FileUtils;
import io.github.cdagaming.unicore.utils.MappingUtils;
import io.github.cdagaming.unicore.utils.StringUtils;
import net.minecraft.common.world.biomes.Biome;
import net.minecraft.common.world.biomes.Biomes;
import unilib.external.io.github.classgraph.ClassInfo;

import java.util.List;
import java.util.function.Supplier;

/**
 * Biome Utilities used to Parse Biome Data and handle related RPC Events
 *
 * @author CDAGaming
 */
@SuppressWarnings("DuplicatedCode")
public class BiomeUtils implements ExtendedModule {
    /**
     * A List of the detected Biome Names
     */
    public List<String> BIOME_NAMES = StringUtils.newArrayList();
    /**
     * A List of the default detected Biome Names
     */
    public List<String> DEFAULT_NAMES = StringUtils.newArrayList();
    /**
     * Whether this module is allowed to start and enabled
     */
    private boolean enabled = false;
    /**
     * Whether this module is active and currently in use
     */
    private boolean isInUse = false;
    /**
     * Whether this module has performed an initial retrieval of config items
     */
    private boolean hasScannedConfig = false;
    /**
     * Whether this module has performed an initial retrieval of internal items
     */
    private boolean hasScannedInternals = false;
    /**
     * Whether this module has performed an initial event sync
     */
    private boolean hasInitialized = false;
    /**
     * The Raw Name of the Current Biome the Player is in
     */
    private String RAW_BIOME_NAME;
    /**
     * The Display Name of the Current Biome the Player is in
     */
    private String CURRENT_BIOME_NAME;
    /**
     * The alternative raw name for the Current Biome the Player is in, if any
     */
    private String RAW_BIOME_IDENTIFIER;
    /**
     * The alternative display name for the Current Biome the Player is in, if any
     */
    private String CURRENT_BIOME_IDENTIFIER;
    /**
     * The Player's Current Biome, if any
     */
    private Biome CURRENT_BIOME;

    @Override
    public void clearFieldData() {
        DEFAULT_NAMES.clear();
        BIOME_NAMES.clear();
    }

    @Override
    public void clearAttributes() {
        CURRENT_BIOME = null;
        RAW_BIOME_NAME = null;
        RAW_BIOME_IDENTIFIER = null;
        CURRENT_BIOME_NAME = null;
        CURRENT_BIOME_IDENTIFIER = null;

        CraftPresence.CLIENT.removeArguments("biome", "data.biome");
        CraftPresence.CLIENT.clearForcedData("biome");
        hasInitialized = false;
    }

    @Override
    public void updateData() {
        final Biome newBiome = Biomes.BIOME_LIST[CraftPresence.world.getBiomeAtBlock((int) CraftPresence.player.posX, (int) CraftPresence.player.posY, (int) CraftPresence.player.posZ)];
        final String newBiomeName = newBiome.biomeName;

        final String newBiomeIdentifier = StringUtils.getOrDefault(newBiomeName, MappingUtils.getClassName(newBiome));

        if (!newBiome.equals(CURRENT_BIOME) || !newBiomeName.equals(RAW_BIOME_NAME) || !newBiomeIdentifier.equals(RAW_BIOME_IDENTIFIER)) {
            CURRENT_BIOME = newBiome;

            RAW_BIOME_NAME = StringUtils.getOrDefault(newBiomeName, newBiomeIdentifier);
            RAW_BIOME_IDENTIFIER = newBiomeIdentifier;
            CURRENT_BIOME_NAME = StringUtils.formatIdentifier(RAW_BIOME_NAME, false, !CraftPresence.CONFIG.advancedSettings.formatWords);
            CURRENT_BIOME_IDENTIFIER = StringUtils.formatIdentifier(RAW_BIOME_IDENTIFIER, true, !CraftPresence.CONFIG.advancedSettings.formatWords);

            if (!DEFAULT_NAMES.contains(CURRENT_BIOME_IDENTIFIER)) {
                DEFAULT_NAMES.add(CURRENT_BIOME_IDENTIFIER);
            }
            if (!BIOME_NAMES.contains(CURRENT_BIOME_IDENTIFIER)) {
                BIOME_NAMES.add(CURRENT_BIOME_IDENTIFIER);
            }

            if (!hasInitialized) {
                initPresence();
                hasInitialized = true;
            }
            updatePresence();
        }
    }

    @Override
    public void initPresence() {
        syncArgument("biome.default.icon", () -> CraftPresence.CONFIG.biomeSettings.fallbackBiomeIcon);

        syncArgument("data.biome.instance", () -> CURRENT_BIOME, true);
        syncArgument("data.biome.class", () -> CURRENT_BIOME.getClass(), true);
        syncArgument("data.biome.name", () -> RAW_BIOME_NAME, true);
        syncArgument("data.biome.identifier", () -> RAW_BIOME_IDENTIFIER, true);

        syncArgument("biome.identifier", () -> CURRENT_BIOME_IDENTIFIER, true);
        syncArgument("biome.name", () -> CURRENT_BIOME_NAME, true);

        syncArgument("biome.message", () -> {
            final ModuleData defaultData = getDefaultData();
            final ModuleData currentData = getData(CURRENT_BIOME_IDENTIFIER);

            final String defaultMessage = Config.isValidProperty(defaultData, "textOverride") ? defaultData.getTextOverride() : "";
            return getResult(Config.isValidProperty(currentData, "textOverride") ? currentData.getTextOverride() : defaultMessage, CURRENT_BIOME_IDENTIFIER);
        });
        syncArgument("biome.icon", () -> {
            final ModuleData defaultData = getDefaultData();
            final ModuleData currentData = getData(CURRENT_BIOME_IDENTIFIER);

            final String defaultIcon = Config.isValidProperty(defaultData, "iconOverride") ? defaultData.getIconOverride() : CURRENT_BIOME_IDENTIFIER;
            final String currentIcon = Config.isValidProperty(currentData, "iconOverride") ? currentData.getIconOverride() : defaultIcon;
            return getResult(CraftPresence.CLIENT.imageOf(true, currentIcon, CraftPresence.CONFIG.biomeSettings.fallbackBiomeIcon, CURRENT_BIOME_IDENTIFIER));
        });
        CraftPresence.CLIENT.addForcedData("biome", () -> isInUse() ? getPresenceData(CURRENT_BIOME_IDENTIFIER) : null);
        CraftPresence.CLIENT.syncTimestamp("data.biome.time");
    }

    @Override
    public void updatePresence() {
        // N/A
    }

    /**
     * Retrieves a List of detected Biome Types
     *
     * @return The detected Biome Types found
     */
    private List<Biome> getBiomeTypes() {
        List<Biome> biomeTypes = StringUtils.newArrayList();

        if (Biomes.BIOME_LIST != null) {
            for (Biome biome : Biomes.BIOME_LIST) {
                if (biome != null && !biomeTypes.contains(biome)) {
                    biomeTypes.add(biome);
                }
            }
        }

        if (biomeTypes.isEmpty() && FileUtils.isClassGraphEnabled()) {
            // Fallback: Use Manual Class Lookup
            for (ClassInfo classInfo : FileUtils.getClassNamesMatchingSuperType(Biome.class).values()) {
                if (classInfo != null) {
                    try {
                        Class<?> classObj = FileUtils.loadClass(classInfo.getName());
                        if (classObj != null) {
                            Biome biomeObj = (Biome) classObj.getDeclaredConstructor().newInstance();
                            if (!biomeTypes.contains(biomeObj)) {
                                biomeTypes.add(biomeObj);
                            }
                        }
                    } catch (Throwable ex) {
                        printException(ex);
                    }
                }
            }
        }

        return biomeTypes;
    }

    @Override
    public void getInternalData() {
        for (Biome biome : getBiomeTypes()) {
            if (biome != null) {
                String biomeName = StringUtils.getOrDefault(biome.biomeName, MappingUtils.getClassName(biome));
                String name = StringUtils.formatIdentifier(biomeName, true, !CraftPresence.CONFIG.advancedSettings.formatWords);
                if (!DEFAULT_NAMES.contains(name)) {
                    DEFAULT_NAMES.add(name);
                }
                if (!BIOME_NAMES.contains(name)) {
                    BIOME_NAMES.add(name);
                }
            }
        }
    }

    @Override
    public void getConfigData() {
        for (String biomeEntry : CraftPresence.CONFIG.biomeSettings.biomeData.keySet()) {
            if (!StringUtils.isNullOrEmpty(biomeEntry)) {
                String name = StringUtils.formatIdentifier(biomeEntry, true, !CraftPresence.CONFIG.advancedSettings.formatWords);
                if (!BIOME_NAMES.contains(name)) {
                    BIOME_NAMES.add(name);
                }
            }
        }
    }

    @Override
    public void syncArgument(String argumentName, Supplier<Boolean> condition, Supplier<Object> event, boolean plain) {
        CraftPresence.CLIENT.syncArgument(argumentName, getModuleFunction(condition, event), plain);
    }

    @Override
    public ModuleData getData(String key) {
        return CraftPresence.CONFIG.biomeSettings.biomeData.get(key);
    }

    @Override
    public String getOverrideText(ModuleData data) {
        return CraftPresence.CLIENT.getOverrideText(getPresenceData(data));
    }

    @Override
    public boolean canFetchInternals() {
        return MappingUtils.areMappingsLoaded() && (!FileUtils.isClassGraphEnabled() || FileUtils.canScanClasses());
    }

    @Override
    public boolean hasScannedInternals() {
        return hasScannedInternals;
    }

    @Override
    public void setScannedInternals(final boolean state) {
        hasScannedInternals = state;
    }

    @Override
    public boolean canFetchConfig() {
        return CraftPresence.CONFIG != null;
    }

    @Override
    public boolean hasScannedConfig() {
        return hasScannedConfig;
    }

    @Override
    public void setScannedConfig(final boolean state) {
        hasScannedConfig = state;
    }

    @Override
    public boolean canBeEnabled() {
        return !CraftPresence.CONFIG.hasChanged() ? CraftPresence.CONFIG.generalSettings.detectBiomeData : isEnabled();
    }

    @Override
    public boolean isEnabled() {
        return enabled;
    }

    @Override
    public void setEnabled(boolean state) {
        this.enabled = state;
    }

    @Override
    public boolean canBeUsed() {
        return CraftPresence.player != null;
    }

    @Override
    public boolean isInUse() {
        return isInUse;
    }

    @Override
    public void setInUse(boolean state) {
        this.isInUse = state;
    }
}
