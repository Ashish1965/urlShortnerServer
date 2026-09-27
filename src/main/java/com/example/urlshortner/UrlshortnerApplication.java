package com.example.urlshortner;

import java.util.TimeZone;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

@SpringBootApplication
public class UrlshortnerApplication {

	public static void main(String[] args) {
        // System.out.println("Default TimeZone: " + TimeZone.getDefault());
		TimeZone.setDefault(TimeZone.getTimeZone("Asia/Kolkata"));
        // System.out.println("Default TimeZone: " + TimeZone.getDefault());
		SpringApplication.run(UrlshortnerApplication.class, args);
	}

}
