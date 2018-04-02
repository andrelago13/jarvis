package jarvis.controllers.definitions.events;

import jarvis.controllers.definitions.Thing;
import org.json.JSONObject;

import java.util.Optional;
import java.util.Set;

public class ThingEvent {
    public static final String KEY_TYPE = "type";
    public static final String KEY_UNIT = "unit";
    public static final String KEY_DESCRIPTION = "description";
    public static final String KEY_HREF = "href";

    public enum Type {
        OVERHEAT,
        ON_OFF,
        TRIGGER,
        VALUE
    }

    protected Type mType;
    protected String mValueType;
    protected String mValueUnit;
    protected String mDescription;
    protected String mHref;

    public ThingEvent(ThingEvent event) {
        init(event.mType, event.mValueType, event.mValueUnit, event.mDescription, event.mHref);
    }

    public ThingEvent(Type type, String valueType, String valueUnit, String description, String href) {
        init(type, valueType, valueUnit, description, href);
    }

    public ThingEvent(JSONObject event, String typeStr, Thing thing) {
        init(Type.valueOf(typeStr),
                event.getString(KEY_TYPE),
                event.getString(KEY_UNIT),
                event.getString(KEY_DESCRIPTION),
                event.getString(KEY_HREF));
    }

    protected void init(Type type, String valueType, String valueUnit, String description, String href) {
        mType = type;
        mValueType = valueType;
        mValueUnit = valueUnit;
        mDescription = description;
        mHref = href;
    }

    public Type getType() {
        return mType;
    }

    public String getValueType() {
        return mValueType;
    }

    public String getValueUnit() {
        return mValueUnit;
    }

    public String getmDescription() {
        return mDescription;
    }

    public String getHref() {
        return mHref;
    }

    public JSONObject toJSON() {
        JSONObject event = new JSONObject();
        event.put(KEY_TYPE, mValueType);
        event.put(KEY_UNIT, mValueUnit);
        event.put(KEY_DESCRIPTION, mDescription);
        event.put(KEY_HREF, mHref);
        return event;
    }

    public String toString() {
        return mValueType;
    }
}
