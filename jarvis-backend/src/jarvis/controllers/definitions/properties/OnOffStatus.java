package jarvis.controllers.definitions.properties;

public class OnOffStatus {
    public static final String ON_STATUS = "on";
    public static final String OFF_STATUS = "off";

    private boolean mValue; //true=on, false=off

    public OnOffStatus(boolean value) {
        mValue = value;
    }

    public OnOffStatus(String status) {
        if(status == null) {
            mValue = false;
            return;
        }

        if(status.toLowerCase().equals(ON_STATUS)) {
            mValue = true;
        } else {
            mValue = false;
        }
    }

    public static boolean isValueOn(String value) {
        if(value != null && value.toLowerCase().equals(ON_STATUS)) {
            return true;
        }
        return false;
    }

    public static boolean isValueOff(String value) {
        return !isValueOn(value);
    }

    public boolean isOn() {
        return mValue;
    }
}
