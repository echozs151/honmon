package com.example.honmon.Models;

// import javax.persistence.Entity;
// import javax.persistence.GeneratedValue;
// import javax.persistence.Id;
// import javax.persistence.Table;

import lombok.NoArgsConstructor;
import lombok.Setter;

import java.io.IOException;

import com.example.honmon.storage.MongoStorageService;
import com.example.honmon.storage.StorageService;
import com.example.honmon.storage.StoredFile;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.DBRef;
import org.springframework.web.multipart.MultipartFile;

import lombok.Getter;

// @Entity
// @Table(name = "books")
// @Getter @Setter @NoArgsConstructor
public class Book { // extends AbstractEntity {

    private String id;
    private String title;
    private String author;
    @DBRef() StoredFile book;

    private StorageService<StoredFile> storageService;

    @Autowired
    public void setStorageService(StorageService<StoredFile> storageService) {
        this.storageService = storageService;
    }

    public Book() {}

    public Book(String title, String author) 
    {
        this.title = title;
        this.author = author;
    }

    public String getId()
    {
        return id;
    }

    public String getAuthor()
    {
        return author;
    }

    public void setAuthor(String author) 
    {
        this.author = author;
    }

    public String getTitle()
    {
        return title;
    }

    public void setTitle(String title) 
    {
        this.title = title;
    }

    public String toString()
    {
        return "Book(id="+getId()+", title="+title+", author="+author+")";
    }

    public void setBook(StoredFile book) {
        this.book = book;
    }

    public StoredFile getBook() {
        return book;
    }

    public StoredFile storeBook(MultipartFile file ) throws IOException {
        String fileRef = storageService.store(file);
		StoredFile uploadedBook = storageService.load(fileRef);
        this.book = uploadedBook;
        return uploadedBook;
    }
    
}
