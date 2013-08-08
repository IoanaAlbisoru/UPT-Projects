package blackboard;

public interface BlackBoard<T> {
  void update(T newInfo);

  T inspect(Inspector<T> knowledge);
}
