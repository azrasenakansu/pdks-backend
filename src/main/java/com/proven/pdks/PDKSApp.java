package com.proven.pdks;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.scheduling.annotation.EnableScheduling;

@SpringBootApplication
@EnableScheduling
public class PDKSApp {

	public static void main(String[] args) {
		SpringApplication.run(PDKSApp.class, args);
	}

}
