package gui;

import java.awt.Dimension;
import javax.swing.BoxLayout;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JTextField;
import javax.swing.SwingConstants;
import javax.swing.event.DocumentEvent;
import javax.swing.event.DocumentListener;
import javax.swing.text.BadLocationException;

class GUIFolderBar {

  public interface FolderValidator {
    public boolean isValid(String folderName);
  }

  private static final String YAY = "YAY ";
  private static final String NAY = "NAY ";

  private JPanel folderBarPanel;
  private JTextField textFolderField;
  private JLabel caption;
  private JLabel validityCaption;
  private final FolderValidator validator;

  public GUIFolderBar(FolderValidator validator, String labelText, String initialFolder) {
    this.validator = validator;
    this.createGUI(labelText, initialFolder, validator);
  }

  private void createGUI(String labelString, String initialFolder, final FolderValidator validator) {
    this.folderBarPanel = new JPanel();
    this.folderBarPanel.setMinimumSize(new Dimension(350, 25));
    this.folderBarPanel.setMaximumSize(new Dimension(350, 25));
    this.folderBarPanel.setPreferredSize(new Dimension(350, 25));
    this.folderBarPanel.setLayout(new BoxLayout(this.folderBarPanel, BoxLayout.LINE_AXIS));

    // ---- caption ----
    this.caption = new JLabel();
    this.caption.setText(labelString);
    this.caption.setMaximumSize(new Dimension(80, 14));
    this.caption.setMinimumSize(new Dimension(80, 14));
    this.caption.setPreferredSize(new Dimension(80, 14));
    this.folderBarPanel.add(this.caption);

    DocumentListener listener = new DocumentListener() {
      @Override
      public void removeUpdate(DocumentEvent e) {
        this.verify(validator, e);
      }

      @Override
      public void insertUpdate(DocumentEvent e) {
        this.verify(validator, e);
      }

      @Override
      public void changedUpdate(DocumentEvent e) {
        this.verify(validator, e);
      }

      private void verify(final FolderValidator validator, DocumentEvent e) {
        try {
          int length = e.getDocument().getLength();
          String text = e.getDocument().getText(0, length);
          if (validator.isValid(text))
            GUIFolderBar.this.validityCaption.setText(GUIFolderBar.YAY);
          else
            GUIFolderBar.this.validityCaption.setText(GUIFolderBar.NAY);
        } catch (BadLocationException e1) {
        }
      }
    };

    // ---- textFolderField ----
    this.textFolderField = new JTextField(initialFolder);
    this.textFolderField.setMinimumSize(new Dimension(220, 20));
    this.textFolderField.setMaximumSize(new Dimension(220, 20));
    this.textFolderField.setPreferredSize(new Dimension(220, 20));
    this.textFolderField.getDocument().addDocumentListener(listener);
    this.folderBarPanel.add(this.textFolderField);

    // ---- validityCaption ----
    this.validityCaption = new JLabel();
    this.validityCaption.setMaximumSize(new Dimension(40, 14));
    this.validityCaption.setMinimumSize(new Dimension(40, 14));
    this.validityCaption.setPreferredSize(new Dimension(40, 14));
    this.validityCaption.setHorizontalAlignment(SwingConstants.RIGHT);

    if (validator.isValid(initialFolder))
      this.validityCaption.setText(GUIFolderBar.YAY);
    else
      this.validityCaption.setText(GUIFolderBar.NAY);

    this.folderBarPanel.add(this.validityCaption);

  }

  public JPanel getPanel() {
    return this.folderBarPanel;
  }

  /**
   * @return returns the text in the text field but only if it is valid!
   */
  public String getFolder() {
    String text = this.textFolderField.getText();
    if (this.validator.isValid(text))
      return text;
    else
      return null;
  }

  public void disable() {
    this.textFolderField.setEnabled(false);
  }

  public void enable() {
    this.textFolderField.setEnabled(true);
  }

}