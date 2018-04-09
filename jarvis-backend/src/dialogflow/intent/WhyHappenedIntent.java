package dialogflow.intent;

import dialogflow.DialogFlowRequest;
import dialogflow.QueryResponse;
import dialogflow.intent.definitions.DialogFlowIntent;
import dialogflow.intent.definitions.IntentExtras;
import dialogflow.intent.subintents.ActionFinder;
import jarvis.actions.causality.CauseHelper;
import jarvis.actions.causality.CommandCause;
import jarvis.actions.command.definitions.Command;
import jarvis.util.JarvisException;
import java.util.Optional;
import org.json.JSONObject;
import res.Config;

public class WhyHappenedIntent extends DialogFlowIntent {

  public static final String INTENT_NAME = Config.DF_WHY_HAPPENNED_INTENT_NAME;
  public static final String INTENT_ID = Config.DF_WHY_HAPPENNED_INTENT_ID;

  public static final String MSG_ERROR = "Sorry, I'm not sure how to answer your question.";
  public static final String MSG_NO_ACTION = "I did not find any action that matches what you asked.";
  public static final String MSG_COMMAND_PREFIX = "Because you told me to ";

  public WhyHappenedIntent(DialogFlowRequest request, IntentExtras extras) {
    super(request, extras);
  }

  @Override
  public QueryResponse execute() throws JarvisException {
    Optional<JSONObject> optParameters = mRequest.getParameters();
    if (!optParameters.isPresent()) {
      return getErrorResponse();
    }

    JSONObject parameters = optParameters.get();

    Optional<DialogFlowIntent> commandIntent = ActionFinder
        .findIntentForPastAction(mRequest,
            parameters.getJSONObject(Config.DF_ACTION_PAST_ENTITY_NAME), mExtras);
    if (!commandIntent.isPresent()) {
      return getNoActionResponse();
    }
    Optional<Command> optionalCommand = commandIntent.get().getCommand();
    if (!optionalCommand.isPresent()) {
      return getNoActionResponse();
    }

    Optional<CommandCause> cause = CauseHelper.getCommandCause(optionalCommand.get());
    if (cause.isPresent()) {
      return getSuccessResponse(cause.get().getCauseText());
    }

    return getErrorResponse();
  }

  @Override
  public Optional<Command> getCommand() {
    return Optional.empty();
  }

  private QueryResponse getSuccessResponse(String cause) {
    StringBuilder builder = new StringBuilder();
    builder.append(MSG_COMMAND_PREFIX);
    builder.append(cause);

    QueryResponse response = new QueryResponse();
    response.addFulfillmentMessage(builder.toString());
    return response;
  }

  private QueryResponse getErrorResponse() {
    QueryResponse response = new QueryResponse();
    response.addFulfillmentMessage(MSG_ERROR);
    return response;
  }

  private QueryResponse getNoActionResponse() {
    QueryResponse response = new QueryResponse();
    response.addFulfillmentMessage(MSG_NO_ACTION);
    return response;
  }
}
