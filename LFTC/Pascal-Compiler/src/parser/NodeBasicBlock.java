package parser;

import grammar.TokenType;
import parser.errors.SynthaxError;

public class NodeBasicBlock extends AbstractSynthaxNode {

  public NodeBasicBlock(Parser parser) {
    super(parser);
  }

  @Override
  public void check() throws SynthaxError {
    this.expect(TokenType.BEGIN_KW);
    new NodeInstructionList(this.parser).check();
    this.accept(TokenType.SEMICOLON);
    this.expect(TokenType.END_KW);
  }

}
