package interpreter.exceptions;

public class UnrecognizedSymbol extends Exception {
  /**
     * 
     */
  private static final long serialVersionUID = 1L;

  public UnrecognizedSymbol(final String text) {
    super("Unrecognized Symbol: " + text);
  }

}
