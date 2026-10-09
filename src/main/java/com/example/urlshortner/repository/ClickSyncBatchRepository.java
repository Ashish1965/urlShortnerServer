
package com.example.urlshortner.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import com.example.urlshortner.entity.ClickSyncBatch;

import java.util.UUID;

public interface ClickSyncBatchRepository
        extends JpaRepository<ClickSyncBatch, UUID> {

    @Modifying
    @Query(value = """
        INSERT INTO click_sync_batch
            (batch_id, short_code, click_count)
        VALUES
            (CAST(:batchId AS UUID), :shortCode, :clickCount)
        ON CONFLICT (batch_id) DO NOTHING
        """, nativeQuery = true)
    int insertIfAbsent(
            @Param("batchId") UUID batchId,
            @Param("shortCode") String shortCode,
            @Param("clickCount") long clickCount
    );
}