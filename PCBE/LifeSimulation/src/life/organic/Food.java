package life.organic;

import net.jcip.annotations.Immutable;
import net.jcip.annotations.ThreadSafe;

@ThreadSafe
@Immutable
public class Food {
  private final long timeItTookToFindFood;

  public Food(long time) {
    this.timeItTookToFindFood = time;
  }

  public long getTimeItTookToFindFood() {
    return this.timeItTookToFindFood;
  }

  @ThreadSafe
  @Immutable
  public static class NoFood extends Food {

    public NoFood(long time) {
      super(time);
    }

    @Override
    public long getTimeItTookToFindFood() {
      return (-1) * super.getTimeItTookToFindFood();
    }
  }

  @Override
  public boolean equals(Object obj) {
    if (obj instanceof Food)
      return true;
    else
      return false;
  }

}
