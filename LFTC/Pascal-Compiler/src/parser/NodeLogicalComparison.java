package parser;

import grammar.TokenType;
import parser.errors.SynthaxError;

public class NodeLogicalComparison extends AbstractSynthaxNode {

  public NodeLogicalComparison(Parser parser) {
    super(parser);
  }

  @Override
  public void check() throws SynthaxError {

    if (this.accept(TokenType.ROUND_BRACKET_OPEN)) {
      new NodeCondition(this.parser).check();
      this.expect(TokenType.ROUND_BRACKET_CLOSE);
    } else {
      new NodeVarExpression(this.parser).check();
      if (!this.comparisonOperator())
        throw new SynthaxError("Expected logical operator", this.parser.getCurrentToken());
      new NodeVarExpression(this.parser).check();
    }
  }

  private boolean comparisonOperator() {
    boolean result = this.accept(TokenType.GREATER) || this.accept(TokenType.GREATER_OR_EQUAL) || this.accept(TokenType.LESS)
        || this.accept(TokenType.LESS_OR_EQUAL) || this.accept(TokenType.EQUAL) || this.accept(TokenType.DIFFERENT);
    return result;
  }

}
