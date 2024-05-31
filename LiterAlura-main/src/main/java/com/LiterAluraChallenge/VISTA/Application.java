package com.LiterAluraChallenge.VISTA;
import com.LiterAluraChallenge.MODELO.*;
import com.LiterAluraChallenge.REPOSITORIO.IAuthorRepository;
import com.LiterAluraChallenge.SERVICIOS.ApiUsage;
import com.LiterAluraChallenge.SERVICIOS.DataConverter;

import java.util.IntSummaryStatistics;
import java.util.List;
import java.util.Optional;
import java.util.Scanner;
import java.util.stream.Collectors;

public class Application {
    private Scanner input = new Scanner(System.in);
    private ApiUsage apiUsage = new ApiUsage();
    private DataConverter converter = new DataConverter();
    private final String URL= "https://gutendex.com/books/";
    private IAuthorRepository repository;

    public Application(IAuthorRepository repository) {
        this.repository = repository;
    }

    public void showMenu(){
        Integer option = -1;
        String menu = """
               
                       Welcome to LiterAlura          
                
                 1 - Search book by title                    
                 2 - List registered books                   
                 3 - List registered authors                 
                 4 - List living authors for a given year    
                 5 - List books by language                  
                 6 - Generate statistics                     
                 7 - Top 10 books                            
                 8 - Search author by name                   
                 9 - List authors with other queries         
                 0 - Exit                                    
                -----------------------------------------------
                         Please, Select an option:                     
                -----------------------------------------------
                """;
        while (option != 0){
            System.out.println(menu);
            try {
                option = input.nextInt();
                switch (option){
                    case 1:
                        searchBookByTitle();
                        break;
                    case 2:
                        listRegisteredBooks();
                        break;
                    case 3:
                        listRegisteredAuthors();
                        break;
                    case 4:
                        listAliveAuthors();
                        break;
                    case 5:
                        listBooksByLanguage();
                        break;
                    case 6:
                        generateStats();
                        break;
                    case 7:
                        top10Books();
                        break;
                    case 8:
                        searchAuthorByName();
                        break;
                    case 9:
                        listAuthorsWithOtherReq();
                        break;
                    case 0:
                        System.out.println("Thank you for using LiterAlura, come back soon....");
                        break;
                    default:
                        System.out.println("Invalid option!");
                        break;
                }
            }catch (NumberFormatException e){
                System.out.println("Invalid option: "+e.getMessage());
            }
            }
        }

    public void searchBookByTitle(){
        System.out.println("Type the name of the book you wish to find: ");
        var name = input.nextLine();
        var json = apiUsage.getData(URL+"?search="+name.replace(" ","%20"));
        var data = converter.getData(json, Data.class);
        Optional<DataBook> bookSearched = data.books().stream().findFirst();
        if (bookSearched.isPresent()){
            System.out.println(
                    "\n-----------Book-------------"+
                            "\nTitle: "+bookSearched.get().title()+
                            "\nAuthor: "+bookSearched.get().authors().stream()
                            .map(a -> a.name()).limit(1).collect(Collectors.joining())+
                            "\nLang: "+bookSearched.get().languages().stream().collect(Collectors.joining())+
                            "\nTotal Downloads: "+bookSearched.get().download()+
                            "\n----------------------------\n"
            );
            try {
                List<Book> bookFound = bookSearched.
                        stream()
                        .map(a -> new Book(a)).
                        collect(Collectors.toList());
                Author authorApi = bookSearched.stream()
                        .flatMap(b -> b.authors().stream()
                                .map(a -> new Author(a)))
                        .collect(Collectors.toList()).stream().findFirst().get();
                Optional<Author> authorDB = repository.searchAuthorByName(bookSearched.get().authors().stream()
                        .map(a -> a.name())
                        .collect(Collectors.joining()));
                Optional<Book> optionalBook = repository.searchBookByName(name);
                if (optionalBook.isPresent()){
                    System.out.println("The book is already stored in the database.");
                }else {
                    Author author;
                    if (authorDB.isPresent()){
                        author = authorDB.get();
                        System.out.println("The author is already saved in the database!");
                    }else {
                        author = authorApi;
                        repository.save(author);
                    }
                    author.setBooks(bookFound);
                    repository.save(author);
                }
            }catch (Exception e){
                System.out.println("Warning! "+e.getMessage());
            }
        }else {
            System.out.println("Book not found!");
        }
    }

    public void listRegisteredBooks(){
        List<Book> books = repository.searchAllBooks();
        books.forEach(b -> System.out.println(
                "----- Book -----" +
                        "\nTitle: " + b.getTitle() +
                        "\nAuthor: " + b.getAuthor().getName() +
                        "\nLang: " + b.getLanguage().getLang() +
                        "\nTotal Downloads: : " + b.getDownloads() +
                        "\n-----------------\n"
        ));
    }

    public void listRegisteredAuthors(){
        List<Author> authors = repository.findAll();
        authors.forEach(b -> System.out.println(
                "Autor: " + b.getName() +
                        "\nBirth year: " + b.getBirthYear() +
                        "\nYear of death: " + b.getDeathYear() +
                        "\nBooks: " + b.getBooks().stream()
                        .map(t -> t.getTitle()).collect(Collectors.toList()) + "\n"
        ));
    }

