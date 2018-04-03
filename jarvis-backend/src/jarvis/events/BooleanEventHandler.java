package jarvis.events;

import jarvis.actions.command.definitions.Command;
import jarvis.controllers.definitions.Thing;
import jarvis.controllers.definitions.events.ThingEvent;
import jarvis.controllers.definitions.properties.OnOffStatus;
import jarvis.engine.JarvisEngine;
import jarvis.events.definitions.EventHandler;
import jarvis.listeners.EventConsumer;

public class BooleanEventHandler extends EventHandler {
    public final boolean value;

    public BooleanEventHandler(EventConsumer consumer, Command command, boolean value) {
        super(consumer, command);
        this.value = value;
    }

    @Override
    public boolean handleMessage(Thing t, ThingEvent e, String message) {
        if(!t.getName().equals(eventConsumer.getThing().getName())) {
            return false;
        }

        if(OnOffStatus.isValueEqualToBoolean(message, value)) {
            JarvisEngine.getInstance().executeCommand(command);
        }
        return true;
    }

    @Override
    public String friendlyString() {
        StringBuilder builder = new StringBuilder();
        builder.append(eventConsumer.getThing().getName());
        builder.append(" is ");
        builder.append(OnOffStatus.getValueString(value));
        return builder.toString();
    }

    @Override
    public boolean equals(EventHandler handler) {
        if(!super.equals(handler)) {
            return false;
        }

        if(!(handler instanceof BooleanEventHandler)) {
            return false;
        }

        if (((BooleanEventHandler) handler).value != value) {
            return false;
        }

        return true;
    }

}
