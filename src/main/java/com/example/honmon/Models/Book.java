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

import java.awt.image.BufferedImage;
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.File;

// import javax.persistence.Entity;
// import javax.persistence.GeneratedValue;
// import javax.persistence.Id;
// import javax.persistence.Table;

import java.io.IOException;
import java.io.InputStream;
import java.util.List;

import javax.imageio.ImageIO;

import com.example.honmon.storage.MongoStorageService;
import com.example.honmon.storage.StorageService;
import com.example.honmon.storage.StoredFile;

import org.apache.pdfbox.pdmodel.PDDocument;
import org.apache.pdfbox.rendering.PDFRenderer;
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
    private String category;
    private List<String> tags;
    private String description;
    @DBRef() StoredFile book;
    private String bookId;
    @DBRef() StoredFile thumbnail;
    private String thumbnailId;

    private StorageService<StoredFile> storageService;

    @Autowired
    public void setStorageService(StorageService<StoredFile> storageService) {
        this.storageService = storageService;
    }

    public List<String> getTags() {
        return tags;
    }

    public void setTags(List<String> tags) {
        this.tags = tags;
    }

    public String getCategory() {
        return category;
    }

    public void setCategory(String category) {
        this.category = category;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public String getBookId() {
        return this.book.getId().toString();
    }

    public String getThumbnailId() {
        if (this.thumbnail != null) {
            return this.thumbnail.getId().toString();
        } else {
            return null;
        }
        
    }

    public Book() {
    }

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
        String fileRef = null;
        if (file == null) {
            // extract image from pdf document
            InputStream documentStream = new ByteArrayInputStream(this.getBook().getFile());
            PDDocument pdfDoc = PDDocument.load(documentStream);
            PDFRenderer pdfRenderer = new PDFRenderer(pdfDoc);
            BufferedImage img = pdfRenderer.renderImage(0);
            ByteArrayOutputStream os = new ByteArrayOutputStream();
            ImageIO.write(img, "jpeg", os);
            InputStream is = new ByteArrayInputStream(os.toByteArray());
            fileRef = storageService.storeBytes(is, getTitle()+"_thumbnail", "image/jpeg");
            pdfDoc.close();
            
            
        } else if(file != null) {
            fileRef = storageService.store(file);    
        }
        

        if (fileRef != null) {
            StoredFile uploadedThumbnail = storageService.load(fileRef);
            this.thumbnail = uploadedThumbnail;
            return uploadedThumbnail;
        }
        return null;

		
    }
    
}
