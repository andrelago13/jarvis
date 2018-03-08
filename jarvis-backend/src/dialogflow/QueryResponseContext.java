package dialogflow;

import org.json.JSONObject;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;

public class QueryResponseContext {
    public static final String KEY_NAME = "name";
    public static final String KEY_PARAMETERS = "parameters";
    public static final String KEY_LIFESPAN = "lifespan";

    private String mName;
    private Map<String, Object> mParameters;
    private int mLifespan;

    public QueryResponseContext(String name, int lifespan, Map<String, Object> parameters) {
        mName = name;
        mLifespan = lifespan;
        mParameters = parameters;
    }

    public QueryResponseContext(String name, int lifespan) {
        this(name, lifespan, new HashMap<>());
    }

    public String getName() {
        return mName;
    }

    public Map<String, Object> getParameters() {
        return mParameters;
    }

    public int getLifespan() {
        return mLifespan;
    }

    public void addParameter(String key, Object obj) {
        mParameters.put(key, obj);
    }

    public JSONObject getJSON() {
        JSONObject result = new JSONObject();

        result.put(KEY_NAME, mName);
        result.put(KEY_LIFESPAN, mLifespan);

        Set<String> keys = mParameters.keySet();
        JSONObject parameters = new JSONObject();
        for(String k : keys) {
            parameters.put(k, mParameters.get(k));
        }
        result.put(KEY_PARAMETERS, parameters);

        return result;
    }

    public String toString() {
        return getJSON().toString();
    }
}
