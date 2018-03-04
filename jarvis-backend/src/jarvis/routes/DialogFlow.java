package jarvis.routes;

import dialogflow.DialogFlowRequest;
import dialogflow.QueryResponse;
import dialogflow.intent.DialogFlowIntent;
import jarvis.util.AdminAlertUtil;
import jarvis.util.JarvisException;
import org.json.JSONObject;

import javax.ws.rs.*;
import java.util.ArrayList;

@Path("/dialogflow")
public class DialogFlow {
    @POST
    @Produces("application/json")
    @Consumes("application/json")
    public String getClichedMessage(String content) {
        try {
            DialogFlowRequest request = new DialogFlowRequest(content);
            DialogFlowIntent intent = DialogFlowIntent.getIntent(request);

            return intent.execute().toString();
        } catch (JarvisException e) {
            AdminAlertUtil.alertJarvisException(e);
            return e.getMessage();
        } catch (Exception e) {
            AdminAlertUtil.alertUnexpectedException(e);
            return e.getMessage();
        }
        //return QueryResponse.getDefaultResponse().toString();
    }
}