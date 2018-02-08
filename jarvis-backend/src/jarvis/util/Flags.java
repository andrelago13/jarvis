package jarvis.util;

public class Flags {
    public static final String LOCAL_EXECUTION = "jarvis_local";

    public static boolean isLocalExecution() {
        return "true".equals(System.getenv("jarvis_local"));
    }
}
