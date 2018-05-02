package jarvis.engine;

import jarvis.actions.command.definitions.Command;
import jarvis.listeners.EventConsumer;
import java.util.HashMap;
import java.util.Map;
import java.util.Optional;

public class AliasEngine {
  private static AliasEngine instance;

  private Map<String, Command> mCommandAliases;
  private Map<String, EventConsumer> mEventAliases;

  private AliasEngine() {
    mCommandAliases = new HashMap<>();
    mEventAliases = new HashMap<>();
  }

  public static AliasEngine getInstance() {
    if(instance == null) {
      instance = new AliasEngine();
    }
    return instance;
  }

  public Optional<Command> getCommandAlias(String alias) {
    if(!mCommandAliases.containsKey(alias)) {
      return Optional.empty();
    }
    return Optional.of(mCommandAliases.get(alias));
  }

  public Optional<EventConsumer> getEventAlias(String alias) {
    if(!mEventAliases.containsKey(alias)) {
      return Optional.empty();
    }
    return Optional.of(mEventAliases.get(alias));
  }

  public void setCommandAlias(String alias, Command command) {
    mCommandAliases.put(alias, command);
  }

  public void setEventAlias(String alias, EventConsumer event) {
    mEventAliases.put(alias, event);
  }

  public Map<String, Command> getCommandAliases() {
    return new HashMap<>(mCommandAliases);
  }

  public Map<String, EventConsumer> getEventAliases() {
    return new HashMap<>(mEventAliases);
  }

  public boolean aliasExists(String alias) {
    return mCommandAliases.containsKey(alias) || mEventAliases.containsKey(alias);
  }

}
