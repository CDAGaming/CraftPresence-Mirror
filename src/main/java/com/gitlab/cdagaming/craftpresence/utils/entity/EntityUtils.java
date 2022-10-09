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

package com.gitlab.cdagaming.craftpresence.utils.entity;

import com.gitlab.cdagaming.craftpresence.CraftPresence;
import com.gitlab.cdagaming.craftpresence.ModUtils;
import com.gitlab.cdagaming.craftpresence.impl.Pair;
import com.gitlab.cdagaming.craftpresence.impl.discord.ArgumentType;
import com.gitlab.cdagaming.craftpresence.utils.StringUtils;
import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import net.minecraft.client.gui.GuiPlayerInfo;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityList;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.util.EnumMovingObjectType;

import java.util.List;
import java.util.Map;

/**
 * Entity Utilities used to Parse Entity Data and handle related RPC Events
 *
 * @author CDAGaming
 */
public class EntityUtils {
    /**
     * A List of the detected Entity Class Names
     */
    private final List<String> ENTITY_CLASSES = Lists.newArrayList();
    /**
     * A Mapping of the Arguments attached to the &TARGETENTITY& RPC Message placeholder
     */
    private final List<Pair<String, String>> entityTargetArgs = Lists.newArrayList();
    /**
     * A Mapping of the Arguments attached to the &RIDINGENTITY& RPC Message placeholder
     */
    private final List<Pair<String, String>> entityRidingArgs = Lists.newArrayList();
    /**
     * Whether this module is active and currently in use
     */
    public boolean isInUse = false;
    /**
     * Whether this module is allowed to start and enabled
     */
    public boolean enabled = false;
    /**
     * The Player's Currently Targeted Entity Name, if any
     */
    public String CURRENT_TARGET_NAME;
    /**
     * The Player's Currently Riding Entity Name, if any
     */
    public String CURRENT_RIDING_NAME;
    /**
     * A List of the detected Entity Names
     */
    public List<String> ENTITY_NAMES = Lists.newArrayList();
    /**
     * The Player's Currently Targeted Entity's Nbt Tags, if any
     */
    public List<String> CURRENT_TARGET_TAGS = Lists.newArrayList();
    /**
     * The Player's Currently Riding Entity's Nbt Tags, if any
     */
    public List<String> CURRENT_RIDING_TAGS = Lists.newArrayList();
    /**
     * The Player's Current Target Entity, if any
     */
    public Entity CURRENT_TARGET;
    /**
     * The Player's Current Riding Entity, if any
     */
    public Entity CURRENT_RIDING;
    /**
     * The Player's Current Targeted Entity's Tag, if any
     */
    private NBTTagCompound CURRENT_TARGET_TAG;
    /**
     * The Player's Current Riding Entity's Tag, if any
     */
    private NBTTagCompound CURRENT_RIDING_TAG;

    /**
     * Clears FULL Data from this Module
     */
    private void emptyData() {
        ENTITY_NAMES.clear();
        ENTITY_CLASSES.clear();
        clearClientData();
    }

    /**
     * Clears Runtime Client Data from this Module (PARTIAL Clear)
     */
    public void clearClientData() {
        CURRENT_TARGET = null;
        CURRENT_RIDING = null;
        CURRENT_TARGET_NAME = null;
        CURRENT_RIDING_NAME = null;
        CURRENT_TARGET_TAG = null;
        CURRENT_RIDING_TAG = null;

        CURRENT_TARGET_TAGS.clear();
        CURRENT_RIDING_TAGS.clear();

        entityTargetArgs.clear();
        entityRidingArgs.clear();

        isInUse = false;
        CraftPresence.CLIENT.removeArgumentsMatching(ArgumentType.Text, "&TARGETENTITY:", "&RIDINGENTITY:");
        CraftPresence.CLIENT.initArgument(ArgumentType.Text, "&TARGETENTITY&", "&RIDINGENTITY&");
    }

