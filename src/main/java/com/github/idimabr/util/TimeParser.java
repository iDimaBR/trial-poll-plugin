package com.github.idimabr.util;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class TimeParser {
    private static final Pattern TIME_PATTERN = Pattern.compile("(\\d+)([a-zA-Z]+)");

    public static int parseTime(String input) {
        Matcher matcher = TIME_PATTERN.matcher(input.toLowerCase());
        int totalSeconds = 0;
        boolean found = false;

        while (matcher.find()) {
            found = true;
            int value = Integer.parseInt(matcher.group(1));
            String unit = matcher.group(2);

            switch (unit) {
                case "s": totalSeconds += value; break;
                case "m": totalSeconds += value * 60; break; // minutes
                case "h": totalSeconds += value * 3600; break;
                case "d": totalSeconds += value * 86400; break;
                case "w": totalSeconds += value * 604800; break;
                case "mo": totalSeconds += value * 2592000; break; // months (30d)
                case "y": totalSeconds += value * 31536000; break; // years (365d)
                default: return -1;
            }
        }

        return found ? totalSeconds : -1;
    }

    public static String formatTime(int seconds) {
        if (seconds <= 0) return "0s";

        int years = seconds / 31536000;
        seconds %= 31536000;

        int months = seconds / 2592000;
        seconds %= 2592000;

        int weeks = seconds / 604800;
        seconds %= 604800;

        int days = seconds / 86400;
        seconds %= 86400;

        int hours = seconds / 3600;
        seconds %= 3600;

        int minutes = seconds / 60;
        int secs = seconds % 60;

        StringBuilder sb = new StringBuilder();
        if (years > 0) sb.append(years).append("y ");
        if (months > 0) sb.append(months).append("mo ");
        if (weeks > 0) sb.append(weeks).append("w ");
        if (days > 0) sb.append(days).append("d ");
        if (hours > 0) sb.append(hours).append("h ");
        if (minutes > 0) sb.append(minutes).append("m ");
        if (secs > 0) sb.append(secs).append("s");
        return sb.toString().trim();
    }
}
