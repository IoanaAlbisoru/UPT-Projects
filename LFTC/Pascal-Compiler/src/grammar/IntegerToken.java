package grammar;

public class IntegerToken extends Token {
  public final int integerValue;

  public IntegerToken(Token t) throws NumberFormatException {
    super(t.type, t.content, t.line, t.column);
    int temp = Integer.parseInt(t.content);
    this.integerValue = temp;
  }

}
