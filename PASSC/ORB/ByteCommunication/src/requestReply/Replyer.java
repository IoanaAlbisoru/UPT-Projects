package requestReply;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.ServerSocket;
import java.net.Socket;
import java.net.SocketTimeoutException;

import commons.Address;
import commons.ByteUtils;

public class Replyer {
  private ServerSocket srvS;
  private Socket s;
  private InputStream iStr;
  private OutputStream oStr;
  private final Address myAddr;

  public Replyer(final Address theAddr) {
    this.myAddr = theAddr;
    try {
      this.srvS = new ServerSocket(this.myAddr.port(), 1000);
      this.srvS.setSoTimeout(2000);
      // System.out.println( "Replyer Serversocket:" + srvS );
    } catch (final Exception e) {
      System.out.println("Error opening server socket");
    }
  }

  public void receive_transform_and_send_feedback(final ByteStreamTransformer t) throws SocketTimeoutException {
    int val;
    byte buffer[] = null;
    final byte[] len = new byte[] { 0, 0, 0, 0 };
    try {
      len[0] = 0;
      len[1] = 0;
      len[2] = 0;
      len[3] = 0;
      this.s = this.srvS.accept();
      // System.out.println( "Replyer accept: Socket" + s );
      this.iStr = this.s.getInputStream();
      this.iStr.read(len);
      val = ByteUtils.byteArrayToInt(len);
      buffer = new byte[val];
      this.iStr.read(buffer);

      final byte[] data = t.transform(buffer);

      this.oStr = this.s.getOutputStream();
      this.oStr.write(data);
      this.oStr.flush();
      this.oStr.close();
      this.iStr.close();
      this.s.close();

    } catch (final SocketTimeoutException timeoutEx) {
      throw timeoutEx;
    } catch (final IOException e) {
      System.out.println("IOException in receive_transform_and_feedback");
    }

  }

  @Override
  protected void finalize() throws Throwable {
    super.finalize();
    this.srvS.close();
  }
}
