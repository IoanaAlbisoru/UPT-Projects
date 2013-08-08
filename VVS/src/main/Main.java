package main;

import gui.GUIWebServer;

import javax.net.ServerSocketFactory;
import server.WebServer;
import server.SocketIOWrapper.SocketIOWrapperFactory;

public class Main {

  /**
   * @param args
   */
  public static void main(String[] args) {
    String serverName = "Deep Thought";
    String configFile = "config";
    ServerSocketFactory serverSockets = ServerSocketFactory.getDefault();
    SocketIOWrapperFactory socketWrappers = SocketIOWrapperFactory.getDefault();

    WebServer server = WebServer.createWebServer(serverName, configFile, serverSockets, socketWrappers);
    GUIWebServer gui = new GUIWebServer(server);
    gui.start();
    try {
      gui.awaitTermination();
    } catch (Exception e) {
    }
  }
}
