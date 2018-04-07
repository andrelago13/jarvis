package jarvis.actions.command.definitions;

import jarvis.controllers.definitions.Thing;
import jarvis.engine.JarvisEngine;
import java.util.List;
import java.util.concurrent.ThreadLocalRandom;
import org.json.JSONObject;

public abstract class Command {

  public static final String KEY_TYPE = "type";
  protected static final String KEY_ID = "id";

  protected long mId;

  protected Command() {
    mId = generateID();
  }

  protected Command(JSONObject json) {
    try {
      mId = json.getLong(KEY_ID);
    } catch (Exception e) {
      mId = Long.parseLong(json.getJSONObject(KEY_ID).getString("$numberLong"));
    }
  }

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

  public abstract List<Thing> targetThings();

  public abstract boolean equals(Command c2);

  public long getId() {
    return mId;
  }

  public boolean isCancellable() {
    return false;
  }
}
