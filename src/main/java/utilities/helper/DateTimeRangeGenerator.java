package utilities.helper;

import org.apache.commons.lang.math.RandomUtils;

import java.time.*;
import java.time.format.DateTimeFormatter;
import java.time.temporal.TemporalAdjusters;

/**
 * The {@code DateTimeRangeGenerator} class provides methods to generate various date and time ranges
 * for today, yesterday, this week, last week, this month, last month, this year, last year,
 * and a random custom range.
 */
public class DateTimeRangeGenerator {

    private static final ZoneOffset zoneOffset = ZoneOffset.ofHours(7);
    private static final DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd'T'HH:mm:ssXXX");

    /**
     * Converts a {@code LocalDateTime} to the required format in UTC+07:00.
     *
     * @param dateTime the {@code LocalDateTime} to be formatted
     * @return a formatted string representation of the {@code LocalDateTime}
     */
    private static String formatDateTime(LocalDateTime dateTime) {
        return dateTime.atOffset(zoneOffset).format(formatter);
    }

    /**
     * Gets the date range for today.
     *
     * @return an array containing the start and end date-time of today
     */
    public static String[] getTodayRange() {
        LocalDateTime start = LocalDate.now().atStartOfDay();
        LocalDateTime end = LocalDate.now().atTime(LocalTime.MAX);
        return new String[]{formatDateTime(start), formatDateTime(end)};
    }

    /**
     * Gets the date range for yesterday.
     *
     * @return an array containing the start and end date-time of yesterday
     */
    public static String[] getYesterdayRange() {
        LocalDateTime start = LocalDate.now().minusDays(1).atStartOfDay();
        LocalDateTime end = LocalDate.now().minusDays(1).atTime(LocalTime.MAX);
        return new String[]{formatDateTime(start), formatDateTime(end)};
    }

    /**
     * Gets the date range for the current week (from Monday to today).
     *
     * @return an array containing the start and end date-time of this week
     */
    public static String[] getThisWeekRange() {
        LocalDate today = LocalDate.now();
        LocalDateTime start = today.with(TemporalAdjusters.previousOrSame(DayOfWeek.MONDAY)).atStartOfDay();
        LocalDateTime end = today.atTime(LocalTime.MAX);
        return new String[]{formatDateTime(start), formatDateTime(end)};
    }

    /**
     * Gets the date range for the last week (from Monday to Sunday).
     *
     * @return an array containing the start and end date-time of last week
     */
    public static String[] getLastWeekRange() {
        LocalDate today = LocalDate.now();
        LocalDateTime start = today.with(TemporalAdjusters.previousOrSame(DayOfWeek.MONDAY)).minusWeeks(1).atStartOfDay();
        LocalDateTime end = start.plusDays(6).with(LocalTime.MAX);
        return new String[]{formatDateTime(start), formatDateTime(end)};
    }

    /**
     * Gets the date range for the current month.
     *
     * @return an array containing the start and end date-time of this month
     */
    public static String[] getThisMonthRange() {
        LocalDateTime start = LocalDate.now().with(TemporalAdjusters.firstDayOfMonth()).atStartOfDay();
        LocalDateTime end = LocalDate.now().atTime(LocalTime.MAX);
        return new String[]{formatDateTime(start), formatDateTime(end)};
    }

    /**
     * Gets the date range for the last month.
     *
     * @return an array containing the start and end date-time of last month
     */
    public static String[] getLastMonthRange() {
        LocalDateTime start = LocalDate.now().minusMonths(1).with(TemporalAdjusters.firstDayOfMonth()).atStartOfDay();
        LocalDateTime end = LocalDate.now().minusMonths(1).with(TemporalAdjusters.lastDayOfMonth()).atTime(LocalTime.MAX);
        return new String[]{formatDateTime(start), formatDateTime(end)};
    }

    /**
     * Gets the date range for the current year.
     *
     * @return an array containing the start and end date-time of this year
     */
    public static String[] getThisYearRange() {
        LocalDateTime start = LocalDate.now().with(TemporalAdjusters.firstDayOfYear()).atStartOfDay();
        LocalDateTime end = LocalDate.now().atTime(LocalTime.MAX);
        return new String[]{formatDateTime(start), formatDateTime(end)};
    }

    /**
     * Gets the date range for the last year.
     *
     * @return an array containing the start and end date-time of last year
     */
    public static String[] getLastYearRange() {
        LocalDateTime start = LocalDate.now().minusYears(1).with(TemporalAdjusters.firstDayOfYear()).atStartOfDay();
        LocalDateTime end = LocalDate.now().minusYears(1).with(TemporalAdjusters.lastDayOfYear()).atTime(LocalTime.MAX);
        return new String[]{formatDateTime(start), formatDateTime(end)};
    }

    /**
     * Generates a date range for the last 7 days.
     *
     * @return an array of strings, where the first element is the start date and the second element is the end date
     */
    public static String[] getLast7DaysRange() {
        LocalDateTime start = LocalDate.now().minusDays(7).atStartOfDay();
        LocalDateTime end = LocalDate.now().atTime(LocalTime.MAX);
        return new String[]{formatDateTime(start), formatDateTime(end)};
    }

    /**
     * Generates a date range for the last 30 days.
     *
     * @return an array of strings, where the first element is the start date and the second element is the end date
     */
    public static String[] getLast30DaysRange() {
        LocalDateTime start = LocalDate.now().minusDays(30).atStartOfDay();
        LocalDateTime end = LocalDate.now().atTime(LocalTime.MAX);
        return new String[]{formatDateTime(start), formatDateTime(end)};
    }

    /**
     * Generates a random time range based on the current date, ensuring the start date
     * is earlier than the end date.
     *
     * @return an array containing the start and end date-time of the random range
     */
    public static String[] getCustomRange() {
        // Get the current date
        LocalDate currentDate = LocalDate.now();

        // Calculate start date
        LocalDate startDate = currentDate.minusDays(10);
        // Calculate end date based on the start date
        LocalDate endDate = startDate.plusDays(365); // Add random days to start date

        // Convert LocalDate to LocalDateTime for formatting
        LocalDateTime startDateTime = startDate.atStartOfDay();
        LocalDateTime endDateTime = endDate.atTime(LocalTime.MAX); // Set end time to the last moment of the end date

        return new String[]{formatDateTime(startDateTime), formatDateTime(endDateTime)};
    }
}
