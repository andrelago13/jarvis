package jarvis.util;

import java.util.HashMap;
import java.util.Map;
import java.util.Set;

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
    if (typeValues.containsKey(type.ordinal())) {
      return typeValues.get(type.ordinal());
    }
    return typeValues.get(UnitType.OTHER.ordinal());
  }

  public static UnitType getUnitTypeForString(String type) {
    Set<Integer> keys = typeValues.keySet();
    for (Integer k : keys) {
      if (typeValues.get(k).equals(type)) {
        return UnitType.values()[k];
      }
    }
    return UnitType.OTHER;
  }
}
