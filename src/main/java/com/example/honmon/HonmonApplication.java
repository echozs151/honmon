package com.example.honmon;

import com.example.honmon.Repo.UserRepository;
import com.example.honmon.storage.StorageProperties;
import com.example.honmon.storage.StorageService;
import com.mongodb.client.MongoClients;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.data.mongodb.core.MongoOperations;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.security.core.userdetails.User;
import org.springframework.web.bind.annotation.CrossOrigin;


@SpringBootApplication
@EnableConfigurationProperties(StorageProperties.class)
public class HonmonApplication {

	@Autowired
  	private UserRepository repository;

	public static void main(String[] args) {
		SpringApplication.run(HonmonApplication.class, args);
	}

	// @Bean
	// public WebMvcConfigurer corsConfigurer() {
	// 	return new WebMvcConfigurer() {
	// 		@Override
	// 		public void addCorsMappings(CorsRegistry registry) {
	// 			registry.addMapping("*").allowedOrigins("http://localhost:8081/");
	// 		}
	// 	};
	// }

	@Bean
	CommandLineRunner init(StorageService storageService) {
		return (args) -> {
			storageService.deleteAll();
			storageService.init();
		};
	}

}
