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

package com.gitlab.cdagaming.craftpresence.utils.entity;

import com.gitlab.cdagaming.craftpresence.CraftPresence;
import com.gitlab.cdagaming.craftpresence.config.Config;
import com.gitlab.cdagaming.craftpresence.config.element.ModuleData;
import com.gitlab.cdagaming.craftpresence.impl.Module;
import com.gitlab.cdagaming.craftpresence.impl.Pair;
import com.gitlab.cdagaming.craftpresence.utils.StringUtils;
import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import net.minecraft.client.network.NetworkPlayerInfo;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityList;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.util.ResourceLocation;
import net.minecraft.world.World;
import net.minecraft.world.storage.WorldInfo;

import java.util.List;
import java.util.Map;

/**
 * Entity Utilities used to Parse Entity Data and handle related RPC Events
 *
 * @author CDAGaming
 */
@SuppressWarnings("DuplicatedCode")
public class EntityUtils implements Module {
    /**
     * Whether this module is allowed to start and enabled
     */
    public boolean enabled = false;
    /**
     * A List of the detected Entity Names
     */
    public List<String> ENTITY_NAMES = Lists.newArrayList();
    /**
     * A Mapping representing the link between UUIDs and Player Names
     */
    public Map<String, String> PLAYER_BINDINGS = Maps.newHashMap();
    /**
     * Whether this module is active and currently in use
     */
    private boolean isInUse = false;
    /**
     * Whether this module has performed an initial retrieval of items
     */
    private boolean hasScanned = false;
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
     * @param entity   The entity to interpret
     * @param original The original entity string name
     * @return The formatted entity display name to use
     */
    public static String getEntityName(final Entity entity, final String original) {
        return StringUtils.isValidUuid(original) ? entity.getName() : original;
    }

    /**
     * Retrieve the weather, utilizing the world
     *
     * @param worldObj The world object to interpret
     * @return the current weather data
     */
    public static Pair<String, Long> getWeather(final World worldObj) {
        String name = "clear";
        long duration = 0L;
        if (worldObj != null) {
            final WorldInfo info = worldObj.getWorldInfo();
            if (info.isThundering()) {
                name = "thunder";
                duration = info.getThunderTime();
            } else if (info.isRaining()) {
                name = "rain";
                duration = info.getRainTime();
            } else {
                name = "clear";
                duration = info.getCleanWeatherTime();
            }
        }
        return new Pair<>(name, duration);
    }

    /**
     * Retrieve the weather, utilizing the entity's world
     *
     * @param entity The entity to interpret
     * @return the current weather data
     */
    public static Pair<String, Long> getWeather(final Entity entity) {
        return getWeather(entity != null ? entity.world : null);
    }

    @Override
    public void emptyData() {
        hasScanned = false;
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
        CraftPresence.CLIENT.clearOverride(
                "entity.target.message", "entity.target.icon",
                "entity.riding.message", "entity.riding.icon"
        );
    }

