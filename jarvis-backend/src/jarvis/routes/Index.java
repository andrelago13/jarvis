package jarvis.routes;

import dialogflow.QueryResponse;
import mongodb.MongoDB;
import org.json.JSONObject;
import slack.SlackUtil;

import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import java.io.IOException;

@Path("/")
public class Index {
    @GET
    // The Java method will produce content identified by the MIME Media type "text/plain"
    @Produces("text/plain")
    public String getClichedMessage() throws IOException, ClassNotFoundException {
        JSONObject obj = new JSONObject();
        obj.put("key", "value");
        return obj.toString();
    }
}

/*
{
    id: '1503c4ba-948c-4a40-9cfc-cf224a37834b',
    timestamp: '2018-01-08T17:23:10.919Z',
    lang: 'en',
    result: {
        source: 'agent',
        resolvedQuery: 'turn on the lights',
        speech: '',
        action: '',
        actionIncomplete: false,
        parameters: {
            actuator: 'light switch'
        },
        contexts: [],
        metadata: {
            intentId: 'e7e07192-7e72-48d5-b702-aea5f8a79f4b',
            webhookUsed: 'true',
            webhookForSlotFillingUsed: 'true',
            intentName: 'Turn the thing on'
        },
        fulfillment: {
            speech: 'Done',
            messages: [Array]
        },
        score: 1
    },
    status: {
        code: 200,
        errorType: 'success',
        webhookTimedOut: false
    },
    sessionId: '730cbf64-7ff0-4b3d-8140-fa24b98193c8'
}
*/