package jarvis.actions.command;

import jarvis.actions.CommandBuilder;
import jarvis.actions.command.definitions.Command;
import jarvis.actions.command.definitions.CommandResult;
import jarvis.controllers.definitions.Thing;
import jarvis.engine.JarvisEngine;
import jarvis.util.JarvisException;
import jarvis.util.TimeUtils;
import org.json.JSONObject;

import java.time.LocalTime;
import java.util.List;
import java.util.concurrent.TimeUnit;

public class PeriodRuleCommand extends Command {
    public static final String TAG = "periodRuleCommand";

    private static final String KEY_START_TIME_STRING = "startTimeString";
    private static final String KEY_END_TIME_STRING = "endTimeString";
    private static final String KEY_COMMAND = "command";

    private Command mCommand;
    private LocalTime mStartTime;
    private LocalTime mEndTime;
    // 0 - unstarted; 1 - must do first action; 2 - must do second action
    private int mStage;

    public PeriodRuleCommand(Command command, LocalTime startTime, LocalTime endTime) {
        mId = generateID();
        mCommand = command;
        mStartTime = startTime;
        mEndTime = endTime;
        mStage = 0;
    }

    public PeriodRuleCommand(JSONObject command) throws JarvisException {
        mId = Long.parseLong(command.getJSONObject(KEY_ID).getString("$numberLong"));
        mStartTime = TimeUtils.parseTime(command.getString(KEY_START_TIME_STRING));
        mEndTime = TimeUtils.parseTime(command.getString(KEY_END_TIME_STRING));

        JSONObject subCommand = command.getJSONObject(KEY_COMMAND);
        mCommand = CommandBuilder.buildFromJSON(subCommand);
        if(mCommand == null) {
            throw new JarvisException("Unable to create nested command from JSON.");
        }

        mStage = 0;
    }

    @Override
    public CommandResult execute() {
        switch (mStage) {
            case 0:
                JarvisEngine.getInstance().addActiveRule(this);
                long initialDelay = TimeUtils.calculateSecondsToLocalTime(mStartTime);
                TimeUtils.TimeInfo initInfo = new TimeUtils.TimeInfo(initialDelay, TimeUnit.SECONDS);
                JarvisEngine.getInstance().scheduleDelayedActionForTimeFromNow(mId, this, initInfo);
                mStage = 1;
                return new CommandResult(true);
            case 1:
                JarvisEngine.getInstance().executeCommand(mCommand);
                long secondTaskDelay = TimeUtils.calculateSecondsToLocalTime(mEndTime);
                TimeUtils.TimeInfo secondInfo = new TimeUtils.TimeInfo(secondTaskDelay, TimeUnit.SECONDS);
                JarvisEngine.getInstance().scheduleDelayedActionForTimeFromNow(mId, this, secondInfo);
                mStage = 2;
                return new CommandResult(true);
            case 2:
                JarvisEngine.getInstance().undoCommand(mCommand);
                long firstTaskDelay = TimeUtils.calculateSecondsToLocalTime(mEndTime);
                TimeUtils.TimeInfo firstInfo = new TimeUtils.TimeInfo(firstTaskDelay, TimeUnit.SECONDS);
                JarvisEngine.getInstance().scheduleDelayedActionForTimeFromNow(mId, this, firstInfo);
                mStage = 1;
                return new CommandResult(true);
        }
        return new CommandResult(false);
    }

    @Override
    public CommandResult undo() {
        JarvisEngine.getInstance().removeActiveRule(mId);
        return new CommandResult(JarvisEngine.getInstance().cancelAction(mId));
    }

    @Override
    public String executeString() {
        return "[PeriodRuleCommand] Scheduled daily rule from " + TimeUtils.localTimeToString(mStartTime)
                + " to " + TimeUtils.localTimeToString(mEndTime) + " : " + mCommand.executeString();
    }

    @Override
    public String undoString() {
        return "[PeriodRuleCommand] Cancelled daily rule scheduling from " + TimeUtils.localTimeToString(mStartTime)
                + " to " + TimeUtils.localTimeToString(mEndTime) + " : " + mCommand.executeString();
    }

    @Override
    public String friendlyExecuteString() {
        return mCommand.friendlyExecuteString() + " everyday from " + TimeUtils.localTimeToString(mStartTime)
                + " to " + TimeUtils.localTimeToString(mEndTime);
    }

    @Override
    public JSONObject getJSON() {
        JSONObject res = new JSONObject();
        res.put(KEY_ID, mId);
        res.put(KEY_TYPE, TAG);
        res.put(KEY_START_TIME_STRING, TimeUtils.localTimeToString(mStartTime));
        res.put(KEY_END_TIME_STRING, TimeUtils.localTimeToString(mEndTime));
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
