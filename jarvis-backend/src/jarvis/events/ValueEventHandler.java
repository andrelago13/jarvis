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
import java.util.Optional;
import org.json.JSONObject;

public class ValueEventHandler extends EventHandler {

  public final static String TAG = "booleanEventHandler";

  public final static String KEY_VALUE = "value";
  public final static String KEY_CONDITION = "condition";

  public final boolean value;
  public final int condition; // <0 - value less than, 0 - value equals, >0 - value greater than

  public ValueEventHandler(EventConsumer consumer, Command command, boolean value, int condition) {
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
      value = json.getBoolean(KEY_VALUE);
      condition = json.getInt(KEY_CONDITION);
    } catch (Exception e) {
      throw new JarvisException("Unable to create ValueEventHandler from JSON.");
    }
  }

  @Override
  public boolean handleMessage(Thing t, ThingEvent e, String message) {
    // FIXME implement
    /*if (!t.getName().equals(eventConsumer.getThing().getName())) {
      return false;
    }

    if (OnOffStatus.isValueEqualToBoolean(message, value)) {
      JarvisEngine.getInstance().executeCommand(command);
      // Every handler must perform logging
      log();
    }*/
    return true;
  }

  @Override
  public String friendlyString() {
    // FIXME implement
    /*StringBuilder builder = new StringBuilder();
    builder.append(eventConsumer.getThing().getName());
    builder.append(" is ");
    builder.append(OnOffStatus.getValueString(value));
    return builder.toString();*/
  }

  @Override
  public String friendlyStringWithCommand() {
    // FIXME implement
    /*StringBuilder builder = new StringBuilder();
    builder.append(command.friendlyExecuteString());
    builder.append(" when ");
    builder.append(eventConsumer.getThing().getName());
    builder.append(" is ");
    builder.append(OnOffStatus.getValueString(value));
    return builder.toString();*/
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
