package testSpanishCastleMagic;

public class MockTestObject implements MockTestRemoteInterface {
  @SuppressWarnings("unused")
  private static final long serialVersionUID = -7480811983534386277L;
  private final String string;
  private final int number;

  public MockTestObject() {
    this.string = "TESING";
    this.number = 2307;
  }

  @Override
  public String getString() {
    return this.string;
  }

  @Override
  public int getNumber() {
    return this.number;
  }

  @Override
  public boolean equals(final Object obj) {
    boolean result = false;
    final MockTestRemoteInterface temp = (MockTestRemoteInterface) obj;
    result = (this.number == temp.getNumber()) && this.string.equals(temp.getString());

    return result;
  }
}
