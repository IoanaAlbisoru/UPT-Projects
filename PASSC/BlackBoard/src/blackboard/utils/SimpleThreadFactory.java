package blackboard.utils;

import java.util.concurrent.ThreadFactory;

public class SimpleThreadFactory implements ThreadFactory {

  @Override
  public Thread newThread(final Runnable run) {
    return new Thread(run);
  }

}
