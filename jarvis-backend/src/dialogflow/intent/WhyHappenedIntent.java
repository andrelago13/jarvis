package dialogflow.intent;

import dialogflow.DialogFlowRequest;
import dialogflow.QueryResponse;
import dialogflow.intent.definitions.DialogFlowIntent;
import dialogflow.intent.definitions.IntentExtras;
import jarvis.actions.command.definitions.Command;
import jarvis.util.JarvisException;
import java.util.Optional;
import res.Config;

public class WhyHappenedIntent extends DialogFlowIntent {

  public static final String INTENT_NAME = Config.DF_WHY_HAPPENNED_INTENT_NAME;
  public static final String INTENT_ID = Config.DF_WHY_HAPPENNED_INTENT_ID;

  public static final String MSG_ERROR = "Sorry, I'm not sure how to answer your question.";

  public WhyHappenedIntent(DialogFlowRequest request, IntentExtras extras) {
    super(request, extras);
  }

  @Override
  public QueryResponse execute() throws JarvisException {
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
