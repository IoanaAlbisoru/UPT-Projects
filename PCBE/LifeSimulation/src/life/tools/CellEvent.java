package life.tools;

import net.jcip.annotations.Immutable;
import net.jcip.annotations.ThreadSafe;

@Immutable
@ThreadSafe
public class CellEvent extends Event {

  protected final String cell;

  public CellEvent(String cellState, String message) {
    super(message);
    this.cell = cellState;
  }

  public String getCell() {
    return this.cell;
  }

  @Override
  public String toString() {
    String temp = super.toString() + this.cell;
    return temp;
  }

}
