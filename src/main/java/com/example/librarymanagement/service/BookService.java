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

    @Autowired
    private org.springframework.security.crypto.password.PasswordEncoder passwordEncoder;

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

    public BookIssue requestBook(String bookId, String userId) {
        Optional<Book> bookOpt = bookRepository.findById(bookId);
        Optional<User> userOpt = userRepository.findById(userId);
        if (bookOpt.isPresent() && userOpt.isPresent() && bookOpt.get().isAvailable()) {
            BookIssue request = new BookIssue(bookOpt.get(), userOpt.get(), LocalDate.now(), LocalDate.now().plusDays(14));
            request.setStatus(BookIssue.Status.PENDING);
            return bookIssueRepository.save(request);
        }
        return null;
    }

    public BookIssue approveIssue(String issueId) {
        Optional<BookIssue> issueOpt = bookIssueRepository.findById(issueId);
        if (issueOpt.isPresent()) {
            BookIssue issue = issueOpt.get();
            if (issue.getStatus() == BookIssue.Status.PENDING) {
                issue.setStatus(BookIssue.Status.APPROVED);
                issue.getBook().setAvailable(false);
                bookRepository.save(issue.getBook());
                return bookIssueRepository.save(issue);
            }
        }
        return null;
    }

    public BookIssue rejectIssue(String issueId) {
        Optional<BookIssue> issueOpt = bookIssueRepository.findById(issueId);
        if (issueOpt.isPresent()) {
            BookIssue issue = issueOpt.get();
            if (issue.getStatus() == BookIssue.Status.PENDING) {
                issue.setStatus(BookIssue.Status.REJECTED);
                return bookIssueRepository.save(issue);
            }
        }
        return null;
    }

    public BookIssue issueBook(String bookId, String userId) {
        // Direct issuance by Admin
        BookIssue issue = requestBook(bookId, userId);
        if (issue != null) {
            return approveIssue(issue.getId());
        }
        return null;
    }

    public BookIssue returnBook(String issueId) {
        Optional<BookIssue> issueOpt = bookIssueRepository.findById(issueId);
        if (issueOpt.isPresent()) {
            BookIssue issue = issueOpt.get();
            issue.setReturnDate(LocalDate.now());
            issue.setStatus(BookIssue.Status.RETURNED);
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

    public User getUserByUsername(String username) {
        return userRepository.findByNameOrEmail(username, username).orElse(null);
    }

    public User saveUser(User user) {
        if (user.getPassword() != null && !user.getPassword().startsWith("$2a$")) {
            user.setPassword(passwordEncoder.encode(user.getPassword()));
        }
        return userRepository.save(user);
    }
}