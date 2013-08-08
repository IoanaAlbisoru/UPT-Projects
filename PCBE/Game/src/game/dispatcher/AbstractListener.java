package game.dispatcher;

import java.util.concurrent.ArrayBlockingQueue;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.TimeUnit;

public abstract class AbstractListener implements Runnable, Listener {

  private static final int QUEUE_SIZE = 5;
  private volatile boolean active = true;
  private final BlockingQueue<Event> eventQueue = new ArrayBlockingQueue<Event>(AbstractListener.QUEUE_SIZE);

  /* (non-Javadoc)
   * @see game.dispatcher.Listener#sendEvent(game.dispatcher.Event)
   */
  @Override
  public void sendEvent(Event event) {
    if (this.active)
      try {
        this.eventQueue.offer(event, 100, TimeUnit.MILLISECONDS);
      } catch (InterruptedException e) {
        System.out.println("HOLY SHIT Couldn't send event");
        e.printStackTrace();
      }
  }

  protected abstract void processEvent(Event event);

  protected final void kill() {
    this.active = false;
  }

  @Override
  public abstract boolean equals(Object obj);

  @Override
  public abstract int hashCode();

  @Override
  public void run() {
    while (!Thread.currentThread().isInterrupted())
      try {
        Event event = this.eventQueue.take();
        this.processEvent(event);
      } catch (InterruptedException timeToShutDown) {
        if (!Thread.currentThread().isInterrupted())
          return;
      }
  }

}
