package parser;

import grammar.TokenType;
import parser.errors.SynthaxError;

class NodeElementList extends AbstractSynthaxNode {

  public NodeElementList(Parser parser) {
    super(parser);
  }

  @Override
  public void check() throws SynthaxError {
    new NodeElement(this.parser).check();
    if (this.accept(TokenType.COMMA))
      this.check();
  }

}
