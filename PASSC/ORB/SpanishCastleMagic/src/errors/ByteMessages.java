package errors;

public class ByteMessages {

  private static byte[] errorByteStream = new byte[] { 0, 0, 0, 4, -127, -127, -127, -127 };
  private static byte[] errorByteStreamNoLength = new byte[] { -127, -127, -127, -127 };

  private static byte[] succesByte = new byte[] { 0, 0, 0, 4, 127, 127, 127, 127 };

  // private static byte[] successByteNoLength = new byte[] { 127, 127, 127,
  // 127 };

  public static byte[] success() {
    return ByteMessages.succesByte;
  }

  public static byte[] failure() {
    return ByteMessages.errorByteStream;
  }

  public static boolean isError(final byte[] array) {
    try {
      for (int i = 0; i < ByteMessages.errorByteStreamNoLength.length; i++) {
        if (array[i] != ByteMessages.errorByteStreamNoLength[i]) {
          return false;
        }
      }
    } catch (final Exception e) {
      return false;
    }
    return true;
  }
}
