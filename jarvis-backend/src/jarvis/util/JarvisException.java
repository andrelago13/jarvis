package jarvis.util;

public class JarvisException extends Exception {

  public JarvisException(String e) {
    super(e);
  }

  public JarvisException(Exception e) {
    super("Jarvis exception: ", e);
  }
}
