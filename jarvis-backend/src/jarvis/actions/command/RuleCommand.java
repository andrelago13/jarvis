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
//        switch (mStage) {
//            case 0:
//                JarvisEngine.getInstance().scheduleAction(mId, this, mStartTimestamp);
//                mStage = 1;
//                break;
//            case 1:
//                CommandResult res = mCommand.execute();
//                if(res.isSuccessful()) {
//                    JarvisEngine.getInstance().scheduleAction(mId, this, mEndTimestamp);
//                    mStage = 2;
//                } else {
//                    mStage = 3;
//                    done();
//                }
//                return res;
//            case 2:
//                CommandResult result = mCommand.undo();
//                mStage = 3;
//                done();
//                return result;
//
//        }
        return new CommandResult(false);
    }

    @Override
    public CommandResult undo() {
        // TODO implement
        return new CommandResult(JarvisEngine.getInstance().cancelAction(mId));
    }

    @Override
    public String executeString() {
        // TODO implement
        return "";//return "[PeriodAction] Scheduled period action " + TimeUtils.friendlyFormatPeriod(mStartTimestamp, mEndTimestamp)
        //        + " : " + mCommand.executeString();
    }

    @Override
    public String undoString() {
        // TODO implement
        return "[PeriodAction] Canceled period action : " + mCommand.executeString();
    }

    @Override
    public String friendlyExecuteString() {
        // TODO implement
        return "";//return "Scheduled action " + mCommand.friendlyExecuteString().toLowerCase() + TimeUtils.friendlyFormatPeriod(mStartTimestamp, mEndTimestamp);
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
        // TODO implement
        return mCommand.targetThings();
    }

    @Override
    public boolean isCancellable() {
        // TODO implement
        return false;//return mStartTimestamp > System.currentTimeMillis();
    }
}
