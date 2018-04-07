package dialogflow.intent;

import dialogflow.DialogFlowContext;
import dialogflow.DialogFlowRequest;
import dialogflow.QueryResponse;
import dialogflow.intent.definitions.DialogFlowIntent;
import dialogflow.intent.definitions.IntentExtras;
import jarvis.actions.command.definitions.Command;
import jarvis.controllers.definitions.Thing;
import jarvis.engine.JarvisEngine;
import jarvis.util.JarvisException;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import org.json.JSONArray;
import res.Config;

public class ConfirmThingIntent extends DialogFlowIntent {

  public static final String INTENT_NAME = Config.DF_CONFIRM_THING_INTENT_NAME;
  public static final String INTENT_ID = Config.DF_CONFIRM_THING_INTENT_ID;

  public static final int EXTRA_CHOSEN_THING = 1;

  public static final String KEY_INTENT = "intent";
  public static final String KEY_REQUEST = "request";
  public static final String KEY_CHOICES = "choices";
  public static final String KEY_ORDINAL = "ordinal";
  public static final String KEY_THING = "thing";

  public static final String MSG_ERROR = "Sorry, I was not able to understand which device you want.";
  public static final String MSG_INVALID_ORDINAL_PREFIX = "Sorry, you may only choose a device up to ";

  public ConfirmThingIntent(DialogFlowRequest request, IntentExtras extras) {
    super(request, extras);
  }

  @Override
  public QueryResponse execute() throws JarvisException {
    List<String> choices = new ArrayList<>();
    String request = null;
    List<DialogFlowContext> contexts = mRequest.getContexts();
    for (DialogFlowContext c : contexts) {
      if (Config.DF_CONFIRM_THING_INTENT_CONTEXT.equals(c.getName())) {
        request = c.getParameters().get(KEY_REQUEST).toString();
        JSONArray choicesJson = (JSONArray) c.getParameters().get(KEY_CHOICES);
        for (int i = 0; i < choicesJson.length(); ++i) {
          choices.add(choicesJson.getString(i));
        }
      }
    }

    String thing = null;
    int ordinal = -1;
    Object ordinalObj = mRequest.getParameters().get().get(KEY_ORDINAL);
    if (!(ordinalObj instanceof String)) {
      ordinal = (int) ordinalObj;
    }

    if (ordinal != -1) {
      if (ordinal > 0 && ordinal <= choices.size()) {
        thing = choices.get(ordinal - 1);
      } else {
        return getInvalidOrdinalResponse(choices.size());
      }
    }

    if (thing == null) {
      String thingTemp = JarvisEngine.getThingName(
          mRequest.getParameters().get().getJSONObject(KEY_THING).getJSONObject(KEY_THING));
      for (String c : choices) {
        if (c.equals(thingTemp) || c.contains(thingTemp)) {
          thing = thingTemp;
        }
      }
    }

    if (thing == null) {
      return getErrorResponse();
    }

    IntentExtras extras = new IntentExtras();
    extras.put(EXTRA_CHOSEN_THING, thing);

    return DialogFlowIntent.getIntent(new DialogFlowRequest(request), extras).execute();
  }

  @Override
  public Optional<Command> getCommand() {
    return Optional.empty();
  }

  private QueryResponse getErrorResponse() {
    QueryResponse response = new QueryResponse();
    response.addFulfillmentMessage(MSG_ERROR);
    return response;
  }

  private QueryResponse getInvalidOrdinalResponse(int ordinal) {
    QueryResponse response = new QueryResponse();
    response.addFulfillmentMessage(MSG_INVALID_ORDINAL_PREFIX + ordinal);
    return response;
  }

  public static QueryResponse getMultipleDeviceResponse(List<Thing> things, String messagePrefix,
      String intent, DialogFlowRequest request) {
    QueryResponse response = new QueryResponse();
    String resultMessage = messagePrefix;

    JSONArray choices = new JSONArray();
    for (int i = 0; i < things.size(); ++i) {
      Thing thing = things.get(i);
      choices.put(thing.getName());
      resultMessage += thing.getName();
      if (i < things.size() - 2) {
        resultMessage += ", ";
      } else if (i < things.size() - 1) {
        resultMessage += " or ";
      }
    }
    resultMessage += "?";
    response.addFulfillmentMessage(resultMessage);

    DialogFlowContext context = new DialogFlowContext(Config.DF_CONFIRM_THING_INTENT_CONTEXT, 1);
    context.addParameter(ConfirmThingIntent.KEY_INTENT, intent);
    context.addParameter(ConfirmThingIntent.KEY_REQUEST, request.getJSON().toString());
    context.addParameter(ConfirmThingIntent.KEY_CHOICES, choices);
    response.addOutContext(context);
    return response;
  }
}
