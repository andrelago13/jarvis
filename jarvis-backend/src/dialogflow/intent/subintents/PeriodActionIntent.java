package dialogflow.intent.subintents;

import dialogflow.DialogFlowRequest;
import dialogflow.QueryResponse;
import dialogflow.intent.definitions.DialogFlowIntent;
import dialogflow.intent.definitions.IntentExtras;
import jarvis.actions.command.PeriodCommand;
import jarvis.actions.command.definitions.Command;
import jarvis.engine.JarvisEngine;
import jarvis.util.JarvisException;
import org.json.JSONObject;

import java.util.Date;
import java.util.Optional;

public class PeriodActionIntent extends DialogFlowIntent {
    public static final String MSG_SUCCESS = "Done, the action was scheduled!";
    public static final String MSG_ERROR = "Invalid parameters for \"Delayed Action\" intent.";

    private Date[] mDates;
    private JSONObject mAction;

    public PeriodActionIntent(DialogFlowRequest request, Date[] dates, JSONObject action, IntentExtras extras) {
        super(request, extras);
        mDates = dates;
        mAction = action;
    }

    @Override
    public QueryResponse execute() throws JarvisException {
        final DialogFlowIntent subIntent = ActionFinder.findIntentForAction(mRequest, mAction, mExtras);
        if(subIntent == null) {
            return getErrorResponse();
        }

        QueryResponse followUpRequest = subIntent.getFollowUpRequest();
        if(followUpRequest != null) {
            return followUpRequest;
        }

        Optional<Command> intentCommand = subIntent.getCommand();
        if(!intentCommand.isPresent()) {
            return getErrorResponse();
        }

        Command cmd = new PeriodCommand(intentCommand.get(), mDates[0].getTime(), mDates[1].getTime());
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

    private static QueryResponse getSuccessResponse() {
        QueryResponse response = new QueryResponse();
        response.addFulfillmentMessage(MSG_SUCCESS);
        return response;
    }
}
