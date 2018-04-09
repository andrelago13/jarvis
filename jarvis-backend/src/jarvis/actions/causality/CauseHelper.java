package jarvis.actions.causality;

import jarvis.actions.command.definitions.Command;
import jarvis.actions.command.util.LoggedCommand;
import jarvis.engine.JarvisEngine;
import jarvis.events.util.LoggedEventHandler;
import java.util.List;
import java.util.Optional;
import mongodb.MongoDB;
import res.Config;

public class CauseHelper {

  public static Optional<CommandCause> getCommandCause(Command cmd) {
    LoggedEventHandler latestEventHandler = getLatestEventHandler(cmd);
    LoggedCommand latestCommand = getLatestCommand(cmd);

    String resultString = null;
    Object resultCause = null;
    if(latestCommand != null && latestEventHandler == null) {
      resultString = latestCommand.getCommand().friendlyExecuteString();
      resultCause = latestCommand;
    }
    if(latestCommand == null && latestEventHandler != null) {
      resultCause = latestEventHandler;
      resultString = latestEventHandler.getEventHandler().friendlyStringWithCommand();
    }
    if(latestCommand != null && latestEventHandler != null) {
      if (latestEventHandler.getTimestamp() > latestCommand.getTimestamp()) {
        resultCause = latestEventHandler;
        resultString = latestEventHandler.getEventHandler().friendlyStringWithCommand();
      } else {
        resultString = latestCommand.getCommand().friendlyExecuteString();
        resultCause = latestCommand;
      }
    }

    if(resultCause != null && resultString != null) {
      return Optional.of(new CommandCause(resultCause, resultString));
    }
    return Optional.empty();
  }

  private static LoggedEventHandler getLatestEventHandler(Command cmd) {
    List<LoggedEventHandler> handlers = MongoDB.getLatestNEventsHandled(Config.MAX_EVENTS_TO_FETCH);
    for (LoggedEventHandler h : handlers) {
      if (h.getEventHandler().command.canCauseCommand(cmd)) {
        return h;
      }
    }
    return null;
  }

  private static LoggedCommand getLatestCommand(Command cmd) {
    List<LoggedCommand> commands = JarvisEngine.getInstance()
        .getLatestNUserCommands(Config.MAX_COMMANDS_TO_FETCH);
    for (LoggedCommand c : commands) {
      if (c.getCommand().canCauseCommand(cmd)) {
        return c;
      }
    }
    return null;
  }
}
