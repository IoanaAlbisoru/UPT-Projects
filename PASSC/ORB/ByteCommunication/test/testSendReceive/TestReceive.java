package testSendReceive;

import junit.framework.Assert;
import junit.framework.TestCase;
import registry.Entry;
import byteSendReceive.ByteReceiver;
import byteSendReceive.ByteSender;

import commons.Address;

public class TestReceive extends TestCase {

  public void testReceive() {
    final Address local = new Entry("127.0.0.1", 2307);

    final byte[] toSend = new byte[] { 0, 12, 13, 14 };
    toSend[0] = (byte) ((byte) toSend.length - 1);

    final ByteReceiver receiver = new ByteReceiver(local);
    final ByteSender sender = new ByteSender();

    new Thread(new Runnable() {
      @Override
      public void run() {
        sender.deliver(local, toSend);
      }
    }).start();

    try {
      Thread.sleep(500);
    } catch (final InterruptedException e) {
      Assert.fail("was interupted for some reasons");
    }

    final byte[] data = receiver.receive();

    for (int i = 0; i < data.length; i++) {
      Assert.assertEquals(data[i], toSend[i + 1]);
    }
  }
}
