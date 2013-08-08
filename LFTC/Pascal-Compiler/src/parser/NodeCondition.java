package parser;

import grammar.TokenType;
import parser.errors.SynthaxError;

class NodeCondition extends AbstractSynthaxNode {

  public NodeCondition(Parser parser) {
    super(parser);
  }

  @Override
  public void check() throws SynthaxError {
    this.accept(TokenType.NOT_KW);
    new NodeLogicalExpression(this.parser).check();
  }

}
