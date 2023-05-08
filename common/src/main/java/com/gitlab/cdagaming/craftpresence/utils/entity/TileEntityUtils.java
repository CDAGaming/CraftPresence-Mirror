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
import com.gitlab.cdagaming.craftpresence.ModUtils;
import com.gitlab.cdagaming.craftpresence.impl.Module;
import com.gitlab.cdagaming.craftpresence.utils.StringUtils;
import net.minecraft.block.Block;
import net.minecraft.init.Items;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;

import java.util.List;
import java.util.Map;

/**
 * Tile Entity Utilities used to Parse TileEntity (Blocks and Items) Data and handle related RPC Events
 *
 * @author CDAGaming
 */
public class TileEntityUtils implements Module {
    /**
     * An Instance of an Empty Item
     */
    public static final Item EMPTY_ITEM = null;
    /**
     * An Instance of an Empty ItemStack
     */
    public static final ItemStack EMPTY_STACK = ItemStack.EMPTY;
    /**
     * A List of the detected Block Names
     */
    public final List<String> BLOCK_NAMES = StringUtils.newArrayList();
    /**
     * A List of the detected Item Names
     */
    public final List<String> ITEM_NAMES = StringUtils.newArrayList();
    /**
     * A List of the detected Block Class Names
     */
    private final List<String> BLOCK_CLASSES = StringUtils.newArrayList();
    /**
     * A List of the detected Item Class Names
     */
    private final List<String> ITEM_CLASSES = StringUtils.newArrayList();
    /**
     * A List of the detected Tile Entity (Blocks + Items) Class Names
     */
    private final List<String> TILE_ENTITY_CLASSES = StringUtils.newArrayList();
    /**
     * Whether this module is allowed to start and enabled
     */
    public boolean enabled = false;
    /**
     * A List of the detected Entity (Blocks + Items) Names
     */
    public List<String> TILE_ENTITY_NAMES = StringUtils.newArrayList();
    /**
     * A List storing a mapping of Tile Entity textures, mapped as entityName:entityObject
     */
    public Map<String, ItemStack> TILE_ENTITY_RESOURCES = StringUtils.newHashMap();
    /**
     * Whether this module is active and currently in use
     */
    private boolean isInUse = false;
    /**
     * Whether this module has performed an initial retrieval of items
     */
    private boolean hasScanned = false;
    /**
     * The Player's Current Main Hand Item, if any
     */
    private ItemStack CURRENT_MAIN_HAND_ITEM;
    /**
     * The Player's Current Offhand Item, if any
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
     * The Player's Current Offhand Item Name, if any
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
     * Retrieve or convert the specified object to an {@link ItemStack}
     *
     * @param data the data to interpret
     * @return the converted or retrieved {@link ItemStack}
     */
    public static ItemStack getStackFrom(Object data) {
        ItemStack itemStack = null;
        if (data != null) {
            if (data instanceof Block) {
                data = getDefaultInstance((Block) data);
            }
            if (data instanceof Item) {
                data = getDefaultInstance((Item) data);
            }
            if (data instanceof ItemStack) {
                itemStack = (ItemStack) data;
            }
        }
        return itemStack;
    }

    /**
     * Determines whether the Specified {@link ItemStack} classifies as NULL or EMPTY
     *
     * @param data The {@link ItemStack} to evaluate
     * @return {@link Boolean#TRUE} if the ItemStack classifies as NULL or EMPTY
     */
    public static boolean isEmpty(final Object data) {
        final ItemStack itemStack = getStackFrom(data);
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
     * Returns the Default Variant of the Specified Block
     *
     * @param blockIn The Block to evaluate
     * @return The default variant of the item
     */
    public static ItemStack getDefaultInstance(final Block blockIn) {
        return new ItemStack(blockIn);
    }

    /**
     * Returns the Default Variant of the Specified Item
     *
     * @param itemIn The Item to evaluate
     * @return The default variant of the item
     */
    public static ItemStack getDefaultInstance(final Item itemIn) {
        return new ItemStack(itemIn);
    }

    /**
     * Retrieves the entities display name, derived from the original supplied name
     *
     * @param data        The {@link ItemStack} to interpret
     * @param stripColors Whether the resulting name should have its formatting stripped
     * @return The formatted entity display name to use
     */
    public static String getName(final Object data, final boolean stripColors) {
        final ItemStack itemStack = getStackFrom(data);
        String result = "";
        if (!isEmpty(itemStack)) {
            result = StringUtils.getOrDefault(
                    itemStack.getDisplayName()
            );
        }

        if (stripColors) {
            result = StringUtils.stripColors(result);
        }
        return result;
    }

    /**
     * Retrieves the entities display name, derived from the original supplied name
     *
     * @param entity The entity to interpret
     * @return The formatted entity display name to use
     */
    public static String getName(final Object entity) {
        return getName(entity, true);
    }

    /**
     * Returns whether the specified name contains raw data
     *
     * @param name The name to interpret
     * @return {@link Boolean#TRUE} if the condition is satisfied
     */
    public static boolean isRawTE(final String name) {
        if (!StringUtils.isNullOrEmpty(name)) {
            final String lowerName = name.toLowerCase();
            return lowerName.contains("tile.") ||
                    lowerName.contains("item.") ||
                    lowerName.contains(".") ||
                    lowerName.contains(".name");
        }
        return false;
    }

    @Override
    public void emptyData() {
        hasScanned = false;
        BLOCK_NAMES.clear();
        BLOCK_CLASSES.clear();
        ITEM_NAMES.clear();
        ITEM_CLASSES.clear();
        TILE_ENTITY_NAMES.clear();
        TILE_ENTITY_CLASSES.clear();
        TILE_ENTITY_RESOURCES.clear();
        clearClientData();
    }

    @Override
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

        setInUse(false);
        CraftPresence.CLIENT.removeArguments("item", "data.item");
        CraftPresence.CLIENT.clearOverride("item.message", "item.icon");
    }

