package jarvis.events;

import jarvis.actions.CommandBuilder;
import jarvis.actions.command.definitions.Command;
import jarvis.controllers.definitions.Thing;
import jarvis.controllers.definitions.events.ThingEvent;
import jarvis.controllers.definitions.properties.OnOffStatus;
import jarvis.engine.JarvisEngine;
import jarvis.events.definitions.EventHandler;
import jarvis.listeners.EventConsumer;
import jarvis.util.JarvisException;
import java.text.NumberFormat;
import java.text.ParseException;
import java.util.Optional;
import org.json.JSONObject;

public class ValueEventHandler extends EventHandler {

  public final static String TAG = "booleanEventHandler";

  public final static String KEY_VALUE = "value";
  public final static String KEY_CONDITION = "condition";

  public final double value;
  public final int condition; // <0 - value less than, 0 - value equals, >0 - value greater than

  public ValueEventHandler(EventConsumer consumer, Command command, double value, int condition) {
    super(TAG, consumer, command);
    this.value = value;
    this.condition = condition;
  }

  public ValueEventHandler(JSONObject json) throws JarvisException {
    super(json);
    if (!TAG.equals(mTag)) {
      throw new JarvisException("JSON does not match ValueEventHandler");
    }

    try {
      value = json.getDouble(KEY_VALUE);
      condition = json.getInt(KEY_CONDITION);
    } catch (Exception e) {
      throw new JarvisException("Unable to create ValueEventHandler from JSON.");
    }
  }

  @Override
  public boolean handleMessage(Thing t, ThingEvent e, String message) {
    // FIXME implement
    if (!t.getName().equals(eventConsumer.getThing().getName())) {
      return false;
    }

    try {
      if(condition < 0) {
        if (NumberFormat.getInstance().parse(message).doubleValue() < value) {
          JarvisEngine.getInstance().executeCommand(command);
          // Every handler must perform logging
          log();
        }
      } else if (condition > 0) {
        if (NumberFormat.getInstance().parse(message).doubleValue() > value) {
          JarvisEngine.getInstance().executeCommand(command);
          // Every handler must perform logging
          log();
        }
      } else {
        if (NumberFormat.getInstance().parse(message).doubleValue() == value) {
          JarvisEngine.getInstance().executeCommand(command);
          // Every handler must perform logging
          log();
        }
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
    if(condition < 0) {
      builder.append("less than ");
    } else if (condition > 0) {
      builder.append("greater than ");
    } else {
      builder.append("equal to ");
    }
    builder.append(value);
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
    // FIXME implement
    /*if (!super.equals(handler)) {
      return false;
    }

    if (!(handler instanceof ValueEventHandler)) {
      return false;
    }

    if (((ValueEventHandler) handler).value != value) {
      return false;
    }*/

    return true;
  }

  @Override
  public JSONObject toJSON() {
    JSONObject result = super.toJSON();
    result.put(KEY_VALUE, value);
    result.put(KEY_CONDITION, condition);
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
}
