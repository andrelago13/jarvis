package jarvis.controllers.definitions.properties;

import jarvis.util.Unit;
import org.json.JSONObject;

import java.util.Optional;

import static jarvis.util.Unit.UnitType;

public class ThingProperty {
    public static final String TYPE_KEY = "type";
    public static final String UNIT_KEY = "unit";
    public static final String DESCRIPTION_KEY = "description";
    public static final String HREF_KEY = "href";

    public enum Type {
        BOOLEAN,
        NUMBER,
        OTHER
    }

    public static final String TYPE_BOOLEAN = "boolean";
    public static final String TYPE_NUMBER = "number";
    public static final String TYPE_OTHER = "other";

    private String mName;
    private Type mType;
    private UnitType mUnit;
    private String mDescription;
    private String mHref;

    public ThingProperty(String name) {
        this(name, Type.OTHER);
    }

    public ThingProperty(String name, Type type) {
        mName = name;
        mType = type;
    }

    public void setUnit(UnitType unit) {
        mUnit = unit;
    }

    public void setHref(String href) {
        mHref = href;
    }

    public void setDescription(String description) {
        mDescription = description;
    }

    public String getName() {
        return mName;
    }

    public Type getType() {
        return mType;
    }

    public boolean hasUnit() {
        return mUnit != null;
    }

    public boolean hasDescription() {
        return mDescription != null && mDescription.length() > 0;
    }

    public boolean hasHref() {
        return mHref != null && mHref.length() > 0;
    }

    public Optional<UnitType> getUnit() {
        if(!hasUnit()) {
            return Optional.empty();
        }
        return Optional.of(mUnit);
    }

    public Optional<String> getDescription() {
        if(!hasDescription()) {
            return Optional.empty();
        }
        return Optional.of(mDescription);
    }

    public Optional<String> getHref() {
        if(!hasHref()) {
            return Optional.empty();
        }
        return Optional.of(mHref);
    }

    public static String getTypeValue(Type type) {
        switch (type) {
            case NUMBER:
                return TYPE_NUMBER;
            case BOOLEAN:
                return TYPE_BOOLEAN;
            case OTHER:
                return TYPE_OTHER;
        }
        return TYPE_OTHER;
    }

    public JSONObject toJSON() {
        JSONObject result = new JSONObject();

        result.put(TYPE_KEY, getTypeValue(mType));
        if(hasDescription()) {
            result.put(DESCRIPTION_KEY, mDescription);
        }
        if(hasUnit()) {
            result.put(UNIT_KEY, Unit.UnitTypeValue(mUnit));
        }
        if(hasHref()) {
            result.put(HREF_KEY, mHref);
        }

        return result;
    }

    public String toString() {
        return toJSON().toString();
    }
}
