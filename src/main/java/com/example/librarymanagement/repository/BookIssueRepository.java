package com.example.librarymanagement.repository;

import com.example.librarymanagement.entity.BookIssue;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface BookIssueRepository extends MongoRepository<BookIssue, String> {
}