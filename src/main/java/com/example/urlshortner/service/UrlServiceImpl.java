package com.example.urlshortner.service;

import org.springframework.beans.factory.annotation.Value;
import com.example.urlshortner.dto.UrlRequestDTO;
import com.example.urlshortner.dto.UrlResponseDTO;
import org.springframework.stereotype.Service;

import com.example.urlshortner.util.ShortCodeGenerator;
import com.example.urlshortner.repository.UrlRepository;
import com.example.urlshortner.entity.Url;
import java.util.Optional;
import com.example.urlshortner.dto.UrlInfoDTO;
import java.util.List;
import java.util.stream.Collectors;
import com.example.urlshortner.exception.ResourceNotFoundException;
import com.example.urlshortner.util.UrlValidator;
import com.example.urlshortner.exception.NotValidUrlException;

@Service
public class UrlServiceImpl implements UrlService {
    @Value("${spring.shortener.short-url}")
    private String shortUrl;
    private final UrlRepository urlRepository;

    public UrlServiceImpl(UrlRepository urlRepository) {
        this.urlRepository = urlRepository;
    }

    @Override
    public UrlResponseDTO createShortUrl(UrlRequestDTO request) {
        // Implement the logic to create a short URL here

        // Optional<Url> existing = urlRepository.findByOriginalUrl(request.url());
        // if (existing.isPresent()) {
        // return new UrlResponseDTO(
        // request.url(),
        // baseUrl + "/" + existing.get().getShortCode());
        // }
        if (!UrlValidator.isValidUrl(request.url())) {
            throw new NotValidUrlException("Invalid URL format");
        }
        String shortCode = generateUniqueCode();

        Url url = new Url();
        url.setOriginalUrl(request.url());
        url.setShortCode(shortCode);
        urlRepository.save(url);
        return new UrlResponseDTO(
                request.url(),
                shortUrl + "/" + shortCode);
    }

    @Override
    public String getOriginalUrl(String shortCode) {
        Optional<Url> urlOptional = urlRepository.findByShortCode(shortCode);
        Url url = urlOptional.orElseThrow(() -> new ResourceNotFoundException("Short URL not found"));

        url.setClickCount(url.getClickCount() + 1);
        urlRepository.save(url);
        return url.getOriginalUrl();
    }

    @Override
    public UrlInfoDTO getUrlInfo(String shortCode) {
        Optional<Url> urlOptional = urlRepository.findByShortCode(shortCode);
        Url url = urlOptional.orElseThrow(() -> new ResourceNotFoundException("Short URL not found"));
        return new UrlInfoDTO(url.getOriginalUrl(), url.getShortCode(), url.getClickCount());
    }

    @Override
    public List<UrlInfoDTO> getAllUrls() {
        return urlRepository.findAll().stream()
                .map(url -> new UrlInfoDTO(url.getOriginalUrl(), url.getShortCode(), url.getClickCount()))
                .collect(Collectors.toList());
    }

    private String generateUniqueCode() {
        String code;
        do {
            code = ShortCodeGenerator.generateCode();
        } while (urlRepository.findByShortCode(code).isPresent());

        return code;
    }
}
