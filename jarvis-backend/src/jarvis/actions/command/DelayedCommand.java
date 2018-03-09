package jarvis.actions.command;

import jarvis.actions.CommandBuilder;
import jarvis.actions.command.definitions.Command;
import jarvis.actions.command.definitions.CommandResult;
import jarvis.engine.JarvisEngine;
import jarvis.util.JarvisException;
import jarvis.util.TimeUtils;
import org.json.JSONObject;

import java.util.concurrent.TimeUnit;

public class DelayedCommand extends Command {
    public static final String TAG = "delayedCommand";

    private static final String KEY_TIME_UNIT = "timeUnit";
    private static final String KEY_TIME_VALUE = "timeValue";
    private static final String KEY_TARGET_TIMESTAMP = "targetTimestamp";
    private static final String KEY_COMMAND = "command";

    private Command mCommand;
    private TimeUtils.TimeInfo mTimeInfo;
    private long mTargetTimestamp;

    public DelayedCommand(Command command, TimeUtils.TimeInfo timeInfo, long targetTimestamp) {
        mId = generateID();
        mCommand = command;
        mTimeInfo = timeInfo;
        mTargetTimestamp = targetTimestamp;
    }

    public DelayedCommand(JSONObject command) throws JarvisException {
        mId = Long.parseLong(command.getJSONObject(KEY_ID).getString("$numberLong"));
        mTargetTimestamp = Long.parseLong(command.getJSONObject(KEY_TARGET_TIMESTAMP).getString("$numberLong"));
        int timeValue = command.getInt(KEY_TIME_VALUE);
        String timeUnit = command.getString(KEY_TIME_UNIT);
        JSONObject subCommand = command.getJSONObject(KEY_COMMAND);
        mTimeInfo = new TimeUtils.TimeInfo(timeValue, TimeUnit.valueOf(timeUnit));
        mCommand = CommandBuilder.buildFromJSON(subCommand);
        if(mCommand == null) {
            throw new JarvisException("Unable to create nested command from JSON.");
        }
    }

    @Override
    public CommandResult execute() {
        JarvisEngine.getInstance().scheduleAction(mId, mCommand, mTimeInfo);
        return new CommandResult(true);
    }

    @Override
    public CommandResult undo() {
        return new CommandResult(JarvisEngine.getInstance().cancelAction(mId));
    }

    @Override
    public String executeString() {
        return "[DelayedAction] Scheduled action for " + mTimeInfo.toString() + " : " + mCommand.executeString();
    }

    @Override
    public String undoString() {
        return "[DelayedAction] Canceled action schedule : " + mCommand.executeString();
    }

    @Override
    public String friendlyExecuteString() {
        return "Schedule action " + mCommand.friendlyExecuteString() + " in "
                + mTimeInfo.toString() + ", on " + TimeUtils.friendlyFormat(mTargetTimestamp);
    }

    @Override
    public JSONObject getJSON() {
        JSONObject res = new JSONObject();
        res.put(KEY_ID, mId);
        res.put(KEY_TYPE, TAG);
        res.put(KEY_TIME_UNIT, mTimeInfo.unit);
        res.put(KEY_TIME_VALUE, mTimeInfo.value);
        res.put(KEY_TARGET_TIMESTAMP, mTargetTimestamp);
        res.put(KEY_COMMAND, mCommand.getJSON());
        return res;
    }

    @Override
    public boolean isCancellable() {
        return mTargetTimestamp > System.currentTimeMillis();
    }
}