package com.mikerusoft.example.java8;

import com.google.common.collect.Multimap;
import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.Getter;
import org.junit.Test;

import java.util.*;
import java.util.function.Function;
import java.util.stream.Stream;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

/**
 * @author Grinfeld Mikhail
 * @since 8/1/2017.
 */
public class CollectorsTest {

    @Test
    public void testToMapWithNullValues() throws Exception {
        Map<Integer, Integer> result = Stream.of(1, 2, 3)
                .collect(Collectors.toMapWithNullValues(Function.identity(), x -> x % 2 == 1 ? x : null));

        assertThat(result)
                .isExactlyInstanceOf(HashMap.class)
                .hasSize(3)
                .containsEntry(1, 1)
                .containsEntry(2, null)
                .containsEntry(3, 3);
    }

    @Test
    public void testToMapWithNullValuesWithSupplier() throws Exception {
        Map<Integer, Integer> result = Stream.of(1, 2, 3)
                .collect(Collectors.toMapWithNullValues(Function.identity(), x -> x % 2 == 1 ? x : null, LinkedHashMap::new));

        assertThat(result)
                .isExactlyInstanceOf(LinkedHashMap.class)
                .hasSize(3)
                .containsEntry(1, 1)
                .containsEntry(2, null)
                .containsEntry(3, 3);
    }

    @Test
    public void testToMapWithNullValuesDuplicate() throws Exception {
        assertThatThrownBy(() -> Stream.of(1, 2, 3, 1)
                .collect(Collectors.toMapWithNullValues(Function.identity(), x -> x % 2 == 1 ? x : null)))
                .isExactlyInstanceOf(IllegalStateException.class)
                .hasMessage("Duplicate key 1");
    }

    @Test
    public void testToMapWithNullValuesParallel() throws Exception {
        Map<Integer, Integer> result = Stream.of(1, 2, 3)
                .parallel() // this causes .combiner() to be called
                .collect(Collectors.toMapWithNullValues(Function.identity(), x -> x % 2 == 1 ? x : null));

        assertThat(result)
                .isExactlyInstanceOf(HashMap.class)
                .hasSize(3)
                .containsEntry(1, 1)
                .containsEntry(2, null)
                .containsEntry(3, 3);
    }

    @Getter
    @AllArgsConstructor(access = AccessLevel.PRIVATE)
    static class Pair<L,R> {
        private L left;
        private R right;

        static <L, R> Pair<L, R> of(L left, R right) {
            return new Pair<>(left, right);
        }
    }

    @Test
    public void whenStream_withOneDifferentKey_expectedMultimapWithOneCollection() {
        Multimap<String, String> collect =
            Stream.of(Pair.of("1", "2")).collect(Collectors.toMultimap(Pair::getLeft, Pair::getRight));

        assertThat(collect).isNotNull();
        assertThat(collect.asMap()).isNotNull().hasSize(1).containsOnlyKeys("1");
        assertThat(collect.get("1")).isNotNull().hasSize(1).containsExactly("2");
    }

    @Test
    public void whenStream_withTwoDifferentKeys_expectedMultimapWithTwoCollections() {
        Multimap<String, String> collect =
            Stream.of(Pair.of("1", "2"), Pair.of("2", "2"))
                .collect(Collectors.toMultimap(Pair::getLeft, Pair::getRight));

        assertThat(collect).isNotNull();
        assertThat(collect.asMap()).isNotNull().hasSize(2).containsOnlyKeys("1", "2");
        assertThat(collect.get("1")).isNotNull().hasSize(1).containsExactly("2");
        assertThat(collect.get("2")).isNotNull().hasSize(1).containsExactly("2");
    }

    @Test
    public void whenStream_withTwoSameKeys_expectedMultimapWithOneCollectionWith2Values() {
        Multimap<String, String> collect =
            Stream.of(Pair.of("1", "2"), Pair.of("1", "3"))
                .collect(Collectors.toMultimap(Pair::getLeft, Pair::getRight));

        assertThat(collect).isNotNull();
        assertThat(collect.asMap()).isNotNull().hasSize(1).containsOnlyKeys("1");
        assertThat(collect.get("1")).isNotNull().hasSize(2).containsExactly("2", "3");
    }

    @Test
    public void whenEmptyStream_expectedMultimapWithOneCollectionWith2Values() {
        Multimap<String, String> collect =
            new ArrayList<Pair<String, String>>().stream()
                .collect(Collectors.toMultimap(Pair::getLeft, Pair::getRight));

        assertThat(collect).isNotNull();
        assertThat(collect.asMap()).isNotNull().hasSize(0);
    }
}
