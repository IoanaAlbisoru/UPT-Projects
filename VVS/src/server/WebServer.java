package server;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.io.PrintWriter;
import java.net.InetAddress;
import java.net.ServerSocket;
import java.net.Socket;
import java.net.SocketException;
import java.net.SocketTimeoutException;
import java.net.UnknownHostException;
import java.util.HashSet;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.RejectedExecutionException;
import java.util.concurrent.atomic.AtomicReference;

import javax.net.ServerSocketFactory;

import server.SocketIOWrapper.SocketIOWrapperFactory;

public class WebServer {

  static class Config {
    private static final String DEFAULT_FOLDER_ROOT = "SiteFolder";
    private static final String DEFAULT_MAINTENANCE_FOLDER = "Maintenance";
    private static final int DEFAULT_PORT = 10023;

    private String rootFolder = Config.DEFAULT_FOLDER_ROOT;
    private String maintenanceFolder = Config.DEFAULT_MAINTENANCE_FOLDER;
    private int portNumber = Config.DEFAULT_PORT;

    private final Config loadConfigurations(String configFile) {
      // has the defaults;
      Config config = new Config();
      File file = new File(configFile);
      // File currentFolder = new File(".");

      if (!file.exists() || file.isDirectory())
        return config;

      try {
        FileReader tempFileReader = null;
        tempFileReader = new FileReader(file);

        BufferedReader in;
        try {
          in = new BufferedReader(tempFileReader);
        } catch (Exception e) {
          if (tempFileReader != null)
            tempFileReader.close();
          return config;
        }

        try {
          String rootFolder = in.readLine();
          File temp = new File(rootFolder);

          if (temp.exists() && temp.isDirectory())
            rootFolder = temp.getAbsolutePath();

          this.rootFolder = rootFolder;
        } catch (Exception ignore) {
        }
        String maintenanceFolder;
        try {
          maintenanceFolder = in.readLine();
          File temp = new File(maintenanceFolder);
          if (temp.exists() && temp.isDirectory())
            maintenanceFolder = temp.getAbsolutePath();

          this.maintenanceFolder = maintenanceFolder;
        } catch (Exception ignore) {
        }
        try {
          int portNumber = Integer.parseInt(in.readLine());
          this.portNumber = portNumber;
        } catch (Exception ignore) {
        }
        return config;
      } catch (Exception e) {
        return config;
      }
    }

    private static boolean writeConfigurations(String configFile, Config configs) {
      File file = new File(configFile);
      PrintWriter out = null;
      try {
        out = new PrintWriter(new FileWriter(file, false));
        out.println(configs.rootFolder);
        out.println(configs.maintenanceFolder);
        out.println(configs.portNumber);
        out.close();
      } catch (Exception e) {
        if (out != null)
          out.close();
        return false;
      }
      return true;
    }
  }

  private static final String NOT_FOUND_PAGE = "NotFound.html";
  private static final String MAINTENANCE_PAGE = "Maintenance.html";
  private static final String INDEX_PAGE = "index.html";
  private static final int THREAD_POOL_SIZE = 42;
  private static final int SERVERSOCKET_TIMEOUT = 5000;

  private final ExecutorService clientRequestExecutor;
  private final ServerSocketFactory serverSocketFactory;
  private final SocketIOWrapperFactory socketWrapperFactory;

  // the Server socket is always created during the start method, and NO WHERE
  // ELSE!
  private AtomicReference<ServerStates> currentState = new AtomicReference<ServerStates>();
  private volatile ServerSocket serverSocket;
  private volatile int port;
  private final String serverName;
  private volatile String rootFolder;
  private volatile String maintanenceFolder;

  private final HashSet<StateListener> listeners = new HashSet<>();
  private final String configFile;

