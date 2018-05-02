package jarvis.controllers;

import com.sun.istack.internal.NotNull;
import com.sun.istack.internal.Nullable;
import jarvis.controllers.definitions.Thing;
import jarvis.controllers.definitions.ThingLinks;
import jarvis.controllers.definitions.events.ThingEvent;
import jarvis.controllers.definitions.properties.ThingProperty;
import jarvis.controllers.definitions.properties.ThingProperty.Type;
import jarvis.engine.ValueTracker;
import java.util.List;
import java.util.Optional;
import org.json.JSONObject;

public class BinarySensor extends Thing {

  protected static final String DEFAULT_DESCRIPTION = "Binary Sensor";
  protected static final String DEFAULT_PROPERTY_DESCRIPTION = "Is triggered (\"on\") if the sensor is activated";
  protected static final String DEFAULT_PROPERTIES_PATH = "properties";
  protected static final String DEFAULT_ACTIONS_PATH = "actions";
  protected static final String DEFAULT_EVENTS_PATH = "events";

  protected BinarySensor(@NotNull String name,
      @Nullable String description,
      @NotNull ThingLinks links,
      @NotNull List<ThingProperty> properties,
      @NotNull List<ThingEvent> events) {
    super(name, Type.BINARY_SENSOR, description, links, properties, events);
  }

  protected BinarySensor(@NotNull JSONObject json) {
    super(json);
    if (mType != Type.BINARY_SENSOR) {
      throw new IllegalArgumentException("Thing type for BinarySensor must be binarySensor.");
    }
  }

  protected BinarySensor(@NotNull BinarySensor t) {
    super(t);
  }

  public Optional<Boolean> isOn() {
    ValueTracker valueTracker = ValueTracker.getInstance();
    return valueTracker.getValueBoolean(mName);
  }

  public static class Builder extends Thing.Builder {

    public Builder() {
      super();
    }

    public static Builder getDefaultBuilder(String name, String basePath) {
      Builder builder = new Builder();

      builder.setName(name);
      builder.setDescription(DEFAULT_DESCRIPTION);
      ThingProperty statusProperty = new ThingProperty("value", ThingProperty.Type.BOOLEAN);
      statusProperty.setDescription(DEFAULT_PROPERTY_DESCRIPTION);
      statusProperty.setHref(basePath + '/' + name + '/' + DEFAULT_PROPERTIES_PATH + '/' + "value");
      builder.addProperty(statusProperty);
      builder.addEvent(new ThingEvent(ThingEvent.Type.TRIGGER, "trigger", "boolean",
          "If sensor is activated", basePath + '/' + name + '/' + DEFAULT_EVENTS_PATH + '/' +
          ThingEvent.Type.TRIGGER.toString().toLowerCase()));
      ThingLinks.Builder linksBuilder = new ThingLinks.Builder();
      linksBuilder.setProperties(basePath + '/' + name + '/' + DEFAULT_PROPERTIES_PATH);
      linksBuilder.setActions(basePath + '/' + name + '/' + DEFAULT_ACTIONS_PATH);
      linksBuilder.setEvents(basePath + '/' + name + '/' + DEFAULT_EVENTS_PATH);
      builder.setLinks(linksBuilder.build());

      return builder;
    }

    @Override
    public BinarySensor build() {
      return new BinarySensor(mName, mDescription, mLinks, mProperties, mEvents);
    }

    public static BinarySensor buildFromCopy(BinarySensor t) {
      return new BinarySensor(t);
    }

    public static BinarySensor buildFromJSON(JSONObject json) {
      return new BinarySensor(json);
    }
  }
}
