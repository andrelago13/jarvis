package jarvis.engine;

import jarvis.actions.CommandRunnable;
import jarvis.actions.ScheduledAction;
import jarvis.actions.command.definitions.Command;
import jarvis.actions.command.definitions.CommandResult;
import jarvis.actions.command.util.LoggedCommand;
import jarvis.communication.LoggerCommunication;
import jarvis.communication.ThingInterface;
import jarvis.controllers.OnOffLight;
import jarvis.controllers.TemperatureSensor;
import jarvis.controllers.definitions.Thing;
import jarvis.controllers.definitions.events.ThingEvent;
import jarvis.events.definitions.EventHandler;
import jarvis.events.util.LoggedEventHandler;
import jarvis.listeners.EventConsumer;
import jarvis.listeners.ValueUpdateEventConsumer;
import jarvis.util.TimeUtils;
import java.sql.Timestamp;
import java.time.LocalTime;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.Set;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.ScheduledFuture;
import java.util.concurrent.TimeUnit;
import mongodb.MongoDB;
import org.json.JSONObject;
import rabbitmq.RabbitMQ;
import res.Config;

public class JarvisEngine {

  private static final String KEY_COMMAND_TEXT = "commandText";
  private static final String KEY_COMMAND = "command";
  private static final String KEY_UNDO = "undo";
  private static final String KEY_TIMESTAMP = "timestamp";
  private static final String KEY_SUCCESS = "success";

  private static JarvisEngine instance;

  private Map<Long, ScheduledAction> mScheduledActions;
  private Set<EventConsumer> mActiveConsumers;
  private Map<Long, EventHandler> mActiveHandlers;

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
    mScheduledActions = new HashMap<>();
    mActiveConsumers = new HashSet<>();
    mActiveHandlers = new HashMap<>();

