package parser;

import grammar.TokenType;
import parser.errors.SynthaxError;

public class NodeVarExpression extends AbstractSynthaxNode {

  public NodeVarExpression(Parser parser) {
    super(parser);
    // TODO Auto-generated constructor stub
  }

  @Override
  public void check() throws SynthaxError {
    new NodeVarTerm(this.parser).check();
    if (this.accept(TokenType.PLUS) || this.accept(TokenType.MINUS))
      this.check();
    else {
      // fall through;
    }

  }

}
