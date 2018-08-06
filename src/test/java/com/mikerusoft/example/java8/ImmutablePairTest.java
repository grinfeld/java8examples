package com.mikerusoft.example.java8;


import org.junit.Test;

import java.util.NoSuchElementException;

import static org.assertj.core.api.Assertions.assertThat;

public class ImmutablePairTest {

    @Test
    public void of_whenLeftAndRightBothNull_expectedPairWithNullValuesAndNotEmpty() {
        assertThat(ImmutablePair.of(null, null)).isNotNull()
                .hasFieldOrPropertyWithValue("left", null)
                .hasFieldOrPropertyWithValue("right", null)
                .isNotSameAs(ImmutablePair.empty());
    }

    @Test
    public void of_whenPairOfStrings_expectedPairWithStrings() {
        assertThat(ImmutablePair.of("1", "2")).isNotNull()
                .hasFieldOrPropertyWithValue("left", "1")
                .hasFieldOrPropertyWithValue("right", "2")
                .isNotSameAs(ImmutablePair.empty()).isEqualTo(ImmutablePair.of("1", "2"));
    }

    @Test
    public void of_whenPairOfLong_expectedPairWithStrings() {
        assertThat(ImmutablePair.of(1L, 2L)).isNotNull()
                .hasFieldOrPropertyWithValue("left", 1L)
                .hasFieldOrPropertyWithValue("right", 2L)
                .isNotSameAs(ImmutablePair.empty()).isEqualTo(ImmutablePair.of(1L, 2L));
    }


    @Test(expected = NullPointerException.class)
    public void map_whenMapperFuncIsNull_expectedNullPointerException() {
        ImmutablePair.of("1", "2").map(null);
    }

    @Test(expected = RuntimeException.class)
    public void map_whenMapperFuncThrowsException_expectedRuntimeException() {
        ImmutablePair.of("1", "2").map(pair -> {
            throw new RuntimeException();
        });
    }

    @Test
    public void map_withNoDefSupplier_whenMapperEmptyPairMapWithGetLeftGetRight_expectedNull() {
        String map = ImmutablePair.of("1", "2").filter(p -> false).map(p -> p.getLeft() + "_" + p.getRight());
        assertThat(map).isNull();
    }

    @Test
    public void map_withDefSupplier_whenMapperEmptyPairMapWithGetLeftGetRight_expectedNull() {
        assertThat(ImmutablePair.of("1", "2").filter(p -> false)
                .map(p -> p.getLeft() + "_" + p.getRight(), () -> "staaam"))
                .isNotNull().isEqualTo("staaam");
    }

    @Test
    public void map_withNoDefSupplier_whenConcatenateLeftRight_expectedConcatenatedString() {
        String map = ImmutablePair.of("1", "2").map(p -> p.getLeft() + "_" + p.getRight());
        assertThat(map).isNotNull().isEqualTo("1_2");
    }

    @Test
    public void map_withDefSupplier_whenConcatenateLeftRight_expectedConcatenatedString() {
        String map = ImmutablePair.of("1", "2").map(p -> p.getLeft() + "_" + p.getRight(), () -> "staaam");
        assertThat(map).isNotNull().isEqualTo("1_2");
    }


    @Test(expected = NullPointerException.class)
    public void flatmap_whenMapperFuncIsNull_expectedNullPointerException() {
        ImmutablePair.of("1", "2").flatMap(null);
    }

    @Test(expected = RuntimeException.class)
    public void flatmap_whenMapperFuncThrowsException_expectedRuntimeException() {
        ImmutablePair.of("1", "2").flatMap(pair -> {
            throw new RuntimeException();
        });
    }

    @Test
    public void flatmap_withStringPair_whenMapperFuncReverseValues_expectedPairWithReversedValues() {
        assertThat(ImmutablePair.of("1", "2").flatMap(pair -> ImmutablePair.of(pair.getRight(), pair.getLeft())))
                .isNotNull()
                .hasFieldOrPropertyWithValue("left", "2")
                .hasFieldOrPropertyWithValue("right", "1")
                .isNotSameAs(ImmutablePair.empty()).isEqualTo(ImmutablePair.of("2", "1"));
    }

    @Test
    public void flatmap_withLongPair_whenMapperFuncConvertToString_expectedStringPair() {
        assertThat(ImmutablePair.of(1, 2).flatMap(pair -> ImmutablePair.of(String.valueOf(pair.getLeft()), String.valueOf(pair.getRight()))))
                .isNotNull()
                .hasFieldOrPropertyWithValue("left", "1")
                .hasFieldOrPropertyWithValue("right", "2")
                .isNotSameAs(ImmutablePair.empty()).isEqualTo(ImmutablePair.of("1", "2"));
    }


