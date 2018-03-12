package dialogflow.intent.subintents;

import dialogflow.DialogFlowRequest;
import dialogflow.QueryResponse;
import dialogflow.intent.DialogFlowIntent;
import jarvis.actions.command.DelayedCommand;
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

    private DialogFlowRequest mRequest;
    private Date[] mDates;
    private JSONObject mAction;

    public PeriodActionIntent(DialogFlowRequest request, Date[] dates, JSONObject action) {
        mRequest = request;
        mDates = dates;
        mAction = action;
    }

    @Override
    public QueryResponse execute() throws JarvisException {
        final DialogFlowIntent subIntent = ActionFinder.findIntentForAction(mRequest, mAction);
        if(subIntent == null) {
            return getErrorResponse();
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
