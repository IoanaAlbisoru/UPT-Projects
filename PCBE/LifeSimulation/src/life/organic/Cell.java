package life.organic;

import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicInteger;

import life.environment.Environment;
import life.environment.Parameters;
import life.tools.CellEvent;
import life.tools.Logger;

public abstract class Cell implements Runnable {
  private final Environment environment;

  private final int ID;
  private int timesHasEaten = 0;
  private static final AtomicInteger CELL_ID = new AtomicInteger(0);

  protected Cell(Environment env) {
    this.environment = env;
    this.ID = Cell.CELL_ID.getAndAdd(1);
  }

  protected abstract void procreate() throws Exception;

  protected Environment getEnvironment() {
    return this.environment;
  }

  private final boolean findFood() {
    Parameters parameters = this.getEnvironment().getParameters();
    long maxWaitTime = TimeUnit.NANOSECONDS.convert(parameters.STARVATION_TIME, parameters.TIME_UNIT);

    long originalTime = this.timeForFood();
    long timeItTookToFindFood = TimeUnit.NANOSECONDS.convert(Math.abs(originalTime), parameters.TIME_UNIT);

    if (originalTime < 0)
      timeItTookToFindFood = (-1) * timeItTookToFindFood;

    long cummulatedTime = Math.abs(timeItTookToFindFood);
    while (timeItTookToFindFood < 0) {
      if (cummulatedTime > maxWaitTime)
        return false;

      // it means we found food and we can get the hell out;
      timeItTookToFindFood = this.timeForFood();
      if (timeItTookToFindFood > 0)
        return true;
      else
        cummulatedTime += TimeUnit.NANOSECONDS.convert(Math.abs(timeItTookToFindFood), parameters.TIME_UNIT);

      if (cummulatedTime > maxWaitTime)
        return false;
    }
    return true;
  }

  private long timeForFood() {
    return this.getEnvironment().findFood().getTimeItTookToFindFood();
  }

  @Override
  public final void run() {
    try {
      this.getEnvironment().awaitForStart();

      Logger logger = this.getEnvironment().getParameters().LOGGER;
      long timeToSleep = TimeUnit.MILLISECONDS.convert(this.getEnvironment().getParameters().TIME_CELL_STAYS_FED, this.getEnvironment().getParameters().TIME_UNIT);

      while (!this.getEnvironment().isDying() && !Thread.currentThread().isInterrupted())
        try {
          Thread.sleep(timeToSleep);
          if (!this.findFood()) {
            this.getEnvironment().removeCell(this);
            logger.log(new CellEvent(this.toString(), "DEAD"));
            return;
          }

          this.incTimesHasEaten();
          if (this.getTimesHasEaten() % this.getEnvironment().getParameters().FOOD_NEEDED_BEFORE_REPRODUCTION == 0)
            this.procreate();

        } catch (Exception e) {
          logger.log(new CellEvent(this.toString(), "DEAD"));
          this.getEnvironment().removeCell(this);
          return;
        }
    } catch (InterruptedException e) {
      // the access to std err needs to be synchronized;
      synchronized (System.class) {
        System.err.println("Environment start timed out;");
      }
      return;
    }
  }

  @Override
  public boolean equals(Object obj) {
    if (obj instanceof Cell) {
      Cell temp = (Cell) obj;
      return temp.ID == this.ID;
    }
    return false;
  }

  @Override
  public int hashCode() {
    return (int) this.ID;
  }

  public void incTimesHasEaten() {
    this.timesHasEaten++;
    this.getEnvironment().getParameters().LOGGER.log(new CellEvent(this.toString(), "Just ate"));
  }

  public int getTimesHasEaten() {
    return this.timesHasEaten;
  }

  @Override
  public String toString() {
    return "ID=" + this.ID + "\nTimes Eaten=" + this.timesHasEaten;
  }
}
