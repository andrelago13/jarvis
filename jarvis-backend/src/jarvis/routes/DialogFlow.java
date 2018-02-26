package jarvis.routes;

import dialogflow.QueryResponse;
import org.json.JSONObject;

import javax.ws.rs.*;

@Path("/dialogflow")
public class DialogFlow {
    @POST
    @Produces("text/plain")
    public String getClichedMessage(@FormParam("lang") String lang) {
        Index.mobj = "+> " + lang;

        QueryResponse response = new QueryResponse();
        response.addFulfillmentMessage("Is it done now?");
        return response.toString();
    }
}