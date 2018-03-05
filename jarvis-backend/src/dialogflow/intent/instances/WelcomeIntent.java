package dialogflow.intent.instances;

import dialogflow.DialogFlowRequest;
import dialogflow.QueryResponse;
import dialogflow.intent.DialogFlowIntent;
import jarvis.util.JarvisException;
import res.Config;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ThreadLocalRandom;

public class WelcomeIntent extends DialogFlowIntent {
    public static final String INTENT_NAME = Config.DF_WELCOME_INTENT_NAME;
    public static final String INTENT_ID = Config.DF_WELCOME_INTENT_ID;

    private static List<String> responses = new ArrayList<>();
    static {
        responses.add("Hi!");
        responses.add("Hello!");
        responses.add("Greetings!");
        responses.add("Hey! What's up?");
        responses.add("How can I help?");
        responses.add("Hello there");
    }

    private DialogFlowRequest mRequest;

    public WelcomeIntent(DialogFlowRequest request) {
        mRequest = request;
    }

    @Override
    public QueryResponse execute() throws JarvisException {
        QueryResponse response = new QueryResponse();
        int randomNum = ThreadLocalRandom.current().nextInt(0, responses.size());
        response.addFulfillmentMessage(responses.get(randomNum));
        return response;
    }
}
