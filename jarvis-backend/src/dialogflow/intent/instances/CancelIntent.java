package dialogflow.intent.instances;

import dialogflow.DialogFlowRequest;
import dialogflow.QueryResponse;
import dialogflow.DialogFlowContext;
import dialogflow.intent.DialogFlowIntent;
import jarvis.actions.definitions.Command;
import jarvis.util.JarvisException;
import res.Config;

import java.util.Optional;

public class CancelIntent extends DialogFlowIntent {
    public static final String INTENT_NAME = Config.DF_CANCEL_INTENT_NAME;
    public static final String INTENT_ID = Config.DF_CANCEL_INTENT_ID;

    public static final String MSG_SUCCESS = "Are you sure you want to cancel the command?";

    private DialogFlowRequest mRequest;

    public CancelIntent(DialogFlowRequest request) {
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

        DialogFlowContext c = new DialogFlowContext(Config.DF_CANCEL_INTENT_CONTEXT,
                Config.DF_CANCEL_INTENT_COMMAND_LIFESPAN);
        c.addParameter(Config.DF_CANCEL_INTENT_COMMAND_ID, "cmd123");
        response.addOutContext(c);

        return response;
    }
}
