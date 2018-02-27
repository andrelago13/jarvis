package jarvis.routes;

import dialogflow.DialogFlowRequest;
import dialogflow.QueryResponse;
import jarvis.util.AdminAlertUtil;
import jarvis.util.JarvisException;
import org.json.JSONObject;

import javax.ws.rs.*;
import java.util.ArrayList;

@Path("/dialogflow")
public class DialogFlow {
    @POST
    @Produces("text/plain")
    @Consumes("application/json")
    public String getClichedMessage(String content) {
        try {
            DialogFlowRequest request = new DialogFlowRequest(content);

            QueryResponse response = new QueryResponse();
            response.addFulfillmentMessage(request.getFulfillmentSpeech());
            return response.toString();
        } catch (JarvisException e) {
            AdminAlertUtil.alertJarvisException(e);
        } catch (Exception e) {
            AdminAlertUtil.alertUnexpectedException(e);
        }
        return QueryResponse.getDefaultResponse().toString();
    }
}