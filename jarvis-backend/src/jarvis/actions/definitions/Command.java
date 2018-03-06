package jarvis.actions.definitions;

import org.json.JSONObject;

public interface Command {
    CommandResult execute();
    CommandResult undo();
    String executeString();
    String undoString();
    JSONObject getJSON();
}
