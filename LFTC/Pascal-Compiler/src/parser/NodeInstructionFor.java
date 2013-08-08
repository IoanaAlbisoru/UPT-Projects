package parser;

import grammar.TokenType;
import parser.errors.SynthaxError;

public class NodeInstructionFor extends AbstractSynthaxNode {

  public NodeInstructionFor(Parser parser) {
    super(parser);
  }

  @Override
  public void check() throws SynthaxError {
    this.expect(TokenType.FOR_KW);
    new NodeVariable(this.parser).check();
    this.expect(TokenType.ASSIGN);
    new NodeVarExpression(this.parser).check();

    if (this.accept(TokenType.DOWNTO_KW) || this.accept(TokenType.TO_KW)) {
      // nothing;
    } else
      throw new SynthaxError("Expected direction: ", this.parser.getCurrentToken());

    new NodeVarExpression(this.parser).check();
    // if the next token isn't the do KW then we must check for an expression
    if (this.parser.getCurrentToken().type != TokenType.DO_KW)
      new NodeVarExpression(this.parser).check();

    this.expect(TokenType.DO_KW);
    new NodeInstruction(this.parser).check();
  }
}
