package life;

import java.util.Collection;
import java.util.concurrent.TimeUnit;

import life.environment.Environment;
import life.environment.Parameters;
import life.organic.SexualCell;
import life.organic.SexualCell.SEX;
import life.tools.FileLogger;

public class Main {

  private final static long RUNNING_TIME = TimeUnit.MILLISECONDS.convert(4, TimeUnit.SECONDS);

  public static void main(String[] args) {
    long timeCellStaysFed = 300;
    long starvationTime = 300;
    long maxFoodSearchTime = 150;
    TimeUnit timeUnit = TimeUnit.MILLISECONDS;
    int nrOfMealsBeforeReproduction = 1;
    int startingFood = 10;
    int foodGenerationLOW = 1;
    int foodGenerationHIGH = 2;
    FileLogger logger = new FileLogger();

    Parameters parameters = new Parameters(timeCellStaysFed, starvationTime, maxFoodSearchTime, timeUnit, nrOfMealsBeforeReproduction, startingFood,
        foodGenerationLOW, foodGenerationHIGH, logger);

    Environment env = Environment.createEnvironment(parameters);

    // Collection<AsexualCell> acell = AsexualCell.createBatchOfCells(env, 1);
    Collection<SexualCell> scell = SexualCell.createBatchOfCells(env, 1, SEX.FEMALE);
    scell.addAll(SexualCell.createBatchOfCells(env, 1, SEX.MALE));

    // for (AsexualCell asexualCell : acell) {
    // env.addCell(asexualCell);
    // }
    for (SexualCell sexualCell : scell)
      env.addCell(sexualCell);

    try {
      System.out.println("Starting");
      env.start();
      Thread.sleep(Main.RUNNING_TIME);
      System.out.println("Initiating environment destruction");
      env.destroy();
      logger.dump("out.txt");
    } catch (InterruptedException e) {
      System.out.println("FAIL: did not stop");
    }
    System.out.println("Done");
    System.exit(0);
  }

}
