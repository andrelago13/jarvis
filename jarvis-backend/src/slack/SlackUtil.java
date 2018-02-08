package slack;

import jarvis.util.Flags;
import jarvis.util.HttpUtil;
import org.json.JSONObject;
import jarvis.util.Config;

public class SlackUtil {
    private static final String MSG_JARVIS_DEPLOYED = "Jarvis was successfully deployed!";

    public static void maybeSendDeployedMessage() {
        if(!Flags.isLocalExecution()) {
            sendDeployedMessage();
        }
    }

    public static boolean sendDeployedMessage() {
        JSONObject obj = new JSONObject();
        obj.put("text", MSG_JARVIS_DEPLOYED);
        return HttpUtil.sendJsonMessage(Config.SLACK_WEBHOOK_URL, obj.toString());
    }
}
