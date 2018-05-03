package dialogflow.intent;

import dialogflow.DialogFlowContext;
import dialogflow.DialogFlowRequest;
import dialogflow.QueryResponse;
import dialogflow.intent.definitions.DialogFlowIntent;
import dialogflow.intent.definitions.IntentExtras;
import dialogflow.intent.subintents.ActionFinder;
import jarvis.actions.command.definitions.Command;
import jarvis.engine.AliasEngine;
import jarvis.engine.JarvisEngine;
import jarvis.events.EventManager;
import jarvis.listeners.EventConsumer;
import jarvis.util.JarvisException;
import java.util.List;
import java.util.Optional;
import org.json.JSONObject;
import res.Config;
import slack.SlackUtil;

public class AliasConfirmIntent extends DialogFlowIntent {

  public static final String INTENT_NAME = Config.DF_ALIAS_SET_TYPE_INTENT_NAME;
  public static final String INTENT_ID = Config.DF_ALIAS_SET_TYPE_INTENT_ID;

  public static final String MSG_SUCCESS_COMMAND_PREFIX = "I created a command alias for ";
  public static final String MSG_SUCCESS_EVENT_PREFIX = "I created an event alias for ";
  public static final String MSG_ERROR = "Sorry, I was not able to setup that alias.";
  public static final String MSG_NO_ALIAS = "Sorry, I found no alias to setup.";

  public AliasConfirmIntent(DialogFlowRequest request, IntentExtras extras) {
    super(request, extras);
  }

  @Override
  public QueryResponse execute() throws JarvisException {
    List<DialogFlowContext> contexts = mRequest.getContexts();
    for (DialogFlowContext c : contexts) {
      if (Config.DF_ALIAS_INTENT_CONTEXT.equals(c.getName())) {
        String alias = c.getParameters().getOrDefault(Config.DF_ALIAS_INTENT_CONTEXT_ALIAS, "")
            .toString();
        if (!alias.isEmpty()) {
          return createAlias(alias);
        }
      }
    }

    return getNoAliasResponse();
  }

  private QueryResponse createAlias(String alias) {
    Optional<JSONObject> optParameters = mRequest.getParameters();
    if (!optParameters.isPresent()) {
      return getErrorResponse();
    }

    JSONObject parameters = optParameters.get();
    if (!parameters.has(Config.DF_EVENT_ENTITY_NAME) && !parameters
        .has(Config.DF_ACTION_ENTITY_NAME)) {
      return getErrorResponse();
    }

    Command command = null;
    EventConsumer event = null;
    // try to parse command
    try {
      JSONObject action = parameters.getJSONObject(Config.DF_ACTION_ENTITY_NAME);
      Optional<DialogFlowIntent> subIntent = ActionFinder
          .findIntentForAction(mRequest, action, mExtras);
      if (subIntent.isPresent()) {
        Optional<Command> cmd = subIntent.get().getCommand();
        if (cmd.isPresent()) {
          command = cmd.get();
        }
      }
    } catch (Exception e) {
      // do nothing
    }
    try {
      JSONObject eventJson = parameters.getJSONObject(Config.DF_EVENT_ENTITY_NAME);
      Optional<EventConsumer> consumer = EventManager.findActiveEventConsumer(eventJson);
      if (consumer.isPresent()) {
        event = consumer.get();
      }
    } catch (Exception e) {
      // do nothing
    }

    if (command != null) {
      AliasEngine.getInstance().setCommandAlias(alias, command);
      return getCommandSuccessResponse(alias);
    } else if (event != null) {
      AliasEngine.getInstance().setEventAlias(alias, event);
      return getEventSuccessResponse(alias);
    }
    return getErrorResponse();
  }

  @Override
  public Optional<Command> getCommand() {
    return Optional.empty();
  }

  private QueryResponse getCommandSuccessResponse(String alias) {
    QueryResponse response = new QueryResponse();
    response.addFulfillmentMessage(MSG_SUCCESS_COMMAND_PREFIX + alias);
    return response;
  }

  private QueryResponse getEventSuccessResponse(String alias) {
    QueryResponse response = new QueryResponse();
    response.addFulfillmentMessage(MSG_SUCCESS_EVENT_PREFIX + alias);
    return response;
  }

  private QueryResponse getErrorResponse() {
    QueryResponse response = new QueryResponse();
    response.addFulfillmentMessage(MSG_ERROR);
    return response;
  }

  private QueryResponse getNoAliasResponse() {
    QueryResponse response = new QueryResponse();
    response.addFulfillmentMessage(MSG_NO_ALIAS);
    return response;
  }
}
