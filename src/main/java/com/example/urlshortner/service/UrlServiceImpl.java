package com.example.urlshortner.service;

import org.springframework.beans.factory.annotation.Value;
import com.example.urlshortner.dto.UrlRequestDTO;
import com.example.urlshortner.dto.UrlResponseDTO;
import org.springframework.stereotype.Service;

import com.example.urlshortner.util.ShortCodeGenerator;
import com.example.urlshortner.repository.UrlRepository;
import com.example.urlshortner.entity.Url;

import com.example.urlshortner.dto.UrlInfoDTO;

import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;
import com.example.urlshortner.exception.ResourceNotFoundException;
import com.example.urlshortner.exception.UrlExpiredException;
import com.example.urlshortner.util.UrlValidator;
import com.example.urlshortner.exception.NotValidUrlException;
import java.util.Optional;
import com.example.urlshortner.util.ExpiryUtil;

@Service
public class UrlServiceImpl implements UrlService {
    @Value("${spring.shortener.short-url}")
    private String shortUrl;
    private final UrlRepository urlRepository;
    private final ExpiryUtil expiryUtil;

    public UrlServiceImpl(UrlRepository urlRepository, ExpiryUtil expiryUtil) {
        this.urlRepository = urlRepository;
        this.expiryUtil = expiryUtil;
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

        List<Url> existingUrls = urlRepository.findAllByOriginalUrl(request.url());

        return existingUrls.stream()
                .filter(url -> url.getExpiryDate() != null &&
                        url.getExpiryDate().isAfter(LocalDateTime.now()))
                .findFirst()
                .map(url -> new UrlResponseDTO(
                        request.url(),
                        shortUrl + "/" + url.getShortCode(),
                        url.getExpiryDate()))
                .orElseGet(() -> {
                    String shortCode = generateUniqueCode();

                    Url newUrl = new Url();
                    newUrl.setOriginalUrl(request.url());
                    newUrl.setShortCode(shortCode);
                    newUrl.setExpiryDate(expiryUtil.generateExpiryDate());
                    urlRepository.save(newUrl);

                    return new UrlResponseDTO(
                            request.url(),
                            shortUrl + "/" + shortCode,
                            newUrl.getExpiryDate());
                });
    }

    @Override
    public String getOriginalUrl(String shortCode) {
        Optional<Url> urlOptional = urlRepository.findByShortCode(shortCode);
        Url url = urlOptional.orElseThrow(() -> new ResourceNotFoundException("Short URL not found"));

        if (url.getExpiryDate() != null &&
                url.getExpiryDate().isBefore(LocalDateTime.now())) {
            throw new UrlExpiredException("This URL has expired");
        }

        url.setClickCount(url.getClickCount() + 1);
        urlRepository.save(url);
        return url.getOriginalUrl();
    }

    @Override
    public UrlInfoDTO getUrlInfo(String shortCode) {
        Optional<Url> urlOptional = urlRepository.findByShortCode(shortCode);
        Url url = urlOptional.orElseThrow(() -> new ResourceNotFoundException("Short URL not found"));

        if (url.getExpiryDate() != null &&
                url.getExpiryDate().isBefore(LocalDateTime.now())) {

            throw new UrlExpiredException("This URL has expired");
        }
        boolean isExpired = url.getExpiryDate() != null &&
                        url.getExpiryDate().isBefore(LocalDateTime.now());
        return new UrlInfoDTO(url.getOriginalUrl(), url.getShortCode(), url.getClickCount(), isExpired);
    }

    @Override
    public List<UrlInfoDTO> getAllUrls() {
        return urlRepository.findAll().stream()
            .map(url -> {

                boolean isExpired = url.getExpiryDate() != null &&
                        url.getExpiryDate().isBefore(LocalDateTime.now());

                return new UrlInfoDTO(
                        url.getOriginalUrl(),
                        url.getShortCode(),
                        url.getClickCount(),
                        isExpired
                );
            })
            .toList();
    }

    private String generateUniqueCode() {
        String code;
        do {
            code = ShortCodeGenerator.generateCode();
        } while (urlRepository.findByShortCode(code).isPresent());

        return code;
    }
}
