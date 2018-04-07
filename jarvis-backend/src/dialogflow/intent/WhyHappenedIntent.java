package dialogflow.intent;

import dialogflow.DialogFlowRequest;
import dialogflow.QueryResponse;
import dialogflow.intent.definitions.DialogFlowIntent;
import dialogflow.intent.definitions.IntentExtras;
import dialogflow.intent.subintents.ActionFinder;
import jarvis.actions.command.definitions.Command;
import jarvis.events.definitions.EventHandler;
import jarvis.util.JarvisException;
import java.util.List;
import java.util.Optional;
import mongodb.MongoDB;
import org.json.JSONObject;
import res.Config;

public class WhyHappenedIntent extends DialogFlowIntent {

  public static final String INTENT_NAME = Config.DF_WHY_HAPPENNED_INTENT_NAME;
  public static final String INTENT_ID = Config.DF_WHY_HAPPENNED_INTENT_ID;

  private static final int MAX_EVENTS_TO_FETCH = 20;

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

    // TODO check command history instead of active handlers
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

    Command command = optionalCommand.get();

    List<EventHandler> handlers = MongoDB.getLatestNEventsHandled(MAX_EVENTS_TO_FETCH);
    for (EventHandler h : handlers) {
      if (h.command.equals(command)) {
        return getSuccessResponse(h);
      }
    }

    return getErrorResponse();
  }

  @Override
  public Optional<Command> getCommand() {
    return Optional.empty();
  }

  private QueryResponse getSuccessResponse(EventHandler handler) {
    StringBuilder builder = new StringBuilder();
    builder.append(MSG_COMMAND_PREFIX);
    builder.append(handler.friendlyStringWithCommand());

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
