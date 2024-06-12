package com.dlqudtjs.codingbattle.common.util;

import java.time.Instant;
import java.time.ZoneId;
import java.time.ZonedDateTime;
import java.util.Date;

public class Time {

    public static ZonedDateTime getZonedDateTime() {
        Instant now = Instant.now();
        return now.atZone(ZoneId.of("Asia/Seoul"));
    }

    public static Date getDate() {
        return Date.from(getZonedDateTime().toInstant());
    }

    public static long getCurrentTimeMillis() {
        return System.currentTimeMillis();
    }
}
