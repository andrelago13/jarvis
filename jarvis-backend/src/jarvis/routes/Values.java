package jarvis.routes;

import jarvis.engine.ValueTracker;
import java.util.Map;
import java.util.Set;
import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;

@Path("/values")
public class Values {

  @GET
  @Produces("text/plain")
  public String getClichedMessage() {
    StringBuilder res = new StringBuilder();
    Map<String, Object> values = ValueTracker.getInstance().getValues();
    Set<String> keys = values.keySet();
    for(String k : keys) {
      res.append(k);
      res.append(" => ");
      res.append(values.get(k));
      res.append('\n');
    }
    return res.toString();
  }
}