    initEventListeners();
  }

  private void initEventListeners() {
    List<Thing> things = ThingInterface.getThings();
    for (Thing t : things) {
      List<ThingEvent> events = t.getEvents();
      for (ThingEvent e : events) {
        addEventListener(t, e);
      }

      // TODO make value updaters as event handlers. Currently done in {@link EventConsumer}
      /*if(t instanceof TemperatureSensor) {
        ThingEvent e = t.getEvents().get(0);
        ValueUpdateEventConsumer consumer = new ValueUpdateEventConsumer(t, e);
        RabbitMQ.getInstance().addQueueReceiver(e.getHref(), consumer);
      }*/
    }
  }

  public void terminate() {
    RabbitMQ.getInstance().terminate();
  }

  private void addEventListener(Thing t, ThingEvent e) {
    RabbitMQ.getInstance().addQueueReceiver(e.getHref(), new EventConsumer(t, e));
    mActiveConsumers.add(new EventConsumer(t, e));
  }

  public List<Thing> getDefaultThings() {
    ArrayList<Thing> things = new ArrayList<>();

    // Default light
    things.add(OnOffLight.Builder.getDefaultBuilder("bedroom light", "/house").build());
    things.add(OnOffLight.Builder.getDefaultBuilder("living room light", "/house").build());
    things.add(
        TemperatureSensor.Builder.getDefaultBuilder("living room temperature", "/house").build());

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

  public List<ScheduledAction> getScheduledActions() {
    List<ScheduledAction> result = new ArrayList<>();
    Set<Long> keys = mScheduledActions.keySet();
    for (Long k : keys) {
      result.add(mScheduledActions.get(k));
    }
    return result;
  }

  public List<LoggedCommand> getLatestNUserCommands(int n) {
    return ThingInterface.getLatestNUserCommands(n);
  }

  public Optional<Command> getCommand(long id) {
    return ThingInterface.getCommand(id);
  }

  public Optional<Command> getUserCommand(long id) {
    return ThingInterface.getUserCommand(id);
  }

  public void updateThingValue(String thingName, Object value) {
    ValueTracker.getInstance().setValue(thingName, value);
  }

  ///////////////////////////////////
  /////////// INTERNALS /////////////
  ///////////////////////////////////

  private void addScheduling(long id, ScheduledAction action) {
    mScheduledActions.put(id, action);
  }

  private void removeScheduling(long id) {
    mScheduledActions.remove(id);
  }

  private void createScheduling(long id, Command cmd, long timeValue, TimeUnit timeUnit) {
    ScheduledExecutorService executor = Executors.newSingleThreadScheduledExecutor();
    ScheduledFuture future = executor.schedule(new CommandRunnable(cmd), timeValue, timeUnit);
    ScheduledAction action = new ScheduledAction(id, cmd, future);
    addScheduling(id, action);
  }

  private void createRepeatedScheduling(long id, Command cmd, LocalTime desiredTime) {
    long initialDelay = TimeUtils.calculateSecondsToLocalTime(desiredTime);
    long repeatInterval = TimeUnit.DAYS.toSeconds(1);

    ScheduledExecutorService executor = Executors.newSingleThreadScheduledExecutor();
    ScheduledFuture future = executor.scheduleAtFixedRate(
        new CommandRunnable(cmd), initialDelay, repeatInterval, TimeUnit.SECONDS);
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
  //////////// EVENTS API ///////////
  ///////////////////////////////////

  public Set<EventConsumer> getActiveConsumers() {
    return mActiveConsumers;
  }

  public void addEventHandler(EventHandler handler) {
    mActiveHandlers.put(handler.getId(), handler);
    MongoDB.logActiveEventHandler(handler.toJSON());
  }

  public boolean removeEventHandler(long id) {
    if (!mActiveHandlers.containsKey(id)) {
      return false;
    }
    mActiveHandlers.remove(id);
    MongoDB.deleteActiveEventHandler(id);
    return true;
  }

  public void handleEvent(Thing thing, ThingEvent event, String message) {
    Set<Long> keys = mActiveHandlers.keySet();
    for (Long k : keys) {
      mActiveHandlers.get(k).handleMessage(thing, event, message);
    }
  }

  public boolean eventHandlerExists(EventHandler handler) {
    Set<Long> keys = mActiveHandlers.keySet();
    for (Long k : keys) {
      if (mActiveHandlers.get(k).equals(handler)) {
        return true;
      }
    }
    return false;
  }

  public boolean logEventHandled(EventHandler handler) {
    LoggedEventHandler info = new LoggedEventHandler(handler);
    return LoggerCommunication.logEventHandled(info);
  }

  public Set<EventHandler> getEventHandlers() {
    Set<EventHandler> result = new HashSet<>();

    Set<Long> keys = mActiveHandlers.keySet();
    for (Long k : keys) {
      result.add(mActiveHandlers.get(k));
    }

    return result;
  }

  public Optional<EventHandler> getEventHandler(long id) {
    if (!mActiveHandlers.containsKey(id)) {
      return Optional.empty();
    }
    return Optional.of(mActiveHandlers.get(id));
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
  public void scheduleDelayedActionForTimeFromNow(long id, Command cmd,
      TimeUtils.TimeInfo timeInfo) {
    createScheduling(id, cmd, timeInfo.value, timeInfo.unit);
  }

  // Schedules a one-time delayed action
  // Command must call actionCompleted when finished
  public void scheduleDelayedActionForTimestamp(long id, Command cmd, long timestamp) {
    scheduleDelayedActionForTimeFromNow(id, cmd,
        new TimeUtils.TimeInfo(timestamp - (new Date().getTime()), TimeUnit.MILLISECONDS));
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
    createRepeatedScheduling(id, cmd, desiredTime);
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

  // Adds a active rule log
  public boolean addActiveRule(Command rule) {
    return MongoDB.logActiveRule(getCommandJSON(rule, new CommandResult(true), false));
  }

  // Removes an active rule log
  public boolean removeActiveRule(long id) {
    return MongoDB.deleteActiveRule(id);
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
