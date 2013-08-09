package crat.server;

import java.io.*;
import java.net.InetAddress;
import java.net.UnknownHostException;
import java.rmi.AccessException;
import java.rmi.RemoteException;
import java.rmi.registry.LocateRegistry;
import java.rmi.registry.Registry;

import crat.common.ServerEventListener;
import crat.common.ServerInterface;

public class MainServer {

  /**
   * @param args
   */

  static Server server = null;
  static Thread updateThread = null;
  static Registry localRegistry = null;

  public static void main( String[] args ) {
    try {
      MainServer.localRegistry = LocateRegistry
          .createRegistry( ServerInterface.PORT );

      MainServer.server = new Server();

      MainServer.localRegistry.rebind( ServerInterface.REGISTRY_NAME,
          MainServer.server );

      MainServer.updateThread = new Thread( new Runnable() {
        @Override
        public void run() {
          periodicUpdateThread();
        }
      } );
      MainServer.updateThread.setDaemon( true );
      MainServer.updateThread.start();

      System.out.println( "------------------------------------" );
      System.out.println( "CRAT SERVER is now up and running" );
      System.out.println( "Users can now connect to the following:" );
      try {
        System.out.println( "Host name:   "
            + InetAddress.getLocalHost().getCanonicalHostName() );
        System.out.println( "IP:          "
            + InetAddress.getLocalHost().getHostAddress() );
      }
      catch (UnknownHostException e) {}
      System.out.println( "------------------------------------" );

      String userInput = "";
      final String QUIT_COMMAND = "quit";
      while (!userInput.equals( QUIT_COMMAND ))
        try {
          InputStreamReader isr = new InputStreamReader( System.in );
          BufferedReader br = new BufferedReader( isr );

          userInput = br.readLine();
        }
        catch (Exception ignore) {}
    }
    catch (AccessException e) {
      e.printStackTrace();
    }
    catch (RemoteException e) {
      e.printStackTrace();
    }
    catch (NullPointerException e) {
      e.printStackTrace();
    }
    finally {
      try {
        MainServer.updateThread.interrupt();
        MainServer.server.shutDown();
        MainServer.localRegistry.unbind( ServerInterface.REGISTRY_NAME );
        System.exit( 0 );
      }
      catch (Exception e) {
        System.exit( 0 );
      }
    }
  }

  private static void periodicUpdateThread() {
    while (true)
      try {
        if ( Thread.currentThread().isInterrupted() )
          return;

        Thread.sleep( ServerEventListener.UPDATE_PERIOD );
        MainServer.server.periodicUpdate();
      }
      catch (InterruptedException ignore) {
        return;
      }
  }
}
