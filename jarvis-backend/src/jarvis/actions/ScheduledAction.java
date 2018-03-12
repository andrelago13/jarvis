package jarvis.actions;

import jarvis.actions.command.definitions.Command;

import java.util.concurrent.ScheduledFuture;

public class ScheduledAction {
    private long mId;
    private ScheduledFuture mFuture;
    private Command mCommand;

    public ScheduledAction(long id, Command cmd, ScheduledFuture future) {
        mId = id;
        mCommand = cmd;
        mFuture = future;
    }

    public long getId() {
        return mId;
    }

    public ScheduledFuture getFuture() {
        return mFuture;
    }

    public Command getCommand() {
        return mCommand;
    }
}
