package life.environment;

import java.util.concurrent.ArrayBlockingQueue;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.Callable;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;
import java.util.concurrent.RejectedExecutionException;
import java.util.concurrent.SynchronousQueue;
import java.util.concurrent.TimeUnit;
import net.jcip.annotations.ThreadSafe;
import life.environment.exceptions.CellDiedException;
import life.organic.SexualCell;
import life.organic.SexualCell.SEX;

@ThreadSafe
class MatchMaker implements PartnerMatcher {

  private static final int QUEUE_CAPACITY = 5 * 42;
  private static final int THREAD_POOL_SIZE = 10;
  private final BlockingQueue<Couple> couples = new ArrayBlockingQueue<Couple>(MatchMaker.QUEUE_CAPACITY);
  private final ExecutorService matchingThreads = Executors.newFixedThreadPool(MatchMaker.THREAD_POOL_SIZE);

  private final long starvationTime;
  private final TimeUnit timeUnit;

  public MatchMaker(long starvationTime, TimeUnit timeUnit) {
    this.starvationTime = starvationTime;
    this.timeUnit = timeUnit;
  }

  private static final class Couple {
    private final SynchronousQueue<SexualCell> female;
    private final SexualCell male;
    private volatile boolean isValidCouple = true;

    private Couple(SexualCell male) {
      this.male = male;
      this.female = new SynchronousQueue<SexualCell>();
    }

    private static Couple newCouple(SexualCell male) {
      return new Couple(male);
    }
  }

  @Override
  public SexualCell findMatch(SexualCell cell) throws CellDiedException {
    try {
      Future<SexualCell> partner = this.matchingThreads.submit(new Match(cell));
      return partner.get();
    } catch (RejectedExecutionException e) {
      throw new CellDiedException("Cell died due to environment shutdown");
    } catch (InterruptedException e) {
      throw new CellDiedException("Cell died of starvation trying to find a partner");
    } catch (ExecutionException e) {
      throw new CellDiedException("Cell died due to the environment. Take that, hippies.");
    }
  }

  @Override
  public void shutdown() {
    this.matchingThreads.shutdown();
  }

  @Override
  public void awaitTermination(long timeout, TimeUnit unit) {
    try {
      this.matchingThreads.awaitTermination(timeout, unit);
    } catch (InterruptedException ignore) {
    }
  }

  @ThreadSafe
  class Match implements Callable<SexualCell> {
    private final SexualCell findMeAPartner;

    private Match(SexualCell findMeAPartner) {
      this.findMeAPartner = findMeAPartner;
    }

    @Override
    public SexualCell call() throws Exception {
      if (this.findMeAPartner.getSex() == SEX.MALE) {
        Couple tempCouple = Couple.newCouple(this.findMeAPartner);
        try {
          MatchMaker.this.couples.add(tempCouple);
        } catch (IllegalStateException queueIsFull) {
          throw new CellDiedException("You died because of overcrowdment");
        }
        SexualCell female = tempCouple.female.poll(MatchMaker.this.starvationTime, MatchMaker.this.timeUnit);
        synchronized (tempCouple) {
          if (female == null) {
            tempCouple.isValidCouple = false;
            MatchMaker.this.couples.remove(tempCouple);
            throw new CellDiedException();
          }
        }
        return female;
      } else {
        Couple partnerCouple = MatchMaker.this.couples.poll(MatchMaker.this.starvationTime, MatchMaker.this.timeUnit);
        try {
          synchronized (partnerCouple) {
            if (!partnerCouple.isValidCouple)
              throw new CellDiedException();
          }
        } catch (NullPointerException noPartnerCouple) {
          throw new CellDiedException();
        }

        partnerCouple.female.add(this.findMeAPartner);
        return partnerCouple.male;
      }
    }
  }// END Match
}
