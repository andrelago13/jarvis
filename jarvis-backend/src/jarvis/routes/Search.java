package jarvis.routes;

import jarvis.communication.ThingInterface;
import jarvis.controllers.definitions.Thing;
import java.util.List;
import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.QueryParam;

@Path("/search")
public class Search {

  @GET
  @Produces("text/plain")
  public String getClichedMessage(@QueryParam("name") String name) {
    List<Thing> things;
    if (name == null) {
      things = ThingInterface.getThings();
    } else {
      things = ThingInterface.getThingsByName(name);
    }

    if (things.isEmpty()) {
      return "EMPTY";
    }

    String res = "Found " + things.size() + " instance(s): " + '\n' + '\n';
    for (Thing t : things) {
      res += t.toString() + '\n' + '\n';
    }
    return res;
  }
}