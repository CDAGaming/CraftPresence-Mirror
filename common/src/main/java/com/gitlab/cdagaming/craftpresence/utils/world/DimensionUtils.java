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

package com.gitlab.cdagaming.craftpresence.utils.world;

import com.gitlab.cdagaming.craftpresence.CraftPresence;
import com.gitlab.cdagaming.craftpresence.config.Config;
import com.gitlab.cdagaming.craftpresence.core.config.element.ModuleData;
import com.gitlab.cdagaming.craftpresence.core.impl.Module;
import io.github.cdagaming.unicore.utils.MappingUtils;
import io.github.cdagaming.unicore.utils.StringUtils;
import net.minecraft.core.Holder;
import net.minecraft.core.HolderLookup;
import net.minecraft.core.registries.Registries;
import net.minecraft.data.registries.VanillaRegistries;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.dimension.DimensionType;

import java.util.List;
import java.util.Optional;

/**
 * Dimension Utilities used to Parse Dimension Data and handle related RPC Events
 *
 * @author CDAGaming
 */
@SuppressWarnings("DuplicatedCode")
public class DimensionUtils implements Module {
    /**
     * Whether this module is allowed to start and enabled
     */
    public boolean enabled = false;
    /**
     * A List of the detected Dimension Names
     */
    public List<String> DIMENSION_NAMES = StringUtils.newArrayList();
    /**
     * A List of the default detected Dimension Names
     */
    public List<String> DEFAULT_NAMES = StringUtils.newArrayList();
    /**
     * Whether this module is active and currently in use
     */
    private boolean isInUse = false;
    /**
     * Whether this module has performed an initial retrieval of items
     */
    private boolean hasScanned = false;
    /**
     * The Name of the Current Dimension the Player is in
     */
    private String CURRENT_DIMENSION_NAME;
    /**
     * The alternative name for the Current Dimension the Player is in, if any
     */
    private String CURRENT_DIMENSION_IDENTIFIER;
    /**
     * The Player's Current Dimension, if any
     */
    private Level CURRENT_DIMENSION;

    @Override
    public void emptyData() {
        hasScanned = false;
        DEFAULT_NAMES.clear();
        DIMENSION_NAMES.clear();
        clearClientData();
    }

    @Override
    public void clearClientData() {
        CURRENT_DIMENSION = null;
        CURRENT_DIMENSION_NAME = null;
        CURRENT_DIMENSION_IDENTIFIER = null;

        setInUse(false);
        CraftPresence.CLIENT.removeArguments("dimension", "data.dimension");
        CraftPresence.CLIENT.clearOverride("dimension.message", "dimension.icon");
    }

    @Override
    public void onTick() {
        enabled = !CraftPresence.CONFIG.hasChanged ? CraftPresence.CONFIG.generalSettings.detectDimensionData : enabled;
        final boolean needsUpdate = enabled && !hasScanned && canFetchData();

        if (needsUpdate) {
            scanForData();
            hasScanned = true;
        }

        if (enabled) {
            if (CraftPresence.player != null) {
                setInUse(true);
                updateData();
            } else if (isInUse()) {
                clearClientData();
            }
        } else if (isInUse()) {
            emptyData();
        }
    }

    @Override
    public void updateData() {
        final Level newProvider = CraftPresence.player.level();
        final ResourceLocation newDimensionType = newProvider.dimension().location();
        final String newDimensionName = StringUtils.formatIdentifier(newDimensionType.toString(), false, !CraftPresence.CONFIG.advancedSettings.formatWords);

        final String newDimension_primaryIdentifier = StringUtils.formatIdentifier(newDimensionType.toString(), true, !CraftPresence.CONFIG.advancedSettings.formatWords);
        final String newDimension_alternativeIdentifier = StringUtils.formatIdentifier(MappingUtils.getClassName(newDimensionType), true, !CraftPresence.CONFIG.advancedSettings.formatWords);
        final String newDimension_Identifier = StringUtils.getOrDefault(newDimension_primaryIdentifier, newDimension_alternativeIdentifier);

        if (!newDimensionName.equals(CURRENT_DIMENSION_NAME) || !newDimension_Identifier.equals(CURRENT_DIMENSION_IDENTIFIER)) {
            CURRENT_DIMENSION = newProvider;
            CURRENT_DIMENSION_NAME = StringUtils.getOrDefault(newDimensionName, newDimension_Identifier);
            CURRENT_DIMENSION_IDENTIFIER = newDimension_Identifier;

            if (!DEFAULT_NAMES.contains(newDimension_Identifier)) {
                DEFAULT_NAMES.add(newDimension_Identifier);
            }
            if (!DIMENSION_NAMES.contains(newDimension_Identifier)) {
                DIMENSION_NAMES.add(newDimension_Identifier);
            }

            updatePresence();
        }
    }

