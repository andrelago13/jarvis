package dialogflow.intent;

import dialogflow.DialogFlowRequest;
import dialogflow.QueryResponse;
import dialogflow.intent.instances.*;
import jarvis.util.JarvisException;

public abstract class DialogFlowIntent {
    public abstract QueryResponse execute() throws JarvisException;

    public static DialogFlowIntent getIntent(DialogFlowRequest request) {
        if(OnOffIntent.INTENT_ID.equals(request.getMetadataIntentId())) {
            return new OnOffIntent(request);
        } else if (DirectActionIntent.INTENT_ID.equals(request.getMetadataIntentId())) {
            return new DirectActionIntent(request);
        } else if (WelcomeIntent.INTENT_ID.equals(request.getMetadataIntentId())) {
            return new WelcomeIntent(request);
        } else if (DelayedActionIntent.INTENT_ID.equals(request.getMetadataIntentId())) {
            return new DelayedActionIntent(request);
        }

        return new InvalidIntent();
    }
}
