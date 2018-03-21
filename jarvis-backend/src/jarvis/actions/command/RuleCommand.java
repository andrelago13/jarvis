package jarvis.actions.command;

import jarvis.actions.CommandBuilder;
import jarvis.actions.CommandRunnable;
import jarvis.actions.command.definitions.Command;
import jarvis.actions.command.definitions.CommandResult;
import jarvis.controllers.definitions.Thing;
import jarvis.engine.JarvisEngine;
import jarvis.util.JarvisException;
import jarvis.util.TimeUtils;
import org.json.JSONObject;

import java.time.*;
import java.util.List;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

public class RuleCommand extends Command {
    public static final String TAG = "ruleCommand";

    private static final String KEY_TIME_STRING = "timeString";
    private static final String KEY_COMMAND = "command";

    private Command mCommand;
    private LocalTime mLocalTime;
    private int mStage;

    public RuleCommand(Command command, LocalTime time) {
        mId = generateID();
        mCommand = command;
        mLocalTime = time;
        mStage = 0;
    }

    public RuleCommand(JSONObject command) throws JarvisException {
        mId = Long.parseLong(command.getJSONObject(KEY_ID).getString("$numberLong"));
        mLocalTime = TimeUtils.parseTime(command.getString(KEY_TIME_STRING));

        JSONObject subCommand = command.getJSONObject(KEY_COMMAND);
        mCommand = CommandBuilder.buildFromJSON(subCommand);
        if(mCommand == null) {
            throw new JarvisException("Unable to create nested command from JSON.");
        }

        mStage = 0;
    }

    @Override
    public CommandResult execute() {
        // TODO implement
        switch (mStage) {
            case 0:
                JarvisEngine.getInstance().scheduleDailyRule(mId, this, mLocalTime);
                mStage = 1;
                break;
            case 1:
                return JarvisEngine.getInstance().executeCommand(mCommand);
        }
        return new CommandResult(false);
    }

    @Override
    public CommandResult undo() {
        return new CommandResult(JarvisEngine.getInstance().cancelDailyRule(mId));
    }

    @Override
    public String executeString() {
        return "[PeriodAction] Scheduled daily rule for " + TimeUtils.localTimeToString(mLocalTime) + " : " + mCommand.executeString();
    }

    @Override
    public String undoString() {
        return "[PeriodAction] Canceled daily rule scheduling for " + TimeUtils.localTimeToString(mLocalTime) + " : " + mCommand.executeString();
    }

    @Override
    public String friendlyExecuteString() {
        return mCommand.friendlyExecuteString() + " everyday at " + TimeUtils.localTimeToString(mLocalTime);
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
    public boolean isCancellable() {
        return true;
    }
}
