package jarvis.listeners;

import com.rabbitmq.client.Channel;
import com.rabbitmq.client.DefaultConsumer;
import jarvis.controllers.definitions.Thing;
import jarvis.controllers.definitions.events.ThingEvent;
import jarvis.engine.JarvisEngine;

public class EventConsumer {
    private Thing mThing;
    private ThingEvent mEvent;

    public EventConsumer(Thing thing, ThingEvent event) {
        mThing = thing;
        mEvent = event;
    }

    public void consume(String message) {
        JarvisEngine.getInstance().handleEvent(mThing, mEvent, message);
    }

    public Thing getThing() {
        return mThing;
    }

    public ThingEvent getEvent() {
        return mEvent;
    }

    public boolean equals(EventConsumer consumer) {
        if(!mThing.getName().equals(consumer.mThing.getName())) {
            return false;
        }

        if(!mEvent.equals(consumer.mEvent)) {
            return false;
        }

        return true;
    }
}
