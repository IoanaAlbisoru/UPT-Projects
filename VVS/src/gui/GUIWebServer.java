package gui;

import gui.GUIFolderBar.FolderValidator;

import java.awt.Container;
import java.awt.GridLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.ItemEvent;
import java.awt.event.ItemListener;
import java.awt.event.WindowEvent;
import java.awt.event.WindowListener;
import java.io.IOException;
import java.util.concurrent.ArrayBlockingQueue;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.TimeUnit;

import javax.swing.JFrame;
import javax.swing.JOptionPane;
import javax.swing.SwingUtilities;
import server.StateListener;
import server.WebServer;
import server.WebServerState;

public class GUIWebServer {

  private JFrame frame;

  private GUIWebServerInfo info;
  private GUIWebServerControl control;
  private GUIWebServerConfiguration configuration;
  private final WebServer server;
  private final StateListener serverListener;
  private UpdaterThread updaterThread;

  public GUIWebServer(WebServer server) {
    this.server = server;
    this.serverListener = new ServerListener();
    this.server.addListener(this.serverListener);
  }

  public void start() {
    this.createGUI();
    this.updaterThread = new UpdaterThread();
    this.updaterThread.start();
  }

  private void createGUI() {
    this.info = new GUIWebServerInfo();
    this.control = new GUIWebServerControl(new StartButtonListener(), new MaintenanceCheckBoxListener());
    this.configuration = new GUIWebServerConfiguration(new RootFolderValidator(), new MaintenanceFolderValidator(), this.server.getRootFolder(),
        this.server.getMaintanenceFolder(), this.server.getPort());

    this.frame = new JFrame();
    this.frame.setName(this.server.getServerName());
    this.frame.setResizable(false);
    this.frame.addWindowListener(new Disposer());
    Container contentPane = this.frame.getContentPane();
    contentPane.setLayout(new GridLayout(3, 0, 5, 10));

    contentPane.add(this.info.getPanel());
    contentPane.add(this.configuration.getPanel());
    contentPane.add(this.control.getPanel());

    this.frame.pack();
    this.frame.setVisible(true);
    this.control.switchToStopped();
    this.configuration.switchToStopped();
  }

  public void awaitTermination() throws Exception {
    this.updaterThread.join();
  }

  final class ServerListener implements StateListener {
    private final BlockingQueue<WebServerState> queue = new ArrayBlockingQueue<>(10);

    @Override
    public void addUpdate(WebServerState toAdd) {
      this.queue.add(toAdd);
    }

    @Override
    public WebServerState getState() throws InterruptedException {
      return this.queue.poll(1, TimeUnit.SECONDS);
    }
  }

  private final class UpdaterThread extends Thread {
    private volatile boolean test = false;

    private void terminate() {
      this.interrupt();
      this.test = true;
    }

    @Override
    public void run() {
      while (!Thread.currentThread().isInterrupted() && !this.test)
        try {
          WebServerState state = GUIWebServer.this.serverListener.getState();
          if (state != null) {
            GUIWebServer.this.info.setAddress(state.serverAddress);
            GUIWebServer.this.info.setPort("" + state.currentPort);
            GUIWebServer.this.info.setStatus(state.status);
          }
        } catch (InterruptedException ignore) {
        }
    }
  }

  private final class StartButtonListener implements ActionListener {
    @Override
    public void actionPerformed(ActionEvent e) {
      if (e.getActionCommand().equals(GUIWebServerControl.BUTTON_START)) {
        String rootFolder = GUIWebServer.this.configuration.getRootFolder();

        if (rootFolder == null) {
          GUIWebServer.this.showError("Cannot start until you enter a valid root folder;", "Invalid root Folder");
          return;
        }

        int portNumber = GUIWebServer.this.configuration.getPort();
        try {
          GUIWebServer.this.server.setRootFolder(rootFolder);
          GUIWebServer.this.server.start(portNumber);
          GUIWebServer.this.control.switchToRunning();
          GUIWebServer.this.configuration.switchToRunning();
        } catch (IOException socketException) {
          GUIWebServer.this.showError("Cannot use port: " + portNumber + socketException.getLocalizedMessage(), "Socket Error");
        }
      } else
        try {
          GUIWebServer.this.control.switchToStopped();
          GUIWebServer.this.configuration.switchToStopped();
          GUIWebServer.this.server.stop();
        } catch (IOException whatTheHell) {
          GUIWebServer.this.showError("Unable to stop server: " + whatTheHell.getLocalizedMessage(), "Server stop error");
        }
    }
  }

