package jarvis.routes;

import dialogflow.DialogFlowRequest;
import dialogflow.QueryResponse;
import org.json.JSONObject;

import javax.ws.rs.*;

@Path("/dialogflow")
public class DialogFlow {
    @POST
    @Produces("text/plain")
    @Consumes("application/json")
    public String getClichedMessage(String content) {
        DialogFlowRequest request = new DialogFlowRequest(content);

        QueryResponse response = new QueryResponse();
        response.addFulfillmentMessage(request.getFulfillmentSpeech());
        return response.toString();
    }
}