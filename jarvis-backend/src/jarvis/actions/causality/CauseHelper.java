package jarvis.actions.causality;

import jarvis.actions.command.definitions.Command;
import jarvis.events.util.LoggedEventHandler;
import java.util.List;
import java.util.Optional;
import mongodb.MongoDB;
import res.Config;

public class CauseHelper {

  public static Optional<CommandCause> getCommandCause(Command cmd) {
    List<LoggedEventHandler> handlers = MongoDB.getLatestNEventsHandled(Config.MAX_EVENTS_TO_FETCH);
    for (LoggedEventHandler h : handlers) {
      if (h.getEventHandler().command.equals(cmd)) {
        return Optional.of(new CommandCause(h.getEventHandler(),
            h.getEventHandler().friendlyStringWithCommand()));
      }
    }

    return Optional.empty();
  }
}