  private WebServer(String serverName, String rootFolder, String maintenanceFolder, int portNumber, String configFile, ServerSocketFactory serverSocketFactory,
      SocketIOWrapperFactory socketWrapperFactory) {
    this.serverName = serverName;
    this.rootFolder = rootFolder;
    this.maintanenceFolder = maintenanceFolder;
    this.port = portNumber;
    this.configFile = configFile;
    this.serverSocketFactory = serverSocketFactory;
    this.socketWrapperFactory = socketWrapperFactory;
    this.currentState.set(ServerStates.STOPPED);
    this.clientRequestExecutor = Executors.newFixedThreadPool(WebServer.THREAD_POOL_SIZE);
  }

  public final static WebServer createWebServer(String serverName, String configFile, ServerSocketFactory serverSocketFactory,
      SocketIOWrapperFactory socketWrapperFactory) {
    Config config = new Config();
    config.loadConfigurations(configFile);
    WebServer temp = new WebServer(serverName, config.rootFolder, config.maintenanceFolder, config.portNumber, configFile, serverSocketFactory,
        socketWrapperFactory);
    return temp;
  }

  private final void replaceServerSocket(int portNumber) throws IOException {
    if (this.serverSocket != null)
      this.serverSocket.close();
    this.serverSocket = this.serverSocketFactory.createServerSocket(portNumber);
    this.serverSocket.setSoTimeout(WebServer.SERVERSOCKET_TIMEOUT);
  }

  private Thread mainThread = null;

  private class WebServerMainThread implements Runnable {
    @Override
    public void run() {

      try {
        while (!Thread.currentThread().isInterrupted())
          try {
            Socket tempSocket = WebServer.this.serverSocket.accept();
            final SocketIOWrapper clientSocket = WebServer.this.socketWrapperFactory.createSocketIOWrapper(tempSocket);
            ClientRequestHandler clientRequestHandler = new ClientRequestHandler(WebServer.this.currentState, clientSocket, WebServer.this.getRootFolder(),
                WebServer.this.maintanenceFolder, WebServer.this.serverName, WebServer.NOT_FOUND_PAGE, WebServer.MAINTENANCE_PAGE, WebServer.INDEX_PAGE);
            WebServer.this.clientRequestExecutor.execute(clientRequestHandler);
          } catch (SocketTimeoutException timeout) {
            // in the case of timeout we just fall through so the while can do
            // the check to see if it was interrupted
          } catch (SocketException socketException) {
            // this is thrown in the case that we're blocked in accept and
            // serverSocket is closed(); we don't have to do anything here
          } catch (IOException e) {
            // TODO log error;
            break;
          } catch (RejectedExecutionException ignore) {
            // after shutdown has been initiated there really isn't much to be
            // done here.
          }
      } finally {
        try {
          if (WebServer.this.serverSocket != null)
            WebServer.this.serverSocket.close();
        } catch (IOException e) {
          // TODO log error;
        }
      }
    }
  };

  public void start(int newPortNumber) throws IOException {
    if (this.currentState.compareAndSet(ServerStates.RUNNING, ServerStates.RUNNING))
      return;

    this.replaceServerSocket(newPortNumber);
    this.port = newPortNumber;
    if (this.currentState.compareAndSet(ServerStates.MAINTENANCE, ServerStates.MAINTENANCE)) {
      if (this.mainThread == null) {
        this.mainThread = new Thread(new WebServerMainThread());
        this.mainThread.start();
      }
    } else {
      if (this.mainThread != null)
        this.mainThread.interrupt();
      this.mainThread = new Thread(new WebServerMainThread());
      this.mainThread.start();
    }
    this.currentState.set(ServerStates.RUNNING);
    this.updateListeners();
  }

  /**
   * This method will throw an IllegalStateException if called while the state
   * of the server is not MAINTENANCE!
   * 
   * @throws IllegalStateException
   */
  public void restart() throws IllegalStateException {
    if (!this.currentState.compareAndSet(ServerStates.MAINTENANCE, ServerStates.MAINTENANCE))
      throw new IllegalStateException("You can restart the server only from MAINTENANCE");

    if (this.currentState.compareAndSet(ServerStates.MAINTENANCE, ServerStates.MAINTENANCE)) {
      if (this.mainThread == null) {
        this.mainThread = new Thread(new WebServerMainThread());
        this.mainThread.start();
      }
    } else {
      if (this.mainThread != null)
        this.mainThread.interrupt();
      this.mainThread = new Thread(new WebServerMainThread());
      this.mainThread.start();
    }
    this.currentState.set(ServerStates.RUNNING);
    this.updateListeners();
  }

