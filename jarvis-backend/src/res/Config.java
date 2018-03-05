package res;

public class Config {
    /**
     * Domain where the Jarvis backend is hosted.
     */
    public static final String JARVIS_DOMAIN = "jarvis-iot.ml";

    /**
     * URL used to send a message to the Slack channel.
     */
    public static final String SLACK_WEBHOOK_URL = "https://hooks.slack.com/services/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";

    /**
     * URL used to send IoT messages to Slack.
     */
    public static final String SLACK_IOT_URL = "https://hooks.slack.com/services/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";

    /**
     * File containing MongoDB credentials
     */
    public static final String MONGO_CREDENTIALS_FILE = "WEB-INF/classes/res/credentials/mongo-creds-example.txt";

    /**
     * Temporary Mongo credentials.
     */
    public static final String MONGO_USER = "jadmin";
    public static final String MONGO_AUTH_DB = "admin";
    public static final String MONGO_PASSWORD = "pwd1231pwd";

    /**
     * Mongo DB config.
     */
    public static final String MONGO_JARVIS_DB = "jarvis";
    public static final String MONGO_THINGS_COLLECTION = "things";

    /**
     * Timeout for Mongo connections.
     */
    public static final int MONGO_TIMEOUT_MS = 5000;

    public static final String JARVIS_DEFAULT_ERROR = "Sorry, there was a problem. Please try again later.";

    /**
     * DialogFlow intent constants.
     */
    public static final String DF_ON_OFF_INTENT_NAME = "Turn on/off";
    public static final String DF_ON_OFF_INTENT_ID = "e7e07192-7e72-48d5-b702-aea5f8a79f4b";
    public static final String DF_DIRECT_ACTION_INTENT_NAME = "Direct Action";
    public static final String DF_DIRECT_ACTION_INTENT_ID = "8b4ac42a-6b56-43a0-9fbb-ca5da1e7d6d1";

    /**
     * DialogFlow entity constants.
     */
    public static final String DF_LIGHT_SWITCH_ENTITY_NAME = "light-switch";
    public static final String DF_ACTION_ENTITY_NAME = "action";
    public static final String DF_ONOFF_ACTION_ENTITY_NAME = "action-onoff";
}
