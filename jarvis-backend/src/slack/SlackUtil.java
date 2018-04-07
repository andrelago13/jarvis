package slack;

import jarvis.util.HttpUtil;
import org.json.JSONObject;
import res.Config;

public class SlackUtil {

  public static boolean sendMessage(String message) {
    return sendMessage(Config.SLACK_WEBHOOK_URL, message);
  }

  public static boolean sendIoTMessage(String message) {
    return sendMessage(Config.SLACK_IOT_URL, message);
  }

  public static boolean sendDebugMessage(String message) {
    return sendMessage(Config.SLACK_DEBUG_URL, message);
  }

  private static boolean sendMessage(String url, String message) {
    JSONObject obj = new JSONObject();
    obj.put("text", message);
    return HttpUtil.sendJsonMessage(url, obj.toString());
  }
}
