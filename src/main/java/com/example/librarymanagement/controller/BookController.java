package com.example.librarymanagement.controller;

import com.example.librarymanagement.entity.Book;
import com.example.librarymanagement.entity.BookIssue;
import com.example.librarymanagement.entity.User;
import com.example.librarymanagement.service.BookService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@Controller
public class BookController {

    @Autowired
    private BookService bookService;

    @GetMapping("/login")
    public String login() {
        return "login";
    }

    @GetMapping("/dashboard")
    public String dashboard(Model model) {
        // Add user specific data
        return "dashboard";
    }

    @GetMapping("/books")
    public String listBooks(Model model) {
        model.addAttribute("books", bookService.getAllBooks());
        return "books";
    }

    @GetMapping("/books/search")
    public String searchBooks(@RequestParam String query, Model model) {
        model.addAttribute("books", bookService.searchBooks(query));
        return "books";
    }

    @GetMapping("/books/new")
    public String newBookForm(Model model) {
        model.addAttribute("book", new Book());
        return "book-form";
    }

    @PostMapping("/books")
    public String saveBook(@ModelAttribute Book book) {
        bookService.saveBook(book);
        return "redirect:/books";
    }

    @GetMapping("/users")
    public String listUsers(Model model) {
        model.addAttribute("users", bookService.getAllUsers());
        return "users";
    }

    @GetMapping("/users/new")
    public String newUserForm(Model model) {
        model.addAttribute("user", new User());
        return "user-form";
    }

    @PostMapping("/users")
    public String saveUser(@ModelAttribute User user) {
        bookService.saveUser(user);
        return "redirect:/users";
    }

    @GetMapping("/issues")
    public String listIssues(Model model) {
        model.addAttribute("issues", bookService.getAllIssues());
        return "issues";
    }

    @GetMapping("/issues/new")
    public String newIssueForm(Model model) {
        model.addAttribute("books", bookService.getAllBooks().stream().filter(Book::isAvailable).toList());
        return "issue-form";
    }

    @PostMapping("/issues")
    public String issueBook(@RequestParam String bookId, java.security.Principal principal) {
        bookService.issueBook(bookId, principal.getName());
        return "redirect:/issues";
    }

    @PostMapping("/issues/{id}/return")
    public String returnBook(@PathVariable String id) {
        bookService.returnBook(id);
        return "redirect:/issues";
    }
}