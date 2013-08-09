package crat.client;

import java.rmi.RemoteException;
import java.rmi.server.UnicastRemoteObject;
import java.util.concurrent.ArrayBlockingQueue;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.TimeUnit;

import crat.common.*;

/**
 * Objects of this type are used to communicate the intent to connect to the
 * user that owns it
 * This type of object is the one registered on the server. And all users that
 * wish to connect to us wil request it from the server
 */
public class ConnectionBridge extends UnicastRemoteObject implements
    SendConnectionRequestsI, ReceiveConnectionRequestsI {

  private static final long serialVersionUID = 3784724496343845147L;
  private BlockingQueue<PackagedData> requestQueue;

  public ConnectionBridge() throws RemoteException{
    this.requestQueue = new ArrayBlockingQueue<PackagedData>( 20 );
  }

  @Override
  public SendMessagesI connect( String myName, SendMessagesI sendingEnd )
      throws RemoteException {

    boolean accept = UserDialog.connectionRequest( myName );
    if ( !accept )
      return null;

    Messenger messageBridge = new Messenger();
    this.requestQueue.add( new PackagedData( myName,messageBridge,sendingEnd ) );
    return messageBridge;
  }

  @Override
  public PackagedData getConnectionRequest() throws InterruptedException {
    return this.requestQueue.poll( 1, TimeUnit.SECONDS );
  }

}
