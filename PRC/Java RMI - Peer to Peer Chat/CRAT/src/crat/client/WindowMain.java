package crat.client;

import java.awt.BorderLayout;
import java.awt.Component;
import java.awt.Dimension;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.awt.event.WindowEvent;
import java.awt.event.WindowListener;
import java.rmi.RemoteException;
import java.rmi.server.UnicastRemoteObject;
import java.util.Collection;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.CopyOnWriteArrayList;
import java.util.concurrent.LinkedBlockingQueue;
import java.util.concurrent.TimeUnit;

import javax.swing.*;

import crat.common.ServerEventListener;

/**
 * This class models the main window. it is responsible for interpreting all
 * user interaction. It's as weakly coupled as possible with the user model. All
 * non-GUI actions are delegated to the 'Network'
 */
public class WindowMain implements MainGUI {
  private static WindowMain instance = null;

  private CopyOnWriteArrayList<String> users;
  private BlockingQueue<String> userConnectionRequests;
  private BlockingQueue<String> userDisconnectRequests;

  private final String myName;
  private final Network network;
  private ListenerServerEvents serverListener;

  public final ServerEventListener getServerListener() {
    return serverListener;
  }

  public static MainGUI createControlGUI( String myName, Network network ) {
    if ( WindowMain.instance == null )
      WindowMain.instance = new WindowMain( myName,network );
    return WindowMain.instance;
  }

  public static WindowMain getReference() {
    return WindowMain.instance;
  }

  /**
   * THIS FUNCTION IS NASTY, AWFULL HACK
   * TODO
   * Used only so that UserDialogs have a parent!!
   * 
   * @return
   */
  public JFrame getFrame() {
    return this.frame;
  }

  private WindowMain( String myName, Network network ){
    this.myName = myName;
    try {
      this.serverListener = new ListenerServerEvents();
    }
    catch (RemoteException e) {
      this.serverListener = null;
    }
    this.network = network;
    this.users = new CopyOnWriteArrayList<String>();
    this.userConnectionRequests = new LinkedBlockingQueue<String>();
    this.userDisconnectRequests = new LinkedBlockingQueue<String>();

  }

  /**
   * This class encapsulates the behavior of a JList, that of a ListModel and of
   * a scrollPane.
   * You can see objects of this type in two places in the main window, the list
   * of the already connected user, and the list with the users connected to the
   * server
   * IF a programmer ever wishes to access some other functions provided by
   * JList, ListModel and Scroll pane he should add a wrapper method to this
   * class's interface
   */
  private class JUserList {
    private JPanel panel;
    private final Dimension listDimension = new Dimension( 200,150 );

    /**
     * Was intended to use only to add this list to a container
     * 
     * @return the panel on which all the list objects are positioned
     */
    protected final JPanel getPanel() {
      return this.panel;
    }

    private DefaultListModel model;
    private JList list;

    /**
     * @param listener
     *          the {@link MouseListener} associated with the events of the
     *          lists
     * @param label
     *          the caption of the label seen in this type of list
     */
    protected JUserList( MouseListener listener, String label ){
      this.model = new DefaultListModel();
      this.list = new JList( this.model );
      this.list.setVisible( true );
      this.list.setSelectionMode( ListSelectionModel.SINGLE_INTERVAL_SELECTION );
      this.list.setLayoutOrientation( JList.VERTICAL );
      this.list.setVisibleRowCount( -1 );
      this.list.addMouseListener( listener );

      this.panel = new JPanel();
      this.panel.setPreferredSize( new Dimension( listDimension ) );
      JLabel jLabel = new JLabel( label );
      this.panel.add( jLabel );
      this.panel.setLayout( new BoxLayout( this.panel,BoxLayout.Y_AXIS ) );
      JScrollPane scrollPane = new JScrollPane( this.list,
          ScrollPaneConstants.VERTICAL_SCROLLBAR_AS_NEEDED,
          ScrollPaneConstants.HORIZONTAL_SCROLLBAR_NEVER );

      scrollPane.setPreferredSize( listDimension );
      scrollPane.setVisible( true );
      this.panel.add( scrollPane );

    }

    protected int getSelectedIndex() {
      return this.list.getSelectedIndex();
    }

