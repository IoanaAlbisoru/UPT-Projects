package parser;

import grammar.TokenType;
import parser.errors.SynthaxError;

public class NodeInstructionList extends AbstractSynthaxNode {

  public NodeInstructionList(Parser parser) {
    super(parser);
  }

  @Override
  public void check() throws SynthaxError {

    if (this.parser.getCurrentToken().type != TokenType.END_KW)
      new NodeInstruction(this.parser).check();

    if (this.accept(TokenType.SEMICOLON))
      this.check();
  }

}
