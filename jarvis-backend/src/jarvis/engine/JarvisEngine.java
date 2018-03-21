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
import res.Config;
import slack.SlackUtil;

import java.sql.Timestamp;
import java.time.*;
import java.util.*;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.ScheduledFuture;
import java.util.concurrent.TimeUnit;

public class JarvisEngine {
    private static final String KEY_COMMAND_TEXT = "commandText";
    private static final String KEY_COMMAND = "command";
    private static final String KEY_UNDO = "undo";
    private static final String KEY_TIMESTAMP = "timestamp";
    private static final String KEY_SUCCESS = "success";

    private static JarvisEngine instance;

    private Map<Long, ScheduledAction> mScheduledActions;

    private JarvisEngine() {
        init();
    }

    public static JarvisEngine getInstance() {
        if (instance == null) {
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
        things.add(OnOffLight.Builder.getDefaultBuilder("bedroom light", "/house").build());
        things.add(OnOffLight.Builder.getDefaultBuilder("living room light", "/house").build());

        return things;
    }

    public List<Thing> findThing(String tag) {
        return ThingInterface.getThingsByName(tag);
    }

    public List<Thing> findThing(JSONObject thing) {
        String name = getThingName(thing);
        if (name == null) {
            return new ArrayList<>();
        }
        return findThing(name);
    }

    public List<Thing> findThingLike(String tag) {
        return ThingInterface.getThingsByNameLike(tag);
    }

    public List<Thing> findThingLike(JSONObject thing) {
        String name = getThingName(thing);
        if (name == null) {
            return new ArrayList<>();
        }
        return findThingLike(name);
    }

    public static String getThingName(JSONObject thing) {
        if (thing.has(Config.DF_LIGHT_SWITCH_ENTITY_NAME)) {
            return thing.getString(Config.DF_LIGHT_SWITCH_ENTITY_NAME);
        }
        return null;
    }

    public Map<Long, ScheduledAction> getScheduledActions() {
        return mScheduledActions;
    }

    public List<Command> getLatestNUserCommands(int n) {
        return ThingInterface.getLatestNUserCommands(n);
    }

    ///////////////////////////////////
    /////////// INTERNALS /////////////
    ///////////////////////////////////

    private void addScheduling(long id, ScheduledAction action) {
        // TODO add to active schedulings
        mScheduledActions.put(id, action);
    }

    private void removeScheduling(long id) {
        // TODO remove from active schedulings
        mScheduledActions.remove(id);
    }

    private void createScheduling(long id, Command cmd, long timeValue, TimeUnit timeUnit) {
        ScheduledExecutorService executor = Executors.newSingleThreadScheduledExecutor();
        ScheduledFuture future = executor.schedule(new CommandRunnable(cmd), timeValue, timeUnit);
        ScheduledAction action = new ScheduledAction(id, cmd, future);
        addScheduling(id, action);
    }

    private void createRepeatedScheduling(long id, Command cmd, long initialDelay, long interval, TimeUnit timeUnit) {
        ScheduledExecutorService executor = Executors.newSingleThreadScheduledExecutor();
        ScheduledFuture future = executor.scheduleAtFixedRate(
                new CommandRunnable(cmd), initialDelay, interval, TimeUnit.SECONDS);
        ScheduledAction action = new ScheduledAction(id, cmd, future);
        addScheduling(id, action);
    }

    private boolean cancelScheduling(long id) {
        if (!mScheduledActions.containsKey(id)) {
            return false;
        }
        ScheduledFuture future = mScheduledActions.get(id).getFuture();
        removeScheduling(id);
        return future.cancel(false);
    }

    ///////////////////////////////////
    ////////// EXECUTION API //////////
    ///////////////////////////////////

    // Executes and logs a thing command
    public CommandResult executeCommand(Command cmd) {
        CommandResult res = cmd.execute();
        logCommand(getCommandJSON(cmd, res, false));
        return res;
    }

    // Undoes and logs a thing command
    public CommandResult undoCommand(Command cmd) {
        CommandResult res = cmd.undo();
        logUserCommand(getCommandJSON(cmd, res, true));
        return res;
    }

    // Logs a thing command
    private boolean logCommand(JSONObject commandJSON) {
        return LoggerCommunication.logCommand(commandJSON);
    }

    // Schedules a one-time delayed action
    // Command must call actionCompleted when finished
    public void scheduleDelayedAction(long id, Command cmd, TimeUtils.TimeInfo timeInfo) {
        createScheduling(id, cmd, timeInfo.value, timeInfo.unit);
    }

    // Schedules a one-time delayed action
    // Command must call actionCompleted when finished
    public void scheduleDelayedAction(long id, Command cmd, long timestamp) {
        scheduleDelayedAction(id, cmd, new TimeUtils.TimeInfo(timestamp - (new Date().getTime()), TimeUnit.MILLISECONDS));
    }

    // Marks a one-time delayed action as completed, removing its object from the active schedulings list
    public void actionCompleted(long id) {
        removeScheduling(id);
    }

    // Cancels a one-time delayed action
    public boolean cancelAction(long id) {
        return cancelScheduling(id);
    }

    // Schedules a daily repeating rule
    // Command must call actionCompleted when finished
    public void scheduleDailyRule(long id, Command cmd, LocalTime desiredTime) {
        LocalDateTime localNow = LocalDateTime.now();
        ZoneId currentZone = ZoneId.systemDefault();
        ZonedDateTime zonedNow = ZonedDateTime.of(localNow, currentZone);
        ZonedDateTime zonedDesiredTime;
        zonedDesiredTime = zonedNow.withHour(desiredTime.getHour())
                .withMinute(desiredTime.getMinute())
                .withSecond(desiredTime.getSecond());

        long initialDelay = Duration.between(zonedNow, zonedDesiredTime).getSeconds();
        long repeatInterval = TimeUnit.DAYS.toSeconds(1);
        createRepeatedScheduling(id, cmd, initialDelay, repeatInterval, TimeUnit.SECONDS);
    }

    // Cancels a daily repeating rule
    public boolean cancelDailyRule(long id) {
        return cancelScheduling(id);
    }

    // Logs a user command
    public boolean logUserCommand(Command cmd) {
        return logUserCommand(cmd, true);
    }

    // Logs a user command
    public boolean logUserCommand(Command cmd, boolean success) {
        return logUserCommand(cmd, new CommandResult(success));
    }

    // Logs a user command
    public boolean logUserCommand(Command cmd, CommandResult result) {
        return logUserCommand(getCommandJSON(cmd, result, false));
    }

    // Logs a user command
    public boolean logUserCommand(JSONObject cmdJSON) {
        return MongoDB.logUserCommand(cmdJSON);
    }

    ///////////////////////////////////
    ////////// JSON UTILS /////////////
    ///////////////////////////////////

    // Converts a command to a loggable JSON object
    private static JSONObject getCommandJSON(Command cmd, CommandResult result, boolean undo) {
        JSONObject json = new JSONObject();
        addStringParameters(json, cmd, undo);
        json.put(KEY_COMMAND, cmd.getJSON());
        Timestamp timestamp = new Timestamp(System.currentTimeMillis());
        json.put(KEY_TIMESTAMP, timestamp.getTime());
        json.put(KEY_SUCCESS, result.isSuccessful());
        return json;
    }

    private static void addStringParameters(JSONObject obj, Command cmd, boolean undo) {
        if (undo) {
            obj.put(KEY_COMMAND_TEXT, cmd.undoString());
            obj.put(KEY_UNDO, true);
        } else {
            obj.put(KEY_COMMAND_TEXT, cmd.executeString());
            obj.put(KEY_UNDO, false);
        }
    }
}
