package dialogflow.intent;

import dialogflow.DialogFlowContext;
import dialogflow.DialogFlowRequest;
import dialogflow.QueryResponse;
import dialogflow.intent.definitions.DialogFlowIntent;
import dialogflow.intent.definitions.IntentExtras;
import jarvis.actions.ScheduledAction;
import jarvis.actions.command.definitions.Command;
import jarvis.actions.command.util.LoggedCommand;
import jarvis.controllers.definitions.Thing;
import jarvis.engine.JarvisEngine;
import jarvis.events.definitions.EventHandler;
import jarvis.util.JarvisException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.Set;
import mongodb.MongoDB;
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

    Set<EventHandler> activeEvents = JarvisEngine.getInstance().getEventHandlers();
    List<ScheduledAction> scheduledActions = JarvisEngine.getInstance().getScheduledActions();

    List<Command> activeCommands = new ArrayList<>();
    for (ScheduledAction action : scheduledActions) {
      long id = action.getId();
      Optional<Command> c = MongoDB.getUserCommand(id);
      if(c.isPresent()) {
        List<Thing> targetThings = c.get().targetThings();
        for (Thing t : targetThings) {
          if (t.getName().equals(thing.getName())) {
            activeCommands.add(c.get());
            break;
          }
        }
      }
      //c.ifPresent(activeCommands::add);
    }

    Set<EventHandler> relatedEvents = new HashSet<>();
    for (EventHandler h : activeEvents) {
      List<Thing> targetThings = h.command.targetThings();
      for (Thing t : targetThings) {
        if (t.getName().equals(thing.getName())) {
          relatedEvents.add(h);
          break;
        }
      }
    }

    return getSuccessResponse(thing, activeCommands, relatedEvents);
  }

  private QueryResponse getSuccessResponse(Thing thing, List<Command> activeCommands,
      Set<EventHandler> relatedEvents) {
    StringBuilder builder = new StringBuilder();
    int totalSize = activeCommands.size() + relatedEvents.size();
    DialogFlowContext outContext = null;

    if (totalSize == 0) {
      builder.append(MSG_SUCCESS_EMPTY_PREFIX);
      builder.append(thing.getName());
    } else if (totalSize == 1) {
      builder.append(MSG_SUCCESS_SINGLE_PREFIX);
      String suffix = "";
      for (Command c : activeCommands) {
        suffix = c.friendlyExecuteString();
        Map<String, Object> parameters = new HashMap<>();
        parameters.put(Config.DF_EDIT_RULE_CONTEXT_COMMAND, c.getJSON().toString());
        outContext = new DialogFlowContext(Config.DF_EDIT_SINGLE_RULE_CONTEXT, 1, parameters);
      }
      for (EventHandler h : relatedEvents) {
        suffix = h.friendlyStringWithCommand();
        Map<String, Object> parameters = new HashMap<>();
        parameters.put(Config.DF_EDIT_RULE_CONTEXT_EVENT, h.toJSON().toString());
        outContext = new DialogFlowContext(Config.DF_EDIT_SINGLE_RULE_CONTEXT, 1, parameters);
      }
      builder.append(suffix);
    } else {
      // TODO add context for multiple rules
      builder.append(MSG_SUCCESS_MULTIPLE_PREFIX.replace("#", Integer.toString(totalSize)));
      builder.append(thing.getName());
      builder.append(". ");
      for (Command cmd : activeCommands) {
        builder.append(cmd.friendlyExecuteString());
        builder.append(". ");
      }
      for (EventHandler h : relatedEvents) {
        builder.append(h.friendlyStringWithCommand());
        builder.append(". ");
      }
    }

    QueryResponse response = new QueryResponse();
    response.addFulfillmentMessage(builder.toString());
    if (outContext != null) {
      response.addOutContext(outContext);
    }
    return response;
  }

  @Override
  public Optional<Command> getCommand() {
    return Optional.empty();
  }

  private static QueryResponse getErrorResponse() {
    QueryResponse response = new QueryResponse();
    response.addFulfillmentMessage(MSG_ERROR);
    return response;
  }
}
