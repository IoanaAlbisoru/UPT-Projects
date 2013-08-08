package requestReply;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.Socket;

import commons.Address;
import commons.ByteUtils;

public class Requestor {

  private Socket s;
  private OutputStream oStr;
  private InputStream iStr;

  // @SuppressWarnings( "unused" )
  // private String myName;

  // public Requestor( String theName ){
  // myName = theName;
  // }

  public byte[] deliver_and_wait_feedback(final Address theDest, final byte[] data) {

    byte[] buffer = null;
    final byte[] len = new byte[] { 0, 0, 0, 0 };
    int val;
    try {
      len[0] = 0;
      len[1] = 0;
      len[2] = 0;
      len[3] = 0;
      this.s = new Socket(theDest.dest(), theDest.port());
      // System.out.println( "Requestor: Socket" + s );
      this.oStr = this.s.getOutputStream();
      this.oStr.write(data);
      this.oStr.flush();
      this.iStr = this.s.getInputStream();

      this.iStr.read(len);
      val = ByteUtils.byteArrayToInt(len);
      buffer = new byte[val];
      this.iStr.read(buffer);
      this.iStr.close();
      this.oStr.close();
      this.s.close();
    } catch (final IOException e) {
      System.out.println("IOException in deliver_and_wait_feedback");
    }
    return buffer;
  }

}
