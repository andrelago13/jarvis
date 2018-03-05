package jarvis.actions.definitions;

public interface Command {
    CommandResult execute();
    CommandResult undo();
}
