package dialogflow.intent.subintents;

import dialogflow.QueryResponse;
import dialogflow.intent.DialogFlowIntent;
import jarvis.actions.command.definitions.Command;
import jarvis.util.JarvisException;
import org.json.JSONObject;

import java.util.Date;
import java.util.Optional;

public class PeriodActionIntent extends DialogFlowIntent {
    private Date[] mDates;

    public PeriodActionIntent(Date[] dates) {
        mDates = dates;
    }

    @Override
    public QueryResponse execute() throws JarvisException {
        return null;
    }

    @Override
    public Optional<Command> getCommand() {
        return Optional.empty();
    }
}
