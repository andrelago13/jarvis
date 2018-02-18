package jarvis.util;

public class Config {
    /**
     * Domain where the Jarvis backend is hosted.
     */
    public static String JARVIS_DOMAIN = "jarvis-iot.ml";

    /**
     * URL used to send a message to the Slack channel.
     */
    public static String SLACK_WEBHOOK_URL = "https://hooks.slack.com/services/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";

    /**
     * File containing MongoDB credentials
     */
    public static String MONGO_CREDENTIALS_FILE = "WEB-INF/classes/res/credentials/mongo-creds-example.txt";

    /**
     * Temporary Mongo credentials.
     */
    public static String MONGO_USER = "jadmin";
    public static String MONGO_DATABASE = "admin";
    public static String MONGO_PASSWORD = "pwd1231pwd";
}
