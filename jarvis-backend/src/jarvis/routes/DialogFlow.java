package jarvis.routes;

import org.json.JSONObject;

import javax.ws.rs.GET;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;

@Path("/dialogflow")
public class DialogFlow {
    @POST
    @Produces("text/plain")
    public String getClichedMessage() {
        JSONObject response = new JSONObject();

        response.put("fulfillmentText", "I'm kind of dumb");

        return response.toString();
    }
}