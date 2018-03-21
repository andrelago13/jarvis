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

public class PeriodRuleCommand extends Command {
    public static final String TAG = "periodRuleCommand";

    private static final String KEY_START_TIME_STRING = "startTimeString";
    private static final String KEY_END_TIME_STRING = "endTimeString";
    private static final String KEY_COMMAND = "command";

    private Command mCommand;
    private LocalTime mStartTime;
    private LocalTime mEndTime;
    private int mStage;

    public PeriodRuleCommand(Command command, LocalTime startTime, LocalTime endTime) {
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
        // TODO
        return null;
    }

    @Override
    public CommandResult undo() {
        return new CommandResult(JarvisEngine.getInstance().cancelDailyRule(mId));
    }

    @Override
    public String executeString() {
        // TODO
        return null;
    }

    @Override
    public String undoString() {
        // TODO
        return null;
    }

    @Override
    public String friendlyExecuteString() {
        // TODO
        return null;
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
        return null;
    }
}
