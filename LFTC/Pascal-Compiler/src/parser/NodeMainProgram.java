package parser;

import grammar.TokenType;
import parser.errors.SynthaxError;

class NodeMainProgram extends AbstractSynthaxNode {

  public NodeMainProgram(Parser parser) {
    super(parser);
  }

  @Override
  public void check() throws SynthaxError {
    this.expect(TokenType.PROGRAM_KW);
    this.expect(TokenType.IDENTIFIER);
    this.expect(TokenType.SEMICOLON);
    new NodeBlock(this.parser).check();
  }

}
