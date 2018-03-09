package dialogflow.intent.instances;

import dialogflow.DialogFlowRequest;
import dialogflow.QueryResponse;
import dialogflow.intent.DialogFlowIntent;
import jarvis.actions.command.definitions.Command;
import jarvis.util.JarvisException;
import res.Config;

import java.util.Optional;

public class ConfirmCancelIntent extends DialogFlowIntent {
    public static final String INTENT_NAME = Config.DF_CONFIRM_CANCEL_INTENT_NAME;
    public static final String INTENT_ID = Config.DF_CONFIRM_CANCEL_INTENT_ID;

    public static final String MSG_SUCCESS = "Cancelled!";

    private DialogFlowRequest mRequest;

    public ConfirmCancelIntent(DialogFlowRequest request) {
        mRequest = request;
    }

    @Override
    public QueryResponse execute() throws JarvisException {
        return getSuccessResponse();
    }

    @Override
    public Optional<Command> getCommand() {
        return Optional.empty();
    }

    private static QueryResponse getSuccessResponse() {
        QueryResponse response = new QueryResponse();
        response.addFulfillmentMessage(MSG_SUCCESS);
        return response;
    }
}
