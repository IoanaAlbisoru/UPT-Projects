package crat.client;

/**
 * A user can expect to receive messages through instances of classes that
 * implement this instance. It was designed for the sole purpose of sending
 * messages between different clients. When a client uses objects of this type
 * he is under the assumption that he will receive at some point or another
 * messages through here
 */
interface ReceiveMessagesI {

  /**
   * @return A String that someone else has made available before hand. Most
   *         likely from different thread. This method is blocking, it will wait
   *         until a String is available. users of this method should not count
   *         on the fact that it times out
   * @throws InterruptedException
   *           if the waiting for a new message is interrupted
   */
  String getMessage() throws InterruptedException;

  /**
   * This method indicates whether or not the user on the other end is still
   * available to send messages. This method does NOT block!
   * 
   * @return true if the user on the other end will no longer send any messages
   */
  boolean isDisconnected();
}
