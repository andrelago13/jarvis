package dialogflow.intent;

import dialogflow.DialogFlowRequest;
import dialogflow.QueryResponse;
import dialogflow.intent.definitions.DialogFlowIntent;
import dialogflow.intent.definitions.IntentExtras;
import dialogflow.intent.subintents.ActionFinder;
import dialogflow.intent.subintents.PeriodActionIntent;
import jarvis.actions.command.DelayedCommand;
import jarvis.actions.command.RuleCommand;
import jarvis.actions.command.definitions.Command;
import jarvis.engine.JarvisEngine;
import jarvis.util.JarvisException;
import jarvis.util.TimeUtils;
import jarvis.util.TimeUtils.TimeInfo;
import org.json.JSONObject;
import res.Config;

import java.time.LocalTime;
import java.util.Date;
import java.util.Optional;

import static jarvis.util.TimeUtils.KEY_DATETIME;
import static jarvis.util.TimeUtils.TimeInfoOrPeriod;

public class RepeatingActionIntent extends DialogFlowIntent {
    public static final String INTENT_NAME = Config.DF_REPEATING_ACTION_INTENT_NAME;
    public static final String INTENT_ID = Config.DF_REPEATING_ACTION_INTENT_ID;

    public static final String MSG_SUCCESS = "Sure, I've setup a rule for that.";
    public static final String MSG_ERROR = "Invalid parameters for \"Repeating Action\" intent.";
    public static final String MSG_INVALID_TIME = "Sorry, I can't setup a rule with that time.";

    public RepeatingActionIntent(DialogFlowRequest request, IntentExtras extras) {
        super(request, extras);
    }

    @Override
    public QueryResponse execute() throws JarvisException {
        Optional<JSONObject> optParameters = mRequest.getParameters();
        if (!optParameters.isPresent()) {
            return getErrorResponse();
        }

        JSONObject parameters = optParameters.get();
        if (!parameters.has(Config.DF_ACTION_ENTITY_NAME) || !parameters.has(Config.DF_PERIODTIME_ENTITY_NAME)) {
            return getErrorResponse();
        }

        // This array will have 2 elements if a period is expected, or 1 if only one action is expected
        LocalTime[] times = TimeUtils.parseTimeOrTimePeriod(parameters.getJSONObject(Config.DF_PERIODTIME_ENTITY_NAME));
        JSONObject action = parameters.getJSONObject(Config.DF_ACTION_ENTITY_NAME);

        if(times == null) {
            return getInvalidTimeResponse();
        }

        if (times.length == 1) {
            return setupRule(action, times[0]);
        } else if (times.length == 2) {
            return setupPeriodRule(action, times[0], times[1]);
        }

        return getErrorResponse();
    }

    private QueryResponse setupPeriodRule(JSONObject action, LocalTime time, LocalTime time1) {
        return null;
    }

    private QueryResponse setupRule(JSONObject action, LocalTime time) {
        // Get action subintent
        final DialogFlowIntent subIntent = ActionFinder.findIntentForAction(mRequest, action, mExtras);
        if(subIntent == null) {
            return getErrorResponse();
        }

        // Check if device name is good
        QueryResponse followUpRequest = subIntent.getFollowUpRequest();
        if(followUpRequest != null) {
            return followUpRequest;
        }

        // Intent must be "commandable"
        Optional<Command> intentCommand = subIntent.getCommand();
        if(!intentCommand.isPresent()) {
            return getErrorResponse();
        }

        // Create delayed command
        Command cmd = new RuleCommand(intentCommand.get(), time);
        JarvisEngine.getInstance().executeCommand(cmd);

        return getSuccessResponse();
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

    private static QueryResponse getInvalidTimeResponse() {
        QueryResponse response = new QueryResponse();
        response.addFulfillmentMessage(MSG_INVALID_TIME);
        return response;
    }

    private static QueryResponse getSuccessResponse() {
        QueryResponse response = new QueryResponse();
        response.addFulfillmentMessage(MSG_SUCCESS);
        return response;
    }
}