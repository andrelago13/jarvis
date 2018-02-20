package jarvis.controllers.definitions;

import java.util.Optional;

public class ThingLinks {
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

    public Optional<String> getProperties() {
        return Optional.of(mProperties);
    }

    public Optional<String> getActions() {
        return Optional.of(mActions);
    }

    public Optional<String> getEvents() {
        return Optional.of(mEvents);
    }

    public Optional<String> getWebsocket() {
        return Optional.of(mWebsocket);
    }

    public static class Builder {
        private String mChildProperties;
        private String mChildActions;
        private String mChildEvents;
        private String mChildWebsocket;

        public Builder() {}

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
    }
}
