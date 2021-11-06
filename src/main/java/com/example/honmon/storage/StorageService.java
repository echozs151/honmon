package com.example.honmon.storage;

import org.springframework.core.io.Resource;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.nio.file.Path;
import java.util.stream.Stream;

public interface StorageService<T> {

	void init();

	String store(MultipartFile file);

	Stream<T> loadAll();

	T load(String filename) throws IOException;

	Resource loadAsResource(String filename);

	void deleteAll();

}