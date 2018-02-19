package jarvis.routes;

import mongodb.MongoDB;
import org.json.JSONObject;

import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import java.io.IOException;

@Path("/pingdb")
public class PingDB {
    @GET
    @Produces("text/plain")
    public String getClichedMessage() {
        if(MongoDB.hasConnection()) {
            return "Database connection active.";
        }
        return "WARNING: database connection down!";
    }
}