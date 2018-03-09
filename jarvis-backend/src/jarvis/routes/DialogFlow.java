package jarvis.routes;

import dialogflow.DialogFlowRequest;
import dialogflow.QueryResponse;
import dialogflow.intent.DialogFlowIntent;
import jarvis.util.AdminAlertUtil;
import jarvis.util.JarvisException;
import slack.SlackUtil;

import javax.ws.rs.*;

@Path("/dialogflow")
public class DialogFlow {
    @POST
    @Produces("application/json")
    @Consumes("application/json")
    public String getClichedMessage(String content) {
        try {
            SlackUtil.sendIoTMessage(content);
            DialogFlowRequest request = new DialogFlowRequest(content);
            DialogFlowIntent intent = DialogFlowIntent.getIntent(request);

            return intent.execute().toString();
        } catch (JarvisException e) {
            AdminAlertUtil.alertJarvisException(e);
        } catch (Exception e) {
            AdminAlertUtil.alertUnexpectedException(e);
        }

        return QueryResponse.getDefaultResponse().toString();
    }
}