package jarvis.routes;

import jarvis.controllers.definitions.Thing;
import jarvis.util.AdminAlertUtil;
import mongodb.MongoDB;

import javax.ws.rs.*;
import java.util.ArrayList;

@Path("/search")
public class Search {
    @GET
    @Produces("text/plain")
    public String getClichedMessage(@QueryParam("name") String name) {
        ArrayList<Thing> things;
        if(name == null) {
            things = MongoDB.getThings();
        } else {
            things = MongoDB.getThingsByName(name);
        }

        if(things.isEmpty()) {
            return "EMPTY";
        }

        String res = "Found " + things.size() + " instance(s): " + '\n' + '\n';
        for(Thing t : things) {
            res += t.toString() + '\n' + '\n';
        }
        return res;
    }
}