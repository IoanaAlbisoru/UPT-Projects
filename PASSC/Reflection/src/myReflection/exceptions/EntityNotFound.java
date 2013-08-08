package myReflection.exceptions;

public class EntityNotFound extends Exception {

  /**
   * 
   */
  private static final long serialVersionUID = 1132764384286524931L;

  public EntityNotFound(final String name) {
    super("No such entity: " + name);
  }
}
