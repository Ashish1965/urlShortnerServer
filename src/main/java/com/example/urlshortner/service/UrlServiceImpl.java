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
import java.time.temporal.ChronoUnit;
import java.util.List;
import com.example.urlshortner.exception.ResourceNotFoundException;
import com.example.urlshortner.exception.UrlExpiredException;
import com.example.urlshortner.util.UrlValidator;
import com.example.urlshortner.exception.NotValidUrlException;
import java.util.Optional;
import com.example.urlshortner.util.ExpiryUtil;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import jakarta.transaction.Transactional;

@Service
public class UrlServiceImpl implements UrlService {
    private static final Logger log = LoggerFactory.getLogger(UrlServiceImpl.class);
    @Value("${spring.shortener.short-url}")
    private String shortUrl;
    private final UrlRepository urlRepository;
    private final ExpiryUtil expiryUtil;
    private final ShortCodeGenerator shortCodeGenerator;
    private final UrlCacheService urlCacheService;

    public UrlServiceImpl(UrlRepository urlRepository, ExpiryUtil expiryUtil, ShortCodeGenerator shortCodeGenerator,
            UrlCacheService urlCacheService) {
        this.urlRepository = urlRepository;
        this.expiryUtil = expiryUtil;
        this.shortCodeGenerator = shortCodeGenerator;
        this.urlCacheService = urlCacheService;
    }

    @Transactional
    @Override
    public UrlResponseDTO createShortUrl(UrlRequestDTO request) {
        // Implement the logic to create a short URL here

        // Optional<Url> existing = urlRepository.findByOriginalUrl(request.url());
        // if (existing.isPresent()) {
        // return new UrlResponseDTO(
        // request.url(),
        // baseUrl + "/" + existing.get().getShortCode());
        // }

        log.info("Creating short URL for: {}", request.url());
        if (!UrlValidator.isValidUrl(request.url())) {
            log.warn("Invalid URL format: {}", request.url());
            throw new NotValidUrlException("Invalid URL format");
        }

        List<Url> existingUrls = urlRepository.findAllByOriginalUrlAndIsActiveTrue(request.url());
        log.info("Found {} existing URLs for: {}", existingUrls.size(), request.url());
        return existingUrls.stream()
                .filter(url -> url.isActive() && url.getExpiryDate() != null &&
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

    @Transactional
    @Override
    public String getOriginalUrl(String shortCode) {
        log.info("Retrieving original URL for short code: {}", shortCode);

        String cachedUrl = urlCacheService.get(shortCode);
        // Long claimed = urlCacheService.claimPendingClicks(shortCode);

        // log.info("Claimed clicks: " + claimed);

        if (cachedUrl != null) {
            log.info("Cache hit for short code: {}. Returning cached URL: {}", shortCode, cachedUrl);
            urlCacheService.incrementClickCount(shortCode);

            return cachedUrl;
        }
        log.info("Cache miss for short code: {}. Fetching from database.", shortCode);
        Optional<Url> urlOptional = urlRepository.findByShortCodeAndIsActiveTrue(shortCode);

        log.info("Found URL: {}", urlOptional.map(Url::getOriginalUrl).orElse("Not Found"));
        Url url = urlOptional.orElseThrow(() -> new ResourceNotFoundException("Short URL not found"));

        if (url.getExpiryDate() != null &&
                url.getExpiryDate().isBefore(LocalDateTime.now())) {

            log.info("URL with short code {} has expired", shortCode);
            throw new UrlExpiredException("This URL has expired");
        }

        url.setClickCount(url.getClickCount() + 1);
        url.setLastAccessedAt(LocalDateTime.now());
        urlRepository.save(url);

        LocalDateTime now = LocalDateTime.now();
        LocalDateTime expiryDate = url.getExpiryDate();

        if (expiryDate != null && !expiryDate.isAfter(now)) {
            throw new UrlExpiredException("URL expired");
        }

        if (expiryDate != null) {
            long ttlSeconds = ChronoUnit.SECONDS.between(now, expiryDate);

            urlCacheService.put(
                    shortCode,
                    url.getOriginalUrl(),
                    ttlSeconds);
            log.info("Cached original URL: {} for short code: {} with TTL: {} seconds", url.getOriginalUrl(), shortCode,
                    ttlSeconds);
        }

        return url.getOriginalUrl();
    }

    @Override
    public UrlInfoDTO getUrlInfo(String shortCode) {
        Optional<Url> urlOptional = urlRepository.findByShortCodeAndIsActiveTrue(shortCode);
        Url url = urlOptional.orElseThrow(() -> new ResourceNotFoundException("Short URL not found"));

        if (url.getExpiryDate() != null &&
                url.getExpiryDate().isBefore(LocalDateTime.now())) {
            log.info("URL with short code {} has expired", shortCode);
            throw new UrlExpiredException("This URL has expired");
        }

        log.info("Returning info for URL with short code: {}", shortCode);
        return new UrlInfoDTO(url.getOriginalUrl(), url.getShortCode(), url.getClickCount(), url.isActive(),
                url.getLastAccessedAt());
    }

    @Override
    public List<UrlInfoDTO> getAllUrls() {
        log.info("Fetching all URLs");
        return urlRepository.findAll().stream()
                .map(url -> {

                    return new UrlInfoDTO(
                            url.getOriginalUrl(),
                            url.getShortCode(),
                            url.getClickCount(),
                            url.isActive(),
                            url.getLastAccessedAt());
                })
                .toList();
    }

    @Override
    public List<UrlInfoDTO> getTopUrls() {

        return urlRepository.findTop5ByOrderByClickCountDesc()
                .stream()
                .map(url -> new UrlInfoDTO(
                        url.getOriginalUrl(),
                        url.getShortCode(),
                        url.getClickCount(),
                        url.isActive(),
                        url.getLastAccessedAt()))
                .toList();
    }

    public String generateUniqueCode() {
        String code;
        do {
            code = shortCodeGenerator.generateCode();
        } while (urlRepository.findByShortCodeAndIsActiveTrue(code).isPresent());

        return code;
    }
}
