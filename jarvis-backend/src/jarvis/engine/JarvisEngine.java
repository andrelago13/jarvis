package jarvis.engine;

import jarvis.communication.ThingInterface;
import jarvis.controllers.definitions.Thing;

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
        ThingInterface.getThings();
        // TODO: get objects from message service
    }

    public static List<Thing> getDefaultThings() {
        List<Thing> result = new ArrayList<>();

        //OnOffLight light = new OnOffLight("light");
        //result.add(light);

        return result;
    }
}
