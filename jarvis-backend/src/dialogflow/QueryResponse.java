package dialogflow;

import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

public class QueryResponse {
    private Optional<String> mFulfillmentText;
    private List<QueryResponseMessage> mFulfillmentMessages;

    public QueryResponse() {
        mFulfillmentText = Optional.empty();
        mFulfillmentMessages = new ArrayList<>();
    }

    public void setFulfillmentText(String text) {
        mFulfillmentText = Optional.of(text);
    }

    public Optional<String> getFulfillmentText() {
        return mFulfillmentText;
    }

    public void addFulfillmentMessage(String speech) {
        addFulfillmentMessage(new QueryResponseMessage(speech));
    }

    public void addFulfillmentMessage(QueryResponseMessage.Type type, String speech) {
        addFulfillmentMessage(new QueryResponseMessage(type, speech));
    }

    public void addFulfillmentMessage(QueryResponseMessage message) {
        mFulfillmentMessages.add(message);
    }

    public List<QueryResponseMessage> getFulfillmentMessages() {
        return mFulfillmentMessages;
    }

    public JSONObject toJSON() {
        JSONObject result = new JSONObject();

        if(mFulfillmentText.isPresent()) {
            result.put("fulfillmentText", mFulfillmentText.get());
        }

        for(QueryResponseMessage message : mFulfillmentMessages) {
            result.append("messages", message.toJSON());
        }

        return result;
    }

    public String toString() {
        return toJSON().toString();
    }
}
