package jarvis.tests;

import jarvis.tests.moduletests.ParserAndJsonGeneratorTests;

import java.util.ArrayList;
import java.util.List;

public class TestSuite {
    public static boolean runTests() {
        List<Test> tests = new ArrayList<>();

        tests.add(new ParserAndJsonGeneratorTests());

        for(int i = 0; i < tests.size(); ++i) {
            int res = tests.get(i).test();
            if(res != -1) {
                return false;
            }
        }

        return true;
    }

    public static String runTestsAsString() {
        List<Test> tests = new ArrayList<>();

        tests.add(new ParserAndJsonGeneratorTests());

        for(int i = 0; i < tests.size(); ++i) {
            int res = tests.get(i).test();
            if(res != -1) {
                return "" + i + " : " + res;
            }
        }

        return "OK";
    }
}
