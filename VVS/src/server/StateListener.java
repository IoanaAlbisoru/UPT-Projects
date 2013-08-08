package server;

public interface StateListener {
  public void addUpdate(WebServerState toAdd);

  /**
   * @return the DTO containing the current web server state. This is a blocking
   *         method!!
   * @throws InterruptedException
   */
  public WebServerState getState() throws InterruptedException;
}
