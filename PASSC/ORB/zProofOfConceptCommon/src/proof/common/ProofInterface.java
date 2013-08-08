package proof.common;

import remoting.RemoteInterface;

public interface ProofInterface extends RemoteInterface {
  void compute(HugeObjI obj);
}
