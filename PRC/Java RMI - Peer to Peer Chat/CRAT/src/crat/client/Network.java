package crat.client;

import java.net.UnknownHostException;
import java.rmi.AccessException;
import java.rmi.RemoteException;
import java.rmi.registry.LocateRegistry;
import java.rmi.registry.Registry;
import java.util.concurrent.CopyOnWriteArraySet;
import java.util.concurrent.atomic.AtomicBoolean;

import crat.common.*;

/**
 * This class handles all the connections: to the server and to other users
 */
public class Network {
  private final String myName;
  private final MainGUI mainWindow;
  private ServerInterface server;
  private final CopyOnWriteArraySet<User> users;

  private final ConnectionManager connectionManager;

  public Network( String myName, ServerInterface server,
      ReceiveConnectionRequestsI connections ){
    this.myName = myName;
    this.server = server;
    this.users = new CopyOnWriteArraySet<User>();
    this.mainWindow = WindowMain.createControlGUI( this.myName, this );
    this.connectionManager = new ConnectionManager( myName,this.mainWindow,
        connections );
  }

  public void start() {
    try {
      this.mainWindow.start();
      this.connectionManager.start();

      this.server.registerListener( this.myName,
          this.mainWindow.getServerListener() );
    }
    catch (RemoteException e) {
      UserDialog
          .showError(
              "Cannot register ServerEventListener, the Available Users List will not be updated",
              "Register Listener" );
    }
    catch (Exception e) {
      UserDialog.showError( "Unable to start application", "Network.start()" );
    }
  }

  /**
   * @param userName
   */
  public void showUserGUI( String userName ) {
    for (User user : this.users )
      try {
        if ( user.getRemoteName().equals( userName ) ) {
          user.showGUI();
        }
      }
      catch (Exception whatTheHell) {
        UserDialog.userTooStupidDialog( whatTheHell.getMessage() );
      }
  }

  private final AtomicBoolean hasBeenClosed = new AtomicBoolean( false );

  public void close() {
    if ( hasBeenClosed.compareAndSet( false, true ) ) {
      this.connectionManager.close();
      try {
        server.disconnect( this.myName );
      }
      catch (Exception ignore) {}
    }
  }

  /**
   * This class handles all user connection related issues
   */
  private class ConnectionManager {

    /**
     * thread in which all connections requests from other users are handled
     */
    private Thread requestHandler;

    /**
     * The thread in which all connections demanded by the user are established
     */
    private Thread connectionSendThread;

    /**
     * This thread handles the disconnect requests from the user AND removes all
     * users that have disconnected on their own(e.g. by closing the application
     * and calling the method "disconnect" on the {@link SendMessagesI} )
     */
    private Thread disconnectHandler;
    private String myName;

    private MainGUI mainGUI;

    /**
     * The object through which we expect to receive connections, this is also
     * the object that we register at the server
     */
    private ReceiveConnectionRequestsI connections;

    public ConnectionManager( String myName, MainGUI control,
        ReceiveConnectionRequestsI connRecv ){
      this.myName = myName;
      this.mainGUI = control;
      this.connections = connRecv;
      Network.this.server = server;
      this.requestHandler = new Thread( new Runnable() {
        @Override
        public void run() {
          ConnectionManager.this.runInboundConnectionsThread();
        }
      } );

      this.disconnectHandler = new Thread( new Runnable() {
        @Override
        public void run() {
          ConnectionManager.this.runDisconnectThread();
        }
      } );

      this.connectionSendThread = new Thread( new Runnable() {
        @Override
        public void run() {
          ConnectionManager.this.runOutboundConnectionsThread();
        }
      } );
    }

    public void start() {
      this.requestHandler.setDaemon( true );
      this.connectionSendThread.setDaemon( true );
      this.disconnectHandler.setDaemon( true );

      this.connectionSendThread.start();
      this.requestHandler.start();
      this.disconnectHandler.start();
    }

    public void close() {
      this.requestHandler.interrupt();
      this.connectionSendThread.interrupt();
      this.disconnectHandler.interrupt();

      for (User user : Network.this.users ) {
        try {
          user.close();
        }
        catch (Exception ignore) {}
      }
    }

