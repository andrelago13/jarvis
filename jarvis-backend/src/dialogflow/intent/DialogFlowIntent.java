package dialogflow.intent;

import dialogflow.DialogFlowRequest;
import dialogflow.QueryResponse;
import dialogflow.intent.instances.*;
import jarvis.actions.command.definitions.Command;
import jarvis.util.JarvisException;

import java.util.Optional;

public abstract class DialogFlowIntent {
    public abstract QueryResponse execute() throws JarvisException;
    public abstract Optional<Command> getCommand();

    public static DialogFlowIntent getIntent(DialogFlowRequest request) {
        if (DirectActionIntent.INTENT_ID.equals(request.getMetadataIntentId())) {
            return new DirectActionIntent(request);
        } else if (WelcomeIntent.INTENT_ID.equals(request.getMetadataIntentId())) {
            return new WelcomeIntent(request);
        } else if (DelayedActionIntent.INTENT_ID.equals(request.getMetadataIntentId())) {
            return new DelayedActionIntent(request);
        } else if (CancelIntent.INTENT_ID.equals(request.getMetadataIntentId())) {
            return new CancelIntent(request);
        } else if (ConfirmCancelIntent.INTENT_ID.equals(request.getMetadataIntentId())) {
            return new ConfirmCancelIntent(request);
        }

        return new InvalidIntent();
    }
}
