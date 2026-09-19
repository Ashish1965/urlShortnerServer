package com.example.urlshortner.controller;

import com.example.urlshortner.service.UrlService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;        
import org.springframework.web.bind.annotation.RestController;
import org.springframework.http.HttpHeaders;


@RestController 
public class RedirectController {
    private final UrlService urlService;    

    public RedirectController(UrlService urlService) {
        this.urlService = urlService;
    }
    @GetMapping ("/{shortCode}")
    public ResponseEntity<String> getOriginalUrl(@PathVariable String shortCode) {
        String originalUrl = urlService.getOriginalUrl(shortCode);
        return ResponseEntity
                .status(HttpStatus.FOUND) // 302 redirect
                .header(HttpHeaders.LOCATION, originalUrl)
                .build();
    }
}
