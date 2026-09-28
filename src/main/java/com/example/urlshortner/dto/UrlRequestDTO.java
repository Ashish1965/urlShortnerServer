package com.example.urlshortner.dto;


import jakarta.validation.constraints.NotBlank;
public record UrlRequestDTO(
        @NotBlank(message = "URL cannot be empty") String url
    ) {

}