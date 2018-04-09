package jarvis.events.definitions;

import jarvis.actions.CommandBuilder;
import jarvis.actions.command.definitions.Command;
import jarvis.controllers.definitions.Thing;
import jarvis.controllers.definitions.events.ThingEvent;
import jarvis.engine.JarvisEngine;
import jarvis.events.BooleanEventHandler;
import jarvis.listeners.EventConsumer;
import jarvis.util.JarvisException;
import java.util.Optional;
import org.json.JSONObject;

public abstract class EventHandler {

  public static final String TAG = "eventHandler";

  protected static final String KEY_TAG = "tag";
  protected static final String KEY_CONSUMER = "consumer";
  protected static final String KEY_COMMAND = "command";

  public final EventConsumer eventConsumer;
  public final Command command;

  protected final String mTag;

  public EventHandler(String tag, EventConsumer consumer, Command command) {
    eventConsumer = consumer;
    this.command = command;
    mTag = tag;
  }

  public EventHandler(EventConsumer consumer, Command command) {
    this(TAG, consumer, command);
  }

  public EventHandler(JSONObject json) throws JarvisException {
    mTag = json.getString(KEY_TAG);

    command = CommandBuilder.buildFromJSON(json.getJSONObject(KEY_COMMAND));
    if (command == null) {
      throw new JarvisException("Invalid command for EventHandler.");
    }
    eventConsumer = new EventConsumer(json.getJSONObject(KEY_CONSUMER));
  }

  public abstract boolean handleMessage(Thing t, ThingEvent e, String message);
  public abstract String friendlyString();
  public abstract String friendlyStringWithCommand();

  protected void log() {
    JarvisEngine.getInstance().logEventHandled(this);
  }

  public boolean equals(EventHandler handler) {
    if (!eventConsumer.equals(handler.eventConsumer)) {
      return false;
    }

    if (!command.equals(handler.command)) {
      return false;
    }

    return true;
  }

  public JSONObject toJSON() {
    JSONObject result = new JSONObject();

    result.put(KEY_TAG, mTag);
    result.put(KEY_CONSUMER, eventConsumer.toJSON());
    result.put(KEY_COMMAND, command.getJSON());

    return result;
  }

  public final String toString() {
    return toJSON().toString();
  }

  public static Optional<EventHandler> buildFromJSON(JSONObject json) {
    try {
      String tag = json.getString(KEY_TAG);
      if (BooleanEventHandler.TAG.equals(tag)) {
        return BooleanEventHandler.buildFromJSON(json);
      }
    } catch (Exception e) {
      // do nothing
    }

    return Optional.empty();
  }
}
