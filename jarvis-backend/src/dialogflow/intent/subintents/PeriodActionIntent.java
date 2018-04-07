package dialogflow.intent.subintents;

import dialogflow.DialogFlowRequest;
import dialogflow.QueryResponse;
import dialogflow.intent.definitions.DialogFlowIntent;
import dialogflow.intent.definitions.IntentExtras;
import jarvis.actions.command.PeriodCommand;
import jarvis.actions.command.definitions.Command;
import jarvis.util.JarvisException;
import java.util.Date;
import java.util.Optional;
import org.json.JSONObject;

public class PeriodActionIntent extends DialogFlowIntent {

  public static final String MSG_SUCCESS = "Done, the action was scheduled!";
  public static final String MSG_ERROR = "Invalid parameters for \"Delayed Action\" intent.";

  private Date[] mDates;
  private JSONObject mAction;

  public PeriodActionIntent(DialogFlowRequest request, Date[] dates, JSONObject action,
      IntentExtras extras) {
    super(request, extras);
    mDates = dates;
    mAction = action;
  }

  @Override
  public QueryResponse execute() throws JarvisException {
    final Optional<DialogFlowIntent> subIntent = ActionFinder
        .findIntentForAction(mRequest, mAction, mExtras);
    if (!subIntent.isPresent()) {
      return getErrorResponse();
    }

    QueryResponse followUpRequest = subIntent.get().getFollowUpRequest();
    if (followUpRequest != null) {
      return followUpRequest;
    }

    Optional<Command> intentCommand = subIntent.get().getCommand();
    if (!intentCommand.isPresent()) {
      return getErrorResponse();
    }

    Command cmd = new PeriodCommand(intentCommand.get(), mDates[0].getTime(), mDates[1].getTime());
    cmd.execute();

    return getSuccessResponse();
  }

  @Override
  public Optional<Command> getCommand() {
    return Optional.empty();
  }

  private static QueryResponse getErrorResponse() {
    QueryResponse response = new QueryResponse();
    response.addFulfillmentMessage(MSG_ERROR);
    return response;
  }

  private static QueryResponse getSuccessResponse() {
    QueryResponse response = new QueryResponse();
    response.addFulfillmentMessage(MSG_SUCCESS);
    return response;
  }
}
