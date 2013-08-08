package testProxy;

import remoting.RemoteInterface;

interface MockRemoteInterface extends RemoteInterface {
  String methodWithReturn(String string, int integer);

  void methodWithNoReturn();

  int methodWithPrimitiveReturn();
}
