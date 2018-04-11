package jarvis.routes;

import jarvis.actions.ScheduledAction;
import jarvis.engine.JarvisEngine;
import java.io.IOException;
import java.io.PrintWriter;
import java.io.StringWriter;
import java.util.List;
import java.util.Map;
import java.util.Set;
import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;

@Path("/schedulings")
public class Schedulings {

  public static String mobj;

  @GET
  // The Java method will produce content identified by the MIME Media type "text/plain"
  @Produces("application/json")
  public String getClichedMessage() throws IOException, ClassNotFoundException {
    try {
      List<ScheduledAction> s = JarvisEngine.getInstance().getScheduledActions();
      String result = "Size: " + s.size() + '\n';
      for (ScheduledAction k : s) {
        result += "" + k.getId() + " " + k.getCommand().executeString() + '\n';
      }
      return result;
    } catch (Exception e) {
      StringWriter sw = new StringWriter();
      PrintWriter pw = new PrintWriter(sw);
      e.printStackTrace(pw);
      return sw.toString();
    }
  }
}