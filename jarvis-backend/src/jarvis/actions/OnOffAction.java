package jarvis.actions;

import jarvis.actions.definitions.Command;
import jarvis.actions.definitions.CommandResult;
import jarvis.controllers.definitions.Thing;
import jarvis.controllers.definitions.actionables.Toggleable;
import jarvis.controllers.definitions.properties.OnOffStatus;
import jarvis.engine.JarvisEngine;
import jarvis.util.JarvisException;
import org.json.JSONObject;

import java.util.List;
import java.util.Optional;

public class OnOffAction extends Command {
    public static final String TAG = "onOffAction";

    public static final String KEY_THING = "thing";
    public static final String KEY_STATUS = "status";

    private Toggleable mToggleable;
    private OnOffStatus mTargetStatus;

    public OnOffAction(Toggleable toggleable, OnOffStatus targetStatus) {
        mTargetStatus = targetStatus;
        mToggleable = toggleable;
    }

    public OnOffAction(JSONObject json) throws JarvisException {
        boolean targetStatus = json.getBoolean(KEY_STATUS);
        String toggleableName = json.getString(KEY_THING);
        List<Thing> things = JarvisEngine.getInstance().findThing(toggleableName);
        if(things.size() < 1 || !(things.get(0) instanceof Toggleable)) {
            throw new JarvisException("Unable to create OnOffAction from provided JSON (no toggleable things found).");
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
        String result = mTargetStatus.isOn() ? "[OnOffAction] Executed device on" : "[OnOffAction] Executed device off";
        if(mToggleable instanceof Thing) {
            result += " (" + ((Thing) mToggleable).getName() + ")";
        }
        return result;
    }

    @Override
    public String undoString() {
        String result = mTargetStatus.isOn() ? "[OnOffAction] Undo device on" : "[OnOffAction] Undo device off";
        if(mToggleable instanceof Thing) {
            result += " (" + ((Thing) mToggleable).getName() + ")";
        }
        return result;
    }

    @Override
    public JSONObject getJSON() {
        JSONObject res = new JSONObject();
        res.put(KEY_TYPE, TAG);
        if(mToggleable instanceof Thing) {
             res.put(KEY_THING, ((Thing) mToggleable).getName());
        }
        res.put(KEY_STATUS, mTargetStatus.isOn());
        return res;
    }
}
