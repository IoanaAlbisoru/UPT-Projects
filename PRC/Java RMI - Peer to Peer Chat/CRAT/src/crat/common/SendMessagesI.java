package crat.common;

import java.rmi.Remote;
import java.rmi.RemoteException;

/**
 * The purpose of this interface is that Implementers make clear that third
 * parties can send message through these objects
 */
public interface SendMessagesI extends Remote {
  void sendMessage( String msg ) throws RemoteException;

  /**
   * It announces the receiver that the user who calls this method will no
   * longer send any messages
   * 
   * @throws RemoteException
   */
  void disconnect() throws RemoteException;
}
