package dialogflow.intent.instances;

import dialogflow.QueryResponse;
import dialogflow.intent.DialogFlowIntent;

public class InvalidIntent extends DialogFlowIntent {
    @Override
    public QueryResponse execute() {
        QueryResponse response = new QueryResponse();
        response.addFulfillmentMessage("Sorry, I did not understand what you mean.");
        return response;
    }
}
