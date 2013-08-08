package remoting;

public interface Registry {
  public boolean bind(String name, RemoteInterface toBind);

  public RemoteInterface lookup(String name);

  public boolean unbind(String name);
}
