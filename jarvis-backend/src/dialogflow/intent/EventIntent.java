package dialogflow.intent;

import dialogflow.DialogFlowRequest;
import dialogflow.QueryResponse;
import dialogflow.intent.definitions.DialogFlowIntent;
import dialogflow.intent.definitions.IntentExtras;
import dialogflow.intent.subintents.ActionFinder;
import jarvis.actions.command.EventCommand;
import jarvis.actions.command.definitions.Command;
import jarvis.engine.JarvisEngine;
import jarvis.events.EventManager;
import jarvis.events.definitions.EventHandler;
import jarvis.util.JarvisException;
import java.util.Optional;
import org.json.JSONObject;
import res.Config;

public class EventIntent extends DialogFlowIntent {

  public static final String INTENT_NAME = Config.DF_EVENT_INTENT_NAME;
  public static final String INTENT_ID = Config.DF_EVENT_INTENT_ID;

  public static final String MSG_ERROR = "Sorry, I was not able to set up that event.";
  public static final String MSG_EVENT_EXISTS = "That event has already been created.";
  public static final String MSG_SUCCESS_PREFIX = "Done, I will ";

  public EventIntent(DialogFlowRequest request, IntentExtras extras) {
    super(request, extras);
  }

  @Override
  public QueryResponse execute() throws JarvisException {
    Optional<JSONObject> optParameters = mRequest.getParameters();
    if (!optParameters.isPresent()) {
      return getErrorResponse();
    }

    JSONObject parameters = optParameters.get();
    if (!parameters.has(Config.DF_ACTION_ENTITY_NAME)) {
      return getErrorResponse();
    }

    JSONObject action = parameters.getJSONObject(Config.DF_ACTION_ENTITY_NAME);
    Optional<DialogFlowIntent> subIntent = ActionFinder
        .findIntentForAction(mRequest, action, mExtras);
    if (!subIntent.isPresent()) {
      return getErrorResponse();
    }

    JSONObject event = parameters.getJSONObject(Config.DF_EVENT_ENTITY_NAME);
    Optional<Command> cmd = subIntent.get().getCommand();
    if (!cmd.isPresent()) {
      return getErrorResponse();
    }

    Optional<EventHandler> optHandler = EventManager.findThingEvent(event, cmd.get());

    if (!optHandler.isPresent()) {
      return getErrorResponse();
    }

    if (JarvisEngine.getInstance().eventHandlerExists(optHandler.get())) {
      return getEventExistsResponse();
    }

    EventCommand eventCommand = new EventCommand(event, cmd.get(), optHandler.get());
    JarvisEngine.getInstance().logUserCommand(eventCommand, eventCommand.execute());
    return getSuccessResponse(eventCommand);
  }

  private QueryResponse getErrorResponse() {
    QueryResponse r = new QueryResponse();
    r.addFulfillmentMessage(MSG_ERROR);
    return r;
  }

  private QueryResponse getEventExistsResponse() {
    QueryResponse r = new QueryResponse();
    r.addFulfillmentMessage(MSG_EVENT_EXISTS);
    return r;
  }

  private QueryResponse getSuccessResponse(EventCommand eventCommand) {
    StringBuilder builder = new StringBuilder();
    builder.append(MSG_SUCCESS_PREFIX);
    builder.append(eventCommand.friendlyExecuteString());

    QueryResponse r = new QueryResponse();
    r.addFulfillmentMessage(builder.toString());
    return r;
  }

  @Override
  public Optional<Command> getCommand() {
    return Optional.empty();
  }
}
