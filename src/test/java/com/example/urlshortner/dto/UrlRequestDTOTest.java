package com.example.urlshortner.dto;

import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;

class UrlRequestDTOTest {

    @Test
    void shouldCreateDtoSuccessfully() {

        // given
        String testUrl = "https://google.com";

        // when
        UrlRequestDTO dto = new UrlRequestDTO(testUrl);

        // then
        assertNotNull(dto);
        assertEquals(testUrl, dto.url());
    }

    @Test
    void shouldHandleNullUrl() {

        UrlRequestDTO dto = new UrlRequestDTO(null);

        assertNotNull(dto); // object still created
        assertNull(dto.url()); // value is null
        assertEquals(null, dto.url());
    }
}
