package parser;

import parser.errors.SynthaxError;

public class NodeDeclarationConstantList extends AbstractSynthaxNode {

  public NodeDeclarationConstantList(Parser parser) {
    super(parser);
  }

  @Override
  public void check() throws SynthaxError {
    // do {
    new NodeDeclarationConstant(this.parser).check();
    // TODO
    // } while (parser.getCurrentToken().type != TokenType.VAR_KW );
  }

}
