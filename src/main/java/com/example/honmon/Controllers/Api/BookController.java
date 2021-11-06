package com.example.honmon.Controllers.Api;

import java.util.List;

import com.example.honmon.Models.Book;
import com.example.honmon.Repo.BookRepository;

import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("api/books")
@CrossOrigin
public class BookController {


    private final BookRepository bookRepository;

    BookController(BookRepository bookRepository)
    {
        this.bookRepository = bookRepository;
    }

    @GetMapping()
    public List<Book> all()
    {
        return (List<Book>) bookRepository.findAll();
    }

    @PostMapping()
    Book newBook(@RequestBody Book newBook) {
        return bookRepository.save(newBook);
    }
}
