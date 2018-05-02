package jarvis.listeners;

import jarvis.controllers.ThingParser;
import jarvis.controllers.definitions.Thing;
import jarvis.controllers.definitions.events.ThingEvent;
import jarvis.engine.JarvisEngine;
import jarvis.util.JarvisException;
import java.util.List;
import org.json.JSONObject;

public class EventConsumer {

  public final static String KEY_THING = "thing";
  public final static String KEY_EVENT = "event";

  protected Thing mThing;
  protected ThingEvent mEvent;

  public EventConsumer(Thing thing, ThingEvent event) {
    mThing = thing;
    mEvent = event;
  }

  public EventConsumer(JSONObject json) throws JarvisException {
    mThing = ThingParser.parseThingFromJson(json.getJSONObject(KEY_THING)).get();

    List<ThingEvent> events = ThingEvent.getThingEvents(json.getJSONObject(KEY_EVENT));
    if (events.size() < 1) {
      throw new JarvisException("Unable to parse EventConsumer.");
    }
    mEvent = events.get(0);
  }

  public void consume(String message) {
    JarvisEngine.getInstance().handleEvent(mThing, mEvent, message);
    JarvisEngine.getInstance().updateThingValue(mThing.getName(), message);
  }

  public Thing getThing() {
    return mThing;
  }

  public ThingEvent getEvent() {
    return mEvent;
  }

  public boolean equals(EventConsumer consumer) {
    if (!mThing.getName().equals(consumer.mThing.getName())) {
      return false;
    }

    if (!mEvent.equals(consumer.mEvent)) {
      return false;
    }

    return true;
  }

  public JSONObject toJSON() {
    JSONObject result = new JSONObject();
    result.put(KEY_THING, mThing.toJSON());
    result.put(KEY_EVENT, mEvent.getFullJSON());
    return result;
  }
}
