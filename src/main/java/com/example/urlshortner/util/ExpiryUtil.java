package com.example.urlshortner.util;

import java.time.LocalDateTime;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

@Component
public class ExpiryUtil {
    @Value("${spring.shortener.expiry-minutes}")
    private int DEFAULT_EXPIRY_MINUTES;

    // private constructor to prevent object creation
    private ExpiryUtil() {}

    public LocalDateTime generateExpiryDate() {
        return LocalDateTime.now().plusMinutes(DEFAULT_EXPIRY_MINUTES);
    }
}