package com.dlqudtjs.codingbattle.common.util;

import java.sql.Timestamp;
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

    public static Timestamp getTimestamp() {
        return new Timestamp(getDate().getTime());
    }

    public static ZonedDateTime convertTimestampToZonedDateTime(Timestamp timestamp) {
        return timestamp.toInstant().atZone(ZoneId.of("Asia/Seoul"));
    }

    public static long getCurrentTimeMillis() {
        return System.currentTimeMillis();
    }
}
