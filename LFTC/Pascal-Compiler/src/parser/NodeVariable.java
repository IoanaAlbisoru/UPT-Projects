package parser;

import grammar.TokenType;
import parser.errors.SynthaxError;

class NodeVariable extends AbstractSynthaxNode {

  public NodeVariable(Parser parser) {
    super(parser);
  }

  @Override
  public void check() throws SynthaxError {
    this.expect(TokenType.IDENTIFIER);

    if (this.accept(TokenType.SQUARE_BRACKET_OPEN)) {
      new NodeVarExpression(this.parser).check();
      this.expect(TokenType.SQUARE_BRACKET_CLOSE);
    }

    if (this.accept(TokenType.DOT))
      this.expect(TokenType.IDENTIFIER);
  }

}
