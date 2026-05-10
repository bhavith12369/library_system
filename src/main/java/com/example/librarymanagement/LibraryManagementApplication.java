package com.example.librarymanagement;

import com.example.librarymanagement.entity.Book;
import com.example.librarymanagement.entity.User;
import com.example.librarymanagement.service.BookService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Bean;

@SpringBootApplication
public class LibraryManagementApplication {

    @Autowired
    private BookService bookService;

    public static void main(String[] args) {
        SpringApplication.run(LibraryManagementApplication.class, args);
    }

    @Bean
    public CommandLineRunner initData() {
        return args -> {
            // Add some sample users
            if (bookService.getAllUsers().isEmpty()) {
                bookService.saveUser(new User("student", "student@example.com", "student", User.Role.STUDENT));
                bookService.saveUser(new User("admin", "admin@example.com", "admin", User.Role.ADMIN));
            }
            // Add some sample books
            if (bookService.getAllBooks().isEmpty()) {
                bookService.saveBook(new Book("The Great Gatsby", "F. Scott Fitzgerald", "978-0-7432-7356-5"));
                bookService.saveBook(new Book("To Kill a Mockingbird", "Harper Lee", "978-0-06-112008-4"));
                bookService.saveBook(new Book("1984", "George Orwell", "978-0-452-28423-4"));
            }
        };
    }
}