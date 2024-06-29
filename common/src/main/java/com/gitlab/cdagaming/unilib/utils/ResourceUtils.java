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

package com.gitlab.cdagaming.unilib.utils;

import net.minecraft.util.ResourceLocation;

/**
 * Image Utilities used to Parse texture resource data
 *
 * @author CDAGaming
 */
public class ResourceUtils {
    /**
     * Object representing an empty texture resource
     */
    private static final ResourceLocation EMPTY_RESOURCE = parseResource("");

    /**
     * Retrieve a texture resource within the specified namespace and path
     *
     * @param namespace the namespace to interpret
     * @param path      the path to interpret
     * @return The found texture resource
     */
    public static ResourceLocation getResource(final String namespace, final String path) {
        return new ResourceLocation(namespace, path);
    }

    /**
     * Retrieve a texture resource within the "minecraft" namespace using the specified path
     *
     * @param path the path to interpret
     * @return The found texture resource
     */
    public static ResourceLocation getResource(final String path) {
        return getResource("minecraft", path);
    }

    /**
     * Retrieve a texture resource using the specified path
     *
     * @param path the path to interpret
     * @return The found texture resource
     */
    public static ResourceLocation parseResource(final String path) {
        return new ResourceLocation(path);
    }

    /**
     * Returns an object representing an empty texture resource
     *
     * @return an object representing an empty texture resource
     */
    public static ResourceLocation getEmptyResource() {
        return EMPTY_RESOURCE;
    }
}
