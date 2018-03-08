package jarvis.actions;

import java.util.concurrent.ScheduledFuture;

public class ScheduledAction {
    private long mId;
    private ScheduledFuture mFuture;

    public ScheduledAction(long id, ScheduledFuture future) {
        mId = id;
        mFuture = future;
    }

    public long getId() {
        return mId;
    }

    public ScheduledFuture getFuture() {
        return mFuture;
    }
}
