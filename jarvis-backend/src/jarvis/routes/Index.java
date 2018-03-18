package jarvis.routes;

import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import java.io.IOException;
import java.io.PrintWriter;
import java.io.StringWriter;

import com.rabbitmq.client.ConnectionFactory;
import com.rabbitmq.client.Connection;
import com.rabbitmq.client.Channel;

@Path("/")
public class Index {
    public static String mobj;

    @GET
    // The Java method will produce content identified by the MIME Media type "text/plain"
    @Produces("application/json")
    public String getClichedMessage() throws IOException, ClassNotFoundException {
        String res = "Nothing to see here :)";
        try {
            if (mobj != null) {
                return mobj;
            }

            ConnectionFactory factory = new ConnectionFactory();
            factory.setHost("jarvis-iot.ml");
            factory.setUsername("rabbitmq");
            factory.setPassword("rabbitmq");
            Connection connection = factory.newConnection();
            Channel channel = connection.createChannel();

            String queue = "/house/bedroom_light/actions";
            channel.queueDeclare(queue, false, false, false, null);
            String message = "on";
            channel.basicPublish("", queue, null, message.getBytes());
            System.out.println(" [x] Sent '" + message + "'");

            channel.close();
            connection.close();
        } catch (Exception e) {
            StringWriter sw = new StringWriter();
            PrintWriter pw = new PrintWriter(sw);
            e.printStackTrace(pw);
            return sw.toString();
        }
        return res;
    }
}