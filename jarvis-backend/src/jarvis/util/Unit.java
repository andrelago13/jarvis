package jarvis.util;

public class Unit {
    public enum UnitType {
        CELSIUS,
        PERCENT,
        OTHER
    }

    public static final String TYPE_CELSIUS = "celsius";
    public static final String TYPE_PERCENT = "percent";
    public static final String TYPE_OTHER = "other";

    public static String UnitTypeValue(UnitType type) {
        switch (type) {
            case CELSIUS:
                return TYPE_CELSIUS;
            case PERCENT:
                return TYPE_OTHER;
        }
        return TYPE_OTHER;
    }
}
