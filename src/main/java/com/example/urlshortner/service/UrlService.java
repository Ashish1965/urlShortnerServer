package com.example.urlshortner.service;

import com.example.urlshortner.dto.UrlRequestDTO;
import com.example.urlshortner.dto.UrlResponseDTO;
import com.example.urlshortner.dto.*;
public interface UrlService {
    UrlResponseDTO createShortUrl(UrlRequestDTO request);
    String getOriginalUrl(String shortCode);
    UrlInfoDTO getUrlInfo(String shortCode);
}
