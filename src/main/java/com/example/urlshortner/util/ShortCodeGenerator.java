package com.example.urlshortner.util;

import org.springframework.beans.factory.annotation.Value;

import com.aventrix.jnanoid.jnanoid.NanoIdUtils;

public class ShortCodeGenerator {

    private static final char[] ALPHABET =
            "abcdefghijklmnopqrstuvwxyzABCDEFGHIJKLMNOPQRSTUVWXYZ0123456789".toCharArray();

    @Value ("${spring.shortener.short-code-length}")
    private static int SIZE;

    public static String generateCode() {
        return NanoIdUtils.randomNanoId(
                NanoIdUtils.DEFAULT_NUMBER_GENERATOR,
                ALPHABET,
                SIZE
        );
    }
}
