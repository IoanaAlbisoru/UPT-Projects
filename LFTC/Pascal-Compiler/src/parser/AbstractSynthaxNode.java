package parser;

import parser.errors.SynthaxError;
import grammar.TokenType;

abstract class AbstractSynthaxNode {
  protected final Parser parser;

  public AbstractSynthaxNode(Parser parser) {
    this.parser = parser;
  }

  public abstract void check() throws SynthaxError;

  protected boolean accept(TokenType nextToken) {
    if (nextToken == TokenType.ERROR)
      throw new RuntimeException("Source file has even Lexical errors, wth dude?\nline:"
          + this.parser.getCurrentToken().line + "\ncolumn: " + this.parser.getCurrentToken().column);

    if (nextToken == this.parser.getCurrentToken().type) {
      this.parser.nextToken();
      return true;
    } else
      return false;
  }

  protected boolean expect(TokenType type) throws SynthaxError {
    if (this.accept(type))
      return true;
    else
      throw new SynthaxError("Expected token: " + type + "\n but was: " + this.parser.getCurrentToken().type,
          this.parser.getCurrentToken());
  }

}
