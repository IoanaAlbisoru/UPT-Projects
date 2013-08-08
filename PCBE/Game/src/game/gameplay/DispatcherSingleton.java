package game.gameplay;

import game.dispatcher.ConcreteDispatcher;
import game.dispatcher.Dispatcher;
import game.gameplay.ResourceListener.ResourceListenerFilter;
import game.gameplay.TimeManager.TimeManagerFilter;

public class DispatcherSingleton {
  public static Dispatcher dispatcher;

  private static Thread dispThread;
  private static Thread timeManagerThread;
  private static Thread resourceManagerThread;

  public static void init() {
    ConcreteDispatcher temp = new ConcreteDispatcher();
    DispatcherSingleton.dispThread = new Thread(temp);
    DispatcherSingleton.dispatcher = temp;
    TimeManager timeM = new TimeManager();
    DispatcherSingleton.timeManagerThread = new Thread(timeM);
    TimeManagerFilter filter = timeM.new TimeManagerFilter();
    DispatcherSingleton.dispatcher.register(filter, timeM);
    ResourceListener res = new ResourceListener();
    DispatcherSingleton.resourceManagerThread = new Thread(res);
    ResourceListenerFilter filter2 = res.new ResourceListenerFilter();
    DispatcherSingleton.dispatcher.register(filter2, res);

    DispatcherSingleton.dispThread.start();
    DispatcherSingleton.timeManagerThread.start();
    DispatcherSingleton.resourceManagerThread.start();
  }
}
