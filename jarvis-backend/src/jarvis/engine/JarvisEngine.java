package jarvis.engine;

import jarvis.communication.ThingInterface;
import jarvis.controllers.OnOffLight;
import jarvis.controllers.definitions.Thing;
import mongodb.MongoDB;

import java.util.ArrayList;
import java.util.List;

public class JarvisEngine {
    private static JarvisEngine instance;

    private JarvisEngine() {init();}

    public static JarvisEngine getInstance() {
        if(instance == null) {
            instance = new JarvisEngine();
        }
        return instance;
    }

    private void init() {
        if(!MongoDB.isInitialized()) {
            MongoDB.initialize(JarvisEngine.getDefaultThings());
        }
    }

    public static List<Thing> getDefaultThings() {
        ArrayList<Thing> things = new ArrayList<>();

        // Default light
        things.add(OnOffLight.Builder.getDefaultBuilder("light", "/room").build());

        return things;
    }

    public static List<Thing> findThing(String tag) {
        List<Thing> result = MongoDB.getThingsByName(tag);
        return result;
    }
}
