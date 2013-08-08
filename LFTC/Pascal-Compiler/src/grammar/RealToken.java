package grammar;

public class RealToken extends Token {

  public final double realValue;

  public RealToken(Token tempToken) throws NumberFormatException {
    super(tempToken.type, tempToken.content, tempToken.line, tempToken.column);
    this.realValue = Double.parseDouble(tempToken.content);
  }

}
