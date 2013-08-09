package crat.client;

import java.lang.management.ManagementFactory;
import java.lang.management.RuntimeMXBean;
import java.rmi.NotBoundException;
import java.rmi.RemoteException;
import java.rmi.registry.LocateRegistry;
import java.rmi.registry.Registry;
import java.util.List;

import crat.common.ServerInterface;

public class MainClient {
  private static String name;

  public static void main( String[] args ) {

    ServerInterface server = null;
    String serverIp;
    UserDialog.quitDialog( "Welcome to CRAT, continue?", "CRAT Client" );

    // MainClient.PrintArgs();

    // we keep asking for user input until we succeed connecting to a server;
    for (;; ) {
      serverIp = UserDialog.showInputDialog( "Please enter server address",
          "CRAT get server", "127.0.0.1", null );

      // means that the cancel button was hit.
      if ( serverIp == null )
        System.exit( 0 );

      if ( serverIp.equals( "" ) )
        continue;

      try {
        // we check to see if the registry at the specified address is up and
        // running;
        Registry registry = LocateRegistry.getRegistry( serverIp,
            ServerInterface.PORT );

        // we get the Server object through which we will be communicating
        server = (ServerInterface) registry
            .lookup( ServerInterface.REGISTRY_NAME );
        break;

      }
      catch (RemoteException e) {
        // means that we could not find any registry at specified address.
        UserDialog.quitDialog( String.format(
            "Could not connect to Server @ %s; try again?", serverIp ), "CRAT" );
      }
      catch (NotBoundException e) {
        UserDialog.showError( "Server seems to have encountered an error.",
            "Server Lookup Error" );
        System.exit( 0 );
      }
    }

    ConnectionBridge connBridge = null;
    // we create the connection bridge that will be used by other users to
    // connect to us;
    for (;; ) {
      MainClient.name = UserDialog.getUserName();

      if ( MainClient.name == null )
        System.exit( 0 );

      if ( MainClient.name.equals( "" ) )
        continue;

      try {
        // this is the item that we register at the server!!
        connBridge = new ConnectionBridge();
      }
      catch (RemoteException e1) {
        // seriously, this should NEVER happen;
        UserDialog.userTooStupidDialog( "Creating Connection Bidge." );
        System.exit( 0 );
      }

      try {
        if ( !server.register( MainClient.name, connBridge ) ) {
          UserDialog.quitDialog( String.format(
              "Username '%s' already in use, try again?", MainClient.name ),
              "Name in use." );
          continue;
        };
        break;
      }
      catch (RemoteException e) {
        UserDialog.showError( "User too stupid exception",
            "Error at registering to server" );
        System.exit( 0 );
      }
      catch (NullPointerException nullPointer) {
        UserDialog.nullPointerDialog( "@Register User Name" );
      }
      catch (Exception whatTheHell) {
        UserDialog.userTooStupidDialog( whatTheHell.getClass().getSimpleName() );
      }

    }

    /*
     * We create the network object by gicing it a reference to the server and
     * to the object that we registered on the server, through which we expect
     * to receive connections
     */
    Network network = new Network( MainClient.name,server,connBridge );
    network.start();

  }

  @SuppressWarnings( "unused" )
  private static void PrintArgs() {
    try {
      RuntimeMXBean RuntimemxBean = ManagementFactory.getRuntimeMXBean();
      List<String> aList = RuntimemxBean.getInputArguments();
      String clsPath = RuntimemxBean.getClassPath();
      String arguments = "";

      for (String temp : aList )
        arguments += temp + '\n';
      System.out.print( "\n------------------------------\n" );
      System.out.println( "ClassPath: " );
      System.out.println( clsPath );
      System.out.print( "\n------------------------------\n" );
      System.out.println( "JVMArgs:" );
      System.out.println( arguments );
      System.out.print( "------------------------------\n" );
    }
    catch (Exception ignore) {}
  }

}
