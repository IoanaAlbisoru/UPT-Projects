package parser;

import grammar.TokenType;
import parser.errors.SynthaxError;

public class NodeInstructionFunctionCall extends AbstractSynthaxNode {

  public NodeInstructionFunctionCall(Parser parser) {
    super(parser);
  }

  @Override
  public void check() throws SynthaxError {
    this.expect(TokenType.IDENTIFIER);
    if (this.accept(TokenType.ROUND_BRACKET_OPEN)) {
      new NodeVarExpressionList(this.parser).check();
      this.expect(TokenType.ROUND_BRACKET_CLOSE);
    }
  }

}
