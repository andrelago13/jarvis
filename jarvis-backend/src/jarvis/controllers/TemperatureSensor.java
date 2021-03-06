package jarvis.controllers;

import com.sun.istack.internal.NotNull;
import com.sun.istack.internal.Nullable;
import jarvis.controllers.definitions.Thing;
import jarvis.controllers.definitions.ThingLinks;
import jarvis.controllers.definitions.events.ThingEvent;
import jarvis.controllers.definitions.properties.ThingProperty;
import jarvis.engine.ValueTracker;
import java.util.List;
import java.util.Optional;
import org.json.JSONObject;

public class TemperatureSensor extends Thing {

  private static final String DEFAULT_DESCRIPTION = "Temperature Sensor";
  private static final String DEFAULT_PROPERTY_DESCRIPTION = "Describes current temperature in Celsius";
  private static final String DEFAULT_PROPERTIES_PATH = "properties";
  private static final String DEFAULT_ACTIONS_PATH = "actions";
  private static final String DEFAULT_EVENTS_PATH = "events";

  protected TemperatureSensor(@NotNull String name,
      @Nullable String description,
      @NotNull ThingLinks links,
      @NotNull List<ThingProperty> properties,
      @NotNull List<ThingEvent> events) {
    super(name, Type.TEMPERATURE_SENSOR, description, links, properties, events);
  }

  protected TemperatureSensor(@NotNull JSONObject json) {
    super(json);
    if (mType != Type.TEMPERATURE_SENSOR) {
      throw new IllegalArgumentException("Thing type for TemperatureSensor must be temperatureSensor.");
    }
  }

  protected TemperatureSensor(@NotNull TemperatureSensor t) {
    super(t);
  }

  public Optional<Double> getTemperature() {
    ValueTracker valueTracker = ValueTracker.getInstance();
    return valueTracker.getValueDouble(mName);
  }

  public static class Builder extends Thing.Builder {

    public Builder() {
      super();
    }

    public static Builder getDefaultBuilder(String name, String basePath) {
      Builder builder = new Builder();

      builder.setName(name);
      builder.setDescription(DEFAULT_DESCRIPTION);
      ThingProperty statusProperty = new ThingProperty("value", ThingProperty.Type.NUMBER);
      statusProperty.setDescription(DEFAULT_PROPERTY_DESCRIPTION);
      statusProperty.setHref(basePath + '/' + name + '/' + DEFAULT_PROPERTIES_PATH + '/' + "value");
      builder.addProperty(statusProperty);
      builder.addEvent(new ThingEvent(ThingEvent.Type.VALUE, "temperature", "celsius",
          "Temperature value", basePath + '/' + name + '/' + DEFAULT_EVENTS_PATH + '/' +
          ThingEvent.Type.VALUE.toString().toLowerCase()));
      ThingLinks.Builder linksBuilder = new ThingLinks.Builder();
      linksBuilder.setProperties(basePath + '/' + name + '/' + DEFAULT_PROPERTIES_PATH);
      linksBuilder.setActions(basePath + '/' + name + '/' + DEFAULT_ACTIONS_PATH);
      linksBuilder.setEvents(basePath + '/' + name + '/' + DEFAULT_EVENTS_PATH);
      builder.setLinks(linksBuilder.build());

      return builder;
    }

    @Override
    public TemperatureSensor build() {
      return new TemperatureSensor(mName, mDescription, mLinks, mProperties, mEvents);
    }

    public static TemperatureSensor buildFromCopy(TemperatureSensor t) {
      return new TemperatureSensor(t);
    }

    public static TemperatureSensor buildFromJSON(JSONObject json) {
      return new TemperatureSensor(json);
    }
  }
}
