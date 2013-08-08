package parser;

import grammar.TokenType;
import parser.errors.SynthaxError;

public class NodeDeclarationConstantSection extends AbstractSynthaxNode {

  public NodeDeclarationConstantSection(Parser parser) {
    super(parser);
  }

  @Override
  public void check() throws SynthaxError {
    // TODO
    if (this.accept(TokenType.CONST_KW))
      new NodeDeclarationConstantList(this.parser).check();
  }

}
