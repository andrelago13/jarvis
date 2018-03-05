package jarvis.controllers.definitions;

import com.sun.istack.internal.NotNull;
import com.sun.istack.internal.Nullable;
import jarvis.controllers.definitions.properties.ThingProperty;
import org.json.JSONObject;

import java.util.*;

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

    public static final Map<Integer, String> typeValues = new HashMap<>();
    static {
        typeValues.put(Type.THING.ordinal(), "thing");
        typeValues.put(Type.ON_OFF_SWITCH.ordinal(), "onOffSwitch");
        typeValues.put(Type.MULTILEVEL_SWITCH.ordinal(), "multilevelSwitch");
        typeValues.put(Type.BINARY_SENSOR.ordinal(), "binarySensor");
        typeValues.put(Type.MULTILEVEL_SENSOR.ordinal(), "multilevelSensor");
        typeValues.put(Type.SMART_PLUG.ordinal(), "smartPlug");
        typeValues.put(Type.ON_OFF_LIGHT.ordinal(), "onOffLight");
        typeValues.put(Type.DIMMABLE_LIGHT.ordinal(), "dimmableLight");
        typeValues.put(Type.ON_OFF_COLOR_LIGHT.ordinal(), "onOffColorLight");
        typeValues.put(Type.DIMMABLE_COLOR_LIGHT.ordinal(), "dimmableColorLight");
    }

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

    protected Thing(Thing t) {
        mName = t.mName;
        mType = t.mType;
        mDescription = t.mDescription;
        mLinks = ThingLinks.Builder.buildFromCopy(t.mLinks);
        mProperties = new ArrayList<>();
        for(ThingProperty property : t.mProperties) {
            mProperties.add(new ThingProperty(property));
        }
    }

    protected Thing(@NotNull JSONObject json) {
        mName = json.getString(NAME_KEY);
        mType = getTypeForString(json.getString(TYPE_KEY));
        if(json.has(DESCRIPTION_KEY)) {
            mDescription = json.getString(DESCRIPTION_KEY);
        }
        if(json.has(LINKS_KEY)) {
            mLinks = ThingLinks.Builder.buildFromJSON(json.getJSONObject(LINKS_KEY));
        } else {
            mLinks = new ThingLinks.Builder().build();
        }
        mProperties = new ArrayList<>();
        if(json.has(PROPERTIES_KEY)) {
            JSONObject properties = json.getJSONObject(PROPERTIES_KEY);
            Set<String> keys = properties.keySet();
            for(String k : keys) {
                mProperties.add(new ThingProperty(k, (properties.getJSONObject(k))));
            }
        }
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
        if(typeValues.containsKey(type.ordinal())) {
            return typeValues.get(type.ordinal());
        }
        return typeValues.get(Type.THING.ordinal());
    }

    public static Type getTypeForString(String type) {
        Set<Integer> keys = typeValues.keySet();
        for(Integer k : keys) {
            if(typeValues.get(k).equals(type)) {
                return Type.values()[k];
            }
        }
        return Type.THING;
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

        protected static Thing buildFromCopy(Thing t) {
            return new Thing(t);
        }

        protected static Thing buildFromJSON(JSONObject json) {
            return new Thing(json);
        }
    }
}
