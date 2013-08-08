package gui;

import static org.mockito.Mockito.*;

import java.io.IOException;
import java.util.concurrent.atomic.AtomicInteger;

import gui.GUIWebServer.ServerListener;

import org.mockito.invocation.InvocationOnMock;
import org.mockito.stubbing.Answer;

import server.StateListener;
import server.WebServer;
import server.WebServerState;

public class MarathonTestGUI {
  private static final String VALID_M = "validM";
  private static final String VALID_R = "validR";

  // 0xDEAD = 57005
  private static final int INVALID_PORT = 0xDEAD;

  private static ServerListener serverListener;

  public static void main(String[] args) {
    final WebServer server = mock(WebServer.class);

    // these 3 methods are called only once @ the gui building so we can easily
    // return only these strings;
    when(server.getMaintanenceFolder()).thenReturn("maintenance");
    when(server.getRootFolder()).thenReturn("root");
    when(server.getPort()).thenReturn("10023");

    // we simply ensure that there is only one valid folder for root and
    // maintenance. It's easy this way;
    when(server.isValidMaintenanceFolder(anyString())).thenAnswer(new Answer<Boolean>() {
      @Override
      public Boolean answer(InvocationOnMock invocation) throws Throwable {
        String argument = (String) invocation.getArguments()[0];
        if (argument.equals(MarathonTestGUI.VALID_M))
          return true;
        else
          return false;
      }
    });

    when(server.isValidRootFolder(anyString())).thenAnswer(new Answer<Boolean>() {
      @Override
      public Boolean answer(InvocationOnMock invocation) throws Throwable {
        String argument = (String) invocation.getArguments()[0];
        if (argument.equals(MarathonTestGUI.VALID_R))
          return true;
        else
          return false;
      }
    });
    /*
     * here I intercept the GUI's attempt to register a listener for server
     * updates. By doing so I will be able to periodically send the GUI whatever
     * updates I want
     */
    doAnswer(new Answer<Void>() {
      @Override
      public Void answer(InvocationOnMock invocation) throws Throwable {
        MarathonTestGUI.serverListener = (ServerListener) invocation.getArguments()[0];
        return null;
      }
    }).when(server).addListener((StateListener) anyObject());
    GUIWebServer gui = new GUIWebServer(server);

    try {
      /*
       * when we call server.start(INVALID_PORT) then we will mimic the
       * behaviour of not being able to create the ServerSocket. Otherwise we
       * will simply update the WebServer's state like everything went smoothly
       */
      final AtomicInteger lastUsedPort = new AtomicInteger(0);
      doAnswer(new Answer<WebServer>() {
        @Override
        public WebServer answer(InvocationOnMock invocation) throws Throwable {
          Integer port = (Integer) invocation.getArguments()[0];
          if (port == MarathonTestGUI.INVALID_PORT)
            throw new IOException("Mock failure. Placeholder for when the ServerSocket fails");
          else {
            lastUsedPort.set(port);
            MarathonTestGUI.serverListener.addUpdate(new WebServerState("running", port.toString(), "127.0.0.1"));
          }
          return server;
        }
      }).when(server).start(anyInt());

      doAnswer(new Answer<Void>() {
        @Override
        public Void answer(InvocationOnMock invocation) throws Throwable {
          MarathonTestGUI.serverListener.addUpdate(new WebServerState("running", "" + lastUsedPort.get(), "127.0.0.1"));
          return null;
        }
      }).when(server).restart();

      doAnswer(new Answer<Void>() {
        @Override
        public Void answer(InvocationOnMock invocation) throws Throwable {
          MarathonTestGUI.serverListener.addUpdate(new WebServerState("maintenance", "" + lastUsedPort.get(), "127.0.0.1"));
          return null;
        }
      }).when(server).enterMaintenance();

      /*
       * First two times we call server.stop() it will work properly. After the
       * 3rd time it will keep failing to stop the ServerSocket
       */
      final AtomicInteger timesStopHasBeenCalled = new AtomicInteger();
      doAnswer(new Answer<Void>() {
        @Override
        public Void answer(InvocationOnMock invocation) throws Throwable {
          MarathonTestGUI.serverListener.addUpdate(new WebServerState("not running", "not running", "not running"));
          if (timesStopHasBeenCalled.getAndIncrement() > 1)
            throw new IOException("Could not close ServerSocket.");
          return null;
        }
      }).when(server).stop();
    } catch (IOException e1) {
    }

    gui.start();
    MarathonTestGUI.serverListener.addUpdate(new WebServerState("not running", "not running", "not running"));
    try {
      gui.awaitTermination();
    } catch (Exception e) {
    }
  }
}
