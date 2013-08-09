package crat.client;

import java.awt.BorderLayout;
import java.awt.Dimension;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.concurrent.ArrayBlockingQueue;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.TimeUnit;

import javax.swing.*;

public class WindowUserChat implements UserGUI {
  private JTextField userInput;
  private JTextArea messages;
  private JFrame frame;

  private final BlockingQueue<String> messageQueue = new ArrayBlockingQueue<String>(
      20 );

  private final String remoteUserNickName;
  private final String myNickName;

  public WindowUserChat( String myName, String name ){
    this.remoteUserNickName = name;
    this.myNickName = myName;
  }

  private void setLayout() {
    this.userInput = new JTextField();
    this.userInput.setPreferredSize( new Dimension( 300,30 ) );
    this.userInput.setVisible( true );

    // we add the action listener for when we hit "Enter" into the box; its an
    // anonymous inner class in which all operation are performed in the swing
    // thread
    this.userInput.addActionListener( new ActionListener() {
      @Override
      public void actionPerformed( ActionEvent e ) {

        SwingUtilities.invokeLater( new Runnable() {
          @Override
          public void run() {
            WindowUserChat.this.display( String.format( "%s(me): %s",
                WindowUserChat.this.myNickName,
                WindowUserChat.this.userInput.getText() ) );
            try {
              WindowUserChat.this.messageQueue
                  .add( WindowUserChat.this.userInput.getText() );
            }
            catch (Exception ex) {}
            WindowUserChat.this.userInput.setText( "" );
          }
        } );

      }
    } );

    this.messages = new JTextArea();
    this.messages.setVisible( true );
    this.messages.setEditable( false );
    this.messages.setLineWrap( true );
    this.messages.setWrapStyleWord( true );

    JScrollPane scrollPane = new JScrollPane( this.messages,
        ScrollPaneConstants.VERTICAL_SCROLLBAR_AS_NEEDED,
        ScrollPaneConstants.HORIZONTAL_SCROLLBAR_NEVER );
    // it is important the that scrollPane has preferred dimension, not the
    // textArea, otherwise the scrollBar won't work.
    scrollPane.setPreferredSize( new Dimension( 300,200 ) );

    this.frame = new JFrame( "" + this.remoteUserNickName );
    this.frame.setDefaultCloseOperation( WindowConstants.HIDE_ON_CLOSE );
    this.frame.setResizable( false );
    this.frame.getContentPane().add( this.userInput, BorderLayout.SOUTH );
    this.frame.getContentPane().add( scrollPane, BorderLayout.NORTH );

    // THIS IS FREAKING UGLY!!!
    this.frame.setLocationRelativeTo( WindowMain.getReference().getFrame() );

    this.frame.pack();
    this.frame.setVisible( true );
  }

  private final String newline = "\n";

  /*
   * (non-Javadoc)
   * @see crat.UserGUI#display(crat.Message)
   * display() needs to be synchronized because both this thread and the thread
   * that holds the remote object may want to display a message at the same time
   */
  @Override
  public void display( final String msg ) {
    SwingUtilities.invokeLater( new Runnable() {

      @Override
      public void run() {
        WindowUserChat.this.messages.append( msg + WindowUserChat.this.newline );
        WindowUserChat.this.messages
            .setCaretPosition( WindowUserChat.this.messages.getText().length() );
      }
    } );

  }

  @Override
  public String getUserInput() throws InterruptedException {
    return this.messageQueue.poll( 1, TimeUnit.SECONDS );
  }

  @Override
  public void close() {
    SwingUtilities.invokeLater( new Runnable() {
      @Override
      public void run() {
        WindowUserChat.this.frame.dispose();
      }
    } );
  }

  @Override
  public void start() {
    try {
      SwingUtilities.invokeAndWait( new Runnable() {
        @Override
        public void run() {
          WindowUserChat.this.setLayout();
        }
      } );
    }
    catch (Exception e) {}
  }

  @Override
  public void show() {
    WindowUserChat.this.frame.setVisible( true );
  }

  @Override
  public boolean isVisible() {
    return this.frame.isVisible();
  }

}
