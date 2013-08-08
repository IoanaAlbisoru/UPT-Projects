package testSpanishCastleMagic;

import junit.framework.Assert;
import junit.framework.TestCase;
import remoting.Utils;

public class TestUtils extends TestCase {
  public void testUtils() {

    try {
      final MockTestObject testStr = new MockTestObject();
      final byte[] a1 = Utils.toBytes(testStr);

      final byte[] a2 = new byte[a1.length - 4];

      System.arraycopy(a1, 4, a2, 0, a2.length);

      final Object result = Utils.toObject(a2);
      Assert.assertEquals(testStr, result);

      final int number = 577;
      final byte[] a = Utils.intToByteArray(number);
      final int b = Utils.byteArrayToInt(a);
      Assert.assertEquals(number, b);

    } catch (final Exception e) {
      Assert.fail(e.getMessage());
    }
  }
}
