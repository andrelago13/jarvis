package jarvis.actions.definitions;

import org.json.JSONObject;

public abstract class Command {
    public static final String KEY_TYPE = "type";

    public abstract CommandResult execute();
    public abstract CommandResult undo();
    public abstract String executeString();
    public abstract String undoString();
    public abstract JSONObject getJSON();
}
