package proof.server;

import proof.common.HugeObjI;
import proof.common.ProofInterface;
import registry.Entry;
import remoting.SpanishCastleMagic;

public class ProofServer implements ProofInterface {

  @SuppressWarnings("unused")
  private static final long serialVersionUID = -1489937392042865406L;

  @Override
  public void compute(final HugeObjI obj) {
    obj.something();
  }

  public static void main(final String args[]) {
    final ProofInterface server = new ProofServer();
    SpanishCastleMagic.bind("Proof", server, new Entry("127.0.0.1", 2307));
    while (true) {
      try {
        Thread.sleep(1000);
      } catch (final InterruptedException e) {
      }
    }
  }

}
