package jarvis.routes;

import jarvis.actions.command.definitions.Command;
import jarvis.engine.AliasEngine;
import jarvis.engine.ValueTracker;
import jarvis.listeners.EventConsumer;
import java.util.Map;
import java.util.Set;
import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;

@Path("/aliases")
public class Aliases {

  @GET
  @Produces("text/plain")
  public String getClichedMessage() {
    StringBuilder res = new StringBuilder();

    Map<String, Command> commands = AliasEngine.getInstance().getCommandAliases();
    Map<String, EventConsumer> events = AliasEngine.getInstance().getEventAliases();

    res.append("Commands: ");
    res.append('\n');

    Set<String> keys = commands.keySet();
    for(String k : keys) {
      res.append(k);
      res.append("  =>  ");
      res.append(commands.get(k).executeString());
      res.append('\n');
    }

    res.append('\n');
    res.append("Events: ");
    res.append('\n');
    keys = events.keySet();
    for(String k : keys) {
      res.append(k);
      res.append("  =>  ");
      res.append(events.get(k).getThing());
      res.append(" | ");
      res.append(events.get(k).getEvent().getHref());
      res.append('\n');
    }
    return res.toString();
  }
}