    protected Object getSelectedValue() {
      return this.list.getSelectedValue();
    }

    protected void addElement( final Object toAdd ) {
      SwingUtilities.invokeLater( new Runnable() {
        @Override
        public void run() {
          JUserList.this.model.addElement( toAdd );
        }
      } );

    }

    protected void removeElement( final Object toRemove ) {
      SwingUtilities.invokeLater( new Runnable() {
        @Override
        public void run() {
          JUserList.this.model.removeElement( toRemove );
        }
      } );
    }

    protected void removeAllElements() {
      SwingUtilities.invokeLater( new Runnable() {
        @Override
        public void run() {
          JUserList.this.model.removeAllElements();
        }
      } );
    }

    protected Object get( int index ) {
      return this.model.get( index );
    }

    protected Object remove( int index ) {
      return this.model.remove( index );
    }
  };

  private JButton buttonUserRemove;
  private JButton buttonServerDisconnect;
  private JButton buttonServerConnect;
  private JUserList listConnectedUsers;
  private JUserList listServerClients;
  private JFrame frame = null;

  private void setLayout() {
    this.listConnectedUsers = new JUserList( new ListenerUserList(),
        "Users we are chatting to:" );
    this.listServerClients = new JUserList( new ListenerServerUserList(),
        "Users connected to server:" );

    this.buttonUserRemove = new JButton( "Remove User" );
    this.buttonUserRemove.setActionCommand( "Remove User" );
    this.buttonUserRemove.addActionListener( new ButtonRemoveUserListener() );
    this.buttonUserRemove.setEnabled( false );

    JPanel buttonUserPanel = new JPanel();
    buttonUserPanel
        .setLayout( new BoxLayout( buttonUserPanel,BoxLayout.X_AXIS ) );
    buttonUserPanel.add( this.buttonUserRemove );
    buttonUserPanel.setBorder( BorderFactory.createEmptyBorder( 5, 5, 5, 5 ) );

    this.buttonServerDisconnect = new JButton( "Disconnect" );
    this.buttonServerDisconnect
        .addActionListener( new ButtonDisconnectFromServer() );
    this.buttonServerDisconnect.setEnabled( true );

    this.buttonServerConnect = new JButton( "  Connect " );
    this.buttonServerConnect.addActionListener( new ButtonConnectToServer() );
    this.buttonServerConnect.setEnabled( false );

    JPanel buttonServerPanel = new JPanel();
    buttonServerPanel.setLayout( new BoxLayout( buttonServerPanel,
        BoxLayout.X_AXIS ) );
    buttonServerPanel.add( this.buttonServerDisconnect );
    buttonServerPanel.add( Box.createHorizontalStrut( 5 ) );
    buttonServerPanel.add( this.buttonServerConnect );
    buttonServerPanel.setBorder( BorderFactory.createEmptyBorder( 5, 5, 5, 5 ) );

    this.frame = new JFrame( "CRAT: " + this.myName );
    JPanel framePanel = new JPanel();
    framePanel.setLayout( new BoxLayout( framePanel,BoxLayout.Y_AXIS ) );

    framePanel.add( buttonUserPanel );
    framePanel.add( this.listConnectedUsers.getPanel() );
    framePanel.add( buttonServerPanel );
    framePanel.add( this.listServerClients.getPanel() );
    //alignment doesn't seem to work although we use a BoxLayout.
    framePanel.setAlignmentX( Component.LEFT_ALIGNMENT );

    this.frame.setDefaultCloseOperation( WindowConstants.EXIT_ON_CLOSE );
    this.frame.setResizable( false );
    this.frame.getContentPane().add( framePanel, BorderLayout.CENTER );
    this.frame.addWindowListener( new ControlWindowListener() );

    this.frame.setLocationByPlatform( true );

    this.frame.pack();
    this.frame.setVisible( true );

  }

  private class ControlWindowListener implements WindowListener {

    @Override
    public void windowActivated( WindowEvent e ) {}

    @Override
    public void windowClosed( WindowEvent e ) {}

    @Override
    public void windowClosing( WindowEvent e ) {
      try {
        WindowMain.this.close();
      }
      catch (Exception ignore) {}
    }

    @Override
    public void windowDeactivated( WindowEvent e ) {/* NOP */}

