package com.mikerusoft.example.java8;

import org.junit.Test;

import java.util.Collections;
import java.util.stream.Stream;

import static org.assertj.core.api.Assertions.assertThat;

public class StreamsTest {

    @Test(expected = NullPointerException.class)
    public void whenIteratorIsNull_expectedNullPointerException() {
        Streams.of(null);
    }

    @Test
    public void whenIteratorNonEmpty_expectedStreamOfValuesFromIterator() {
        Stream<String> stream = Streams.of(Collections.singletonList("Hello").iterator());
        assertThat(stream).isNotNull().isInstanceOf(Stream.class);
        stream.forEach(t -> {
            assertThat(t).isNotNull().isEqualTo("Hello");
        });
    }
}