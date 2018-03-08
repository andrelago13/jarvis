package dialogflow.intent.subintents;

import dialogflow.DialogFlowRequest;
import dialogflow.QueryResponse;
import dialogflow.intent.DialogFlowIntent;
import jarvis.actions.OnOffAction;
import jarvis.actions.definitions.Command;
import jarvis.actions.definitions.CommandResult;
import jarvis.controllers.definitions.Thing;
import jarvis.controllers.definitions.actionables.Toggleable;
import jarvis.controllers.definitions.properties.OnOffStatus;
import jarvis.engine.JarvisEngine;
import jarvis.util.JarvisException;
import org.json.JSONObject;
import res.Config;

import java.util.List;
import java.util.Optional;

public class OnOffSubIntent extends DialogFlowIntent {
    public static final String MSG_SUCCESS = "Done!";
    public static final String MSG_ERROR = "Sorry, I was not able to do that.";
    public static final String MSG_ALREADY_STATE = "The device is already ";
    public static final String MSG_DEVICE_NOT_FOUND = "The device you requested was not found.";
    public static final String MSG_MULTIPLE_DEVICES_PREFIX = "Multiple devices were found by the name of ";
    public static final String MSG_DEVICE_NOT_SUPPORTED = "The specified device does not support the toggle feature.";

    private static final String KEY_STATUS = "status";
    private static final String KEY_ACTUATOR = "actuator";

    private DialogFlowRequest mRequest;
    private JSONObject mParameters;

    public OnOffSubIntent(DialogFlowRequest request, JSONObject parameters) {
        mRequest = request;
        mParameters = parameters;
    }

    private static boolean isStateDifferent(Toggleable device, OnOffStatus status) {
        Optional<Boolean> isOn = device.isOn();
        if (status.isOn() && isOn.isPresent() && isOn.get()) {
            return false;
        } else if(!status.isOn() && isOn.isPresent() && !isOn.get()) {
            return false;
        }

        return true;
    }

    public OnOffAction getAction() {

    }

    @Override
    public QueryResponse execute() throws JarvisException {
        QueryResponse response = new QueryResponse();
        OnOffStatus status = new OnOffStatus(mParameters.getString(KEY_STATUS));
        JSONObject actuator = mParameters.getJSONObject(KEY_ACTUATOR);

        String resultMessage = MSG_ERROR;
        Command cmd = null;

        if (actuator.has(Config.DF_LIGHT_SWITCH_ENTITY_NAME)) {
            String name = actuator.getString(Config.DF_LIGHT_SWITCH_ENTITY_NAME);
            List<Thing> things = JarvisEngine.findThing(name);

            if (things.isEmpty()) {
                resultMessage = MSG_DEVICE_NOT_FOUND;
            } else if (things.size() > 1) {
                resultMessage = MSG_MULTIPLE_DEVICES_PREFIX + name;
            } else if(things.get(0) instanceof Toggleable) {
                Toggleable device = (Toggleable) things.get(0);
                if(isStateDifferent(device, status)) {
                    cmd = new OnOffAction(device, status);
                } else {
                    resultMessage = MSG_ALREADY_STATE + status.getStatusString();
                }
            } else {
                resultMessage = MSG_DEVICE_NOT_SUPPORTED;
            }
        }

        if(cmd != null) {
            CommandResult result = JarvisEngine.executeCommand(cmd);
            if(result.isSuccessful()) {
                resultMessage = MSG_SUCCESS;
            }
        }

        response.addFulfillmentMessage(resultMessage);
        return response;
    }
}
