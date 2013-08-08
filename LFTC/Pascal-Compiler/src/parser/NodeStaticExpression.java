package parser;

import grammar.TokenType;
import parser.errors.SynthaxError;

class NodeStaticExpression extends AbstractSynthaxNode {

  public NodeStaticExpression(Parser parser) {
    super(parser);
  }

  @Override
  public void check() throws SynthaxError {
    new NodeStaticTerm(this.parser).check();
    if (this.accept(TokenType.PLUS) || this.accept(TokenType.MINUS))
      this.check();
    else {
      // fall through;
    }

  }

}
