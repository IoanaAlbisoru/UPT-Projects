package parser;

import grammar.TokenType;
import parser.errors.SynthaxError;

public class NodeVariableList extends AbstractSynthaxNode {

  public NodeVariableList(Parser parser) {
    super(parser);
  }

  @Override
  public void check() throws SynthaxError {
    new NodeVariable(this.parser).check();
    if (this.accept(TokenType.COMMA))
      this.check();

  }

}
