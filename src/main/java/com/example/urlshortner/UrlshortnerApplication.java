package com.example.urlshortner;

import java.util.TimeZone;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

@SpringBootApplication
public class UrlshortnerApplication {

	public static void main(String[] args) {
		// PostgreSQL must receive a timezone it recognizes during connection startup.
		TimeZone.setDefault(TimeZone.getTimeZone("UTC"));
        // System.out.println("Timezone: " + TimeZone.getDefault().getID());
        // System.out.println(TimeZone.getDefault());
		SpringApplication.run(UrlshortnerApplication.class, args);
	}

}
