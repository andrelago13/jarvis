package dialogflow.intent.instances;

import dialogflow.DialogFlowRequest;
import dialogflow.QueryResponse;
import dialogflow.intent.DialogFlowIntent;
import dialogflow.intent.subintents.ActionFinder;
import jarvis.actions.command.definitions.Command;
import jarvis.util.JarvisException;
import org.json.JSONObject;
import res.Config;

import java.util.Optional;

public class DirectActionIntent extends DialogFlowIntent {
    public static final String INTENT_NAME = Config.DF_DIRECT_ACTION_INTENT_NAME;
    public static final String INTENT_ID = Config.DF_DIRECT_ACTION_INTENT_ID;

    public static final String MSG_ERROR = "Invalid parameters for \"Direct Action\" intent.";

    private DialogFlowRequest mRequest;

    public DirectActionIntent(DialogFlowRequest request) {
        mRequest = request;
    }

    @Override
    public QueryResponse execute() throws JarvisException {
        Optional<JSONObject> optParameters = mRequest.getParameters();
        if (!optParameters.isPresent()) {
            return getErrorResponse();
        }

        JSONObject parameters = optParameters.get();
        if (!parameters.has(Config.DF_ACTION_ENTITY_NAME)) {
            return getErrorResponse();
        }

        JSONObject action = parameters.getJSONObject(Config.DF_ACTION_ENTITY_NAME);
        DialogFlowIntent subIntent = ActionFinder.findIntentForAction(mRequest, action);
        if(subIntent == null) {
            return getErrorResponse();
        }

        return subIntent.execute();
    }

    @Override
    public Optional<Command> getCommand() {
        Optional<JSONObject> optParameters = mRequest.getParameters();
        if (optParameters.isPresent()) {
            JSONObject parameters = optParameters.get();
            if (parameters.has(Config.DF_ACTION_ENTITY_NAME)) {
                JSONObject action = parameters.getJSONObject(Config.DF_ACTION_ENTITY_NAME);
                DialogFlowIntent subIntent = ActionFinder.findIntentForAction(mRequest, action);
                if(subIntent == null) {
                    return subIntent.getCommand();
                }
            }
        }

        return Optional.empty();
    }

    private static QueryResponse getErrorResponse() {
        QueryResponse response = new QueryResponse();
        response.addFulfillmentMessage(MSG_ERROR);
        return response;
    }
}
