package life.environment;

import java.util.concurrent.TimeUnit;

import life.environment.exceptions.CellDiedException;
import life.organic.SexualCell;

interface PartnerMatcher {
  /**
   * @param cell
   *          the cell for which to find a partner
   * @return the partner cell. Be aware that if cellNr1 gets cellNr2 as partner
   *         then cellNr2 will get cellNr1 as its partner
   * @throws CellDiedException
   *           will be thrown in case the cell is dead, the cause doesn't really
   *           matter;
   */
  public SexualCell findMatch(SexualCell cell) throws CellDiedException;

  public void shutdown();

  public void awaitTermination(long timeout, TimeUnit unit);
}