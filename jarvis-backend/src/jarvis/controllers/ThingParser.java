package jarvis.controllers;

import jarvis.controllers.definitions.Thing;
import org.json.JSONObject;

import java.util.Optional;

import static jarvis.controllers.definitions.Thing.TYPE_KEY;

public class ThingParser {
    public static Optional<Thing> parseThingFromJson(String json) {
        JSONObject jsonObj = new JSONObject(json);
        if(!jsonObj.has(TYPE_KEY)) {
            return Optional.empty();
        }

        String docType = jsonObj.getString(TYPE_KEY);
        if(Thing.getTypeString(Thing.Type.ON_OFF_LIGHT).equals(docType)) {
            return Optional.of(OnOffLight.Builder.buildFromJSON(jsonObj));
        }

        return Optional.empty();
    }
}