    @Override
    public void onTick() {
        enabled = !CraftPresence.CONFIG.hasChanged ? CraftPresence.CONFIG.advancedSettings.enablePerItem : enabled;
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
        final ItemStack NEW_CURRENT_MAIN_HAND_ITEM = CraftPresence.player.getHeldItemMainhand();
        final ItemStack NEW_CURRENT_OFFHAND_ITEM = CraftPresence.player.getHeldItemOffhand();
        final ItemStack NEW_CURRENT_HELMET = CraftPresence.player.inventory.armorInventory.get(3);
        final ItemStack NEW_CURRENT_CHEST = CraftPresence.player.inventory.armorInventory.get(2);
        final ItemStack NEW_CURRENT_LEGS = CraftPresence.player.inventory.armorInventory.get(1);
        final ItemStack NEW_CURRENT_BOOTS = CraftPresence.player.inventory.armorInventory.get(0);

        final boolean hasMainHandChanged = NEW_CURRENT_MAIN_HAND_ITEM != CURRENT_MAIN_HAND_ITEM;
        final boolean hasOffHandChanged = NEW_CURRENT_OFFHAND_ITEM != CURRENT_OFFHAND_ITEM;
        final boolean hasHelmetChanged = NEW_CURRENT_HELMET != CURRENT_HELMET;
        final boolean hasChestChanged = NEW_CURRENT_CHEST != CURRENT_CHEST;
        final boolean hasLegsChanged = NEW_CURRENT_LEGS != CURRENT_LEGS;
        final boolean hasBootsChanged = NEW_CURRENT_BOOTS != CURRENT_BOOTS;

        if (hasMainHandChanged) {
            CURRENT_MAIN_HAND_ITEM = NEW_CURRENT_MAIN_HAND_ITEM;
            CURRENT_MAIN_HAND_ITEM_NAME = getName(CURRENT_MAIN_HAND_ITEM);

            if (!isEmpty(CURRENT_MAIN_HAND_ITEM)) {
                CraftPresence.CLIENT.syncTimestamp("data.item.main_hand.time");
            }
        }

        if (hasOffHandChanged) {
            CURRENT_OFFHAND_ITEM = NEW_CURRENT_OFFHAND_ITEM;
            CURRENT_OFFHAND_ITEM_NAME = getName(CURRENT_OFFHAND_ITEM);

            if (!isEmpty(CURRENT_OFFHAND_ITEM)) {
                CraftPresence.CLIENT.syncTimestamp("data.item.off_hand.time");
            }
        }

        if (hasHelmetChanged) {
            CURRENT_HELMET = NEW_CURRENT_HELMET;
            CURRENT_HELMET_NAME = getName(CURRENT_HELMET);

            if (!isEmpty(CURRENT_HELMET)) {
                CraftPresence.CLIENT.syncTimestamp("data.item.helmet.time");
            }
        }

        if (hasChestChanged) {
            CURRENT_CHEST = NEW_CURRENT_CHEST;
            CURRENT_CHEST_NAME = getName(CURRENT_CHEST);

            if (!isEmpty(CURRENT_CHEST)) {
                CraftPresence.CLIENT.syncTimestamp("data.item.chestplate.time");
            }
        }

        if (hasLegsChanged) {
            CURRENT_LEGS = NEW_CURRENT_LEGS;
            CURRENT_LEGS_NAME = getName(CURRENT_LEGS);

            if (!isEmpty(CURRENT_LEGS)) {
                CraftPresence.CLIENT.syncTimestamp("data.item.leggings.time");
            }
        }

        if (hasBootsChanged) {
            CURRENT_BOOTS = NEW_CURRENT_BOOTS;
            CURRENT_BOOTS_NAME = getName(CURRENT_BOOTS);

            if (!isEmpty(CURRENT_BOOTS)) {
                CraftPresence.CLIENT.syncTimestamp("data.item.boots.time");
            }
        }

        if (hasMainHandChanged || hasOffHandChanged ||
                hasHelmetChanged || hasChestChanged ||
                hasLegsChanged || hasBootsChanged) {
            updatePresence();
        }
    }

