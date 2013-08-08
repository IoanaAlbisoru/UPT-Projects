package interpreter.exceptions;

public class InvalidArgumentType extends Exception {
  /**
   * 
   */
  private static final long serialVersionUID = 1L;

  public InvalidArgumentType(final String text) {
    super("Invalid Argument Type: " + text);
  }

}
