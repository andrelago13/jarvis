package jarvis.util;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.time.LocalTime;
import java.time.format.DateTimeFormatter;
import java.time.temporal.ChronoUnit;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.TimeUnit;

public class TimeUtils {
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

    public static TimeInfo dateTimeToInfo(String dateTime) {
        if (dateTime.length() > 20) {
            return null;
        }
        String cleanDateTime = dateTime.replace('T', ' ');
        cleanDateTime.substring(0, cleanDateTime.length() - 2);

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

    public static class TimeInfo {
        public final long value;
        public final TimeUnit unit;

        public TimeInfo(long value, TimeUnit unit) {
            this.value = value;
            this.unit = unit;
        }
    }
}
