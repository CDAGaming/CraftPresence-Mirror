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

package com.gitlab.cdagaming.craftpresence.impl.guava;

import com.google.common.base.Supplier;
import com.google.common.collect.*;

import java.io.Serializable;
import java.util.*;

import static com.gitlab.cdagaming.craftpresence.impl.guava.CollectPreconditions.checkNonnegative;
import static com.google.common.base.Preconditions.checkNotNull;

/**
 * A builder for a multimap implementation that allows customization of the backing map and value
 * collection implementations used in a particular multimap.
 *
 * <p>This can be used to easily configure multimap data structure implementations not provided
 * explicitly in {@code com.google.common.collect}, for example:
 *
 * <pre>   {@code
 *   ListMultimap<String, Integer> treeListMultimap =
 *       MultimapBuilder.treeKeys().arrayListValues().build();
 *   SetMultimap<Integer, MyEnum> hashEnumMultimap =
 *       MultimapBuilder.hashKeys().enumSetValues(MyEnum.class).build();}</pre>
 *
 * <p>{@code MultimapBuilder} instances are immutable.  Invoking a configuration method has no
 * effect on the receiving instance; you must store and use the new builder instance it returns
 * instead.
 *
 * <p>The generated multimaps are serializable if the key and value types are serializable,
 * unless stated otherwise in one of the configuration methods.
 *
 * @param <K0> An upper bound on the key type of the generated multimap.
 * @param <V0> An upper bound on the value type of the generated multimap.
 * @author Louis Wasserman
 * @since 16.0
 */
@SuppressWarnings({"unchecked", "Guava"})
public abstract class MultimapBuilder<K0, V0> {
    /*
     * Leaving K and V as upper bounds rather than the actual key and value types allows type
     * parameters to be left implicit more often. CacheBuilder uses the same technique.
     */

    private static final int DEFAULT_EXPECTED_KEYS = 8;

    private MultimapBuilder() {
    }

    /**
     * Uses a {@link HashMap} to map keys to value collections.
     */
    public static MultimapBuilderWithKeys<Object> hashKeys() {
        return hashKeys(DEFAULT_EXPECTED_KEYS);
    }

    /**
     * Uses a {@link HashMap} to map keys to value collections, initialized to expect the specified
     * number of keys.
     *
     * @throws IllegalArgumentException if {@code expectedKeys < 0}
     */
    public static MultimapBuilderWithKeys<Object> hashKeys(final int expectedKeys) {
        checkNonnegative(expectedKeys, "expectedKeys");
        return new MultimapBuilderWithKeys<Object>() {
            @Override
            <K, V> Map<K, Collection<V>> createMap() {
                return Maps.newHashMapWithExpectedSize(expectedKeys);
            }
        };
    }

    /**
     * Uses a {@link LinkedHashMap} to map keys to value collections.
     *
     * <p>The collections returned by {@link Multimap#keySet()}, {@link Multimap#keys()}, and
     * {@link Multimap#asMap()} will iterate through the keys in the order that they were first added
     * to the multimap, save that if all values associated with a key are removed and then the key is
     * added back into the multimap, that key will come last in the key iteration order.
     */
    public static MultimapBuilderWithKeys<Object> linkedHashKeys() {
        return linkedHashKeys(DEFAULT_EXPECTED_KEYS);
    }

    /**
     * Uses a {@link LinkedHashMap} to map keys to value collections, initialized to expect the
     * specified number of keys.
     *
     * <p>The collections returned by {@link Multimap#keySet()}, {@link Multimap#keys()}, and
     * {@link Multimap#asMap()} will iterate through the keys in the order that they were first added
     * to the multimap, save that if all values associated with a key are removed and then the key is
     * added back into the multimap, that key will come last in the key iteration order.
     */
    public static MultimapBuilderWithKeys<Object> linkedHashKeys(final int expectedKeys) {
        checkNonnegative(expectedKeys, "expectedKeys");
        return new MultimapBuilderWithKeys<Object>() {
            @Override
            <K, V> Map<K, Collection<V>> createMap() {
                return new LinkedHashMap<>(expectedKeys);
            }
        };
    }

