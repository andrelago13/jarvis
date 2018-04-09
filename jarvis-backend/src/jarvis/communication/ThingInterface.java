package jarvis.communication;

import jarvis.actions.command.definitions.Command;
import jarvis.actions.command.util.LoggedCommand;
import jarvis.controllers.definitions.Thing;
import java.util.List;
import java.util.Optional;
import mongodb.MongoDB;
import rabbitmq.RabbitMQ;
import res.Config;
import slack.SlackUtil;

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

  public static List<LoggedCommand> getLatestNUserCommands(int n) {
    return MongoDB.getLatestNUserCommands(n);
  }

  public static Optional<Command> getCommand(long id) {
    return MongoDB.getCommand(id);
  }

  public static Optional<Command> getUserCommand(long id) {
    return MongoDB.getUserCommand(id);
  }

  public static boolean sendThingsMessage(String url, String message) {
    String formattedPath = url.replace(' ', '_');
    SlackUtil.sendIoTMessage(formattedPath + " " + message);
    return RabbitMQ.getInstance().sendMessage(formattedPath, message);
  }

  public static void init(List<Thing> defaultThings) {
    if (!MongoDB.isInitialized()) {
      MongoDB.initialize(defaultThings);
    }
    String[] rabbitCreds = Config.getRabbitCredentials();
    RabbitMQ.getInstance().init(rabbitCreds[0], rabbitCreds[1], rabbitCreds[2]);
    MongoDB.deleteActiveRules();
  }
}
