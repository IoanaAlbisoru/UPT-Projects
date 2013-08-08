package interpreter.exceptions;

public class DuplicateVariable extends Exception {

  /**
   * 
   */
  private static final long serialVersionUID = -856180614072381297L;

  public DuplicateVariable(final String text) {
    super("Duplicate variable: " + text);
  }

}
