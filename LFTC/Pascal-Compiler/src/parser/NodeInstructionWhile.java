package parser;

import grammar.TokenType;
import parser.errors.SynthaxError;

public class NodeInstructionWhile extends AbstractSynthaxNode {

  public NodeInstructionWhile(Parser parser) {
    super(parser);
    // TODO Auto-generated constructor stub
  }

  @Override
  public void check() throws SynthaxError {
    this.expect(TokenType.WHILE_KW);
    new NodeCondition(this.parser).check();
    this.expect(TokenType.DO_KW);
    new NodeInstruction(this.parser).check();
  }

}
