package com.example.honmon.services;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.nio.file.FileSystem;
import java.nio.file.FileSystems;
import java.nio.file.FileVisitResult;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.SimpleFileVisitor;
import java.nio.file.attribute.BasicFileAttributes;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.stream.Stream;

import com.example.honmon.Models.Book;
import com.example.honmon.Repo.BookRepository;
import com.example.honmon.storage.StorageService;
import com.example.honmon.storage.StoredFile;

import org.apache.commons.io.DirectoryWalker;
import org.apache.commons.io.FileUtils;
import org.apache.commons.io.FilenameUtils;
import org.apache.commons.io.file.PathUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;


@Service
public class ImportService {

    private static final Logger log = LoggerFactory.getLogger(ImportService.class);

    @Autowired
    StorageService<StoredFile> storageService;

    BookRepository bookRepository;

    public ImportService(BookRepository bookRepository) {
        this.bookRepository = bookRepository;
    }

    public Set<String> importBooks(String path) throws IOException
    {
        
        String sysPath = FilenameUtils.separatorsToSystem(path);
        // Path pathC = FileSystems.getDefault().getPath(sysPath);
        // Stream<Path> is = PathUtils.walk(pathC, null, 0, false);


        Set<String> fileList = new HashSet<>();
        Files.walkFileTree(Paths.get(sysPath), new SimpleFileVisitor<Path>() {
            
            @Override
            public FileVisitResult visitFile(Path file, BasicFileAttributes attrs)
            throws IOException {
                if (!Files.isDirectory(file)) {
                    FileInputStream fis = new FileInputStream(file.toAbsolutePath().toFile());

                    int size = fis.available();
                    String title = FilenameUtils.removeExtension(file.getFileName().toString());
                    String extension = FilenameUtils.getExtension(file.getFileName().toString());
                    Book book = new Book();
                    book.setTitle(title);
                    book.setFileExtension(extension);;
                    String bookref = storageService.storeBytes(fis, size, title, StoredFile.findContentType(extension));
                    book.setBook(storageService.load(bookref));
                    
                    bookRepository.save(book);
                    fileList.add(file.getFileName().toString());
                    fis.close();
                }
                return FileVisitResult.CONTINUE;
            }
        });
        return fileList;


        // log.debug("Testing log " + sysPath);
        // log.debug("Res:  " + fileList);




        // return;

    }
}
