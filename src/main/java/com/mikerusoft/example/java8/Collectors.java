package com.mikerusoft.example.java8;

import com.google.common.collect.ArrayListMultimap;
import com.google.common.collect.Multimap;

import java.util.*;
import java.util.concurrent.ConcurrentHashMap;
import java.util.function.BiConsumer;
import java.util.function.BinaryOperator;
import java.util.function.Function;
import java.util.function.Supplier;
import java.util.stream.Collector;

/**
 * @author Grinfeld Mikhail
 * @since 8/1/2017.
 */
public class Collectors {
    /**
     * In contrast to {@link java.util.stream.Collectors#toMap(Function, Function)} the result map
     * may have null values.
     * Thanks to sjngm from stackoverflow (http://stackoverflow.com/users/483113/sjngm)
     */
    public static <T, K, U, M extends Map<K, U>> Collector<T, M, M> toMapWithNullValues(Function<? super T, ? extends K> keyMapper, Function<? super T, ? extends U> valueMapper) {
        return toMapWithNullValues(keyMapper, valueMapper, HashMap::new);
    }

    /**
     * In contrast to {@link java.util.stream.Collectors#toMap(Function, Function, BinaryOperator, Supplier)}
     * the result map may have null values.
     * Thanks to sjngm from stackoverflow (http://stackoverflow.com/users/483113/sjngm)
     */
    public static <T, K, U, M extends Map<K, U>> Collector<T, M, M> toMapWithNullValues(Function<? super T, ? extends K> keyMapper, Function<? super T, ? extends U> valueMapper, Supplier<Map<K, U>> supplier) {
        return new MapWithNullValuesCollector<>(keyMapper, valueMapper, supplier);
    }

    public static <T> Collector<T, ?, Set<T>> toConcurrentSet() {
        return new CollectorImpl<>(ConcurrentHashMap::newKeySet, Set::add,
                (left, right) -> { left.addAll(right); return left; },
                CollectorImpl.CH_CONCURRENT_NOID);
    }

    public static <I,K,V> MultiMapCollector<I,K,V> toMultimap(Function<I, K> keyCreator, Function<I, V> valueCreator) {
        return new MultiMapCollector<>(keyCreator, valueCreator);
    }

    static class MapWithNullValuesCollector<T, K, U, M extends Map<K, U>> implements Collector<T, M, M> {

        private Function<? super T, ? extends K> keyMapper;
        private Function<? super T, ? extends U> valueMapper;
        private Supplier<Map<K, U>> supplier;

        public MapWithNullValuesCollector(Function<? super T, ? extends K> keyMapper, Function<? super T, ? extends U> valueMapper, Supplier<Map<K, U>> supplier) {
            this.keyMapper = keyMapper;
            this.valueMapper = valueMapper;
            this.supplier = supplier;
        }

        @Override
        public Supplier<M> supplier() {
            return () -> {
                @SuppressWarnings("unchecked")
                M map = (M) supplier.get();
                return map;
            };
        }

        @Override
        public BiConsumer<M, T> accumulator() {
            return (map, element) -> {
                K key = keyMapper.apply(element);
                if (map.containsKey(key)) {
                    throw new IllegalStateException("Duplicate key " + key);
                }
                map.put(key, valueMapper.apply(element));
            };
        }

        @Override
        public BinaryOperator<M> combiner() {
            return (map1, map2) -> {
                map1.putAll(map2);
                return map1;
            };
        }

        @Override
        public Function<M, M> finisher() {
            return Function.identity();
        }

        @Override
        public Set<Characteristics> characteristics() {
            return Collections.unmodifiableSet(EnumSet.of(Characteristics.IDENTITY_FINISH));
        }
    }

    /**
     * This class is copy with small changes of inner (package level) CollectorImpl in Collectors class.
     * Make it easy to create Collector for concurrent set. See {@link Collectors#toConcurrentSet()}
     *
     * Simple implementation class for {@code Collector} of Set
     *
     * @param <T> the type of elements to be collected
     * @param <R> the type of the result
     */
    static class CollectorImpl<T, A, R> implements Collector<T, Set<A>, R> {

        static final Set<Characteristics> CH_CONCURRENT_NOID
                = Collections.unmodifiableSet(EnumSet.of(Characteristics.CONCURRENT,
                Characteristics.UNORDERED));

        static final Set<Characteristics> CH_UNORDERED_ID
                = Collections.unmodifiableSet(EnumSet.of(Characteristics.UNORDERED,
                Characteristics.IDENTITY_FINISH));

        private final Supplier<Set<A>> supplier;
        private final BiConsumer<Set<A>, T> accumulator;
        private final BinaryOperator<Set<A>> combiner;
        private final Function<Set<A>, R> finisher;
        private final Set<Characteristics> characteristics;

        public CollectorImpl(Supplier<Set<A>> supplier,
                      BiConsumer<Set<A>, T> accumulator,
                      BinaryOperator<Set<A>> combiner, Set<Characteristics> characteristics) {
            this.supplier = supplier;
            this.accumulator = accumulator;
            this.combiner = combiner;
            this.finisher = i -> (R) i;
            this.characteristics = characteristics;
        }

        public BiConsumer<Set<A>, T> accumulator() {
            return accumulator;
        }

        public Supplier<Set<A>> supplier() {
            return supplier;
        }

        public BinaryOperator<Set<A>> combiner() {
            return combiner;
        }

        public Function<Set<A>, R> finisher() {
            return finisher;
        }

        public Set<Characteristics> characteristics() {
            return characteristics;
        }
    }

    static class MultiMapCollector<I,K,V> implements Collector<I, Multimap<K, V>, Multimap<K, V>> {

        private Function<I, K> keyCreator;
        private Function<I, V> valueCreator;

        private MultiMapCollector(Function<I, K> keyCreator, Function<I, V> valueCreator) {
            this.keyCreator = keyCreator;
            this.valueCreator = valueCreator;
        }

        @Override
        public Supplier<Multimap<K, V>> supplier() {
            return ArrayListMultimap::create;
        }

        @Override
        public BiConsumer<Multimap<K, V>, I> accumulator() {
            return (map, val) -> map.put(keyCreator.apply(val), valueCreator.apply(val));
        }

        @Override
        public BinaryOperator<Multimap<K, V>> combiner() {
            return (l, r) -> { l.putAll(r); return l; };
        }

        @Override
        public Function<Multimap<K, V>, Multimap<K, V>> finisher() {
            return Function.identity();
        }

        @Override
        public Set<Characteristics> characteristics() {
            return Collections.emptySet();
        }
    }
}
