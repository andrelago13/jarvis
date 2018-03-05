package jarvis.controllers.definitions.properties;

import jarvis.util.Unit;
import org.json.JSONObject;

import java.util.HashMap;
import java.util.Map;
import java.util.Optional;
import java.util.Set;

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

    public static final Map<Integer, String> typeValues = new HashMap<>();
    static {
        typeValues.put(Type.BOOLEAN.ordinal(), "boolean");
        typeValues.put(Type.NUMBER.ordinal(), "number");
        typeValues.put(Type.OTHER.ordinal(), "other");
    }

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

    public ThingProperty(ThingProperty t) {
        mName = t.mName;
        mType = t.mType;
        if(t.hasDescription()) {
            mDescription = t.mDescription;
        }
        if(t.hasUnit()) {
            mUnit = t.mUnit;
        }
        if(t.hasHref()) {
            mHref = t.mHref;
        }
    }

    public ThingProperty(String name, JSONObject json) {
        mName = name;
        mType = getTypeFromString(json.getString(TYPE_KEY));
        if(json.has(DESCRIPTION_KEY)) {
            mDescription = json.getString(DESCRIPTION_KEY);
        }
        if(json.has(UNIT_KEY)) {
            mUnit = Unit.getUnitTypeForString(json.getString(UNIT_KEY));
        }
        if(json.has(HREF_KEY)) {
            mHref = json.getString(HREF_KEY);
        }
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
        if(typeValues.containsKey(type.ordinal())) {
            return typeValues.get(type.ordinal());
        }
        return typeValues.get(Type.OTHER.ordinal());
    }

    public static Type getTypeFromString(String value) {
        Set<Integer> keys = typeValues.keySet();
        for(Integer k : keys) {
            if(typeValues.get(k).equals(value)) {
                return Type.values()[k];
            }
        }
        return Type.OTHER;
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
