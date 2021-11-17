package com.example.honmon.Controllers;

import java.io.ByteArrayInputStream;
import java.io.IOException;

import javax.servlet.http.HttpServletResponse;

import com.example.honmon.Models.Book;
import com.example.honmon.Repo.BookRepository;
import com.example.honmon.storage.StorageService;
import com.example.honmon.storage.StoredFile;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.util.StreamUtils;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("resource")
public class ResourceServeController {

    @Autowired
    StorageService<StoredFile> storageService;

    @Autowired
    BookRepository bookRepository;

    @GetMapping("book-thumbnail/{id}")
    public void getImage(HttpServletResponse response, @PathVariable String id) throws IOException {
        Book book = bookRepository.findById(id);
        if (book.getThumbnail() != null) {
            var thumbnail = storageService.load(book.getThumbnail().getId().toString());
            if (thumbnail != null) {
                response.setContentType(thumbnail.getFileType());
                StreamUtils.copy(new ByteArrayInputStream(thumbnail.getFile()), response.getOutputStream());    
            }
        }
    }

    @GetMapping("book-preview/{id}")
    public void getBookPreview(HttpServletResponse response, @PathVariable String id) throws IOException {
        Book book = bookRepository.findById(id);
        var fileRef = storageService.load(book.getBook().getId().toString());
        response.setContentType(fileRef.getFileType());
        StreamUtils.copy(new ByteArrayInputStream(fileRef.getFile()), response.getOutputStream());
    }

    @GetMapping("cbz-meta/{id}")
    public Book getCbzFile(HttpServletResponse response, @PathVariable String id) throws IOException {
        Book book = bookRepository.findById(id);
        return book;
    }
}
