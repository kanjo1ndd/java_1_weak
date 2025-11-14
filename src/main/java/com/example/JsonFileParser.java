package com.example;

import com.fasterxml.jackson.core.JsonFactory;
import com.fasterxml.jackson.core.JsonParser;
import com.fasterxml.jackson.core.JsonToken;

import java.io.File;
import java.io.FileInputStream;
import java.util.function.Consumer;

public class JsonFileParser {

    private final JsonFactory factory = new JsonFactory();
    
    public void parseFile(File file, Consumer<Book> consumer) throws Exception {

        try (FileInputStream fis = new FileInputStream(file);
             JsonParser parser = factory.createParser(fis)) {

            if (parser.nextToken() != JsonToken.START_ARRAY) {
                throw new IllegalStateException("Expected JSON array");
            }

            while (parser.nextToken() == JsonToken.START_OBJECT) {

                Book book = new Book();

                while (true) {
                    JsonToken t = parser.nextToken();
                    if (t == JsonToken.END_OBJECT) break;

                    String fieldName = parser.currentName();
                    parser.nextToken();

                    switch (fieldName) {
                        case "title":
                            book.setTitle(parser.getValueAsString());
                            break;
                        case "author":
                            book.setAuthor(parser.getValueAsString());
                            break;
                        case "year_published":
                            book.setYear_published(parser.getIntValue());
                            break;
                        case "genre":
                            book.setGenre(parser.getValueAsString());
                            break;
                        default:
                            parser.skipChildren();
                    }
                }

                consumer.accept(book);
            }
        }
    }
}