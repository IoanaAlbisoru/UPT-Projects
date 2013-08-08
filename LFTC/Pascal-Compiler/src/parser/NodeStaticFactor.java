package parser;

import grammar.TokenType;
import parser.errors.SynthaxError;

class NodeStaticFactor extends AbstractSynthaxNode {

  public NodeStaticFactor(Parser parser) {
    super(parser);
    // TODO Auto-generated constructor stub
  }

  @Override
  public void check() throws SynthaxError {
    if (this.accept(TokenType.IDENTIFIER)) {
    } else
      new NodeConstant(this.parser).check();
  }

}
