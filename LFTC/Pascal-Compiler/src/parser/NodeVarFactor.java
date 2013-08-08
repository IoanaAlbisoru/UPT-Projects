package parser;

import grammar.TokenType;
import parser.errors.SynthaxError;

public class NodeVarFactor extends AbstractSynthaxNode {

  public NodeVarFactor(Parser parser) {
    super(parser);
  }

  @Override
  public void check() throws SynthaxError {
    if (this.accept(TokenType.IDENTIFIER)) {

      // means we have a record
      if (this.accept(TokenType.DOT)) {
        this.expect(TokenType.IDENTIFIER);
        return;
      }

      // an array access;
      if (this.accept(TokenType.SQUARE_BRACKET_OPEN)) {
        new NodeVarExpression(this.parser).check();
        this.expect(TokenType.SQUARE_BRACKET_CLOSE);
        return;
      }

      // a function call;
      if (this.accept(TokenType.ROUND_BRACKET_OPEN)) {
        new NodeVarExpressionList(this.parser).check();
        this.expect(TokenType.ROUND_BRACKET_CLOSE);
        return;
      }
    } else // we have an expression put in brackets
    if (this.accept(TokenType.ROUND_BRACKET_OPEN)) {
      new NodeVarExpression(this.parser).check();
      this.expect(TokenType.ROUND_BRACKET_CLOSE);
    } else
      new NodeConstant(this.parser).check();
  }

}
