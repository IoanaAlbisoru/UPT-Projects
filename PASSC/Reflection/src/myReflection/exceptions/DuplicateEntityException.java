package myReflection.exceptions;

public class DuplicateEntityException extends Exception {

  /**
   * 
   */
  private static final long serialVersionUID = 3796284335554349247L;

  public DuplicateEntityException(final String name) {
    super(String.format("Entity '%s' already exists.", name));
  }
}
