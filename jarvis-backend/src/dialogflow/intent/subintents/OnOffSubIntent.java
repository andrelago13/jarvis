package dialogflow.intent.subintents;

import dialogflow.DialogFlowRequest;
import dialogflow.QueryResponse;
import dialogflow.intent.ConfirmThingIntent;
import dialogflow.intent.definitions.DialogFlowIntent;
import dialogflow.intent.definitions.IntentExtras;
import jarvis.actions.command.OnOffCommand;
import jarvis.actions.command.definitions.Command;
import jarvis.actions.command.definitions.CommandResult;
import jarvis.controllers.definitions.Thing;
import jarvis.controllers.definitions.actionables.Toggleable;
import jarvis.controllers.definitions.properties.OnOffStatus;
import jarvis.engine.JarvisEngine;
import jarvis.util.JarvisException;
import java.util.List;
import java.util.Optional;
import org.json.JSONObject;

public class OnOffSubIntent extends DialogFlowIntent {

  public static final String TAG = "OnOffSubIntent";

  public static final String MSG_SUCCESS = "Done!";
  public static final String MSG_ERROR = "Sorry, I was not able to do that.";
  public static final String MSG_ALREADY_STATE = "The device is already ";
  public static final String MSG_DEVICE_NOT_FOUND = "The device you requested was not found.";
  public static final String MSG_MULTIPLE_DEVICES_PREFIX = "Do you mean ";
  public static final String MSG_DEVICE_NOT_SUPPORTED = "The specified device does not support the toggle feature.";

  private static final String KEY_STATUS = "status";
  private static final String KEY_ACTUATOR = "actuator";

  private JSONObject mParameters;
  private Command mCommand;
  private String mResultMessage;
  private QueryResponse mResponse;

  public OnOffSubIntent(DialogFlowRequest request, JSONObject parameters, IntentExtras extras) {
    super(request, extras);
    mParameters = parameters;
  }

  private static boolean isStateDifferent(Toggleable device, OnOffStatus status) {
    Optional<Boolean> isOn = device.isOn();
    if (status.isOn() && isOn.isPresent() && isOn.get()) {
      return false;
    } else if (!status.isOn() && isOn.isPresent() && !isOn.get()) {
      return false;
    }

    return true;
  }

  @Override
  public QueryResponse execute() throws JarvisException {
    QueryResponse response = new QueryResponse();
    if (mResponse != null) {
      return mResponse;
    }

    Optional<Command> cmd = getCommand();
    if (cmd.isPresent()) {
      CommandResult result = JarvisEngine.getInstance().executeCommand(cmd.get());
      if (result.isSuccessful()) {
        mResultMessage = MSG_SUCCESS;
      }
    }

    response.addFulfillmentMessage(mResultMessage);
    return response;
  }

  private List<Thing> getThings(JSONObject actuator) {
    List<Thing> things;
    if (mExtras.hasKey(ConfirmThingIntent.EXTRA_CHOSEN_THING)) {
      String thingName = (String) mExtras.get(ConfirmThingIntent.EXTRA_CHOSEN_THING);
      things = JarvisEngine.getInstance().findThing(thingName);
    } else {
      things = JarvisEngine.getInstance().findThingLike(actuator);
    }
    return things;
  }

  private Optional<Command> computeCommand(JSONObject actuator, OnOffStatus status) {
    List<Thing> things = getThings(actuator);

    if (things.isEmpty()) {
      mResultMessage = MSG_DEVICE_NOT_FOUND;
    } else if (things.size() > 1) {
      mResponse = ConfirmThingIntent
          .getMultipleDeviceResponse(things, MSG_MULTIPLE_DEVICES_PREFIX, TAG, mRequest);
    } else if (things.get(0) instanceof Toggleable) {
      Toggleable device = (Toggleable) things.get(0);
      if (isStateDifferent(device, status)) {
        mCommand = new OnOffCommand(device, status);
      } else {
        mResultMessage = MSG_ALREADY_STATE + status.getStatusString();
      }
    } else {
      mResultMessage = MSG_DEVICE_NOT_SUPPORTED;
    }

    if (mCommand == null) {
      return Optional.empty();
    }
    return Optional.of(mCommand);
  }

  @Override
  public Optional<Command> getCommand() {
    if (mCommand != null) {
      return Optional.of(mCommand);
    }
    OnOffStatus status = new OnOffStatus(mParameters.getString(KEY_STATUS));
    JSONObject actuator = mParameters.getJSONObject(KEY_ACTUATOR);

    return computeCommand(actuator, status);
  }

  @Override
  public QueryResponse getFollowUpRequest() {
    if (mExtras.hasKey(ConfirmThingIntent.EXTRA_CHOSEN_THING)) {
      return null;
    }

    JSONObject actuator = mParameters.getJSONObject(KEY_ACTUATOR);
    List<Thing> things = JarvisEngine.getInstance().findThingLike(actuator);
    if (things.size() > 1) {
      return ConfirmThingIntent
          .getMultipleDeviceResponse(things, MSG_MULTIPLE_DEVICES_PREFIX, TAG, mRequest);
    }

    return null;
  }
}
