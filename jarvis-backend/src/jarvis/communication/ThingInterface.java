package jarvis.communication;

import jarvis.controllers.definitions.Thing;
import slack.SlackUtil;

import java.util.ArrayList;
import java.util.List;

public class ThingInterface {
    public static List<Thing> getThings() {
        return new ArrayList<>();
    }

    public static boolean sendThingsMessage(String url, String message) {
        return SlackUtil.sendIoTMessage(url + " " + message);
    }
}
