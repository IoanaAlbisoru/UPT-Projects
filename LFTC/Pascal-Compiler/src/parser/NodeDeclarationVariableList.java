package parser;

import parser.errors.SynthaxError;

public class NodeDeclarationVariableList extends AbstractSynthaxNode {

  public NodeDeclarationVariableList(Parser parser) {
    super(parser);
  }

  @Override
  public void check() throws SynthaxError {
    new NodeDeclarationVariable(this.parser).check();
    // if (accept(TokenType.SEMICOLON)) {
    // this.check();
    // }
  }

}
