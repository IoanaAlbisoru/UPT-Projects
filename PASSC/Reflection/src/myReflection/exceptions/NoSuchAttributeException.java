package myReflection.exceptions;

public class NoSuchAttributeException extends Exception {

  /**
   * 
   */
  private static final long serialVersionUID = 8745905680270299565L;

  public NoSuchAttributeException(final String name, final String where) {
    super("No attribute '" + name + "' @ " + where);
  }
}
