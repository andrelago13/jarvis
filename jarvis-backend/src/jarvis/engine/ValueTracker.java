package jarvis.engine;

import java.text.NumberFormat;
import java.util.HashMap;
import java.util.Map;
import java.util.Optional;

public class ValueTracker {
  private static ValueTracker instance;

  private Map<String, Object> values;

  private ValueTracker() {
    values = new HashMap<>();
  }

  public static ValueTracker getInstance() {
    if(instance == null) {
      instance = new ValueTracker();
    }
    return instance;
  }

  public Optional<Object> getValue(String thingName) {
    Object val = values.get(thingName);
    if(val == null) {
      return Optional.empty();
    }
    return Optional.of(val) ;
  }

  public void setValue(String thingName, Object value) {
    values.put(thingName, value);
  }

  public Optional<Number> getValueNumber(String thingName) {
    Object value = values.get(thingName);
    if(value == null) {
      return Optional.empty();
    }

    if(value instanceof String) {
      try {
        return Optional.of(NumberFormat.getInstance().parse((String)value));
      } catch (Exception e) {
        return Optional.empty();
      }
    } else if (value instanceof Number) {
      return Optional.of((Number) value);
    }
    return Optional.empty();
  }

  public Optional<Integer> getValueInteger(String thingName) {
    Object value = values.get(thingName);
    if(value == null) {
      return Optional.empty();
    }

    if(value instanceof String) {
      try {
        return Optional.of(NumberFormat.getInstance().parse((String)value).intValue());
      } catch (Exception e) {
        return Optional.empty();
      }
    } else if (value instanceof Number) {
      Number num = (Number) value;
      return Optional.of(num.intValue());
    }
    return Optional.empty();
  }

  public Optional<Double> getValueDouble(String thingName) {
    Object value = values.get(thingName);
    if(value == null) {
      return Optional.empty();
    }

    if(value instanceof String) {
      try {
        return Optional.of(NumberFormat.getInstance().parse((String)value).doubleValue());
      } catch (Exception e) {
        return Optional.empty();
      }
    } else if (value instanceof Number) {
      Number num = (Number) value;
      return Optional.of(num.doubleValue());
    }
    return Optional.empty();
  }
}
