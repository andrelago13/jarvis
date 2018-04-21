package jarvis.listeners;

import jarvis.controllers.definitions.Thing;
import jarvis.controllers.definitions.events.ThingEvent;
import jarvis.engine.JarvisEngine;
import jarvis.util.JarvisException;
import org.json.JSONObject;
import slack.SlackUtil;

public class ValueUpdateEventConsumer extends EventConsumer {

  public ValueUpdateEventConsumer(Thing thing, ThingEvent event) {
    super(thing, event);
  }

  public ValueUpdateEventConsumer(JSONObject json) throws JarvisException {
    super(json);
  }

  @Override
  public void consume(String message) {
    JarvisEngine.getInstance().updateThingValue(mThing.getName(), message);
  }
}
