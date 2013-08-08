package server;

import java.io.Serializable;
import java.util.Random;

import commonI.StockI;

public class StockShare implements StockI, Serializable {
  /**
   * 
   */
  private static final long serialVersionUID = 3575025226797029474L;
  private final String name;
  private int price;

  public StockShare(final String name) {
    this.name = name;
    this.price = new Random().nextInt(5000);
  }

  @Override
  public int price() {
    return this.price;
  }

  @Override
  public String name() {
    return this.name;
  }

  void changePrice(final Random rand) {
    this.price = rand.nextInt(5000);
  }

  @Override
  public String toString() {
    return this.name + ": " + this.price;
  }

}
