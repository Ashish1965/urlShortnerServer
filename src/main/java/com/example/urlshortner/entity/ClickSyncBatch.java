package com.example.urlshortner.entity;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;  
import lombok.Data;

import java.time.LocalDateTime;
import java.util.UUID;

@Entity
@Data
public class ClickSyncBatch {
    @Id
    @Column(name = "batch_id")
    private UUID batchId;

    @Column(name = "short_code", nullable = false)
    private String shortCode;

    @Column(name = "click_count", nullable = false)
    private Long clickCount;

    @Column(name = "synced_at", nullable = false)
    private LocalDateTime syncedAt;

}
