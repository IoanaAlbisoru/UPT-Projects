package byteSendReceive;

import java.io.IOException;
import java.io.InputStream;
import java.io.Serializable;
import java.net.ServerSocket;
import java.net.Socket;

import commons.Address;
import commons.ByteUtils;

public class ByteReceiver implements Serializable {
  private static final long serialVersionUID = 8118827135639251911L;
  // private ServerSocket srvS;
  // private Socket s;
  // private InputStream iStr;

  private final Address myAddr;

  public ByteReceiver(final Address theAddr) {
    this.myAddr = theAddr;
    try {
      // srvS = new ServerSocket( myAddr.port(),1000 );
      // System.out.println( "Receiver Serversocket:" + srvS );
    } catch (final Exception e) {
      System.out.println("Error opening server socket");
    }
  }

  public byte[] receive() {
    int val;
    final byte[] len = new byte[] { 0, 0, 0, 0 };
    byte buffer[] = null;
    try {
      len[0] = 0;
      len[1] = 0;
      len[2] = 0;
      len[3] = 0;
      final ServerSocket srvS = new ServerSocket(this.myAddr.port(), 1000);
      final Socket s = srvS.accept();
      // System.out.println( "Receiver accept: Socket" + s );
      final InputStream iStr = s.getInputStream();
      iStr.read(len);
      val = ByteUtils.byteArrayToInt(len);
      buffer = new byte[val];
      iStr.read(buffer);

      iStr.close();
      s.close();
      srvS.close();

    } catch (final IOException e) {
      System.out.println("IOException in receive_transform_and_feedback");
    }
    return buffer;
  }

  @Override
  protected void finalize() throws Throwable {
    super.finalize();
    // srvS.close();
  }
}