    @Override
    public void updatePresence() {
        // Form Dimension Argument List
        final ModuleData defaultData = CraftPresence.CONFIG.dimensionSettings.dimensionData.get("default");
        final ModuleData currentData = CraftPresence.CONFIG.dimensionSettings.dimensionData.get(CURRENT_DIMENSION_IDENTIFIER);

        final String defaultMessage = Config.isValidProperty(defaultData, "textOverride") ? defaultData.getTextOverride() : "";
        final String currentMessage = Config.isValidProperty(currentData, "textOverride") ? currentData.getTextOverride() : defaultMessage;
        final String defaultIcon = Config.isValidProperty(defaultData, "iconOverride") ? defaultData.getIconOverride() : CURRENT_DIMENSION_IDENTIFIER;
        final String currentIcon = Config.isValidProperty(currentData, "iconOverride") ? currentData.getIconOverride() : defaultIcon;
        final String formattedIcon = CraftPresence.CLIENT.imageOf("dimension.icon", true, currentIcon, CraftPresence.CONFIG.dimensionSettings.fallbackDimensionIcon);

        CraftPresence.CLIENT.syncArgument("dimension.default.icon", CraftPresence.CONFIG.dimensionSettings.fallbackDimensionIcon);

        CraftPresence.CLIENT.syncArgument("data.dimension.instance", CURRENT_DIMENSION);
        CraftPresence.CLIENT.syncArgument("data.dimension.class", CURRENT_DIMENSION.getClass());
        CraftPresence.CLIENT.syncArgument("dimension.name", CURRENT_DIMENSION_NAME);

        CraftPresence.CLIENT.syncOverride(currentData != null ? currentData : defaultData, "dimension.message", "dimension.icon");
        CraftPresence.CLIENT.syncArgument("dimension.message", currentMessage);
        CraftPresence.CLIENT.syncArgument("dimension.icon", formattedIcon);
        CraftPresence.CLIENT.syncTimestamp("data.dimension.time");
    }

    /**
     * Retrieves a List of detected Dimension Types
     *
     * @return The detected Dimension Types found
     */
    private List<ResourceLocation> getDimensionTypes() {
        List<ResourceLocation> dimensionTypes = StringUtils.newArrayList();
        Optional<HolderLookup.RegistryLookup<DimensionType>> dimensionRegistry = VanillaRegistries.createLookup().lookup(Registries.DIMENSION_TYPE);

        if (dimensionRegistry.isPresent()) {
            List<Holder.Reference<DimensionType>> defaultDimensionTypes = StringUtils.newArrayList(dimensionRegistry.get().listElements().toList());

            if (!defaultDimensionTypes.isEmpty()) {
                for (Holder.Reference<DimensionType> type : defaultDimensionTypes) {
                    if (type != null) {
                        dimensionTypes.add(type.key().location());
                    }
                }
            }
        }

        return dimensionTypes;
    }

    @Override
    public void getAllData() {
        for (ResourceLocation TYPE : getDimensionTypes()) {
            if (TYPE != null) {
                String dimensionName = StringUtils.getOrDefault(TYPE.toString(), MappingUtils.getClassName(TYPE));
                String name = StringUtils.formatIdentifier(dimensionName, true, !CraftPresence.CONFIG.advancedSettings.formatWords);
                if (!DEFAULT_NAMES.contains(name)) {
                    DEFAULT_NAMES.add(name);
                }
                if (!DIMENSION_NAMES.contains(name)) {
                    DIMENSION_NAMES.add(name);
                }
            }
        }

        for (String dimensionEntry : CraftPresence.CONFIG.dimensionSettings.dimensionData.keySet()) {
            if (!StringUtils.isNullOrEmpty(dimensionEntry)) {
                String name = StringUtils.formatIdentifier(dimensionEntry, true, !CraftPresence.CONFIG.advancedSettings.formatWords);
                if (!DIMENSION_NAMES.contains(name)) {
                    DIMENSION_NAMES.add(name);
                }
            }
        }
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
    public boolean isInUse() {
        return isInUse;
    }

    @Override
    public void setInUse(boolean state) {
        this.isInUse = state;
    }
}
