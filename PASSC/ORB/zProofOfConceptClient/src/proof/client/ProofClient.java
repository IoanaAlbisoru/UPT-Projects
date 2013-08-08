package proof.client;

import proof.common.HugeObjI;
import proof.common.ProofInterface;
import registry.Entry;
import remoting.SpanishCastleMagic;

public class ProofClient {
  public static void main(final String args[]) {
    final ProofInterface server = (ProofInterface) SpanishCastleMagic.lookup("Proof", new Entry("127.0.0.1", 2307));

    if (server == null) {
      System.exit(0);
    }
    final HugeObjI huge = new Huge();
    server.compute(huge);
  }
}
