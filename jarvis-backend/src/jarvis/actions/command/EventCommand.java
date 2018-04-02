package jarvis.actions.command;

import dialogflow.DialogFlowRequest;
import dialogflow.intent.definitions.DialogFlowIntent;
import dialogflow.intent.definitions.IntentExtras;
import dialogflow.intent.subintents.ActionFinder;
import jarvis.actions.CommandBuilder;
import jarvis.actions.command.definitions.Command;
import jarvis.actions.command.definitions.CommandResult;
import jarvis.controllers.definitions.Thing;
import jarvis.engine.JarvisEngine;
import jarvis.events.EventManager;
import jarvis.events.definitions.EventHandler;
import jarvis.util.JarvisException;
import jarvis.util.TimeUtils;
import org.json.JSONObject;
import res.Config;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.concurrent.TimeUnit;

public class EventCommand extends Command {
    public static final String TAG = "eventCommand";

    public static final String KEY_EVENT = "event";
    public static final String KEY_COMMAND = "command";

    private JSONObject mEvent;
    private Command mCommand;
    private EventHandler mEventHandler;

    public EventCommand(JSONObject event, Command command, EventHandler eventHandler) {
        super();
        mEvent = event;
        mEventHandler = eventHandler;
        mCommand = command;
    }

    public EventCommand(JSONObject command) throws JarvisException {
        mId = Long.parseLong(command.getJSONObject(KEY_ID).getString("$numberLong"));
        mEvent = command.getJSONObject(KEY_EVENT);
        mCommand = CommandBuilder.buildFromJSON(command.getJSONObject(KEY_COMMAND));

        Optional<EventHandler> optHandler = EventManager.findThingEvent(mEvent, mCommand);
        if(!optHandler.isPresent()) {
            throw new JarvisException("EventCommand: Unable to parse intent.");
        }
        mEventHandler = optHandler.get();
    }

    @Override
    public CommandResult execute() {
        JarvisEngine.getInstance().addEventHandler(mId, mEventHandler);
        return new CommandResult(true);
    }

    @Override
    public CommandResult undo() {
        return new CommandResult(JarvisEngine.getInstance().removeEventHandler(mId));
    }

    @Override
    public String executeString() {
        return "[Event] do " + mCommand.executeString() + " when " + mEventHandler.friendlyString();
    }

    @Override
    public String undoString() {
        return "[Event] cancel doing " + mCommand.executeString() + " when " + mEventHandler.friendlyString();
    }

    @Override
    public String friendlyExecuteString() {
        return mCommand.friendlyExecuteString() + " when " + mEventHandler.friendlyString();
    }

    @Override
    public JSONObject getJSON() {
        JSONObject res = new JSONObject();
        res.put(KEY_ID, mId);
        res.put(KEY_TYPE, TAG);
        res.put(KEY_EVENT, mEvent);
        res.put(KEY_COMMAND, mCommand.getJSON());
        return res;
    }

    @Override
    public List<Thing> targetThings() {
        List<Thing> res = new ArrayList<>();
        res.add(mEventHandler.eventConsumer.getThing());
        return res;
    }

    @Override
    public boolean isCancellable() {
        return true;
    }
}
