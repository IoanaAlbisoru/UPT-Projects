package crat.client;

import java.util.concurrent.atomic.AtomicBoolean;

import javax.swing.JFrame;
import javax.swing.JOptionPane;
import javax.swing.SwingUtilities;

/**
 * This class contains only static methods that display simple modal dialogs
 * This class is state-less!
 */
public class UserDialog {

  public static void quitDialog( String msg, String caption ) {
    Object[] options = { "Continue", "Quit" };
    int n = UserDialog.showOptionDialog( msg, caption, options, options[0],
        null );

    if ( n == 1 )
      System.exit( 0 );
  }

  public static void nullPointerDialog( final String where ) {
    SwingUtilities.invokeLater( new Runnable() {
      @Override
      public void run() {
        UserDialog.showError( "NullPointerException, seriously?", where );
      }
    } );
  }

  public static void userTooStupidDialog( final String where ) {
    SwingUtilities.invokeLater( new Runnable() {
      @Override
      public void run() {
        UserDialog.showError( "User too stupid Exception.", where );
      }
    } );
  }

  public static String getUserName() {
    String temp = UserDialog
        .showInputDialog( "Please enter desired user name:",
            "Enter User Name.", "UserName", null );
    return temp;
  }

  public static String showInputDialog( String msg, String where,
      String defaultText, JFrame parent ) {
    String temp = (String) JOptionPane.showInputDialog( null, msg, where,
        JOptionPane.PLAIN_MESSAGE, null, null, defaultText );

    return temp;
  }

  public static int showOptionDialog( String msg, String where,
      Object[] options, Object defaultOpt, JFrame parent ) {
    return JOptionPane.showOptionDialog( null, msg, where,
        JOptionPane.YES_NO_OPTION, JOptionPane.QUESTION_MESSAGE, null, options,
        defaultOpt );
  }

  public static void showError( final String msg, final String where ) {
    SwingUtilities.invokeLater( new Runnable() {
      @Override
      public void run() {
        JFrame parent = null;
        try {
          parent = WindowMain.getReference().getFrame();
        }
        catch (Exception ignore) {}
        JOptionPane.showMessageDialog( parent, msg, where,
            JOptionPane.ERROR_MESSAGE );
      }
    } );

  }

  public static synchronized boolean connectionRequest( final String user ) {
    // Custom button text
    final AtomicBoolean result = new AtomicBoolean( false );
    final AtomicBoolean isSet = new AtomicBoolean( false );

    SwingUtilities.invokeLater( new Runnable() {

      @Override
      public void run() {
        Object[] options = { "Accept", "Decline" };
        int n = JOptionPane.showOptionDialog( WindowMain.getReference()
            .getFrame(), "User '" + user + "' is trying to connect, accept?",
            "Connection Request", JOptionPane.YES_NO_CANCEL_OPTION,
            JOptionPane.QUESTION_MESSAGE, null, options, options[0] );
        isSet.set( true );
        result.set( n == 0 );

      }
    } );

    while (!isSet.get()) {}
    return result.get();
  }

  public static void showMessage( final String msg, final String where ) {

    SwingUtilities.invokeLater( new Runnable() {
      @Override
      public void run() {
        JFrame parent = null;
        try {
          parent = WindowMain.getReference().getFrame();
        }
        catch (Exception ignore) {}

        JOptionPane.showMessageDialog( parent, msg, where,
            JOptionPane.INFORMATION_MESSAGE );
      }
    } );
  }
}