    @Override
    public void onTick() {
        enabled = !CraftPresence.CONFIG.hasChanged ? CraftPresence.CONFIG.advancedSettings.enablePerEntity : enabled;
        final boolean needsUpdate = enabled && !hasScanned;

        if (needsUpdate) {
            new Thread(this::getAllData, "CraftPresence-Entity-Lookup").start();
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
        final Entity NEW_CURRENT_TARGET = CraftPresence.instance.objectMouseOver != null && CraftPresence.instance.objectMouseOver.entityHit != null ? CraftPresence.instance.objectMouseOver.entityHit : null;
        final Entity NEW_CURRENT_RIDING = CraftPresence.player.getRidingEntity();

        String NEW_CURRENT_TARGET_NAME, NEW_CURRENT_RIDING_NAME;

        // Note: Unlike getEntities, this does NOT require Server Module to be enabled
        // Users are still free to manually add Uuid's as they please for this module
        if (NEW_CURRENT_TARGET instanceof EntityPlayer) {
            final EntityPlayer NEW_CURRENT_PLAYER_TARGET = (EntityPlayer) NEW_CURRENT_TARGET;
            NEW_CURRENT_TARGET_NAME = StringUtils.stripColors(NEW_CURRENT_PLAYER_TARGET.getGameProfile().getId().toString());
        } else {
            NEW_CURRENT_TARGET_NAME = NEW_CURRENT_TARGET != null ?
                    StringUtils.stripColors(NEW_CURRENT_TARGET.getDisplayName().getFormattedText()) : "";
        }

        if (NEW_CURRENT_RIDING instanceof EntityPlayer) {
            final EntityPlayer NEW_CURRENT_PLAYER_RIDING = (EntityPlayer) NEW_CURRENT_RIDING;
            NEW_CURRENT_RIDING_NAME = StringUtils.stripColors(NEW_CURRENT_PLAYER_RIDING.getGameProfile().getId().toString());
        } else {
            NEW_CURRENT_RIDING_NAME = NEW_CURRENT_RIDING != null ?
                    StringUtils.stripColors(NEW_CURRENT_RIDING.getDisplayName().getFormattedText()) : "";
        }

        final boolean hasTargetChanged = (NEW_CURRENT_TARGET != null &&
                !NEW_CURRENT_TARGET.equals(CURRENT_TARGET) || !NEW_CURRENT_TARGET_NAME.equals(CURRENT_TARGET_NAME)) ||
                (NEW_CURRENT_TARGET == null && CURRENT_TARGET != null);
        final boolean hasRidingChanged = (NEW_CURRENT_RIDING != null &&
                !NEW_CURRENT_RIDING.equals(CURRENT_RIDING) || !NEW_CURRENT_RIDING_NAME.equals(CURRENT_RIDING_NAME)) ||
                (NEW_CURRENT_RIDING == null && CURRENT_RIDING != null);

        if (hasTargetChanged) {
            CURRENT_TARGET = NEW_CURRENT_TARGET;
            CURRENT_TARGET_NAME = NEW_CURRENT_TARGET_NAME;

            if (CURRENT_TARGET != null) {
                CraftPresence.CLIENT.syncTimestamp("data.entity.target.time");
            }
        }

        if (hasRidingChanged) {
            CURRENT_RIDING = NEW_CURRENT_RIDING;
            CURRENT_RIDING_NAME = NEW_CURRENT_RIDING_NAME;

            if (CURRENT_RIDING != null) {
                CraftPresence.CLIENT.syncTimestamp("data.entity.riding.time");
            }
        }

        if (hasTargetChanged || hasRidingChanged) {
            updatePresence();
        }
    }

    @Override
    public void updatePresence() {
        // Form Entity Argument List
        final ModuleData defaultTargetData = CraftPresence.CONFIG.advancedSettings.entitySettings.targetData.get("default");
        final ModuleData defaultRidingData = CraftPresence.CONFIG.advancedSettings.entitySettings.ridingData.get("default");

        final ModuleData currentTargetData = CraftPresence.CONFIG.advancedSettings.entitySettings.targetData.get(CURRENT_TARGET_NAME);
        final ModuleData currentRidingData = CraftPresence.CONFIG.advancedSettings.entitySettings.targetData.get(CURRENT_RIDING_NAME);

        final String defaultTargetMessage = Config.isValidProperty(defaultTargetData, "textOverride") ? defaultTargetData.getTextOverride() : "";
        final String defaultRidingMessage = Config.isValidProperty(defaultRidingData, "textOverride") ? defaultRidingData.getTextOverride() : "";

        final String currentTargetMessage = Config.isValidProperty(currentTargetData, "textOverride") ? currentTargetData.getTextOverride() : defaultTargetMessage;
        final String currentRidingMessage = Config.isValidProperty(currentRidingData, "textOverride") ? currentRidingData.getTextOverride() : defaultRidingMessage;

        final String currentTargetIcon = Config.isValidProperty(currentTargetData, "iconOverride") ? currentTargetData.getIconOverride() : CURRENT_TARGET_NAME;
        final String currentRidingIcon = Config.isValidProperty(currentRidingData, "iconOverride") ? currentRidingData.getIconOverride() : CURRENT_RIDING_NAME;

        final String formattedTargetIcon = CraftPresence.CLIENT.imageOf("entity.target.icon", true, currentTargetIcon, CraftPresence.CONFIG.advancedSettings.entitySettings.fallbackEntityIcon);
        final String formattedRidingIcon = CraftPresence.CLIENT.imageOf("entity.riding.icon", true, currentRidingIcon, CraftPresence.CONFIG.advancedSettings.entitySettings.fallbackEntityIcon);

        CraftPresence.CLIENT.syncArgument("entity.default.icon", CraftPresence.CONFIG.advancedSettings.entitySettings.fallbackEntityIcon);

        // NOTE: Only Apply if Entities are not Empty, otherwise Clear Argument
        if (CURRENT_TARGET != null) {
            CraftPresence.CLIENT.syncArgument("data.entity.target.instance", CURRENT_TARGET);
            CraftPresence.CLIENT.syncArgument("data.entity.target.class", CURRENT_TARGET.getClass());
            CraftPresence.CLIENT.syncArgument("entity.target.name", getEntityName(CURRENT_TARGET, CURRENT_TARGET_NAME));

            CraftPresence.CLIENT.syncOverride(currentTargetData != null ? currentTargetData : defaultTargetData, "entity.target.message", "entity.target.icon");
            CraftPresence.CLIENT.syncArgument("entity.target.message", currentTargetMessage);
            CraftPresence.CLIENT.syncArgument("entity.target.icon", formattedTargetIcon);
        } else {
            CraftPresence.CLIENT.removeArguments("entity.target", "data.entity.target");
        }

        if (CURRENT_RIDING != null) {
            CraftPresence.CLIENT.syncArgument("data.entity.riding.instance", CURRENT_RIDING);
            CraftPresence.CLIENT.syncArgument("data.entity.riding.class", CURRENT_RIDING.getClass());
            CraftPresence.CLIENT.syncArgument("entity.riding.name", getEntityName(CURRENT_RIDING, CURRENT_RIDING_NAME));

            CraftPresence.CLIENT.syncOverride(currentRidingData != null ? currentRidingData : defaultRidingData, "entity.riding.message", "entity.riding.icon");
            CraftPresence.CLIENT.syncArgument("entity.riding.message", currentRidingMessage);
            CraftPresence.CLIENT.syncArgument("entity.riding.icon", formattedRidingIcon);
        } else {
            CraftPresence.CLIENT.removeArguments("entity.riding", "data.entity.riding");
        }
    }

    @Override
    public void getAllData() {
        if (!EntityList.getEntityNameList().isEmpty()) {
            for (ResourceLocation entityLocation : EntityList.getEntityNameList()) {
                if (entityLocation != null) {
                    final String entityName = StringUtils.getOrDefault(EntityList.getTranslationName(entityLocation), "generic");
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
