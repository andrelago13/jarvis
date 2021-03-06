package dialogflow.intent;

import dialogflow.DialogFlowContext;
import dialogflow.DialogFlowRequest;
import dialogflow.QueryResponse;
import dialogflow.intent.definitions.DialogFlowIntent;
import dialogflow.intent.definitions.IntentExtras;
import jarvis.actions.command.definitions.Command;
import jarvis.actions.command.util.LoggedCommand;
import jarvis.engine.JarvisEngine;
import jarvis.util.JarvisException;
import java.util.List;
import java.util.Optional;
import res.Config;

public class CancelIntent extends DialogFlowIntent {

  public static final String INTENT_NAME = Config.DF_CANCEL_INTENT_NAME;
  public static final String INTENT_ID = Config.DF_CANCEL_INTENT_ID;

  public static final String MSG_SUCCESS_PREFIX = "Are you sure you want to cancel the following command: ";

  public static final String MSG_ERROR = "Sorry, I found no command to cancel.";

  public CancelIntent(DialogFlowRequest request, IntentExtras extras) {
    super(request, extras);
  }

  @Override
  public QueryResponse execute() throws JarvisException {
    List<LoggedCommand> commands = JarvisEngine.getInstance()
        .getLatestNUserCommands(Config.MAX_COMMANDS_TO_FETCH);
    Command cancelCommand = getCommandToCancel(commands);
    if (cancelCommand == null) {
      return getErrorResponse();
    }

    return getSuccessResponse(cancelCommand);
  }

  @Override
  public Optional<Command> getCommand() {
    return Optional.empty();
  }

  private static Command getCommandToCancel(List<LoggedCommand> commands) {
    for (LoggedCommand c : commands) {
      if (c.getCommand().isCancellable()) {
        return c.getCommand();
      }
    }

    return null;
  }

  private static QueryResponse getSuccessResponse(Command cmd) {
    QueryResponse response = new QueryResponse();
    response.addFulfillmentMessage(MSG_SUCCESS_PREFIX + cmd.friendlyExecuteString());

    DialogFlowContext c = new DialogFlowContext(Config.DF_CANCEL_INTENT_CONTEXT,
        Config.DF_CANCEL_INTENT_COMMAND_LIFESPAN);
    c.addParameter(Config.DF_CANCEL_INTENT_COMMAND_ID, "" + cmd.getId());
    response.addOutContext(c);

    return response;
  }

  private static QueryResponse getErrorResponse() {
    QueryResponse response = new QueryResponse();
    response.addFulfillmentMessage(MSG_ERROR);
    return response;
  }
}
