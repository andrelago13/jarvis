package jarvis.controllers;

import jarvis.controllers.definitions.Thing;

public class OnOffLight extends Thing {
    public OnOffLight(String name) {
        super(name, Type.ON_OFF_LIGHT);
    }

    public OnOffLight(String name, String description) {
        super(name, Type.ON_OFF_LIGHT, description);
    }
}
