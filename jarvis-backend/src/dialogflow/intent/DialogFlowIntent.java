package dialogflow.intent;

import dialogflow.DialogFlowRequest;
import dialogflow.QueryResponse;
import dialogflow.intent.instances.InvalidIntent;
import dialogflow.intent.instances.OnOffIntent;
import jarvis.util.JarvisException;

public abstract class DialogFlowIntent {
    public abstract QueryResponse execute() throws JarvisException;

    public static DialogFlowIntent getIntent(DialogFlowRequest request) {
        if(OnOffIntent.INTENT_ID.equals(request.getMetadataIntentId())) {
            return new OnOffIntent(request);
        }

        return new InvalidIntent();
    }
}
