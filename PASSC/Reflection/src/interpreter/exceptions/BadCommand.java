package interpreter.exceptions;

public class BadCommand extends Exception {

  /**
   * 
   */
  private static final long serialVersionUID = 1L;

  public BadCommand(final String text) {
    super(text);
  }

}
