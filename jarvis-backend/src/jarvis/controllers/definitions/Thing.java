package jarvis.controllers.definitions;

import com.sun.istack.internal.NotNull;
import com.sun.istack.internal.Nullable;
import jarvis.controllers.definitions.properties.ThingProperty;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

public class Thing {
    public final static String NAME_KEY = "name";
    public final static String TYPE_KEY = "type";
    public final static String DESCRIPTION_KEY = "description";
    public final static String LINKS_KEY = "links";
    public final static String PROPERTIES_KEY = "properties";

    protected String mName;
    protected Type mType;
    protected String mDescription;
    protected ThingLinks mLinks;
    protected List<ThingProperty> mProperties;

    /**
     * Enum to represent the different IoT device types (from https://iot.mozilla.org/wot).
     * <p>
     * Thing: The default device type of "thing" can be used when no more specific device type is more appropriate. The
     * only mandatory fields for a Thing Description of this type are the "name" and “type” fields.
     * <p>
     * On/Off Switch: On/Off Switch is a generic device type for actuators with a boolean on/off state. This may be used
     * for smart plugs and light switches for example.
     * <p>
     * Multilevel Switch: A switch with multiple levels such as a dimmer switch.
     * <p>
     * Binary Sensor: Binary Sensor is a generic device type for sensors with a boolean on/off state. This may be used
     * for smart door, infrared movement or flood sensors for example.
     * <p>
     * Multilevel Sensor: A generic multi level sensor with a value which can be expressed as a percentage.
     * <p>
     * Smart Plug: A smart plug has a boolean on/off state and measures current power consumption. It may also have a
     * level setting and report voltage, current and frequency.
     * <p>
     * On/Off Light: A light that can be turned on and off like an LED or a bulb.
     * <p>
     * Dimmable Light: A light that can have its brightness level set.
     * <p>
     * On/Off Color Light: A colored light that can be switched on and off.
     * <p>
     * Dimmable Color Light: A colored light that can have its brightness level set.
     */
    public enum Type {
        THING,
        ON_OFF_SWITCH,
        MULTILEVEL_SWITCH,
        BINARY_SENSOR,
        MULTILEVEL_SENSOR,
        SMART_PLUG,
        ON_OFF_LIGHT,
        DIMMABLE_LIGHT,
        ON_OFF_COLOR_LIGHT,
        DIMMABLE_COLOR_LIGHT
    }

    public final static String THING_TYPE = "thing";
    public final static String ON_OFF_SWITCH_TYPE = "onOffSwitch";
    public final static String MULTILEVEL_SWITCH_TYPE = "multilevelSwitch";
    public final static String BINARY_SENSOR_TYPE = "binarySensor";
    public final static String MULTILEVEL_SENSOR_TYPE = "multilevelSensor";
    public final static String SMART_PLUG_TYPE = "smartPlug";
    public final static String ON_OFF_LIGHT_TYPE = "onOffLight";
    public final static String DIMMABLE_LIGHT_TYPE = "dimmableLight";
    public final static String ON_OFF_COLOR_LIGHT_TYPE = "onOffColorLight";
    public final static String DIMMABLE_COLOR_LIGHT_TYPE = "dimmableColorLight";

    protected Thing(@NotNull String name,
                    @NotNull Type type,
                    @Nullable String description,
                    @NotNull ThingLinks links,
                    @NotNull List<ThingProperty> properties) {
        if (name == null || type == null || links == null || properties == null) {
            throw new IllegalArgumentException(
                    "Not nullable constructor parameters were null, please read documentation.");
        }
        mName = name;
        mType = type;
        mDescription = description;
        mLinks = links;
        mProperties = properties;
    }

    protected Thing(@NotNull JSONObject json) {
        mName = json.getString(NAME_KEY);
        // TODO: implement parser
    }

    public JSONObject toJSON() {
        JSONObject result = new JSONObject();

        //base params
        result.put(NAME_KEY, mName);
        result.put(TYPE_KEY, getTypeString(mType));
        if(hasDescription()) {
            result.put(DESCRIPTION_KEY, mDescription);
        }

        //properties
        if(!mProperties.isEmpty()) {
            JSONObject properties = new JSONObject();
            for(ThingProperty property : mProperties) {
                properties.put(property.getName(), property.toJSON());
            }
            result.put(PROPERTIES_KEY, properties);
        }

        //links
        if(mLinks.hasLinks()) {
            result.put(LINKS_KEY, mLinks.toJSON());
        }

        // TODO: implement actions and events
        //actions
        //events

        return result;
    }

    public String toString() {
        return toJSON().toString();
    }

    public String getName() {
        return mName;
    }

    public Type getType() {
        return mType;
    }

    public boolean hasDescription() {
        return mDescription != null;
    }

    public Optional<String> getDescription() {
        if (!hasDescription()) {
            return Optional.empty();
        }
        return Optional.of(mDescription);
    }

    public static String getTypeString(Type type) {
        switch (type) {
            case THING:
                return THING_TYPE;
            case ON_OFF_SWITCH:
            case MULTILEVEL_SWITCH:
                return MULTILEVEL_SWITCH_TYPE;
            case BINARY_SENSOR:
                return BINARY_SENSOR_TYPE;
            case MULTILEVEL_SENSOR:
                return MULTILEVEL_SENSOR_TYPE;
            case SMART_PLUG:
                return SMART_PLUG_TYPE;
            case ON_OFF_LIGHT:
                return ON_OFF_LIGHT_TYPE;
            case DIMMABLE_LIGHT:
                return DIMMABLE_LIGHT_TYPE;
            case ON_OFF_COLOR_LIGHT:
                return ON_OFF_COLOR_LIGHT_TYPE;
            case DIMMABLE_COLOR_LIGHT:
                return DIMMABLE_COLOR_LIGHT_TYPE;
        }
        return THING_TYPE;
    }

    public static class Builder {
        protected String mName;
        protected Type mType;
        protected String mDescription;
        protected ThingLinks mLinks;
        protected List<ThingProperty> mProperties;

        protected Builder() {
            mProperties = new ArrayList<>();
        }

        protected Thing build() {
            return new Thing(mName, mType, mDescription, mLinks, mProperties);
        }

        protected void setName(String name) {
            mName = name;
        }

        protected void setType(Type type) {
            mType = type;
        }

        protected void setDescription(String description) {
            mDescription = description;
        }

        protected void setLinks(ThingLinks links) {
            mLinks = links;
        }

        protected void setProperties(List<ThingProperty> properties) {
            mProperties = properties;
        }

        protected void addProperty(ThingProperty property) {
            if(mProperties == null) {
                mProperties = new ArrayList<>();
            }
            mProperties.add(property);
        }

        protected boolean isValid() {
            return mName != null && mType != null && mLinks != null && mProperties != null;
        }
    }
}
