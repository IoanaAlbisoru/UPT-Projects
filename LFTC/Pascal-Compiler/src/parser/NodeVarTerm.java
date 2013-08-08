package parser;

import grammar.TokenType;
import parser.errors.SynthaxError;

public class NodeVarTerm extends AbstractSynthaxNode {

  public NodeVarTerm(Parser parser) {
    super(parser);
  }

  @Override
  public void check() throws SynthaxError {
    new NodeVarFactor(this.parser).check();

    if (this.accept(TokenType.DIVIDE) || this.accept(TokenType.MULTIPLY) || this.accept(TokenType.DIV_KW) || this.accept(TokenType.MOD_KW))
      this.check();
    else {
    }
  }

}
