package jarvis.events;

import dialogflow.intent.definitions.DialogFlowIntent;
import jarvis.events.definitions.EventHandler;
import jarvis.listeners.EventConsumer;

public class BooleanEventHandler extends EventHandler {
    public final boolean value;

    public BooleanEventHandler(EventConsumer consumer, DialogFlowIntent intent, boolean value) {
        super(consumer, intent);
        this.value = value;
    }
}
