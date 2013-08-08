package testProxy;

class MockRemoteObject implements MockRemoteInterface {

  @SuppressWarnings("unused")
  private static final long serialVersionUID = 3398099810126529572L;
  public static int RETURN_VALUE = 10;

  @Override
  public String methodWithReturn(final String string, final int integer) {
    return string + integer;
  }

  @Override
  public void methodWithNoReturn() {
    return;
  }

  @Override
  public int methodWithPrimitiveReturn() {
    return MockRemoteObject.RETURN_VALUE;
  }

}
