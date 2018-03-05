package dialogflow.intent;

import dialogflow.DialogFlowRequest;
import dialogflow.QueryResponse;
import dialogflow.intent.instances.DirectActionIntent;
import dialogflow.intent.instances.InvalidIntent;
import dialogflow.intent.instances.OnOffIntent;
import jarvis.util.JarvisException;

public abstract class DialogFlowIntent {
    public abstract QueryResponse execute() throws JarvisException;

    public static DialogFlowIntent getIntent(DialogFlowRequest request) {
        if(OnOffIntent.INTENT_ID.equals(request.getMetadataIntentId())) {
            return new OnOffIntent(request);
        } else if (DirectActionIntent.INTENT_ID.equals(request.getMetadataIntentId())) {
            return new DirectActionIntent(request);
        }

        return new InvalidIntent();
    }
}
