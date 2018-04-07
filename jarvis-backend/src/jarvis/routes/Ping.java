package jarvis.routes;

import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;

@Path("/ping")
public class Ping {

  @GET
  @Produces("text/plain")
  public String getClichedMessage() {
    return "I am alive! :D";
  }
}