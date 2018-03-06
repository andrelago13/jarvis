package jarvis.routes;

import org.json.JSONObject;
import slack.SlackUtil;

import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import java.io.IOException;
import java.io.PrintWriter;
import java.io.StringWriter;
import java.sql.Timestamp;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

@Path("/")
public class Index {
    public static String mobj;

    @GET
    // The Java method will produce content identified by the MIME Media type "text/plain"
    @Produces("application/json")
    public String getClichedMessage() throws IOException, ClassNotFoundException {
        try {
            if (mobj != null) {
                return mobj;
            }
            JSONObject obj = new JSONObject();
            obj.put("key", "value");

            ScheduledExecutorService executor =
                    Executors.newSingleThreadScheduledExecutor();
            executor.schedule(new Runnable() {
                @Override
                public void run() {
                    SlackUtil.sendIoTMessage("test schedule");
                }
            }, 10, TimeUnit.SECONDS);

            SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd hh:mm:ss");
            String date = "2017-07-12T16:30:00Z";
            date.replace('T', ' ');
            Date parsedDate = dateFormat.parse(date.replace('T', ' '));
            Timestamp timestamp = new java.sql.Timestamp(parsedDate.getTime());
            return timestamp.toString();
        } catch (Exception e) {
            StringWriter sw = new StringWriter();
            PrintWriter pw = new PrintWriter(sw);
            e.printStackTrace(pw);
            return sw.toString();
        }
    }
}