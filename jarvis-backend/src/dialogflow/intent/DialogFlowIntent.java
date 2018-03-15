package dialogflow.intent;

import dialogflow.DialogFlowRequest;
import dialogflow.QueryResponse;
import dialogflow.intent.instances.*;
import jarvis.actions.command.definitions.Command;
import jarvis.util.JarvisException;

import java.util.Optional;

public abstract class DialogFlowIntent {
    protected final DialogFlowRequest mRequest;
    protected final IntentExtras mExtras;

    protected DialogFlowIntent(DialogFlowRequest request, IntentExtras extras) {
        mRequest = request;
        mExtras = extras;
    }

    public abstract QueryResponse execute() throws JarvisException;
    public abstract Optional<Command> getCommand();

    public static DialogFlowIntent getIntent(DialogFlowRequest request, IntentExtras extras) {
        if (DirectActionIntent.INTENT_ID.equals(request.getMetadataIntentId())) {
            return new DirectActionIntent(request, extras);
        } else if (WelcomeIntent.INTENT_ID.equals(request.getMetadataIntentId())) {
            return new WelcomeIntent(request, extras);
        } else if (DelayedActionIntent.INTENT_ID.equals(request.getMetadataIntentId())) {
            return new DelayedActionIntent(request, extras);
        } else if (CancelIntent.INTENT_ID.equals(request.getMetadataIntentId())) {
            return new CancelIntent(request, extras);
        } else if (ConfirmCancelIntent.INTENT_ID.equals(request.getMetadataIntentId())) {
            return new ConfirmCancelIntent(request, extras);
        } else if (ConfirmThingIntent.INTENT_ID.equals(request.getMetadataIntentId())) {
            return new ConfirmThingIntent(request, extras);
        }

        return new InvalidIntent();
    }

    public static DialogFlowIntent getIntent(DialogFlowRequest request) {
        return getIntent(request, new IntentExtras());
    }
}