    @Override
    public void updatePresence() {
        // Retrieve Messages
        final String defaultItemMessage = CraftPresence.CONFIG.advancedSettings.itemMessages.getOrDefault("default", "");

        final String offHandItemMessage = CraftPresence.CONFIG.advancedSettings.itemMessages.getOrDefault(CURRENT_OFFHAND_ITEM_NAME, CURRENT_OFFHAND_ITEM_NAME);
        final String mainItemMessage = CraftPresence.CONFIG.advancedSettings.itemMessages.getOrDefault(CURRENT_MAIN_HAND_ITEM_NAME, CURRENT_MAIN_HAND_ITEM_NAME);

        final String helmetMessage = CraftPresence.CONFIG.advancedSettings.itemMessages.getOrDefault(CURRENT_HELMET_NAME, CURRENT_HELMET_NAME);
        final String chestMessage = CraftPresence.CONFIG.advancedSettings.itemMessages.getOrDefault(CURRENT_CHEST_NAME, CURRENT_CHEST_NAME);
        final String legsMessage = CraftPresence.CONFIG.advancedSettings.itemMessages.getOrDefault(CURRENT_LEGS_NAME, CURRENT_LEGS_NAME);
        final String bootsMessage = CraftPresence.CONFIG.advancedSettings.itemMessages.getOrDefault(CURRENT_BOOTS_NAME, CURRENT_BOOTS_NAME);

        CraftPresence.CLIENT.syncArgument("item.message.default", defaultItemMessage);
        CraftPresence.CLIENT.syncArgument("item.message.holding", String.format("[%s, %s]",
                StringUtils.getOrDefault(CURRENT_MAIN_HAND_ITEM_NAME, "N/A"),
                StringUtils.getOrDefault(CURRENT_OFFHAND_ITEM_NAME, "N/A")
        ), true);
        CraftPresence.CLIENT.syncArgument("item.message.equipped", String.format("[%s, %s, %s, %s]",
                StringUtils.getOrDefault(CURRENT_HELMET_NAME, "N/A"),
                StringUtils.getOrDefault(CURRENT_CHEST_NAME, "N/A"),
                StringUtils.getOrDefault(CURRENT_LEGS_NAME, "N/A"),
                StringUtils.getOrDefault(CURRENT_BOOTS_NAME, "N/A")
        ), true);

        // NOTE: Only Apply if Entities are not Empty, otherwise Clear Argument
        if (!isEmpty(CURRENT_MAIN_HAND_ITEM)) {
            CraftPresence.CLIENT.syncArgument("data.item.main_hand.instance", CURRENT_MAIN_HAND_ITEM);
            CraftPresence.CLIENT.syncArgument("data.item.main_hand.class", CURRENT_MAIN_HAND_ITEM.getClass());
            CraftPresence.CLIENT.syncArgument("item.main_hand.name", CURRENT_MAIN_HAND_ITEM_NAME);
            CraftPresence.CLIENT.syncArgument("item.main_hand.message", mainItemMessage);
        } else {
            CraftPresence.CLIENT.removeArguments("item.main_hand", "data.item.main_hand");
        }

        if (!isEmpty(CURRENT_OFFHAND_ITEM)) {
            CraftPresence.CLIENT.syncArgument("data.item.off_hand.instance", CURRENT_OFFHAND_ITEM);
            CraftPresence.CLIENT.syncArgument("data.item.off_hand.class", CURRENT_OFFHAND_ITEM.getClass());
            CraftPresence.CLIENT.syncArgument("item.off_hand.name", CURRENT_OFFHAND_ITEM_NAME);
            CraftPresence.CLIENT.syncArgument("item.off_hand.message", offHandItemMessage);
        } else {
            CraftPresence.CLIENT.removeArguments("item.off_hand", "data.item.off_hand");
        }

        if (!isEmpty(CURRENT_HELMET)) {
            CraftPresence.CLIENT.syncArgument("data.item.helmet.instance", CURRENT_HELMET);
            CraftPresence.CLIENT.syncArgument("data.item.helmet.class", CURRENT_HELMET.getClass());
            CraftPresence.CLIENT.syncArgument("item.helmet.name", CURRENT_HELMET_NAME);
            CraftPresence.CLIENT.syncArgument("item.helmet.message", helmetMessage);
        } else {
            CraftPresence.CLIENT.removeArguments("item.helmet", "data.item.helmet");
        }

        if (!isEmpty(CURRENT_CHEST)) {
            CraftPresence.CLIENT.syncArgument("data.item.chestplate.instance", CURRENT_CHEST);
            CraftPresence.CLIENT.syncArgument("data.item.chestplate.class", CURRENT_CHEST.getClass());
            CraftPresence.CLIENT.syncArgument("item.chestplate.name", CURRENT_CHEST_NAME);
            CraftPresence.CLIENT.syncArgument("item.chestplate.message", chestMessage);
        } else {
            CraftPresence.CLIENT.removeArguments("item.chestplate", "data.item.chestplate");
        }

        if (!isEmpty(CURRENT_LEGS)) {
            CraftPresence.CLIENT.syncArgument("data.item.leggings.instance", CURRENT_LEGS);
            CraftPresence.CLIENT.syncArgument("data.item.leggings.class", CURRENT_LEGS.getClass());
            CraftPresence.CLIENT.syncArgument("item.leggings.name", CURRENT_LEGS_NAME);
            CraftPresence.CLIENT.syncArgument("item.leggings.message", legsMessage);
        } else {
            CraftPresence.CLIENT.removeArguments("item.leggings", "data.item.leggings");
        }

        if (!isEmpty(CURRENT_BOOTS)) {
            CraftPresence.CLIENT.syncArgument("data.item.boots.instance", CURRENT_BOOTS);
            CraftPresence.CLIENT.syncArgument("data.item.boots.class", CURRENT_BOOTS.getClass());
            CraftPresence.CLIENT.syncArgument("item.boots.name", CURRENT_BOOTS_NAME);
            CraftPresence.CLIENT.syncArgument("item.boots.message", bootsMessage);
        } else {
            CraftPresence.CLIENT.removeArguments("item.boots", "data.item.boots");
        }
    }

