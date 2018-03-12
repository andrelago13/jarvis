package jarvis.util;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.time.LocalTime;
import java.time.format.DateTimeFormatter;
import java.time.temporal.ChronoField;
import java.time.temporal.ChronoUnit;
import java.util.*;
import java.util.concurrent.TimeUnit;

public class TimeUtils {
    public static final int LENGTH_TIME_PERIOD = 17;
    public static final int LENGTH_DATE_PERIOD = 41;
    public static final int LENGTH_DATETIME = 20;

    public static final SimpleDateFormat FRIENDLY_FORMATTER =
            new SimpleDateFormat("EEEE MMMM dd, HH:mm:ss a", Locale.ENGLISH);

    public static final String UNIT_SECOND = "s";
    public static final String UNIT_MINUTE = "min";
    public static final String UNIT_HOUR = "h";
    public static final String UNIT_DAY = "day";
    public static final String UNIT_WEEK = "wk";
    public static final String UNIT_FORTNIGHT = "fortnight";
    public static final String UNIT_MONTH = "mo";
    public static final String UNIT_YEAR = "yr";
    public static final String UNIT_DECADE = "decade";
    public static final String UNIT_CENTURY = "century";

    private static final Map<String, TimeUnit> unitValues = new HashMap<>();

    static {
        unitValues.put(UNIT_SECOND, TimeUnit.SECONDS);
        unitValues.put(UNIT_MINUTE, TimeUnit.MINUTES);
        unitValues.put(UNIT_HOUR, TimeUnit.HOURS);
        unitValues.put(UNIT_DAY, TimeUnit.DAYS);
    }

    private static final Map<String, Integer> daysConverter = new HashMap<>();

    static {
        daysConverter.put(UNIT_WEEK, 7);
        daysConverter.put(UNIT_FORTNIGHT, 14);
    }

    public static TimeInfo timeUnitToInfo(String unit, long value) {
        if (unitValues.containsKey(unit)) {
            TimeUnit timeUnit = unitValues.get(unit);
            return new TimeInfo(timeUnit.toSeconds(value), TimeUnit.SECONDS);
        }
        if (daysConverter.containsKey(unit)) {
            int daysCount = daysConverter.get(unit);
            return new TimeInfo(TimeUnit.DAYS.toSeconds(value * daysCount), TimeUnit.SECONDS);
        }

        return null;
    }

    public static TimeInfo dayTimeToInfo(String time) {
        LocalTime localTime = LocalTime.parse(time, DateTimeFormatter.ofPattern("HH:mm:ss"));
        return new TimeInfo(LocalTime.now().until(localTime, ChronoUnit.SECONDS), TimeUnit.SECONDS);
    }

    public static Date[] parsePeriod(String dateTime) {
        if(dateTime.length() == LENGTH_DATE_PERIOD) {
            return dateTimeToDatePeriod(dateTime);
        } else if (dateTime.length() == LENGTH_TIME_PERIOD) {
            return dateTimeToPeriod(dateTime);
        }
        return null;
    }

    public static Date[] dateTimeToDatePeriod(String dateTime) {
        // "2017-07-12T12:00:00Z/2017-07-12T16:00:00Z"
        if (dateTime.length() != LENGTH_DATE_PERIOD) {
            return null;
        }

        String[] dates = dateTime.split("/");
        if (dates.length != 2) {
            return null;
        }
        String startDateStr = dates[0].replace('T', ' ');
        String endDateStr = dates[1].replace('T', ' ');
        startDateStr = startDateStr.substring(0, startDateStr.length() - 2);
        endDateStr = endDateStr.substring(0, endDateStr.length() - 2);

        SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd hh:mm:ss");
        try {
            Date startDate = dateFormat.parse(startDateStr);
            Date endDate = dateFormat.parse(endDateStr);
            return new Date[]{startDate, endDate};
        } catch (ParseException e) {
            e.printStackTrace();
            AdminAlertUtil.alertUnexpectedException(e);
        }
        return null;
    }

