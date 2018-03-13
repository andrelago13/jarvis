package dialogflow.intent.instances;

import dialogflow.DialogFlowContext;
import dialogflow.DialogFlowRequest;
import dialogflow.QueryResponse;
import dialogflow.intent.DialogFlowIntent;
import jarvis.actions.command.definitions.Command;
import jarvis.engine.JarvisEngine;
import jarvis.util.JarvisException;
import res.Config;

import java.util.List;
import java.util.Optional;

public class ConfirmThingIntent extends DialogFlowIntent {
    public static final String INTENT_NAME = Config.DF_CONFIRM_THING_INTENT_NAME;
    public static final String INTENT_ID = Config.DF_CONFIRM_THING_INTENT_ID;

    public static final String KEY_INTENT = "intent";
    public static final String KEY_PARAMETERS = "parameters";
    public static final String KEY_CHOICES = "choices";

    public static final String MSG_SUCCESS = "Cancelled!";
    public static final String MSG_ERROR = "Sorry, I was not able to cancel that event.";

    private DialogFlowRequest mRequest;

    public ConfirmThingIntent(DialogFlowRequest request) {
        mRequest = request;
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
}