    @Override
    public void getAllData() {
        for (Block block : Block.REGISTRY) {
            if (!isEmpty(block)) {
                final ItemStack stack = getStackFrom(block);
                final String blockName = getName(stack);
                if (!BLOCK_NAMES.contains(blockName)) {
                    BLOCK_NAMES.add(blockName);
                }
                if (!BLOCK_CLASSES.contains(block.getClass().getName())) {
                    BLOCK_CLASSES.add(block.getClass().getName());
                }
                if (!TILE_ENTITY_RESOURCES.containsKey(blockName)) {
                    TILE_ENTITY_RESOURCES.put(blockName, stack);
                }
            }
        }

        for (Item item : Item.REGISTRY) {
            if (!isEmpty(item)) {
                final ItemStack stack = getStackFrom(item);
                final String itemName = getName(stack);
                if (!ITEM_NAMES.contains(itemName)) {
                    ITEM_NAMES.add(itemName);
                }
                if (!ITEM_CLASSES.contains(item.getClass().getName())) {
                    ITEM_CLASSES.add(item.getClass().getName());
                }
                if (!TILE_ENTITY_RESOURCES.containsKey(itemName)) {
                    TILE_ENTITY_RESOURCES.put(itemName, stack);
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

    /**
     * Verifies, Synchronizes and Removes any Invalid Items and Blocks from their Lists
     */
    public void verifyEntities() {
        for (String item : StringUtils.newArrayList(ITEM_NAMES)) {
            if (isRawTE(item)) {
                ITEM_NAMES.remove(item);
                if (ModUtils.RAW_TRANSLATOR != null && ModUtils.RAW_TRANSLATOR.hasTranslation(item)) {
                    ITEM_NAMES.add(ModUtils.RAW_TRANSLATOR.translate(item));
                }
            }
        }

        for (String item : StringUtils.newArrayList(BLOCK_NAMES)) {
            if (isRawTE(item)) {
                BLOCK_NAMES.remove(item);
                if (ModUtils.RAW_TRANSLATOR != null && ModUtils.RAW_TRANSLATOR.hasTranslation(item)) {
                    BLOCK_NAMES.add(ModUtils.RAW_TRANSLATOR.translate(item));
                }
            }
        }

        StringUtils.addEntriesNotPresent(TILE_ENTITY_NAMES, BLOCK_NAMES);
        StringUtils.addEntriesNotPresent(TILE_ENTITY_NAMES, ITEM_NAMES);

        StringUtils.addEntriesNotPresent(TILE_ENTITY_CLASSES, BLOCK_CLASSES);
        StringUtils.addEntriesNotPresent(TILE_ENTITY_CLASSES, ITEM_CLASSES);
    }
}
