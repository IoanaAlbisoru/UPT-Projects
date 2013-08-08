package life.environment;

import life.organic.Cell;
import life.organic.Food;

interface World {
  public abstract Food getFood();

  public abstract void removeCell(Cell cellToRemove);

  public abstract void addCell(Cell toAdd);

}