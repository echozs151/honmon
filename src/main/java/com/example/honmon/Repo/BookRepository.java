package com.example.honmon.Repo;

import java.util.List;

import com.example.honmon.Models.Book;

import org.springframework.data.mongodb.repository.MongoRepository;
// import org.springframework.data.repository.CrudRepository;

// public interface BookRepository extends CrudRepository<Book, Long> {
// }


public interface BookRepository extends MongoRepository<Book, Long> {
    Book findById(String id);
}