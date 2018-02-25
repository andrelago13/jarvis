package jarvis.routes;

import dialogflow.QueryResponse;
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
        QueryResponse response = new QueryResponse();
        response.addFulfillmentMessage("Is it done now?");
        return response.toString();
    }
}