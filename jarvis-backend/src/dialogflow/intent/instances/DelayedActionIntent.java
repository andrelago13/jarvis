package dialogflow.intent.instances;

import dialogflow.QueryResponse;
import dialogflow.intent.DialogFlowIntent;
import jarvis.util.JarvisException;
import res.Config;

public class DelayedActionIntent extends DialogFlowIntent {
    public static final String INTENT_NAME = Config.DF_DELAYED_ACTION_INTENT_NAME;
    public static final String INTENT_ID = Config.DF_DELAYED_ACTION_INTENT_ID;

    @Override
    public QueryResponse execute() throws JarvisException {
        return null;
    }
}
