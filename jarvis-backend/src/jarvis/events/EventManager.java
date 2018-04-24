package jarvis.events;

import jarvis.actions.command.definitions.Command;
import jarvis.controllers.definitions.events.ThingEvent;
import jarvis.controllers.definitions.events.ThingEvent.Type;
import jarvis.controllers.definitions.properties.OnOffStatus;
import jarvis.engine.JarvisEngine;
import jarvis.events.ValueEventHandler.Condition;
import jarvis.events.definitions.EventHandler;
import jarvis.listeners.EventConsumer;
import jarvis.util.Temperature;
import java.util.Optional;
import java.util.Set;
import org.json.JSONObject;

public class EventManager {

  public static final String KEY_ON_OFF_ACTUATOR = "on-off-actuator";
  public static final String KEY_ON_OFF_STATUS = "on-off-status";
  public static final String KEY_TEMPERATURE_SENSOR = "temperature_sensor";

  public static Optional<EventHandler> findThingEvent(JSONObject event, Command cmd) {
    if (event.has(KEY_ON_OFF_ACTUATOR)) {
      JSONObject thing = event.getJSONObject(KEY_ON_OFF_ACTUATOR);
      String thingName = JarvisEngine.getThingName(thing);
      boolean onOffStatus = OnOffStatus.isValueOn(event.getString(KEY_ON_OFF_STATUS));

      Set<EventConsumer> consumers = JarvisEngine.getInstance().getActiveConsumers();
      for (EventConsumer consumer : consumers) {
        if (thingName.equals(consumer.getThing().getName()) &&
            consumer.getEvent().getType() == ThingEvent.Type.ON_OFF) {
          return Optional.of(new BooleanEventHandler(consumer, cmd, onOffStatus));
        }
      }
    } else if (event.has(KEY_TEMPERATURE_SENSOR)) {
      String sensorName = event.getString(KEY_TEMPERATURE_SENSOR);
      Optional<Temperature> temperature = Temperature.buildFromJSON(event.getJSONObject(Temperature.KEY_TEMPERATURE));
      Condition condition = ValueEventHandler.parseConditionFromEventWithDefault(event, Condition.EQUAL_TO);
      if(temperature.isPresent()) {
        // FIXME value updaters are not in event consumers
        Set<EventConsumer> consumers = JarvisEngine.getInstance().getActiveConsumers();
        for (EventConsumer consumer : consumers) {
          if (sensorName.equals(consumer.getThing().getName()) &&
              consumer.getEvent().getType() == Type.VALUE) {
            return Optional.of(new ValueEventHandler(consumer, cmd, temperature.get(), condition));
          }
        }
      }
    }

    return Optional.empty();
  }
}
