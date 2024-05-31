package com.LiterAluraChallenge.REPOSITORIO;

import com.LiterAluraChallenge.MODELO.Author;
import com.LiterAluraChallenge.MODELO.Book;
import com.LiterAluraChallenge.MODELO.Language;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.util.List;
import java.util.Optional;

public interface IAuthorRepository extends JpaRepository<Author, Long> {
    @Query("SELECT a FROM Book b JOIN b.author a WHERE a.name LIKE %:name%")
    Optional<Author> searchAuthorByName(String name);

    @Query("SELECT b FROM Book b JOIN b.author a WHERE b.title LIKE %:name%")
    Optional<Book> searchBookByName(String name);

    @Query("SELECT b FROM Author a JOIN a.books b")
    List<Book> searchAllBooks();

    @Query("SELECT a FROM Author a WHERE a.deathYear > :date")
    List<Author> searchAuthorsAlive(Integer date);

    @Query("SELECT b FROM Author a JOIN a.books b WHERE b.languages = :lang ")
    List<Book> searchBooksByLanguage(Language lang);

    @Query("SELECT b FROM Author a JOIN a.books b ORDER BY b.download DESC LIMIT 10")
    List<Book> top10Books();

    @Query("SELECT a FROM Author a WHERE a.birthYear = :date")
    List<Author> ListAuthorsByBirth(Integer date);

    @Query("SELECT a FROM Author a WHERE a.deathYear = :date")
    List<Author> ListAuthorsByDecease(Integer date);
}
