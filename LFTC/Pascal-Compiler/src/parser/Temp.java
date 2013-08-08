package parser;

import grammar.TokenType;
import parser.errors.SynthaxError;

public enum Temp {
  EPSILON {
    @Override
    public void checkConsistency() {
    }
  };

  public static Parser parser;

  public abstract void checkConsistency();

  protected boolean accept(TokenType nextToken) {
    if (nextToken == Temp.parser.getCurrentToken().type) {
      Temp.parser.nextToken();
      return true;
    } else
      return false;
  }

  protected boolean expect(TokenType type) throws SynthaxError {
    if (this.accept(type))
      return true;
    else
      throw new SynthaxError("Error @: " + Temp.parser.getCurrentToken().column + Temp.parser.getCurrentToken().line,
          Temp.parser.getCurrentToken());
  }
}
