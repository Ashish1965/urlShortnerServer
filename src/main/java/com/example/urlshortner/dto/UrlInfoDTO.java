package com.example.urlshortner.dto;

import java.time.LocalDateTime;

public record UrlInfoDTO(String originalUrl,
        String shortCode,
        long clickCount,
        boolean isActive,
        LocalDateTime lastAccessedAt) {

}

 


