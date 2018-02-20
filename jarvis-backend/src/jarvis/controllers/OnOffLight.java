package jarvis.controllers;

import jarvis.controllers.definitions.Thing;
import jarvis.controllers.definitions.ThingLinks;
import jarvis.controllers.definitions.actionables.Toggleable;

import java.util.Optional;

public class OnOffLight extends Thing implements Toggleable {
    public OnOffLight(String name) {
        super(name, Type.ON_OFF_LIGHT, null, new ThingLinks.Builder().build());
    }

    public OnOffLight(String name, String description) {
        super(name, Type.ON_OFF_LIGHT, description, new ThingLinks.Builder().build());
    }

    @Override
    public Optional<Boolean> turnOn() {
        return Optional.empty();
    }

    @Override
    public Optional<Boolean> turnOff() {
        return Optional.empty();
    }

    @Override
    public Optional<Boolean> toggle() {
        return Optional.empty();
    }

    @Override
    public Optional<Boolean> isOn() {
        return Optional.empty();
    }
}
