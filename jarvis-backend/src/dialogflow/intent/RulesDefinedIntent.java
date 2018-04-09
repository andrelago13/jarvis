package dialogflow.intent;

import dialogflow.DialogFlowRequest;
import dialogflow.QueryResponse;
import dialogflow.intent.definitions.DialogFlowIntent;
import dialogflow.intent.definitions.IntentExtras;
import jarvis.actions.command.definitions.Command;
import jarvis.actions.command.util.LoggedCommand;
import jarvis.controllers.definitions.Thing;
import jarvis.engine.JarvisEngine;
import jarvis.util.JarvisException;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import org.json.JSONObject;
import res.Config;

public class RulesDefinedIntent extends DialogFlowIntent {

  public static final String INTENT_NAME = Config.DF_RULES_DEFINED_INTENT_NAME;
  public static final String INTENT_ID = Config.DF_RULES_DEFINED_INTENT_ID;

  public static final String KEY_THING = "thing";

  public static final String MSG_ERROR = "Sorry, I don't know which rules are set.";
  public static final String MSG_SUCCESS_EMPTY_PREFIX = "Actually, there are no active rules for the ";
  public static final String MSG_SUCCESS_SINGLE_PREFIX = "You told me to ";
  public static final String MSG_SUCCESS_MULTIPLE_PREFIX = "There are # the rules for the ";

  public RulesDefinedIntent(DialogFlowRequest request, IntentExtras extras) {
    super(request, extras);
  }

  @Override
  public QueryResponse execute() throws JarvisException {
    Optional<JSONObject> optParameters = mRequest.getParameters();
    if (!optParameters.isPresent()) {
      return getErrorResponse();
    }

    List<Thing> things = JarvisEngine.getInstance()
        .findThing(optParameters.get().getJSONObject(KEY_THING));
    if (things.size() != 1) {
      // TODO check if more than 1 things are selected
      return getErrorResponse();
    }
    Thing thing = things.get(0);

    // TODO check the active rules
    List<LoggedCommand> commands = JarvisEngine.getInstance()
        .getLatestNUserCommands(Config.MAX_COMMANDS_TO_FETCH);

    List<LoggedCommand> matchingCommands = new ArrayList<>();
    for (LoggedCommand cmd : commands) {
      List<Thing> cmdTargets = cmd.getCommand().targetThings();
      for (Thing t : cmdTargets) {
        if (t.getName().equals(thing.getName())) {
          matchingCommands.add(cmd);
        }
      }
    }

    return getSuccessResponse(thing, matchingCommands);
  }

  @Override
  public Optional<Command> getCommand() {
    return Optional.empty();
  }

  private static QueryResponse getSuccessResponse(Thing thing, List<LoggedCommand> commands) {
    StringBuilder builder = new StringBuilder();
    if (commands.size() == 0) {
      builder.append(MSG_SUCCESS_EMPTY_PREFIX);
      builder.append(thing.getName());
    } else if (commands.size() == 1) {
      builder.append(MSG_SUCCESS_SINGLE_PREFIX);
      builder.append(commands.get(0).getCommand().friendlyExecuteString());
    } else {
      builder.append(MSG_SUCCESS_MULTIPLE_PREFIX.replace("#", Integer.toString(commands.size())));
      builder.append(thing.getName());
      builder.append(". ");
      for(LoggedCommand cmd : commands) {
        builder.append(cmd.getCommand().friendlyExecuteString());
        builder.append(". ");
      }
    }

    QueryResponse response = new QueryResponse();
    response.addFulfillmentMessage(builder.toString());
    return response;
  }

  private static QueryResponse getErrorResponse() {
    QueryResponse response = new QueryResponse();
    response.addFulfillmentMessage(MSG_ERROR);
    return response;
  }
}