    @Override
    public void windowDeiconified( WindowEvent e ) {/* NOP */}

    @Override
    public void windowIconified( WindowEvent e ) {/* NOP */}

    @Override
    public void windowOpened( WindowEvent e ) {/* NOP */}
  }

  /**
   * The action listener of the Disconnect from server button
   */
  private class ButtonDisconnectFromServer implements ActionListener {
    @Override
    public void actionPerformed( ActionEvent arg0 ) {
      try {
        WindowMain.this.network.disconnectFromServer();
        SwingUtilities.invokeLater( new Runnable() {
          public void run() {
            WindowMain.this.buttonServerDisconnect.setEnabled( false );
            WindowMain.this.buttonServerConnect.setEnabled( true );
          }
        } );

      }
      catch (RemoteException e) {
        UserDialog.showError( "Unable to disconnect", "Disconnect Button" );
      }
      WindowMain.this.listServerClients.removeAllElements();
    }
  };

  private class ButtonConnectToServer implements ActionListener {

    public void actionPerformed( ActionEvent e ) {
      final String temp = UserDialog.showInputDialog(
          "Please Insert new Server.", "Connect to Server", "127.0.0.1",
          WindowMain.this.frame );

      // means that the user hit "Cancel"
      if ( temp == null ) {
        return;
      }
      if ( WindowMain.this.network.reconnectToServer( temp ) ) {
        UserDialog.showMessage(
            String.format( "You are now connected to %s", temp ),
            "Server connect" );
        SwingUtilities.invokeLater( new Runnable() {
          @Override
          public void run() {
            WindowMain.this.buttonServerDisconnect.setEnabled( true );
            WindowMain.this.buttonServerConnect.setEnabled( false );
          }
        } );

      } else {
        UserDialog.showError(
            String.format( "Cannot connect to server @ '%s'", temp ),
            "Failed to reconnect." );
      }
    }

  }

  private class ButtonRemoveUserListener implements ActionListener {

    @Override
    public void actionPerformed( ActionEvent e ) {
      int index = WindowMain.this.listConnectedUsers.getSelectedIndex();

      // means that no item is selected, we have nothing to remove;
      if ( index == -1 )
        return;

      // we remove the user from the set
      String toRemove = (String) WindowMain.this.listConnectedUsers.get( index );
      if ( WindowMain.this.users.remove( toRemove ) ) {
        WindowMain.this.userDisconnectRequests.add( toRemove );
      };

      WindowMain.this.listConnectedUsers.remove( index );

      // we always disable it. The button is enabled only when the user
      // clicks on another contact
      SwingUtilities.invokeLater( new Runnable() {
        @Override
        public void run() {
          WindowMain.this.buttonUserRemove.setEnabled( false );
        }
      } );

    }
  }

  private class ListenerUserList implements MouseListener {

    @Override
    public void mouseClicked( final MouseEvent e ) {
      try {
        if ( e.getClickCount() == 2 ) {
          SwingUtilities.invokeLater( new Runnable() {
            public void run() {
              int index = WindowMain.this.listConnectedUsers.getSelectedIndex();

              if ( index != -1 ) {
                String temp = (String) WindowMain.this.listConnectedUsers
                    .getSelectedValue();
                WindowMain.this.network.showUserGUI( temp );
              }
            }
          } );
        }

        final int index = WindowMain.this.listConnectedUsers.getSelectedIndex();
        SwingUtilities.invokeLater( new Runnable() {
          @Override
          public void run() {
            if ( index == -1 )
              WindowMain.this.buttonUserRemove.setEnabled( false );
            else
              WindowMain.this.buttonUserRemove.setEnabled( true );
          }
        } );
      }
      catch (Exception whatTheHell) {
        UserDialog.userTooStupidDialog( "ControlWindow on Mouse Click" );
      }
    }

    @Override
    public void mouseEntered( MouseEvent e ) {}

    @Override
    public void mouseExited( MouseEvent e ) {}

    @Override
    public void mousePressed( MouseEvent e ) {}

    @Override
    public void mouseReleased( MouseEvent e ) {}

  }

  private class ListenerServerUserList implements MouseListener {

