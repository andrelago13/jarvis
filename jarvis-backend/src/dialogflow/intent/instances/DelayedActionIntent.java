package dialogflow.intent.instances;

import dialogflow.DialogFlowRequest;
import dialogflow.QueryResponse;
import dialogflow.intent.DialogFlowIntent;
import dialogflow.intent.IntentExtras;
import dialogflow.intent.subintents.ActionFinder;
import dialogflow.intent.subintents.PeriodActionIntent;
import jarvis.actions.command.DelayedCommand;
import jarvis.actions.command.definitions.Command;
import jarvis.engine.JarvisEngine;
import jarvis.util.JarvisException;
import jarvis.util.TimeUtils;
import jarvis.util.TimeUtils.TimeInfo;
import org.json.JSONObject;
import res.Config;

import java.util.Date;
import java.util.Optional;

public class DelayedActionIntent extends DialogFlowIntent {
    public static final String INTENT_NAME = Config.DF_DELAYED_ACTION_INTENT_NAME;
    public static final String INTENT_ID = Config.DF_DELAYED_ACTION_INTENT_ID;

    public static final String KEY_DATETIME = "date-time"; // tomorrow, on Jan 20th
    public static final String KEY_DURATION= "duration"; // in X minutes
    public static final String KEY_TIME = "time"; // at X
    public static final String KEY_AMOUNT = "amount"; // at X
    public static final String KEY_UNIT = "unit"; // at X

    public static final String MSG_SUCCESS = "Done, the action was scheduled!";
    public static final String MSG_ERROR = "Invalid parameters for \"Delayed Action\" intent.";
    public static final String MSG_INVALID_TIME = "Sorry, I can't set an action for the time you specified.";

    public DelayedActionIntent(DialogFlowRequest request, IntentExtras extras) {
        super(request, extras);
    }

    @Override
    public QueryResponse execute() throws JarvisException {
        Optional<JSONObject> optParameters = mRequest.getParameters();
        if (!optParameters.isPresent()) {
            return getErrorResponse();
        }

        JSONObject parameters = optParameters.get();
        if (!parameters.has(Config.DF_ACTION_ENTITY_NAME) || !parameters.has(Config.DF_TIME_ENTITY_NAME)) {
            return getErrorResponse();
        }

        JSONObject action = parameters.getJSONObject(Config.DF_ACTION_ENTITY_NAME);
        JSONObject timeJson = parameters.getJSONObject(Config.DF_TIME_ENTITY_NAME);

        if(timeJson.has(KEY_DATETIME)) {
            String datetime = timeJson.getString(KEY_DATETIME);
            if(datetime.length() != TimeUtils.LENGTH_DATETIME) {
                Date[] dates = TimeUtils.parsePeriod(datetime);
                if(dates != null) {
                    return new PeriodActionIntent(mRequest, dates, action, mExtras).execute();
                } else {
                    return getInvalidTimeResponse();
                }
            }
        }

        TimeInfo timeInfo = parseTimeToSeconds(timeJson);
        if(timeInfo == null || timeInfo.value < 0) {
            return getInvalidTimeResponse();
        }
        long targetTimestamp = TimeUtils.calculateTargetTimestamp(timeInfo);

        final DialogFlowIntent subIntent = ActionFinder.findIntentForAction(mRequest, action, mExtras);
        if(subIntent == null) {
            return getErrorResponse();
        }
        Optional<Command> intentCommand = subIntent.getCommand();
        if(!intentCommand.isPresent()) {
            return getErrorResponse();
        }

        Command cmd = new DelayedCommand(intentCommand.get(), timeInfo, targetTimestamp);
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

    private TimeInfo parseTimeToSeconds(JSONObject time) {
        if(time.has(KEY_DATETIME)) {
            // "2017-07-12T16:30:00Z"
            return TimeUtils.dateTimeToInfo(time.getString(KEY_DATETIME));
        } else if(time.has(KEY_DURATION)) {
            // {"amount":10,"unit":"min"}
            JSONObject duration = time.getJSONObject(KEY_DURATION);
            if(duration.has(KEY_AMOUNT) && duration.has(KEY_UNIT)) {
                return TimeUtils.timeUnitToInfo(duration.getString(KEY_UNIT), duration.getInt(KEY_AMOUNT));
            }
        } else if(time.has(KEY_TIME)) {
            // "16:30:00"
            return TimeUtils.dayTimeToInfo(time.getString(KEY_TIME));
        }
        return null;
    }
}
