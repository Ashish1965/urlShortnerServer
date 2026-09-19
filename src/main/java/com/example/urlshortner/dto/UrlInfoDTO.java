package com.example.urlshortner.dto;

public record UrlInfoDTO(String originalUrl,
        String shortCode,
        long clickCount) {

}
