package game.dispatcher;

import java.util.Map.Entry;
import java.util.concurrent.ArrayBlockingQueue;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.ConcurrentHashMap;

public class ConcreteDispatcher implements Dispatcher, Runnable {

  private static final int _5 = 5;
  private static final int QUEUE_SIZE = 42;
  private final BlockingQueue<Event> eventQueue = new ArrayBlockingQueue<>(ConcreteDispatcher.QUEUE_SIZE);
  private final ConcurrentHashMap<Filter, AbstractListener> listenerMap = new ConcurrentHashMap<>(ConcreteDispatcher._5);

  @Override
  public void run() {
    while (!Thread.currentThread().isInterrupted()) {
      Event event;
      try {
        event = this.eventQueue.take();
      } catch (InterruptedException systemIsShuttingDown) {
        return;
      }

      for (Entry<Filter, AbstractListener> entry : this.listenerMap.entrySet())
        try {
          if (entry.getKey().accept(event))
            entry.getValue().sendEvent(event);
        } catch (NullPointerException ignore) {
        }

    }
  }

  @Override
  public void register(Filter filter, AbstractListener listener) throws IllegalArgumentException {
    this.listenerMap.putIfAbsent(filter, listener);
  }

  @Override
  public void addEvent(Event event) throws InterruptedException {
    this.eventQueue.put(event);
  }

  @Override
  public void deregister(Listener listener) {
    for (Entry<Filter, AbstractListener> entry : this.listenerMap.entrySet())
      try {
        if (entry.getValue().equals(listener))
          this.listenerMap.remove(entry.getKey());
      } catch (NullPointerException ignore) {
      }
  }
}
