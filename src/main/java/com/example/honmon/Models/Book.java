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
package com.example.honmon.Models;

// import javax.persistence.Entity;
// import javax.persistence.GeneratedValue;
// import javax.persistence.Id;
// import javax.persistence.Table;

import java.io.IOException;

import com.example.honmon.storage.StorageService;
import com.example.honmon.storage.StoredFile;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.mongodb.core.mapping.DBRef;
import org.springframework.web.multipart.MultipartFile;


// @Entity
// @Table(name = "books")
// @Getter @Setter @NoArgsConstructor
public class Book { // extends AbstractEntity {

    private String id;
    private String title;
    private String author;
    @DBRef() StoredFile book;
    @DBRef() StoredFile thumbnail;

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

    public void setThumbnail(StoredFile thumbnail) {
        this.thumbnail = thumbnail;
    }

    public StoredFile getThumbnail() {
        return thumbnail;
    }

    public StoredFile storeThumbnail(MultipartFile file ) throws IOException {
        String fileRef = storageService.store(file);
		StoredFile uploadedThumbnail = storageService.load(fileRef);
        this.thumbnail = uploadedThumbnail;
        return uploadedThumbnail;
    }
    
}
