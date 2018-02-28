package jarvis.routes;

import jarvis.controllers.OnOffLight;
import mongodb.MongoDB;
import org.json.JSONObject;

import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import java.io.IOException;

@Path("/")
public class Index {
    public static String mobj;

    @GET
    // The Java method will produce content identified by the MIME Media type "text/plain"
    @Produces("application/json")
    public String getClichedMessage() throws IOException, ClassNotFoundException {
        if(mobj != null) {
            return mobj;
        }
        JSONObject obj = new JSONObject();
        obj.put("key", "value");
        //return obj.toString();
        OnOffLight l =  OnOffLight.Builder.getDefaultBuilder("testlight", "/base").build();
        return "" + l.toString();
    }
}