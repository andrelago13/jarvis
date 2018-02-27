package jarvis.engine;

import jarvis.communication.ThingInterface;

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
}
