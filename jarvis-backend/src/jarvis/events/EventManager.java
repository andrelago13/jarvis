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
import res.Config;

public class EventManager {

  public static Optional<EventHandler> findThingEvent(JSONObject event, Command cmd) {
    if (event.has(Config.DF_ON_OFF_ACTUATOR_ENTITY_NAME)) {
      JSONObject thing = event.getJSONObject(Config.DF_ON_OFF_ACTUATOR_ENTITY_NAME);
      String thingName = JarvisEngine.getThingName(thing);
      boolean onOffStatus = OnOffStatus.isValueOn(event.getString(Config.DF_ON_OFF_STATUS_ENTITY_NAME));

      Set<EventConsumer> consumers = JarvisEngine.getInstance().getActiveConsumers();
      for (EventConsumer consumer : consumers) {
        if (thingName.equals(consumer.getThing().getName()) &&
            consumer.getEvent().getType() == ThingEvent.Type.ON_OFF) {
          return Optional.of(new BooleanEventHandler(consumer, cmd, onOffStatus));
        }
      }
    } else if (event.has(Config.DF_TEMPERATURE_SENSOR_ENTITY_NAME)) {
      String sensorName = event.getString(Config.DF_TEMPERATURE_SENSOR_ENTITY_NAME);
      Optional<Temperature> temperature = Temperature.buildFromJSON(event.getJSONObject(Temperature.KEY_TEMPERATURE));
      Condition condition = ValueEventHandler.parseConditionFromEventWithDefault(event, Condition.EQUAL_TO);
      if(temperature.isPresent()) {
        Set<EventConsumer> consumers = JarvisEngine.getInstance().getActiveConsumers();
        for (EventConsumer consumer : consumers) {
          if (sensorName.equals(consumer.getThing().getName()) &&
              consumer.getEvent().getType() == Type.VALUE) {
            return Optional.of(new ValueEventHandler(consumer, cmd, temperature.get(), condition));
          }
        }
      }
    } else if (event.has(Config.DF_BINARY_SENSOR_ENTITY_NAME)) {
      String sensorName = event.getString(Config.DF_BINARY_SENSOR_ENTITY_NAME);
      if(event.has(Config.DF_ON_OFF_STATUS_ENTITY_NAME)) {
        boolean desiredStatus = OnOffStatus.isValueOn(event.getString(Config.DF_ON_OFF_STATUS_ENTITY_NAME));
        Set<EventConsumer> consumers = JarvisEngine.getInstance().getActiveConsumers();
        for (EventConsumer consumer : consumers) {
          if (sensorName.equals(consumer.getThing().getName()) &&
              consumer.getEvent().getType() == Type.TRIGGER) {
            return Optional.of(new BinaryTriggerEventHandler(consumer, cmd, desiredStatus));
          }
        }
      }
    }

    return Optional.empty();
  }
}
