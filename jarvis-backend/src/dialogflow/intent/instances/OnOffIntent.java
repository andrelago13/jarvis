package dialogflow.intent.instances;

import dialogflow.DialogFlowRequest;
import dialogflow.QueryResponse;
import dialogflow.intent.DialogFlowIntent;
import res.Config;

public class OnOffIntent extends DialogFlowIntent {
    public static final String INTENT_NAME = Config.DF_ON_OFF_INTENT_NAME;
    public static final String INTENT_ID = Config.DF_ON_OFF_INTENT_ID;

    private DialogFlowRequest mRequest;

    public OnOffIntent(DialogFlowRequest request) {
        mRequest = request;
    }

    @Override
    public QueryResponse execute() {
        QueryResponse response = new QueryResponse();
        response.addFulfillmentMessage("The specified intent is not yet implemented.");
        return response;
    }
}
