package parser;

import grammar.TokenType;
import parser.errors.SynthaxError;

public class NodeInstructionRead extends AbstractSynthaxNode {

  public NodeInstructionRead(Parser parser) {
    super(parser);
  }

  @Override
  public void check() throws SynthaxError {
    this.expect(TokenType.READ_KW);
    this.expect(TokenType.ROUND_BRACKET_OPEN);
    new NodeVariableList(this.parser).check();
    this.expect(TokenType.ROUND_BRACKET_CLOSE);

  }

}
