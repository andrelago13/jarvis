package dialogflow.intent.instances;

import dialogflow.DialogFlowRequest;
import dialogflow.QueryResponse;
import dialogflow.intent.DialogFlowIntent;
import jarvis.controllers.OnOffLight;
import jarvis.controllers.definitions.Thing;
import jarvis.controllers.definitions.actionables.Toggleable;
import jarvis.controllers.definitions.properties.OnOffStatus;
import jarvis.engine.JarvisEngine;
import org.json.JSONObject;
import res.Config;

import java.util.ArrayList;
import java.util.Optional;

public class OnOffIntent extends DialogFlowIntent {
    public static final String INTENT_NAME = Config.DF_ON_OFF_INTENT_NAME;
    public static final String INTENT_ID = Config.DF_ON_OFF_INTENT_ID;

    public static final String MSG_ERROR = "Invalid parameters for \"Turn On/Off\" intent.";
    public static final String MSG_SUCCESS = "Done!";
    public static final String MSG_ALREADY_ON = "The device is already on.";
    public static final String MSG_ALREADY_OFF = "The device is already off.";
    public static final String MSG_DEVICE_NOT_FOUND = "The device you requested was not found.";
    public static final String MSG_MULTIPLE_DEVICES_PREFIX = "Multiple devices were found by the name of ";

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
        if (!optParameters.isPresent()) {
            response.addFulfillmentMessage(MSG_ERROR);
            return response;
        }

        JSONObject parameters = optParameters.get();
        if (!parameters.has(KEY_STATUS) || !parameters.has(KEY_ACTUATOR)) {
            response.addFulfillmentMessage(MSG_ERROR);
            return response;
        }

        OnOffStatus status = new OnOffStatus(parameters.getString(KEY_STATUS));
        JSONObject actuator = parameters.getJSONObject(KEY_ACTUATOR);

        if (actuator.has(Config.LIGHT_SWITCH_ENTITY_NAME)) {
            String name = actuator.getString(Config.LIGHT_SWITCH_ENTITY_NAME);
            ArrayList<Thing> things = JarvisEngine.findThing(name);
            if (things.isEmpty()) {
                response.addFulfillmentMessage(MSG_DEVICE_NOT_FOUND);
            } else if (things.size() > 1) {
                response.addFulfillmentMessage(MSG_MULTIPLE_DEVICES_PREFIX + name);
            } else {
                Toggleable device = (Toggleable) things.get(0);
                Optional<Boolean> isOn = device.isOn();
                if (status.isOn()) {
                    if(isOn.isPresent() && isOn.get()) {
                        response.addFulfillmentMessage(MSG_ALREADY_ON);
                    } else {
                        device.turnOn();
                        response.addFulfillmentMessage(MSG_SUCCESS);
                    }
                } else {
                    if(isOn.isPresent() && !isOn.get()) {
                        response.addFulfillmentMessage(MSG_ALREADY_OFF);
                    } else {
                        device.turnOff();
                        response.addFulfillmentMessage(MSG_SUCCESS);
                    }
                }
            }
        }

        return response;
    }
}
