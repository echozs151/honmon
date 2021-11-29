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

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Base64;
import java.util.Collections;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.zip.ZipEntry;
import java.util.zip.ZipInputStream;

import javax.servlet.http.HttpServletResponse;

import com.example.honmon.Models.Book;
import com.example.honmon.Repo.BookRepository;
import com.example.honmon.services.ZipService;
import com.example.honmon.storage.StorageService;
import com.example.honmon.storage.StoredFile;
import com.fasterxml.jackson.databind.ObjectMapper;

import org.apache.commons.io.FilenameUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.ByteArrayResource;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

import ch.qos.logback.classic.Logger;

@RestController
@RequestMapping("api/books")
@CrossOrigin
public class BookController {


    private final BookRepository bookRepository;

    @Autowired
    StorageService<StoredFile> storageService;

    @Autowired
    private MongoTemplate mongoTemplate;

    BookController(BookRepository bookRepository)
    {
        this.bookRepository = bookRepository;
    }

    @GetMapping()
    public List<Book> all(
        @RequestParam(required = false) String key,
        @RequestParam(required = false) String val
    )
    {
        if (key != null && key.equals("title")) {
            return bookRepository.findBooksByRegexpTitle(val);            
        }
        
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
        newBook.setFileType(file.getContentType());
        newBook.setFileExtension(FilenameUtils.getExtension(file.getOriginalFilename()));
        if (file.getContentType().equals(MediaType.APPLICATION_PDF_VALUE)) {
            newBook.storeThumbnail(thumbnail,  MediaType.APPLICATION_PDF);
        }

        if (file.getContentType().equals(MediaType.APPLICATION_OCTET_STREAM_VALUE)) {
            newBook.storeThumbnail(thumbnail, MediaType.APPLICATION_OCTET_STREAM);
        }
        
        bookRepository.save(newBook);
        return Map.of("status", "success");
    }

    @GetMapping("{id}")
    public Book getBook(@PathVariable String id) {
        Book book = bookRepository.findById(id);
        book.setLastAccess(new Date());
        bookRepository.save(book);
        // try {
            //book.setThumbnail(storageService.load(book.getThumbnailId()));
            //book.setBook(storageService.load(book.getBookId()));
        // } catch (IOException e) {
            // e.printStackTrace();
        // }
        return book;
    }

    @DeleteMapping("{id}")
    public void deleteBook(@PathVariable String id) throws IOException {
        Book book = bookRepository.findById(id);
        if (book.getBook() != null) {
            storageService.delete(book.getBook().getId().toString());
        }
        
        bookRepository.deleteById(id);
    }

    @PutMapping("{id}")
    public Book editBook(
        @PathVariable String id,
        @RequestParam("model") String model,
        @RequestParam(value = "file", required = false) MultipartFile file,
        @RequestParam(value = "thumbnail", required = false) MultipartFile thumbnail
    ) throws IOException {
        ObjectMapper mapper = new ObjectMapper();
        Book updateBook = mapper.readValue(model, Book.class);
        Book book = bookRepository.findById(id);
        
        
        // BeanUtils.copyProperties(updateBook, book);

        book.setAuthor(updateBook.getAuthor());
        book.setTitle(updateBook.getTitle());
        book.setCategory(updateBook.getCategory());
        book.setCollection(updateBook.getCollection());
        book.setDescription(updateBook.getDescription());
        book.setTags(updateBook.getTags());

        // book.setFileType();
        StoredFile tmpBook = storageService.load(book.getBookId());
        if (file == null) {
            book.setFileExtension(FilenameUtils.getExtension(tmpBook.getFilename()));
            book.setBook(tmpBook);
        }

        if (thumbnail == null && book.getThumbnailId() != null) {
            StoredFile tmpThumbnail = storageService.load(book.getThumbnailId());
            book.setThumbnail(tmpThumbnail);
        } else {
            if (FilenameUtils.getExtension(tmpBook.getFilename()).equals("pdf")) {
                book.storeThumbnail(thumbnail,  MediaType.APPLICATION_PDF);
            } else {
                book.storeThumbnail(thumbnail, MediaType.APPLICATION_OCTET_STREAM);
            }
            
        }
        
        
        bookRepository.save(book);
        return book;
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
        // ApplicationTemp tmp = new ApplicationTemp();
        // File tmpDir = tmp.getDir();
        StoredFile loadFile = storageService.load(book.getThumbnail().getId().toString());

        // var imgFile = new ClassPathResource("image/sid.jpg");
        ResponseEntity.ok().contentType(MediaType.parseMediaType(loadFile.getFileType()));

        return ResponseEntity.ok()
                .contentType(MediaType.parseMediaType(loadFile.getFileType() ))
                // .header(HttpHeaders.CONTENT_DISPOSITION, "attachment; filename=\"" + loadFile.getFilename() + "\"")
                .body(new ByteArrayResource(loadFile.getFile()));
    }

    Logger logger;

    @GetMapping("cbz-img/{id}/{filename}")
    public ResponseEntity<ByteArrayResource> getCbzFile(
        @PathVariable String id,
        @PathVariable String filename,
        @RequestParam(required = false) String progress
    ) throws IOException {
        Book book = bookRepository.findById(id);
        StoredFile file = storageService.load(book.getBook().getId().toString());
        ZipInputStream zis = ZipService.unzipRef(new ByteArrayInputStream(file.getFile()));
        // HashMap<String, String> list = new HashMap<String, String>();
        // List<String> fileList = new ArrayList<String>();
        String fileNameDcd = new String(Base64.getDecoder().decode(filename));
        
        
        logger.info("Test");
        ZipEntry entry = zis.getNextEntry();
        // return "name.toString()";
        while(entry != null) {
            if (entry.getName().equals(fileNameDcd)) {
                
                ByteArrayOutputStream oStream =  new ByteArrayOutputStream();
                byte[] buffer = new byte[256];
                int len;
                while ((len = zis.read(buffer)) > 0) {
                    oStream.write(buffer, 0, len);
                }
                ResponseEntity.ok().contentType(MediaType.parseMediaType(MediaType.IMAGE_JPEG_VALUE));
                oStream.close();
                zis.close();
                zis.closeEntry();
                if (progress != null) {
                    book.setProgress(progress);
                    bookRepository.save(book);
                }
                return ResponseEntity.ok()
                        .contentType(MediaType.parseMediaType(MediaType.IMAGE_JPEG_VALUE ))
                        // .header(HttpHeaders.CONTENT_DISPOSITION, "attachment; filename=\"" + loadFile.getFilename() + "\"")
                        .body(new ByteArrayResource(oStream.toByteArray()));
            }
            
            entry = zis.getNextEntry();
        }
        zis.close();
        zis.closeEntry();
        return null;
    }

    @GetMapping("cbz-meta/{id}")
    public List<String> getCbzFile(HttpServletResponse response, @PathVariable String id) throws IOException {
        Book book = bookRepository.findById(id);
        StoredFile file = storageService.load(book.getBook().getId().toString());
        ZipInputStream zis = ZipService.unzipRef(new ByteArrayInputStream(file.getFile()));
        HashMap<String, String> list = new HashMap<String, String>();
        List<String> fileList = new ArrayList<String>();
        ZipEntry entry = zis.getNextEntry();
        while(entry != null) {
            fileList.add(entry.getName());
            
            entry = zis.getNextEntry();
        }
        zis.closeEntry();
        zis.close();
        Collections.sort(fileList);
        return fileList;
    }
    
}
