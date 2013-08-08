package remoting;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.ObjectOutputStream;
import java.nio.ByteBuffer;

public class Utils {
  /**
   * @param obj
   *          the object to convert to bytes
   * @return a byte array with the serialized object which contains the length
   *         of the serialized object on the first position
   * @throws IOException
   */
  public static byte[] toBytes(final Object obj) throws IOException {
    final ByteArrayOutputStream bos = new ByteArrayOutputStream();
    final ObjectOutputStream oos = new ObjectOutputStream(bos);
    oos.writeObject(obj);
    oos.flush();
    oos.close();
    bos.close();

    final byte[] buffer = bos.toByteArray();
    final int temp = buffer.length;

    final byte[] data = new byte[temp + 4];

    System.arraycopy(buffer, 0, data, 4, buffer.length);
    final byte[] len = Utils.intToByteArray(temp);
    data[0] = len[0];
    data[1] = len[1];
    data[2] = len[2];
    data[3] = len[3];
    return data;
  }

  public static byte[] toBytesNoLength(final Object obj) throws IOException {
    final ByteArrayOutputStream bos = new ByteArrayOutputStream();
    final ObjectOutputStream oos = new ObjectOutputStream(bos);
    oos.writeObject(obj);
    oos.flush();
    oos.close();
    bos.close();
    final byte[] buffer = bos.toByteArray();
    // int temp = buffer.length;
    // byte[] data = new byte[buffer.length];
    // System.arraycopy( buffer, 0, data, 0, buffer.length );
    return buffer;
  }

  public static Object toObject(final byte[] bytes) throws IOException, ClassNotFoundException {
    Object object = null;
    object = new java.io.ObjectInputStream(new java.io.ByteArrayInputStream(bytes)).readObject();
    return object;
  }

  public static final byte[] intToByteArray(final int value) {
    final ByteBuffer b = ByteBuffer.allocate(4);
    b.putInt(value);
    return b.array();
  }

  public static final int byteArrayToInt(final byte[] value) {
    final ByteBuffer b = ByteBuffer.allocate(4);
    b.put(value);
    return b.getInt(0);
  }
}
