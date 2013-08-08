package parser;

import grammar.TokenType;
import parser.errors.SynthaxError;

class NodeElement extends AbstractSynthaxNode {

  public NodeElement(Parser parser) {
    super(parser);
    // TODO Auto-generated constructor stub
  }

  @Override
  public void check() throws SynthaxError {
    if (!this.accept(TokenType.STRING_CONST))
      new NodeVarExpression(this.parser).check();
  }

}
