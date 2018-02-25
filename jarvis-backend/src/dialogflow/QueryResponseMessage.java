package dialogflow;

import org.json.JSONObject;

public class QueryResponseMessage {
    public enum Type {
        DEFAULT,
        GOOGLE
    }

    private Type mType;
    private String mSpeech;

    public QueryResponseMessage(String speech) {
        this(Type.DEFAULT, speech);
    }

    public QueryResponseMessage(Type type, String speech) {
        mType = type;
        mSpeech = speech;
    }

    public JSONObject toJSON() {
        JSONObject result = new JSONObject();
        result.put("speech", mSpeech);
        result.put("type", getTypeIndex(mType));
        return result;
    }

    public static int getTypeIndex(Type t) {
        switch (t) {
            case DEFAULT:
                return 0;
            default:
                return -1;
        }
    }
}
