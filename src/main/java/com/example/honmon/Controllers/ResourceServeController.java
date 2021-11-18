package com.example.honmon.Controllers;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.util.Base64;
import java.util.zip.ZipEntry;
import java.util.zip.ZipInputStream;

import javax.servlet.http.HttpServletResponse;

import com.example.honmon.Models.Book;
import com.example.honmon.Repo.BookRepository;
import com.example.honmon.services.ZipService;
import com.example.honmon.storage.StorageService;
import com.example.honmon.storage.StoredFile;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.core.io.ByteArrayResource;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
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

    @Cacheable("book-thumbnail")
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

    @Cacheable("cbz-meta")
    @GetMapping("cbz-meta/{id}")
    public Book getCbzFile(HttpServletResponse response, @PathVariable String id) throws IOException {
        Book book = bookRepository.findById(id);
        return book;
    }

    @Cacheable("cbz-image")
    @GetMapping("cbz-img/{id}/{filename}")
    public ResponseEntity<ByteArrayResource> getCbzFile(
        @PathVariable String id,
        @PathVariable String filename
    ) throws IOException {
        // final byte[] requestContent;
        // requestContent = IOUtils.toByteArray(request.getReader());
        Book book = bookRepository.findById(id);
        StoredFile file = storageService.load(book.getBook().getId().toString());
        ZipInputStream zis = ZipService.unzipRef(new ByteArrayInputStream(file.getFile()));
        // HashMap<String, String> list = new HashMap<String, String>();
        // List<String> fileList = new ArrayList<String>();
        String fileNameDcd = new String(Base64.getDecoder().decode(filename));
        
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

                return ResponseEntity.ok()
                        .contentType(MediaType.parseMediaType(MediaType.IMAGE_JPEG_VALUE ))
                        // .header(HttpHeaders.CONTENT_DISPOSITION, "attachment; filename=\"" + loadFile.getFilename() + "\"")
                        .body(new ByteArrayResource(oStream.toByteArray()));
            }
            
            entry = zis.getNextEntry();
        }

        return null;
    }
}
