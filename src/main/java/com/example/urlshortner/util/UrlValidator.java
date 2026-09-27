package com.example.urlshortner.util;

import java.net.URI;
public class UrlValidator {
    public static boolean isValidUrl(String url) {
        try {
            new URI(url).toURL();
            return true;
        } catch (Exception e) {
            return false;
        }
    }
}
