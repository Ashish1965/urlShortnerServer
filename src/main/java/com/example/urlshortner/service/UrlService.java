package com.example.urlshortner.service;

import com.example.urlshortner.dto.UrlRequestDTO;
import com.example.urlshortner.dto.UrlResponseDTO;

public interface UrlService {
    UrlResponseDTO createShortUrl(UrlRequestDTO request);
}
