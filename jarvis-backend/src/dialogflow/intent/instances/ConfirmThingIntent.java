package dialogflow.intent.instances;

import dialogflow.DialogFlowContext;
import dialogflow.DialogFlowRequest;
import dialogflow.QueryResponse;
import dialogflow.intent.DialogFlowIntent;
import dialogflow.intent.IntentExtras;
import jarvis.actions.command.definitions.Command;
import jarvis.controllers.definitions.Thing;
import jarvis.engine.JarvisEngine;
import jarvis.util.JarvisException;
import org.json.JSONArray;
import org.json.JSONObject;
import res.Config;

import java.util.List;
import java.util.Optional;

public class ConfirmThingIntent extends DialogFlowIntent {
    public static final String INTENT_NAME = Config.DF_CONFIRM_THING_INTENT_NAME;
    public static final String INTENT_ID = Config.DF_CONFIRM_THING_INTENT_ID;

    public static final String KEY_INTENT = "intent";
    public static final String KEY_REQUEST = "request";
    public static final String KEY_CHOICES = "choices";

    public static final String MSG_SUCCESS = "Cancelled!";
    public static final String MSG_ERROR = "Sorry, I was not able to cancel that event.";

    public ConfirmThingIntent(DialogFlowRequest request, IntentExtras extras) {
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

    private QueryResponse getSuccessResponse() {
        QueryResponse response = new QueryResponse();
        response.addFulfillmentMessage(MSG_SUCCESS);
        return response;
    }

    private QueryResponse getErrorResponse() {
        QueryResponse response = new QueryResponse();
        response.addFulfillmentMessage(MSG_ERROR);
        return response;
    }

    public static QueryResponse getMultipleDeviceResponse(List<Thing> things, String messagePrefix, String intent, DialogFlowRequest request) {
        QueryResponse response = new QueryResponse();
        String resultMessage = messagePrefix;

        JSONArray choices = new JSONArray();
        for (int i = 0; i < things.size(); ++i) {
            Thing thing = things.get(i);
            choices.put(thing.getName());
            resultMessage += thing.getName();
            if (i < things.size() - 2) {
                resultMessage += ", ";
            } else if (i < things.size() - 1) {
                resultMessage += " or ";
            }
        }
        resultMessage += ".";
        response.addFulfillmentMessage(resultMessage);

        DialogFlowContext context = new DialogFlowContext(Config.DF_CONFIRM_THING_INTENT_CONTEXT, 1);
        context.addParameter(ConfirmThingIntent.KEY_INTENT, intent);
        context.addParameter(ConfirmThingIntent.KEY_REQUEST, request.getJSON().toString());
        context.addParameter(ConfirmThingIntent.KEY_CHOICES, choices);
        response.addOutContext(context);
        return response;
    }
}
