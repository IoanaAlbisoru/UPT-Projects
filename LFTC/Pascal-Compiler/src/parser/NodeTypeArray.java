package parser;

import grammar.TokenType;
import parser.errors.SynthaxError;

public class NodeTypeArray extends AbstractSynthaxNode {

  public NodeTypeArray(Parser parser) {
    super(parser);
  }

  @Override
  public void check() throws SynthaxError {
    if (this.expect(TokenType.ARRAY_KW)) {
      this.expect(TokenType.SQUARE_BRACKET_OPEN);
      new NodeStaticExpression(this.parser).check();
      this.expect(TokenType.DOUBLEDOT);
      new NodeStaticExpression(this.parser).check();
      this.expect(TokenType.SQUARE_BRACKET_CLOSE);
      this.expect(TokenType.OF_KW);
      new NodeTypeSimple(this.parser).check();
    }
  }

}
