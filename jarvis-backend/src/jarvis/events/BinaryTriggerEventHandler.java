package jarvis.events;

import jarvis.actions.CommandBuilder;
import jarvis.actions.command.definitions.Command;
import jarvis.controllers.definitions.Thing;
import jarvis.controllers.definitions.events.ThingEvent;
import jarvis.controllers.definitions.properties.OnOffStatus;
import jarvis.engine.JarvisEngine;
import jarvis.engine.ValueTracker;
import jarvis.events.definitions.EventHandler;
import jarvis.listeners.EventConsumer;
import jarvis.util.JarvisException;
import java.util.Optional;
import org.json.JSONObject;

public class BinaryTriggerEventHandler extends EventHandler {

  public final static String TAG = "binaryTriggerEventHandler";

  public final static String KEY_CONDITION = "condition";

  public final boolean condition; // false - expects trigger off; true - expects trigger on

  public BinaryTriggerEventHandler(EventConsumer consumer, Command command, boolean condition) {
    super(TAG, consumer, command);
    this.condition = condition;
  }

  public BinaryTriggerEventHandler(JSONObject json) throws JarvisException {
    super(json);
    if (!TAG.equals(mTag)) {
      throw new JarvisException("JSON does not match BinaryTriggerEventHandler");
    }

    try {
      condition = json.getBoolean(KEY_CONDITION);
    } catch (Exception e) {
      throw new JarvisException("Unable to create ValueEventHandler from JSON.");
    }
  }

  @Override
  public boolean handleMessage(Thing t, ThingEvent e, String message) {
    if (!t.getName().equals(eventConsumer.getThing().getName())) {
      return false;
    }

    boolean currentValue = OnOffStatus.isValueOn(message);
    Optional<String> previousValue = ValueTracker.getInstance().getValueString(t.getName());

    // Executes command if the condition is met and there is either no previous value for the sensor
    // or there is and it is different
    if (condition == currentValue && (!previousValue.isPresent() ||
        !OnOffStatus.isValueEqualToBoolean(previousValue.get(), currentValue))) {
      JarvisEngine.getInstance().executeCommand(command);
      // Every handler must perform logging
      log();
      return true;
    }

    return false;
  }

  @Override
  public String friendlyString() {
    StringBuilder builder = new StringBuilder();
    builder.append(eventConsumer.getThing().getName());
    if (condition) {
      builder.append(" is activated");
    } else {
      builder.append(" is deactivated");
    }
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

    if (!(handler instanceof BinaryTriggerEventHandler)) {
      return false;
    }

    BinaryTriggerEventHandler h = (BinaryTriggerEventHandler) handler;

    return h.condition == condition;
  }

  @Override
  public JSONObject toJSON() {
    JSONObject result = super.toJSON();
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

      return Optional.of(new BinaryTriggerEventHandler(json));
    } catch (Exception e) {
      // do nothing
    }

    return Optional.empty();
  }
}
