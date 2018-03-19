package dialogflow.intent.definitions;

import java.util.HashMap;
import java.util.Map;

public class IntentExtras {
    public Map<Integer, Object> mMap;

    public IntentExtras() {
        mMap = new HashMap<>();
    }

    public void put(Integer key, Object obj) {
        mMap.put(key, obj);
    }

    public boolean hasKey(Integer key) {
        return mMap.containsKey(key);
    }

    public Object get(Integer key) {
        return mMap.getOrDefault(key, null);
    }
}
