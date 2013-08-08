package life.organic;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Random;

import net.jcip.annotations.ThreadSafe;
import life.environment.Environment;
import life.environment.exceptions.CellDiedException;
import life.tools.CellEvent;
import life.tools.SexualCellEvent;

@ThreadSafe
public class SexualCell extends Cell {

  private static final String DAUGHTER_CELL_BIRTH_MSG = "Two cells spawned me!";
  private static final String PRE_REPRODUCTION_MSG = "MATING";

  public static enum SEX {
    MALE {
      @Override
      public String toString() {
        return "Male";
      }
    },
    FEMALE {
      @Override
      public String toString() {
        return "Female";
      }
    };
  }

  private final SEX sex;

  private SexualCell(Environment env) {
    super(env);
    Random rand = new Random();
    if (rand.nextBoolean())
      this.sex = SEX.MALE;
    else
      this.sex = SEX.FEMALE;
  }

  private SexualCell(Environment env, SEX sex) {
    super(env);
    this.sex = sex;
  }

  public static final Collection<SexualCell> createBatchOfCells(Environment env, int nr, SEX sex) {
    Collection<SexualCell> temp = new ArrayList<SexualCell>();
    for (int i = 0; i < nr; i++)
      temp.add(new SexualCell(env, sex));
    return temp;
  }

  @Override
  protected void procreate() throws CellDiedException {
    SexualCell partner = this.getEnvironment().findSexualPartner(this);
    this.getEnvironment().getParameters().LOGGER.log(new SexualCellEvent(this.toString(), partner.toString(), SexualCell.PRE_REPRODUCTION_MSG));
    this.reproduce(partner);
  }

  private final void reproduce(SexualCell partner) {
    if (partner != null && this.sex == SEX.FEMALE && partner.sex == SEX.MALE) {
      SexualCell child = this.birthChild(partner);

      this.getEnvironment().getParameters().LOGGER.log(new CellEvent(child.toString(), SexualCell.DAUGHTER_CELL_BIRTH_MSG));

      this.getEnvironment().addCell(child);
      return;
    }

    if (partner != null && this.sex == SEX.MALE && partner.sex == SEX.FEMALE)
      return;

  }

  private final SexualCell birthChild(SexualCell partner) {
    return new SexualCell(this.getEnvironment());
  }

  public SEX getSex() {
    return this.sex;
  }

  @Override
  public String toString() {
    return super.toString() + "\n Sexual Cell; Gender=" + this.sex.toString();
  }
}