    /**
     * Uses a naturally-ordered {@link TreeMap} to map keys to value collections.
     *
     * <p>The collections returned by {@link Multimap#keySet()}, {@link Multimap#keys()}, and
     * {@link Multimap#asMap()} will iterate through the keys in sorted order.
     *
     * <p>For all multimaps generated by the resulting builder, the {@link Multimap#keySet()} can be
     * safely cast to a {@link java.util.SortedSet}, and the {@link Multimap#asMap()} can safely be
     * cast to a {@link java.util.SortedMap}.
     */
    @SuppressWarnings("rawtypes")
    public static MultimapBuilderWithKeys<Comparable> treeKeys() {
        return treeKeys(Ordering.natural());
    }

    /**
     * Uses a {@link TreeMap} sorted by the specified comparator to map keys to value collections.
     *
     * <p>The collections returned by {@link Multimap#keySet()}, {@link Multimap#keys()}, and
     * {@link Multimap#asMap()} will iterate through the keys in sorted order.
     *
     * <p>For all multimaps generated by the resulting builder, the {@link Multimap#keySet()} can be
     * safely cast to a {@link java.util.SortedSet}, and the {@link Multimap#asMap()} can safely be
     * cast to a {@link java.util.SortedMap}.
     *
     * <p>Multimaps generated by the resulting builder will not be serializable if {@code comparator}
     * is not serializable.
     */
    public static <K0> MultimapBuilderWithKeys<K0> treeKeys(final Comparator<K0> comparator) {
        checkNotNull(comparator);
        return new MultimapBuilderWithKeys<K0>() {
            @Override
            <K extends K0, V> Map<K, Collection<V>> createMap() {
                return new TreeMap<>(comparator);
            }
        };
    }

    /**
     * Uses an {@link EnumMap} to map keys to value collections.
     */
    public static <K0 extends Enum<K0>> MultimapBuilderWithKeys<K0> enumKeys(
            final Class<K0> keyClass) {
        checkNotNull(keyClass);
        return new MultimapBuilderWithKeys<K0>() {
            @Override
            <K extends K0, V> Map<K, Collection<V>> createMap() {
                // K must actually be K0, since enums are effectively final
                // (their subclasses are inaccessible)
                return (Map<K, Collection<V>>) new EnumMap<K0, Collection<V>>(keyClass);
            }
        };
    }

    /**
     * Returns a new, empty {@code Multimap} with the specified implementation.
     */
    public abstract <K extends K0, V extends V0> Multimap<K, V> build();

    /**
     * Returns a {@code Multimap} with the specified implementation, initialized with the entries of
     * {@code multimap}.
     */
    public <K extends K0, V extends V0> Multimap<K, V> build(
            Multimap<? extends K, ? extends V> multimap) {
        Multimap<K, V> result = build();
        result.putAll(multimap);
        return result;
    }

    private enum LinkedListSupplier implements Supplier<List<Object>> {
        INSTANCE;

        public static <V> Supplier<List<V>> instance() {
            // Each call generates a fresh LinkedList, which can serve as a List<V> for any V.
            @SuppressWarnings("rawtypes")
            Supplier<List<V>> result = (Supplier) INSTANCE;
            return result;
        }

        @Override
        public List<Object> get() {
            return new LinkedList<>();
        }
    }

    private static final class ArrayListSupplier<V> implements Supplier<List<V>>, Serializable {
        /**
         * The serialized version identifier for this object
         */
        private static final long serialVersionUID = 1L;

        private final int expectedValuesPerKey;

        ArrayListSupplier(int expectedValuesPerKey) {
            this.expectedValuesPerKey = checkNonnegative(expectedValuesPerKey, "expectedValuesPerKey");
        }

        @Override
        public List<V> get() {
            return new ArrayList<V>(expectedValuesPerKey);
        }
    }

    private static final class HashSetSupplier<V> implements Supplier<Set<V>>, Serializable {
        /**
         * The serialized version identifier for this object
         */
        private static final long serialVersionUID = 1L;

        private final int expectedValuesPerKey;

        HashSetSupplier(int expectedValuesPerKey) {
            this.expectedValuesPerKey = checkNonnegative(expectedValuesPerKey, "expectedValuesPerKey");
        }

        @Override
        public Set<V> get() {
            return Sets.newHashSetWithExpectedSize(expectedValuesPerKey);
        }
    }

    private static final class LinkedHashSetSupplier<V> implements Supplier<Set<V>>, Serializable {
        /**
         * The serialized version identifier for this object
         */
        private static final long serialVersionUID = 1L;

        private final int expectedValuesPerKey;

