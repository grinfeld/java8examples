package com.mikerusoft.example.java8;

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
public class LambdaUtilities {
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
        return new Collector<T, M, M>() {

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
                return Collections.unmodifiableSet(EnumSet.of(Collector.Characteristics.IDENTITY_FINISH));
            }

        };
    }

    public static <T> Collector<T, ?, Set<T>> toConcurrentSet() {
        return new CollectorImpl<>(ConcurrentHashMap::newKeySet, Set::add,
                (left, right) -> { left.addAll(right); return left; },
                CollectorImpl.CH_CONCURRENT_NOID);
    }

    /**
     * This class is copy with small changes of inner (package level) CollectorImpl in Collectors class.
     * Make it easy to create Collector for concurrent set. See {@link LambdaUtilities#toConcurrentSet()}
     *
     * Simple implementation class for {@code Collector} of Set
     *
     * @param <T> the type of elements to be collected
     * @param <R> the type of the result
     */
    static class CollectorImpl<T, A, R> implements Collector<T, Set<A>, R> {

        static final Set<Collector.Characteristics> CH_CONCURRENT_NOID
                = Collections.unmodifiableSet(EnumSet.of(Collector.Characteristics.CONCURRENT,
                Collector.Characteristics.UNORDERED));

        static final Set<Collector.Characteristics> CH_UNORDERED_ID
                = Collections.unmodifiableSet(EnumSet.of(Collector.Characteristics.UNORDERED,
                Collector.Characteristics.IDENTITY_FINISH));

        private final Supplier<Set<A>> supplier;
        private final BiConsumer<Set<A>, T> accumulator;
        private final BinaryOperator<Set<A>> combiner;
        private final Function<Set<A>, R> finisher;
        private final Set<Characteristics> characteristics;

        CollectorImpl(Supplier<Set<A>> supplier,
                      BiConsumer<Set<A>, T> accumulator,
                      BinaryOperator<Set<A>> combiner, Set<Characteristics> characteristics) {
            this.supplier = supplier;
            this.accumulator = accumulator;
            this.combiner = combiner;
            this.finisher = i -> (R) i;
            this.characteristics = characteristics;
        }

        @Override
        public BiConsumer<Set<A>, T> accumulator() {
            return accumulator;
        }

        @Override
        public Supplier<Set<A>> supplier() {
            return supplier;
        }

        @Override
        public BinaryOperator<Set<A>> combiner() {
            return combiner;
        }

        @Override
        public Function<Set<A>, R> finisher() {
            return finisher;
        }

        @Override
        public Set<Characteristics> characteristics() {
            return characteristics;
        }
    }
}
