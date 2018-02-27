package jarvis.routes;

import jarvis.util.AdminAlertUtil;
import mongodb.MongoDB;

import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;

@Path("/pingdb")
public class PingDB {
    @GET
    @Produces("text/plain")
    public String getClichedMessage() {

        try {
            if(MongoDB.hasConnection()) {
                return "Database connection active.";
            }
        } catch (Exception e) {
            AdminAlertUtil.alertUnexpectedException(e);
        }
        return "WARNING: database connection down!";
    }
}