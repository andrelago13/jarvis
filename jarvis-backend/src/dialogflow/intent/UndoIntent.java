package dialogflow.intent;

import dialogflow.DialogFlowRequest;
import dialogflow.QueryResponse;
import dialogflow.intent.definitions.DialogFlowIntent;
import dialogflow.intent.definitions.IntentExtras;
import jarvis.actions.command.definitions.Command;
import jarvis.actions.command.definitions.CommandResult;
import jarvis.actions.command.util.LoggedCommand;
import jarvis.engine.JarvisEngine;
import jarvis.util.JarvisException;
import java.util.List;
import java.util.Optional;
import res.Config;

public class UndoIntent extends DialogFlowIntent {

  public static final String INTENT_NAME = Config.DF_UNDO_INTENT_NAME;
  public static final String INTENT_ID = Config.DF_UNDO_INTENT_ID;

  public static final String MSG_SUCCESS = "Done.";
  public static final String MSG_ERROR = "Sorry, I found no command to undo.";

  public UndoIntent(DialogFlowRequest request, IntentExtras extras) {
    super(request, extras);
  }

  @Override
  public QueryResponse execute() throws JarvisException {
    List<LoggedCommand> commands = JarvisEngine.getInstance()
        .getLatestNUserCommands(Config.MAX_COMMANDS_TO_FETCH);

    if (commands.isEmpty()) {
      return getErrorResponse();
    }

    Command cmd = commands.get(0).getCommand();
    CommandResult res = JarvisEngine.getInstance().undoCommand(cmd);

    if (res.isSuccessful()) {
      return getSuccessResponse();
    }

    return getErrorResponse();
  }

  @Override
  public Optional<Command> getCommand() {
    return Optional.empty();
  }

  private static QueryResponse getSuccessResponse() {
    QueryResponse response = new QueryResponse();
    response.addFulfillmentMessage(MSG_SUCCESS);
    return response;
  }

  private static QueryResponse getErrorResponse() {
    QueryResponse response = new QueryResponse();
    response.addFulfillmentMessage(MSG_ERROR);
    return response;
  }
}
