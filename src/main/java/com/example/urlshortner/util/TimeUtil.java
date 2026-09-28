package com.example.urlshortner.util;

import java.time.LocalDateTime;
import java.time.ZoneId;

public class TimeUtil {

    private static final ZoneId UTC = ZoneId.of("UTC");
    private static final ZoneId IST = ZoneId.of("Asia/Kolkata");

    // private constructor to prevent object creation
    private TimeUtil() {}

    public static LocalDateTime convertToIST(LocalDateTime utcTime) {
        if (utcTime == null) return null;

        return utcTime.atZone(UTC)
                .withZoneSameInstant(IST)
                .toLocalDateTime();
    }

    public static LocalDateTime convertToUTC(LocalDateTime istTime) {
        if (istTime == null) return null;

        return istTime.atZone(IST)
                .withZoneSameInstant(UTC)
                .toLocalDateTime();
    }
}