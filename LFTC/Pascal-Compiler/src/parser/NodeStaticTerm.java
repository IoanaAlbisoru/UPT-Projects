package parser;

import grammar.TokenType;
import parser.errors.SynthaxError;

class NodeStaticTerm extends AbstractSynthaxNode {

  public NodeStaticTerm(Parser parser) {
    super(parser);
  }

  @Override
  public void check() throws SynthaxError {
    new NodeStaticFactor(this.parser).check();

    if (this.accept(TokenType.DIVIDE) || this.accept(TokenType.MULTIPLY) || this.accept(TokenType.DIV_KW) || this.accept(TokenType.MOD_KW))
      this.check();
    else {
    }
  }
}
