/*  
Copyright 2021 the original author or authors.

This file is part of Honmon.

Honmon is free software: you can redistribute it and/or modify
it under the terms of the GNU General Public License as published by
the Free Software Foundation, either version 3 of the License, or
(at your option) any later version.

Honmon is distributed in the hope that it will be useful,
but WITHOUT ANY WARRANTY; without even the implied warranty of
MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
GNU General Public License for more details.

You should have received a copy of the GNU General Public License
along with Honmon.  If not, see <https://www.gnu.org/licenses/>.
*/
package com.example.honmon.Controllers.Api;

import java.io.File;
import java.io.IOException;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import com.example.honmon.Models.Book;
import com.example.honmon.Repo.BookRepository;
import com.example.honmon.storage.StorageService;
import com.example.honmon.storage.StoredFile;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonMappingException;
import com.fasterxml.jackson.databind.ObjectMapper;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.system.ApplicationTemp;
import org.springframework.core.io.ByteArrayResource;
import org.springframework.core.io.ClassPathResource;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

@RestController
@RequestMapping("api/books")
@CrossOrigin
public class BookController {


    private final BookRepository bookRepository;

    @Autowired
    StorageService<StoredFile> storageService;

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
    Book newBook(@RequestBody Book newBook ) {
        return bookRepository.save(newBook);
    }

    @PostMapping("new")
    Map<String, String> newBookWithFile(
        @RequestParam("model") String model,
        @RequestParam(value = "file", required = false) MultipartFile file,
        @RequestParam(value = "thumbnail", required = false) MultipartFile thumbnail
    ) throws IOException {
        ObjectMapper mapper = new ObjectMapper();
        Book newBook = mapper.readValue(model, Book.class);
        newBook.setStorageService(storageService);
        newBook.storeBook(file);
        newBook.storeThumbnail(thumbnail);
        bookRepository.save(newBook);
        return Map.of("status", "success");
    }

    @GetMapping("{id}")
    public Book getBook(@PathVariable String id) {
        return bookRepository.findById(id);
    }

    @GetMapping("download/{id}")
    public ResponseEntity<ByteArrayResource> downloadBook(@PathVariable String id) throws IOException {
        Book book = bookRepository.findById(id);
        StoredFile loadFile = storageService.load(book.getBook().getId().toString());
        return ResponseEntity.ok()
                .contentType(MediaType.parseMediaType(loadFile.getFileType() ))
                .header(HttpHeaders.CONTENT_DISPOSITION, "attachment; filename=\"" + loadFile.getFilename() + "\"")
                .body(new ByteArrayResource(loadFile.getFile()));
    }

    @GetMapping("thumbnail/{id}")
    public ResponseEntity<ByteArrayResource> thumbnail(@PathVariable String id) throws IOException {
        Book book = bookRepository.findById(id);
        ApplicationTemp tmp = new ApplicationTemp();
        File tmpDir = tmp.getDir();
        StoredFile loadFile = storageService.load(book.getThumbnail().getId().toString());

        var imgFile = new ClassPathResource("image/sid.jpg");
        ResponseEntity.ok().contentType(MediaType.parseMediaType(loadFile.getFileType()));

        return ResponseEntity.ok()
                .contentType(MediaType.parseMediaType(loadFile.getFileType() ))
                // .header(HttpHeaders.CONTENT_DISPOSITION, "attachment; filename=\"" + loadFile.getFilename() + "\"")
                .body(new ByteArrayResource(loadFile.getFile()));
    }
    
}
