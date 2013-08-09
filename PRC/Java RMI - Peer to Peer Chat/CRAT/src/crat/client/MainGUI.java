package crat.client;

import crat.common.ServerEventListener;

public interface MainGUI {
  void start();

  String getConnectionRequest() throws InterruptedException;

  String getDisconnectRequest() throws InterruptedException;

  void addUser( String toAdd );

  void removeUser( String toRemove );

  void close();

  ServerEventListener getServerListener();
}
