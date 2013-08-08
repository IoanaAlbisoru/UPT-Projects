package blackboard;

import java.util.Collection;

public interface Inspector<T> {

  public T inspect(final Collection<T> data);

}
