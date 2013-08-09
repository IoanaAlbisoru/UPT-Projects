package crat.client;

import java.rmi.RemoteException;
import java.util.concurrent.*;
import java.util.concurrent.atomic.AtomicBoolean;

import crat.common.*;

/**
 * This class abstract the idea of a 'user. It implies that there is a
 * connection between the local instance of this class and a remote user
 */
public class User {
  /**
   * This is the object though which we will expect to receive messages from the
   * other end
   */
  private ReceiveMessagesI received;
  /**
   * This is the object through which we expect to sent messages to the other
   * end
   */
  private SendMessagesI send;
  protected final UserGUI gui;

  /**
   * The name of the user we are connected to
   */
  private final String remoteName;
  /**
   * Our user name
   */
  private final String myName;

  private AtomicBoolean isDisconnected;

  public User( ReceiveMessagesI rec, SendMessagesI send, String remoteName,
      String myName ){
    this.received = rec;
    this.send = send;
    this.remoteName = remoteName;
    this.myName = myName;
    // this.remoteIP = address;
    this.isDisconnected = new AtomicBoolean( false );

    this.gui = this.createGUI();

    this.receiver = this.threadFactory.newThread( new Runnable() {
      @Override
      public void run() {
        User.this.runSender();
      }
    } );

    this.sender = this.threadFactory.newThread( new Runnable() {
      @Override
      public void run() {
        User.this.runReceiver();
      }
    } );
  }

  public void close() {
    try {
      this.send.disconnect();
    }
    catch (Exception ignore) {}
    this.receiver.interrupt();
    this.sender.interrupt();
    this.isDisconnected.set( true );

    if ( this.gui != null )
      this.gui.close();
  }

  private void closeWithoutNotification() {
    try {
      this.receiver.interrupt();
      this.sender.interrupt();
      this.isDisconnected.set( true );

      if ( this.gui != null )
        this.gui.close();
    }
    catch (Exception ignore) {}
  }

  public void start() {
    this.gui.start();
    this.receiver.setDaemon( true );
    this.sender.setDaemon( true );
    this.receiver.start();
    this.sender.start();

  }

  protected UserGUI createGUI() {
    return new WindowUserChat( this.myName,this.remoteName );
  }

  private ThreadFactory threadFactory = new ThreadFactory() {
    @Override
    public Thread newThread( Runnable r ) {
      return new Thread( r );
    }
  };

  private Thread receiver;
  private Thread sender;

  private void runSender() {
    while (true) {

      if ( Thread.currentThread().isInterrupted() )
        return;

      if ( this.gui == null )
        continue;

      // if the window isn't visible we simply step over;
      if ( !this.gui.isVisible() )
        continue;

      String temp = null;
      try {
        temp = this.gui.getUserInput();
      }
      catch (InterruptedException ignore) {
        if ( Thread.currentThread().isInterrupted() )
          return;
      }
      if ( temp != null )
        try {
          this.send.sendMessage( temp );
        }
        catch (RemoteException e) {
          UserDialog.showError(
              "Failed to send last Message; closing connection",
              "Error sending to " + this.remoteName );
          this.close();
        }
    }
  }

  private void runReceiver() {
    while (true) {

      if ( Thread.currentThread().isInterrupted() )
        return;

      String temp;

      try {
        // we check to see if the user is disconnected
        if ( this.received.isDisconnected() ) {
          UserDialog.showMessage( String.format(
              "User '%s' just disconnected.", User.this.remoteName ),
              "User Disconnected." );
          // we don't have to notify the user at the other end because he made
          // the request to disconnect so I reckon he has a memory span of more
          // than .01s
          this.closeWithoutNotification();
          return;
        }

        temp = this.received.getMessage();
        if ( temp != null ) {
          this.gui.show();
          this.gui.display( String.format( "%s: %s", this.remoteName, temp ) );
        }
      }
      catch (InterruptedException ignore) {
        if ( Thread.currentThread().isInterrupted() )
          return;
      }
      catch (Exception e) {
        UserDialog.showError( "Error at receiving message; closing",
            "Error receiving from" + this.remoteName );
        this.close();
      }
    }
  }

  public final String getRemoteName() {
    return this.remoteName;
  }

  /**
   * @return true if the user on the other end is disconnected.
   */
  public final boolean isDisconnected() {
    return this.isDisconnected.get();
  }

  /*
   * A user can be equal to a user OR to a String as well, since the only thing
   * that we compare is the name
   */
  @Override
  public boolean equals( Object arg0 ) {
    if ( arg0 instanceof User )
      return this.remoteName.equals( ((User) arg0).remoteName );

    if ( arg0 instanceof String )
      return this.remoteName.equals( arg0 );

    return false;
  }

  @Override
  public String toString() {
    return this.remoteName;
  }

  public void showGUI() {
    this.gui.show();
  }
}
