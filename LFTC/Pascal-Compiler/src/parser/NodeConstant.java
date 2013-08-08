package parser;

import grammar.TokenType;
import parser.errors.SynthaxError;

public class NodeConstant extends AbstractSynthaxNode {

  public NodeConstant(Parser parser) {
    super(parser);
  }

  @Override
  public void check() throws SynthaxError {
    if (this.accept(TokenType.NUMBER_BASE) || this.accept(TokenType.NUMBER_REAL) || this.accept(TokenType.NUMBER_INTEGER)
        || this.accept(TokenType.CHAR_CONST)) {
    } else
      throw new SynthaxError("", this.parser.getCurrentToken());
  }

}
