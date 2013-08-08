package myReflection.exceptions;

public class DuplicateOperation extends Exception {

  /**
   * 
   */
  private static final long serialVersionUID = 1L;

  public DuplicateOperation(final String name) {
    super(String.format("Duplicate operation '%s'", name));
  }

}
