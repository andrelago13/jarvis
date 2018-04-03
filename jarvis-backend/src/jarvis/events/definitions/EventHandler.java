package jarvis.events.definitions;

import dialogflow.intent.definitions.DialogFlowIntent;
import jarvis.actions.command.definitions.Command;
import jarvis.controllers.definitions.Thing;
import jarvis.controllers.definitions.events.ThingEvent;
import jarvis.listeners.EventConsumer;

public abstract class EventHandler {
    public final EventConsumer eventConsumer;
    public final Command command;

    public EventHandler(EventConsumer consumer, Command command) {
        eventConsumer = consumer;
        this.command = command;
    }

    public boolean handleMessage(Thing t, ThingEvent e, String message) {
        return false;
    }

    public abstract String friendlyString();

    public boolean equals(EventHandler handler) {
        if(!eventConsumer.equals(handler.eventConsumer)) {
            return false;
        }

        // TODO compare command

        return true;
    }
}
