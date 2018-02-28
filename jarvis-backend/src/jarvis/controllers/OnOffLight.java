package jarvis.controllers;

import com.sun.istack.internal.NotNull;
import com.sun.istack.internal.Nullable;
import jarvis.controllers.definitions.Thing;
import jarvis.controllers.definitions.ThingLinks;
import jarvis.controllers.definitions.actionables.Toggleable;
import jarvis.controllers.definitions.properties.ThingProperty;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

public class OnOffLight extends Thing implements Toggleable {
    private static final String DEFAULT_DESCRIPTION = "On/Off light switch";
    private static final String DEFAULT_PROPERTY_DESCRIPTION = "Describes current state of the switch (true=on)";
    private static final String DEFAULT_PROPERTIES_PATH = "properties";
    private static final String DEFAULT_ACTIONS_PATH = "actions";
    private static final String DEFAULT_EVENTS_PATH = "events";

    protected OnOffLight(@NotNull String name,
                         @Nullable String description,
                         @NotNull ThingLinks links,
                         @NotNull List<ThingProperty> properties) {
        super(name, Type.ON_OFF_LIGHT, description, links, properties);
    }

    @Override
    public Optional<Boolean> turnOn() {
        return Optional.empty();
    }

    @Override
    public Optional<Boolean> turnOff() {
        return Optional.empty();
    }

    @Override
    public Optional<Boolean> toggle() {
        return Optional.empty();
    }

    @Override
    public Optional<Boolean> isOn() {
        return Optional.empty();
    }

    public static class Builder extends Thing.Builder {
        public Builder() {
            super();
        }

        public static Builder getDefaultBuilder(String name, String basePath) {
            Builder builder = new Builder();

            builder.setName(name);
            builder.setDescription(DEFAULT_DESCRIPTION);
            ThingProperty statusProperty = new ThingProperty("status", ThingProperty.Type.BOOLEAN);
            statusProperty.setDescription(DEFAULT_PROPERTY_DESCRIPTION);
            builder.addProperty(statusProperty);
            ThingLinks.Builder linksBuilder = new ThingLinks.Builder();
            linksBuilder.setProperties(basePath + '/' + DEFAULT_PROPERTIES_PATH);
            linksBuilder.setActions(basePath + '/' + DEFAULT_ACTIONS_PATH);
            linksBuilder.setEvents(basePath + '/' + DEFAULT_EVENTS_PATH);
            builder.setLinks(linksBuilder.build());

            return builder;
        }

        @Override
        public OnOffLight build() {
            return new OnOffLight(mName, mDescription, mLinks, mProperties);
        }

        @Override
        public void setName(String name) {
            super.setName(name);
        }

        @Override
        public void setDescription(String description) {
            super.setDescription(description);
        }

        @Override
        public void setLinks(ThingLinks links) {
            super.setLinks(links);
        }

        @Override
        public void setProperties(List<ThingProperty> properties) {
            super.setProperties(properties);
        }

        @Override
        public void addProperty(ThingProperty property) {
            super.addProperty(property);
        }

        @Override
        public boolean isValid() {
            return super.isValid();
        }
    }
}
