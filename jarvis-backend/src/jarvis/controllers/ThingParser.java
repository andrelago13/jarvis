package jarvis.controllers;

import static jarvis.controllers.definitions.Thing.TYPE_KEY;

import jarvis.controllers.definitions.Thing;
import java.util.Optional;
import org.json.JSONObject;

public class ThingParser {

  public static Optional<Thing> parseThingFromJson(String jsonStr) {
    JSONObject jsonObj = new JSONObject(jsonStr);
    return parseThingFromJson(jsonObj);
  }

  public static Optional<Thing> parseThingFromJson(JSONObject json) {
    if (!json.has(TYPE_KEY)) {
      return Optional.empty();
    }

    String docType = json.getString(TYPE_KEY);
    if (Thing.getTypeString(Thing.Type.ON_OFF_LIGHT).equals(docType)) {
      return Optional.of(OnOffLight.Builder.buildFromJSON(json));
    } else if (Thing.getTypeString(Thing.Type.TEMPERATURE_SENSOR).equals(docType)) {
      return Optional.of(TemperatureSensor.Builder.buildFromJSON(json));
    }

    return Optional.empty();
  }
}
