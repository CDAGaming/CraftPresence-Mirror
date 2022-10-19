/*
 * MIT License
 *
 * Copyright (c) 2018 - 2022 CDAGaming (cstack2011@yahoo.com)
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
import com.gitlab.cdagaming.craftpresence.impl.Pair;
import com.gitlab.cdagaming.craftpresence.impl.discord.ArgumentType;
import com.gitlab.cdagaming.craftpresence.utils.MappingUtils;
import com.gitlab.cdagaming.craftpresence.utils.StringUtils;
import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import net.minecraft.core.Holder;
import net.minecraft.core.HolderLookup;
import net.minecraft.core.registries.Registries;
import net.minecraft.data.registries.VanillaRegistries;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.level.dimension.DimensionType;

import java.util.List;
import java.util.Map;
import java.util.Optional;

/**
 * Dimension Utilities used to Parse Dimension Data and handle related RPC Events
 *
 * @author CDAGaming
 */
public class DimensionUtils {
    /**
     * A List of the detected Dimension Type's
     */
    private final List<ResourceLocation> DIMENSION_TYPES = Lists.newArrayList();
    /**
     * The argument format to follow for Rich Presence Data
     */
    private final String argumentFormat = "&DIMENSION&";
    /**
     * The sub-argument format to follow for Rich Presence Data
     */
    private final String subArgumentFormat = "&DIMENSION:";
    /**
     * A Mapping of the Arguments attached to the &DIMENSION& RPC Message placeholder
     */
    private final List<Pair<String, String>> dimensionArgs = Lists.newArrayList();
    /**
     * A Mapping of the Arguments attached to the &ICON& RPC Message placeholder
     */
    private final List<Pair<String, String>> iconArgs = Lists.newArrayList();
    /**
     * Whether this module is active and currently in use
     */
    public boolean isInUse = false;
    /**
     * Whether this module is allowed to start and enabled
     */
    public boolean enabled = false;
    /**
     * A List of the detected Dimension Names
     */
    public List<String> DIMENSION_NAMES = Lists.newArrayList();
    /**
     * The Name of the Current Dimension the Player is in
     */
    private String CURRENT_DIMENSION_NAME;
    /**
     * The alternative name for the Current Dimension the Player is in, if any
     */
    private String CURRENT_DIMENSION_IDENTIFIER;

    /**
     * Clears FULL Data from this Module
     */
    private void emptyData() {
        DIMENSION_NAMES.clear();
        DIMENSION_TYPES.clear();
        clearClientData();
    }

    /**
     * Clears Runtime Client Data from this Module (PARTIAL Clear)
     */
    public void clearClientData() {
        CURRENT_DIMENSION_NAME = null;
        CURRENT_DIMENSION_IDENTIFIER = null;
        dimensionArgs.clear();
        iconArgs.clear();

        isInUse = false;
        CraftPresence.CLIENT.removeArgumentsMatching(subArgumentFormat);
        CraftPresence.CLIENT.initArgument(argumentFormat);
    }

    /**
     * Module Event to Occur on each tick within the Application
     */
    public void onTick() {
        enabled = !CraftPresence.CONFIG.hasChanged ? CraftPresence.CONFIG.detectDimensionData : enabled;
        final boolean needsUpdate = enabled && (
                DIMENSION_NAMES.isEmpty() || DIMENSION_TYPES.isEmpty()
        );

        if (needsUpdate) {
            getDimensions();
        }

        if (enabled) {
            if (CraftPresence.player != null) {
                isInUse = true;
                updateDimensionData();
            } else if (isInUse) {
                clearClientData();
            }
        } else if (isInUse) {
            emptyData();
        }
    }

    /**
     * Synchronizes Data related to this module, if needed
     */
    private void updateDimensionData() {
        final ResourceLocation newDimensionType = CraftPresence.player.level.dimension().location();
        final String newDimensionName = StringUtils.formatIdentifier(newDimensionType.toString(), false, !CraftPresence.CONFIG.formatWords);

        final String newDimension_primaryIdentifier = StringUtils.formatIdentifier(newDimensionType.toString(), true, !CraftPresence.CONFIG.formatWords);
        final String newDimension_alternativeIdentifier = StringUtils.formatIdentifier(MappingUtils.getClassName(newDimensionType), true, !CraftPresence.CONFIG.formatWords);
        final String newDimension_Identifier = !StringUtils.isNullOrEmpty(newDimension_primaryIdentifier) ? newDimension_primaryIdentifier : newDimension_alternativeIdentifier;

        if (!newDimensionName.equals(CURRENT_DIMENSION_NAME) || !newDimension_Identifier.equals(CURRENT_DIMENSION_IDENTIFIER)) {
            CURRENT_DIMENSION_NAME = !StringUtils.isNullOrEmpty(newDimensionName) ? newDimensionName : newDimension_Identifier;
            CURRENT_DIMENSION_IDENTIFIER = newDimension_Identifier;

            if (!DIMENSION_NAMES.contains(newDimension_Identifier)) {
                DIMENSION_NAMES.add(newDimension_Identifier);
            }
            if (!DIMENSION_TYPES.contains(newDimensionType)) {
                DIMENSION_TYPES.add(newDimensionType);
            }

            updateDimensionPresence();
        }
    }

