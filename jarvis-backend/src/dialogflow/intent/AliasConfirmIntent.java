package dialogflow.intent;

import dialogflow.DialogFlowContext;
import dialogflow.DialogFlowRequest;
import dialogflow.QueryResponse;
import dialogflow.intent.definitions.DialogFlowIntent;
import dialogflow.intent.definitions.IntentExtras;
import dialogflow.intent.subintents.ActionFinder;
import jarvis.actions.command.definitions.Command;
import jarvis.engine.JarvisEngine;
import jarvis.listeners.EventConsumer;
import jarvis.util.JarvisException;
import java.util.List;
import java.util.Optional;
import org.json.JSONObject;
import res.Config;

public class AliasConfirmIntent extends DialogFlowIntent {

  public static final String INTENT_NAME = Config.DF_ALIAS_SET_TYPE_INTENT_NAME;
  public static final String INTENT_ID = Config.DF_ALIAS_SET_TYPE_INTENT_ID;

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
        String alias = c.getParameters().getOrDefault(Config.DF_ALIAS_INTENT_CONTEXT_ALIAS, "").toString();
        if(!alias.isEmpty()) {
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
    if (!parameters.has(Config.DF_EVENT_ENTITY_NAME) && !parameters.has(Config.DF_ACTION_ENTITY_NAME)) {
      return getErrorResponse();
    }

    Command command = null;
    EventConsumer event = null;
    try {
      JSONObject action = parameters.getJSONObject(Config.DF_ACTION_ENTITY_NAME);
      Optional<DialogFlowIntent> subIntent = ActionFinder
          .findIntentForAction(mRequest, action, mExtras);
      if(subIntent.isPresent()) {
        Optional<Command> cmd = subIntent.get().getCommand();
        if(cmd.isPresent()) {
          command = cmd.get();
        }
      }
    } catch (Exception e) {
      // do nothing
    }

    // TODO check whether alias is for event or command
    return getErrorResponse();
  }

  @Override
  public Optional<Command> getCommand() {
    return Optional.empty();
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
