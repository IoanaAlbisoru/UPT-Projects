package life.tools;

import net.jcip.annotations.Immutable;
import net.jcip.annotations.ThreadSafe;

@ThreadSafe
@Immutable
public class SexualCellEvent extends CellEvent {

  private final String partner;

  public SexualCellEvent(String cellState, String partnerState, String message) {
    super(cellState, message);
    this.partner = partnerState;
  }

  @Override
  public String toString() {
    String temp = super.toString() + "\n<<Is mating with:>>\n" + this.partner;
    return temp;
  }
}
