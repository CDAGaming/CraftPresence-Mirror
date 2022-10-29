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
import net.minecraft.block.Block;
import net.minecraft.init.Items;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.util.ResourceLocation;

import java.util.List;
import java.util.Map;

/**
 * Tile Entity Utilities used to Parse TileEntity (Blocks and Items) Data and handle related RPC Events
 *
 * @author CDAGaming
 */
@SuppressWarnings("ConstantConditions")
public class TileEntityUtils {
    /**
     * A List of the detected Block Names
     */
    public final List<String> BLOCK_NAMES = Lists.newArrayList();
    /**
     * A List of the detected Item Names
     */
    public final List<String> ITEM_NAMES = Lists.newArrayList();
    /**
     * A Mapping of the Arguments attached to the &TILEENTITY& Equipment placeholders
     */
    private final List<Pair<String, String>> equipmentArgs = Lists.newArrayList();
    /**
     * A List of the detected Block Class Names
     */
    private final List<String> BLOCK_CLASSES = Lists.newArrayList();
    /**
     * A List of the detected Item Class Names
     */
    private final List<String> ITEM_CLASSES = Lists.newArrayList();
    /**
     * A List of the detected Tile Entity (Blocks + Items) Class Names
     */
    private final List<String> TILE_ENTITY_CLASSES = Lists.newArrayList();
    /**
     * An Instance of an Empty Item
     */
    private final Item EMPTY_ITEM = null;
    /**
     * An Instance of an Empty ItemStack
     */
    private final ItemStack EMPTY_STACK = new ItemStack(EMPTY_ITEM);
    /**
     * The argument format to follow for Rich Presence Data
     */
    private final String argumentFormat = "&TILEENTITY&";
    /**
     * The sub-argument format to follow for Rich Presence Data
     */
    private final String subArgumentFormat = "&TILEENTITY:";
    /**
     * A Mapping of the Arguments attached to the &TILEENTITY& RPC Message sub-argument placeholders
     */
    private final Map<String, List<Pair<String, String>>> subArgumentData = Maps.newHashMap();
    /**
     * Whether this module is active and currently in use
     */
    public boolean isInUse = false;
    /**
     * Whether this module is allowed to start and enabled
     */
    public boolean enabled = false;
    /**
     * A List of the detected Entity (Blocks + Items) Names
     */
    public List<String> TILE_ENTITY_NAMES = Lists.newArrayList();
    /**
     * A List storing a mapping of Tile Entity textures, mapped as entityName:entityTexture
     */
    public Map<String, ResourceLocation> TILE_ENTITY_RESOURCES = Maps.newHashMap();
    /**
     * The Player's Current Main Hand Item's Nbt Tags, if any
     */
    public List<String> CURRENT_MAIN_HAND_ITEM_TAGS = Lists.newArrayList();
    /**
     * The Player's Current Off Hand Item's Nbt Tags, if any
     */
    public List<String> CURRENT_OFFHAND_ITEM_TAGS = Lists.newArrayList();
    /**
     * The Player's Currently equipped Helmet's Nbt Tags, if any
     */
    public List<String> CURRENT_HELMET_TAGS = Lists.newArrayList();
    /**
     * The Player's Currently equipped Chest's Nbt Tags, if any
     */
    public List<String> CURRENT_CHEST_TAGS = Lists.newArrayList();
    /**
     * The Player's Currently equipped Leggings Nbt Tags, if any
     */
    public List<String> CURRENT_LEGS_TAGS = Lists.newArrayList();
    /**
     * The Player's Currently equipped Boots Nbt Tags, if any
     */
    public List<String> CURRENT_BOOTS_TAGS = Lists.newArrayList();
    /**
     * A Mapping of the Arguments attached to the &TILEENTITY& RPC Message placeholder
     */
    private List<Pair<String, String>> tileEntityArgs = Lists.newArrayList();
    /**
     * The Player's Current Main Hand Item, if any
     */
    private ItemStack CURRENT_MAIN_HAND_ITEM;
    /**
     * The Player's Current Off Hand Item, if any
     */
    private ItemStack CURRENT_OFFHAND_ITEM;
    /**
     * The Player's Currently Equipped Helmet, if any
     */
    private ItemStack CURRENT_HELMET;
    /**
     * The Player's Currently Equipped ChestPlate, if any
     */
    private ItemStack CURRENT_CHEST;
    /**
     * The Player's Currently Equipped Leggings, if any
     */
    private ItemStack CURRENT_LEGS;
    /**
     * The Player's Currently Equipped Boots, if any
     */
    private ItemStack CURRENT_BOOTS;
    /**
     * The Player's Current Main Hand Item Name, if any
     */
    private String CURRENT_MAIN_HAND_ITEM_NAME;
    /**
     * The Player's Current Off Hand Item Name, if any
     */
    private String CURRENT_OFFHAND_ITEM_NAME;
    /**
     * The Player's Currently Equipped Helmet Name, if any
     */
    private String CURRENT_HELMET_NAME;
    /**
     * The Player's Currently Equipped ChestPlate Name, if any
     */
    private String CURRENT_CHEST_NAME;
    /**
     * The Player's Currently Equipped Leggings Name, if any
     */
    private String CURRENT_LEGS_NAME;
    /**
     * The Player's Currently Equipped Boots Name, if any
     */
    private String CURRENT_BOOTS_NAME;
    /**
     * The Player's Current Main Hand Item's Tag, if any
     */
    private NBTTagCompound CURRENT_MAIN_HAND_ITEM_TAG;
    /**
     * The Player's Current Off Hand Item's Tag, if any
     */
    private NBTTagCompound CURRENT_OFFHAND_ITEM_TAG;
    /**
     * The Player's Currently equipped Helmet's Tag, if any
     */
    private NBTTagCompound CURRENT_HELMET_TAG;
    /**
     * The Player's Currently equipped Chest's Tag, if any
     */
    private NBTTagCompound CURRENT_CHEST_TAG;
    /**
     * The Player's Currently equipped Leggings Tag, if any
     */
    private NBTTagCompound CURRENT_LEGS_TAG;
    /**
     * The Player's Currently equipped Boots Tag, if any
     */
    private NBTTagCompound CURRENT_BOOTS_TAG;
    /**
     * If the Player doesn't have any Items in the Critical Slots such as equipped items or armor
     */
    private boolean allItemsEmpty = false;

