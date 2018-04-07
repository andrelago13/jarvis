package jarvis.actions.command;

import jarvis.actions.CommandBuilder;
import jarvis.actions.command.definitions.Command;
import jarvis.actions.command.definitions.CommandResult;
import jarvis.controllers.definitions.Thing;
import jarvis.engine.JarvisEngine;
import jarvis.util.JarvisException;
import jarvis.util.TimeUtils;
import java.time.LocalTime;
import java.util.List;
import org.json.JSONObject;

public class RuleCommand extends Command {

  public static final String TAG = "ruleCommand";

  private static final String KEY_TIME_STRING = "timeString";
  private static final String KEY_COMMAND = "command";

  private Command mCommand;
  private LocalTime mLocalTime;
  private int mStage;

  public RuleCommand(Command command, LocalTime time) {
    super();
    mCommand = command;
    mLocalTime = time;
    mStage = 0;
  }

  public RuleCommand(JSONObject command) throws JarvisException {
    super(command);
    mLocalTime = TimeUtils.parseTime(command.getString(KEY_TIME_STRING));

    JSONObject subCommand = command.getJSONObject(KEY_COMMAND);
    mCommand = CommandBuilder.buildFromJSON(subCommand);
    if (mCommand == null) {
      throw new JarvisException("Unable to create nested command from JSON.");
    }

    mStage = 0;
  }

  @Override
  public CommandResult execute() {
    switch (mStage) {
      case 0:
        JarvisEngine.getInstance().addActiveRule(this);
        JarvisEngine.getInstance().scheduleDailyRule(mId, this, mLocalTime);
        mStage = 1;
        return new CommandResult(true);
      case 1:
        return JarvisEngine.getInstance().executeCommand(mCommand);
    }
    return new CommandResult(false);
  }

  @Override
  public CommandResult undo() {
    JarvisEngine.getInstance().removeActiveRule(mId);
    return new CommandResult(JarvisEngine.getInstance().cancelDailyRule(mId));
  }

  @Override
  public String executeString() {
    return "[RuleCommand] Scheduled daily rule for " + TimeUtils.localTimeToString(mLocalTime)
        + " : " + mCommand.executeString();
  }

  @Override
  public String undoString() {
    return "[RuleCommand] Canceled daily rule scheduling for " + TimeUtils
        .localTimeToString(mLocalTime) + " : " + mCommand.executeString();
  }

  @Override
  public String friendlyExecuteString() {
    return mCommand.friendlyExecuteString() + " everyday at " + TimeUtils
        .localTimeToString(mLocalTime);
  }

  @Override
  public JSONObject getJSON() {
    JSONObject res = new JSONObject();
    res.put(KEY_ID, mId);
    res.put(KEY_TYPE, TAG);
    res.put(KEY_TIME_STRING, TimeUtils.localTimeToString(mLocalTime));
    res.put(KEY_COMMAND, mCommand.getJSON());
    return res;
  }

  @Override
  public List<Thing> targetThings() {
    return mCommand.targetThings();
  }

  @Override
  public boolean equals(Command c2) {
    if (!(c2 instanceof RuleCommand)) {
      return false;
    }

    RuleCommand ruleCommand = (RuleCommand) c2;

    if (!mCommand.equals(ruleCommand.mCommand)) {
      return false;
    }

    if (!mLocalTime.equals(ruleCommand.mLocalTime)) {
      return false;
    }

    return true;
  }

  @Override
  public boolean isCancellable() {
    return true;
  }
}