  private final class MaintenanceCheckBoxListener implements ItemListener {

    @Override
    public void itemStateChanged(ItemEvent e) {
      if (e.getStateChange() == ItemEvent.SELECTED) {

        String maintenanceFolder = GUIWebServer.this.configuration.getMaintenanceFolder();
        if (maintenanceFolder == null) {
          GUIWebServer.this.showError("Cannot enter maintenance until you enter a valid maintenance folder;", "Invalid maintenance Folder");
          return;
        }
        GUIWebServer.this.server.setMaintanenceFolder(maintenanceFolder);
        GUIWebServer.this.server.enterMaintenance();
        GUIWebServer.this.control.switchToMaintenance();
        GUIWebServer.this.configuration.switchToMaintenance();
      } else if (e.getStateChange() == ItemEvent.DESELECTED)
        try {
          String newRootFolder = GUIWebServer.this.configuration.getRootFolder();
          if (newRootFolder == null) {
            GUIWebServer.this.showError("Cannot exit maintenance until you enter a valid root folder;", "Invalid root Folder");
            return;
          }
          GUIWebServer.this.server.setRootFolder(newRootFolder);
          GUIWebServer.this.server.restart();
          GUIWebServer.this.control.switchToRunning();
          GUIWebServer.this.configuration.switchToRunning();
        } catch (IllegalStateException e1) {
          GUIWebServer.this.showError("This bug should be reported to the developers. You shouldn't be able to restart from anyother state than MAINTENANCE"
              + e1.getLocalizedMessage(), "IllegalStateException");
          GUIWebServer.this.control.switchToStopped();
          GUIWebServer.this.configuration.switchToStopped();
        }
    }
  }

  private final class RootFolderValidator implements FolderValidator {

    @Override
    public boolean isValid(String folderName) {
      return GUIWebServer.this.server.isValidRootFolder(folderName);
    }
  }

  private final class MaintenanceFolderValidator implements FolderValidator {

    @Override
    public boolean isValid(String folderName) {
      return GUIWebServer.this.server.isValidMaintenanceFolder(folderName);
    }

  }

  public void showError(final String msg, final String title) {
    SwingUtilities.invokeLater(new Runnable() {
      @Override
      public void run() {
        JFrame parent = GUIWebServer.this.frame;
        JOptionPane.showMessageDialog(parent, msg, title, JOptionPane.ERROR_MESSAGE);
      }
    });
  }

  private final class Disposer implements WindowListener {
    @Override
    public void windowOpened(WindowEvent e) {
    }

    @Override
    public void windowClosing(WindowEvent e) {
      try {
        GUIWebServer.this.updaterThread.interrupt();
        GUIWebServer.this.updaterThread.terminate();
        GUIWebServer.this.server.terminate();
      } catch (Exception e1) {
        GUIWebServer.this.showError("Could not save the config file", "Error at saving config file.");
      } finally {
        GUIWebServer.this.frame.dispose();
      }
    }

    @Override
    public void windowClosed(WindowEvent e) {
    }

    @Override
    public void windowIconified(WindowEvent e) {
    }

    @Override
    public void windowDeiconified(WindowEvent e) {
    }

    @Override
    public void windowActivated(WindowEvent e) {
    }

    @Override
    public void windowDeactivated(WindowEvent e) {
    }
  }

}
