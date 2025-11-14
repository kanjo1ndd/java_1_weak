package com.example;
public class Book {
    private String title;
    private String author;
    private Integer year_published;
    private String genre;

    public Book() {}

    public String getTitle() { return title; }
    public void setTitle(String title) { this.title = title; }

    public String getAuthor() { return author; }
    public void setAuthor(String author) { this.author = author; }

    public Integer getYear_published() { return year_published; }
    public void setYear_published(Integer year_published) { this.year_published = year_published; }

    public String getGenre() { return genre; }
    public void setGenre(String genre) { this.genre = genre; }
}