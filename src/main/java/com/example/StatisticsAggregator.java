package com.example;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.atomic.LongAdder;
import java.util.List;
import java.util.Arrays;
import java.util.stream.Collectors;

public class StatisticsAggregator {
    private final Map<String, LongAdder> counts = new ConcurrentHashMap<>();

    public void accept(Book book, String attribute) {
        if ("author".equalsIgnoreCase(attribute)) {
            String key = normalize(book.getAuthor());
            if (key != null) increment(key);
        } else if ("year_published".equalsIgnoreCase(attribute)) {
            Integer y = book.getYear_published();
            if (y != null) increment(String.valueOf(y));
        } else if ("genre".equalsIgnoreCase(attribute)) {
            String g = book.getGenre();
            if (g != null) {
                List<String> parts = Arrays.stream(g.split(","))
                    .map(String::trim)
                    .filter(s -> !s.isEmpty())
                    .map(this::normalize)
                    .collect(Collectors.toList());
                for (String p : parts) increment(p);
            }
        } else {
            // generic: try reflectively (not required)
        }
    }

    private void increment(String key) {
        counts.computeIfAbsent(key, k -> new LongAdder()).increment();
    }

    private String normalize(String s) {
        if (s == null) return null;
        return s.trim();
    }

    public Map<String, Long> snapshotSortedDesc() {
        return counts.entrySet().stream()
            .collect(Collectors.toMap(
                Map.Entry::getKey,
                e -> e.getValue().longValue()
            ))
            .entrySet().stream()
            .sorted((a,b) -> Long.compare(b.getValue(), a.getValue()))
            .collect(Collectors.toMap(
                Map.Entry::getKey,
                Map.Entry::getValue,
                (x,y) -> x,
                java.util.LinkedHashMap::new
            ));
    }
}