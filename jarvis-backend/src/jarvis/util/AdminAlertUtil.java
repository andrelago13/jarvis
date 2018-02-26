package jarvis.util;

import slack.SlackUtil;

public class AdminAlertUtil {
    private static final String MSG_JARVIS_DEPLOYED = "Jarvis was successfully deployed! :clap: :tada:";
    private static final String MSG_NO_DB_CONNECTION = "There seems to be no connection to the database. :warning:";
    private static final String MSG_DB_ERROR = "There was an error with the Jarvis DB: :fire: :fire: :fire:";

    public static boolean alertJarvisDeployed() {
        return makeProductionAlert(MSG_JARVIS_DEPLOYED);
    }

    public static boolean alertNoDbConnection() {
        return makeProductionAlert(MSG_NO_DB_CONNECTION);
    }

    public static boolean alertDbError(String error) {
        return makeProductionAlert(MSG_DB_ERROR + error);
    }

    private static boolean makeProductionAlert(String message) {
        if(Flags.isLocalExecution()) {
            return false;
        }
        return SlackUtil.sendMessage(message);
    }
}