        LinkedHashSetSupplier(int expectedValuesPerKey) {
            this.expectedValuesPerKey = checkNonnegative(expectedValuesPerKey, "expectedValuesPerKey");
        }

        @Override
        public Set<V> get() {
            return Sets.newLinkedHashSetWithExpectedSize(expectedValuesPerKey);
        }
    }

    private static final class TreeSetSupplier<V> implements Supplier<SortedSet<V>>, Serializable {
        /**
         * The serialized version identifier for this object
         */
        private static final long serialVersionUID = 1L;

        private final Comparator<? super V> comparator;

        TreeSetSupplier(Comparator<? super V> comparator) {
            this.comparator = checkNotNull(comparator);
        }

        @Override
        public SortedSet<V> get() {
            return new TreeSet<>(comparator);
        }
    }

    private static final class EnumSetSupplier<V extends Enum<V>>
            implements Supplier<Set<V>>, Serializable {
        /**
         * The serialized version identifier for this object
         */
        private static final long serialVersionUID = 1L;

        private final Class<V> clazz;

        EnumSetSupplier(Class<V> clazz) {
            this.clazz = checkNotNull(clazz);
        }

        @Override
        public Set<V> get() {
            return EnumSet.noneOf(clazz);
        }
    }

    /**
     * An intermediate stage in a {@link MultimapBuilder} in which the key-value collection map
     * implementation has been specified, but the value collection implementation has not.
     *
     * @param <K0> The upper bound on the key type of the generated multimap.
     */
    public abstract static class MultimapBuilderWithKeys<K0> {

        private static final int DEFAULT_EXPECTED_VALUES_PER_KEY = 2;

        MultimapBuilderWithKeys() {
        }

        abstract <K extends K0, V> Map<K, Collection<V>> createMap();

        /**
         * Uses an {@link ArrayList} to store value collections.
         */
        public ListMultimapBuilder<K0, Object> arrayListValues() {
            return arrayListValues(DEFAULT_EXPECTED_VALUES_PER_KEY);
        }

        /**
         * Uses an {@link ArrayList} to store value collections, initialized to expect the specified
         * number of values per key.
         *
         * @throws IllegalArgumentException if {@code expectedValuesPerKey < 0}
         */
        public ListMultimapBuilder<K0, Object> arrayListValues(final int expectedValuesPerKey) {
            checkNonnegative(expectedValuesPerKey, "expectedValuesPerKey");
            return new ListMultimapBuilder<K0, Object>() {
                @Override
                public <K extends K0, V> ListMultimap<K, V> build() {
                    return Multimaps.newListMultimap(
                            MultimapBuilderWithKeys.this.createMap(),
                            new ArrayListSupplier<>(expectedValuesPerKey));
                }
            };
        }

        /**
         * Uses a {@link LinkedList} to store value collections.
         */
        public ListMultimapBuilder<K0, Object> linkedListValues() {
            return new ListMultimapBuilder<K0, Object>() {
                @Override
                public <K extends K0, V> ListMultimap<K, V> build() {
                    return Multimaps.newListMultimap(
                            MultimapBuilderWithKeys.this.createMap(), LinkedListSupplier.instance());
                }
            };
        }

        /**
         * Uses a {@link HashSet} to store value collections.
         */
        public SetMultimapBuilder<K0, Object> hashSetValues() {
            return hashSetValues(DEFAULT_EXPECTED_VALUES_PER_KEY);
        }

        /**
         * Uses a {@link HashSet} to store value collections, initialized to expect the specified number
         * of values per key.
         *
         * @throws IllegalArgumentException if {@code expectedValuesPerKey < 0}
         */
        public SetMultimapBuilder<K0, Object> hashSetValues(final int expectedValuesPerKey) {
            checkNonnegative(expectedValuesPerKey, "expectedValuesPerKey");
            return new SetMultimapBuilder<K0, Object>() {
                @Override
                public <K extends K0, V> SetMultimap<K, V> build() {
                    return Multimaps.newSetMultimap(
                            MultimapBuilderWithKeys.this.createMap(),
                            new HashSetSupplier<>(expectedValuesPerKey));
                }
            };
        }

        /**
         * Uses a {@link LinkedHashSet} to store value collections.
         */
        public SetMultimapBuilder<K0, Object> linkedHashSetValues() {
            return linkedHashSetValues(DEFAULT_EXPECTED_VALUES_PER_KEY);
        }

