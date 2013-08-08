package parser;

import grammar.TokenType;
import parser.errors.SynthaxError;

class NodeInstructionRepeat extends AbstractSynthaxNode {

  public NodeInstructionRepeat(Parser parser) {
    super(parser);
  }

  @Override
  public void check() throws SynthaxError {
    this.expect(TokenType.REPEAT_KW);
    new NodeInstruction(this.parser).check();
    this.expect(TokenType.UNTIL_KW);
    new NodeCondition(this.parser).check();
  }

}
