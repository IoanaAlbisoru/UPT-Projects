package parser;

import grammar.TokenType;
import parser.errors.SynthaxError;

class NodeInstructionPrint extends AbstractSynthaxNode {

  public NodeInstructionPrint(Parser parser) {
    super(parser);
  }

  @Override
  public void check() throws SynthaxError {
    this.expect(TokenType.PRINT_KW);
    this.expect(TokenType.ROUND_BRACKET_OPEN);
    new NodeElementList(this.parser).check();
    this.expect(TokenType.ROUND_BRACKET_CLOSE);
  }

}
