package jarvis.controllers.definitions;

public abstract class Thing {
    protected String mName;
    protected Type mType;
    protected String mDescription;

    /**
     * Enum to represent the different IoT device types (from https://iot.mozilla.org/wot).
     *
     * Thing: The default device type of "thing" can be used when no more specific device type is more appropriate. The
     * only mandatory fields for a Thing Description of this type are the "name" and “type” fields.
     *
     * On/Off Switch: On/Off Switch is a generic device type for actuators with a boolean on/off state. This may be used
     * for smart plugs and light switches for example.
     *
     * Multilevel Switch: A switch with multiple levels such as a dimmer switch.
     *
     * Binary Sensor: Binary Sensor is a generic device type for sensors with a boolean on/off state. This may be used
     * for smart door, infrared movement or flood sensors for example.
     *
     * Multilevel Sensor: A generic multi level sensor with a value which can be expressed as a percentage.
     *
     * Smart Plug: A smart plug has a boolean on/off state and measures current power consumption. It may also have a
     * level setting and report voltage, current and frequency.
     *
     * On/Off Light: A light that can be turned on and off like an LED or a bulb.
     *
     * Dimmable Light: A light that can have its brightness level set.
     *
     * On/Off Color Light: A colored light that can be switched on and off.
     *
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

    protected Thing(String name, Type type) {
        mName = name;
        mType = type;
    }

    protected Thing(String name, Type type, String description) {
        mName = name;
        mType = type;
        mDescription = description;
    }
}
