package life.environment;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Random;
import java.util.concurrent.ConcurrentLinkedQueue;

import net.jcip.annotations.ThreadSafe;

import life.organic.Cell;
import life.organic.Food;
import life.organic.Food.NoFood;

@ThreadSafe
class Map implements World {

  private final ConcurrentLinkedQueue<Food> food = new ConcurrentLinkedQueue<Food>();
  private final long max_FOOD_SEARCH_TIME;
  private final int FOOD_GENERATION_RANGE_LOW;
  private final int FOOD_GENERATION_RANGE_HIGH;

  public Map(int starting_FOOD, long max_FOOD_SEARCH_TIME, int fOOD_GENERATION_RANGE_LOW, int fOOD_GENERATION_RANGE_HIGH) {
    this.max_FOOD_SEARCH_TIME = max_FOOD_SEARCH_TIME;
    this.FOOD_GENERATION_RANGE_LOW = fOOD_GENERATION_RANGE_LOW;
    this.FOOD_GENERATION_RANGE_HIGH = fOOD_GENERATION_RANGE_HIGH;
    for (int i = 0; i < starting_FOOD; i++)
      this.food.add(new Food(this.generateFoodTime()));
  }

  @Override
  public Food getFood() {
    // it's ok, we don't need a precise food.size(); it's a quasi random
    // function anyway.
    if (Parameters.foundFood(this.food.size())) {
      Food givenFood = this.food.poll();
      if (givenFood == null)
        return new NoFood(this.generateFoodTime());
      else
        return givenFood;
    } else
      return new NoFood(this.generateFoodTime());
  }

  @Override
  public void removeCell(Cell cellToRemove) {
    Random rand = new Random();
    int foodNr = rand.nextInt(this.FOOD_GENERATION_RANGE_LOW) + this.FOOD_GENERATION_RANGE_HIGH - this.FOOD_GENERATION_RANGE_LOW;
    Collection<Food> foodToAdd = this.generateFood(foodNr);
    this.food.addAll(foodToAdd);
  }

  @Override
  public void addCell(Cell toAdd) {
  }

  private Collection<Food> generateFood(int number) {
    Collection<Food> initialFood = new ArrayList<Food>(number);
    for (int i = 0; i < number; i++)
      initialFood.add(new Food(this.generateFoodTime()));
    return initialFood;
  }

  private long generateFoodTime() {
    return new Random().nextInt((int) this.max_FOOD_SEARCH_TIME);
  }

}
