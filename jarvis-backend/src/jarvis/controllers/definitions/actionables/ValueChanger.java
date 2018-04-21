package jarvis.controllers.definitions.actionables;

import jarvis.controllers.definitions.properties.SensorValue;

public interface ValueChanger {
  public SensorValue getValue();
  public void setValue(SensorValue value);
}
