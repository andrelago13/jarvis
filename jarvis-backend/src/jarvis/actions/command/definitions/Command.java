package jarvis.actions.command.definitions;

import jarvis.engine.JarvisEngine;
import org.json.JSONObject;

import java.util.concurrent.ThreadLocalRandom;

public abstract class Command {
    public static final String KEY_TYPE = "type";
    protected static final String KEY_ID = "id";

    protected long mId;

    protected static long generateID() {
        return ThreadLocalRandom.current().nextLong(0, Long.MAX_VALUE);
    }

    protected void done() {
        JarvisEngine.getInstance().actionCompleted(mId);
    }

    public abstract CommandResult execute();
    public abstract CommandResult undo();
    public abstract String executeString();
    public abstract String undoString();
    public abstract String friendlyExecuteString();
    public abstract JSONObject getJSON();

    public long getId() {
        return mId;
    }

    public boolean isCancellable() {
        return false;
    }
}
