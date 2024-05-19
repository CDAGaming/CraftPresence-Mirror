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

package com.gitlab.cdagaming.craftpresence.utils.entity;

import com.gitlab.cdagaming.craftpresence.CraftPresence;
import com.gitlab.cdagaming.craftpresence.config.Config;
import com.gitlab.cdagaming.craftpresence.core.config.element.ModuleData;
import com.gitlab.cdagaming.craftpresence.core.impl.ExtendedModule;
import io.github.cdagaming.unicore.utils.StringUtils;
import net.minecraft.client.network.NetworkPlayerInfo;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityList;
import net.minecraft.util.ResourceLocation;
import net.minecraft.world.World;
import net.minecraft.world.storage.WorldInfo;

import java.util.List;
import java.util.Map;
import java.util.Objects;

/**
 * Entity Utilities used to Parse Entity Data and handle related RPC Events
 *
 * @author CDAGaming
 */
@SuppressWarnings("DuplicatedCode")
public class EntityUtils implements ExtendedModule {
    /**
     * Whether this module is allowed to start and enabled
     */
    public boolean enabled = false;
    /**
     * A List of the detected Entity Names
     */
    public List<String> ENTITY_NAMES = StringUtils.newArrayList();
    /**
     * A List of the default detected Entity Names
     */
    public List<String> DEFAULT_NAMES = StringUtils.newArrayList();
    /**
     * A Mapping representing the link between UUIDs and Player Names
     */
    public Map<String, String> PLAYER_BINDINGS = StringUtils.newHashMap();
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
     * Whether placeholders for the target entity have been initialized
     */
    private boolean hasInitializedTarget = false;
    /**
     * Whether placeholders for the riding entity have been initialized
     */
    private boolean hasInitializedRiding = false;
    /**
     * The Player's Currently Targeted Entity Name, if any
     */
    private String CURRENT_TARGET_NAME;
    /**
     * The Player's Currently Riding Entity Name, if any
     */
    private String CURRENT_RIDING_NAME;
    /**
     * The Player's Current Target Entity, if any
     */
    private Entity CURRENT_TARGET;
    /**
     * The Player's Current Riding Entity, if any
     */
    private Entity CURRENT_RIDING;

    /**
     * Retrieves the entities display name, derived from the original supplied name
     *
     * @param entity          The entity to interpret
     * @param stripFormatting Whether the resulting name should have its formatting stripped
     * @return The formatted entity display name to use
     */
    public static String getName(final Entity entity, final boolean stripFormatting) {
        String result = "";
        if (entity != null) {
            result = StringUtils.getOrDefault(
                    entity.getDisplayName().getFormattedText(),
                    entity.getName()
            );
        }

        if (stripFormatting) {
            result = StringUtils.stripAllFormatting(result);
        }
        return result;
    }

    /**
     * Retrieves the entities display name, derived from the original supplied name
     *
     * @param entity The entity to interpret
     * @return The formatted entity display name to use
     */
    public static String getName(final Entity entity) {
        return getName(entity, true);
    }

    /**
     * Retrieve the weather, utilizing the world
     *
     * @param worldObj The world object to interpret
     * @return the current weather data
     */
    public static String getWeather(final World worldObj) {
        String name = "clear";
        if (worldObj != null) {
            final WorldInfo info = worldObj.getWorldInfo();
            if (info.isThundering()) {
                name = "thunder";
            } else if (info.isRaining()) {
                name = "rain";
            } else {
                name = "clear";
            }
        }
        return name;
    }

    /**
     * Retrieve the weather, utilizing the entity's world
     *
     * @param entity The entity to interpret
     * @return the current weather data
     */
    public static String getWeather(final Entity entity) {
        return getWeather(entity != null ? entity.world : null);
    }

    @Override
    public void emptyData() {
        queueConfigScan();
        queueInternalScan();
        DEFAULT_NAMES.clear();
        ENTITY_NAMES.clear();
        PLAYER_BINDINGS.clear();
        clearClientData();
    }

