package gui;

import gui.GUIFolderBar.FolderValidator;

import java.awt.Dimension;
import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;

import javax.swing.BoxLayout;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JTextField;
import javax.swing.SwingUtilities;

class GUIWebServerConfiguration implements ModeSwitch {
  private static final String NAME = "Server Configuration";

  private JPanel serverConfigurationPanel;
  private JTextField portNumberField;
  private GUIFolderBar rootBar;
  private GUIFolderBar maintenanceBar;

  public GUIWebServerConfiguration(FolderValidator rootFolderListener, FolderValidator maintenanceFolderListener, String rootFolder, String maintenanceFolder,
      String portNumber) {
    this.createGUI(rootFolderListener, maintenanceFolderListener, rootFolder, maintenanceFolder, portNumber);
  }

  private void createGUI(FolderValidator rootFolderValidator, FolderValidator maintenanceFolderValidator, String rootFolder, String maintenanceFolder,
      String portNumber) {
    this.serverConfigurationPanel = new JPanel();
    this.serverConfigurationPanel.setName(GUIWebServerConfiguration.NAME);
    this.serverConfigurationPanel.setLayout(new BoxLayout(this.serverConfigurationPanel, BoxLayout.Y_AXIS));

    // ======== portNumberPanel ========
    JPanel portNumberPanel = new JPanel();
    portNumberPanel.setMaximumSize(new Dimension(200, 25));
    portNumberPanel.setMinimumSize(new Dimension(200, 25));
    portNumberPanel.setPreferredSize(new Dimension(200, 25));
    portNumberPanel.setLayout(new BoxLayout(portNumberPanel, BoxLayout.LINE_AXIS));

    // ---- portNumberLabel ----
    JLabel portNumberLabel = new JLabel();
    portNumberLabel.setText("Port number:  ");
    portNumberLabel.setMaximumSize(new Dimension(90, 14));
    portNumberLabel.setMinimumSize(new Dimension(90, 14));
    portNumberLabel.setPreferredSize(new Dimension(90, 14));
    portNumberPanel.add(portNumberLabel);

    // ---- portNumberField ----
    this.portNumberField = new JTextField();
    this.portNumberField.setText("" + portNumber);
    this.portNumberField.setMaximumSize(new Dimension(80, 18));
    this.portNumberField.setMinimumSize(new Dimension(80, 18));
    this.portNumberField.setPreferredSize(new Dimension(80, 18));
    this.portNumberField.addKeyListener(new InputValidator());
    portNumberPanel.add(this.portNumberField);

    this.serverConfigurationPanel.add(portNumberPanel);

    // ---- folder bars----
    this.rootBar = new GUIFolderBar(rootFolderValidator, "Root Folder:", rootFolder);
    this.maintenanceBar = new GUIFolderBar(maintenanceFolderValidator, "Maintenance:", maintenanceFolder);
    this.serverConfigurationPanel.add(this.rootBar.getPanel());
    this.serverConfigurationPanel.add(this.maintenanceBar.getPanel());
  }

  public JPanel getPanel() {
    return this.serverConfigurationPanel;
  }

  public int getPort() {
    return Integer.parseInt(this.portNumberField.getText());
  }

  public String getRootFolder() {
    return this.rootBar.getFolder();
  }

  public String getMaintenanceFolder() {
    return this.maintenanceBar.getFolder();
  }

  private static class InputValidator implements KeyListener {

    @Override
    public void keyTyped(KeyEvent e) {
      char keyChar = e.getKeyChar();
      if (!Character.isDigit(keyChar))
        e.consume();
      else {
        JTextField textField = (JTextField) e.getSource();
        if (textField.getText().length() == 5)
          e.consume();
      }
    }

    @Override
    public void keyPressed(KeyEvent e) {
    }

    @Override
    public void keyReleased(KeyEvent e) {
    }

  }

  @Override
  public void switchToRunning() {
    SwingUtilities.invokeLater(new Runnable() {
      @Override
      public void run() {
        GUIWebServerConfiguration.this.rootBar.disable();
        GUIWebServerConfiguration.this.maintenanceBar.enable();
        GUIWebServerConfiguration.this.portNumberField.setEnabled(false);
      }
    });
  }

  @Override
  public void switchToStopped() {
    SwingUtilities.invokeLater(new Runnable() {

      @Override
      public void run() {
        GUIWebServerConfiguration.this.rootBar.enable();
        GUIWebServerConfiguration.this.maintenanceBar.enable();
        GUIWebServerConfiguration.this.portNumberField.setEnabled(true);
      }
    });

  }

  @Override
  public void switchToMaintenance() {
    SwingUtilities.invokeLater(new Runnable() {

      @Override
      public void run() {
        GUIWebServerConfiguration.this.rootBar.enable();
        GUIWebServerConfiguration.this.maintenanceBar.disable();
        GUIWebServerConfiguration.this.portNumberField.setEnabled(false);
      }
    });
  }
}
