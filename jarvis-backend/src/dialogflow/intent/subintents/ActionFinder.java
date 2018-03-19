package dialogflow.intent.subintents;

import dialogflow.DialogFlowRequest;
import dialogflow.intent.definitions.DialogFlowIntent;
import dialogflow.intent.definitions.IntentExtras;
import org.json.JSONObject;
import res.Config;

public class ActionFinder {
    public static DialogFlowIntent findIntentForAction(DialogFlowRequest request, JSONObject action, IntentExtras extras) {
        if(action.has(Config.DF_ONOFF_ACTION_ENTITY_NAME)) {
            return new OnOffSubIntent(request, action.getJSONObject(Config.DF_ONOFF_ACTION_ENTITY_NAME), extras);
        }

        return null;
    }
}
