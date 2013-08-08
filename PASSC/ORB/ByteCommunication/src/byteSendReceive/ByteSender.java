package byteSendReceive;

import java.io.IOException;
import java.io.OutputStream;
import java.io.Serializable;
import java.net.Socket;

import commons.Address;

public class ByteSender implements Serializable {
  private static final long serialVersionUID = 2598687649139119327L;

  // private Socket s;
  // private OutputStream oStr;

  public void deliver(final Address theDest, final byte[] data) {
    try {
      final Socket s = new Socket(theDest.dest(), theDest.port());
      // System.out.println( "Sender: Socket" + s );
      final OutputStream oStr = s.getOutputStream();
      oStr.write(data);
      oStr.flush();
      oStr.close();
      s.close();
    } catch (final IOException e) {
      System.out.println("IOException in deliver");
    }
  }
}
