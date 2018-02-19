package slack;

import jarvis.util.HttpUtil;
import org.json.JSONObject;
import res.Config;

public class SlackUtil {
    public static boolean sendMessage(String message) {
        JSONObject obj = new JSONObject();
        obj.put("text", message);
        return HttpUtil.sendJsonMessage(Config.SLACK_WEBHOOK_URL, obj.toString());
    }
}
