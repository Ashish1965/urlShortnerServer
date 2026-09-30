package com.example.urlshortner.scheduler;

import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;
import java.time.LocalDateTime;

import com.example.urlshortner.entity.Url;
import com.example.urlshortner.repository.UrlRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import jakarta.transaction.Transactional;
import java.util.List;
import org.springframework.beans.factory.annotation.Value;

@Component
public class UrlCleanupScheduler {

private static final Logger softLog = LoggerFactory.getLogger("SOFT_DELETE_LOG");
private static final Logger hardLog = LoggerFactory.getLogger("HARD_DELETE_LOG");
    private final UrlRepository urlRepository;
    
    @Value("${spring.shortener.permanent-delete-days}")
    private int permanentDeleteDays;

    public UrlCleanupScheduler(UrlRepository urlRepository) {
        this.urlRepository = urlRepository;
    }

    @Scheduled(fixedRate = 60000)
    @Transactional
    public void markExpiredUrls() {

        LocalDateTime now = LocalDateTime.now();

        List<Url> expiredUrls = urlRepository
                .findByExpiryDateBeforeAndIsActiveTrue(now);

        softLog.info("Found {} expired URLs", expiredUrls.size());

        int count = urlRepository.softDeleteExpired(now);

        softLog .info("Marked {} URLs as expired at {}", count, now);
    }

    @Scheduled(fixedRate = 60000)
    @Transactional
    public void deleteOldSoftDeletedUrls() {

        LocalDateTime cutoff = LocalDateTime.now().minusDays(permanentDeleteDays);

        long count = urlRepository
                .countByIsActiveFalseAndDeletedAtBefore(cutoff);
        hardLog.info("Found {} soft-deleted URLs older than {} days", count, permanentDeleteDays);
        urlRepository
                .deleteByIsActiveFalseAndDeletedAtBefore(cutoff);

        hardLog.info("Permanently deleted {} URLs", count);
    }
}