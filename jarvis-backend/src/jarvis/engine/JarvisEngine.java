package jarvis.engine;

import jarvis.actions.definitions.Command;
import jarvis.actions.definitions.CommandResult;
import jarvis.communication.LoggerCommunication;
import jarvis.communication.ThingInterface;
import jarvis.controllers.OnOffLight;
import jarvis.controllers.definitions.Thing;
import mongodb.MongoDB;
import org.json.JSONObject;

import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.List;

public class JarvisEngine {
    private static final String KEY_COMMAND_TEXT = "commandText";
    private static final String KEY_COMMAND = "command";
    private static final String KEY_UNDO = "undo";
    private static final String KEY_TIMESTAMP = "timestamp";
    private static final String KEY_SUCCESS = "success";

    private static JarvisEngine instance;

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
    }

    public static List<Thing> getDefaultThings() {
        ArrayList<Thing> things = new ArrayList<>();

        // Default light
        things.add(OnOffLight.Builder.getDefaultBuilder("light", "/room").build());

        return things;
    }

    public static List<Thing> findThing(String tag) {
        List<Thing> result = ThingInterface.getThingsByName(tag);
        return result;
    }

    public static CommandResult executeCommand(Command cmd) {
        CommandResult res = cmd.execute();
        logCommand(getCommandJSON(cmd, res, false));
        return res;
    }

    public static CommandResult undoCommand(Command cmd) {
        CommandResult res = cmd.undo();
        logCommand(getCommandJSON(cmd, res, true));
        return res;
    }

    private static boolean logCommand(JSONObject commandJSON) {
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
        json.put(KEY_TIMESTAMP, timestamp.toString());
        json.put(KEY_SUCCESS, result.isSuccessful());
        return json;
    }
}
