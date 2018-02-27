package jarvis.routes;

import dialogflow.QueryResponse;
import mongodb.MongoDB;
import org.json.JSONObject;
import slack.SlackUtil;

import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import java.io.IOException;

@Path("/")
public class Index {
    public static String mobj;

    @GET
    // The Java method will produce content identified by the MIME Media type "text/plain"
    @Produces("application/json")
    public String getClichedMessage() throws IOException, ClassNotFoundException {
        if(mobj != null) {
            return mobj;
        }
        JSONObject obj = new JSONObject();
        obj.put("key", "value");
        return obj.toString();
    }
}