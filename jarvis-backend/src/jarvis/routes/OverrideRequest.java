package jarvis.routes;

import java.io.IOException;
import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;

@Path("/overriderequest")
public class OverrideRequest {

  public static String mobj;

  @GET
  // The Java method will produce content identified by the MIME Media type "text/plain"
  @Produces("application/json")
  public String getClichedMessage() throws IOException, ClassNotFoundException {
    String res = "Write Requests: ";
    if (DialogFlow.writeRequests) {
      DialogFlow.writeRequests = false;
    } else {
      DialogFlow.writeRequests = true;
    }
    res += DialogFlow.writeRequests;
    return res;
  }
}