    public static Date[] dateTimeToPeriod(String period) {
        // "12:00:00/16:00:00"
        if (period.length() != LENGTH_TIME_PERIOD) {
            return null;
        }
        String[] times = period.split("/");
        if (times.length != 2) {
            return null;
        }

        LocalTime time1 = LocalTime.parse(times[0], DateTimeFormatter.ofPattern("HH:mm:ss"));
        int hour1 = time1.get(ChronoField.CLOCK_HOUR_OF_DAY);
        int minute1 = time1.get(ChronoField.MINUTE_OF_HOUR);
        int second1 = time1.get(ChronoField.SECOND_OF_MINUTE);

        LocalTime time2 = LocalTime.parse(times[1], DateTimeFormatter.ofPattern("HH:mm:ss"));
        int hour2 = time2.get(ChronoField.CLOCK_HOUR_OF_DAY);
        int minute2 = time2.get(ChronoField.MINUTE_OF_HOUR);
        int second2 = time2.get(ChronoField.SECOND_OF_MINUTE);

        Calendar cal1 = Calendar.getInstance();
        cal1.set(Calendar.HOUR_OF_DAY, hour1);
        cal1.set(Calendar.MINUTE, minute1);
        cal1.set(Calendar.SECOND, second1);
        cal1.set(Calendar.MILLISECOND, 0);

        Calendar cal2 = Calendar.getInstance();
        cal2.set(Calendar.HOUR_OF_DAY, hour2);
        cal2.set(Calendar.MINUTE, minute2);
        cal2.set(Calendar.SECOND, second2);
        cal2.set(Calendar.MILLISECOND, 0);

        return new Date[] {cal1.getTime(), cal2.getTime()};
    }

    public static TimeInfo dateTimeToInfo(String dateTime) {
        // "2017-07-12T12:00:00Z"
        if (dateTime.length() != LENGTH_DATETIME) {
            return null;
        }
        String cleanDateTime = dateTime.replace('T', ' ');
        cleanDateTime = cleanDateTime.substring(0, cleanDateTime.length() - 2);

        SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd hh:mm:ss");
        try {
            Date parsedDate = dateFormat.parse(cleanDateTime);
            Date now = new Date();
            long seconds = TimeUnit.MILLISECONDS.toSeconds(parsedDate.getTime() - now.getTime());
            return new TimeInfo(seconds, TimeUnit.SECONDS);
        } catch (ParseException e) {
            e.printStackTrace();
            AdminAlertUtil.alertUnexpectedException(e);
        }

        return null;
    }

    public static String friendlyFormat(long date) {
        return FRIENDLY_FORMATTER.format(new Date(date));
    }

    public static String friendlyFormat(TimeInfo timeInfo) {
        if (timeInfo.value == 0) {
            return "0 seconds";
        }
        long totalSeconds = timeInfo.unit.toSeconds(timeInfo.value);

        long seconds = totalSeconds;
        long minutes = 0;
        long hours = 0;
        long days = 0;
        if (seconds > 59) {
            minutes = TimeUnit.SECONDS.toMinutes(seconds);
            seconds = seconds - minutes * 60;
            if (minutes > 59) {
                hours = TimeUnit.MINUTES.toHours(minutes);
                minutes = minutes - hours * 60;
                if (hours > 23) {
                    days = TimeUnit.HOURS.toDays(hours);
                    hours = hours - days * 24;
                }
            }
        }

        StringBuilder result = new StringBuilder();
        if (days > 1) {
            result.append(days);
            result.append(" day");
            result.append((days == 1 ? " " : "s "));
        }
        if (hours > 1) {
            result.append(hours);
            result.append(" hour");
            result.append((hours == 1 ? " " : "s "));
        }
        if (minutes > 1) {
            result.append(minutes);
            result.append(" minute");
            result.append((minutes == 1 ? " " : "s "));
        }
        if (seconds > 1) {
            result.append(seconds);
            result.append(" second");
            result.append((seconds == 1 ? " " : "s "));
        }
        return result.toString();
    }

    public static long calculateTargetTimestamp(TimeInfo info) {
        return System.currentTimeMillis() + info.unit.toMillis(info.value);
    }

    public static class TimeInfo {
        public final long value;
        public final TimeUnit unit;

        public TimeInfo(long value, TimeUnit unit) {
            this.value = value;
            this.unit = unit;
        }

        public String toString() {
            return "" + value + " " + unit.toString().toLowerCase();
        }
    }
}