  public void enterMaintenance() {
    if (this.currentState.compareAndSet(ServerStates.MAINTENANCE, ServerStates.MAINTENANCE))
      return;
    this.currentState.set(ServerStates.MAINTENANCE);
  }

  /**
   * @throws IOException
   *           the server is effectively stopped, and the port that the previous
   *           socket was opened on will most likely still be in use, so in the
   *           case this error was used most probably we will have to restart
   *           the server with a different port. Due to the nature of how the OS
   *           handles sockets this is rather non-deterministic to test.
   * 
   */
  public void stop() throws IOException {
    if (this.currentState.compareAndSet(ServerStates.STOPPED, ServerStates.STOPPED))
      return;

    this.currentState.set(ServerStates.STOPPED);
    if (this.mainThread != null)
      this.mainThread.interrupt();

    if (this.serverSocket != null) {
      this.updateListeners();
      // I do this trick so that the field this.serverSocket it set to null even
      // though the socket failed to close()
      ServerSocket tempSS = this.serverSocket;
      this.serverSocket = null;
      tempSS.close();
    }
  }

  public void terminate() throws Exception {
    this.listeners.clear();
    this.clientRequestExecutor.shutdown();
    if (this.mainThread != null)
      this.mainThread.interrupt();

    try {
      if (this.serverSocket != null)
        this.serverSocket.close();
    } catch (Exception ignore) {
    }
    this.writeConfigFile();
  }

  private void writeConfigFile() throws Exception {
    Config temp = new Config();
    temp.rootFolder = this.rootFolder;
    temp.maintenanceFolder = this.maintanenceFolder;
    temp.portNumber = this.port;
    if (!Config.writeConfigurations(this.configFile, temp))
      throw new Exception("Could not save configurations");
  }

  public void addListener(StateListener stateListener) {
    this.listeners.add(stateListener);
    this.updateListeners();
  }

  private void updateListeners() {
    String address = "";
    try {
      InetAddress addr = InetAddress.getLocalHost();
      address = addr.getHostAddress();
    } catch (UnknownHostException e) {
    }
    WebServerState state;
    if (this.currentState.compareAndSet(ServerStates.STOPPED, ServerStates.STOPPED))
      state = new WebServerState(this.currentState.toString(), "not running", "not running");
    else
      state = new WebServerState(this.currentState.toString(), "" + this.port, address);
    for (StateListener listener : this.listeners)
      listener.addUpdate(state);
  }

  public String getServerName() {
    return this.serverName;
  }

  public String getRootFolder() {
    return this.rootFolder;
  }

  public void setRootFolder(String rootFolder) {
    this.rootFolder = rootFolder;
  }

  public String getMaintanenceFolder() {
    return this.maintanenceFolder;
  }

  public void setMaintanenceFolder(String maintanenceFolder) {
    this.maintanenceFolder = maintanenceFolder;
  }

  public boolean isValidRootFolder(String rootFolder) {
    return this.verifyFolder(rootFolder, WebServer.INDEX_PAGE);
  }

  public boolean isValidMaintenanceFolder(String maintanenceFolder) {
    return this.verifyFolder(maintanenceFolder, WebServer.MAINTENANCE_PAGE);
  }

  private boolean verifyFolder(String rootFolder, String fileName) {
    try {
      File folder = new File(rootFolder);
      if (folder.exists() && folder.isDirectory()) {
        File indexFile = new File(folder.getAbsolutePath() + "/" + fileName);
        boolean exists = indexFile.exists();
        boolean isFile = indexFile.isFile();
        return exists && isFile;
      }
      return false;
    } catch (Exception e) {
      return false;
    }
  }

  public String getPort() {
    return Integer.toString(this.port);
  }

}
