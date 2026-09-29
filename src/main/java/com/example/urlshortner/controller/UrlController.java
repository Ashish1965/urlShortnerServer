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

import jakarta.validation.Valid;

import java.util.List;
import org.springframework.web.bind.annotation.PathVariable;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;


@RestController
@RequestMapping("/api/url")
public class UrlController {

    private final UrlService urlService;
    private static final Logger log = LoggerFactory.getLogger(UrlController.class);
    // constructor injection
    public UrlController(UrlService urlService) {
        this.urlService = urlService;
    }

    @PostMapping("/shorten")
    public ResponseEntity<UrlResponseDTO> createShortUrl(@Valid @RequestBody UrlRequestDTO request) {
        log.info("Received request to shorten URL: {}", request.url());
        UrlResponseDTO response = urlService.createShortUrl(request);

        return ResponseEntity.ok(response);
    }

    @GetMapping("/info/{shortCode}")
    public ResponseEntity<UrlInfoDTO> getUrlInfo(@PathVariable String shortCode) {
        log.info("Received request for URL info for short code: {}", shortCode);
        UrlInfoDTO response = urlService.getUrlInfo(shortCode);
        return ResponseEntity.ok(response);
    }

    @GetMapping ("/all")
    public ResponseEntity<List<UrlInfoDTO>> getAllUrls() {
        log.info("Received request for all URLs");
        return ResponseEntity.ok(urlService.getAllUrls());
    }
}
