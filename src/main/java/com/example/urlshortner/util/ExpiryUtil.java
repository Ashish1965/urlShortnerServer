package com.example.urlshortner.util;

import java.time.LocalDateTime;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

@Component
public class ExpiryUtil {
    @Value("${spring.shortener.expiry-days}")
    private int DEFAULT_EXPIRY_DAYS;

    // private constructor to prevent object creation
    private ExpiryUtil() {}

    public LocalDateTime generateExpiryDate() {
        return LocalDateTime.now().plusDays(DEFAULT_EXPIRY_DAYS);
    }
}