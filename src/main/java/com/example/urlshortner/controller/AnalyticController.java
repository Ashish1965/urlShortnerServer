package com.example.urlshortner.controller;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import java.util.List;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.example.urlshortner.dto.UrlInfoDTO;
import com.example.urlshortner.service.UrlService;

@RestController
@RequestMapping("/api/analytics/url")

public class AnalyticController {

    private final UrlService urlService;
    private static final Logger log = LoggerFactory.getLogger(UrlController.class);
    public AnalyticController(UrlService urlService) {
        this.urlService = urlService;
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

    @GetMapping ("/top")
    public ResponseEntity<List<UrlInfoDTO>> getTopUrls() {
        log.info("Received request for top URLs");

        return ResponseEntity.ok(urlService.getTopUrls());
    }
}
