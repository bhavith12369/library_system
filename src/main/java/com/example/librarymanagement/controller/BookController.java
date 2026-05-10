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

    @GetMapping("/admin/login")
    public String adminLogin() {
        return "admin-login";
    }

    @GetMapping("/signup")
    public String signupForm(Model model) {
        model.addAttribute("user", new User());
        return "signup";
    }

    @PostMapping("/signup")
    public String signup(@ModelAttribute User user) {
        user.setRole(User.Role.STUDENT); // Public signup is always for students
        bookService.saveUser(user);
        return "redirect:/login?signupSuccess";
    }

    @GetMapping("/dashboard")
    public String dashboard(java.security.Principal principal, Model model) {
        String username = principal.getName();
        User user = bookService.getUserByUsername(username);
        
        if (user != null && user.getRole() == User.Role.ADMIN) {
            return "admin-dashboard";
        }
        return "student-dashboard";
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
    public String listIssues(java.security.Principal principal, Model model) {
        String username = principal.getName();
        User user = bookService.getUserByUsername(username);
        
        List<BookIssue> issues;
        if (user != null && user.getRole() == User.Role.ADMIN) {
            issues = bookService.getAllIssues();
        } else {
            // Filter issues for student
            issues = bookService.getAllIssues().stream()
                    .filter(i -> i.getUser() != null && i.getUser().getName().equals(username))
                    .toList();
        }
        model.addAttribute("issues", issues);
        return "issues";
    }

    @GetMapping("/issues/new")
    public String newIssueForm(@RequestParam(required = false) String bookId, Model model) {
        model.addAttribute("books", bookService.getAllBooks().stream().filter(Book::isAvailable).toList());
        model.addAttribute("users", bookService.getAllUsers().stream().filter(u -> u.getRole() == User.Role.STUDENT).toList());
        model.addAttribute("selectedBookId", bookId);
        return "issue-form";
    }

    @PostMapping("/issues")
    public String issueBook(@RequestParam String bookId, @RequestParam String userId) {
        bookService.issueBook(bookId, userId);
        return "redirect:/issues";
    }

    @PostMapping("/issues/request")
    public String requestBook(@RequestParam String bookId, java.security.Principal principal) {
        User user = bookService.getUserByUsername(principal.getName());
        bookService.requestBook(bookId, user.getId());
        return "redirect:/issues?requested";
    }

    @PostMapping("/admin/issues/{id}/approve")
    public String approveIssue(@PathVariable String id) {
        bookService.approveIssue(id);
        return "redirect:/issues";
    }

    @PostMapping("/admin/issues/{id}/reject")
    public String rejectIssue(@PathVariable String id) {
        bookService.rejectIssue(id);
        return "redirect:/issues";
    }

    @PostMapping("/issues/{id}/return")
    public String returnBook(@PathVariable String id) {
        bookService.returnBook(id);
        return "redirect:/issues";
    }
}