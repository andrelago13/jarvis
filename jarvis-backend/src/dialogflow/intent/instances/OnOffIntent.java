package dialogflow.intent.instances;

import dialogflow.DialogFlowRequest;
import dialogflow.QueryResponse;
import dialogflow.intent.DialogFlowIntent;
import jarvis.controllers.OnOffLight;
import jarvis.controllers.definitions.properties.OnOffStatus;
import jarvis.engine.JarvisEngine;
import org.json.JSONObject;
import res.Config;

import java.util.Optional;

public class OnOffIntent extends DialogFlowIntent {
    public static final String INTENT_NAME = Config.DF_ON_OFF_INTENT_NAME;
    public static final String INTENT_ID = Config.DF_ON_OFF_INTENT_ID;

    public static final String DEFAULT_ERROR_MESSAGE = "Invalid parameters for \"Turn On/Off\" intent.";
    public static final String DEFAULT_SUCCESS_MESSAGE = "Done!";

    private static final String KEY_STATUS = "status";
    private static final String KEY_ACTUATOR = "actuator";

    private DialogFlowRequest mRequest;

    public OnOffIntent(DialogFlowRequest request) {
        mRequest = request;
    }

    @Override
    public QueryResponse execute() {
        QueryResponse response = new QueryResponse();

        Optional<JSONObject> optParameters = mRequest.getParameters();
        if(!optParameters.isPresent()) {
            response.addFulfillmentMessage(DEFAULT_ERROR_MESSAGE);
            return response;
        }

        JSONObject parameters = optParameters.get();
        if(!parameters.has(KEY_STATUS) || ! parameters.has(KEY_ACTUATOR)) {
            response.addFulfillmentMessage(DEFAULT_ERROR_MESSAGE);
            return response;
        }

        OnOffStatus status = new OnOffStatus(parameters.getString(KEY_STATUS));
        JSONObject actuator = parameters.getJSONObject(KEY_ACTUATOR);

        if(actuator.has(Config.LIGHT_SWITCH_ENTITY_NAME)) {
            OnOffLight l = (OnOffLight) JarvisEngine.findThing(actuator.getString(Config.LIGHT_SWITCH_ENTITY_NAME));
            if(status.isOn()) {
                l.turnOn();
            } else {
                l.turnOff();
            }
            response.addFulfillmentMessage(DEFAULT_SUCCESS_MESSAGE);
        }

        return response;
    }
}
