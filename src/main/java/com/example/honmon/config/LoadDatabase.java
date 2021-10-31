package com.example.honmon.config;

import com.example.honmon.Models.Book;
import com.example.honmon.Models.User;
import com.example.honmon.Repo.BookRepository;
import com.example.honmon.Repo.UserRepository;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.CommandLineRunner;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
class LoadDatabase {

  private static final Logger log = LoggerFactory.getLogger(LoadDatabase.class);

  @Bean
  CommandLineRunner initDatabase(
      BookRepository repository,
      UserRepository userRepository
      ) {

    return args -> {
      log.info("Preloading " + repository.save(new Book("Bilbo Baggins", "burglar")));
      log.info("Preloading " + repository.save(new Book("Frodo Baggins", "thief")));
      log.info("Preloading " + userRepository.save(new User("admin", "admin", "ROLE_ADMIN")));
      log.info("Preloading " + userRepository.save(new User("user", "user", "ROLE_USER")));
    };
  }
}