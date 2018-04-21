package jarvis.actions.causality;

public class CommandCause {
  private Object mCauseObj;
  private String mCauseText;

  public CommandCause(Object cause, String text) {
    mCauseObj = cause;
    mCauseText = text;
  }

  public Object getCause() {
    return mCauseObj;
  }

  public String getCauseText() {
    return mCauseText;
  }
}
