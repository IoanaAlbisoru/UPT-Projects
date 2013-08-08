package parser;

import grammar.TokenType;
import parser.errors.SynthaxError;

class NodeVarExpressionList extends AbstractSynthaxNode {

  public NodeVarExpressionList(Parser parser) {
    super(parser);
  }

  @Override
  public void check() throws SynthaxError {
    new NodeVarExpression(this.parser).check();
    if (this.accept(TokenType.COMMA))
      this.check();

  }

}
