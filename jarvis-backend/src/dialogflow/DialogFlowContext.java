package dialogflow;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;
import org.json.JSONArray;
import org.json.JSONObject;

public class DialogFlowContext {

  public static final String KEY_NAME = "name";
  public static final String KEY_PARAMETERS = "parameters";
  public static final String KEY_LIFESPAN = "lifespan";

  private String mName;
  private Map<String, Object> mParameters;
  private int mLifespan;

  public DialogFlowContext(String name, int lifespan, Map<String, Object> parameters) {
    mName = name;
    mLifespan = lifespan;
    mParameters = parameters;
  }

  public DialogFlowContext(String name, int lifespan) {
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
    for (String k : keys) {
      parameters.put(k, mParameters.get(k));
    }
    result.put(KEY_PARAMETERS, parameters);

    return result;
  }

  public String toString() {
    return getJSON().toString();
  }

  public static DialogFlowContext fromJSON(JSONObject context) {
    String name = context.getString(KEY_NAME);
    int lifespan = context.getInt(KEY_LIFESPAN);
    if (!context.has(KEY_PARAMETERS)) {
      return new DialogFlowContext(name, lifespan);
    }

    Map<String, Object> parameters = new HashMap<>();
    JSONObject parametersJSON = context.getJSONObject(KEY_PARAMETERS);
    Set<String> parameterKeys = parametersJSON.keySet();
    for (String k : parameterKeys) {
      parameters.put(k, parametersJSON.get(k));
    }
    return new DialogFlowContext(name, lifespan, parameters);
  }

  public static List<DialogFlowContext> fromJSONList(JSONArray contexts) {
    List<DialogFlowContext> result = new ArrayList<>();

    for (int index = 0; index < contexts.length(); ++index) {
      result.add(DialogFlowContext.fromJSON(contexts.getJSONObject(index)));
    }

    return result;
  }
}
