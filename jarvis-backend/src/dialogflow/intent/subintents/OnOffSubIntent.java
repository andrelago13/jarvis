package dialogflow.intent.subintents;

import dialogflow.DialogFlowContext;
import dialogflow.DialogFlowRequest;
import dialogflow.QueryResponse;
import dialogflow.intent.DialogFlowIntent;
import dialogflow.intent.instances.ConfirmThingIntent;
import jarvis.actions.command.OnOffCommand;
import jarvis.actions.command.definitions.Command;
import jarvis.actions.command.definitions.CommandResult;
import jarvis.controllers.definitions.Thing;
import jarvis.controllers.definitions.actionables.Toggleable;
import jarvis.controllers.definitions.properties.OnOffStatus;
import jarvis.engine.JarvisEngine;
import jarvis.util.JarvisException;
import org.json.JSONArray;
import org.json.JSONObject;
import res.Config;

import java.util.List;
import java.util.Optional;

public class OnOffSubIntent extends DialogFlowIntent {
    public static final String TAG = "OnOffSubIntent";

    public static final String MSG_SUCCESS = "Done!";
    public static final String MSG_ERROR = "Sorry, I was not able to do that.";
    public static final String MSG_ALREADY_STATE = "The device is already ";
    public static final String MSG_DEVICE_NOT_FOUND = "The device you requested was not found.";
    public static final String MSG_MULTIPLE_DEVICES_PREFIX = "Do you mean ";
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
        } else if (!status.isOn() && isOn.isPresent() && !isOn.get()) {
            return false;
        }

        return true;
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
            List<Thing> things = JarvisEngine.getInstance().findThing(name);

            if (things.isEmpty()) {
                resultMessage = MSG_DEVICE_NOT_FOUND;
            } else if (things.size() > 1) {
                resultMessage = MSG_MULTIPLE_DEVICES_PREFIX;

                JSONArray choices = new JSONArray();
                for(int i = 0; i < things.size(); ++i) {
                    Thing thing = things.get(i);
                    choices.put(thing.getName());
                    resultMessage += thing.getName();
                    if(i < things.size() - 2) {
                        resultMessage += ", ";
                    } else if(i < things.size() - 1) {
                        resultMessage += " or ";
                    }
                }
                resultMessage += ".";
                response.addFulfillmentMessage(resultMessage);

                DialogFlowContext context = new DialogFlowContext(Config.DF_CONFIRM_THING_INTENT_CONTEXT, 1);
                context.addParameter(ConfirmThingIntent.KEY_INTENT, TAG);
                context.addParameter(ConfirmThingIntent.KEY_PARAMETERS, mParameters);
                context.addParameter(ConfirmThingIntent.KEY_CHOICES, choices);
                response.addOutContext(context);
                return response;
            } else if (things.get(0) instanceof Toggleable) {
                Toggleable device = (Toggleable) things.get(0);
                if (isStateDifferent(device, status)) {
                    cmd = new OnOffCommand(device, status);
                } else {
                    resultMessage = MSG_ALREADY_STATE + status.getStatusString();
                }
            } else {
                resultMessage = MSG_DEVICE_NOT_SUPPORTED;
            }
        }

        if (cmd != null) {
            CommandResult result = JarvisEngine.getInstance().executeCommand(cmd);
            if (result.isSuccessful()) {
                resultMessage = MSG_SUCCESS;
            }
        }

        response.addFulfillmentMessage(resultMessage);
        return response;
    }

    @Override
    public Optional<Command> getCommand() {
        OnOffStatus status = new OnOffStatus(mParameters.getString(KEY_STATUS));
        JSONObject actuator = mParameters.getJSONObject(KEY_ACTUATOR);
        Command cmd = null;

        if (actuator.has(Config.DF_LIGHT_SWITCH_ENTITY_NAME)) {
            String name = actuator.getString(Config.DF_LIGHT_SWITCH_ENTITY_NAME);
            List<Thing> things = JarvisEngine.getInstance().findThing(name);

            if (things.size() == 1 && things.get(0) instanceof Toggleable) {
                Toggleable device = (Toggleable) things.get(0);
                cmd = new OnOffCommand(device, status);
            }
        }

        if (cmd != null) {
            return Optional.of(cmd);
        }
        return Optional.empty();
    }
}
