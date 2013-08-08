package gui;

import java.awt.event.ActionListener;
import java.awt.event.ItemListener;

import javax.swing.GroupLayout;
import javax.swing.JButton;
import javax.swing.JCheckBox;
import javax.swing.JPanel;
import javax.swing.SwingUtilities;

class GUIWebServerControl implements ModeSwitch {
  public static final String BUTTON_START = "Start";
  public static final String BUTTON_STOP = "Stop";

  private static final String CHECK_BOX_CAPTION = "Maintenance Mode";

  private JPanel serverControlPanel;
  private JButton startButton;
  private JCheckBox maintenanceCheckBox;

  public GUIWebServerControl(ActionListener startButtonListener, ItemListener maintenanceCheckBoxListener) {
    this.createGUI(startButtonListener, maintenanceCheckBoxListener);
  }

  public JPanel getPanel() {
    return this.serverControlPanel;
  }

  private void createGUI(ActionListener buttonListener, ItemListener checkBoxListener) {
    // ---- maintenanceCheckBox ----
    this.maintenanceCheckBox = new JCheckBox(GUIWebServerControl.CHECK_BOX_CAPTION);
    this.maintenanceCheckBox.addItemListener(checkBoxListener);

    // ---- startButton ----
    this.startButton = new JButton();
    this.changeButtonTextAndCommand(GUIWebServerControl.BUTTON_START);
    this.startButton.addActionListener(buttonListener);

    this.serverControlPanel = new JPanel();
    GroupLayout ServerControlLayout = new GroupLayout(this.serverControlPanel);
    this.serverControlPanel.setLayout(ServerControlLayout);

    ServerControlLayout.setHorizontalGroup(ServerControlLayout.createParallelGroup().addGroup(
        ServerControlLayout.createSequentialGroup().addGap(16, 16, 16)
            .addComponent(this.startButton, GroupLayout.PREFERRED_SIZE, 88, GroupLayout.PREFERRED_SIZE).addGap(18, 18, 18)
            .addComponent(this.maintenanceCheckBox, GroupLayout.DEFAULT_SIZE, 161, Short.MAX_VALUE).addContainerGap()));
    ServerControlLayout.setVerticalGroup(ServerControlLayout.createParallelGroup().addGroup(
        ServerControlLayout
            .createSequentialGroup()
            .addGap(19, 19, 19)
            .addGroup(
                ServerControlLayout.createParallelGroup(GroupLayout.Alignment.BASELINE)
                    .addComponent(this.startButton, GroupLayout.PREFERRED_SIZE, 39, GroupLayout.PREFERRED_SIZE).addComponent(this.maintenanceCheckBox))
            .addContainerGap(23, Short.MAX_VALUE)));
  }

  @Override
  public void switchToRunning() {
    SwingUtilities.invokeLater(new Runnable() {
      @Override
      public void run() {
        GUIWebServerControl.this.changeButtonTextAndCommand(GUIWebServerControl.BUTTON_STOP);
        GUIWebServerControl.this.maintenanceCheckBox.setEnabled(true);
      }
    });
  }

  @Override
  public void switchToStopped() {
    SwingUtilities.invokeLater(new Runnable() {
      @Override
      public void run() {
        GUIWebServerControl.this.changeButtonTextAndCommand(GUIWebServerControl.BUTTON_START);
        GUIWebServerControl.this.maintenanceCheckBox.setEnabled(false);
      }
    });
  }

  @Override
  public void switchToMaintenance() {
  }

  private void changeButtonTextAndCommand(String text) {
    this.startButton.setText(text);
    this.startButton.setActionCommand(text);
  }

}
