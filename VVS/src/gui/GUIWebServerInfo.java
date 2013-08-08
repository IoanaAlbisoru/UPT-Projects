package gui;

import java.awt.Color;
import java.awt.ComponentOrientation;
import java.awt.Dimension;

import javax.swing.BoxLayout;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.SwingConstants;
import javax.swing.SwingUtilities;
import javax.swing.border.EmptyBorder;
import javax.swing.border.LineBorder;

class GUIWebServerInfo {

  private static final String SERVER_PORT_CAPTION = "Listening port: ";
  private static final String SERVER_ADDRESS_CAPTION = "Server address: ";
  private static final String SERVER_STATUS_CAPTION = "Server status: ";

  private JPanel serverInfoPanel;

  private JPanel serverStatusPanel;
  private JLabel serverStatusCaptionLabel;
  private JLabel serverStatus;

  private JPanel serverAddressPanel;
  private JLabel serverAddressCaptionLabel;
  private JLabel serverAddress;

  private JPanel serverPortPanel;
  private JLabel serverPortCaptionLabel;
  private JLabel serverPort;

  public GUIWebServerInfo() {
    this.createGUI();
  }

  public JPanel getPanel() {
    return this.serverInfoPanel;
  }

  private void createGUI() {
    this.serverInfoPanel = new JPanel();
    this.serverInfoPanel.setBorder(new LineBorder(Color.black));

    this.serverInfoPanel.setBorder(new javax.swing.border.CompoundBorder(new javax.swing.border.TitledBorder(new javax.swing.border.EmptyBorder(0, 0, 0, 0),
        "", javax.swing.border.TitledBorder.CENTER, javax.swing.border.TitledBorder.BOTTOM, new java.awt.Font("Dialog", java.awt.Font.BOLD, 12),
        java.awt.Color.red), this.serverInfoPanel.getBorder()));

    this.serverInfoPanel.setLayout(new BoxLayout(this.serverInfoPanel, BoxLayout.Y_AXIS));

    Dimension maximumSize = new Dimension(110, 14);
    {
      this.serverStatusPanel = new JPanel();
      this.serverStatusPanel.setMaximumSize(new Dimension(350, 25));
      this.serverStatusPanel.setMinimumSize(new Dimension(350, 25));
      this.serverStatusPanel.setBorder(new EmptyBorder(5, 5, 5, 5));
      this.serverStatusPanel.setComponentOrientation(ComponentOrientation.LEFT_TO_RIGHT);
      this.serverStatusPanel.setLayout(new BoxLayout(this.serverStatusPanel, BoxLayout.LINE_AXIS));

      // ---- serverStatusCaptionLabel ----
      this.serverStatusCaptionLabel = new JLabel();
      this.serverStatusCaptionLabel.setText(GUIWebServerInfo.SERVER_STATUS_CAPTION);
      this.serverStatusCaptionLabel.setHorizontalAlignment(SwingConstants.LEFT);
      this.serverStatusCaptionLabel.setComponentOrientation(ComponentOrientation.LEFT_TO_RIGHT);
      this.serverStatusCaptionLabel.setMaximumSize(maximumSize);
      this.serverStatusCaptionLabel.setMinimumSize(maximumSize);
      this.serverStatusPanel.add(this.serverStatusCaptionLabel);

      // ---- serverStatus ----
      this.serverStatus = new JLabel();
      this.serverStatus.setText("YOU SHOULD NEVER SEE THIS");
      this.serverStatus.setHorizontalAlignment(SwingConstants.LEFT);
      this.serverStatus.setComponentOrientation(ComponentOrientation.LEFT_TO_RIGHT);
      this.serverStatus.setMaximumSize(new Dimension(110, 14));
      this.serverStatus.setMinimumSize(new Dimension(30, 14));
      this.serverStatusPanel.add(this.serverStatus);
    }
    this.serverInfoPanel.add(this.serverStatusPanel);

    // ======== serverAddressPanel ========
    {
      this.serverAddressPanel = new JPanel();
      this.serverAddressPanel.setMaximumSize(new Dimension(350, 25));
      this.serverAddressPanel.setMinimumSize(new Dimension(350, 25));
      this.serverAddressPanel.setBorder(new EmptyBorder(5, 5, 5, 5));
      this.serverAddressPanel.setComponentOrientation(ComponentOrientation.LEFT_TO_RIGHT);
      this.serverAddressPanel.setLayout(new BoxLayout(this.serverAddressPanel, BoxLayout.LINE_AXIS));

      // ---- serverAddressCaptionLabel ----
      this.serverAddressCaptionLabel = new JLabel();
      this.serverAddressCaptionLabel.setText(GUIWebServerInfo.SERVER_ADDRESS_CAPTION);
      this.serverAddressCaptionLabel.setHorizontalAlignment(SwingConstants.LEFT);
      this.serverAddressCaptionLabel.setMaximumSize(maximumSize);
      this.serverAddressCaptionLabel.setMinimumSize(maximumSize);
      this.serverAddressPanel.add(this.serverAddressCaptionLabel);

      // ---- serverAddress ----
      this.serverAddress = new JLabel();
      this.serverAddress.setText("YOU SHOULD NEVER SEE THIS");
      this.serverAddress.setMaximumSize(new Dimension(110, 14));
      this.serverAddressPanel.add(this.serverAddress);
    }
    this.serverInfoPanel.add(this.serverAddressPanel);

    // ======== serverPortPanel ========
    {
      this.serverPortPanel = new JPanel();
      this.serverPortPanel.setMaximumSize(new Dimension(350, 25));
      this.serverPortPanel.setMinimumSize(new Dimension(350, 25));
      this.serverPortPanel.setBorder(new EmptyBorder(5, 5, 5, 5));
      this.serverPortPanel.setComponentOrientation(ComponentOrientation.LEFT_TO_RIGHT);
      this.serverPortPanel.setLayout(new BoxLayout(this.serverPortPanel, BoxLayout.LINE_AXIS));

      // ---- serverPortCaptionLabel ----
      this.serverPortCaptionLabel = new JLabel();
      this.serverPortCaptionLabel.setText(GUIWebServerInfo.SERVER_PORT_CAPTION);
      this.serverPortCaptionLabel.setMaximumSize(maximumSize);
      this.serverPortCaptionLabel.setMinimumSize(maximumSize);
      this.serverPortPanel.add(this.serverPortCaptionLabel);

      // ---- serverPort ----
      this.serverPort = new JLabel();
      this.serverPort.setText("YOU SHOULD NEVER SEE THIS");
      this.serverPort.setMaximumSize(maximumSize);
      this.serverPortPanel.add(this.serverPort);
    }
    this.serverInfoPanel.add(this.serverPortPanel);
  }

  public void setStatus(final String status) {
    SwingUtilities.invokeLater(new Runnable() {
      @Override
      public void run() {
        GUIWebServerInfo.this.serverStatus.setText(status);
      }
    });
  }

  public void setAddress(final String address) {
    SwingUtilities.invokeLater(new Runnable() {
      @Override
      public void run() {
        GUIWebServerInfo.this.serverAddress.setText(address);
      }
    });
  }

  public void setPort(final String port) {
    SwingUtilities.invokeLater(new Runnable() {
      @Override
      public void run() {
        GUIWebServerInfo.this.serverPort.setText(port);
      }
    });
  }
}