package jarvis.actions;

import jarvis.actions.definitions.Command;
import jarvis.actions.definitions.CommandResult;
import jarvis.controllers.definitions.actionables.Toggleable;
import jarvis.controllers.definitions.properties.OnOffStatus;

import java.util.Optional;

public class OnOffAction implements Command {
    private Toggleable mToggleable;
    private OnOffStatus mTargetStatus;

    public OnOffAction(Toggleable toggleable, OnOffStatus targetStatus) {
        mTargetStatus = targetStatus;
        mToggleable = toggleable;
    }

    @Override
    public CommandResult execute() {
        Optional<Boolean> result;
        if(mTargetStatus.isOn()) {
            result = mToggleable.turnOn();
        } else {
            result = mToggleable.turnOff();
        }

        if(result.isPresent()) {
            return new CommandResult(result.get());
        }
        return new CommandResult(true);
    }

    @Override
    public CommandResult undo() {
        Optional<Boolean> result;
        if(mTargetStatus.isOn()) {
            result = mToggleable.turnOff();
        } else {
            result = mToggleable.turnOn();
        }

        if(result.isPresent()) {
            return new CommandResult(result.get());
        }
        return new CommandResult(true);
    }
}
