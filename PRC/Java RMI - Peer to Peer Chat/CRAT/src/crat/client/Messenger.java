package crat.client;

import java.rmi.RemoteException;
import java.rmi.server.UnicastRemoteObject;
import java.util.concurrent.*;
import java.util.concurrent.atomic.AtomicBoolean;

import crat.common.SendMessagesI;

/**
 * This class is used to as a messenger between two users. One user puts
 * messages in the Queue the other user retrieves the messages from the Queue.
 *
 */
public class Messenger extends UnicastRemoteObject implements ReceiveMessagesI,
    SendMessagesI {
  private final int MAX_CAPACITY = 20;
  private final AtomicBoolean isDisconnected;
  private final BlockingQueue<String> messages = new ArrayBlockingQueue<String>(
      this.MAX_CAPACITY );

  public Messenger() throws RemoteException{
    super();
    this.isDisconnected = new AtomicBoolean( false );
  }

  private static final long serialVersionUID = -1939153506512668246L;

  @Override
  public void sendMessage( String msg ) throws RemoteException {
    this.messages.add( msg );
  }

  @Override
  public String getMessage() throws InterruptedException {
    return this.messages.poll( 1, TimeUnit.SECONDS );
  }

  @Override
  public void disconnect() throws RemoteException {
    this.isDisconnected.set( true );
  }

  @Override
  public boolean isDisconnected() {
    return this.isDisconnected.get();
  }

}
