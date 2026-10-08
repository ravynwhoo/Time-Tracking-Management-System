package com.timetrack.util;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.sql.Date;
import java.sql.Time;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.time.LocalDate;
import java.time.LocalTime;
import java.time.format.DateTimeFormatter;
import java.time.temporal.ChronoUnit;

/**
 * Utility for parsing, formatting, and computing durations for Dates and Times.
 */
public class DateUtil {

    private static final String DATE_PATTERN = "yyyy-MM-dd";
    private static final String TIME_PATTERN = "HH:mm";
    private static final DateTimeFormatter TIME_FORMATTER = DateTimeFormatter.ofPattern("HH:mm");
    private static final DateTimeFormatter TIME_WITH_SEC = DateTimeFormatter.ofPattern("HH:mm:ss");

    /**
     * Parses a date string (YYYY-MM-DD) into java.sql.Date.
     */
    public static Date parseDate(String dateStr) {
        if (dateStr == null || dateStr.trim().isEmpty()) {
            return null;
        }
        try {
            SimpleDateFormat sdf = new SimpleDateFormat(DATE_PATTERN);
            sdf.setLenient(false);
            java.util.Date parsed = sdf.parse(dateStr.trim());
            return new Date(parsed.getTime());
        } catch (ParseException e) {
            throw new IllegalArgumentException("Invalid date format. Expected yyyy-MM-dd: " + dateStr, e);
        }
    }

    /**
     * Formats java.sql.Date into YYYY-MM-DD.
     */
    public static String formatDate(Date date) {
        if (date == null) return "";
        return new SimpleDateFormat(DATE_PATTERN).format(date);
    }

    /**
     * Parses a time string (HH:mm or HH:mm:ss) into java.sql.Time.
     */
    public static Time parseTime(String timeStr) {
        if (timeStr == null || timeStr.trim().isEmpty()) {
            return null;
        }
        String trimmed = timeStr.trim();
        LocalTime lt;
        if (trimmed.length() == 5) {
            lt = LocalTime.parse(trimmed, TIME_FORMATTER);
        } else {
            lt = LocalTime.parse(trimmed, TIME_WITH_SEC);
        }
        return Time.valueOf(lt);
    }

    /**
     * Formats java.sql.Time into HH:mm.
     */
    public static String formatTime(Time time) {
        if (time == null) return "";
        LocalTime lt = time.toLocalTime();
        return lt.format(TIME_FORMATTER);
    }

    /**
     * Calculates duration in hours between startTime and endTime.
     * Duration = (endTime - startTime) in minutes / 60.0.
     * If endTime is earlier than startTime (e.g. overnight), adds 24 hours.
     */
    public static BigDecimal calculateDuration(Time startTime, Time endTime) {
        if (startTime == null || endTime == null) {
            return BigDecimal.ZERO;
        }
        LocalTime start = startTime.toLocalTime();
        LocalTime end = endTime.toLocalTime();

        long minutes = ChronoUnit.MINUTES.between(start, end);
        if (minutes < 0) {
            minutes += 24 * 60; // Overnight wrap
        }

        BigDecimal hours = BigDecimal.valueOf(minutes).divide(BigDecimal.valueOf(60), 2, RoundingMode.HALF_UP);
        return hours;
    }

    /**
     * Gets today's date as java.sql.Date.
     */
    public static Date today() {
        return Date.valueOf(LocalDate.now());
    }

    /**
     * Gets date N days ago.
     */
    public static Date daysAgo(int days) {
        return Date.valueOf(LocalDate.now().minusDays(days));
    }
}
