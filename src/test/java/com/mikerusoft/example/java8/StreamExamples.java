package com.mikerusoft.example.java8;

import org.junit.Test;
import static org.assertj.core.api.Assertions.*;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

/**
 * @author Grinfeld Mikhail
 * @since 7/31/2017.
 */
public class StreamExamples {

    @Test
    public void list2mapOfLists() {
        Map<String, String> book1Details = new HashMap<>();
        book1Details.put("1234", "author1");
        book1Details.put("5678", "author2");
        Book book1 = new Book(book1Details);

        Map<String, String> book2Details = new HashMap<>();
        book2Details.put("1234", "author2");
        Book book2 = new Book(book2Details);

        List<Book> books = new ArrayList<>();
        books.add(book1);
        books.add(book2);

        Map<String, List<String>> library =
            books.stream()
                .flatMap(b -> b.getAttribute().entrySet().stream())
                .collect(
                    Collectors.groupingBy(
                        Map.Entry::getKey,
                        Collectors.mapping(
                            Map.Entry::getValue, Collectors.toList()
                        )
                    )
                );

        assertThat(library).containsOnlyKeys("1234", "5678");

        List<String> l1 = library.get("1234");
        assertThat(l1).hasSize(2).contains("author1", "author2");

        List<String> l2 = library.get("5678");
        assertThat(l2).hasSize(1).contains("author2");
    }

    public static class Book {
        //key is isbn, val is author
        private Map<String, String> attribute;

        public Book(Map<String, String> attribute) {
            super();
            this.attribute = attribute;
        }


        public Map<String, String> getAttribute() {
            return attribute;
        }
        public void setAttribute(Map<String, String> attribute) {
            this.attribute = attribute;
        }

    }
}
