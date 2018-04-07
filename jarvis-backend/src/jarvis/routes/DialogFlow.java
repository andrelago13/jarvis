package jarvis.routes;

import dialogflow.DialogFlowRequest;
import dialogflow.QueryResponse;
import dialogflow.intent.definitions.DialogFlowIntent;
import jarvis.util.AdminAlertUtil;
import jarvis.util.JarvisException;
import javax.ws.rs.Consumes;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import slack.SlackUtil;

@Path("/dialogflow")
public class DialogFlow {

  public static boolean writeRequests = false;

  @POST
  @Produces("application/json")
  @Consumes("application/json")
  public String getClichedMessage(String content) {
    try {
      if (writeRequests) {
        SlackUtil.sendMessage(content);
      }
      DialogFlowRequest request = new DialogFlowRequest(content);
      DialogFlowIntent intent = DialogFlowIntent.getIntent(request);

      String res = intent.execute().toString();
      res += "";
      return res;
    } catch (JarvisException e) {
      AdminAlertUtil.alertJarvisException(e);
      QueryResponse r = new QueryResponse();
      r.addFulfillmentMessage(AdminAlertUtil.getExceptionStackTrace(e));
      return r.toString();
    } catch (Exception e) {
      AdminAlertUtil.alertUnexpectedException(e);
      QueryResponse r = new QueryResponse();
      r.addFulfillmentMessage(AdminAlertUtil.getExceptionStackTrace(e));
      return r.toString();
    }

    //return QueryResponse.getDefaultResponse().toString();
  }
}