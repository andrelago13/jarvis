package dialogflow;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import org.json.JSONObject;
import res.Config;

public class QueryResponse {

  public static final String KEY_FULFILLMENT_TEXT = "fulfillmentText";
  public static final String KEY_MESSAGES = "messages";
  public static final String KEY_CONTEXT_OUT = "contextOut";

  private Optional<String> mFulfillmentText;
  private List<QueryResponseMessage> mFulfillmentMessages;
  private List<DialogFlowContext> mOutContexts;

  public QueryResponse() {
    mFulfillmentText = Optional.empty();
    mFulfillmentMessages = new ArrayList<>();
    mOutContexts = new ArrayList<>();
  }

  public void setFulfillmentText(String text) {
    mFulfillmentText = Optional.of(text);
  }

  public Optional<String> getFulfillmentText() {
    return mFulfillmentText;
  }

  public void addOutContext(DialogFlowContext context) {
    mOutContexts.add(context);
  }

  public List<DialogFlowContext> getOutContext() {
    return mOutContexts;
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

    if (mFulfillmentText.isPresent()) {
      result.put(KEY_FULFILLMENT_TEXT, mFulfillmentText.get());
    }

    for (QueryResponseMessage message : mFulfillmentMessages) {
      result.append(KEY_MESSAGES, message.toJSON());
      result.put("speech", message.getSpeech());
    }

    for (DialogFlowContext c : mOutContexts) {
      result.append(KEY_CONTEXT_OUT, c.getJSON());
    }

    return result;
  }

  public String toString() {
    return toJSON().toString();
  }

  public static QueryResponse getDefaultResponse() {
    QueryResponse response = new QueryResponse();
    response.addFulfillmentMessage(Config.JARVIS_DEFAULT_ERROR);
    return response;
  }
}