    public void listAliveAuthors(){
        System.out.println("Enter the living year of the author(s) you wish to search for:");
        try{
            var date = Integer.valueOf(input.nextLine());
            List<Author> authors = repository.searchAuthorsAlive(date);
            if(!authors.isEmpty()){
                System.out.println();
                authors.forEach(a -> System.out.println(
                        "Author: " + a.getName() +
                        "\nDate of birth: " + a.getBirthYear() +
                        "\nDate of death: " + a.getDeathYear() +
                        "\nBooks: " + a.getBooks().stream()
                        .map(b -> b.getTitle()).collect(Collectors.toList()) + "\n"
                ));
            } else{
                System.out.println("There are no living authors in that year registered in the DB!");
            }
        } catch(NumberFormatException e){
            System.out.println("enter a valid year " + e.getMessage());
        }
    }
    public void listBooksByLanguage(){
        String menu = """
                Enter the language to search for books:
                es - Spanish
                en - English
                fr - French
                pt - Portuguese
                """;
        System.out.println(menu);
        var lang = input.nextLine();
        if(lang.equalsIgnoreCase("es") || lang.equalsIgnoreCase("en") ||
                lang.equalsIgnoreCase("fr") || lang.equalsIgnoreCase("pt")){
            Language language = Language.fromString(lang);
            List<Book> books = repository.searchBooksByLanguage(language);
            if(books.isEmpty()){
                System.out.println("There are no books registered in that lang!");
            } else{
                System.out.println();
                books.forEach(b -> System.out.println(
                        "----- Book -----" +
                                "\nTitle: " + b.getTitle() +
                                "\nAuthor: " + b.getAuthor().getName() +
                                "\nlang: " + b.getLanguage().getLang() +
                                "\nTotal Downloads: : " + b.getDownloads() +
                                "\n-----------------\n"
                ));
            }
        } else{
            System.out.println("Enter a lang in the valid format");
        }
    }

    public void generateStats(){
        var json = apiUsage.getData(URL);
        var data = converter.getData(json,Data.class);
        IntSummaryStatistics stats = data.books().stream()
                .filter(b->b.download() >0)
                .collect(Collectors.summarizingInt(DataBook::download));
        Integer average = (int) stats.getAverage();
        System.out.println("\n----- Stats -----");
        System.out.println("Average number of downloads: " + average);
        System.out.println("Maximum number of downloads: " + stats.getMax());
        System.out.println("Minimum number of downloads: " + stats.getMin());
        System.out.println("Number of records evaluated to calculate the statistics: " + stats.getCount());
        System.out.println("-----------------\n");
    }

    public void top10Books(){
        List<Book> books = repository.top10Books();
        books.forEach(b-> System.out.println(
                "----- Book -----" +
                        "\nTitle: " + b.getTitle() +
                        "\nAuthor: " + b.getAuthor().getName() +
                        "\nLang: " + b.getLanguage().getLang() +
                        "\nTotal Downloads: : " + b.getDownloads() +
                        "\n-----------------\n"
        ));
    }
    public void searchAuthorByName(){
        System.out.println("Enter the name of the author you wish to search for:");
        var name = input.nextLine();
        Optional<Author> author = repository.searchAuthorByName(name);
        if (author.isPresent()){
            System.out.println(
                    "\nAuthor: " + author.get().getName() +
                            "\nDate of birth: " + author.get().getBirthYear() +
                            "\nDate of death: " + author.get().getDeathYear() +
                            "\nBooks: " + author.get().getBooks().stream()
                            .map(b -> b.getTitle()).collect(Collectors.toList()) + "\n"
            );
        }else {
            System.out.println("The author does not exist in the database!");
        }
    }

    public void listAuthorsWithOtherReq(){
        String menu = """
                Enter the option you wish to list authors by
                1 - List author by year of birth
                2 - List author by year of death
                """;
        System.out.println(menu);
        try {
            var option = input.nextInt();
            switch (option){
                case 1:
                    ListAuthorsByBirth();
                    break;
                case 2:
                    ListAuthorsByDecease();
                    break;
                default:
                    System.out.println("Invalid option!");
                    break;
            }
        }catch (NumberFormatException e){
            System.out.println("Invalid option! " + e.getMessage());
        }
    }

    public void ListAuthorsByBirth(){
        System.out.println("Enter the year of birth you wish to search for:");
        try{
            var birth = input.nextInt();
            List<Author> authors = repository.ListAuthorsByBirth(birth);
            if(authors.isEmpty()){
                System.out.println("There are no authors with a year of birth equal to " + birth);
            } else {
                System.out.println();
                authors.forEach(a -> System.out.println(
                        "Author: " + a.getName() +
                                "\nBirth year: : " + a.getBirthYear() +
                                "\nDeath year: : " + a.getDeathYear() +
                                "\nBooks: " + a.getBooks().stream().map(b -> b.getTitle()).collect(Collectors.toList()) + "\n"
                ));
            }
        } catch (NumberFormatException e){
            System.out.println("Año no valido: " + e.getMessage());
        }
    }

    public void ListAuthorsByDecease(){
        System.out.println("Enter the year of death you wish to search for:");
        try{
            var decease = input.nextInt();
            List<Author> authors = repository.ListAuthorsByDecease(decease);
            if(authors.isEmpty()){
                System.out.println("There are no authors with year of death equal to " + decease);
            } else {
                System.out.println();
                authors.forEach(a -> System.out.println(
                        "Autor: " + a.getName() +
                                "\nDate of birth: : " + a.getBirthYear() +
                                "\nDate of death: : " + a.getDeathYear() +
                                "\nBooks: " + a.getBooks().stream().map(b -> b.getTitle()).collect(Collectors.toList()) + "\n"
                ));
            }
        } catch (NumberFormatException e) {
            System.out.println("Invalid option: " + e.getMessage());
        }
    }
}