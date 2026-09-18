package com.example.urlshortner.util;

import java.util.Random;

public class ShortCodeGenerator {
    private static final String CHAR_SET = "abcdefghijklmnopqrstuvwxyzABCDEFGHIJKLMNOPQRSTUVWXYZ0123456789";
    private static final int LENGTH = 6;

    public static String generateCode() {
        StringBuilder sb = new StringBuilder();
        Random random = new Random();

        for (int i = 0; i < LENGTH; i++) {
            sb.append(CHAR_SET.charAt(random.nextInt(CHAR_SET.length())));
        }

        return sb.toString();
    }
}
