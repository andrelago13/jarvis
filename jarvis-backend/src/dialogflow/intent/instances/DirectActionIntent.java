package dialogflow.intent.instances;

import dialogflow.DialogFlowRequest;
import dialogflow.QueryResponse;
import dialogflow.intent.DialogFlowIntent;
import dialogflow.intent.subintents.OnOffSubIntent;
import jarvis.actions.OnOffAction;
import jarvis.controllers.definitions.Thing;
import jarvis.controllers.definitions.actionables.Toggleable;
import jarvis.controllers.definitions.properties.OnOffStatus;
import jarvis.engine.JarvisEngine;
import jarvis.util.JarvisException;
import org.json.JSONObject;
import res.Config;

import java.util.List;
import java.util.Optional;

public class DirectActionIntent extends DialogFlowIntent {
    public static final String INTENT_NAME = Config.DF_DIRECT_ACTION_INTENT_NAME;
    public static final String INTENT_ID = Config.DF_DIRECT_ACTION_INTENT_ID;

    public static final String MSG_ERROR = "Invalid parameters for \"Turn On/Off\" intent.";

    private DialogFlowRequest mRequest;

    public DirectActionIntent(DialogFlowRequest request) {
        mRequest = request;
    }

    @Override
    public QueryResponse execute() throws JarvisException {
        QueryResponse response = new QueryResponse();

        Optional<JSONObject> optParameters = mRequest.getParameters();
        if (!optParameters.isPresent()) {
            response.addFulfillmentMessage(MSG_ERROR);
            return response;
        }

        JSONObject parameters = optParameters.get();
        if (!parameters.has(Config.DF_ACTION_ENTITY_NAME)) {
            response.addFulfillmentMessage(MSG_ERROR);
            return response;
        }

        JSONObject action = parameters.getJSONObject(Config.DF_ACTION_ENTITY_NAME);
        if(action.has(Config.DF_ONOFF_ACTION_ENTITY_NAME)) {
            OnOffSubIntent subIntent =
                    new OnOffSubIntent(mRequest, action.getJSONObject(Config.DF_ONOFF_ACTION_ENTITY_NAME));
            subIntent.execute(response);
        } else {
            response.addFulfillmentMessage(MSG_ERROR);
        }

        return response;
    }
}
