package com.rollerspeed;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

@SpringBootApplication(scanBasePackages = "com.rollerspeed")
public class RollerspeedApplication {

	public static void main(String[] args) {
		SpringApplication.run(RollerspeedApplication.class, args);
	}

}
