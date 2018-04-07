package jarvis.routes;

import java.io.IOException;
import java.io.PrintWriter;
import java.io.StringWriter;
import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;

@Path("/")
public class Index {

  public static String mobj;

  @GET
  // The Java method will produce content identified by the MIME Media type "text/plain"
  @Produces("application/json")
  public String getClichedMessage() throws IOException, ClassNotFoundException {
    String res = "Nothing to see here :)";
    try {
      if (mobj != null) {
        return mobj;
      }
    } catch (Exception e) {
      StringWriter sw = new StringWriter();
      PrintWriter pw = new PrintWriter(sw);
      e.printStackTrace(pw);
      return sw.toString();
    }
    return res;
  }
}