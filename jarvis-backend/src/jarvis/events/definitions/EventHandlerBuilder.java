package jarvis.events.definitions;

import jarvis.events.BooleanEventHandler;
import jarvis.util.JarvisException;
import java.util.Optional;
import org.json.JSONObject;

public class EventHandlerBuilder {

  public final static Optional<EventHandler> buildFromJSON(JSONObject json) {
    EventHandler result = null;

    try {
      String tag = json.getString(EventHandler.KEY_TAG);
      if (BooleanEventHandler.TAG.equals(tag)) {
        result = new BooleanEventHandler(json);
      }
    } catch (JarvisException e) {
      // do nothing
    }

    if (result == null) {
      return Optional.empty();
    }
    return Optional.of(result);
  }
}
