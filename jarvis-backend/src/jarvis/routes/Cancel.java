package jarvis.routes;

import jarvis.engine.JarvisEngine;

import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.QueryParam;

@Path("/cancel")
public class Cancel {
    @GET
    @Produces("text/plain")
    public String getClichedMessage(@QueryParam("idStr") String idStr) {
        long id = Long.parseLong(idStr);
        return "" + JarvisEngine.getInstance().cancelAction(id);
    }
}