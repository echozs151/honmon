package com.example.honmon;

import java.io.IOException;

import com.example.honmon.services.ZipService;

import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;

@SpringBootTest
class HonmonApplicationTests {

	@Test
	void contextLoads() throws IOException {
		BCryptPasswordEncoder encoder = new BCryptPasswordEncoder();
		String pass = "test2";
		String encodedPass = encoder.encode(pass);
		System.out.println(encodedPass);

	}

	@Test
	void zipServiceTest() throws IOException{
		// ZipService zipService = new ZipService();
		ZipService.unzip("src/main/resources/zips/fairygirls_vol_1.cbz");
	}

}
