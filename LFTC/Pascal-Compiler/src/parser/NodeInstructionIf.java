package parser;

import grammar.TokenType;
import parser.errors.SynthaxError;

public class NodeInstructionIf extends AbstractSynthaxNode {

  public NodeInstructionIf(Parser parser) {
    super(parser);
  }

  @Override
  public void check() throws SynthaxError {
    this.expect(TokenType.IF_KW);
    new NodeCondition(this.parser).check();
    this.expect(TokenType.THEN_KW);
    new NodeInstruction(this.parser).check();

    if (this.accept(TokenType.ELSE_KW))
      new NodeInstruction(this.parser).check();
  }

}
