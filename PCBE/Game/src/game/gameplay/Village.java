package game.gameplay;

import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.concurrent.locks.ReentrantLock;

public class Village {
  public final Location location;
  public final int ID;
  private static final AtomicInteger IDCounter = new AtomicInteger(1);

  public Village(Location loc) {
    this.location = loc;
    this.ID = Village.IDCounter.getAndIncrement();

  }

  int numberOfTroops = 10;
  int numberOfForeignTroops = 0;
  int wood = 100;
  int food = 100;
  int iron = 100;

  @Override
  public boolean equals(Object obj) {
    if (obj instanceof Village) {
      Village village = (Village) obj;
      if (village.ID == this.ID)
        return true;
    }

    return false;
  }

  private final ReentrantLock lock = new ReentrantLock();

  public void lock() {
    this.lock.lock();
  }

  public void tryLock() throws InterruptedException {
    this.lock.tryLock(100, TimeUnit.MILLISECONDS);
  }

  public void unlock() {
    this.lock.unlock();
  }
}
