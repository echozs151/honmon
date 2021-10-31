package com.example.honmon.Repo;

import com.example.honmon.Models.Book;

import org.springframework.data.repository.CrudRepository;

public interface BookRepository extends CrudRepository<Book, Long> {
}
