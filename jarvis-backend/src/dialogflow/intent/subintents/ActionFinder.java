package dialogflow.intent.subintents;

import dialogflow.DialogFlowRequest;
import dialogflow.intent.definitions.DialogFlowIntent;
import dialogflow.intent.definitions.IntentExtras;
import java.util.Optional;
import org.json.JSONObject;
import res.Config;

public class ActionFinder {

  public static Optional<DialogFlowIntent> findIntentForAction(DialogFlowRequest request,
      JSONObject action, IntentExtras extras) {
    if (action.has(Config.DF_ONOFF_ACTION_ENTITY_NAME)) {
      return Optional
          .of(new OnOffSubIntent(request, action.getJSONObject(Config.DF_ONOFF_ACTION_ENTITY_NAME),
              extras));
    }

    return Optional.empty();
  }

  public static Optional<DialogFlowIntent> findIntentForPastAction(DialogFlowRequest request,
      JSONObject action, IntentExtras extras) {
    if (action.has(Config.DF_ACTION_PAST_ONOFF_NAME)) {
      return Optional
          .of(new OnOffSubIntent(request, action.getJSONObject(Config.DF_ACTION_PAST_ONOFF_NAME),
              extras));
    }

    return Optional.empty();
  }
}
