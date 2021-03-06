package dialogflow;

import jarvis.util.JarvisException;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

public class DialogFlowRequest {

  private JSONObject mInternalJSON;

  private static final String KEY_ID = "id";
  private static final String KEY_TIMESTAMP = "timestamp";
  private static final String KEY_LANG = "lang";
  private static final String KEY_SESSION_ID = "sessionId";
  private static final String KEY_RESULT = "result";
  private static final String KEY_SOURCE = "source";
  private static final String KEY_RESOLVED_QUERY = "resolvedQuery";
  private static final String KEY_SPEECH = "speech";
  private static final String KEY_ACTION = "action";
  private static final String KEY_ACTION_INCOMPLETE = "actionIncomplete";
  private static final String KEY_METADATA = "metadata";
  private static final String KEY_INTENT_ID = "intentId";
  private static final String KEY_INTENT_NAME = "intentName";
  private static final String KEY_FULFILLMENT = "fulfillment";
  private static final String KEY_FULFILLMENT_SPEECH = "speech";
  private static final String KEY_CONTEXTS = "contexts";
  private static final String KEY_SCORE = "score";
  private static final String KEY_PARAMETERS = "parameters";
  private static final String KEY_STATUS = "status";
  private static final String KEY_STATUS_CODE = "code";
  private static final String KEY_STATUS_ERROR_TYPE = "errorType";
  private static final String KEY_MESSAGES = "messages";

  private String mId;
  private String mTimestamp;
  private String mLang;
  private String mSessionId; // Optional

  /* RESULT */
  private String mSource;
  private String mResolvedQuery;
  private String mSpeech; // Optional
  private String mAction; // Optional
  private Boolean mActionIncomplete;
  private String mMetadataIntentId;
  private String mMetadataIntentName;
  private String mFulfillmentSpeech;
  private Double mScore;
  private JSONObject mParameters; // Optional
  private List<DialogFlowContext> mContexts;

  /* STATUS */
  private int mStatusCode;
  private String mStatusErrorType;

  public DialogFlowRequest(String request) throws JarvisException {
    try {
      init(new JSONObject(request));
    } catch (JSONException e) {
      throw new JarvisException(e);
    }
  }

  public DialogFlowRequest(JSONObject json) throws JarvisException {
    try {
      init(json);
    } catch (JSONException e) {
      throw new JarvisException(e);
    }
  }

  private void init(JSONObject json) {
    mInternalJSON = json;
    parseJSON();
  }

  private void parseJSON() {
    mId = mInternalJSON.getString(KEY_ID);
    mTimestamp = mInternalJSON.getString(KEY_TIMESTAMP);
    mLang = mInternalJSON.getString(KEY_LANG);

    parseJSON_result();

    parseJSON_status();

    mSessionId = mInternalJSON.getString(KEY_SESSION_ID);
  }

  private void parseJSON_result() {
    JSONObject result = mInternalJSON.getJSONObject(KEY_RESULT);

    mSource = result.getString(KEY_SOURCE);
    mResolvedQuery = result.getString(KEY_RESOLVED_QUERY);
    if (result.has(KEY_SPEECH)) {
      String speech = result.getString(KEY_SPEECH);
      if (speech != null && speech.length() > 0) {
        mSpeech = speech;
      }
    }
    if (result.has(KEY_ACTION)) {
      String action = result.getString(KEY_ACTION);
      if (action != null && action.length() > 0) {
        mAction = action;
      }
    }
    if (result.has(KEY_PARAMETERS)) {
      mParameters = result.getJSONObject(KEY_PARAMETERS);
    }

    mActionIncomplete = result.getBoolean(KEY_ACTION_INCOMPLETE);

    JSONObject metadata = result.getJSONObject(KEY_METADATA);
    mMetadataIntentId = metadata.getString(KEY_INTENT_ID);
    mMetadataIntentName = metadata.getString(KEY_INTENT_NAME);

    JSONObject fulfillment = result.getJSONObject(KEY_FULFILLMENT);
    if (fulfillment.has(KEY_FULFILLMENT_SPEECH)) {
      mFulfillmentSpeech = fulfillment.getString(KEY_FULFILLMENT_SPEECH);
    } else if (fulfillment.has(KEY_MESSAGES)) {
      JSONArray messages = fulfillment.getJSONArray(KEY_MESSAGES);
      if (messages.length() > 0) {
        mFulfillmentSpeech = messages.getJSONObject(0).getString(KEY_SPEECH);
      }
    }
    if (mFulfillmentSpeech == null) {
      mFulfillmentSpeech = "";
    }

    mContexts = new ArrayList<>();
    if (result.has(KEY_CONTEXTS)) {
      mContexts = DialogFlowContext.fromJSONList(result.getJSONArray(KEY_CONTEXTS));
    }

    mScore = result.getDouble(KEY_SCORE);
  }

  private void parseJSON_status() {
    JSONObject status = mInternalJSON.getJSONObject(KEY_STATUS);
    mStatusCode = status.getInt(KEY_STATUS_CODE);
    mStatusErrorType = status.getString(KEY_STATUS_ERROR_TYPE);
  }

  public String getId() {
    return mId;
  }

  public String getTimestamp() {
    return mTimestamp;
  }

  public String getLang() {
    return mLang;
  }

  public Optional<String> getSessionId() {
    if (mSessionId == null || mSessionId.length() < 1) {
      return Optional.empty();
    }
    return Optional.of(mSessionId);
  }

  public String getSource() {
    return mSource;
  }

  public String getResolvedQuery() {
    return mResolvedQuery;
  }

  public Optional<String> getSpeech() {
    if (mSpeech == null || mSpeech.length() < 1) {
      return Optional.empty();
    }
    return Optional.of(mSpeech);
  }

  public Optional<String> getAction() {
    if (mAction == null || mAction.length() < 1) {
      return Optional.empty();
    }
    return Optional.of(mAction);
  }

  public Optional<JSONObject> getParameters() {
    if (mParameters == null) {
      return Optional.empty();
    }
    return Optional.of(mParameters);
  }

  public Boolean getActionIncomplete() {
    return mActionIncomplete;
  }

  public String getMetadataIntentId() {
    return mMetadataIntentId;
  }

  public String getMetadataIntentName() {
    return mMetadataIntentName;
  }

  public String getFulfillmentSpeech() {
    return mFulfillmentSpeech;
  }

  public Double getScore() {
    return mScore;
  }

  public int getStatusCode() {
    return mStatusCode;
  }

  public String getStatusErrorType() {
    return mStatusErrorType;
  }

  public List<DialogFlowContext> getContexts() {
    return mContexts;
  }

  public String toString() {
    return mInternalJSON.toString();
  }

  public JSONObject getJSON() {
    return mInternalJSON;
  }
}
