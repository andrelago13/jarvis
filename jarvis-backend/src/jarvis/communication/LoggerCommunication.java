package jarvis.communication;

import jarvis.controllers.definitions.Thing;
import jarvis.events.util.LoggedEventHandler;
import java.util.List;
import mongodb.MongoDB;
import org.json.JSONObject;

public class LoggerCommunication {
  public static void init(List<Thing> defaultThings) {
    if (!MongoDB.isInitialized()) {
      MongoDB.initialize(defaultThings);
    }
  }

  public static boolean logCommand(JSONObject json) {
    return MongoDB.logCommand(json);
  }

  public static boolean logEventHandled(LoggedEventHandler info) {
    return MongoDB.logEventHandled(info.toJSON());
  }
}
