package jarvis.communication;

import jarvis.controllers.definitions.Thing;
import mongodb.MongoDB;
import slack.SlackUtil;

import java.util.ArrayList;
import java.util.List;

public class ThingInterface {
    public static List<Thing> getThings() {
        return MongoDB.getThings();
    }

    public static List<Thing> getThingsByName(String name) {
        return MongoDB.getThingsByName(name);
    }

    public static boolean sendThingsMessage(String url, String message) {
        return SlackUtil.sendIoTMessage(url + " " + message);
    }

    public static void init(List<Thing> defaultThings) {
        if(!MongoDB.isInitialized()) {
            MongoDB.initialize(defaultThings);
        }
    }
}
