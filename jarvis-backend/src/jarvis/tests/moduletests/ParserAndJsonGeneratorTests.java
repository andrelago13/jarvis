package jarvis.tests.moduletests;

import jarvis.controllers.OnOffLight;
import jarvis.tests.Test;
import org.json.JSONObject;

public class ParserAndJsonGeneratorTests implements Test {

  private static final String sampleOnOffLight = "{\"name\":\"testlight\",\"description\":\"On/Off light switch\",\"links\":{\"actions\":\"/base/actions\",\"properties\":\"/base/properties\",\"events\":\"/base/events\"},\"type\":\"onOffLight\",\"properties\":{\"status\":{\"description\":\"Describes current state of the switch (true=on)\",\"type\":\"boolean\"}}}";

  @Override
  public int test() {
    if (!test1()) {
      return 1;
    }
    if (!test2()) {
      return 2;
    }

    return -1;
  }

  private boolean test1() {
    OnOffLight l = OnOffLight.Builder.getDefaultBuilder("testlight", "/base").build();
    return sampleOnOffLight.equals(l.toString());
  }

  private boolean test2() {
    OnOffLight l = OnOffLight.Builder.getDefaultBuilder("testlight", "/base").build();
    String str1 = l.toString();
    OnOffLight t2 = OnOffLight.Builder.buildFromJSON(new JSONObject(str1));
    return str1.equals(t2.toString());
  }
}