    /**
     * Clears FULL Data from this Module
     */
    private void emptyData() {
        BLOCK_NAMES.clear();
        BLOCK_CLASSES.clear();
        ITEM_NAMES.clear();
        ITEM_CLASSES.clear();
        TILE_ENTITY_NAMES.clear();
        TILE_ENTITY_CLASSES.clear();
        TILE_ENTITY_RESOURCES.clear();
        clearClientData();
    }

    /**
     * Clears Runtime Client Data from this Module (PARTIAL Clear)
     */
    public void clearClientData() {
        CURRENT_MAIN_HAND_ITEM = EMPTY_STACK;
        CURRENT_OFFHAND_ITEM = EMPTY_STACK;
        CURRENT_MAIN_HAND_ITEM_NAME = null;
        CURRENT_OFFHAND_ITEM_NAME = null;

        CURRENT_HELMET = EMPTY_STACK;
        CURRENT_CHEST = EMPTY_STACK;
        CURRENT_LEGS = EMPTY_STACK;
        CURRENT_BOOTS = EMPTY_STACK;
        CURRENT_HELMET_NAME = null;
        CURRENT_CHEST_NAME = null;
        CURRENT_LEGS_NAME = null;
        CURRENT_BOOTS_NAME = null;

        CURRENT_MAIN_HAND_ITEM_TAG = null;
        CURRENT_OFFHAND_ITEM_TAG = null;
        CURRENT_HELMET_TAG = null;
        CURRENT_CHEST_TAG = null;
        CURRENT_LEGS_TAG = null;
        CURRENT_BOOTS_TAG = null;

        CURRENT_MAIN_HAND_ITEM_TAGS.clear();
        CURRENT_OFFHAND_ITEM_TAGS.clear();
        CURRENT_HELMET_TAGS.clear();
        CURRENT_CHEST_TAGS.clear();
        CURRENT_LEGS_TAGS.clear();
        CURRENT_BOOTS_TAGS.clear();

        tileEntityArgs.clear();
        equipmentArgs.clear();

        allItemsEmpty = true;
        isInUse = false;

        // Clear Sub-Argument Mappings
        for (String entry : subArgumentData.keySet()) {
            CraftPresence.CLIENT.removeArgumentsMatching(ArgumentType.Text, entry);
        }
        subArgumentData.clear();

        CraftPresence.CLIENT.initArgument(ArgumentType.Text, argumentFormat);
    }

