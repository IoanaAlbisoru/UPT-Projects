package parser;

import grammar.TokenType;
import parser.errors.SynthaxError;

public class NodeIdentifierList extends AbstractSynthaxNode {

  public NodeIdentifierList(Parser parser) {
    super(parser);
  }

  @Override
  public void check() throws SynthaxError {
    this.expect(TokenType.IDENTIFIER);
    if (this.accept(TokenType.COMMA))
      this.check();
  }

}
