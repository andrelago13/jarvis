package dialogflow.intent.instances;

import dialogflow.DialogFlowRequest;
import dialogflow.QueryResponse;
import dialogflow.intent.DialogFlowIntent;
import dialogflow.intent.subintents.ActionFinder;
import dialogflow.intent.subintents.OnOffSubIntent;
import jarvis.util.AdminAlertUtil;
import jarvis.util.JarvisException;
import jarvis.util.TimeUtils;
import org.json.JSONObject;
import res.Config;
import slack.SlackUtil;

import java.util.Optional;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

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

    private DialogFlowRequest mRequest;

    public DelayedActionIntent(DialogFlowRequest request) {
        mRequest = request;
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

        long seconds = parseTimeToSeconds(timeJson);
        if(seconds == -1) {
            return getInvalidTimeResponse();
        }

        final DialogFlowIntent subIntent = ActionFinder.findIntentForAction(mRequest, action);
        if(subIntent == null) {
            return getErrorResponse();
        }

        ScheduledExecutorService executor =
                Executors.newSingleThreadScheduledExecutor();
        executor.schedule(new Runnable() {
            @Override
            public void run() {
                try {
                    subIntent.execute();
                } catch (JarvisException e) {
                    AdminAlertUtil.alertJarvisException(e);
                    e.printStackTrace();
                }
            }
        }, seconds, TimeUnit.SECONDS);

        return getSuccessResponse();
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

    private long parseTimeToSeconds(JSONObject time) {
        if(time.has(KEY_DATETIME)) {
            // normal "2017-07-12T16:30:00Z"
            // period "2017-07-12T12:00:00Z/2017-07-12T16:00:00Z"
        } else if(time.has(KEY_DURATION)) {
            // {"amount":10,"unit":"min"}
            JSONObject duration = time.getJSONObject(KEY_DURATION);
            if(duration.has(KEY_AMOUNT) && duration.has(KEY_UNIT)) {
                return TimeUtils.timeUnitToSeconds(duration.getString(KEY_UNIT), duration.getInt(KEY_AMOUNT));
            }
        } else if(time.has(KEY_TIME)) {
            // "16:30:00"
        }
        return -1;
    }
}
