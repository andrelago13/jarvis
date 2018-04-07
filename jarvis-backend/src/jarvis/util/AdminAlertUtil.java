package jarvis.util;

import java.io.PrintWriter;
import java.io.StringWriter;
import slack.SlackUtil;

public class AdminAlertUtil {

  private static final String MSG_JARVIS_DEPLOYED = "Jarvis was successfully deployed! :clap: :tada:";
  private static final String MSG_NO_DB_CONNECTION = "There seems to be no connection to the database. :warning:";
  private static final String MSG_DB_ERROR = "There was an error with the Jarvis DB: :fire: :fire: :fire:";
  private static final String MSG_JARVIS_EXCEPTION = ":warning: Jarvis has crashed: ";
  private static final String MSG_UNEXPECTED_EXCEPTION = ":fire: Jarvis has crashed with an unexpected error : ";

  public static boolean alertJarvisDeployed() {
    return makeProductionAlert(MSG_JARVIS_DEPLOYED);
  }

  public static boolean alertNoDbConnection() {
    return makeProductionAlert(MSG_NO_DB_CONNECTION);
  }

  public static boolean alertDbError(String error) {
    return makeProductionAlert(MSG_DB_ERROR + error);
  }

  public static boolean alertJarvisException(JarvisException e) {
    String stackTrace = getExceptionStackTrace(e);
    return makeProductionAlert(MSG_JARVIS_EXCEPTION + e.toString() + '\n' + stackTrace);
  }

  public static boolean alertUnexpectedException(Exception e) {
    String stackTrace = getExceptionStackTrace(e);
    return makeProductionAlert(MSG_UNEXPECTED_EXCEPTION + e.toString() + '\n' + stackTrace);
  }

  public static boolean customAlert(String message) {
    return makeProductionAlert(message);
  }

  private static boolean makeProductionAlert(String message) {
    if (Flags.isLocalExecution()) {
      return false;
    }
    return SlackUtil.sendMessage(message);
  }

  public static String getExceptionStackTrace(Throwable t) {
    StringWriter sw = new StringWriter();
    PrintWriter pw = new PrintWriter(sw);
    t.printStackTrace(pw);
    return sw.toString();
  }
}
