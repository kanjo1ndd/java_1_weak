package com.example;

import org.junit.jupiter.api.Test;

import java.nio.file.Files;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

public class JsonFileParserTest {

    @Test
    public void parsesArrayOfBooks() throws Exception {
        String json = """
                [
                  {"title":"A","author":"X","year_published":2000,"genre":"G1"},
                  {"title":"B","author":"Y","year_published":2001,"genre":"G2,G3"}
                ]
                """;

        Path tmp = Files.createTempFile("books", ".json");
        Files.writeString(tmp, json);

        JsonFileParser parser = new JsonFileParser();
        List<Book> consumed = new ArrayList<>();
        parser.parseFile(tmp.toFile(), consumed::add);

        assertEquals(2, consumed.size());

        Book first = consumed.get(0);
        assertEquals("A", first.getTitle());
        assertEquals("X", first.getAuthor());
        assertEquals(2000, first.getYear_published());
        assertEquals("G1", first.getGenre());

        Book second = consumed.get(1);
        assertEquals("B", second.getTitle());
        assertEquals("Y", second.getAuthor());
        assertEquals(2001, second.getYear_published());
        assertEquals("G2,G3", second.getGenre());
    }
}