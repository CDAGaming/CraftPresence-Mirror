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

package com.gitlab.cdagaming.craftpresence.utils;

import net.minecraft.entity.Entity;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.*;

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
     * @param data The data to interpret
     * @return the resulting NBT Tag, or null if not found
     */
    public static NBTTagCompound getNbt(final Object data) {
        if (data instanceof Entity) {
            return getNbt((Entity) data);
        } else if (data instanceof ItemStack) {
            return getNbt((ItemStack) data);
        }
        return null;
    }

    /**
     * Attempt to retrieve the NBT Tag with the specified path
     *
     * @param entity The Entity data to interpret
     * @return the resulting NBT Tag, or null if not found
     */
    public static NBTTagCompound getNbt(final Entity entity) {
        NBTTagCompound result = new NBTTagCompound();
        return entity != null ? entity.writeToNBT(result) : result;
    }

    /**
     * Attempt to retrieve the NBT Tag with the specified path
     *
     * @param stack The ItemStack data to interpret
     * @return the resulting NBT Tag, or null if not found
     */
    public static NBTTagCompound getNbt(final ItemStack stack) {
        NBTTagCompound result = new NBTTagCompound();
        return stack != null ? stack.writeToNBT(result) : result;
    }

    /**
     * Attempt to retrieve the NBT Tag with the specified path
     *
     * @param data The data to interpret
     * @param path The path to traverse from the root tag
     * @return the resulting NBT Tag, or null if not found
     */
    public static NBTBase getNbt(final Object data, final String... path) {
        if (data instanceof Entity) {
            return getNbt((Entity) data, path);
        } else if (data instanceof ItemStack) {
            return getNbt((ItemStack) data, path);
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
    public static NBTBase getNbt(final Entity entity, final String... path) {
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
    public static NBTBase getNbt(final ItemStack stack, final String... path) {
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
    public static NBTBase getNbt(final NBTTagCompound root, final String... path) {
        if (path == null || path.length == 0) {
            return root;
        } else {
            NBTBase currentTag = root;
            for (int i = 0; i < path.length; i++) {
                if (currentTag instanceof NBTTagCompound) {
                    currentTag = ((NBTTagCompound) currentTag).getTag(path[i]);
                } else if (currentTag instanceof NBTTagList) {
                    int index = Integer.parseInt(path[i]);
                    currentTag = ((NBTTagList) currentTag).get(index);
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
    public static Object parseTag(final NBTBase tag) {
        if (tag == null) {
            return null;
        }

        switch (tag.getId()) {
            case 1:
                return ((NBTTagByte) tag).getByte();
            case 2:
                return ((NBTTagShort) tag).getShort();
            case 3:
                return ((NBTTagInt) tag).getInt();
            case 4:
                return ((NBTTagLong) tag).getLong();
            case 5:
                return ((NBTTagFloat) tag).getFloat();
            case 6:
                return ((NBTTagDouble) tag).getDouble();
            case 7:
                return ((NBTTagByteArray) tag).getByteArray();
            case 8:
                return ((NBTTagString) tag).getString();
            case 9: {
                final NBTTagList list = ((NBTTagList) tag);
                final List<Object> converted = StringUtils.newArrayList();
                if (!list.isEmpty()) {
                    for (int i = 0; i < list.tagCount(); i++) {
                        converted.add(parseTag(list.get(i)));
                    }
                }
                return converted;
            }
            case 10:
                try {
                    return FileUtils.toJsonData(tag.toString());
                } catch (Throwable ex) {
                    if (CommandUtils.isVerboseMode()) {
                        ex.printStackTrace();
                    }
                    return tag.toString();
                }
            case 11:
                return ((NBTTagIntArray) tag).getIntArray();
            case 0:
            case 12:
            case 99:
            default:
                return tag;
        }
    }
}
