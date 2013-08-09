package crat.common;

import java.rmi.Remote;
import java.rmi.RemoteException;

/**
 * This is the interface of the server. It should be implemented only by servers
 * that are fairly light weight
 */
public interface ServerInterface extends Remote {
  /**
   * the port on which users should expect to find objects of this type
   */
  public static final int PORT = 2307;
  /**
   * the name with which objects of this type are registered
   */
  public static final String REGISTRY_NAME = "CRAT_SERVER";

  /**
   * Users register to a server, by doing this they are announcing their
   * willingness to communicate with other users that register with this same
   * server
   * 
   * @param name
   *          the name under which a client wants to register. This name must be
   *          unique and is the only way the server identifies users that
   *          register
   * @param connBridge
   *          the {@link SendConnectionRequestsI} through which users will be
   *          receiving connections
   * @return true if the registration was successful
   *         false if the name was already registered
   * @throws RemoteException
   *           is thrown when there is an error of unspecified nature on the
   *           side of the server
   */
  public boolean register( String name, SendConnectionRequestsI connBridge )
      throws RemoteException;

  /**
   * This is the only way one can obtain an interface through which to connect
   * to another client
   * 
   * @param name
   *          the name of the user we want to obtain an interface through which
   *          we can connect
   * @return the {@link SendConnectionRequestsI} through which you can connect
   *         to the specified user
   *         null if there is no such user
   * @throws RemoteException
   *           is thrown when there is an error of unspecified nature on the
   *           side of the server
   */
  public SendConnectionRequestsI getUserConnectionI( String name )
      throws RemoteException;

  /**
   * This method is called by the client to announce the server that he is
   * disconnecting.
   * 
   * @param name
   *          is the name of the client that calls this method
   * @throws RemoteException
   */
  public void disconnect( String name ) throws RemoteException;

  /**
   * A client can register a {@link ServerEventListener} that gets notified
   * appropriately on server events.
   * 
   * @param from
   *          the user that this {@link ServerEventListener} belongs to. If
   *          there is no such user the listener won't be registered
   * @param listener
   *          the {@link SendConnectionRequestsI} through which the client will
   *          be notified on server changes
   * @throws RemoteException
   */
  public void registerListener( String from, ServerEventListener listener )
      throws RemoteException;

  /**
   * @param from
   *          removes a previously registered {@link ServerEventListener}. The
   *          method does nothing if there isn't a client registered under the
   *          name of 'from'
   * @throws RemoteException
   */
  public void removeListener( String from ) throws RemoteException;

}
