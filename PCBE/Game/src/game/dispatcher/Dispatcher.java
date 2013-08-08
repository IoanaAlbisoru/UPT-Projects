package game.dispatcher;

public interface Dispatcher {

  public void register(Filter filter, AbstractListener listener) throws IllegalArgumentException;

  public void addEvent(Event event) throws InterruptedException;

  public void deregister(Listener listener);

}
