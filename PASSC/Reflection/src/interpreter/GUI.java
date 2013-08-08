package interpreter;

import java.awt.BorderLayout;
import java.awt.Dimension;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.util.Collection;

import javax.swing.BorderFactory;
import javax.swing.Box;
import javax.swing.BoxLayout;
import javax.swing.DefaultListModel;
import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JList;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTextArea;
import javax.swing.ListSelectionModel;
import javax.swing.ScrollPaneConstants;
import javax.swing.SwingUtilities;
import javax.swing.WindowConstants;

import myReflection.Entity;
import myReflection.Instance;
import myReflection.ObjectModel;
import myReflection.exceptions.EntityNotFound;

public class GUI {
  private JMyList entities;
  private JMyList instances;
  private JTextArea userInput;
  private JTextArea output;
  private JFrame frame;

  private JButton interpret;

  private void setLayout() {
    this.userInput = new JTextArea();
    this.userInput.setVisible(true);
    this.userInput.setEditable(true);

    final JScrollPane scrollPaneUserInput = new JScrollPane(this.userInput,
        ScrollPaneConstants.VERTICAL_SCROLLBAR_AS_NEEDED, ScrollPaneConstants.HORIZONTAL_SCROLLBAR_AS_NEEDED);

    scrollPaneUserInput.setPreferredSize(new Dimension(200, 200));

    this.output = new JTextArea();
    this.output.setVisible(true);
    this.output.setEditable(false);

    final JScrollPane scrollPaneOutput = new JScrollPane(this.output, ScrollPaneConstants.VERTICAL_SCROLLBAR_AS_NEEDED,
        ScrollPaneConstants.HORIZONTAL_SCROLLBAR_AS_NEEDED);

    scrollPaneOutput.setPreferredSize(new Dimension(200, 200));

    this.entities = new JMyList(new EntityMouseListener(), "Entities:", ListSelectionModel.SINGLE_INTERVAL_SELECTION,
        JList.VERTICAL);

    final JPanel textPanel = new JPanel();
    textPanel.setLayout(new BoxLayout(textPanel, BoxLayout.X_AXIS));
    textPanel.add(scrollPaneOutput);
    textPanel.add(Box.createHorizontalStrut(10));
    textPanel.add(scrollPaneUserInput);
    textPanel.add(Box.createHorizontalStrut(10));
    textPanel.add(this.entities.getPanel());
    textPanel.setBorder(BorderFactory.createEmptyBorder(5, 5, 5, 5));

    this.instances = new JMyList(new InstanceMouseListener(), "Instances",
        ListSelectionModel.SINGLE_INTERVAL_SELECTION, JList.HORIZONTAL_WRAP);

    final JPanel instancePanel = new JPanel();
    instancePanel.setLayout(new BoxLayout(instancePanel, BoxLayout.LINE_AXIS));
    instancePanel.setBorder(BorderFactory.createEmptyBorder(5, 5, 5, 5));
    instancePanel.setMaximumSize(new Dimension(900, 100));
    this.instances.getPanel().setMaximumSize(new Dimension(900, 100));
    this.instances.getPanel().setPreferredSize(new Dimension(400, 100));
    instancePanel.add(this.instances.getPanel());

    final JPanel buttonPanel = new JPanel();
    buttonPanel.setBorder(BorderFactory.createEmptyBorder(5, 5, 5, 5));
    this.interpret = new JButton("Interpret");
    this.interpret.setEnabled(true);
    this.interpret.addActionListener(new InterpretButtonListener());
    buttonPanel.add(this.interpret);

    this.frame = new JFrame();
    this.frame.setDefaultCloseOperation(WindowConstants.EXIT_ON_CLOSE);
    this.frame.getContentPane().add(textPanel, BorderLayout.CENTER);
    this.frame.getContentPane().add(buttonPanel, BorderLayout.SOUTH);
    this.frame.getContentPane().add(instancePanel, BorderLayout.NORTH);

    this.frame.pack();
    this.frame.setVisible(true);
  }

  private class EntityMouseListener implements MouseListener {

    @Override
    public void mouseReleased(final MouseEvent e) {
    }

    @Override
    public void mousePressed(final MouseEvent e) {
    }

    @Override
    public void mouseExited(final MouseEvent e) {
    }

    @Override
    public void mouseEntered(final MouseEvent e) {
    }

    @Override
    public void mouseClicked(final MouseEvent e) {
      if (e.getClickCount() == 2) {

        final Entity temp;
        final String entityName = (String) GUI.this.entities.getSelectedValue();
        try {
          temp = ObjectModel.getEntity(entityName);
          SwingUtilities.invokeLater(new Runnable() {
            @Override
            public void run() {
              if (temp == null) {
                return;
              }
              GUI.this.output.setText(temp.forInterpreter());
            }
          });

        } catch (final EntityNotFound e1) {
          return;
        }

      }
    }
  };

  private class InstanceMouseListener implements MouseListener {
    @Override
    public void mouseReleased(final MouseEvent e) {
    }

    @Override
    public void mousePressed(final MouseEvent e) {
    }

    @Override
    public void mouseExited(final MouseEvent e) {
    }

