package parser;

import grammar.TokenType;
import parser.errors.SynthaxError;

public class NodeInstructionAssign extends AbstractSynthaxNode {

  public NodeInstructionAssign(Parser parser) {
    super(parser);
  }

  @Override
  public void check() throws SynthaxError {
    new NodeVariable(this.parser).check();
    this.expect(TokenType.ASSIGN);
    new NodeVarExpression(this.parser).check();
  }

}
