package jarvis.communication;

import jarvis.controllers.definitions.Thing;
import mongodb.MongoDB;
import org.json.JSONObject;

import java.util.List;

public class LoggerCommunication {
    public static final String KEY_TIMESTAMP = "timestamp";
    public static final String KEY_EVENT = "event";

    public static void init(List<Thing> defaultThings) {
        if(!MongoDB.isInitialized()) {
            MongoDB.initialize(defaultThings);
        }
    }

    public static boolean logCommand(JSONObject json) {
        return MongoDB.logCommand(json);
    }

    public static boolean logEventHandled(JSONObject json) {
        JSONObject res = new JSONObject();
        res.put(KEY_TIMESTAMP, System.currentTimeMillis());
        res.put(KEY_EVENT, json);
        return MongoDB.logEventHandled(res);
    }
}
