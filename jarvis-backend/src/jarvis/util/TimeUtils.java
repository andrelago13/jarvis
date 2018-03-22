package jarvis.util;

import org.json.JSONObject;
import res.Config;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.time.*;
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
    public static final SimpleDateFormat TIME_FORMATTER =
            new SimpleDateFormat("HH:mm:ss a", Locale.ENGLISH);

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

    public static final String KEY_DATETIME = "date-time"; // tomorrow, on Jan 20th
    public static final String KEY_DURATION = "duration"; // in X minutes
    public static final String KEY_TIME = "time"; // at X
    public static final String KEY_AMOUNT = "amount"; // at X
    public static final String KEY_UNIT = "unit"; // at X

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

    protected static Date[] parsePeriod(String dateTime) {
        if (dateTime.length() == LENGTH_DATE_PERIOD) {
            return dateTimeToDatePeriod(dateTime);
        } else if (dateTime.length() == LENGTH_TIME_PERIOD) {
            return timeToDatePeriod(dateTime);
        }
        return null;
    }

    protected static Date[] dateTimeToDatePeriod(String dateTime) {
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

    protected static Date[] timeToDatePeriod(String period) {
        // "12:00:00/16:00:00"
        LocalTime[] times = timeToPeriod(period);
        if (times == null) {
            return null;
        }

        LocalTime time1 = times[0];
        LocalTime time2 = times[1];

        Calendar cal1 = Calendar.getInstance();
        cal1.set(Calendar.HOUR_OF_DAY, time1.get(ChronoField.CLOCK_HOUR_OF_DAY));
        cal1.set(Calendar.MINUTE, time1.get(ChronoField.MINUTE_OF_HOUR));
        cal1.set(Calendar.SECOND, time1.get(ChronoField.SECOND_OF_MINUTE));
        cal1.set(Calendar.MILLISECOND, 0);

        Calendar cal2 = Calendar.getInstance();
        cal2.set(Calendar.HOUR_OF_DAY, time2.get(ChronoField.CLOCK_HOUR_OF_DAY));
        cal2.set(Calendar.MINUTE, time2.get(ChronoField.MINUTE_OF_HOUR));
        cal2.set(Calendar.SECOND, time2.get(ChronoField.SECOND_OF_MINUTE));
        cal2.set(Calendar.MILLISECOND, 0);

        return new Date[]{cal1.getTime(), cal2.getTime()};
    }

    protected static LocalTime[] timeToPeriod(String period) {
        // "12:00:00/16:00:00"
        if (period.length() != LENGTH_TIME_PERIOD) {
            return null;
        }
        String[] times = period.split("/");
        if (times.length != 2) {
            return null;
        }

        LocalTime time1 = LocalTime.parse(times[0], DateTimeFormatter.ofPattern("HH:mm:ss"));
        LocalTime time2 = LocalTime.parse(times[1], DateTimeFormatter.ofPattern("HH:mm:ss"));

        return new LocalTime[]{time1, time2};
    }

    protected static TimeInfo dateTimeToInfo(String dateTime) {
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

    public static String friendlyFormatPeriod(long startDate, long endDate) {
        Date start = new Date(startDate);
        Date end = new Date(endDate);
        Calendar c = Calendar.getInstance();
        c.setTime(start);
        int timeId = c.get(Calendar.DAY_OF_YEAR);
        c.setTime(end);

        if (timeId == c.get(Calendar.DAY_OF_YEAR)) {
            String result = "from " + friendlyFormat(startDate) + " to ";
            result += TIME_FORMATTER.format(endDate);
            return result;
        } else {
            return "from " + friendlyFormat(startDate) + " to " + friendlyFormat(endDate);
        }
    }

    public static long calculateTargetTimestamp(TimeInfo info) {
        return System.currentTimeMillis() + info.unit.toMillis(info.value);
    }

    public static TimeInfo parseTimeToSecondsFromNow(JSONObject time) {
        if (time.has(KEY_DATETIME)) {
            // "2017-07-12T16:30:00Z"
            return dateTimeToInfo(time.getString(KEY_DATETIME));
        } else if (time.has(KEY_DURATION)) {
            // {"amount":10,"unit":"min"}
            JSONObject duration = time.getJSONObject(KEY_DURATION);
            if (duration.has(KEY_AMOUNT) && duration.has(KEY_UNIT)) {
                return timeUnitToInfo(duration.getString(KEY_UNIT), duration.getInt(KEY_AMOUNT));
            }
        } else if (time.has(KEY_TIME)) {
            // "16:30:00"
            return dayTimeToInfo(time.getString(KEY_TIME));
        }
        return null;
    }

    public static TimeInfoOrPeriod parseTimeValueFromNow(JSONObject json) {
        if (json.has(KEY_DATETIME)) {
            String datetime = json.getString(KEY_DATETIME);
            if (datetime.length() == LENGTH_DATE_PERIOD) {
                Date[] dates = TimeUtils.parsePeriod(datetime);
                if (dates != null) {
                    return new TimeInfoOrPeriod(dates);
                }
            }
        }
        return new TimeInfoOrPeriod(TimeUtils.parseTimeToSecondsFromNow(json));
    }

    public static LocalTime[] parseTimeOrTimePeriod(JSONObject json) {
        //time-period: "12:00:00/16:00:00"
        if (json.has(Config.DF_TIME_PERIOD_SYS_ENTITY_NAME)) {
            LocalTime[] times = timeToPeriod(json.getString(Config.DF_TIME_PERIOD_SYS_ENTITY_NAME));
            if (times == null || times.length != 2) {
                return null;
            }
            return times;
        }

        //start / end: 2x"16:30:00"
        if (json.has(Config.DF_STARTTIME_ENTITY_NAME) && json.has(Config.DF_ENDTIME_ENTITY_NAME)) {
            LocalTime time1 = LocalTime.parse(json.getString(Config.DF_STARTTIME_ENTITY_NAME),
                    DateTimeFormatter.ofPattern("HH:mm:ss"));
            LocalTime time2 = LocalTime.parse(json.getString(Config.DF_ENDTIME_ENTITY_NAME),
                    DateTimeFormatter.ofPattern("HH:mm:ss"));
            return new LocalTime[]{time1, time2};
        }

        //time: "16:30:00"
        if (json.has(Config.DF_TIME_ENTITY_NAME)) {
            return new LocalTime[]{parseTime(json.getString(Config.DF_TIME_ENTITY_NAME))};
        }

        return null;
    }

    public static LocalTime parseTime(String time) {
        return LocalTime.parse(time, DateTimeFormatter.ofPattern("HH:mm:ss"));
    }

    public static String localTimeToString(LocalTime time) {
        StringBuilder builder = new StringBuilder();
        builder.append(String.format("%02d", time.get(ChronoField.CLOCK_HOUR_OF_DAY)));
        builder.append(":");
        builder.append(String.format("%02d", time.get(ChronoField.MINUTE_OF_HOUR)));
        builder.append(":");
        builder.append(String.format("%02d", time.get(ChronoField.SECOND_OF_MINUTE)));
        return builder.toString();
    }

    public static long calculateSecondsToLocalTime(LocalTime desiredTime) {
        LocalDateTime localNow = LocalDateTime.now();
        ZoneId currentZone = ZoneId.systemDefault();
        ZonedDateTime zonedNow = ZonedDateTime.of(localNow, currentZone);
        ZonedDateTime zonedDesiredTime;
        zonedDesiredTime = zonedNow.withHour(desiredTime.getHour())
                .withMinute(desiredTime.getMinute())
                .withSecond(desiredTime.getSecond());

        long duration = Duration.between(zonedNow, zonedDesiredTime).getSeconds();
        if(duration < 0) {
            duration += TimeUnit.DAYS.toSeconds(1);
        }
        return duration;
    }

    ////////////////////////////////////////////////////////////////////

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

    public static class TimeInfoOrPeriod {
        private TimeInfo mTimeInfo;
        private Date[] mDates;

        public TimeInfoOrPeriod(TimeInfo info) {
            mTimeInfo = info;
        }

        public TimeInfoOrPeriod(Date[] dates) {
            mDates = dates;
        }

        public TimeInfoOrPeriod(Date date1, Date date2) {
            mDates = new Date[]{date1, date2};
        }

        public TimeInfo getTimeInfo() {
            return mTimeInfo;
        }

        public Date[] getDates() {
            return mDates;
        }

        public boolean hasTimeInfo() {
            return mTimeInfo != null;
        }

        public boolean hasDates() {
            return mDates != null && mDates.length == 2;
        }
    }
}
