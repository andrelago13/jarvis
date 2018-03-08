package jarvis.communication;

import jarvis.controllers.definitions.Thing;
import mongodb.MongoDB;
import org.json.JSONObject;

import java.util.List;

public class LoggerCommunication {
    public static void init(List<Thing> defaultThings) {
        if(!MongoDB.isInitialized()) {
            MongoDB.initialize(defaultThings);
        }
    }

    public static boolean logCommand(JSONObject json) {
        return MongoDB.logCommand(json);
    }
}
