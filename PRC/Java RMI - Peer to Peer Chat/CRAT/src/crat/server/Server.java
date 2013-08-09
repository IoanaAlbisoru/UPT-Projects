package crat.server;

import java.rmi.RemoteException;
import java.rmi.server.UnicastRemoteObject;
import java.util.concurrent.ConcurrentHashMap;
import java.util.*;

import crat.common.SendConnectionRequestsI;
import crat.common.ServerEventListener;
import crat.common.ServerInterface;

public class Server extends UnicastRemoteObject implements ServerInterface {

  private static final long serialVersionUID = -1180294910450965731L;

  public Server() throws RemoteException{
    super();
  }

  private ConcurrentHashMap<String, SendConnectionRequestsI> users = new ConcurrentHashMap<String, SendConnectionRequestsI>();
  private ConcurrentHashMap<String, ServerEventListener> eventListeners = new ConcurrentHashMap<String, ServerEventListener>();

  @Override
  public synchronized boolean register( String name,
      SendConnectionRequestsI connBridge ) throws RemoteException {

    if ( this.users.contains( name ) )
      return false;

    if ( null == this.users.putIfAbsent( name, connBridge ) ) {
      System.out.println( String.format( "'%s' just connected.", name ) );

      for (ServerEventListener listener : this.eventListeners.values() )
        listener.userConnected( name );
      return true;
    };
    return false;
  }

  @Override
  public SendConnectionRequestsI getUserConnectionI( String name )
      throws RemoteException {
    return this.users.get( name );
  }

  @Override
  public void disconnect( String name ) throws RemoteException {
    this.users.remove( name );
    this.eventListeners.remove( name );

    for (ServerEventListener listener : this.eventListeners.values() )
      listener.userDisconnected( name );

    System.out.println( String.format( "'%s' just disconnected.", name ) );
  }

  @Override
  public void registerListener( String from, ServerEventListener listener )
      throws RemoteException {
    try {
      if ( !this.users.containsKey( from ) )
        return;

      this.eventListeners.putIfAbsent( from, listener );

      // listener.periodicUpdate( this.getSerializableCollection() );
    }
    catch (Exception e) {
      throw new RemoteException();
    }
  }

  @Override
  public void removeListener( String from ) throws RemoteException {
    try {
      this.eventListeners.remove( from );
    }
    catch (Exception e) {
      throw new RemoteException();
    }
  }

  public void periodicUpdate() {

    Collection<String> temp = this.getSerializableCollection();
    if ( temp == null )
      return;

    for (ServerEventListener listener : this.eventListeners.values() )
      try {
        listener.periodicUpdate( temp );
      }
      catch (Exception ignore) {}
  }

  public void shutDown() {
    for (ServerEventListener listener : this.eventListeners.values() )
      try {
        listener.serverShutingDown();
      }
      catch (Exception ignore) {}
  }

  /**
   * The intended usage of this method is to generate a serialized version of
   * the user collection with the intent of sending it through the network
   * 
   * @return a collection containing all the user names. This method MUST always
   *         return a collection that is serializable
   */
  private Collection<String> getSerializableCollection() {
    try {

      Set<String> set = this.users.keySet();
      if ( set.isEmpty() )
        return null;

      Collection<String> temp = new ArrayList<String>();
      for (String user : set )
        temp.add( user );
      if ( temp.isEmpty() )
        return null;

      return temp;
    }
    catch (Exception e) {}
    return null;
  }

}