    @Test(expected = NullPointerException.class)
    public void filter_whenPredicateIsNull_expectedNullPointerException() {
        ImmutablePair.of("1", "2").filter(null);
    }

    @Test
    public void filter_whenPredicateReturnsFalse_expectedEmptyPair() {
        assertThat(ImmutablePair.of(1, 2).filter(p -> false)).isNotNull().isSameAs(ImmutablePair.empty());
    }

    @Test
    public void filter_whenPredicateReturnsTrue_expectedEmptyPair() {
        assertThat(ImmutablePair.of(1, 2).filter(p -> true)).isNotNull().isNotSameAs(ImmutablePair.empty()).isEqualTo(ImmutablePair.of(1, 2));
    }


    @Test(expected = NoSuchElementException.class)
    public void getRight_whenEmptyPair_expectedNoSuchElementException() {
        ImmutablePair.of("1", "2").filter(p -> false).getRight();
    }

    @Test(expected = NoSuchElementException.class)
    public void getLeft_whenEmptyPair_expectedNoSuchElementException() {
        ImmutablePair.of("1", "2").filter(p -> false).getLeft();
    }


    @Test
    public void getRightElse_whenNonEmpty_expectedOriginalPair() {
        assertThat(ImmutablePair.of("1", "2")
                .getRightElse("staaam")).isNotNull().isEqualTo("2");
    }

    @Test
    public void getLeftElse_whenNonEmpty_expectedOriginalPair() {
        assertThat(ImmutablePair.of("1", "2")
                .getLeftElse("staaam")).isNotNull().isEqualTo("1");
    }

    @Test
    public void getRightElse_whenEmptyPair_expectedDefaultValue() {
        assertThat(ImmutablePair.of("1", "2").filter(p -> false)
                .getRightElse("staaam")).isNotNull().isEqualTo("staaam");
    }

    @Test
    public void getLeftElse_whenEmptyPair_expectedDefaultValue() {
        assertThat(ImmutablePair.of("1", "2").filter(p -> false)
                .getLeftElse("staaam")).isNotNull().isEqualTo("staaam");
    }


    @Test
    public void getLeftThrow_whenEmptyPairThrowClassNotFoundException_expectedClassCastException() {
        try {
            ImmutablePair.of("1", "2").filter(t -> false).getLeftElseThrow(ClassCastException::new);
        } catch (Throwable e) {
            assertThat(e).isNotNull().isInstanceOf(RuntimeException.class).isInstanceOf(ClassCastException.class);
        }
    }

    @Test
    public void getRightThrow_whenEmptyPairThrowClassNotFoundException_expectedCastFoundException() {
        try {
            ImmutablePair.of("1", "2").filter(t -> false).getRightElseThrow(ClassCastException::new);
        } catch (Throwable e) {
            assertThat(e).isNotNull().isInstanceOf(RuntimeException.class).isInstanceOf(ClassCastException.class);
        }
    }

    @Test
    public void getLeftThrow_whenEmptyPairThrowClassNotFoundException_expectedClassNotFoundException() {
        try {
            ImmutablePair.of("1", "2").filter(t -> false).getLeftElseThrow(ClassNotFoundException::new);
        } catch (Throwable e) {
            assertThat(e).isNotNull().isInstanceOf(RuntimeException.class);
            assertThat(e.getCause()).isNotNull().isInstanceOf(ClassNotFoundException.class);
        }
    }

    @Test
    public void getRightThrow_whenEmptyPairThrowClassNotFoundException_expectedClassNotFoundException() {
        try {
            ImmutablePair.of("1", "2").filter(t -> false).getRightElseThrow(ClassNotFoundException::new);
        } catch (Throwable e) {
            assertThat(e).isNotNull().isInstanceOf(RuntimeException.class);
            assertThat(e.getCause()).isNotNull().isInstanceOf(ClassNotFoundException.class);
        }
    }

    @Test
    public void getLeftElseThrow_whenNonEmptyElseThrowClassCastException_expectedDefaultValue() {
        assertThat(ImmutablePair.of("1", "2").getLeftElseThrow(ClassCastException::new)).isNotNull()
                .isNotSameAs(ImmutablePair.empty()).isEqualTo("1");

    }

    @Test
    public void getRightElseThrow_whenNonEmptyElseThrowClassCastException_expectedDefaultValue() {
        assertThat(ImmutablePair.of("1", "2").getRightElseThrow(ClassCastException::new)).isNotNull()
                .isNotSameAs(ImmutablePair.empty()).isEqualTo("2");

    }
}