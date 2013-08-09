package crat.client;

/**
 * This interface models the things that a very simple GUI that takes user input
 * in form of text; and displays text
 */
public interface UserGUI {
  /**
   * @param msg
   *          the string that is to be displayed
   */
  void display( String msg );

  /**
   * @return returns the input from the user. This function is a blocking
   *         function. It will return only when there's user input or when it
   *         has been interrupted
   * @throws InterruptedException
   */
  String getUserInput() throws InterruptedException;

  void close();

  void start();

  /**
   * if the GUI is hidden then it makes it visible, if it already is visible it
   * brings it to the foreground
   */
  void show();

  boolean isVisible();

}
