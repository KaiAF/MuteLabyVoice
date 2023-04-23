package com.namemc.mlv.utils;

public class TimeManager {
    /**
     *
     * @return the current time in milliseconds.
     */
    public static long getTime() {
        return System.currentTimeMillis();
    }

    /**
     * Convert "3d" to milliseconds
     *
     * @param timeString the time string
     * @return the amount of milliseconds equivalent to the given string. The default is seconds.
     */
    public static long toMS(String timeString) {
        String[] timeStringFormatted = timeString.toLowerCase().split("(?<=\\D)(?=\\d)|(?<=\\d)(?=\\D)");
        Long i = Long.parseLong(timeStringFormatted[0]);
        String type = timeStringFormatted[1];

        switch (type) {
            case "m":
                return i * 1000 * 60;
            case "h":
                return i * 1000 * 60 * 60;
            case "d":
                return i * 1000 * 60 * 60 * 24;
            case "w":
                return i * 1000 * 60 * 60 * 24 * 7;
            case "mo":
                return i * 1000 * 60 * 60 * 24 * 31;
            case "y":
                return i * 1000 * 60 * 60 * 24 * 31 * 13;
            default:
                return i * 1000;
        }
    }
}
