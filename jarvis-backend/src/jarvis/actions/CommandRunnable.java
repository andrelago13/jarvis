package jarvis.actions;

import jarvis.actions.command.definitions.Command;

public class CommandRunnable implements Runnable {

  private Command mCommand;

  public CommandRunnable(Command cmd) {
    mCommand = cmd;
  }

  @Override
  public void run() {
    mCommand.execute();
  }
}
