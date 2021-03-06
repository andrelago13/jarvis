package jarvis.events.util;

import jarvis.events.definitions.EventHandler;
import jarvis.util.JarvisException;
import org.json.JSONObject;

public class LoggedEventHandler {

  public static final String KEY_TIMESTAMP = "timestamp";
  public static final String KEY_EVENT = "event";

  private EventHandler mHandler;
  private long mTimestamp;

  public LoggedEventHandler(JSONObject json) throws JarvisException {
    try {
      mTimestamp = json.getLong(KEY_TIMESTAMP);
      mHandler = EventHandler.buildFromJSON(json.getJSONObject(KEY_EVENT)).get();
    } catch (Exception e) {
      throw new JarvisException("Unable to parse LoggedEventHandler.");
    }
  }

  public LoggedEventHandler(EventHandler handler, long timestamp) {
    mHandler = handler;
    mTimestamp = timestamp;
  }

  public LoggedEventHandler(EventHandler handler) {
    this(handler, System.currentTimeMillis());
  }

  public long getTimestamp() {
    return mTimestamp;
  }

  public EventHandler getEventHandler() {
    return mHandler;
  }

  public JSONObject toJSON() {
    JSONObject res = new JSONObject();
    res.put(KEY_TIMESTAMP, mTimestamp);
    res.put(KEY_EVENT, mHandler.toJSON());
    return res;
  }
}
