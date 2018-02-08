package slack;

import org.apache.http.HttpResponse;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.entity.StringEntity;
import org.apache.http.impl.client.HttpClientBuilder;
import org.json.JSONObject;
import util.Config;

public class SlackUtil {
    private static final String MSG_JARVIS_DEPLOYED = "Jarvis was successfully deployed!";

    public static boolean sendDeployedMessage() {
        JSONObject obj = new JSONObject();
        obj.append("text", MSG_JARVIS_DEPLOYED);
        return sendJsonMessage(obj.toString());
    }

    private static boolean sendJsonMessage(String message) {
        HttpClient httpClient = HttpClientBuilder.create().build();
        try {
            HttpPost request = new HttpPost(Config.SLACK_WEBHOOK_URL);
            StringEntity params = new StringEntity(message);
            request.addHeader("content-type", "application/json");
            request.setEntity(params);
            httpClient.execute(request);
        }catch (Exception ex) {
            return false;
        }
        return true;
    }
}
