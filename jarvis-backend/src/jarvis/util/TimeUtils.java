package jarvis.util;

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

    public static long timeUnitToSeconds(String unit, long value) {
        if(unitValues.containsKey(unit)) {
            TimeUnit timeUnit = unitValues.get(unit);
            return timeUnit.toSeconds(value);
        }
        if(daysConverter.containsKey(unit)) {
            int daysCount = daysConverter.get(unit);
            return TimeUnit.DAYS.toSeconds(value * daysCount);
        }

        return -1;
    }
}
