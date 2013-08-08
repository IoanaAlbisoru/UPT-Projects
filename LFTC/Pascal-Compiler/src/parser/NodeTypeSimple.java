package parser;

import grammar.TokenType;
import parser.errors.SynthaxError;

public class NodeTypeSimple extends AbstractSynthaxNode {

  public NodeTypeSimple(Parser parser) {
    super(parser);
    // TODO Auto-generated constructor stub
  }

  @Override
  public void check() throws SynthaxError {
    if (this.accept(TokenType.INTEGER_KW) || this.accept(TokenType.REAL_KW) || this.accept(TokenType.CHAR_KW)) {

    } else
      throw new SynthaxError("Expected a proper type", this.parser.getCurrentToken());
  }

}
