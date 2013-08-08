package parser;

import grammar.TokenType;
import parser.errors.SynthaxError;

class NodeFieldList extends AbstractSynthaxNode {

  public NodeFieldList(Parser parser) {
    super(parser);
  }

  @Override
  public void check() throws SynthaxError {
    new NodeIdentifierList(this.parser).check();
    this.expect(TokenType.COLON);
    new NodeTypeSimple(this.parser).check();
    if (this.accept(TokenType.SEMICOLON))
      this.check();
  }

}
