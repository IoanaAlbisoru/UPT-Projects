package life.environment.exceptions;

public class CellDiedException extends Exception {

  private static final long serialVersionUID = 1L;

  public CellDiedException(String msg) {
    super(msg);
  }

  public CellDiedException() {
    super();
  }
}