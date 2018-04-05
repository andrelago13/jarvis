package jarvis.events;

import jarvis.actions.CommandBuilder;
import jarvis.actions.command.definitions.Command;
import jarvis.controllers.definitions.Thing;
import jarvis.controllers.definitions.events.ThingEvent;
import jarvis.controllers.definitions.properties.OnOffStatus;
import jarvis.engine.JarvisEngine;
import jarvis.events.definitions.EventHandler;
import jarvis.listeners.EventConsumer;
import jarvis.util.JarvisException;
import org.json.JSONObject;

import java.util.Optional;

public class BooleanEventHandler extends EventHandler {
    public final static String TAG = "booleanEventHandler";

    public final static String KEY_VALUE = "value";

    public final boolean value;

    public BooleanEventHandler(EventConsumer consumer, Command command, boolean value) {
        super(TAG, consumer, command);
        this.value = value;
    }

    public BooleanEventHandler(JSONObject json) throws JarvisException {
        super(json);
        if(!TAG.equals(mTag)) {
            throw new JarvisException("JSON does not match BooleanEventHandler");
        }

        try {
            value = json.getBoolean(KEY_VALUE);
        } catch (Exception e) {
            throw new JarvisException("Unable to create BooleanEventHandler from JSON.");
        }
    }

    @Override
    public boolean handleMessage(Thing t, ThingEvent e, String message) {
        if(!t.getName().equals(eventConsumer.getThing().getName())) {
            return false;
        }

        if(OnOffStatus.isValueEqualToBoolean(message, value)) {
            JarvisEngine.getInstance().executeCommand(command);
        }
        return true;
    }

    @Override
    public String friendlyString() {
        StringBuilder builder = new StringBuilder();
        builder.append(eventConsumer.getThing().getName());
        builder.append(" is ");
        builder.append(OnOffStatus.getValueString(value));
        return builder.toString();
    }

    @Override
    public boolean equals(EventHandler handler) {
        if(!super.equals(handler)) {
            return false;
        }

        if(!(handler instanceof BooleanEventHandler)) {
            return false;
        }

        if (((BooleanEventHandler) handler).value != value) {
            return false;
        }

        return true;
    }

    @Override
    public JSONObject toJSON() {
        JSONObject result = super.toJSON();
        result.put(KEY_VALUE, value);
        return result;
    }

    public static Optional<EventHandler> buildFromJSON(JSONObject json) {
        try {
            String tag = json.getString(KEY_TAG);
            if(!TAG.equals(tag)) {
                return Optional.empty();
            }

            Command cmd = CommandBuilder.buildFromJSON(json.getJSONObject(KEY_COMMAND));
            if(cmd == null) {
                return Optional.empty();
            }


        } catch (Exception e) {
            // do nothing
        }

        return Optional.empty();
    }
}
