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

package com.gitlab.cdagaming.craftpresence.utils;

import com.gitlab.cdagaming.craftpresence.core.Constants;
import io.github.cdagaming.unicore.utils.FileUtils;
import io.github.cdagaming.unicore.utils.StringUtils;
import net.minecraft.core.component.DataComponentHolder;
import net.minecraft.core.component.DataComponents;
import net.minecraft.nbt.*;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.item.component.CustomData;

import java.util.List;

/**
 * Utilities for interpreting NBT and related Data Types
 *
 * @author CDAGaming
 */
public class NbtUtils {
    /**
     * Attempt to retrieve the NBT Tag with the specified path
     *
     * @param entity The Entity data to interpret
     * @return the resulting NBT Tag, or null if not found
     */
    public static CompoundTag getNbt(final Entity entity) {
        CompoundTag result = new CompoundTag();
        return entity != null ? entity.saveWithoutId(result) : result;
    }

    /**
     * Attempt to retrieve the NBT Tag with the specified path
     *
     * @param stack The ItemStack data to interpret
     * @return the resulting NBT Tag, or null if not found
     */
    public static CompoundTag getNbt(final DataComponentHolder stack) {
        CompoundTag result = new CompoundTag();
        return stack != null ? stack.getOrDefault(DataComponents.CUSTOM_DATA, CustomData.EMPTY).copyTag() : result;
    }

    /**
     * Attempt to retrieve the NBT Tag with the specified path
     *
     * @param data The data to interpret
     * @param path The path to traverse from the root tag
     * @return the resulting NBT Tag, or null if not found
     */
    public static Tag getNbt(final Object data, final String... path) {
        if (data instanceof Entity entity) {
            return getNbt(entity, path);
        } else if (data instanceof DataComponentHolder component) {
            return getNbt(component, path);
        }
        return null;
    }

    /**
     * Attempt to retrieve the NBT Tag with the specified path
     *
     * @param entity The Entity data to interpret
     * @param path   The path to traverse from the root tag
     * @return the resulting NBT Tag, or null if not found
     */
    public static Tag getNbt(final Entity entity, final String... path) {
        return getNbt(
                getNbt(entity), path
        );
    }

    /**
     * Attempt to retrieve the NBT Tag with the specified path
     *
     * @param stack The ItemStack data to interpret
     * @param path  The path to traverse from the root tag
     * @return the resulting NBT Tag, or null if not found
     */
    public static Tag getNbt(final DataComponentHolder stack, final String... path) {
        return getNbt(
                getNbt(stack), path
        );
    }

    /**
     * Attempt to retrieve the NBT Tag with the specified path
     *
     * @param root The root NBT Tag to interpret
     * @param path The path to traverse from the root tag
     * @return the resulting NBT Tag, or null if not found
     */
    public static Tag getNbt(final CompoundTag root, final String... path) {
        if (path == null || path.length == 0) {
            return root;
        } else {
            Tag currentTag = root;
            for (int i = 0; i < path.length; i++) {
                if (currentTag instanceof CompoundTag compound) {
                    currentTag = compound.get(path[i]);
                } else if (currentTag instanceof ListTag list) {
                    int index = Integer.parseInt(path[i]);
                    currentTag = list.get(index);
                } else {
                    if (i == path.length - 1) {
                        break;
                    }
                    return null;
                }
            }
            return currentTag;
        }
    }

    /**
     * Convert the specified NBT Tag into it's Primitive Equivalent
     *
     * @param tag The nbt tag to interpret
     * @return the primitive equivalent of the NBT Tag data
     */
    public static Object parseTag(final Tag tag) {
        if (tag == null) {
            return null;
        }

        switch (tag.getId()) {
            case 1:
                return ((ByteTag) tag).getAsByte();
            case 2:
                return ((ShortTag) tag).getAsShort();
            case 3:
                return ((IntTag) tag).getAsInt();
            case 4:
                return ((LongTag) tag).getAsLong();
            case 5:
                return ((FloatTag) tag).getAsFloat();
            case 6:
                return ((DoubleTag) tag).getAsDouble();
            case 7:
                return ((ByteArrayTag) tag).getAsByteArray();
            case 8:
                return ((StringTag) tag).getAsString();
            case 9: {
                final ListTag list = ((ListTag) tag);
                final List<Object> converted = StringUtils.newArrayList();
                if (!list.isEmpty()) {
                    for (int i = 0; i < list.size(); i++) {
                        converted.add(parseTag(list.get(i)));
                    }
                }
                return converted;
            }
            case 10:
                try {
                    return FileUtils.toJsonData(tag.toString());
                } catch (Throwable ex) {
                    Constants.LOG.debugError(ex);
                    return tag.toString();
                }
            case 11:
                return ((IntArrayTag) tag).getAsIntArray();
            case 12:
                return ((LongArrayTag) tag).getAsLongArray();
            case 0:
            case 99:
            default:
                return tag;
        }
    }
}
