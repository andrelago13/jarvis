package jarvis.actions.definitions;

import java.util.Optional;

public class CommandResult {
    private Optional<Object> mResult;
    private boolean mSuccess;

    public CommandResult(boolean success, Object result) {
        mSuccess = success;
        mResult = Optional.of(result);
    }

    public CommandResult(boolean success) {
        mSuccess = success;
        mResult = Optional.empty();
    }

    public Optional<Object> getResult() {
        return mResult;
    }

    public boolean isSuccessful() {
        return mSuccess;
    }
}
