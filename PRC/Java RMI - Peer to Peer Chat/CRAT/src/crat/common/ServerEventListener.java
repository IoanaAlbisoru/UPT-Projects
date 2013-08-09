package crat.common;

import java.rmi.Remote;
import java.rmi.RemoteException;
import java.util.Collection;

/**
 * The server notifies clients via this interface on the specified events.
 * The server notifies by "pushing" data.
 * THERE IS NO guarantee that servers will provide all the kinds of
 * notifications
 */
public interface ServerEventListener extends Remote {
  /**
   * The time in ms that the server send periodic updates with it's state at
   * that time
   */
  public static final int UPDATE_PERIOD = 2000;

  /**
   * @param newUser
   *          the user that connected to the server
   * @throws RemoteException
   */
  public void userConnected( String newUser ) throws RemoteException;

  /**
   * @param disconnectedUser
   *          the user that disconnected from the server
   * @throws RemoteException
   */
  public void userDisconnected( String disconnectedUser )
      throws RemoteException;

  /**
   * The server announces it's listeners that it's shutting down
   * 
   * @throws RemoteException
   */
  public void serverShutingDown() throws RemoteException;

  /**
   * Users should expect periodic updates on the state of the server.
   * 
   * @param users
   *          a collection with the users currently connected to the server
   * @throws RemoteException
   */
  public void periodicUpdate( Collection<String> users ) throws RemoteException;
}
