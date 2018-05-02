package jarvis.controllers;

import com.sun.istack.internal.NotNull;
import com.sun.istack.internal.Nullable;
import jarvis.communication.ThingInterface;
import jarvis.controllers.definitions.Thing;
import jarvis.controllers.definitions.ThingLinks;
import jarvis.controllers.definitions.actionables.Toggleable;
import jarvis.controllers.definitions.events.ThingEvent;
import jarvis.controllers.definitions.properties.ThingProperty;
import jarvis.engine.ValueTracker;
import java.util.List;
import java.util.Optional;
import org.json.JSONObject;

public class OnOffSwitch extends Thing implements Toggleable {

  protected static final String DEFAULT_DESCRIPTION = "On/Off switch";
  protected static final String DEFAULT_PROPERTY_DESCRIPTION = "Describes current state of the switch (true=on)";
  protected static final String DEFAULT_PROPERTIES_PATH = "properties";
  protected static final String DEFAULT_ACTIONS_PATH = "actions";
  protected static final String DEFAULT_EVENTS_PATH = "events";

  protected OnOffSwitch(@NotNull String name,
      @Nullable String description,
      @NotNull ThingLinks links,
      @NotNull List<ThingProperty> properties,
      @NotNull List<ThingEvent> events) {
    super(name, Type.ON_OFF_SWITCH, description, links, properties, events);
  }

  protected OnOffSwitch(@NotNull JSONObject json) {
    super(json);
    if (mType != Type.ON_OFF_SWITCH) {
      throw new IllegalArgumentException("Thing type for OnOffSwitch must be onOffSwitch.");
    }
  }

  protected OnOffSwitch(@NotNull OnOffSwitch t) {
    super(t);
  }

  @Override
  public Optional<Boolean> turnOn() {
    ValueTracker.getInstance().setValue(mName, true);
    return Optional.of(ThingInterface.sendThingsMessage(mLinks.getActions().get(), "on"));
  }

  @Override
  public Optional<Boolean> turnOff() {
    ValueTracker.getInstance().setValue(mName, false);
    return Optional.of(ThingInterface.sendThingsMessage(mLinks.getActions().get(), "off"));
  }

  @Override
  public Optional<Boolean> toggle() {
    Optional<Boolean> status = ValueTracker.getInstance().getValueBoolean(mName);
    if(status.isPresent()) {
      if(status.get()) {
        // is on, will turn off
        return turnOff();
      } else {
        // is off, will turn on
        return turnOn();
      }
    }

    return Optional.empty();
  }

  @Override
  public Optional<Boolean> isOn() {
    return ValueTracker.getInstance().getValueBoolean(mName);
  }

  public static class Builder extends Thing.Builder {

    public Builder() {
      super();
    }

    public static Builder getDefaultBuilder(String name, String basePath) {
      Builder builder = new Builder();

      builder.setName(name);
      builder.setDescription(DEFAULT_DESCRIPTION);
      ThingProperty statusProperty = new ThingProperty("status", ThingProperty.Type.BOOLEAN);
      statusProperty.setDescription(DEFAULT_PROPERTY_DESCRIPTION);
      builder.addProperty(statusProperty);
      builder.addEvent(new ThingEvent(ThingEvent.Type.ON_OFF, "boolean", "boolean",
          "Whether switch is on", basePath + '/' + name + '/' + DEFAULT_EVENTS_PATH + '/' +
          ThingEvent.Type.ON_OFF.toString().toLowerCase()));
      ThingLinks.Builder linksBuilder = new ThingLinks.Builder();
      linksBuilder.setProperties(basePath + '/' + name + '/' + DEFAULT_PROPERTIES_PATH);
      linksBuilder.setActions(basePath + '/' + name + '/' + DEFAULT_ACTIONS_PATH);
      linksBuilder.setEvents(basePath + '/' + name + '/' + DEFAULT_EVENTS_PATH);
      builder.setLinks(linksBuilder.build());

      return builder;
    }

    @Override
    public OnOffSwitch build() {
      return new OnOffSwitch(mName, mDescription, mLinks, mProperties, mEvents);
    }

    public static OnOffSwitch buildFromCopy(OnOffSwitch t) {
      return new OnOffSwitch(t);
    }

    public static OnOffSwitch buildFromJSON(JSONObject json) {
      return new OnOffSwitch(json);
    }
  }
}
