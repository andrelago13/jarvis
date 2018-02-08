package jarvis.util;

import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.entity.StringEntity;
import org.apache.http.impl.client.HttpClientBuilder;

public class HttpUtil {
    public static final String JSON_MIME_TYPE = "application/json";

    public static boolean sendJsonMessage(String url, String message) {
        return sendMessage(JSON_MIME_TYPE, url, message);
    }

    public static boolean sendMessage(String mimeType, String url, String message) {
        HttpClient httpClient = HttpClientBuilder.create().build();
        try {
            HttpPost request = new HttpPost(url);
            StringEntity params = new StringEntity(message);
            request.addHeader("content-type", mimeType);
            request.setEntity(params);
            httpClient.execute(request);
        }catch (Exception ex) {
            return false;
        }
        return true;
    }
}