    /**
     * Module Event to Occur on each tick within the Application
     */
    public void onTick() {
        enabled = !CraftPresence.CONFIG.hasChanged ? CraftPresence.CONFIG.enablePerEntity : enabled;
        final boolean needsUpdate = enabled && (ENTITY_NAMES.isEmpty() || ENTITY_CLASSES.isEmpty());

        if (needsUpdate) {
            getEntities();
        }

        if (enabled) {
            if (CraftPresence.player != null) {
                isInUse = true;
                updateEntityData();
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
    private void updateEntityData() {
        final Entity NEW_CURRENT_TARGET = CraftPresence.instance.objectMouseOver != null && CraftPresence.instance.objectMouseOver.typeOfHit == EnumMovingObjectType.ENTITY ? CraftPresence.instance.objectMouseOver.entityHit : null;
        final Entity NEW_CURRENT_RIDING = CraftPresence.player.ridingEntity;

        String NEW_CURRENT_TARGET_NAME, NEW_CURRENT_RIDING_NAME;

        // Note: Unlike getEntities, this does NOT require Server Module to be enabled
        // Users are still free to manually add Uuid's as they please for this module
        if (NEW_CURRENT_TARGET instanceof EntityPlayer) {
            final EntityPlayer NEW_CURRENT_PLAYER_TARGET = (EntityPlayer) NEW_CURRENT_TARGET;
            NEW_CURRENT_TARGET_NAME = StringUtils.stripColors(NEW_CURRENT_PLAYER_TARGET.getDisplayName());
        } else {
            NEW_CURRENT_TARGET_NAME = NEW_CURRENT_TARGET != null ?
                    StringUtils.stripColors(NEW_CURRENT_TARGET.getEntityName()) : "";
        }

        if (NEW_CURRENT_RIDING instanceof EntityPlayer) {
            final EntityPlayer NEW_CURRENT_PLAYER_RIDING = (EntityPlayer) NEW_CURRENT_RIDING;
            NEW_CURRENT_RIDING_NAME = StringUtils.stripColors(NEW_CURRENT_PLAYER_RIDING.getDisplayName());
        } else {
            NEW_CURRENT_RIDING_NAME = NEW_CURRENT_RIDING != null ?
                    StringUtils.stripColors(NEW_CURRENT_RIDING.getEntityName()) : "";
        }

        final boolean hasTargetChanged = (NEW_CURRENT_TARGET != null &&
                !NEW_CURRENT_TARGET.equals(CURRENT_TARGET) || !NEW_CURRENT_TARGET_NAME.equals(CURRENT_TARGET_NAME)) ||
                (NEW_CURRENT_TARGET == null && CURRENT_TARGET != null);
        final boolean hasRidingChanged = (NEW_CURRENT_RIDING != null &&
                !NEW_CURRENT_RIDING.equals(CURRENT_RIDING) || !NEW_CURRENT_RIDING_NAME.equals(CURRENT_RIDING_NAME)) ||
                (NEW_CURRENT_RIDING == null && CURRENT_RIDING != null);

        if (hasTargetChanged) {
            CURRENT_TARGET = NEW_CURRENT_TARGET;
            CURRENT_TARGET_TAG = CURRENT_TARGET != null ? CURRENT_TARGET.getEntityData() : null;
            final List<String> NEW_CURRENT_TARGET_TAGS = CURRENT_TARGET_TAG != null ? Lists.newArrayList(CURRENT_TARGET_TAG.getTags()) : Lists.newArrayList();

            if (!NEW_CURRENT_TARGET_TAGS.equals(CURRENT_TARGET_TAGS)) {
                CURRENT_TARGET_TAGS = NEW_CURRENT_TARGET_TAGS;
            }
            CURRENT_TARGET_NAME = NEW_CURRENT_TARGET_NAME;
        }

        if (hasRidingChanged) {
            CURRENT_RIDING = NEW_CURRENT_RIDING;
            CURRENT_RIDING_TAG = CURRENT_RIDING != null ? CURRENT_RIDING.getEntityData() : null;
            final List<String> NEW_CURRENT_RIDING_TAGS = CURRENT_RIDING_TAG != null ? Lists.newArrayList(CURRENT_RIDING_TAG.getTags()) : Lists.newArrayList();

            if (!NEW_CURRENT_RIDING_TAGS.equals(CURRENT_RIDING_TAGS)) {
                CURRENT_RIDING_TAGS = NEW_CURRENT_RIDING_TAGS;
            }
            CURRENT_RIDING_NAME = NEW_CURRENT_RIDING_NAME;
        }

        if (hasTargetChanged || hasRidingChanged) {
            updateEntityPresence();
        }
    }

    /**
     * Updates RPC Data related to this Module
     */
    public void updateEntityPresence() {
        // Retrieve Messages
        final String defaultEntityTargetMessage = StringUtils.getConfigPart(CraftPresence.CONFIG.entityTargetMessages,
                "default", 0, 1, CraftPresence.CONFIG.splitCharacter,
                null);
        final String defaultEntityRidingMessage = StringUtils.getConfigPart(CraftPresence.CONFIG.entityRidingMessages,
                "default", 0, 1, CraftPresence.CONFIG.splitCharacter,
                null);

        final String targetEntityMessage = StringUtils.getConfigPart(CraftPresence.CONFIG.entityTargetMessages,
                CURRENT_TARGET_NAME, 0, 1, CraftPresence.CONFIG.splitCharacter,
                defaultEntityTargetMessage);
        final String ridingEntityMessage = StringUtils.getConfigPart(CraftPresence.CONFIG.entityRidingMessages,
                CURRENT_RIDING_NAME, 0, 1, CraftPresence.CONFIG.splitCharacter,
                defaultEntityRidingMessage);

        // Form Entity Argument List
        entityTargetArgs.clear();
        entityRidingArgs.clear();

        entityTargetArgs.add(new Pair<>("&ENTITY&", getEntityName(CURRENT_TARGET, CURRENT_TARGET_NAME)));
        entityRidingArgs.add(new Pair<>("&ENTITY&", getEntityName(CURRENT_RIDING, CURRENT_RIDING_NAME)));

        // Extend Arguments, if tags available
        if (!CURRENT_TARGET_TAGS.isEmpty()) {
            for (String tagName : CURRENT_TARGET_TAGS) {
                entityTargetArgs.add(new Pair<>("&" + tagName + "&", CURRENT_TARGET_TAG.getTag(tagName).toString()));
            }
        }

        if (!CURRENT_RIDING_TAGS.isEmpty()) {
            for (String tagName : CURRENT_RIDING_TAGS) {
                entityRidingArgs.add(new Pair<>("&" + tagName + "&", CURRENT_RIDING_TAG.getTag(tagName).toString()));
            }
        }

        // Add applicable args as sub-placeholders
        for (Pair<String, String> argumentData : entityTargetArgs) {
            CraftPresence.CLIENT.syncArgument("&TARGETENTITY:" + argumentData.getFirst().substring(1), argumentData.getSecond(), ArgumentType.Text);
        }
        for (Pair<String, String> argumentData : entityRidingArgs) {
            CraftPresence.CLIENT.syncArgument("&RIDINGENTITY:" + argumentData.getFirst().substring(1), argumentData.getSecond(), ArgumentType.Text);
        }

        // Add All Generalized Arguments, if any
        if (!CraftPresence.CLIENT.generalArgs.isEmpty()) {
            StringUtils.addEntriesNotPresent(entityTargetArgs, CraftPresence.CLIENT.generalArgs);
            StringUtils.addEntriesNotPresent(entityRidingArgs, CraftPresence.CLIENT.generalArgs);
        }

        final String CURRENT_TARGET_MESSAGE = StringUtils.sequentialReplaceAnyCase(targetEntityMessage, entityTargetArgs);
        final String CURRENT_RIDING_MESSAGE = StringUtils.sequentialReplaceAnyCase(ridingEntityMessage, entityRidingArgs);

        // NOTE: Only Apply if Entities are not Empty, otherwise Clear Argument
        if (CURRENT_TARGET != null) {
            CraftPresence.CLIENT.syncArgument("&TARGETENTITY&", CURRENT_TARGET_MESSAGE, ArgumentType.Text);
        } else {
            CraftPresence.CLIENT.removeArgumentsMatching(ArgumentType.Text, "&TARGETENTITY:");
            CraftPresence.CLIENT.initArgument(ArgumentType.Text, "&TARGETENTITY&");
        }
        if (CURRENT_RIDING != null) {
            CraftPresence.CLIENT.syncArgument("&RIDINGENTITY&", CURRENT_RIDING_MESSAGE, ArgumentType.Text);
        } else {
            CraftPresence.CLIENT.removeArgumentsMatching(ArgumentType.Text, "&RIDINGENTITY:");
            CraftPresence.CLIENT.initArgument(ArgumentType.Text, "&RIDINGENTITY&");
        }
    }

    /**
     * Generate a parsable display string for the argument data provided
     *
     * @param argumentFormat    The primary argument format to interpret
     * @param subArgumentFormat The secondary (or sub-prefix) argument format to interpret
     * @param types             The argument types to interpret
     * @return the parsable string
     */
    public String generateArgumentMessage(final String argumentFormat, final String subArgumentFormat, ArgumentType... types) {
        types = (types != null && types.length > 0 ? types : ArgumentType.values());
        final Map<ArgumentType, List<String>> argumentData = Maps.newHashMap();
        List<String> queuedEntries;
        for (ArgumentType type : types) {
            queuedEntries = Lists.newArrayList();
            if (type == ArgumentType.Text) {
                queuedEntries.add(subArgumentFormat + "ENTITY&");
            }
            argumentData.put(type, queuedEntries);
        }
        return CraftPresence.CLIENT.generateArgumentMessage(argumentFormat, subArgumentFormat, argumentData);
    }

    /**
     * Retrieves the entities display name, derived from the original supplied name
     *
     * @param entity   The entity to interpret
     * @param original The original entity string name
     * @return The formatted entity display name to use
     */
    public String getEntityName(final Entity entity, final String original) {
        return StringUtils.isValidUuid(original) ? entity.getEntityName() : original;
    }

    /**
     * Retrieves a List of Tags from a Entity name, if currently equipped
     *
     * @param name The Entity Name
     * @return A List of Tags from a Entity name, if currently equipped
     */
    public List<String> getListFromName(String name) {
        name = !StringUtils.isNullOrEmpty(name) ? name : "";
        return name.equalsIgnoreCase(CURRENT_TARGET_NAME) ? CURRENT_TARGET_TAGS
                : name.equalsIgnoreCase(CURRENT_RIDING_NAME) ? CURRENT_RIDING_TAGS : Lists.<String>newArrayList();
    }

    /**
     * Generates Entity Tag Placeholder String
     *
     * @param name         The Entity Name
     * @param addExtraData Whether to add additional data to the string
     * @param tags         A List of the tags associated with the Entity
     * @return The Resulting Entity Tag Placeholder String
     */
    public String generatePlaceholderString(final String name, final boolean addExtraData, final List<String> tags) {
        final StringBuilder finalString = new StringBuilder(addExtraData ? "" : "\n{");
        if (!tags.isEmpty()) {
            for (int i = 0; i < tags.size(); i++) {
                final String tagName = tags.get(i);
                finalString.append(addExtraData ? "\n - " : "").append("&").append(tagName).append("&");

                if (addExtraData) {
                    // If specified, also append the Tag's value to the placeholder String
                    final String tagValue =
                            tags.equals(CURRENT_TARGET_TAGS) ? CURRENT_TARGET_TAG.getTag(tagName).toString() :
                                    tags.equals(CURRENT_RIDING_TAGS) ? CURRENT_RIDING_TAG.getTag(tagName).toString() : null;

                    if (!StringUtils.isNullOrEmpty(tagValue)) {
                        finalString.append(String.format(" (%s \"%s\")",
                                ModUtils.TRANSLATOR.translate("gui.config.message.editor.preview"),
                                (tagValue.length() >= 128) ? "<...>" : tagValue
                        ));
                    }
                } else if (i < tags.size() - 1) {
                    finalString.append(",");
                    if (i % 5 == 4) {
                        finalString.append("\n");
                    }
                }
            }
        }
        if (!addExtraData) {
            finalString.append("}");
        }
        return ((!StringUtils.isNullOrEmpty(name) ? name : "None") + " " + ((!StringUtils.isNullOrEmpty(finalString.toString()) && !finalString.toString().equals("\n{}")) ? finalString.toString() : "\\n - N/A"));
    }

    /**
     * Retrieves and Synchronizes detected Entities
     */
    public void getEntities() {
        if (!EntityList.classToStringMapping.values().isEmpty()) {
            for (Object entityLocationObj : EntityList.classToStringMapping.values()) {
                final String entityLocation = (String) entityLocationObj;
                if (entityLocation != null) {
                    final String entityName = !StringUtils.isNullOrEmpty(entityLocation) ? entityLocation : "generic";
                    final Class<?> entityClass = (Class<?>) EntityList.stringToClassMapping.get(entityLocation);
                    if (entityClass != null) {
                        if (!ENTITY_NAMES.contains(entityName)) {
                            ENTITY_NAMES.add(entityName);
                        }
                        if (!ENTITY_CLASSES.contains(entityClass.getName())) {
                            ENTITY_CLASSES.add(entityClass.getName());
                        }
                    }
                }
            }
        }

        // If Server Data is enabled, allow Uuid's to count as entities
        if (CraftPresence.SERVER.enabled) {
            for (GuiPlayerInfo playerInfo : CraftPresence.SERVER.currentPlayerList) {
                final String uuidString = playerInfo.name;
                if (!StringUtils.isNullOrEmpty(uuidString) && !ENTITY_NAMES.contains(uuidString)) {
                    ENTITY_NAMES.add(uuidString);
                }
            }
        }

        for (String entityTargetMessage : CraftPresence.CONFIG.entityTargetMessages) {
            if (!StringUtils.isNullOrEmpty(entityTargetMessage)) {
                final String[] part = entityTargetMessage.split(CraftPresence.CONFIG.splitCharacter);
                if (!StringUtils.isNullOrEmpty(part[0]) && !ENTITY_NAMES.contains(part[0])) {
                    ENTITY_NAMES.add(part[0]);
                }
            }
        }

        for (String entityRidingMessage : CraftPresence.CONFIG.entityRidingMessages) {
            if (!StringUtils.isNullOrEmpty(entityRidingMessage)) {
                final String[] part = entityRidingMessage.split(CraftPresence.CONFIG.splitCharacter);
                if (!StringUtils.isNullOrEmpty(part[0]) && !ENTITY_NAMES.contains(part[0])) {
                    ENTITY_NAMES.add(part[0]);
                }
            }
        }

        verifyEntities();
    }

    /**
     * Verifies, Synchronizes and Removes any Invalid Items and Blocks from their Lists
     */
    private void verifyEntities() {
        // Add Verification here as needed
    }
}
