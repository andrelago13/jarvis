package jarvis.events;

import jarvis.actions.CommandBuilder;
import jarvis.actions.command.definitions.Command;
import jarvis.controllers.definitions.Thing;
import jarvis.controllers.definitions.events.ThingEvent;
import jarvis.engine.JarvisEngine;
import jarvis.events.definitions.EventHandler;
import jarvis.listeners.EventConsumer;
import jarvis.util.JarvisException;
import jarvis.util.Temperature;
import java.text.NumberFormat;
import java.text.ParseException;
import java.util.Optional;
import org.json.JSONObject;
import res.Config;

public class ValueEventHandler extends EventHandler {

  public static enum Condition {
    LESS_THAN,
    EQUAL_TO,
    GREATER_THAN
  }

  public final static String TAG = "booleanEventHandler";

  public final static String KEY_TEMPERATURE = "value";
  public final static String KEY_CONDITION = "condition";

  public final Temperature temperature;
  public final Condition condition; // <0 - value less than, 0 - value equals, >0 - value greater than

  public ValueEventHandler(EventConsumer consumer, Command command, Temperature temperature, Condition condition) {
    super(TAG, consumer, command);
    this.temperature = temperature;
    this.condition = condition;
  }

  public ValueEventHandler(EventConsumer consumer, Command command, Temperature temperature, int condition) {
    this(consumer, command, temperature, Condition.values()[condition]);
  }

  public ValueEventHandler(JSONObject json) throws JarvisException {
    super(json);
    if (!TAG.equals(mTag)) {
      throw new JarvisException("JSON does not match ValueEventHandler");
    }

    try {
      temperature = Temperature.buildFromJSON(json.getJSONObject(KEY_TEMPERATURE)).get();
      int conditionValue = json.getInt(KEY_CONDITION);
      condition = Condition.values()[conditionValue];
    } catch (Exception e) {
      throw new JarvisException("Unable to create ValueEventHandler from JSON.");
    }
  }

  @Override
  public boolean handleMessage(Thing t, ThingEvent e, String message) {
    if (!t.getName().equals(eventConsumer.getThing().getName())) {
      return false;
    }

    try {
      switch (condition) {
        case EQUAL_TO:
          if (NumberFormat.getInstance().parse(message).doubleValue() == temperature.getValue()) {
            JarvisEngine.getInstance().executeCommand(command);
            // Every handler must perform logging
            log();
          }
          break;
        case LESS_THAN:
          if (NumberFormat.getInstance().parse(message).doubleValue() < temperature.getValue()) {
            JarvisEngine.getInstance().executeCommand(command);
            // Every handler must perform logging
            log();
          }
          break;
        case GREATER_THAN:
          if (NumberFormat.getInstance().parse(message).doubleValue() > temperature.getValue()) {
            JarvisEngine.getInstance().executeCommand(command);
            // Every handler must perform logging
            log();
          }
          break;
      }
    } catch (ParseException e1) {
      // do nothing
      return false;
    }
    return true;
  }

  @Override
  public String friendlyString() {
    StringBuilder builder = new StringBuilder();
    builder.append(eventConsumer.getThing().getName());
    builder.append(" value is ");
    switch (condition) {
      case EQUAL_TO:
        builder.append("equal to ");
        break;
      case LESS_THAN:
        builder.append("less than ");
        break;
      case GREATER_THAN:
        builder.append("greater than ");
        break;
    }
    builder.append(temperature.getValue());
    builder.append(" ");
    builder.append(temperature.getUnit().toString().toLowerCase());
    return builder.toString();
  }

  @Override
  public String friendlyStringWithCommand() {
    StringBuilder builder = new StringBuilder();
    builder.append(command.friendlyExecuteString());
    builder.append(" when the ");
    builder.append(friendlyString());
    return builder.toString();
  }

  @Override
  public boolean equals(EventHandler handler) {
    if (!super.equals(handler)) {
      return false;
    }

    if (!(handler instanceof ValueEventHandler)) {
      return false;
    }

    ValueEventHandler h = (ValueEventHandler) handler;

    return h.temperature.getValue() == temperature.getValue() && h.condition == condition;
  }

  @Override
  public JSONObject toJSON() {
    JSONObject result = super.toJSON();
    result.put(KEY_TEMPERATURE, temperature.toJSON());
    result.put(KEY_CONDITION, condition.ordinal());
    return result;
  }

  public static Optional<EventHandler> buildFromJSON(JSONObject json) {
    try {
      String tag = json.getString(KEY_TAG);
      if (!TAG.equals(tag)) {
        return Optional.empty();
      }

      Command cmd = CommandBuilder.buildFromJSON(json.getJSONObject(KEY_COMMAND));
      if (cmd == null) {
        return Optional.empty();
      }

      return Optional.of(new ValueEventHandler(json));
    } catch (Exception e) {
      // do nothing
    }

    return Optional.empty();
  }

  /**
   * Checks if the JSON has any "sensor_event_condition", returning the appropriate value.
   */
  public static Optional<Condition> parseConditionFromEvent(JSONObject json) {
    if (!json.has(Config.DF_SENSOR_EVENT_CONDITION_ENTITY_NAME)) {
      return Optional.empty();
    }
    JSONObject condition = json.getJSONObject(Config.DF_SENSOR_EVENT_CONDITION_ENTITY_NAME);
    if(condition.has(Config.DF_SENSOR_EVENT_CONDITION_EQUAL_ENTITY_NAME)) {
      return Optional.of(Condition.EQUAL_TO);
    } else if(condition.has(Config.DF_SENSOR_EVENT_CONDITION_GREATER_ENTITY_NAME)) {
      return Optional.of(Condition.GREATER_THAN);
    } else if(condition.has(Config.DF_SENSOR_EVENT_CONDITION_LESS_ENTITY_NAME)) {
      return Optional.of(Condition.LESS_THAN);
    }
    return Optional.empty();
  }

  public static Condition parseConditionFromEventWithDefault(JSONObject json, Condition defaultValue) {
    Optional<Condition> cond = parseConditionFromEvent(json);
    if(!cond.isPresent()) {
      return defaultValue;
    }
    return cond.get();
  }
}