    @Override
    public void mouseEntered(final MouseEvent e) {
    }

    @Override
    public void mouseClicked(final MouseEvent e) {
      if (e.getClickCount() == 2) {

        final Instance temp;
        final String instanceName = (String) GUI.this.instances.getSelectedValue();
        try {
          temp = Parser.instances.get(instanceName);
          SwingUtilities.invokeLater(new Runnable() {
            @Override
            public void run() {
              if (temp == null) {
                return;
              }
              GUI.this.output.setText(temp.forInterpreter());
            }
          });

        } catch (final Exception e1) {
          return;
        }
      }
    }
  };

  private class InterpretButtonListener implements ActionListener {

    @Override
    public void actionPerformed(final ActionEvent arg0) {
      final String textToBeInterpreted = GUI.this.userInput.getText();

      SwingUtilities.invokeLater(new Runnable() {
        @Override
        public void run() {
          GUI.this.userInput.setText("");
          GUI.this.output.setText("");

        }
      });

      final Parser parser = new Parser();

      try {
        parser.parse(textToBeInterpreted);
        GUI.this.updateEntities();
        GUI.this.updateInstances();
      } catch (final Exception e) {
        final String temp = e.getMessage();
        SwingUtilities.invokeLater(new Runnable() {
          @Override
          public void run() {
            GUI.this.output.setText("Error: " + temp);
          }
        });
      }
    }
  }

  private void updateEntities() {
    this.entities.removeAllElements();
    final Collection<Entity> entities = ObjectModel.getEntities();
    for (final Entity ent : entities) {
      this.entities.addElement(ent.getName());
    }
  }

  private void updateInstances() {
    if (Parser.opResult != null) {
      SwingUtilities.invokeLater(new Runnable() {
        @Override
        public void run() {
          GUI.this.output.setText(Parser.opResult.toString());
          Parser.opResult = null;
        }
      });
    }
    this.instances.removeAllElements();
    for (final String instanceName : Parser.instances.keySet()) {
      this.instances.addElement(instanceName);
    }

  }

  private void start() {
    SwingUtilities.invokeLater(new Runnable() {
      @Override
      public void run() {
        GUI.this.setLayout();
      }
    });
  }

  private class JMyList {
    private final JPanel panel;
    private final Dimension listDimension = new Dimension(200, 150);

    /**
     * Was intended to use only to add this list to a container
     * 
     * @return the panel on which all the list objects are positioned
     */
    protected final JPanel getPanel() {
      return this.panel;
    }

    private final DefaultListModel<Object> model;
    private final JList<Object> list;

    /**
     * @param listener
     *          the {@link MouseListener} associated with the events of the
     *          lists
     * @param label
     *          the caption of the label seen in this type of list
     */
    protected JMyList(final MouseListener listener, final String label, final int selectionMode, final int Orientation) {
      this.model = new DefaultListModel<Object>();
      this.list = new JList<Object>(this.model);
      this.list.setVisible(true);
      this.list.setSelectionMode(selectionMode);
      this.list.setLayoutOrientation(Orientation);
      this.list.setVisibleRowCount(-1);
      this.list.addMouseListener(listener);

      this.panel = new JPanel();
      this.panel.setPreferredSize(new Dimension(this.listDimension));
      final JLabel jLabel = new JLabel(label);
      this.panel.add(jLabel);
      this.panel.setLayout(new BoxLayout(this.panel, BoxLayout.Y_AXIS));
      final JScrollPane scrollPane = new JScrollPane(this.list, ScrollPaneConstants.VERTICAL_SCROLLBAR_AS_NEEDED,
          ScrollPaneConstants.HORIZONTAL_SCROLLBAR_AS_NEEDED);

      scrollPane.setPreferredSize(this.listDimension);
      scrollPane.setVisible(true);
      this.panel.add(scrollPane);

    }

    @SuppressWarnings("unused")
    protected int getSelectedIndex() {
      return this.list.getSelectedIndex();
    }

    protected Object getSelectedValue() {
      return this.list.getSelectedValue();
    }

    protected void addElement(final Object toAdd) {
      SwingUtilities.invokeLater(new Runnable() {
        @Override
        public void run() {
          JMyList.this.model.addElement(toAdd);
        }
      });
    }

    @SuppressWarnings("unused")
    protected void removeElement(final Object toRemove) {
      SwingUtilities.invokeLater(new Runnable() {
        @Override
        public void run() {
          JMyList.this.model.removeElement(toRemove);
        }
      });
    }

    protected void removeAllElements() {
      SwingUtilities.invokeLater(new Runnable() {
        @Override
        public void run() {
          JMyList.this.model.removeAllElements();
        }
      });
    }

    @SuppressWarnings("unused")
    protected Object get(final int index) {
      return this.model.get(index);
    }

    @SuppressWarnings("unused")
    protected Object remove(final int index) {
      return this.model.remove(index);
    }
  };

  public static void main(final String args[]) {
    final GUI gui = new GUI();
    gui.start();
  }

  /*
   * TODO ======================================================================
   * ====== ====================================================================
   * ======== ==================================================================
   * ==========
   */

}
