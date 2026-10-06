package com.example.urlshortner.service;

import com.example.urlshortner.entity.Url;
import com.example.urlshortner.repository.UrlRepository;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Optional;
import java.time.LocalDateTime;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
@ExtendWith(MockitoExtension.class)
class UrlServiceTest {

    @Mock
    private UrlRepository urlRepository;

    @InjectMocks
    private UrlServiceImpl urlService;


    @Test
    void shouldReturnOriginalUrl() {

        Url url = new Url();
        url.setOriginalUrl("https://google.com");

        when(urlRepository.findByShortCodeAndIsActiveTrue("abc"))
                .thenReturn(Optional.of(url));

        String result = urlService.getOriginalUrl("abc");

        assertEquals("https://google.com", result);
    }


    @Test
    void shouldThrowExceptionWhenUrlNotFound() {

        when(urlRepository.findByShortCodeAndIsActiveTrue("abc"))
                .thenReturn(Optional.empty());

        assertThrows(RuntimeException.class, () -> {
            urlService.getOriginalUrl("abc");
        });
    }


    @Test
    void shouldThrowExceptionWhenUrlIsExpired() {

        Url url = new Url();

        url.setOriginalUrl("https://google.com");
        url.setExpiryDate(LocalDateTime.now().minusDays(1));

        when(urlRepository.findByShortCodeAndIsActiveTrue("abc"))
                .thenReturn(Optional.of(url));

        assertThrows(RuntimeException.class, () -> {
            urlService.getOriginalUrl("abc");
        });
    }


    @Test
    void shouldReturnUrlNotExpired() {

        Url url = new Url();

        url.setOriginalUrl("https://google.com");
        url.setExpiryDate(LocalDateTime.now().plusDays(1));

        when(urlRepository.findByShortCodeAndIsActiveTrue("abc"))
                .thenReturn(Optional.of(url));

        String result = urlService.getOriginalUrl("abc");

        assertEquals("https://google.com", result);
    }


    @Test
    void shouldIncrementClickCountAndUpdateLastAccessedAt() {

        Url url = new Url();

        url.setOriginalUrl("https://google.com");
        url.setExpiryDate(LocalDateTime.now().plusDays(1));
        url.setClickCount(0L);

        when(urlRepository.findByShortCodeAndIsActiveTrue("abc"))
                .thenReturn(Optional.of(url));

        String result = urlService.getOriginalUrl("abc");

        assertEquals("https://google.com", result);
        assertEquals(1L, url.getClickCount());
        assertNotNull(url.getLastAccessedAt());

        verify(urlRepository).save(url);
    }
}