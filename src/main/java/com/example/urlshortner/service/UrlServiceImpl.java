package com.example.urlshortner.service;

import org.springframework.beans.factory.annotation.Value;
import com.example.urlshortner.dto.UrlRequestDTO;
import com.example.urlshortner.dto.UrlResponseDTO;
import org.springframework.stereotype.Service;
import com.example.urlshortner.util.ShortCodeGenerator;
import com.example.urlshortner.repository.UrlRepository;
import com.example.urlshortner.entity.Url;


@Service
public class UrlServiceImpl implements UrlService {
    @Value("${app.base-url}")
    private String baseUrl;
    private final UrlRepository urlRepository;

    public UrlServiceImpl(UrlRepository urlRepository) {
        this.urlRepository = urlRepository;
    }

    @Override
    public UrlResponseDTO createShortUrl(UrlRequestDTO request) {
        // Implement the logic to create a short URL here
        String shortCode = ShortCodeGenerator.generateCode();

        Url url = new Url();
        url.setOriginalUrl(request.url());
        url.setShortCode(shortCode);
        urlRepository.save(url);
        return new UrlResponseDTO(
                request.url(),
                baseUrl + "/" + shortCode
        );
    }
}
