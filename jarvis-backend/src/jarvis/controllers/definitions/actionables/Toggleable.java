package jarvis.controllers.definitions.actionables;

import java.util.Optional;

public interface Toggleable {
    Optional<Boolean> turnOn();
    Optional<Boolean> turnOff();
    Optional<Boolean> toggle();
    Optional<Boolean> isOn();
}
