package jarvis.actions.command;

import jarvis.actions.CommandBuilder;
import jarvis.actions.command.definitions.Command;
import jarvis.actions.command.definitions.CommandResult;
import jarvis.engine.JarvisEngine;
import jarvis.util.JarvisException;
import jarvis.util.TimeUtils;
import org.json.JSONObject;

import java.util.concurrent.ThreadLocalRandom;
import java.util.concurrent.TimeUnit;

public class DelayedCommand extends Command {
    public static final String TAG = "delayedCommand";

    private static final String KEY_TIME_UNIT = "timeUnit";
    private static final String KEY_TIME_VALUE = "timeValue";
    private static final String KEY_COMMAND = "command";
    private static final String KEY_ID = "id";

    private Command mCommand;
    private TimeUtils.TimeInfo mTimeInfo;
    private long mId;

    public DelayedCommand(Command command, TimeUtils.TimeInfo timeInfo) {
        mCommand = command;
        mTimeInfo = timeInfo;
        mId = generateID();
    }

    public DelayedCommand(JSONObject command) throws JarvisException {
        long id = Long.parseLong(command.getJSONObject(KEY_ID).getString("$numberLong"));
        int timeValue = command.getInt(KEY_TIME_VALUE);
        String timeUnit = command.getString(KEY_TIME_UNIT);
        JSONObject subCommand = command.getJSONObject(KEY_COMMAND);

        mId = id;
        mCommand = CommandBuilder.buildFromJSON(subCommand);
        if(mCommand == null) {
            throw new JarvisException("Unable to create nested command from JSON.");
        }
        mTimeInfo = new TimeUtils.TimeInfo(timeValue, TimeUnit.valueOf(timeUnit));
    }

    private static long generateID() {
        return ThreadLocalRandom.current().nextLong(0, Long.MAX_VALUE);
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
        return "[DelayedAction] Scheduled action for " + mTimeInfo.value + " " +
                mTimeInfo.unit.toString() + " : " + mCommand.executeString();
    }

    @Override
    public String undoString() {
        return "[DelayedAction] Canceled action schedule : " + mCommand.executeString();
    }

    @Override
    public JSONObject getJSON() {
        JSONObject res = new JSONObject();
        res.put(KEY_ID, mId);
        res.put(KEY_TYPE, TAG);
        res.put(KEY_TIME_UNIT, mTimeInfo.unit);
        res.put(KEY_TIME_VALUE, mTimeInfo.value);
        res.put(KEY_COMMAND, mCommand.getJSON());
        return res;
    }
}