    /**
     * Updates RPC Data related to this Module
     */
    public void updateDimensionPresence() {
        // Form Dimension Argument List
        dimensionArgs.clear();
        iconArgs.clear();

        final String defaultDimensionMessage = StringUtils.getConfigPart(CraftPresence.CONFIG.dimensionMessages, "default", 0, 1, CraftPresence.CONFIG.splitCharacter, null);
        final String currentDimensionMessage = StringUtils.getConfigPart(CraftPresence.CONFIG.dimensionMessages, CURRENT_DIMENSION_IDENTIFIER, 0, 1, CraftPresence.CONFIG.splitCharacter, defaultDimensionMessage);
        final String currentDimensionIcon = StringUtils.getConfigPart(CraftPresence.CONFIG.dimensionMessages, CURRENT_DIMENSION_IDENTIFIER, 0, 2, CraftPresence.CONFIG.splitCharacter, CURRENT_DIMENSION_IDENTIFIER);
        final String formattedIconKey = StringUtils.formatAsIcon(currentDimensionIcon.replace(" ", "_"));

        dimensionArgs.add(new Pair<>("&DIMENSION&", CURRENT_DIMENSION_NAME));

        iconArgs.add(new Pair<>("&ICON&", CraftPresence.CONFIG.defaultDimensionIcon));

        // Add applicable args as sub-placeholders
        for (Pair<String, String> argumentData : dimensionArgs) {
            CraftPresence.CLIENT.syncArgument(subArgumentFormat + argumentData.getFirst().substring(1), argumentData.getSecond(), ArgumentType.Text);
        }
        for (Pair<String, String> argumentData : iconArgs) {
            CraftPresence.CLIENT.syncArgument(subArgumentFormat + argumentData.getFirst().substring(1), argumentData.getSecond(), ArgumentType.Image);
        }

        // Add All Generalized Arguments, if any
        if (!CraftPresence.CLIENT.generalArgs.isEmpty()) {
            StringUtils.addEntriesNotPresent(dimensionArgs, CraftPresence.CLIENT.generalArgs);
        }

        final String CURRENT_DIMENSION_ICON = StringUtils.sequentialReplaceAnyCase(formattedIconKey, iconArgs);
        final String CURRENT_DIMENSION_MESSAGE = StringUtils.sequentialReplaceAnyCase(currentDimensionMessage, dimensionArgs);

        CraftPresence.CLIENT.syncArgument(argumentFormat, CURRENT_DIMENSION_MESSAGE, ArgumentType.Text);
        CraftPresence.CLIENT.syncArgument(argumentFormat, CraftPresence.CLIENT.imageOf(argumentFormat, true, CURRENT_DIMENSION_ICON, CraftPresence.CONFIG.defaultDimensionIcon), ArgumentType.Image);
    }

    /**
     * Retrieves a List of detected Dimension Types
     *
     * @return The detected Dimension Types found
     */
    private List<ResourceLocation> getDimensionTypes() {
        List<ResourceLocation> dimensionTypes = Lists.newArrayList();
        Optional<HolderLookup.RegistryLookup<DimensionType>> dimensionRegistry = VanillaRegistries.createLookup().lookup(Registries.DIMENSION_TYPE);

        if (dimensionRegistry.isPresent()) {
            List<Holder.Reference<DimensionType>> defaultDimensionTypes = dimensionRegistry.get().listElements().toList();

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

    /**
     * Updates and Initializes Module Data, based on found Information
     */
    public void getDimensions() {
        for (ResourceLocation TYPE : getDimensionTypes()) {
            if (TYPE != null) {
                String dimensionName = !StringUtils.isNullOrEmpty(TYPE.toString()) ? TYPE.toString() : MappingUtils.getClassName(TYPE);
                String name = StringUtils.formatIdentifier(dimensionName, true, !CraftPresence.CONFIG.formatWords);
                if (!DIMENSION_NAMES.contains(name)) {
                    DIMENSION_NAMES.add(name);
                }
                if (!DIMENSION_TYPES.contains(TYPE)) {
                    DIMENSION_TYPES.add(TYPE);
                }
            }
        }

        for (String dimensionMessage : CraftPresence.CONFIG.dimensionMessages) {
            if (!StringUtils.isNullOrEmpty(dimensionMessage)) {
                final String[] part = dimensionMessage.split(CraftPresence.CONFIG.splitCharacter);
                if (!StringUtils.isNullOrEmpty(part[0])) {
                    String name = StringUtils.formatIdentifier(part[0], true, !CraftPresence.CONFIG.formatWords);
                    if (!DIMENSION_NAMES.contains(name)) {
                        DIMENSION_NAMES.add(name);
                    }
                }
            }
        }
    }

    /**
     * Generate a parsable display string for the argument data provided
     *
     * @param types The argument types to interpret
     * @return the parsable string
     */
    public String generateArgumentMessage(ArgumentType... types) {
        types = (types != null && types.length > 0 ? types : ArgumentType.values());
        final Map<ArgumentType, List<String>> argumentData = Maps.newHashMap();
        List<String> queuedEntries;
        for (ArgumentType type : types) {
            queuedEntries = Lists.newArrayList();
            if (type == ArgumentType.Image) {
                queuedEntries.add(subArgumentFormat + "ICON&");
            } else if (type == ArgumentType.Text) {
                queuedEntries.add(subArgumentFormat + "DIMENSION&");
            }
            argumentData.put(type, queuedEntries);
        }
        return CraftPresence.CLIENT.generateArgumentMessage(argumentFormat, subArgumentFormat, argumentData);
    }
}
