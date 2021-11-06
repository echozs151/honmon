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
package com.example.honmon.config;

import com.example.honmon.Models.Book;
import com.example.honmon.Models.Settings;
import com.example.honmon.Models.User;
import com.example.honmon.Repo.BookRepository;
import com.example.honmon.Repo.SettingsRepository;
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
      UserRepository userRepository,
      SettingsRepository settingsRepository
      ) {

      Settings dbInitCheck = settingsRepository.findByKey("db_init");

      if (dbInitCheck == null) {
        settingsRepository.save(new Settings("db_init", "1"));

        return args -> {
          log.info("Preloading " + repository.save(new Book("Bilbo Baggins", "burglar")));
          log.info("Preloading " + repository.save(new Book("Frodo Baggins", "thief")));
          log.info("Preloading " + userRepository.save(new User("admin", "admin", "ROLE_ADMIN")));
          log.info("Preloading " + userRepository.save(new User("user", "user", "ROLE_USER")));
      };
      }
      return null;
      
      
  }
}