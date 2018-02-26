package dialogflow;

import com.mongodb.util.JSON;
import org.json.JSONObject;

public class DialogFlowRequest {
    public JSONObject internal;

    public DialogFlowRequest(String request) {
        internal = new JSONObject(request);
    }
}
