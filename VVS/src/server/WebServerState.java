package server;

public class WebServerState {
  public final String status;
  public final String currentPort;
  public final String serverAddress;

  public WebServerState(String state, String currentPort, String serverAdress) {
    this.status = state;
    this.currentPort = currentPort;
    this.serverAddress = serverAdress;
  }
}
