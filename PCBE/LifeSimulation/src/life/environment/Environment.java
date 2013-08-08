package life.environment;

import java.util.concurrent.CountDownLatch;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.RejectedExecutionException;
import java.util.concurrent.TimeUnit;

import life.environment.exceptions.CellDiedException;
import life.organic.Cell;
import life.organic.Food;
import life.organic.SexualCell;

public final class Environment {
  private final Parameters parameters;
  private final ExecutorService cellExecutor;
  private final CountDownLatch startSimulationLatch;
  private final PartnerMatcher matchMaker;
  private final World worldMap;

  private Environment(Parameters parameters) {
    this.parameters = parameters;
    this.cellExecutor = Executors.newFixedThreadPool(10);
    this.startSimulationLatch = new CountDownLatch(1);
    this.matchMaker = new MatchMaker(this.parameters.STARVATION_TIME, this.parameters.TIME_UNIT);
    this.worldMap = new Map(this.parameters.STARTING_FOOD, this.parameters.MAX_FOOD_SEARCH_TIME, this.parameters.FOOD_GENERATION_RANGE_LOW,
        this.parameters.FOOD_GENERATION_RANGE_HIGH);
  }

  public final static Environment createEnvironment(Parameters parameters) {
    return new Environment(parameters);
  }

  public final void destroy() throws InterruptedException {
    this.isDying = true;
    this.cellExecutor.shutdown();
    this.matchMaker.shutdown();
    this.cellExecutor.awaitTermination(2, TimeUnit.SECONDS);
    this.matchMaker.awaitTermination(2, TimeUnit.SECONDS);
  }

  private volatile boolean isDying = false;

  public boolean isDying() {
    return this.isDying;
  }

  public final void start() {
    this.startSimulationLatch.countDown();
  }

  public final void awaitForStart() throws InterruptedException {
    this.startSimulationLatch.await();
  }

  public SexualCell findSexualPartner(SexualCell cell) throws CellDiedException {
    SexualCell findMatch = this.matchMaker.findMatch(cell);
    return findMatch;
  }

  public void addCell(Cell cell) {
    try {
      this.cellExecutor.execute(cell);
      this.worldMap.addCell(cell);
    } catch (RejectedExecutionException ignore) {
    }
  }

  public void removeCell(Cell cell) {

    this.worldMap.removeCell(cell);
  }

  public final Food findFood() {
    return this.worldMap.getFood();
  }

  public Parameters getParameters() {
    return this.parameters;
  }

}
