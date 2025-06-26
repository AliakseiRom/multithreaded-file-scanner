package com.main.multithreaded_scanner_file.model;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class TimeParser {

    public static Long parseTimeToMillis(String time) {
        Pattern pattern = Pattern.compile("^(\\d+)(s|m|h|d|mm)$");
        Matcher matcher = pattern.matcher(time.trim());

        if (!matcher.matches()) {
            throw new IllegalArgumentException("Invalid time format");
        }

        Long amount = Long.parseLong(matcher.group(1));
        String unit = matcher.group(2);

        return switch (unit) {
            case "s" -> amount * 1000L;
            case "m" -> amount * 60 * 1000L;
            case "h" -> amount * 60 * 60 * 1000L;
            case "d" -> amount * 24 * 60 * 60 * 1000L;
            case "mm" -> amount * 30L * 24 * 60 * 60 * 1000L;
            default -> throw new IllegalArgumentException("Invalid time unit");
        };
    }

}
