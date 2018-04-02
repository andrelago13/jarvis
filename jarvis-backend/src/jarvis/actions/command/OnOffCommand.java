package jarvis.actions.command;

import jarvis.actions.command.definitions.Command;
import jarvis.actions.command.definitions.CommandResult;
import jarvis.controllers.definitions.Thing;
import jarvis.controllers.definitions.actionables.Toggleable;
import jarvis.controllers.definitions.events.ThingEvent;
import jarvis.controllers.definitions.properties.OnOffStatus;
import jarvis.engine.JarvisEngine;
import jarvis.util.JarvisException;
import org.json.JSONObject;
import rabbitmq.RabbitMQ;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

public class OnOffCommand extends Command {
    public static final String TAG = "onOffCommand";

    public static final String KEY_THING = "thing";
    public static final String KEY_STATUS = "status";

    private Toggleable mToggleable;
    private OnOffStatus mTargetStatus;

    public OnOffCommand(Toggleable toggleable, OnOffStatus targetStatus) {
        mId = generateID();
        mTargetStatus = targetStatus;
        mToggleable = toggleable;
    }

    public OnOffCommand(JSONObject json) throws JarvisException {
        mId = Long.parseLong(json.getJSONObject(KEY_ID).getString("$numberLong"));
        boolean targetStatus = json.getBoolean(KEY_STATUS);
        String toggleableName = json.getString(KEY_THING);
        List<Thing> things = JarvisEngine.getInstance().findThing(toggleableName);
        if(things.size() < 1 || !(things.get(0) instanceof Toggleable)) {
            throw new JarvisException("Unable to create OnOffCommand from provided JSON (no toggleable things found).");
        }

        mToggleable = (Toggleable) things.get(0);
        mTargetStatus = new OnOffStatus(targetStatus);
    }

    @Override
    public CommandResult execute() {
        Optional<Boolean> result;
        if(mTargetStatus.isOn()) {
            result = mToggleable.turnOn();
        } else {
            result = mToggleable.turnOff();
        }

        if(mToggleable instanceof Thing) {
            Thing thing = (Thing) mToggleable;
            for(ThingEvent e : thing.getEvents()) {
                if(e.getType() == ThingEvent.Type.ON_OFF) {
                    String queue = e.getHref().toLowerCase();
                    RabbitMQ.getInstance().sendMessage(queue, mTargetStatus.getStatusString());
                }
            }
        }

        if(result.isPresent()) {
            return new CommandResult(result.get());
        }
        return new CommandResult(true);
    }

    @Override
    public CommandResult undo() {
        Optional<Boolean> result;
        if(mTargetStatus.isOn()) {
            result = mToggleable.turnOff();
        } else {
            result = mToggleable.turnOn();
        }

        if(result.isPresent()) {
            return new CommandResult(result.get());
        }
        return new CommandResult(true);
    }

    @Override
    public String executeString() {
        String result = mTargetStatus.isOn() ? "[OnOffCommand] Executed device on" : "[OnOffCommand] Executed device off";
        if(mToggleable instanceof Thing) {
            result += " (" + ((Thing) mToggleable).getName() + ")";
        }
        return result;
    }

    @Override
    public String undoString() {
        String result = mTargetStatus.isOn() ? "[OnOffCommand] Undo device on" : "[OnOffCommand] Undo device off";
        if(mToggleable instanceof Thing) {
            result += " (" + ((Thing) mToggleable).getName() + ")";
        }
        return result;
    }

    @Override
    public String friendlyExecuteString() {
        if(mToggleable instanceof Thing) {
            return "Turn " + ((Thing) mToggleable).getName() + " " + mTargetStatus.getStatusString();
        } else {
            return "Turn device " + mTargetStatus.getStatusString();
        }
    }

    @Override
    public JSONObject getJSON() {
        JSONObject res = new JSONObject();
        res.put(KEY_ID, mId);
        res.put(KEY_TYPE, TAG);
        if(mToggleable instanceof Thing) {
             res.put(KEY_THING, ((Thing) mToggleable).getName());
        }
        res.put(KEY_STATUS, mTargetStatus.isOn());
        return res;
    }

    @Override
    public List<Thing> targetThings() {
        List<Thing> result = new ArrayList<>();
        if(mToggleable instanceof Thing) {
            result.add((Thing) mToggleable);
        }
        return result;
    }
}
