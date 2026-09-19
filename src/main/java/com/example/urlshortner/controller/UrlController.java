package com.example.urlshortner.controller;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.example.urlshortner.dto.UrlInfoDTO;
import com.example.urlshortner.dto.UrlRequestDTO;
import com.example.urlshortner.dto.UrlResponseDTO;
import com.example.urlshortner.service.UrlService;
import org.springframework.web.bind.annotation.PathVariable;


@RestController
@RequestMapping("/api/url")
public class UrlController {
    private final UrlService urlService;

    // constructor injection
    public UrlController(UrlService urlService) {
        this.urlService = urlService;
    }

    @PostMapping("/shorten")
    public ResponseEntity<UrlResponseDTO> createShortUrl(@RequestBody UrlRequestDTO request) {

        UrlResponseDTO response = urlService.createShortUrl(request);

        return ResponseEntity.ok(response);
    }

    @GetMapping("/info/{shortCode}")
    public ResponseEntity<UrlInfoDTO> getUrlInfo(@PathVariable String shortCode) {
        UrlInfoDTO response = urlService.getUrlInfo(shortCode);
        return ResponseEntity.ok(response);
    }
}
