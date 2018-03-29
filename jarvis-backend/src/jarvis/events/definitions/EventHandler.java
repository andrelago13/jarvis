package jarvis.events.definitions;

import dialogflow.intent.definitions.DialogFlowIntent;
import jarvis.controllers.definitions.Thing;
import jarvis.controllers.definitions.events.ThingEvent;
import jarvis.listeners.EventConsumer;

public abstract class EventHandler {
    public final EventConsumer eventConsumer;
    public final DialogFlowIntent handlerIntent;

    public EventHandler(EventConsumer consumer, DialogFlowIntent intent) {
        eventConsumer = consumer;
        handlerIntent = intent;
    }

    public boolean handleMessage(Thing t, ThingEvent e, String message) {
        return false;
    }
}
