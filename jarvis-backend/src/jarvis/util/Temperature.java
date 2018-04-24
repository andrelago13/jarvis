package jarvis.util;

import java.util.HashMap;
import java.util.Map;
import java.util.Optional;
import java.util.Set;
import org.json.JSONObject;

public class Temperature {
  public static final String KEY_TEMPERATURE = "temperature";
  public static final String KEY_AMOUNT = "amount";
  public static final String KEY_UNIT = "unit";

  public static enum Unit {
    CELSIUS,
    FAHRENHEIT
  }

  private static final Map<Unit, String> unitValues = new HashMap<>();
  static {
    unitValues.put(Unit.CELSIUS, "C");
    unitValues.put(Unit.FAHRENHEIT, "F");
  }

  private double mValue;
  private Unit mUnit;

  public Temperature(double value, Unit unit) {
    mValue = value;
    mUnit = unit;
  }

  public double getValue() {
    return mValue;
  }

  public Unit getUnit() {
    return mUnit;
  }

  public void setValue(double value) {
    mValue = value;
  }

  public void setUnit(Unit unit) {
    mUnit = unit;
  }

  public static Optional<Unit> getUnitFromString(String unit) {
    Set<Unit> keys = unitValues.keySet();
    for(Unit k : keys) {
      if(unitValues.get(k).equals(unit)) {
        return Optional.of(k);
      }
    }
    return Optional.empty();
  }

  public static Optional<Temperature> buildFromJSON(JSONObject json) {
    if(!json.has(KEY_AMOUNT) || !json.has(KEY_UNIT)) {
      return Optional.empty();
    }
    double amount = json.getDouble(KEY_AMOUNT);
    String unitString = json.getString(KEY_UNIT);
    Optional<Unit> unit = getUnitFromString(unitString);
    if(!unit.isPresent()) {
      return Optional.empty();
    }

    return Optional.of(new Temperature(amount, unit.get()));
  }

  public JSONObject toJSON() {
    JSONObject result = new JSONObject();
    result.put(KEY_AMOUNT, mValue);
    result.put(KEY_UNIT, unitValues.get(mUnit));
    return result;
  }
}
