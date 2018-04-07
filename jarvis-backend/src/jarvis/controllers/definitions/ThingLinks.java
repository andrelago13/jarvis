package jarvis.controllers.definitions;

import java.util.Optional;
import org.json.JSONObject;

public class ThingLinks {

  public static final String PROPERTIES_KEY = "properties";
  public static final String ACTIONS_KEY = "actions";
  public static final String EVENTS_KEY = "events";
  public static final String WEBSOCKET_KEY = "websocket";

  private String mProperties;
  private String mActions;
  private String mEvents;
  private String mWebsocket;

  protected ThingLinks(String properties, String actions, String events, String websocket) {
    mProperties = properties;
    mActions = actions;
    mEvents = events;
    mWebsocket = websocket;
  }

  public boolean hasProperties() {
    return mProperties != null;
  }

  public boolean hasActions() {
    return mActions != null;
  }

  public boolean hasEvents() {
    return mEvents != null;
  }

  public boolean hasWebsocket() {
    return mWebsocket != null;
  }

  public boolean hasLinks() {
    return hasWebsocket() || hasEvents() || hasActions() || hasProperties();
  }

  public Optional<String> getProperties() {
    if (!hasProperties()) {
      return Optional.empty();
    }
    return Optional.of(mProperties);
  }

  public Optional<String> getActions() {
    if (!hasActions()) {
      return Optional.empty();
    }
    return Optional.of(mActions);
  }

  public Optional<String> getEvents() {
    if (!hasEvents()) {
      return Optional.empty();
    }
    return Optional.of(mEvents);
  }

  public Optional<String> getWebsocket() {
    if (!hasWebsocket()) {
      return Optional.empty();
    }
    return Optional.of(mWebsocket);
  }

  public String toString() {
    return toJSON().toString();
  }

  public JSONObject toJSON() {
    JSONObject result = new JSONObject();

    if (hasProperties()) {
      result.put(PROPERTIES_KEY, mProperties);
    }
    if (hasActions()) {
      result.put(ACTIONS_KEY, mActions);
    }
    if (hasEvents()) {
      result.put(EVENTS_KEY, mEvents);
    }
    if (hasWebsocket()) {
      result.put(WEBSOCKET_KEY, mWebsocket);
    }

    return result;
  }

  public static class Builder {

    private String mChildProperties;
    private String mChildActions;
    private String mChildEvents;
    private String mChildWebsocket;

    public Builder() {
    }

    public void setProperties(String properties) {
      mChildProperties = properties;
    }

    public void setActions(String actions) {
      mChildActions = actions;
    }

    public void setEvents(String events) {
      mChildEvents = events;
    }

    public void setWebsocket(String websocket) {
      mChildWebsocket = websocket;
    }

    public ThingLinks build() {
      return new ThingLinks(mChildProperties, mChildActions, mChildEvents, mChildWebsocket);
    }

    public static ThingLinks buildFromJSON(JSONObject json) {
      Builder b = new Builder();
      if (json.has(PROPERTIES_KEY)) {
        b.setProperties(json.getString(PROPERTIES_KEY));
      }
      if (json.has(ACTIONS_KEY)) {
        b.setActions(json.getString(ACTIONS_KEY));
      }
      if (json.has(EVENTS_KEY)) {
        b.setEvents(json.getString(EVENTS_KEY));
      }
      if (json.has(WEBSOCKET_KEY)) {
        b.setWebsocket(json.getString(WEBSOCKET_KEY));
      }
      return b.build();
    }

    public static ThingLinks buildFromCopy(ThingLinks t) {
      Builder b = new Builder();
      if (t.hasProperties()) {
        b.setProperties(t.mProperties);
      }
      if (t.hasActions()) {
        b.setActions(t.mActions);
      }
      if (t.hasEvents()) {
        b.setEvents(t.mEvents);
      }
      if (t.hasWebsocket()) {
        b.setWebsocket(t.mWebsocket);
      }
      return b.build();
    }
  }
}