    @Override
    public void clearClientData() {
        CURRENT_TARGET = null;
        CURRENT_RIDING = null;
        CURRENT_TARGET_NAME = null;
        CURRENT_RIDING_NAME = null;

        setInUse(false);
        CraftPresence.CLIENT.removeArguments("entity", "data.entity");
        CraftPresence.CLIENT.removeForcedData(
                "entity.target", "entity.riding"
        );
        hasInitialized = false;
        hasInitializedTarget = false;
        hasInitializedRiding = false;
    }

    @Override
    public void onTick() {
        enabled = !CraftPresence.CONFIG.hasChanged ? CraftPresence.CONFIG.advancedSettings.enablePerEntity : enabled;
        final boolean needsConfigUpdate = enabled && !hasScannedConfig() && canFetchConfig();
        final boolean needsInternalUpdate = enabled && !hasScannedInternals() && canFetchInternals();

        if (needsConfigUpdate) {
            scanConfigData();
            hasScannedConfig = true;
        }
        if (needsInternalUpdate) {
            scanInternalData();
            hasScannedInternals = true;
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
        final Entity NEW_CURRENT_TARGET = CraftPresence.instance.objectMouseOver != null && CraftPresence.instance.objectMouseOver.entityHit != null ? CraftPresence.instance.objectMouseOver.entityHit : null;
        final Entity NEW_CURRENT_RIDING = CraftPresence.player.getRidingEntity();

        final boolean hasTargetChanged = !Objects.equals(NEW_CURRENT_TARGET, CURRENT_TARGET);
        final boolean hasRidingChanged = !Objects.equals(NEW_CURRENT_RIDING, CURRENT_RIDING);

        if (hasTargetChanged) {
            CURRENT_TARGET = NEW_CURRENT_TARGET;
            CURRENT_TARGET_NAME = getName(CURRENT_TARGET);

            if (CURRENT_TARGET != null) {
                CraftPresence.CLIENT.syncTimestamp("data.entity.target.time");

                if (!DEFAULT_NAMES.contains(CURRENT_TARGET_NAME)) {
                    DEFAULT_NAMES.add(CURRENT_TARGET_NAME);
                }
                if (!ENTITY_NAMES.contains(CURRENT_TARGET_NAME)) {
                    ENTITY_NAMES.add(CURRENT_TARGET_NAME);
                }
            }
        }

        if (hasRidingChanged) {
            CURRENT_RIDING = NEW_CURRENT_RIDING;
            CURRENT_RIDING_NAME = getName(CURRENT_RIDING);

            if (CURRENT_RIDING != null) {
                CraftPresence.CLIENT.syncTimestamp("data.entity.riding.time");

                if (!DEFAULT_NAMES.contains(CURRENT_RIDING_NAME)) {
                    DEFAULT_NAMES.add(CURRENT_RIDING_NAME);
                }
                if (!ENTITY_NAMES.contains(CURRENT_RIDING_NAME)) {
                    ENTITY_NAMES.add(CURRENT_RIDING_NAME);
                }
            }
        }

        if (hasTargetChanged || hasRidingChanged) {
            if (!hasInitialized) {
                initPresence();
                hasInitialized = true;
            }
            updatePresence();
        }
    }

    @Override
    public void initPresence() {
        CraftPresence.CLIENT.syncFunction("entity.default.icon", () -> CraftPresence.CONFIG.advancedSettings.entitySettings.fallbackEntityIcon);
    }

    @Override
    public void updatePresence() {
        // NOTE: Only Apply if Entities are not Empty, otherwise Clear Argument
        if (CURRENT_TARGET != null) {
            if (!hasInitializedTarget) {
                CraftPresence.CLIENT.syncFunction("data.entity.target.instance", () -> CURRENT_TARGET);
                CraftPresence.CLIENT.syncFunction("data.entity.target.class", () -> CURRENT_TARGET.getClass());
                CraftPresence.CLIENT.syncFunction("entity.target.name", () -> CURRENT_TARGET_NAME, true);

                CraftPresence.CLIENT.syncFunction("entity.target.message", () -> {
                    final ModuleData defaultData = CraftPresence.CONFIG.advancedSettings.entitySettings.targetData.get("default");
                    final ModuleData currentData = CraftPresence.CONFIG.advancedSettings.entitySettings.targetData.get(CURRENT_TARGET_NAME);

                    final String defaultMessage = Config.isValidProperty(defaultData, "textOverride") ? defaultData.getTextOverride() : "";
                    return getResult(Config.isValidProperty(currentData, "textOverride") ? currentData.getTextOverride() : defaultMessage, currentData != null ? currentData : defaultData);
                });
                CraftPresence.CLIENT.syncFunction("entity.target.icon", () -> {
                    final ModuleData defaultData = CraftPresence.CONFIG.advancedSettings.entitySettings.targetData.get("default");
                    final ModuleData currentData = CraftPresence.CONFIG.advancedSettings.entitySettings.targetData.get(CURRENT_TARGET_NAME);

                    final String defaultIcon = Config.isValidProperty(defaultData, "iconOverride") ? defaultData.getIconOverride() : CURRENT_TARGET_NAME;
                    final String currentIcon = Config.isValidProperty(currentData, "iconOverride") ? currentData.getIconOverride() : defaultIcon;
                    return getResult(CraftPresence.CLIENT.imageOf("entity.target.icon", true, currentIcon, CraftPresence.CONFIG.advancedSettings.entitySettings.fallbackEntityIcon), currentData != null ? currentData : defaultData);
                });
                CraftPresence.CLIENT.addForcedData("entity.target", () -> {
                    final ModuleData defaultData = CraftPresence.CONFIG.advancedSettings.entitySettings.targetData.get("default");
                    final ModuleData currentData = CraftPresence.CONFIG.advancedSettings.entitySettings.targetData.get(CURRENT_TARGET_NAME);
                    return getPresenceData(currentData != null ? currentData : defaultData);
                });
                hasInitializedTarget = true;
            }
        } else if (hasInitializedTarget) {
            CraftPresence.CLIENT.removeArguments("entity.target", "data.entity.target");
            CraftPresence.CLIENT.removeForcedData("entity.target");
            hasInitializedTarget = false;
        }

        if (CURRENT_RIDING != null) {
            if (!hasInitializedRiding) {
                CraftPresence.CLIENT.syncFunction("data.entity.riding.instance", () -> CURRENT_RIDING);
                CraftPresence.CLIENT.syncFunction("data.entity.riding.class", () -> CURRENT_RIDING.getClass());
                CraftPresence.CLIENT.syncFunction("entity.riding.name", () -> CURRENT_RIDING_NAME, true);

                CraftPresence.CLIENT.syncFunction("entity.riding.message", () -> {
                    final ModuleData defaultData = CraftPresence.CONFIG.advancedSettings.entitySettings.ridingData.get("default");
                    final ModuleData currentData = CraftPresence.CONFIG.advancedSettings.entitySettings.ridingData.get(CURRENT_RIDING_NAME);

                    final String defaultMessage = Config.isValidProperty(defaultData, "textOverride") ? defaultData.getTextOverride() : "";
                    return getResult(Config.isValidProperty(currentData, "textOverride") ? currentData.getTextOverride() : defaultMessage, currentData != null ? currentData : defaultData);
                });
                CraftPresence.CLIENT.syncFunction("entity.riding.icon", () -> {
                    final ModuleData defaultData = CraftPresence.CONFIG.advancedSettings.entitySettings.ridingData.get("default");
                    final ModuleData currentData = CraftPresence.CONFIG.advancedSettings.entitySettings.ridingData.get(CURRENT_RIDING_NAME);

                    final String defaultIcon = Config.isValidProperty(defaultData, "iconOverride") ? defaultData.getIconOverride() : CURRENT_RIDING_NAME;
                    final String currentIcon = Config.isValidProperty(currentData, "iconOverride") ? currentData.getIconOverride() : defaultIcon;
                    return getResult(CraftPresence.CLIENT.imageOf("entity.riding.icon", true, currentIcon, CraftPresence.CONFIG.advancedSettings.entitySettings.fallbackEntityIcon), currentData != null ? currentData : defaultData);
                });
                CraftPresence.CLIENT.addForcedData("entity.riding", () -> {
                    final ModuleData defaultData = CraftPresence.CONFIG.advancedSettings.entitySettings.ridingData.get("default");
                    final ModuleData currentData = CraftPresence.CONFIG.advancedSettings.entitySettings.ridingData.get(CURRENT_RIDING_NAME);
                    return getPresenceData(currentData != null ? currentData : defaultData);
                });
                hasInitializedRiding = true;
            }
        } else if (hasInitializedRiding) {
            CraftPresence.CLIENT.removeArguments("entity.riding", "data.entity.riding");
            CraftPresence.CLIENT.removeForcedData("entity.riding");
            hasInitializedRiding = false;
        }
    }

    @Override
    public void getInternalData() {
        if (!EntityList.getEntityNameList().isEmpty()) {
            for (ResourceLocation entityLocation : EntityList.getEntityNameList()) {
                if (entityLocation != null) {
                    final String entityName = StringUtils.getOrDefault(EntityList.getTranslationName(entityLocation), "generic");
                    if (!DEFAULT_NAMES.contains(entityName)) {
                        DEFAULT_NAMES.add(entityName);
                    }
                    if (!ENTITY_NAMES.contains(entityName)) {
                        ENTITY_NAMES.add(entityName);
                    }
                }
            }
        }

        // If Server Data is enabled, allow Uuid's to count as entities
        if (CraftPresence.SERVER.enabled) {
            for (NetworkPlayerInfo playerInfo : CraftPresence.SERVER.currentPlayerList) {
                if (playerInfo != null) {
                    final String uuidString = playerInfo.getGameProfile().getId().toString();
                    if (!StringUtils.isNullOrEmpty(uuidString)) {
                        if (!ENTITY_NAMES.contains(uuidString)) {
                            ENTITY_NAMES.add(uuidString);
                        }
                        if (!PLAYER_BINDINGS.containsKey(uuidString)) {
                            PLAYER_BINDINGS.put(uuidString, playerInfo.getGameProfile().getName());
                        }
                    }
                }
            }
        }
    }

    @Override
    public void getConfigData() {
        for (String entityTargetEntry : CraftPresence.CONFIG.advancedSettings.entitySettings.targetData.keySet()) {
            if (!StringUtils.isNullOrEmpty(entityTargetEntry) && !ENTITY_NAMES.contains(entityTargetEntry)) {
                ENTITY_NAMES.add(entityTargetEntry);
            }
        }

        for (String entityRidingEntry : CraftPresence.CONFIG.advancedSettings.entitySettings.ridingData.keySet()) {
            if (!StringUtils.isNullOrEmpty(entityRidingEntry) && !ENTITY_NAMES.contains(entityRidingEntry)) {
                ENTITY_NAMES.add(entityRidingEntry);
            }
        }
    }

    @Override
    public String getOverrideText(ModuleData data) {
        return CraftPresence.CLIENT.getOverrideText(getPresenceData(data));
    }

    @Override
    public boolean hasScannedInternals() {
        return hasScannedInternals;
    }

    @Override
    public void queueInternalScan() {
        hasScannedInternals = false;
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
    public void queueConfigScan() {
        hasScannedConfig = false;
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
