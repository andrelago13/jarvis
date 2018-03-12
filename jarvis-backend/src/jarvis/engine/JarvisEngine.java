package jarvis.engine;

import jarvis.actions.CommandRunnable;
import jarvis.actions.ScheduledAction;
import jarvis.actions.command.definitions.Command;
import jarvis.actions.command.definitions.CommandResult;
import jarvis.communication.LoggerCommunication;
import jarvis.communication.ThingInterface;
import jarvis.controllers.OnOffLight;
import jarvis.controllers.definitions.Thing;
import jarvis.util.TimeUtils;
import mongodb.MongoDB;
import org.json.JSONObject;

import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.ScheduledFuture;

public class JarvisEngine {
    private static final String KEY_COMMAND_TEXT = "commandText";
    private static final String KEY_COMMAND = "command";
    private static final String KEY_UNDO = "undo";
    private static final String KEY_TIMESTAMP = "timestamp";
    private static final String KEY_SUCCESS = "success";

    private static JarvisEngine instance;

    private Map<Long, ScheduledAction> mScheduledActions;

    private JarvisEngine() {init();}

    public static JarvisEngine getInstance() {
        if(instance == null) {
            instance = new JarvisEngine();
        }
        return instance;
    }

    private void init() {
        ThingInterface.init(getDefaultThings());
        LoggerCommunication.init(getDefaultThings());
        // TODO get actions from backup
        mScheduledActions = new HashMap<>();
    }

    public List<Thing> getDefaultThings() {
        ArrayList<Thing> things = new ArrayList<>();

        // Default light
        things.add(OnOffLight.Builder.getDefaultBuilder("light", "/room").build());

        return things;
    }

    public List<Thing> findThing(String tag) {
        return ThingInterface.getThingsByName(tag);
    }

    public List<Command> getLatestNCommands(int n) {
        return ThingInterface.getLatestNCommands(n);
    }

    public Map<Long, ScheduledAction> getScheduledActions() {
        return mScheduledActions;
    }

    public void scheduleAction(long id, Command cmd, TimeUtils.TimeInfo timeInfo) {
        ScheduledExecutorService executor =
                Executors.newSingleThreadScheduledExecutor();
        ScheduledFuture future = executor.schedule(new CommandRunnable(cmd), timeInfo.value, timeInfo.unit);
        ScheduledAction action = new ScheduledAction(id, cmd, future);
        mScheduledActions.put(id, action);
    }

    public boolean cancelAction(long id) {
        if(!mScheduledActions.containsKey(id)) {
            return false;
        }
        ScheduledFuture future = mScheduledActions.get(id).getFuture();
        mScheduledActions.remove(id);
        return future.cancel(false);
    }

    public CommandResult executeCommand(Command cmd) {
        CommandResult res = cmd.execute();
        logCommand(getCommandJSON(cmd, res, false));
        return res;
    }

    public CommandResult undoCommand(Command cmd) {
        CommandResult res = cmd.undo();
        logCommand(getCommandJSON(cmd, res, true));
        return res;
    }

    private boolean logCommand(JSONObject commandJSON) {
        return MongoDB.logCommand(commandJSON);
    }

    private static JSONObject getCommandJSON(Command cmd, CommandResult result, boolean undo) {
        JSONObject json = new JSONObject();
        if(undo) {
            json.put(KEY_COMMAND_TEXT, cmd.undoString());
            json.put(KEY_UNDO, true);
        } else {
            json.put(KEY_COMMAND_TEXT, cmd.executeString());
            json.put(KEY_UNDO, false);
        }
        json.put(KEY_COMMAND, cmd.getJSON());
        Timestamp timestamp = new Timestamp(System.currentTimeMillis());
        json.put(KEY_TIMESTAMP, timestamp.getTime());
        json.put(KEY_SUCCESS, result.isSuccessful());
        return json;
    }
}
