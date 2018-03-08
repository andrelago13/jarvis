package jarvis.actions;

import jarvis.actions.definitions.Command;
import jarvis.actions.definitions.CommandResult;
import jarvis.util.TimeUtils;
import org.json.JSONObject;

import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;

public class DelayedCommand extends Command {
    public static final String TAG = "delayedCommand";

    private static final String KEY_TIME_UNIT = "timeUnit";
    private static final String KEY_TIME_VALUE = "timeValue";
    private static final String KEY_COMMAND = "command";

    private Command mCommand;
    private TimeUtils.TimeInfo mTimeInfo;

    public DelayedCommand(Command command, TimeUtils.TimeInfo timeInfo) {
        mCommand = command;
        mTimeInfo = timeInfo;
    }

    @Override
    public CommandResult execute() {
        // TODO use jarvis scheduler
        ScheduledExecutorService executor =
                Executors.newSingleThreadScheduledExecutor();
        executor.schedule(new Runnable() {
            @Override
            public void run() {
                mCommand.execute();
            }
        }, mTimeInfo.value, mTimeInfo.unit);
        return new CommandResult(true);
    }

    @Override
    public CommandResult undo() {
        // TODO use jarvis scheduler
        return new CommandResult(false);
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
        res.put(KEY_TYPE, TAG);
        res.put(KEY_TIME_UNIT, mTimeInfo.unit);
        res.put(KEY_TIME_VALUE, mTimeInfo.value);
        res.put(KEY_COMMAND, mCommand.getJSON());
        return res;
    }
}
