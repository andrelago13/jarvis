package dialogflow.intent.subintents;

import com.sun.istack.internal.NotNull;
import dialogflow.DialogFlowRequest;
import dialogflow.intent.definitions.DialogFlowIntent;
import dialogflow.intent.definitions.IntentExtras;
import org.json.JSONObject;
import res.Config;

import java.util.Optional;

public class ActionFinder {
    @NotNull
    public static Optional<DialogFlowIntent> findIntentForAction(DialogFlowRequest request, JSONObject action, IntentExtras extras) {
        if(action.has(Config.DF_ONOFF_ACTION_ENTITY_NAME)) {
            return Optional.of(new OnOffSubIntent(request, action.getJSONObject(Config.DF_ONOFF_ACTION_ENTITY_NAME), extras));
        }

        return Optional.empty();
    }
}
