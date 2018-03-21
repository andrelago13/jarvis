package jarvis.communication;

import jarvis.actions.command.definitions.Command;
import jarvis.controllers.definitions.Thing;
import mongodb.MongoDB;
import rabbitmq.RabbitMQ;
import res.Config;
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

    public static List<Thing> getThingsByNameLike(String name) {
        return MongoDB.getThingsWithNameLike(name);
    }

    public static List<Command> getLatestNUserCommands(int n) {
        return MongoDB.getLatestNUserCommands(n);
    }

    public static boolean sendThingsMessage(String url, String message) {
        String formattedPath = url.replace(' ', '_');
        SlackUtil.sendIoTMessage(formattedPath + " " + message);
        return RabbitMQ.getInstance().sendMessage(formattedPath, message);
    }

    public static void init(List<Thing> defaultThings) {
        if(!MongoDB.isInitialized()) {
            MongoDB.initialize(defaultThings);
        }
        String[] rabbitCreds = Config.getRabbitCredentials();
        RabbitMQ.getInstance().init(rabbitCreds[0], rabbitCreds[1], rabbitCreds[2]);
    }
}
