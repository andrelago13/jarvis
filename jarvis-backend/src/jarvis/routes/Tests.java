package jarvis.routes;

import jarvis.tests.TestSuite;

import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;

@Path("/tests")
public class Tests {
    @GET
    @Produces("text/plain")
    public String getClichedMessage() {
        return TestSuite.runTestsAsString();
    }
}
