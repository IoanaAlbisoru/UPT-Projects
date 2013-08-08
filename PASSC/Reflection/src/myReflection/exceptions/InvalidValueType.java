package myReflection.exceptions;

public class InvalidValueType extends Exception {

  /**
   * 
   */
  private static final long serialVersionUID = 6057878110245274669L;

  public InvalidValueType(final String where) {
    super("Invalid value type @" + where);
  }

}
