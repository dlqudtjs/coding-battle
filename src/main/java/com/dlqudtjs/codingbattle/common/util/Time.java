package com.dlqudtjs.codingbattle.common.util;

import java.sql.Timestamp;
import java.time.Duration;
import java.time.Instant;
import java.time.LocalDateTime;
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

    public static String getElapsedTime(Timestamp startTime, Timestamp endTime) {
        LocalDateTime start = startTime.toLocalDateTime();
        LocalDateTime end = endTime.toLocalDateTime();

        Duration duration = Duration.between(start, end);

        long hours = duration.toHours();
        long minutes = duration.toMinutesPart();
        long seconds = duration.toSecondsPart();

        return String.format("%02d:%02d:%02d", hours, minutes, seconds);
    }

    public static ZonedDateTime convertTimestampToZonedDateTime(Timestamp timestamp) {
        return timestamp.toInstant().atZone(ZoneId.of("Asia/Seoul"));
    }

    public static long getCurrentTimeMillis() {
        return System.currentTimeMillis();
    }
}
