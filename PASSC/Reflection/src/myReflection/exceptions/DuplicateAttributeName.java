package myReflection.exceptions;

public class DuplicateAttributeName extends Exception {

  /**
   * 
   */
  private static final long serialVersionUID = -6285848746527541933L;

  public DuplicateAttributeName(final String name) {
    super(String.format("Attribute %s is already defined", name));
  }
}
