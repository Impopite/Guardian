package it.impo.protect.api.utils;

import java.time.format.DateTimeFormatter;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class LogUtils {

    private static final DateTimeFormatter FORMATTER = DateTimeFormatter.ofPattern("dd/MM/yyyy - HH:mm:ss");
    private static final Pattern TIME_PATTERN = Pattern.compile("(\\d+)([smhd])");
    private static final Pattern STRICT_TIME_PATTERN = Pattern.compile("^(\\d+[smhd])+$");


    public static long parseTime(String input) {
        if (input == null || input.isBlank()) return 0;
        input = input.toLowerCase().trim();
        if (!isValidTimeInput(input)) return 0;

        long seconds = 0;
        Matcher m = TIME_PATTERN.matcher(input);
        while (m.find()) {
            long v = Long.parseLong(m.group(1));
            seconds += switch (m.group(2).charAt(0)) {
                case 's' -> v;
                case 'm' -> v * 60;
                case 'h' -> v * 3600;
                case 'd' -> v * 86400;
                default  -> 0;
            };
        }
        return seconds;
    }

    public static boolean isValidTimeInput(String input) {
        if (input == null || input.isBlank()) return false;
        return STRICT_TIME_PATTERN.matcher(input.toLowerCase().trim()).matches();
    }
}
