package dialogflow.intent.instances;

import dialogflow.QueryResponse;
import dialogflow.intent.DialogFlowIntent;
import jarvis.actions.definitions.Command;

import java.util.Optional;

public class InvalidIntent extends DialogFlowIntent {
    @Override
    public QueryResponse execute() {
        QueryResponse response = new QueryResponse();
        response.addFulfillmentMessage("Sorry, I did not understand what you mean.");
        return response;
    }

    @Override
    public Optional<Command> getCommand() {
        return Optional.empty();
    }
}
