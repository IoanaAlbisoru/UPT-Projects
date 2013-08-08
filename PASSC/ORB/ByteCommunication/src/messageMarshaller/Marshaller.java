package messageMarshaller;

public class Marshaller {
  public byte[] marshal(final Message theMsg) {
    final String m = "  " + theMsg.sender + ":" + theMsg.data;
    byte b[] = new byte[m.length()];
    b = m.getBytes();
    b[0] = (byte) m.length();
    return b;
  }

  public Message unmarshal(final byte[] byteArray) {
    final String msg = new String(byteArray);
    final String sender = msg.substring(1, msg.indexOf(":"));
    final String m = msg.substring(msg.indexOf(":") + 1, msg.length() - 1);
    return new Message(sender, m);
  }

}
