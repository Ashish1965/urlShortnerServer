package com.example.urlshortner.repository;

import com.example.urlshortner.entity.Url;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import java.util.Optional;
import java.time.LocalDateTime;
import java.util.List;

@Repository
public interface UrlRepository extends JpaRepository<Url, Long> {
    Optional<Url> findByShortCodeAndIsActiveTrue(String shortCode);

    Optional<Url> findByOriginalUrlAndIsActiveTrue(String originalUrl);

    List<Url> findAllByOriginalUrlAndIsActiveTrue(String originalUrl);

    List<Url> findByExpiryDateBeforeAndIsActiveTrue(LocalDateTime now);

    @Modifying
    @Query("""
                UPDATE Url u
                SET u.isActive = false, u.deletedAt = :now
                WHERE u.expiryDate < :now AND u.isActive = true
            """)
    int softDeleteExpired(@Param("now") LocalDateTime now);

    @Modifying
    @Query("""
                UPDATE Url u
                SET u.clickCount = u.clickCount + 1,
                    u.lastAccessedAt = :now
                WHERE u.shortCode = :shortCode
            """)
    int incrementClickCount(
            @Param("shortCode") String shortCode,
            @Param("now") LocalDateTime now);

    long countByIsActiveFalseAndDeletedAtBefore(LocalDateTime time);

    void deleteByIsActiveFalseAndDeletedAtBefore(LocalDateTime time);

    List<Url> findTop5ByOrderByClickCountDesc();

    boolean existsByShortCode(String shortCode);
}