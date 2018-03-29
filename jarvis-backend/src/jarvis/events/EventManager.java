package jarvis.events;

import dialogflow.intent.definitions.DialogFlowIntent;
import jarvis.controllers.definitions.events.ThingEvent;
import jarvis.controllers.definitions.properties.OnOffStatus;
import jarvis.engine.JarvisEngine;
import jarvis.events.definitions.EventHandler;
import jarvis.listeners.EventConsumer;
import org.json.JSONObject;

import java.util.Optional;
import java.util.Set;

public class EventManager {
    public static final String KEY_ON_OFF_ACTUATOR = "on-off-actuator";
    public static final String KEY_ON_OFF_STATUS = "on-off-status";

    public static Optional<EventHandler> findThingEvent(JSONObject event, DialogFlowIntent handlerIntent) {
        if(event.has(KEY_ON_OFF_ACTUATOR)) {
            JSONObject thing = event.getJSONObject(KEY_ON_OFF_ACTUATOR);
            String thingName = JarvisEngine.getThingName(thing);
            boolean onOffStatus = OnOffStatus.isValueOn(event.getString(KEY_ON_OFF_STATUS));

            Set<EventConsumer> consumers = JarvisEngine.getInstance().getActiveConsumers();
            for(EventConsumer consumer : consumers) {
                if(thingName.equals(consumer.getThing().getName()) &&
                        consumer.getEvent().getType() == ThingEvent.Type.ON_OFF) {
                    return Optional.of(new BooleanEventHandler(consumer, handlerIntent, onOffStatus));
                }
            }
        }

        return Optional.empty();
    }
}
