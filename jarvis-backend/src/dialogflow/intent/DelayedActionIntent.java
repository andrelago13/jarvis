package dialogflow.intent;

import static jarvis.util.TimeUtils.TimeInfoOrPeriod;

import dialogflow.DialogFlowRequest;
import dialogflow.QueryResponse;
import dialogflow.intent.definitions.DialogFlowIntent;
import dialogflow.intent.definitions.IntentExtras;
import dialogflow.intent.subintents.ActionFinder;
import dialogflow.intent.subintents.PeriodActionIntent;
import jarvis.actions.command.DelayedCommand;
import jarvis.actions.command.definitions.Command;
import jarvis.engine.JarvisEngine;
import jarvis.util.JarvisException;
import jarvis.util.TimeUtils;
import jarvis.util.TimeUtils.TimeInfo;
import java.util.Date;
import java.util.Optional;
import org.json.JSONObject;
import res.Config;

public class DelayedActionIntent extends DialogFlowIntent {

  public static final String INTENT_NAME = Config.DF_DELAYED_ACTION_INTENT_NAME;
  public static final String INTENT_ID = Config.DF_DELAYED_ACTION_INTENT_ID;

  public static final String MSG_SUCCESS = "Done, the action was scheduled!";
  public static final String MSG_ERROR = "Invalid parameters for \"Delayed Action\" intent.";
  public static final String MSG_INVALID_TIME = "Sorry, I can't set an action for the time you specified.";

  public DelayedActionIntent(DialogFlowRequest request, IntentExtras extras) {
    super(request, extras);
  }

  @Override
  public QueryResponse execute() throws JarvisException {
    Optional<JSONObject> optParameters = mRequest.getParameters();
    if (!optParameters.isPresent()) {
      return getErrorResponse();
    }

    JSONObject parameters = optParameters.get();
    if (!parameters.has(Config.DF_ACTION_ENTITY_NAME) || !parameters
        .has(Config.DF_TIME_ENTITY_NAME)) {
      return getErrorResponse();
    }

    JSONObject action = parameters.getJSONObject(Config.DF_ACTION_ENTITY_NAME);
    JSONObject timeJson = parameters.getJSONObject(Config.DF_TIME_ENTITY_NAME);

    TimeInfoOrPeriod parsedResult = TimeUtils.parseTimeValueFromNow(timeJson);
    if (parsedResult == null) {
      return getInvalidTimeResponse();
    }

    if (parsedResult.hasDates()) {
      return executePeriodCommand(action, parsedResult.getDates());
    } else if (parsedResult.hasTimeInfo()) {
      return executeDelayedCommand(action, parsedResult.getTimeInfo());
    }

    return getErrorResponse();
  }

  protected QueryResponse executeDelayedCommand(JSONObject action, TimeInfo timeInfo) {
    // Only schedule actions in the future
    if (timeInfo.value < 0) {
      return getInvalidTimeResponse();
    }
    long targetTimestamp = TimeUtils.calculateTargetTimestamp(timeInfo);

    // Get action subintent
    final Optional<DialogFlowIntent> subIntent = ActionFinder
        .findIntentForAction(mRequest, action, mExtras);
    if (!subIntent.isPresent()) {
      return getErrorResponse();
    }

    // Check if device name is good
    QueryResponse followUpRequest = subIntent.get().getFollowUpRequest();
    if (followUpRequest != null) {
      return followUpRequest;
    }

    // Intent must be "commandable"
    Optional<Command> intentCommand = subIntent.get().getCommand();
    if (!intentCommand.isPresent()) {
      return getErrorResponse();
    }

    // Create delayed command
    Command cmd = new DelayedCommand(intentCommand.get(), timeInfo, targetTimestamp);

    JarvisEngine.getInstance().logUserCommand(cmd, cmd.execute());

    return getSuccessResponse();
  }

  protected QueryResponse executePeriodCommand(JSONObject action, Date[] dates)
      throws JarvisException {
    long now = System.currentTimeMillis();
    if (now > dates[0].getTime()) {
      return getInvalidTimeResponse();
    }

    return new PeriodActionIntent(mRequest, dates, action, mExtras).execute();
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

  private static QueryResponse getInvalidTimeResponse() {
    QueryResponse response = new QueryResponse();
    response.addFulfillmentMessage(MSG_INVALID_TIME);
    return response;
  }

  private static QueryResponse getSuccessResponse() {
    QueryResponse response = new QueryResponse();
    response.addFulfillmentMessage(MSG_SUCCESS);
    return response;
  }
}
