package jarvis.actions.command;

import jarvis.actions.CommandBuilder;
import jarvis.actions.command.definitions.Command;
import jarvis.actions.command.definitions.CommandResult;
import jarvis.controllers.definitions.Thing;
import jarvis.engine.JarvisEngine;
import jarvis.util.JarvisException;
import jarvis.util.TimeUtils;
import org.json.JSONObject;

import java.util.List;

public class PeriodCommand extends Command {
    public static final String TAG = "periodCommand";

    private static final String KEY_START_TIMESTAMP = "startTimestamp";
    private static final String KEY_END_TIMESTAMP = "endTimestamp";
    private static final String KEY_COMMAND = "command";

    private Command mCommand;
    private long mStartTimestamp;
    private long mEndTimestamp;
    // 0 - unstarted, 1 - will execute 'do', 2 - will execute 'undo'
    private int mStage = 0;

    public PeriodCommand(Command command, long startTimestamp, long endTimestamp) {
        super();
        mCommand = command;
        mStartTimestamp = startTimestamp;
        mEndTimestamp = endTimestamp;
    }

    public PeriodCommand(JSONObject command) throws JarvisException {
        super(command);
        mStartTimestamp = Long.parseLong(command.getJSONObject(KEY_START_TIMESTAMP).getString("$numberLong"));
        mEndTimestamp = Long.parseLong(command.getJSONObject(KEY_END_TIMESTAMP).getString("$numberLong"));
        JSONObject subCommand = command.getJSONObject(KEY_COMMAND);
        mCommand = CommandBuilder.buildFromJSON(subCommand);
        if(mCommand == null) {
            throw new JarvisException("Unable to create nested command from JSON.");
        }
    }

    @Override
    public CommandResult execute() {
        switch (mStage) {
            case 0:
                JarvisEngine.getInstance().scheduleDelayedActionForTimestamp(mId, this, mStartTimestamp);
                mStage = 1;
                break;
            case 1:
                CommandResult res = mCommand.execute();
                if(res.isSuccessful()) {
                    JarvisEngine.getInstance().scheduleDelayedActionForTimestamp(mId, this, mEndTimestamp);
                    mStage = 2;
                } else {
                    mStage = 3;
                    done();
                }
                return res;
            case 2:
                CommandResult result = mCommand.undo();
                mStage = 3;
                done();
                return result;

        }
        return new CommandResult(false);
    }

    @Override
    public CommandResult undo() {
        return new CommandResult(JarvisEngine.getInstance().cancelAction(mId));
    }

    @Override
    public String executeString() {
        return "[PeriodAction] Scheduled period action " + TimeUtils.friendlyFormatPeriod(mStartTimestamp, mEndTimestamp)
                + " : " + mCommand.executeString();
    }

    @Override
    public String undoString() {
        return "[PeriodAction] Canceled period action : " + mCommand.executeString();
    }

    @Override
    public String friendlyExecuteString() {
        return "Scheduled action " + mCommand.friendlyExecuteString().toLowerCase()
                + TimeUtils.friendlyFormatPeriod(mStartTimestamp, mEndTimestamp);
    }

    @Override
    public JSONObject getJSON() {
        JSONObject res = new JSONObject();
        res.put(KEY_ID, mId);
        res.put(KEY_TYPE, TAG);
        res.put(KEY_START_TIMESTAMP, mStartTimestamp);
        res.put(KEY_END_TIMESTAMP, mEndTimestamp);
        res.put(KEY_COMMAND, mCommand.getJSON());
        return res;
    }

    @Override
    public List<Thing> targetThings() {
        return mCommand.targetThings();
    }

    @Override
    public boolean isCancellable() {
        return mStartTimestamp > System.currentTimeMillis();
    }
}
