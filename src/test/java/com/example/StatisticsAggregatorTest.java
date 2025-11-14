package com.example;

import org.junit.jupiter.api.Test;

import java.util.Map;

import static org.junit.jupiter.api.Assertions.*;

public class StatisticsAggregatorTest {

    @Test
    public void countsGenres() {
        StatisticsAggregator agg = new StatisticsAggregator();

        Book b1 = new Book();
        b1.setGenre("Romance, Tragedy");

        Book b2 = new Book();
        b2.setGenre("Romance");

        agg.accept(b1, "genre");
        agg.accept(b2, "genre");

        Map<String, Long> map = agg.snapshotSortedDesc();
        assertEquals(2L, map.get("Romance"));
        assertEquals(1L, map.get("Tragedy"));
    }

    @Test
    public void countsAuthorsAndYear() {
        StatisticsAggregator agg = new StatisticsAggregator();

        Book b1 = new Book();
        b1.setAuthor("Alice");
        b1.setYear_published(2000);

        Book b2 = new Book();
        b2.setAuthor("Bob");
        b2.setYear_published(2000);

        Book b3 = new Book();
        b3.setAuthor("Alice");
        b3.setYear_published(2001);

        agg.accept(b1, "author");
        agg.accept(b2, "author");
        agg.accept(b3, "author");

        Map<String, Long> authorMap = agg.snapshotSortedDesc();
        assertEquals(2L, authorMap.get("Alice"));
        assertEquals(1L, authorMap.get("Bob"));

        // Проверка по году
        StatisticsAggregator aggYear = new StatisticsAggregator();
        aggYear.accept(b1, "year_published");
        aggYear.accept(b2, "year_published");
        aggYear.accept(b3, "year_published");

        Map<String, Long> yearMap = aggYear.snapshotSortedDesc();
        assertEquals(2L, yearMap.get("2000"));
        assertEquals(1L, yearMap.get("2001"));
    }
}