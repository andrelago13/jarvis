package dialogflow.intent.definitions;

import dialogflow.DialogFlowRequest;
import dialogflow.QueryResponse;
import dialogflow.intent.CancelIntent;
import dialogflow.intent.ConfirmCancelIntent;
import dialogflow.intent.ConfirmThingIntent;
import dialogflow.intent.DelayedActionIntent;
import dialogflow.intent.DirectActionIntent;
import dialogflow.intent.EventIntent;
import dialogflow.intent.RepeatingActionIntent;
import dialogflow.intent.RulesDefinedIntent;
import dialogflow.intent.UndoIntent;
import dialogflow.intent.WelcomeIntent;
import dialogflow.intent.WhyHappenedIntent;
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

  public QueryResponse getFollowUpRequest() {
    return null;
  }

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
    } else if (RepeatingActionIntent.INTENT_ID.equals(request.getMetadataIntentId())) {
      return new RepeatingActionIntent(request, extras);
    } else if (UndoIntent.INTENT_ID.equals(request.getMetadataIntentId())) {
      return new UndoIntent(request, extras);
    } else if (EventIntent.INTENT_ID.equals(request.getMetadataIntentId())) {
      return new EventIntent(request, extras);
    } else if (WhyHappenedIntent.INTENT_ID.equals(request.getMetadataIntentId())) {
      return new WhyHappenedIntent(request, extras);
    } else if (RulesDefinedIntent.INTENT_ID.equals(request.getMetadataIntentId())) {
      return new RulesDefinedIntent(request, extras);
    }

    return new InvalidIntent();
  }

  public static DialogFlowIntent getIntent(DialogFlowRequest request) {
    return getIntent(request, new IntentExtras());
  }
}
