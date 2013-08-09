package crat.common;

import java.rmi.Remote;
import java.rmi.RemoteException;

/**
 * Whenever a user makes available an object of this type he is practically
 * announcing that is receiving connections. The exact nature of the way that
 * connections requests are handled once they are sent are not specified by the
 * interface
 */
public interface SendConnectionRequestsI extends Remote {

  /**
   * @param myName
   *          the name of the person who wishes to connect
   * @param sendingEnd
   *          an object though which the user we wish to connect to can send us
   *          messages
   * @return null if our request was denied
   *         and object though which we can send messages to the user we just
   *         tried to connect to, if the request was accepted.
   * @throws RemoteException
   */
  public SendMessagesI connect( String myName, SendMessagesI sendingEnd )
      throws RemoteException;
}
