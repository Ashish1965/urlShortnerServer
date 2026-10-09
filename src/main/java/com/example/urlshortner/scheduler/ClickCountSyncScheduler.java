package com.example.urlshortner.scheduler;

import com.example.urlshortner.service.UrlCacheService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;
import java.util.Set;

@Component
public class ClickCountSyncScheduler {
    private static final Logger log = LoggerFactory.getLogger(ClickCountSyncScheduler.class);

    private final UrlCacheService urlCacheService;

    public ClickCountSyncScheduler(
            UrlCacheService urlCacheService) {
        this.urlCacheService = urlCacheService;
    }

    @Scheduled(fixedRate = 120_000)
    public void synchronizeClickCounts() {

        Set<String> shortCodes = urlCacheService.getPendingShortCodes();

        if (shortCodes.isEmpty()) {
            log.info("No pending click counters found");
            return;
        }

        for (String shortCode : shortCodes) {

            String batchId = urlCacheService.claimPendingBatch(shortCode);

            if (batchId != null) {
                log.info(
                        "Claimed batch {} for short code {}",
                        batchId,
                        shortCode);

                // Next: persist batch to PostgreSQL,
                // then acknowledge it after commit.
            }
        }
    }
}
