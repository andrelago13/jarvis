package jarvis.util;

import java.util.HashMap;
import java.util.Map;

public class Unit {
    public enum UnitType {
        CELSIUS,
        PERCENT,
        OTHER
    }

    public static final Map<Integer, String> typeValues = new HashMap<>();
    static {
        typeValues.put(UnitType.CELSIUS.ordinal(), "celsius");
        typeValues.put(UnitType.PERCENT.ordinal(), "percent");
        typeValues.put(UnitType.OTHER.ordinal(), "other");
    }

    public static String UnitTypeValue(UnitType type) {
        if(typeValues.containsKey(type.ordinal())) {
            return typeValues.get(type.ordinal());
        }
        return typeValues.get(UnitType.OTHER.ordinal());
    }
}
