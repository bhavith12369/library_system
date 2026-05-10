package com.example.librarymanagement.service;

import com.example.librarymanagement.entity.Book;
import com.example.librarymanagement.entity.BookIssue;
import com.example.librarymanagement.entity.User;
import com.example.librarymanagement.repository.BookIssueRepository;
import com.example.librarymanagement.repository.BookRepository;
import com.example.librarymanagement.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.time.temporal.ChronoUnit;
import java.util.List;
import java.util.Optional;

@Service
public class BookService {

    @Autowired
    private BookRepository bookRepository;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private BookIssueRepository bookIssueRepository;

    public List<Book> getAllBooks() {
        return bookRepository.findAll();
    }

    public Optional<Book> getBookById(String id) {
        return bookRepository.findById(id);
    }

    public Book saveBook(Book book) {
        return bookRepository.save(book);
    }

    public void deleteBook(String id) {
        bookRepository.deleteById(id);
    }

    public List<Book> searchBooks(String query) {
        // Simple search by title or author
        return bookRepository.findAll().stream()
                .filter(book -> book.getTitle().toLowerCase().contains(query.toLowerCase()) ||
                        book.getAuthor().toLowerCase().contains(query.toLowerCase()))
                .toList();
    }

    public BookIssue issueBook(String bookId, String username) {
        Optional<Book> bookOpt = bookRepository.findById(bookId);
        Optional<User> userOpt = userRepository.findByName(username);
        if (bookOpt.isPresent() && userOpt.isPresent() && bookOpt.get().isAvailable()) {
            Book book = bookOpt.get();
            book.setAvailable(false);
            bookRepository.save(book);
            LocalDate issueDate = LocalDate.now();
            LocalDate dueDate = issueDate.plusDays(14); // 2 weeks
            BookIssue issue = new BookIssue(book, userOpt.get(), issueDate, dueDate);
            return bookIssueRepository.save(issue);
        }
        return null;
    }

    public BookIssue returnBook(String issueId) {
        Optional<BookIssue> issueOpt = bookIssueRepository.findById(issueId);
        if (issueOpt.isPresent()) {
            BookIssue issue = issueOpt.get();
            issue.setReturnDate(LocalDate.now());
            double fine = calculateFine(issue);
            issue.setFine(fine);
            issue.getBook().setAvailable(true);
            bookRepository.save(issue.getBook());
            return bookIssueRepository.save(issue);
        }
        return null;
    }

    private double calculateFine(BookIssue issue) {
        if (issue.getReturnDate() != null && issue.getReturnDate().isAfter(issue.getDueDate())) {
            long daysOverdue = ChronoUnit.DAYS.between(issue.getDueDate(), issue.getReturnDate());
            return daysOverdue * 0.5; // $0.50 per day
        }
        return 0.0;
    }

    public List<BookIssue> getAllIssues() {
        return bookIssueRepository.findAll();
    }

    public List<User> getAllUsers() {
        return userRepository.findAll();
    }

    public User saveUser(User user) {
        // Encode password if not encoded
        if (user.getPassword() != null && !user.getPassword().startsWith("$2a$")) {
            // Simple encoding, in real app use BCrypt
            user.setPassword("{noop}" + user.getPassword()); // For demo, use noop
        }
        return userRepository.save(user);
    }
}