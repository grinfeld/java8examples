package com.mikerusoft.example.java8;

import org.junit.Test;

import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.function.Function;
import java.util.stream.Stream;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

/**
 * @author Grinfeld Mikhail
 * @since 8/1/2017.
 */
public class LambdaUtilitiesTest {
    @Test
    public void testToMapWithNullValues() throws Exception {
        Map<Integer, Integer> result = Stream.of(1, 2, 3)
                .collect(LambdaUtilities.toMapWithNullValues(Function.identity(), x -> x % 2 == 1 ? x : null));

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
                .collect(LambdaUtilities.toMapWithNullValues(Function.identity(), x -> x % 2 == 1 ? x : null, LinkedHashMap::new));

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
                .collect(LambdaUtilities.toMapWithNullValues(Function.identity(), x -> x % 2 == 1 ? x : null)))
                .isExactlyInstanceOf(IllegalStateException.class)
                .hasMessage("Duplicate key 1");
    }

    @Test
    public void testToMapWithNullValuesParallel() throws Exception {
        Map<Integer, Integer> result = Stream.of(1, 2, 3)
                .parallel() // this causes .combiner() to be called
                .collect(LambdaUtilities.toMapWithNullValues(Function.identity(), x -> x % 2 == 1 ? x : null));

        assertThat(result)
                .isExactlyInstanceOf(HashMap.class)
                .hasSize(3)
                .containsEntry(1, 1)
                .containsEntry(2, null)
                .containsEntry(3, 3);
    }

}
