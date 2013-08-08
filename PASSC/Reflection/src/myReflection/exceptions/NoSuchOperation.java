package myReflection.exceptions;

public final class NoSuchOperation extends Exception {

  /**
   * 
   */
  private static final long serialVersionUID = 6084470407386793217L;

  public NoSuchOperation(final String name, final String entity) {
    super(String.format("No such operation operation '%s' in entity %s", name, entity));
  }
}
