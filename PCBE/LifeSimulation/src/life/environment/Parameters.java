package life.environment;

import java.util.Random;
import java.util.concurrent.TimeUnit;

import net.jcip.annotations.Immutable;
import net.jcip.annotations.ThreadSafe;

import life.tools.Logger;

@ThreadSafe
@Immutable
public class Parameters {

  public final long TIME_CELL_STAYS_FED;
  public final long STARVATION_TIME;
  public final long MAX_FOOD_SEARCH_TIME;
  public final TimeUnit TIME_UNIT;
  public final int FOOD_NEEDED_BEFORE_REPRODUCTION;
  public final int STARTING_FOOD;
  public final Logger LOGGER;
  public final int FOOD_GENERATION_RANGE_LOW;
  public final int FOOD_GENERATION_RANGE_HIGH;

  public Parameters(long timeCellStaysFed, long starvationTime, long maxFoodSearchTime, TimeUnit timeUnit, int nrOfMealsBeforeReproduction, int startingFood,
      int foodGenerationLOW, int foodGenerationHIGH, Logger logger) {

    IllegalArgumentException illegalArgumentException = new IllegalArgumentException();

    if (timeCellStaysFed < 0 || starvationTime < 0 || maxFoodSearchTime < 0)
      throw illegalArgumentException;
    this.TIME_CELL_STAYS_FED = timeCellStaysFed;
    this.STARVATION_TIME = starvationTime;
    this.MAX_FOOD_SEARCH_TIME = maxFoodSearchTime;
    this.TIME_UNIT = timeUnit;

    if (nrOfMealsBeforeReproduction < 1)
      throw illegalArgumentException;

    this.FOOD_NEEDED_BEFORE_REPRODUCTION = nrOfMealsBeforeReproduction;

    if (startingFood < 0)
      throw illegalArgumentException;

    this.STARTING_FOOD = startingFood;

    if (foodGenerationHIGH < 0 || foodGenerationLOW < 0)
      throw illegalArgumentException;

    if (foodGenerationHIGH < foodGenerationLOW)
      throw illegalArgumentException;

    this.FOOD_GENERATION_RANGE_LOW = foodGenerationLOW;
    this.FOOD_GENERATION_RANGE_HIGH = foodGenerationHIGH;

    if (logger == null)
      throw illegalArgumentException;

    this.LOGGER = logger;
  }

  static final double THRESHOLD_FOR_FINDING_FOOD = 0.5;

  static boolean foundFood(int totalAmountOfFood) {
    return calculateOddsOfFindingFood(totalAmountOfFood) > Parameters.THRESHOLD_FOR_FINDING_FOOD;
  }

  private static double calculateOddsOfFindingFood(int totalAmountOfFood) {
    // TODO implement a function that depends on foodQuantity
    Random rand = new Random();
    if (totalAmountOfFood == 0)
      return rand.nextDouble();

    double temp = rand.nextDouble();
    double temp2 = totalAmountOfFood * temp;

    temp = (temp * temp2) / temp2;

    return temp;
  }
}
