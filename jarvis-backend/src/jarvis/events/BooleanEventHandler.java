package jarvis.events;

import dialogflow.intent.definitions.DialogFlowIntent;
import jarvis.controllers.definitions.Thing;
import jarvis.controllers.definitions.events.ThingEvent;
import jarvis.controllers.definitions.properties.OnOffStatus;
import jarvis.events.definitions.EventHandler;
import jarvis.listeners.EventConsumer;
import jarvis.util.AdminAlertUtil;
import jarvis.util.JarvisException;

public class BooleanEventHandler extends EventHandler {
    public final boolean value;

    public BooleanEventHandler(EventConsumer consumer, DialogFlowIntent intent, boolean value) {
        super(consumer, intent);
        this.value = value;
    }

    @Override
    public boolean handleMessage(Thing t, ThingEvent e, String message) {
        if(!t.getName().equals(eventConsumer.getThing().getName())) {
            return false;
        }

        if(OnOffStatus.isValueEqualToBoolean(message, value)) {
            try {
                handlerIntent.execute();
            } catch (JarvisException e1) {
                AdminAlertUtil.alertJarvisException(e1);
                e1.printStackTrace();
                return false;
            }
        }
        return true;
    }
}
