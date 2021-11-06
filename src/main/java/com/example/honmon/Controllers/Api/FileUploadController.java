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

import java.io.IOException;
import java.io.InputStream;
import java.util.stream.Collectors;

import com.example.honmon.Models.Book;
import com.example.honmon.Repo.BookRepository;
import com.example.honmon.storage.StoredFile;
import com.example.honmon.storage.StorageFileNotFoundException;
import com.example.honmon.storage.StorageService;

import org.apache.commons.io.IOUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.Resource;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.servlet.mvc.method.annotation.MvcUriComponentsBuilder;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;


@RestController
@RequestMapping("api/upload")
public class FileUploadController {

	private final StorageService<StoredFile> storageService;
	private static final Logger log = LoggerFactory.getLogger(FileUploadController.class);


	@Autowired
	private BookRepository bookRepository;

	@Autowired
	public FileUploadController(StorageService<StoredFile> storageService) {
		this.storageService = storageService;
	}

	@GetMapping()
	public String listUploadedFiles(Model model) throws IOException {

		// model.addAttribute("files", storageService.loadAll().map(
		// 		path -> MvcUriComponentsBuilder.fromMethodName(FileUploadController.class,
		// 				"serveFile", path.getFileName().toString()).build().toUri().toString()+"/preview")
		// 		.collect(Collectors.toList()));

		return "uploadForm";
	}

	@GetMapping("/files/{filename:.+}")
	@ResponseBody()
	public ResponseEntity<Resource> serveFile(@PathVariable String filename) {

		Resource file = storageService.loadAsResource(filename);
		return ResponseEntity.ok().header(HttpHeaders.CONTENT_DISPOSITION,
				"attachment; filename=\"" + file.getFilename() + "\"").body(file);
	}

	@GetMapping(value = "/files/{filename:.+}/preview", produces = MediaType.IMAGE_JPEG_VALUE)
	public @ResponseBody byte[] getImage(@PathVariable String filename) throws IOException {
		Resource file = storageService.loadAsResource(filename);
		
		// InputStream in = getClass()
		// 	.getResourceAsStream("/com/baeldung/produceimage/image.jpg");
		return IOUtils.toByteArray(file.getInputStream());
	}

	@PostMapping()
	public String handleFileUpload(@RequestParam("file") MultipartFile file,
			RedirectAttributes redirectAttributes) throws IOException {

		String fileRef = storageService.store(file);
		log.info(fileRef);


		StoredFile uploadedBook = storageService.load(fileRef);
		
		Book book = new Book();
		book.setAuthor("test");
		book.setTitle("test title");
		book.setBook(uploadedBook);

		bookRepository.save(book);
		

		return fileRef;
	}

	@ExceptionHandler(StorageFileNotFoundException.class)
	public ResponseEntity<?> handleStorageFileNotFound(StorageFileNotFoundException exc) {
		return ResponseEntity.notFound().build();
	}

}