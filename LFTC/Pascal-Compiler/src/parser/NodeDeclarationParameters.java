package parser;

import grammar.TokenType;
import parser.errors.SynthaxError;

public class NodeDeclarationParameters extends AbstractSynthaxNode {

  public NodeDeclarationParameters(Parser parser) {
    super(parser);
  }

  @Override
  public void check() throws SynthaxError {
    this.accept(TokenType.VAR_KW);
    new NodeDeclarationVariable(this.parser).check();
    if (this.accept(TokenType.COMMA) || this.accept(TokenType.SEMICOLON))
      this.check();
  }

}
