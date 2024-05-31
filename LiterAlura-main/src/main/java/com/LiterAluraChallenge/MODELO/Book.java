package com.LiterAluraChallenge.MODELO;

import jakarta.persistence.*;

import java.util.stream.Collectors;

@Entity
@Table(name = "books")
public class Book {
    @Id
    private Long id;
    private String title;
    @Enumerated(EnumType.STRING)
    private Language language;
    private Integer downloads;
    @ManyToOne
    private Author author;

    public Book() {
    }

    public Book(DataBook book){
        this.id = book.id();
        this.title = book.title();
        this.language = Language.fromString(book.languages()
                                                .stream()
                                                .limit(1)
                                                .collect(Collectors.joining())
                                            );
        this.downloads = book.download();
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public Language getLanguage() {
        return language;
    }

    public void setLanguage(Language language) {
        this.language = language;
    }

    public Integer getDownloads() {
        return downloads;
    }

    public void setDownloads(Integer downloads) {
        this.downloads = downloads;
    }

    public Author getAuthor() {
        return author;
    }

    public void setAuthor(Author author) {
        this.author = author;
    }

    @Override
    public String toString() {
        return "Book{" +
                "title='" + title + '\'' +
                ", language=" + language +
                ", downloads=" + downloads +
                ", author=" + author +
                '}';
    }
}
