package jarvis.routes;

import jarvis.controllers.OnOffLight;
import jarvis.controllers.definitions.Thing;
import mongodb.MongoDB;
import org.json.JSONObject;

import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import java.io.IOException;
import java.io.PrintWriter;
import java.io.StringWriter;

@Path("/")
public class Index {
    public static String mobj;

    @GET
    // The Java method will produce content identified by the MIME Media type "text/plain"
    @Produces("application/json")
    public String getClichedMessage() throws IOException, ClassNotFoundException {
        try {
            if(mobj != null) {
                return mobj;
            }
            JSONObject obj = new JSONObject();
            obj.put("key", "value");
            //return obj.toString();
            OnOffLight l =  OnOffLight.Builder.getDefaultBuilder("testlight", "/base").build();
            String str1 = l.toString();
            OnOffLight t2 = OnOffLight.Builder.buildFromJSON(new JSONObject(str1));
            return "" + l.toString();// + '\n' + '\n' + t2.toString();
        } catch (Exception e) {
            StringWriter sw = new StringWriter();
            PrintWriter pw = new PrintWriter(sw);
            e.printStackTrace(pw);
            return sw.toString();
        }
    }
}