        /**
         * Uses a {@link LinkedHashSet} to store value collections, initialized to expect the specified
         * number of values per key.
         *
         * @throws IllegalArgumentException if {@code expectedValuesPerKey < 0}
         */
        public SetMultimapBuilder<K0, Object> linkedHashSetValues(final int expectedValuesPerKey) {
            checkNonnegative(expectedValuesPerKey, "expectedValuesPerKey");
            return new SetMultimapBuilder<K0, Object>() {
                @Override
                public <K extends K0, V> SetMultimap<K, V> build() {
                    return Multimaps.newSetMultimap(
                            MultimapBuilderWithKeys.this.createMap(),
                            new LinkedHashSetSupplier<>(expectedValuesPerKey));
                }
            };
        }

        /**
         * Uses a naturally-ordered {@link TreeSet} to store value collections.
         */
        @SuppressWarnings("rawtypes")
        public SortedSetMultimapBuilder<K0, Comparable> treeSetValues() {
            return treeSetValues(Ordering.natural());
        }

        /**
         * Uses a {@link TreeSet} ordered by the specified comparator to store value collections.
         *
         * <p>Multimaps generated by the resulting builder will not be serializable if
         * {@code comparator} is not serializable.
         */
        public <V0> SortedSetMultimapBuilder<K0, V0> treeSetValues(final Comparator<V0> comparator) {
            checkNotNull(comparator, "comparator");
            return new SortedSetMultimapBuilder<K0, V0>() {
                @Override
                public <K extends K0, V extends V0> SortedSetMultimap<K, V> build() {
                    return Multimaps.newSortedSetMultimap(
                            MultimapBuilderWithKeys.this.createMap(), new TreeSetSupplier<>(comparator));
                }
            };
        }

        /**
         * Uses an {@link EnumSet} to store value collections.
         */
        public <V0 extends Enum<V0>> SetMultimapBuilder<K0, V0> enumSetValues(
                final Class<V0> valueClass) {
            checkNotNull(valueClass, "valueClass");
            return new SetMultimapBuilder<K0, V0>() {
                @Override
                public <K extends K0, V extends V0> SetMultimap<K, V> build() {
                    // V must actually be V0, since enums are effectively final
                    // (their subclasses are inaccessible)
                    @SuppressWarnings("rawtypes")
                    Supplier<Set<V>> factory = (Supplier) new EnumSetSupplier<V0>(valueClass);
                    return Multimaps.newSetMultimap(MultimapBuilderWithKeys.this.createMap(), factory);
                }
            };
        }
    }

    /**
     * A specialization of {@link MultimapBuilder} that generates {@link ListMultimap} instances.
     */
    public abstract static class ListMultimapBuilder<K0, V0> extends MultimapBuilder<K0, V0> {
        ListMultimapBuilder() {
        }

        @Override
        public abstract <K extends K0, V extends V0> ListMultimap<K, V> build();

        @Override
        public <K extends K0, V extends V0> ListMultimap<K, V> build(
                Multimap<? extends K, ? extends V> multimap) {
            return (ListMultimap<K, V>) super.build(multimap);
        }
    }

    /**
     * A specialization of {@link MultimapBuilder} that generates {@link SetMultimap} instances.
     */
    public abstract static class SetMultimapBuilder<K0, V0> extends MultimapBuilder<K0, V0> {
        SetMultimapBuilder() {
        }

        @Override
        public abstract <K extends K0, V extends V0> SetMultimap<K, V> build();

        @Override
        public <K extends K0, V extends V0> SetMultimap<K, V> build(
                Multimap<? extends K, ? extends V> multimap) {
            return (SetMultimap<K, V>) super.build(multimap);
        }
    }

    /**
     * A specialization of {@link MultimapBuilder} that generates {@link SortedSetMultimap} instances.
     */
    public abstract static class SortedSetMultimapBuilder<K0, V0> extends SetMultimapBuilder<K0, V0> {
        SortedSetMultimapBuilder() {
        }

        @Override
        public abstract <K extends K0, V extends V0> SortedSetMultimap<K, V> build();

        @Override
        public <K extends K0, V extends V0> SortedSetMultimap<K, V> build(
                Multimap<? extends K, ? extends V> multimap) {
            return (SortedSetMultimap<K, V>) super.build(multimap);
        }
    }
}
