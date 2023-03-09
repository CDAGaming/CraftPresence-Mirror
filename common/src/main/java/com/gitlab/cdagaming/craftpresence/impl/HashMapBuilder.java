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

package com.gitlab.cdagaming.craftpresence.impl;

import com.gitlab.cdagaming.craftpresence.utils.StringUtils;

import java.util.Map;

/**
 * A builder class for creating a new HashMap instance and adding key-value pairs to it.
 *
 * @param <K> the type of keys in the HashMap
 * @param <V> the type of values in the HashMap
 * @author CDAGaming
 */
public class HashMapBuilder<K, V> {
    private final Map<K, V> map;

    /**
     * Constructs a new HashMapBuilder with a new HashMap instance.
     */
    public HashMapBuilder() {
        map = StringUtils.newHashMap();
    }

    /**
     * Adds a new key-value pair to the HashMap.
     *
     * @param key   the key to add to the HashMap
     * @param value the value to associate with the key in the HashMap
     * @return a reference to this HashMapBuilder instance
     */
    public HashMapBuilder<K, V> put(K key, V value) {
        map.put(key, value);
        return this;
    }

    /**
     * Returns the final HashMap instance with all key-value pairs added using the builder.
     *
     * @return the final HashMap instance
     */
    public Map<K, V> build() {
        return map;
    }
}
