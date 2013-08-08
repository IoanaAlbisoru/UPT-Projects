package interpreter.exceptions;

public class UninitializedVariables extends Exception {

  /**
   * 
   */
  private static final long serialVersionUID = 8811596562015466658L;

  public UninitializedVariables(final String text) {
    super("Uninitialized variables, cannot execute: " + text);
  }

}
