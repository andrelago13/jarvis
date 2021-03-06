package dialogflow.intent.definitions;

import dialogflow.QueryResponse;
import jarvis.actions.command.definitions.Command;
import java.util.Optional;

public class InvalidIntent extends DialogFlowIntent {

  public InvalidIntent() {
    super(null, null);
  }

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
