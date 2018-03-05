package dialogflow.entities;

import res.Config;

public class LightSwitchEntity implements DialogFlowEntity {
    public final static String ENTITY_NAME = Config.LIGHT_SWITCH_ENTITY_NAME;

    @Override
    public String getEntityName() {
        return ENTITY_NAME;
    }

    @Override
    public String getName() {
        return null;
    }
}
