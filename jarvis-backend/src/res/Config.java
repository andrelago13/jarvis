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
    public static final String MONGO_COMMANDS_COLLECTION = "commands";
    public static final String MONGO_RULES_COLLECTION = "rules";

    /**
     * Timeout for Mongo connections.
     */
    public static final int MONGO_TIMEOUT_MS = 5000;

    public static final String JARVIS_DEFAULT_ERROR = "Sorry, there was a problem. Please try again later.";

    /**
     * DialogFlow intent constants.
     */
    public static final String DF_WELCOME_INTENT_NAME = "Welcome Intent";
    public static final String DF_WELCOME_INTENT_ID = "f3814c50-b4df-4f04-90b6-261b44eece77";
    public static final String DF_ON_OFF_INTENT_NAME = "Turn on/off";
    public static final String DF_ON_OFF_INTENT_ID = "e7e07192-7e72-48d5-b702-aea5f8a79f4b";
    public static final String DF_DIRECT_ACTION_INTENT_NAME = "Direct Action";
    public static final String DF_DIRECT_ACTION_INTENT_ID = "8b4ac42a-6b56-43a0-9fbb-ca5da1e7d6d1";
    public static final String DF_DELAYED_ACTION_INTENT_NAME = "Delayed Action";
    public static final String DF_DELAYED_ACTION_INTENT_ID = "36e77c05-5ada-404d-8de5-cf7a7c1f2f96";
    public static final String DF_CANCEL_INTENT_NAME = "Cancel Command";
    public static final String DF_CANCEL_INTENT_ID = "62544394-9d76-42da-a40b-6a6cda34066b";
    public static final String DF_CONFIRM_CANCEL_INTENT_NAME = "Confirm Cancel";
    public static final String DF_CONFIRM_CANCEL_INTENT_ID = "145e7d3b-9105-4bfb-8c6a-78c3e5c2783b";
    public static final String DF_CONFIRM_THING_INTENT_NAME = "Confirm Thing Choice";
    public static final String DF_CONFIRM_THING_INTENT_ID = "5f3f7eb8-c2dc-42db-9f59-38a9bf0c3a52";
    public static final String DF_REPEATING_ACTION_INTENT_NAME = "Repeating Intent";
    public static final String DF_REPEATING_ACTION_INTENT_ID = "977ef8d4-1096-4733-b724-82bad2931fc8";

    /**
     * DialogFlow context constants.
     */
    public static final String DF_CANCEL_INTENT_CONTEXT = "confirm-cancel-event";
    public static final String DF_CANCEL_INTENT_COMMAND_ID = "commandId";
    public static final int DF_CANCEL_INTENT_COMMAND_LIFESPAN = 1;
    public static final String DF_CONFIRM_THING_INTENT_CONTEXT = "confirm-thing-choice";

    /**
     * DialogFlow entity constants.
     */
    public static final String DF_LIGHT_SWITCH_ENTITY_NAME = "light-switch";
    public static final String DF_ACTION_ENTITY_NAME = "action";
    public static final String DF_TIME_ENTITY_NAME = "time";
    public static final String DF_PERIODTIME_ENTITY_NAME = "periodtime";
    public static final String DF_STARTTIME_ENTITY_NAME = "starttime";
    public static final String DF_ENDTIME_ENTITY_NAME = "endtime";
    public static final String DF_ONOFF_ACTION_ENTITY_NAME = "action-onoff";

    public static final String DF_TIME_PERIOD_SYS_ENTITY_NAME = "time-period";

    /**
     * RabbitMQ constants
     */
    public static final String RABBITMQ_HOST_ENV = "rabbitmq-host";
    public static final String RABBITMQ_USERNAME_ENV = "rabbitmq-user";
    public static final String RABBITMQ_PASSWORD_ENV = "rabbitmq-pass";
    public static final String RABBITMQ_HOST = "jarvis-iot.ml";
    public static final String RABBITMQ_USERNAME = "rabbitmq";
    public static final String RABBITMQ_PASSWORD = "rabbitmq";
}
