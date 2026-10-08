package com.example.urlshortner.controller;

import com.example.urlshortner.service.UrlServiceImpl;
import com.example.urlshortner.util.UrlValidator;
import com.example.urlshortner.dto.UrlRequestDTO;
import com.example.urlshortner.entity.Url;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;

import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.when;

import java.time.LocalDateTime;

@ExtendWith(MockitoExtension.class)
class UrlControllerTest {

    @Mock
    private UrlServiceImpl urlService;

    @InjectMocks
    private UrlController urlController;

    @Test
    void checkInvalidUrl() {

        String url = "invalid-url";

        boolean result = UrlValidator.isValidUrl(url);

        System.out.println("Validation result = " + result);

        assertFalse(result);
    }

    
}