package dialogflow.intent;

import dialogflow.DialogFlowContext;
import dialogflow.DialogFlowRequest;
import dialogflow.QueryResponse;
import dialogflow.intent.definitions.DialogFlowIntent;
import dialogflow.intent.definitions.IntentExtras;
import jarvis.actions.command.definitions.Command;
import jarvis.actions.command.util.LoggedCommand;
import jarvis.engine.AliasEngine;
import jarvis.engine.JarvisEngine;
import jarvis.util.JarvisException;
import java.util.List;
import java.util.Optional;
import org.json.JSONObject;
import res.Config;

public class AliasIntent extends DialogFlowIntent {

  public static final String INTENT_NAME = Config.DF_ALIAS_INTENT_NAME;
  public static final String INTENT_ID = Config.DF_ALIAS_INTENT_ID;

  public static final String MSG_SUCCESS = "What do you want that alias to represent?";
  public static final String MSG_ALIAS_ALREADY_EXISTS = "Sorry, that alias already exists.";
  public static final String MSG_ERROR = "Sorry, I was not able to create that alias.";

  public AliasIntent(DialogFlowRequest request, IntentExtras extras) {
    super(request, extras);
  }

  @Override
  public QueryResponse execute() throws JarvisException {
    Optional<JSONObject> optParameters = mRequest.getParameters();
    if (!optParameters.isPresent()) {
      return getErrorResponse();
    }

    JSONObject parameters = optParameters.get();
    if (!parameters.has(Config.DF_ALIAS_ENTITY_NAME)) {
      return getErrorResponse();
    }
    String alias = parameters.getString(Config.DF_ALIAS_ENTITY_NAME);

    if(AliasEngine.getInstance().aliasExists(alias)) {
      return getAliasExistsResponse();
    }

    return getSuccessResponse(alias);
  }

  @Override
  public Optional<Command> getCommand() {
    return Optional.empty();
  }

  private static QueryResponse getSuccessResponse(String alias) {
    QueryResponse response = new QueryResponse();
    response.addFulfillmentMessage(MSG_SUCCESS);

    DialogFlowContext c = new DialogFlowContext(Config.DF_ALIAS_INTENT_CONTEXT, 1);
    c.addParameter(Config.DF_ALIAS_INTENT_CONTEXT_ALIAS, alias);
    response.addOutContext(c);

    return response;
  }

  private static QueryResponse getAliasExistsResponse() {
    QueryResponse response = new QueryResponse();
    response.addFulfillmentMessage(MSG_ALIAS_ALREADY_EXISTS);
    return response;
  }

  private static QueryResponse getErrorResponse() {
    QueryResponse response = new QueryResponse();
    response.addFulfillmentMessage(MSG_ERROR);
    return response;
  }
}
