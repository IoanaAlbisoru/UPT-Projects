package life.organic;

import java.util.ArrayList;
import java.util.Collection;

import net.jcip.annotations.ThreadSafe;
import life.environment.Environment;
import life.tools.CellEvent;

@ThreadSafe
public class AsexualCell extends Cell {

  private static final String DAUGHTER_CELL_BIRTH_MSG = "I am alive";
  private static final String PRE_REPRODUCTION_MSG = "SPLITTING!";

  protected AsexualCell(Environment env) {
    super(env);
  }

  public static final Collection<AsexualCell> createBatchOfCells(Environment env, int nr) {
    Collection<AsexualCell> temp = new ArrayList<AsexualCell>();
    for (int i = 0; i < nr; i++)
      temp.add(new AsexualCell(env));
    return temp;
  }

  @Override
  protected void procreate() {
    this.getEnvironment().getParameters().LOGGER.log(new CellEvent(this.toString(), AsexualCell.PRE_REPRODUCTION_MSG));
    AsexualCell child = new AsexualCell(this.getEnvironment());
    this.getEnvironment().getParameters().LOGGER.log(new CellEvent(child.toString(), AsexualCell.DAUGHTER_CELL_BIRTH_MSG));
  }

  @Override
  public String toString() {
    return super.toString() + "\nAsexual Cell";
  }

}