    /*
     * If we double click on a user name in the available user list then we will
     * attempt to connect to him
     */
    @Override
    public void mouseClicked( final MouseEvent e ) {
      try {
        if ( e.getClickCount() == 2 ) {
          try {
            String temp = (String) WindowMain.this.listServerClients
                .getSelectedValue();
            // we add the request only if it hasn't been made and if we're
            // not already connected to that user
            if ( !WindowMain.this.users.contains( temp ) ) {
              if ( !WindowMain.this.userConnectionRequests.contains( temp ) )
                WindowMain.this.userConnectionRequests.add( temp );
            } else {
              UserDialog.showError(
                  String.format( "Already connected to: '%s'", temp ),
                  "Duplicate user name" );
            }
          }
          catch (Exception ignore) {}
        }
      }
      catch (Exception whatTheHell) {
        UserDialog.userTooStupidDialog( "ControlWindow on Mouse Click" );
      }
    }

    @Override
    public void mouseEntered( MouseEvent e ) {}

    @Override
    public void mouseExited( MouseEvent e ) {}

    @Override
    public void mousePressed( MouseEvent e ) {}

    @Override
    public void mouseReleased( MouseEvent e ) {}

  }

  private class ListenerServerEvents extends UnicastRemoteObject implements
      ServerEventListener {

    protected ListenerServerEvents() throws RemoteException{
      super();
    }

    private static final long serialVersionUID = -6496391112433937723L;

    @Override
    public void userConnected( final String newUser ) throws RemoteException {
      if ( !newUser.equals( WindowMain.this.myName ) )
        WindowMain.this.listServerClients.addElement( newUser );
    }

    @Override
    public void userDisconnected( final String disconnectedUser )
        throws RemoteException {
      if ( !disconnectedUser.equals( WindowMain.this.myName ) )
        WindowMain.this.listServerClients.removeElement( disconnectedUser );
    }

    @Override
    public void serverShutingDown() throws RemoteException {
      WindowMain.this.listServerClients.removeAllElements();
      UserDialog.showError( "Server shutdown, no new connections are possible",
          "Server Shutdown" );
      SwingUtilities.invokeLater( new Runnable() {
        public void run() {
          WindowMain.this.buttonServerConnect.setEnabled( true );
          WindowMain.this.buttonServerDisconnect.setEnabled( false );
        }
      } );
    }

    @Override
    public synchronized void periodicUpdate( final Collection<String> users )
        throws RemoteException {
      WindowMain.this.listServerClients.removeAllElements();
      users.remove( WindowMain.this.myName );
      for (String user : users )
        WindowMain.this.listServerClients.addElement( user );
    }
  }

  /*
   * (non-Javadoc)
   * @see crat.ControlGUI#start()
   */
  @Override
  public void start() {

    // we create the GUI
    SwingUtilities.invokeLater( new Runnable() {
      @Override
      public void run() {
        try {
          WindowMain.this.setLayout();
        }
        catch (Exception whatTheHell) {
          UserDialog.userTooStupidDialog( "Create Control Window" );
        }
      }
    } );
  }

  @Override
  public String getConnectionRequest() throws InterruptedException {
    return this.userConnectionRequests.poll( 1, TimeUnit.SECONDS );
  }

  @Override
  public void addUser( final String toAdd ) {
    try {
      if ( this.users.addIfAbsent( toAdd ) ) {
        this.listConnectedUsers.addElement( toAdd );
      };
    }
    catch (NullPointerException nullPointer) {
      UserDialog.nullPointerDialog( "Control Window AddUser" );
    }
    catch (Exception whatTheHell) {
      UserDialog.userTooStupidDialog( "ControlWindow AddUser" );
    }
  }

  @Override
  public void close() {
    SwingUtilities.invokeLater( new Runnable() {
      @Override
      public void run() {
        WindowMain.this.frame.dispose();
      }
    } );

    try {
      this.network.close();
    }
    catch (Exception ignore) {}

  }

  @Override
  public void removeUser( String toRemove ) {
    this.users.remove( toRemove );
    this.listConnectedUsers.removeElement( toRemove );
  }

  @Override
  public String getDisconnectRequest() throws InterruptedException {
    return this.userDisconnectRequests.poll( 1, TimeUnit.SECONDS );
  }
}
