package com.example.urlshortner.dto;

import java.time.LocalDateTime;

public record UrlResponseDTO(String originalUrl,String shortUrl,LocalDateTime expiryDate){

}
