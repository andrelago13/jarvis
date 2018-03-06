package dialogflow.intent.subintents;

import dialogflow.DialogFlowRequest;
import dialogflow.intent.DialogFlowIntent;
import org.json.JSONObject;
import res.Config;

public class ActionFinder {
    public static DialogFlowIntent findIntentForAction(DialogFlowRequest request, JSONObject action) {
        if(action.has(Config.DF_ONOFF_ACTION_ENTITY_NAME)) {
            return new OnOffSubIntent(request, action.getJSONObject(Config.DF_ONOFF_ACTION_ENTITY_NAME));
        }

        return null;
    }
}