    /**
     * Module Event to Occur on each tick within the Application
     */
    public void onTick() {
        enabled = !CraftPresence.CONFIG.hasChanged ? CraftPresence.CONFIG.advancedSettings.enablePerItem : enabled;
        final boolean needsUpdate = enabled && (TILE_ENTITY_NAMES.isEmpty() || TILE_ENTITY_CLASSES.isEmpty());

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
     * Determines whether the Specified Item classifies as NULL or EMPTY
     *
     * @param item The Item to Evaluate
     * @return {@code true} if the Item classifies as NULL or EMPTY
     */
    private boolean isEmpty(final Item item) {
        return item == null || isEmpty(getDefaultInstance(item));
    }

    /**
     * Determines whether the Specified Block classifies as NULL or EMPTY
     *
     * @param block The Block to evaluate
     * @return {@code true} if the Block classifies as NULL or EMPTY
     */
    private boolean isEmpty(final Block block) {
        return block == null || isEmpty(Item.getItemFromBlock(block));
    }

    /**
     * Returns the Default Variant of the Specified Item
     *
     * @param itemIn The Item to evaluate
     * @return The default variant of the item
     */
    private ItemStack getDefaultInstance(final Item itemIn) {
        return new ItemStack(itemIn);
    }

    /**
     * Determines whether the Specified ItemStack classifies as NULL or EMPTY
     *
     * @param itemStack The ItemStack to evaluate
     * @return {@code true} if the ItemStack classifies as NULL or EMPTY
     */
    private boolean isEmpty(final ItemStack itemStack) {
        if (itemStack == null || itemStack.equals(EMPTY_STACK)) {
            return true;
        } else if (itemStack.getItem() != EMPTY_ITEM && itemStack.getItem() != Items.AIR) {
            if (itemStack.getCount() <= 0) {
                return true;
            } else {
                return itemStack.getItemDamage() < -32768 || itemStack.getItemDamage() > 65535;
            }
        } else {
            return true;
        }
    }

    /**
     * Synchronizes Data related to this module, if needed
     */
    private void updateEntityData() {
        final ItemStack NEW_CURRENT_MAIN_HAND_ITEM = CraftPresence.player.getHeldItemMainhand();
        final ItemStack NEW_CURRENT_OFFHAND_ITEM = CraftPresence.player.getHeldItemOffhand();
        final ItemStack NEW_CURRENT_HELMET = CraftPresence.player.inventory.armorInventory.get(3);
        final ItemStack NEW_CURRENT_CHEST = CraftPresence.player.inventory.armorInventory.get(2);
        final ItemStack NEW_CURRENT_LEGS = CraftPresence.player.inventory.armorInventory.get(1);
        final ItemStack NEW_CURRENT_BOOTS = CraftPresence.player.inventory.armorInventory.get(0);

        final String NEW_CURRENT_MAIN_HAND_ITEM_NAME = !isEmpty(NEW_CURRENT_MAIN_HAND_ITEM) ?
                StringUtils.stripColors(NEW_CURRENT_MAIN_HAND_ITEM.getDisplayName()) : "";
        final String NEW_CURRENT_OFFHAND_ITEM_NAME = !isEmpty(NEW_CURRENT_OFFHAND_ITEM) ?
                StringUtils.stripColors(NEW_CURRENT_OFFHAND_ITEM.getDisplayName()) : "";
        final String NEW_CURRENT_HELMET_NAME = !isEmpty(NEW_CURRENT_HELMET) ?
                StringUtils.stripColors(NEW_CURRENT_HELMET.getDisplayName()) : "";
        final String NEW_CURRENT_CHEST_NAME = !isEmpty(NEW_CURRENT_CHEST) ?
                StringUtils.stripColors(NEW_CURRENT_CHEST.getDisplayName()) : "";
        final String NEW_CURRENT_LEGS_NAME = !isEmpty(NEW_CURRENT_LEGS) ?
                StringUtils.stripColors(NEW_CURRENT_LEGS.getDisplayName()) : "";
        final String NEW_CURRENT_BOOTS_NAME = !isEmpty(NEW_CURRENT_BOOTS) ?
                StringUtils.stripColors(NEW_CURRENT_BOOTS.getDisplayName()) : "";

        final boolean hasMainHandChanged = (!isEmpty(NEW_CURRENT_MAIN_HAND_ITEM) &&
                !NEW_CURRENT_MAIN_HAND_ITEM.equals(CURRENT_MAIN_HAND_ITEM) || !NEW_CURRENT_MAIN_HAND_ITEM_NAME.equals(CURRENT_MAIN_HAND_ITEM_NAME)) ||
                (isEmpty(NEW_CURRENT_MAIN_HAND_ITEM) && !isEmpty(CURRENT_MAIN_HAND_ITEM));
        final boolean hasOffHandChanged = (!isEmpty(NEW_CURRENT_OFFHAND_ITEM) &&
                !NEW_CURRENT_OFFHAND_ITEM.equals(CURRENT_OFFHAND_ITEM) || !NEW_CURRENT_OFFHAND_ITEM_NAME.equals(CURRENT_OFFHAND_ITEM_NAME)) ||
                (isEmpty(NEW_CURRENT_OFFHAND_ITEM) && !isEmpty(CURRENT_OFFHAND_ITEM));
        final boolean hasHelmetChanged = (!isEmpty(NEW_CURRENT_HELMET) &&
                !NEW_CURRENT_HELMET.equals(CURRENT_HELMET) || !NEW_CURRENT_HELMET_NAME.equals(CURRENT_HELMET_NAME)) ||
                (isEmpty(NEW_CURRENT_HELMET) && !isEmpty(CURRENT_HELMET));
        final boolean hasChestChanged = (!isEmpty(NEW_CURRENT_CHEST) &&
                !NEW_CURRENT_CHEST.equals(CURRENT_CHEST) || !NEW_CURRENT_CHEST_NAME.equals(CURRENT_CHEST_NAME)) ||
                (isEmpty(NEW_CURRENT_CHEST) && !isEmpty(CURRENT_CHEST));
        final boolean hasLegsChanged = (!isEmpty(NEW_CURRENT_LEGS) &&
                !NEW_CURRENT_LEGS.equals(CURRENT_LEGS) || !NEW_CURRENT_LEGS_NAME.equals(CURRENT_LEGS_NAME)) ||
                (isEmpty(NEW_CURRENT_LEGS) && !isEmpty(CURRENT_LEGS));
        final boolean hasBootsChanged = (!isEmpty(NEW_CURRENT_BOOTS) &&
                !NEW_CURRENT_BOOTS.equals(CURRENT_BOOTS) || !NEW_CURRENT_BOOTS_NAME.equals(CURRENT_BOOTS_NAME)) ||
                (isEmpty(NEW_CURRENT_BOOTS) && !isEmpty(CURRENT_BOOTS));

        if (hasMainHandChanged) {
            CURRENT_MAIN_HAND_ITEM = NEW_CURRENT_MAIN_HAND_ITEM;
            CURRENT_MAIN_HAND_ITEM_TAG = !isEmpty(CURRENT_MAIN_HAND_ITEM) ? CURRENT_MAIN_HAND_ITEM.writeToNBT(new NBTTagCompound()) : null;
            final List<String> NEW_CURRENT_MAIN_HAND_ITEM_TAGS = CURRENT_MAIN_HAND_ITEM_TAG != null ? Lists.newArrayList(CURRENT_MAIN_HAND_ITEM_TAG.getKeySet()) : Lists.newArrayList();

            if (!NEW_CURRENT_MAIN_HAND_ITEM_TAGS.equals(CURRENT_MAIN_HAND_ITEM_TAGS)) {
                CURRENT_MAIN_HAND_ITEM_TAGS = NEW_CURRENT_MAIN_HAND_ITEM_TAGS;
            }
            CURRENT_MAIN_HAND_ITEM_NAME = NEW_CURRENT_MAIN_HAND_ITEM_NAME;
        }

        if (hasOffHandChanged) {
            CURRENT_OFFHAND_ITEM = NEW_CURRENT_OFFHAND_ITEM;
            CURRENT_OFFHAND_ITEM_TAG = !isEmpty(CURRENT_OFFHAND_ITEM) ? CURRENT_OFFHAND_ITEM.writeToNBT(new NBTTagCompound()) : null;
            final List<String> NEW_CURRENT_OFFHAND_ITEM_TAGS = CURRENT_OFFHAND_ITEM_TAG != null ? Lists.newArrayList(CURRENT_OFFHAND_ITEM_TAG.getKeySet()) : Lists.newArrayList();

            if (!NEW_CURRENT_OFFHAND_ITEM_TAGS.equals(CURRENT_OFFHAND_ITEM_TAGS)) {
                CURRENT_OFFHAND_ITEM_TAGS = NEW_CURRENT_OFFHAND_ITEM_TAGS;
            }
            CURRENT_OFFHAND_ITEM_NAME = NEW_CURRENT_OFFHAND_ITEM_NAME;
        }

        if (hasHelmetChanged) {
            CURRENT_HELMET = NEW_CURRENT_HELMET;
            CURRENT_HELMET_TAG = !isEmpty(CURRENT_HELMET) ? CURRENT_HELMET.writeToNBT(new NBTTagCompound()) : null;
            final List<String> NEW_CURRENT_HELMET_TAGS = CURRENT_HELMET_TAG != null ? Lists.newArrayList(CURRENT_HELMET_TAG.getKeySet()) : Lists.newArrayList();

            if (!NEW_CURRENT_HELMET_TAGS.equals(CURRENT_HELMET_TAGS)) {
                CURRENT_HELMET_TAGS = NEW_CURRENT_HELMET_TAGS;
            }
            CURRENT_HELMET_NAME = NEW_CURRENT_HELMET_NAME;
        }

        if (hasChestChanged) {
            CURRENT_CHEST = NEW_CURRENT_CHEST;
            CURRENT_CHEST_TAG = !isEmpty(CURRENT_CHEST) ? CURRENT_CHEST.writeToNBT(new NBTTagCompound()) : null;
            final List<String> NEW_CURRENT_CHEST_TAGS = CURRENT_CHEST_TAG != null ? Lists.newArrayList(CURRENT_CHEST_TAG.getKeySet()) : Lists.newArrayList();

            if (!NEW_CURRENT_CHEST_TAGS.equals(CURRENT_CHEST_TAGS)) {
                CURRENT_CHEST_TAGS = NEW_CURRENT_CHEST_TAGS;
            }
            CURRENT_CHEST_NAME = NEW_CURRENT_CHEST_NAME;
        }

        if (hasLegsChanged) {
            CURRENT_LEGS = NEW_CURRENT_LEGS;
            CURRENT_LEGS_TAG = !isEmpty(CURRENT_LEGS) ? CURRENT_LEGS.writeToNBT(new NBTTagCompound()) : null;
            final List<String> NEW_CURRENT_LEGS_TAGS = CURRENT_LEGS_TAG != null ? Lists.newArrayList(CURRENT_LEGS_TAG.getKeySet()) : Lists.newArrayList();

            if (!NEW_CURRENT_LEGS_TAGS.equals(CURRENT_LEGS_TAGS)) {
                CURRENT_LEGS_TAGS = NEW_CURRENT_LEGS_TAGS;
            }
            CURRENT_LEGS_NAME = NEW_CURRENT_LEGS_NAME;
        }

        if (hasBootsChanged) {
            CURRENT_BOOTS = NEW_CURRENT_BOOTS;
            CURRENT_BOOTS_TAG = !isEmpty(CURRENT_BOOTS) ? CURRENT_BOOTS.writeToNBT(new NBTTagCompound()) : null;
            final List<String> NEW_CURRENT_BOOTS_TAGS = CURRENT_BOOTS_TAG != null ? Lists.newArrayList(CURRENT_BOOTS_TAG.getKeySet()) : Lists.newArrayList();

            if (!NEW_CURRENT_BOOTS_TAGS.equals(CURRENT_BOOTS_TAGS)) {
                CURRENT_BOOTS_TAGS = NEW_CURRENT_BOOTS_TAGS;
            }
            CURRENT_BOOTS_NAME = NEW_CURRENT_BOOTS_NAME;
        }

        if (hasMainHandChanged || hasOffHandChanged ||
                hasHelmetChanged || hasChestChanged ||
                hasLegsChanged || hasBootsChanged) {
            allItemsEmpty = isEmpty(CURRENT_MAIN_HAND_ITEM) && isEmpty(CURRENT_OFFHAND_ITEM) && isEmpty(CURRENT_HELMET) && isEmpty(CURRENT_CHEST) && isEmpty(CURRENT_LEGS) && isEmpty(CURRENT_BOOTS);
            updateEntityPresence();
        }
    }

    /**
     * Updates RPC Data related to this Module
     */
    public void updateEntityPresence() {
        // Retrieve Messages
        final String defaultItemMessage = CraftPresence.CONFIG.advancedSettings.itemMessages.getOrDefault("default", "");
        final String placeholderItemMessage = CraftPresence.CONFIG.statusMessages.playerItemsPlaceholderMessage;

        String offHandItemMessage = CraftPresence.CONFIG.advancedSettings.itemMessages.getOrDefault(CURRENT_OFFHAND_ITEM_NAME, CURRENT_OFFHAND_ITEM_NAME);
        String mainItemMessage = CraftPresence.CONFIG.advancedSettings.itemMessages.getOrDefault(CURRENT_MAIN_HAND_ITEM_NAME, defaultItemMessage);

        String helmetMessage = CraftPresence.CONFIG.advancedSettings.itemMessages.getOrDefault(CURRENT_HELMET_NAME, CURRENT_HELMET_NAME);
        String chestMessage = CraftPresence.CONFIG.advancedSettings.itemMessages.getOrDefault(CURRENT_CHEST_NAME, CURRENT_CHEST_NAME);
        String legsMessage = CraftPresence.CONFIG.advancedSettings.itemMessages.getOrDefault(CURRENT_LEGS_NAME, CURRENT_LEGS_NAME);
        String bootsMessage = CraftPresence.CONFIG.advancedSettings.itemMessages.getOrDefault(CURRENT_BOOTS_NAME, CURRENT_BOOTS_NAME);

        // Form Entity/Item Argument List
        tileEntityArgs.clear();
        equipmentArgs.clear();

        // Clear Sub-Argument Mappings
        for (String entry : subArgumentData.keySet()) {
            CraftPresence.CLIENT.removeArgumentsMatching(ArgumentType.Text, entry);
        }
        subArgumentData.clear();

        // Extend Argument Messages, if tags available
        if (!CURRENT_MAIN_HAND_ITEM_TAGS.isEmpty()) {
            for (String tagName : CURRENT_MAIN_HAND_ITEM_TAGS) {
                mainItemMessage = StringUtils.replaceAnyCase(mainItemMessage, "&" + tagName + "&", CURRENT_MAIN_HAND_ITEM_TAG.getTag(tagName).toString());
            }
        }

        if (!CURRENT_OFFHAND_ITEM_TAGS.isEmpty()) {
            for (String tagName : CURRENT_OFFHAND_ITEM_TAGS) {
                offHandItemMessage = StringUtils.replaceAnyCase(offHandItemMessage, "&" + tagName + "&", CURRENT_OFFHAND_ITEM_TAG.getTag(tagName).toString());
            }
        }

        if (!CURRENT_HELMET_TAGS.isEmpty()) {
            for (String tagName : CURRENT_HELMET_TAGS) {
                helmetMessage = StringUtils.replaceAnyCase(helmetMessage, "&" + tagName + "&", CURRENT_HELMET_TAG.getTag(tagName).toString());
            }
        }

        if (!CURRENT_CHEST_TAGS.isEmpty()) {
            for (String tagName : CURRENT_CHEST_TAGS) {
                chestMessage = StringUtils.replaceAnyCase(chestMessage, "&" + tagName + "&", CURRENT_CHEST_TAG.getTag(tagName).toString());
            }
        }

        if (!CURRENT_LEGS_TAGS.isEmpty()) {
            for (String tagName : CURRENT_LEGS_TAGS) {
                legsMessage = StringUtils.replaceAnyCase(legsMessage, "&" + tagName + "&", CURRENT_LEGS_TAG.getTag(tagName).toString());
            }
        }

        if (!CURRENT_BOOTS_TAGS.isEmpty()) {
            for (String tagName : CURRENT_BOOTS_TAGS) {
                bootsMessage = StringUtils.replaceAnyCase(bootsMessage, "&" + tagName + "&", CURRENT_BOOTS_TAG.getTag(tagName).toString());
            }
        }

        equipmentArgs.add(new Pair<>("&MAIN&", !StringUtils.isNullOrEmpty(CURRENT_MAIN_HAND_ITEM_NAME) ?
                StringUtils.replaceAnyCase(mainItemMessage, "&item&", CURRENT_MAIN_HAND_ITEM_NAME) : ""));
        equipmentArgs.add(new Pair<>("&OFFHAND&", !StringUtils.isNullOrEmpty(CURRENT_OFFHAND_ITEM_NAME) ?
                StringUtils.replaceAnyCase(offHandItemMessage, "&item&", CURRENT_OFFHAND_ITEM_NAME) : ""));
        equipmentArgs.add(new Pair<>("&HELMET&", !StringUtils.isNullOrEmpty(CURRENT_HELMET_NAME) ?
                StringUtils.replaceAnyCase(helmetMessage, "&item&", CURRENT_HELMET_NAME) : ""));
        equipmentArgs.add(new Pair<>("&CHEST&", !StringUtils.isNullOrEmpty(CURRENT_CHEST_NAME) ?
                StringUtils.replaceAnyCase(chestMessage, "&item&", CURRENT_CHEST_NAME) : ""));
        equipmentArgs.add(new Pair<>("&LEGS&", !StringUtils.isNullOrEmpty(CURRENT_LEGS_NAME) ?
                StringUtils.replaceAnyCase(legsMessage, "&item&", CURRENT_LEGS_NAME) : ""));
        equipmentArgs.add(new Pair<>("&BOOTS&", !StringUtils.isNullOrEmpty(CURRENT_BOOTS_NAME) ?
                StringUtils.replaceAnyCase(bootsMessage, "&item&", CURRENT_BOOTS_NAME) : ""));

        // Add applicable args as sub-placeholders
        subArgumentData.put(subArgumentFormat, equipmentArgs);
        for (Map.Entry<String, List<Pair<String, String>>> entry : subArgumentData.entrySet()) {
            if (!StringUtils.isNullOrEmpty(entry.getKey()) && !entry.getValue().isEmpty()) {
                for (Pair<String, String> argumentData : entry.getValue()) {
                    CraftPresence.CLIENT.syncArgument(
                            entry.getKey() + argumentData.getFirst().substring(1),
                            argumentData.getSecond(),
                            ArgumentType.Text
                    );
                }
            }
        }

        // Add All Generalized Arguments, if any
        tileEntityArgs = Lists.newArrayList(equipmentArgs);
        if (!CraftPresence.CLIENT.generalArgs.isEmpty()) {
            StringUtils.addEntriesNotPresent(tileEntityArgs, CraftPresence.CLIENT.generalArgs);
        }

        final String CURRENT_MESSAGE = StringUtils.sequentialReplaceAnyCase(placeholderItemMessage, tileEntityArgs);

        // NOTE: Only Apply if Items are not Empty, otherwise Clear Argument
        if (!allItemsEmpty) {
            CraftPresence.CLIENT.syncArgument(argumentFormat, CURRENT_MESSAGE, ArgumentType.Text);
        } else {
            CraftPresence.CLIENT.removeArgumentsMatching(ArgumentType.Text, subArgumentFormat);
            CraftPresence.CLIENT.initArgument(ArgumentType.Text, argumentFormat);
        }
    }

    /**
     * Generate a parsable display string for the argument data provided
     *
     * @param types The argument types to interpret
     * @return the parsable string
     */
    public String generateArgumentMessage(String argumentFormat, String subArgumentFormat, ArgumentType... types) {
        types = (types != null && types.length > 0 ? types : ArgumentType.values());
        final boolean useDefault = StringUtils.isNullOrEmpty(argumentFormat) && StringUtils.isNullOrEmpty(subArgumentFormat);
        if (useDefault) {
            argumentFormat = this.argumentFormat;
            subArgumentFormat = this.subArgumentFormat;
        }

        final Map<ArgumentType, List<String>> argumentData = Maps.newHashMap();
        List<String> queuedEntries;
        for (ArgumentType type : types) {
            queuedEntries = Lists.newArrayList();
            if (type == ArgumentType.Text) {
                if (!useDefault) {
                    queuedEntries.add(subArgumentFormat + "MAIN&");
                    queuedEntries.add(subArgumentFormat + "OFFHAND&");
                    queuedEntries.add(subArgumentFormat + "HELMET&");
                    queuedEntries.add(subArgumentFormat + "CHEST&");
                    queuedEntries.add(subArgumentFormat + "LEGS&");
                    queuedEntries.add(subArgumentFormat + "BOOTS&");
                } else {
                    queuedEntries.add(subArgumentFormat + "ITEM&");
                }
            }
            argumentData.put(type, queuedEntries);
        }
        return CraftPresence.CLIENT.generateArgumentMessage(argumentFormat, subArgumentFormat, argumentData);
    }

    /**
     * Generate a parsable display string for the argument data provided
     *
     * @param types The argument types to interpret
     * @return the parsable string
     */
    public String generateArgumentMessage(ArgumentType... types) {
        return generateArgumentMessage(null, null, types);
    }

    /**
     * Retrieves a List of Tags from a TileEntity name, if currently equipped
     *
     * @param name The TileEntity Name
     * @return A List of Tags from a TileEntity name, if currently equipped
     */
    public List<String> getListFromName(String name) {
        name = !StringUtils.isNullOrEmpty(name) ? name : "";
        return name.equalsIgnoreCase(CURRENT_MAIN_HAND_ITEM_NAME) ? CURRENT_MAIN_HAND_ITEM_TAGS
                : name.equalsIgnoreCase(CURRENT_OFFHAND_ITEM_NAME) ? CURRENT_OFFHAND_ITEM_TAGS
                : name.equalsIgnoreCase(CURRENT_HELMET_NAME) ? CURRENT_HELMET_TAGS
                : name.equalsIgnoreCase(CURRENT_CHEST_NAME) ? CURRENT_CHEST_TAGS
                : name.equalsIgnoreCase(CURRENT_LEGS_NAME) ? CURRENT_LEGS_TAGS
                : name.equalsIgnoreCase(CURRENT_BOOTS_NAME) ? CURRENT_BOOTS_TAGS : Lists.newArrayList();
    }

    /**
     * Generates TileEntity Tag Placeholder String
     *
     * @param name         The TileEntity Name
     * @param addExtraData Whether to add additional data to the string
     * @param tags         A List of the tags associated with the TileEntity
     * @return The Resulting TileEntity Tag Placeholder String
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
                            tags.equals(CURRENT_MAIN_HAND_ITEM_TAGS) ? CURRENT_MAIN_HAND_ITEM_TAG.getTag(tagName).toString() :
                                    tags.equals(CURRENT_OFFHAND_ITEM_TAGS) ? CURRENT_OFFHAND_ITEM_TAG.getTag(tagName).toString() :
                                            tags.equals(CURRENT_HELMET_TAGS) ? CURRENT_HELMET_TAG.getTag(tagName).toString() :
                                                    tags.equals(CURRENT_CHEST_TAGS) ? CURRENT_CHEST_TAG.getTag(tagName).toString() :
                                                            tags.equals(CURRENT_LEGS_TAGS) ? CURRENT_LEGS_TAG.getTag(tagName).toString() :
                                                                    tags.equals(CURRENT_BOOTS_TAGS) ? CURRENT_BOOTS_TAG.getTag(tagName).toString() : null;

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
        for (Block block : Block.REGISTRY) {
            if (!isEmpty(block)) {
                final String blockName = block.getLocalizedName();
                if (!BLOCK_NAMES.contains(blockName)) {
                    BLOCK_NAMES.add(blockName);
                }
                if (!BLOCK_CLASSES.contains(block.getClass().getName())) {
                    BLOCK_CLASSES.add(block.getClass().getName());
                }

                if (!TILE_ENTITY_RESOURCES.containsKey(blockName)) {
                    try {
                        final ResourceLocation initialData = new ResourceLocation(
                                CraftPresence.instance.getBlockRendererDispatcher().getModelForState(
                                        block.getDefaultState()
                                ).getParticleTexture().getIconName()
                        );
                        TILE_ENTITY_RESOURCES.put(blockName,
                                new ResourceLocation(initialData.getNamespace(),
                                        "textures/" + initialData.getPath() + ".png"
                                )
                        );
                    } catch (Exception ignored) {
                    }
                }
            }
        }

        for (Item item : Item.REGISTRY) {
            if (!isEmpty(item)) {
                final String itemName = item.getItemStackDisplayName(getDefaultInstance(item));
                if (!ITEM_NAMES.contains(itemName)) {
                    ITEM_NAMES.add(itemName);
                }
                if (!ITEM_CLASSES.contains(item.getClass().getName())) {
                    ITEM_CLASSES.add(item.getClass().getName());
                }

                if (!TILE_ENTITY_RESOURCES.containsKey(itemName)) {
                    try {
                        final ResourceLocation initialData = new ResourceLocation(
                                CraftPresence.instance.getRenderItem().getItemModelMesher().getItemModel(
                                        getDefaultInstance(item)
                                ).getParticleTexture().getIconName()
                        );
                        TILE_ENTITY_RESOURCES.put(itemName,
                                new ResourceLocation(initialData.getNamespace(),
                                        "textures/" + initialData.getPath() + ".png"
                                )
                        );
                    } catch (Exception ignored) {
                    }
                }
            }
        }

        for (String itemEntry : CraftPresence.CONFIG.advancedSettings.itemMessages.keySet()) {
            if (!StringUtils.isNullOrEmpty(itemEntry)) {
                if (!ITEM_NAMES.contains(itemEntry)) {
                    ITEM_NAMES.add(itemEntry);
                }
                if (!BLOCK_NAMES.contains(itemEntry)) {
                    BLOCK_NAMES.add(itemEntry);
                }
            }
        }

        verifyEntities();
    }

    /**
     * Verifies, Synchronizes and Removes any Invalid Items and Blocks from their Lists
     */
    public void verifyEntities() {
        List<String> removingBlocks = Lists.newArrayList();
        List<String> removingItems = Lists.newArrayList();
        for (String itemName : ITEM_NAMES) {
            if (!StringUtils.isNullOrEmpty(itemName)) {
                final String lowerItemName = itemName.toLowerCase();
                if (lowerItemName.contains("tile.") || lowerItemName.contains("item.") || lowerItemName.contains(".") || lowerItemName.contains(".name")) {
                    removingItems.add(itemName);
                }
            }
        }

        for (String blockName : BLOCK_NAMES) {
            if (!StringUtils.isNullOrEmpty(blockName)) {
                final String lowerBlockName = blockName.toLowerCase();
                if (lowerBlockName.contains("tile.") || lowerBlockName.contains("item.") || lowerBlockName.contains(".") || lowerBlockName.contains(".name")) {
                    removingBlocks.add(blockName);
                }
            }
        }

        ITEM_NAMES.removeAll(removingItems);
        ITEM_NAMES.removeAll(BLOCK_NAMES);
        BLOCK_NAMES.removeAll(ITEM_NAMES);
        BLOCK_NAMES.removeAll(removingBlocks);

        StringUtils.addEntriesNotPresent(TILE_ENTITY_NAMES, BLOCK_NAMES);
        StringUtils.addEntriesNotPresent(TILE_ENTITY_NAMES, ITEM_NAMES);

        StringUtils.addEntriesNotPresent(TILE_ENTITY_CLASSES, BLOCK_CLASSES);
        StringUtils.addEntriesNotPresent(TILE_ENTITY_CLASSES, ITEM_CLASSES);
    }
}
