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

// import javax.persistence.Entity;
// import javax.persistence.GeneratedValue;
// import javax.persistence.Id;
// import javax.persistence.Table;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.io.PipedInputStream;
import java.io.PipedOutputStream;
import java.util.Date;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.zip.ZipEntry;
import java.util.zip.ZipInputStream;

import javax.imageio.ImageIO;

import com.example.honmon.services.ZipService;
import com.example.honmon.storage.StorageService;
import com.example.honmon.storage.StoredFile;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

import org.apache.commons.io.FilenameUtils;
import org.apache.commons.io.IOUtils;
import org.apache.pdfbox.pdmodel.PDDocument;
import org.apache.pdfbox.rendering.PDFRenderer;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.mongodb.core.mapping.DBRef;
import org.springframework.http.MediaType;
import org.springframework.web.multipart.MultipartFile;


// @Entity
// @Table(name = "books")
// @Getter @Setter @NoArgsConstructor
public class Book { // extends AbstractEntity {

    private String id;
    private String title;
    private String author;
    private String category;
    private String collection;
    private List<String> tags;
    private String description;
    private String fileType;
    private String fileExtension;
    private Date lastAccess;
    private String progress;
    private String totalPages;
    @DBRef() StoredFile book;
    private String bookId;
    @DBRef() StoredFile thumbnail;
    private String thumbnailId;

    private StorageService<StoredFile> storageService;

    @Autowired
    public void setStorageService(StorageService<StoredFile> storageService) {
        this.storageService = storageService;
    }

    public String getCollection() {
        return collection;
    }

    public void setCollection(String collection) {
        this.collection = collection;
    }

    public String getTotalPages() {
        return totalPages;
    }

    public void setTotalPages(String totalPages) {
        this.totalPages = totalPages;
    }

    public String getProgress() {
        return progress;
    }

    public void setProgress(String progress) {
        this.progress = progress;
    }

    public Date getLastAccess() {
        return lastAccess;
    }

    public void setLastAccess(Date lastAccess) {
        this.lastAccess = lastAccess;
    }

    public String getFileExtension() {
        return fileExtension;
    }

    public void setFileExtension(String fileExtension) {
        this.fileExtension = fileExtension;
    }

    public String getFileType() {
        return fileType;
    }

    public void setFileType(String fileType) {
        this.fileType = fileType;
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
        if (this.book != null) {
            this.bookId = this.book.getId().toString();
        }
        
        return this.bookId;
    }

    public String getThumbnailId() {
        if (this.thumbnail != null && this.thumbnail.getId() != null) {
            this.thumbnailId = this.thumbnail.getId().toString();
            return this.thumbnailId;
        } else {
            return this.thumbnailId;
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

    public StoredFile storeThumbnail(MultipartFile file, MediaType mediaType ) throws IOException {
        String fileRef = null;
        if (file == null) {
            // extract image from pdf document
            if (mediaType.equals(MediaType.APPLICATION_PDF)) {
                InputStream documentStream = new ByteArrayInputStream(this.getBook().getFile());
                PDDocument pdfDoc = PDDocument.load(documentStream);
                PDFRenderer pdfRenderer = new PDFRenderer(pdfDoc);
                BufferedImage img = pdfRenderer.renderImage(0);
                ByteArrayOutputStream os = new ByteArrayOutputStream();
                ImageIO.write(img, "jpeg", os);
                InputStream is = new ByteArrayInputStream(os.toByteArray());
                fileRef = storageService.storeBytes(is, (long)is.available(), getTitle()+"_thumbnail", "image/jpeg");
                pdfDoc.close();
            }

            if (mediaType.equals(MediaType.APPLICATION_OCTET_STREAM))
            {
                InputStream documentStream = new ByteArrayInputStream(this.getBook().getFile());
                ZipInputStream zis = ZipService.unzipRef(documentStream);
                ZipEntry zipEntry = zis.getNextEntry();
                Pattern p = Pattern.compile("[_\\s\\-][0]*1\\.");
                Matcher m;
                while (zipEntry != null) {
                    m = p.matcher(zipEntry.getName());
                    if(m.find())
                    {
                        String ext = FilenameUtils.getExtension(zipEntry.getName());
                        String contentType;
                        ByteArrayOutputStream oStream =  new ByteArrayOutputStream();
                        byte[] buffer = new byte[256];
                        int len;
                        while ((len = zis.read(buffer)) > 0) {
                            oStream.write(buffer, 0, len);
                        }
                
                        if (ext.equals("jpg") || ext.equals("jpeg")) {
                            contentType = MediaType.IMAGE_JPEG_VALUE;
                            fileRef = storageService.storeBytes(
                                new ByteArrayInputStream(oStream.toByteArray()),
                                zipEntry.getSize(),
                                zipEntry.getName(),
                                contentType
                            );
                        }

                        if (ext.equals("png")) {

                        }
                        break;
                    }
                    zipEntry = zis.getNextEntry();
                 }
                
                

            }
            
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
