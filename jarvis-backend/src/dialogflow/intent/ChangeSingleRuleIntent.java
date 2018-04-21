package dialogflow.intent;

import dialogflow.DialogFlowContext;
import dialogflow.DialogFlowRequest;
import dialogflow.QueryResponse;
import dialogflow.intent.definitions.DialogFlowIntent;
import dialogflow.intent.definitions.IntentExtras;
import jarvis.actions.command.definitions.Command;
import jarvis.engine.JarvisEngine;
import jarvis.events.EventManager;
import jarvis.events.definitions.EventHandler;
import jarvis.util.JarvisException;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import org.json.JSONObject;
import res.Config;

public class ChangeSingleRuleIntent extends DialogFlowIntent {

  public static final String INTENT_NAME = Config.DF_CHANGE_SINGLE_RULE_INTENT_NAME;
  public static final String INTENT_ID = Config.DF_CHANGE_SINGLE_RULE_INTENT_ID;

  public static final String KEY_EVENT = "event";
  public static final String KEY_COMMAND = "command";

  public static final String MSG_ERROR = "Sorry, I was not able to make that change.";

  public ChangeSingleRuleIntent(DialogFlowRequest request, IntentExtras extras) {
    super(request, extras);
  }

  @Override
  public QueryResponse execute() throws JarvisException {
    Optional<JSONObject> optParameters = mRequest.getParameters();
    if (!optParameters.isPresent()) {
      return getErrorResponse();
    }
    JSONObject parameters = optParameters.get();

    List<DialogFlowContext> contexts = mRequest.getContexts();
    DialogFlowContext context = null;
    for (DialogFlowContext c : contexts) {
      if (c.getName().equals(Config.DF_EDIT_SINGLE_RULE_CONTEXT)) {
        context = c;
        break;
      }
    }

    if (context == null) {
      return getErrorResponse();
    }
    Map<String, Object> contextParameters = context.getParameters();

    if (parameters.has(KEY_EVENT) && contextParameters
        .containsKey(Config.DF_EDIT_RULE_CONTEXT_EVENT)) {
      JSONObject currentEventJson = new JSONObject(
          (String) contextParameters.get(Config.DF_EDIT_RULE_CONTEXT_EVENT));
      long id = Long.parseLong(currentEventJson.getString(EventHandler.KEY_ID));
      Optional<EventHandler> optCurrentHandler = JarvisEngine.getInstance().getEventHandler(id);
      if(!optCurrentHandler.isPresent()) {
        return getErrorResponse();
      }

      EventHandler currentHandler = optCurrentHandler.get();
      Optional<EventHandler> optNewHandler = EventManager.findThingEvent(parameters.getJSONObject(KEY_EVENT), currentHandler.command);
      if(optNewHandler.isPresent()) {
        // TODO cancel last event, activate new event and return success message
      }
    } else if (parameters.has(KEY_COMMAND)) {
      // TODO implement command changes
    }

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
}