    private void runDisconnectThread() {
      while (true) {
        if ( Thread.currentThread().isInterrupted() )
          return;

        String toDisconnect = null;
        try {
          // we get the user we want to disconnect from, or wait until there is
          // one
          toDisconnect = Network.this.mainWindow.getDisconnectRequest();
        }
        catch (InterruptedException ignore) {
          // we put this here so we interrupt this thread as soon as possible.
          if ( Thread.currentThread().isInterrupted() )
            return;
        }
        if ( toDisconnect != null ) {
          try {
            for (User user : Network.this.users ) {
              if ( user.getRemoteName().equals( toDisconnect ) ) {
                user.close();
                Network.this.users.remove( toDisconnect );
                break;
              }
            }
          }
          catch (Exception whatTheHell) {
            UserDialog.userTooStupidDialog( "Disconnect handler" );
          }
        }

        // now we check if any user has disconnected;
        try {
          for (User user : Network.this.users ) {
            if ( user.isDisconnected() ) {
              // DO NOT use user.close() it is not necessary!
              Network.this.mainWindow.removeUser( user.getRemoteName() );
              Network.this.users.remove( user );
              break;
            }
          }
        }
        catch (Exception whatTheHell) {
          UserDialog.userTooStupidDialog( "Disconnect handler" );
        }
      }
    }

    private void runOutboundConnectionsThread() {
      while (true) {
        if ( Thread.currentThread().isInterrupted() )
          return;

        String remoteName = null;
        try {
          remoteName = Network.this.mainWindow.getConnectionRequest();
        }
        catch (InterruptedException ignore) {
          if ( Thread.currentThread().isInterrupted() )
            return;
        }
        if ( remoteName != null )
          try {
            // we check to see if we got a request to connect to someone we're
            // already connected to
            if ( Network.this.users.contains( remoteName ) ) {
              UserDialog.showError(
                  String.format( "Already connected to '%s'.", remoteName ),
                  "Already Connected: Network" );
              continue;
            }

            SendConnectionRequestsI connect = Network.this.server
                .getUserConnectionI( remoteName );

            if ( connect == null )
              throw new UnknownHostException( remoteName );

            Messenger bridge = new Messenger();
            SendMessagesI sendingEnd = connect.connect( this.myName, bridge );

            if ( sendingEnd == null ) {
              UserDialog
                  .showMessage( String.format(
                      "%s has declined your connection", remoteName ),
                      "Connection Decline" );
              continue;
            }

            User newUser = new User( bridge,sendingEnd,remoteName,this.myName );
            // true if the user wasn't present
            if ( Network.this.users.add( newUser ) ) {
              Network.this.mainWindow.addUser( remoteName );
              newUser.start();
            } else {
              UserDialog.showError(
                  String.format( "User '%s' already connected" ),
                  "Outbound Connections: Network" );
            }

          }
          catch (AccessException e) {
            UserDialog.showError( e.getMessage(), "Stupid security clearence" );
          }
          catch (RemoteException e) {
            UserDialog.showError( e.getMessage(),
                "Remote Exception @ Connecting" );
          }
          catch (UnknownHostException e) {
            UserDialog.showError(
                String.format( "Unkown user: '%s'", e.getMessage() ),
                "Unkown User Name" );
          }
          catch (NullPointerException nullPointer) {
            UserDialog.nullPointerDialog( "@Connecting" );
          }
          catch (Exception e) {
            UserDialog.userTooStupidDialog( "@Connecting" );
          }
      }
    }

    private void runInboundConnectionsThread() {
      while (true) {

        if ( Thread.currentThread().isInterrupted() )
          return;

        ReceiveConnectionRequestsI.PackagedData userData;
        try {
          userData = this.connections.getConnectionRequest();
          if ( userData != null ) {
            User newUser = new User( userData.receivingEndI,
                userData.sendingEnd,userData.name,this.myName );

            if ( Network.this.users.add( newUser ) ) {
              this.mainGUI.addUser( userData.name );
              newUser.start();
            }
          }
        }
        catch (InterruptedException itr) {
          if ( Thread.currentThread().isInterrupted() )
            return;
        }
        catch (Exception e) {
          UserDialog.showError( e.getMessage(),
              "Couldn't Process Connection Request" );
        }

      }
    }

  }

  public void disconnectFromServer() throws RemoteException {
    this.server.disconnect( this.myName );
    this.server = null;
    UserDialog.showMessage( "You are no longer connected to the server.",
        "Disconnect from Server" );
  }

  /**
   * @param serverAddress
   *          the server of the server we wish to connect to
   * @return true if the connection was successful
   *         false otherwise
   */
  public boolean reconnectToServer( String serverAddress ) {
    try {
      if ( serverAddress == null || serverAddress.equals( "" ) )
        return false;

      Registry registry = LocateRegistry.getRegistry( serverAddress,
          ServerInterface.PORT );

      // we get the Server object through which we will be communicating
      this.server = (ServerInterface) registry
          .lookup( ServerInterface.REGISTRY_NAME );

      this.server.register( this.myName,
          (ConnectionBridge) this.connectionManager.connections );

      this.server.registerListener( this.myName,
          this.mainWindow.getServerListener() );

      return true;
    }
    catch (Exception e) {
      return false;
    }
  }
}
