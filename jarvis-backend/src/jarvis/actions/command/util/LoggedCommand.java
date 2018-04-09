package jarvis.actions.command.util;

import static jarvis.actions.CommandBuilder.KEY_COMMAND;

import jarvis.actions.CommandBuilder;
import jarvis.actions.command.definitions.Command;
import jarvis.util.JarvisException;
import org.json.JSONObject;

public class LoggedCommand {
  public static final String KEY_TIMESTAMP = "timestamp";

  private Command mCommand;
  private long mTimestamp;

  public LoggedCommand(JSONObject json) throws JarvisException {
    try {
      mTimestamp = json.getLong(KEY_TIMESTAMP);
      mCommand = CommandBuilder.buildFromJSON(json.getJSONObject(KEY_COMMAND));
      if(mCommand == null) {
        throw new Exception();
      }
    } catch (Exception e) {
      throw new JarvisException("Unable to parse LoggedCommand.");
    }
  }

  public LoggedCommand(Command command, long timestamp) {
    mCommand = command;
    mTimestamp = timestamp;
  }

  public LoggedCommand(Command cmd) {
    this(cmd, System.currentTimeMillis());
  }

  public long getTimestamp() {
    return mTimestamp;
  }

  public Command getCommand() {
    return mCommand;
  }

  public JSONObject toJSON() {
    JSONObject res = new JSONObject();
    res.put(KEY_TIMESTAMP, mTimestamp);
    res.put(CommandBuilder.KEY_COMMAND, mCommand.getJSON());
    return res;
  }
}
